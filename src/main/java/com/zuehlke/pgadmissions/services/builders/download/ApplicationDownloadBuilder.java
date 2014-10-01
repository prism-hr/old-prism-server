package com.zuehlke.pgadmissions.services.builders.download;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationAdditionalInformation;
import com.zuehlke.pgadmissions.domain.ApplicationAddress;
import com.zuehlke.pgadmissions.domain.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.ApplicationFunding;
import com.zuehlke.pgadmissions.domain.ApplicationLanguageQualification;
import com.zuehlke.pgadmissions.domain.ApplicationPassport;
import com.zuehlke.pgadmissions.domain.ApplicationPersonalDetail;
import com.zuehlke.pgadmissions.domain.ApplicationProgramDetail;
import com.zuehlke.pgadmissions.domain.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.ApplicationSupervisor;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.dto.ApplicationDownloadDTO;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.builders.download.ApplicationDownloadBuilderConfiguration.ApplicationDownloadBuilderFontSize;
import com.zuehlke.pgadmissions.utils.ConversionUtils;

@Component
@Scope("prototype")
public class ApplicationDownloadBuilder {

    private static Logger LOGGER = LoggerFactory.getLogger(ApplicationDownloadBuilder.class);

    @Value("${xml.export.provided}")
    public String provided;

    @Value("${xml.export.not.provided}")
    public String notProvided;

    @Value("${xml.export.not.submitted}")
    public String notSubmitted;

    @Value("${xml.export.date.format}")
    public String dateFormat;
    
    @Value("${xml.export.logo.file.width.percentage}")
    public Float logoFileWidthPercentage;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private ApplicationDownloadBuilderHelper applicationDownloadBuilderHelper;

    private final List<Object> bookmarks = Lists.newLinkedList();

    public void build(ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument, PdfWriter writer) throws PdfDocumentBuilderException {
        try {
            Application application = applicationDownloadDTO.getApplication();
            addCoverPage(application, pdfDocument, writer);
            writer.setPageEvent(new NewPageEvent().withApplication(application));
            addProgramSection(application, pdfDocument);
            addSupervisorSection(application, pdfDocument);
            addPersonalDetailSection(applicationDownloadDTO, pdfDocument);
            addAddressSection(application, pdfDocument);
            addQualificationSection(applicationDownloadDTO, pdfDocument);
            addEmploymentSection(application, pdfDocument);
            addFundingSection(applicationDownloadDTO, pdfDocument);
            addReferencesSection(applicationDownloadDTO, pdfDocument);
            addDocumentsSection(applicationDownloadDTO, pdfDocument);
            addAdditionalInformationSection(applicationDownloadDTO, pdfDocument);
            addSupportingDocuments(applicationDownloadDTO, pdfDocument, writer);
        } catch (Exception e) {
            LOGGER.error("Error building download for application " + applicationDownloadDTO.getApplication().getCode(), e);
            throw new PdfDocumentBuilderException(e.getMessage(), e);
        }
    }
    
    public void addContentRowSmall(String title, String content, PdfPTable table) {
        addContentRow(title, content, ApplicationDownloadBuilderFontSize.SMALL, table);
    }

    public void addContentRowMedium(String title, String content, PdfPTable table) {
        addContentRow(title, content, ApplicationDownloadBuilderFontSize.MEDIUM, table);
    }

    private void addCoverPage(Application application, Document pdfDocument, PdfWriter writer) throws MalformedURLException, IOException, DocumentException {
        pdfDocument.newPage();
        
        Image logoImage = applicationDownloadBuilderHelper.newLogoImage();
        logoImage.setAbsolutePosition(pdfDocument.right() - logoImage.getWidth() * 0.5f, pdfDocument.top() + 20f);
        pdfDocument.add(logoImage);

        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.drawLine(writer.getDirectContent(), pdfDocument.left(), pdfDocument.right(), pdfDocument.top() + 10f);

        pdfDocument.add(applicationDownloadBuilderHelper.newSectionSeparator());
        pdfDocument.add(applicationDownloadBuilderHelper.newSectionSeparator());

        PdfPTable body = startSection(pdfDocument, "Application");

        addContentRowMedium("Applicant", application.getUser().getDisplayName(), body);
        addContentRowMedium("Applicant Rating", application.getRatingAverage(), body);
        addApplicationSummaryExtended(application, body, ApplicationDownloadBuilderFontSize.MEDIUM);

        closeSection(pdfDocument, body);
        pdfDocument.newPage();
    }

    private void addProgramSection(Application application, Document pdfDocument) throws DocumentException {
        PdfPTable body = startSection(pdfDocument, "Program Detail");
        addApplicationSummary(application, body, ApplicationDownloadBuilderFontSize.MEDIUM);

        ApplicationProgramDetail programDetail = application.getProgramDetail();
        boolean programDetailNull = programDetail == null;

        String tentativeStartDate = programDetailNull ? null : programDetail.getStartDate(dateFormat);
        String confirmedStartDate = programDetailNull ? null : application.getConfirmedStartDate(dateFormat);

        addContentRowMedium(confirmedStartDate == null ? "Start Date" : "Confirmed Start Date", confirmedStartDate == null ? tentativeStartDate
                : confirmedStartDate, body);
        addContentRowMedium("How did you find us?", programDetailNull ? null : importedEntityService.getName(programDetail.getReferralSource()), body);

        closeSection(pdfDocument, body);
    }

    private void addSupervisorSection(Application application, Document pdfDocument) throws DocumentException {
        PdfPTable body = startSection(pdfDocument, "Supervisors");
        Set<ApplicationSupervisor> supervisors = application.getSupervisors();

        if (supervisors.isEmpty()) {
            addContentRow("Supervisor", null, ApplicationDownloadBuilderFontSize.MEDIUM, body);
            closeSection(pdfDocument, body);
        } else {
            closeSection(pdfDocument, body);

            int counter = 1;
            for (ApplicationSupervisor supervisor : supervisors) {
                PdfPTable subBody = startSubection(pdfDocument, "Supervisor (" + counter++ + ")");

                addContentRowMedium("Supervisor First Name", supervisor.getUser().getFirstName(), subBody);
                addContentRowMedium("Supervisor Last Name", supervisor.getUser().getLastName(), subBody);
                addContentRowMedium("Supervisor Email", supervisor.getUser().getEmail(), subBody);
                addContentRowMedium("Is this supervisor aware of your application?",
                        ConversionUtils.booleanToString(supervisor.getAcceptedSupervision(), "Yes", "No"), subBody);

                closeSection(pdfDocument, body);
            }
        }
    }

    private void addPersonalDetailSection(ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws DocumentException {
        PdfPTable body = startSection(pdfDocument, "Applicant Detail");
        Application application = applicationDownloadDTO.getApplication();

        ApplicationPersonalDetail personalDetail = application.getPersonalDetail();
        boolean personalDetailNull = personalDetail == null;

        addContentRowMedium("Title", personalDetailNull ? null : importedEntityService.getName(personalDetail.getTitle()), body);

        User applicationCreator = application.getUser();

        addContentRowMedium("First Name", applicationCreator.getFirstName(), body);
        addContentRowMedium("First Name 2", applicationCreator.getFirstName2(), body);
        addContentRowMedium("First Name 3", applicationCreator.getFirstName3(), body);
        addContentRowMedium("Last Name", applicationCreator.getLastName(), body);
        addContentRowMedium("Email", applicationCreator.getEmail(), body);

        addContentRowMedium("Telephone Number", personalDetail.getPhone(), body);
        addContentRowMedium("Skype", personalDetailNull ? null : personalDetail.getSkype(), body);

        addContentRowMedium("Gender", personalDetailNull ? null : importedEntityService.getName(personalDetail.getGender()), body);
        addContentRowMedium("Date of Birth", personalDetailNull ? null : personalDetail.getDateOfBirth(dateFormat), body);
        addContentRowMedium("Country of Birth", personalDetailNull ? null : importedEntityService.getName(personalDetail.getCountry()), body);
        addContentRowMedium("Country of Domicile", personalDetailNull ? null : importedEntityService.getName(personalDetail.getDomicile()), body);
        addContentRowMedium("Nationality", personalDetailNull ? null : personalDetail.getNationalities(), body);

        if (applicationDownloadDTO.isIncludeEqualOpportunitiesData()) {
            addContentRowMedium("Ethnicity", personalDetailNull ? null : importedEntityService.getName(personalDetail.getEthnicity()), body);
            addContentRowMedium("Disability", personalDetailNull ? null : importedEntityService.getName(personalDetail.getDisability()), body);
        }

        addContentRowMedium("Do you Require a Visa to Study in the UK?",
                ConversionUtils.booleanToString(personalDetail == null ? false : personalDetail.getVisaRequired(), "Yes", "No"), body);

        boolean passportAvailable = personalDetailNull ? false : personalDetail.getPassportAvailable();

        addContentRowMedium("Do you have a passport?", ConversionUtils.booleanToString(passportAvailable, "Yes", "No"), body);

        addContentRowMedium("Is the specified language of work your first language?",
                personalDetailNull ? null : ConversionUtils.booleanToString(personalDetail.getFirstLanguageLocale(), "Yes", "No"), body);

        boolean languageQualificationAvailable = personalDetailNull ? false : personalDetail.getLanguageQualificationAvailable();

        addContentRowMedium("Do you have a language qualification?",
                personalDetailNull ? null : ConversionUtils.booleanToString(languageQualificationAvailable, "Yes", "No"), body);

        closeSection(pdfDocument, body);

        if (passportAvailable) {
            addPassport(pdfDocument, personalDetail.getPassport());
        }

        if (languageQualificationAvailable) {
            addLanquageQualification(pdfDocument, applicationDownloadDTO, personalDetail.getLanguageQualification());
        }
    }

    private void addPassport(Document pdfDocument, ApplicationPassport passport) throws DocumentException {
        PdfPTable body = startSection(pdfDocument, "Passport");

        addContentRowMedium("Passport Number", passport.getNumber(), body);
        addContentRowMedium("Name on Passport", passport.getName(), body);
        addContentRowMedium("Passport Issue Date", passport.getIssueDate(dateFormat), body);
        addContentRowMedium("Passport Expiry Date", passport.getExipryDate(dateFormat), body);

        closeSection(pdfDocument, body);
    }

    private void addLanquageQualification(Document pdfDocument, ApplicationDownloadDTO applicationDownloadDTO,
            ApplicationLanguageQualification languageQualification) throws DocumentException {
        PdfPTable body = startSection(pdfDocument, "Language Qualification");

        addContentRowMedium("Qualification Type", importedEntityService.getName(languageQualification.getType()), body);
        addContentRowMedium("Date of Examination", languageQualification.getExamDate(dateFormat), body);
        addContentRowMedium("Overall Score", languageQualification.getOverallScore(), body);
        addContentRowMedium("Reading Score", languageQualification.getReadingScore(), body);
        addContentRowMedium("Essay/Writing Score", languageQualification.getWritingScore(), body);
        addContentRowMedium("Speaking Score", languageQualification.getSpeakingScore(), body);
        addContentRowMedium("Listening Score", languageQualification.getListeningScore(), body);
        addDocument(body, "Proof of Award", languageQualification.getDocument(), applicationDownloadDTO.isIncludeAttachments());

        closeSection(pdfDocument, body);
    }

    private void addAddressSection(Application application, Document pdfDocument) throws DocumentException {
        PdfPTable body = startSection(pdfDocument, "Address Detail");  
        ApplicationAddress address = application.getAddress();
        
        addContentRowMedium("Current Address", address == null ? null : address.getCurrentAddressLocation(), body);
        addContentRowMedium("Contact Address", address == null ? null : address.getConcatAddressLocation(), body);
        
        closeSection(pdfDocument, body);
    }

    private void addQualificationSection(ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws DocumentException {
        PdfPTable body = startSection(pdfDocument, "Qualifications");
        Set<ApplicationQualification> qualifications = applicationDownloadDTO.getApplication().getQualifications();
        
        if (qualifications.isEmpty()) {
            addContentRowMedium("Qualification", null, body);
            closeSection(pdfDocument, body);
        } else {
            closeSection(pdfDocument, body);
            
            int counter = 1;
            for (ApplicationQualification qualification : qualifications) {
                PdfPTable subBody = startSubection(pdfDocument, "Qualification (" + counter++ + ")");
                
                ImportedInstitution institution = qualification.getInstitution();
                
                addContentRowMedium("Institution Country", institution == null ? null : importedEntityService.getName(institution.getDomicile()), subBody);
                addContentRowMedium("Institution/Provider Name", importedEntityService.getName(institution), subBody);
                addContentRowMedium("Qualification Type", importedEntityService.getName(qualification.getType()), subBody);
                addContentRowMedium("Qualification Title", qualification.getTitle(), subBody);
                addContentRowMedium("Qualification Subject", qualification.getSubject(), subBody);
                addContentRowMedium("Language of Study", qualification.getLanguage(), subBody);
                addContentRowMedium("Start Date", qualification.getStartDate(dateFormat), subBody);

                boolean completed = BooleanUtils.isTrue(qualification.getCompleted());
                
                addContentRowMedium("Has this Qualification been awarded", ConversionUtils.booleanToString(completed, "Yes", "No"), subBody);
                addContentRowMedium(completed ? "Confirmed Grade/Result/GPA" : "Expected Grade/Result/GPA", qualification.getAwardDate(dateFormat), subBody);
                addContentRowMedium(completed ? "Confirmed Award Date" : "Expected Award Date", qualification.getAwardDate(dateFormat), subBody);
                addDocument(subBody, completed ? "Final Transcript" : "Interim Transcript", qualification.getDocument(), applicationDownloadDTO.isIncludeAttachments());

                closeSection(pdfDocument, subBody);
            }
        }
    }

    private void addEmploymentSection(Application application, Document pdfDocument) throws DocumentException {
        PdfPTable body = startSection(pdfDocument, "Employment Positions");
        Set<ApplicationEmploymentPosition> positions = application.getEmploymentPositions();

        if (positions.isEmpty()) {
            addContentRowMedium("Position", null, body);
            closeSection(pdfDocument, body);
        } else {
            closeSection(pdfDocument, body);
            
            int counter = 1;
            for (ApplicationEmploymentPosition position : positions) {
                PdfPTable subBody = startSubection(pdfDocument, "Position (" + counter++ + ")");
                
                addContentRowMedium("Employer Name", position.getEmployerName(), subBody);
                addContentRowMedium("Employer Address", position.getEmployerAddressLocation(), subBody);
                addContentRowMedium("Position Title", position.getPosition(), subBody);
                addContentRowMedium("Position Remit", position.getRemit(), subBody);
                addContentRowMedium("Start Date", position.getStartDate(dateFormat), subBody);
                addContentRowMedium("Is this your Current Position", ConversionUtils.booleanToString(position.getCurrent(), "Yes", "No"), subBody);
                addContentRowMedium("End Date", position.getEndDate(dateFormat), subBody);

                closeSection(pdfDocument, subBody);
            }
        }
    }

    private void addFundingSection(ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws DocumentException {
        PdfPTable body = startSection(pdfDocument, "Funding Awards");
        Set<ApplicationFunding> fundings = applicationDownloadDTO.getApplication().getFundings();

        if (fundings.isEmpty()) {
            addContentRowMedium("Award", null, body);
            closeSection(pdfDocument, body);
        } else {
            closeSection(pdfDocument, body);
            
            int counter = 1;
            for (ApplicationFunding funding : fundings) {
                PdfPTable subBody = startSubection(pdfDocument, "Award (" + counter++ + ")");
                
                addContentRowMedium("Award Type", importedEntityService.getName(funding.getFundingSource()), subBody);
                addContentRowMedium("Award Description", funding.getDescription(), subBody);
                addContentRowMedium("Award Value", funding.getValue(), subBody);
                addContentRowMedium("Award Date", funding.getAwardDate(dateFormat), subBody);
                addDocument(subBody, "Proof of Award", funding.getDocument(), applicationDownloadDTO.isIncludeAttachments());

                closeSection(pdfDocument, subBody);
            }
        }
    }

    private void addReferencesSection(ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws DocumentException {
        PdfPTable body = startSection(pdfDocument, "Referees");
        Set<ApplicationReferee> referees = applicationDownloadDTO.getApplication().getReferees();

        if (referees.isEmpty()) {
            addContentRowMedium("Referee", null, body);
            closeSection(pdfDocument, body);
        } else {
            closeSection(pdfDocument, body);
            
            int counter = 1;
            for (ApplicationReferee referee : referees) {
                PdfPTable subBody = startSubection(pdfDocument, "Referee (" + counter++ + ")");
                User user = referee.getUser();
                
                addContentRowMedium("First Name", user == null ? null : user.getFirstName(), subBody);
                addContentRowMedium("Last Name", user == null ? null : user.getLastName(), subBody);
                addContentRowMedium("Address", referee.getAddressLocation(), subBody);
                addContentRowMedium("Email", user == null ? null :user.getEmail(), subBody);
                addContentRowMedium("Telephone Number", referee.getPhoneNumber(), subBody);
                addContentRowMedium("Skype", referee.getSkype(), subBody);
                addContentRowMedium("Employer", referee.getJobEmployer(), subBody);
                addContentRowMedium("Position Title", referee.getJobTitle(), subBody);

                if (applicationDownloadDTO.isIncludeReferences()) {
                    subBody.addCell(applicationDownloadBuilderHelper.newContentCellMedium("Reference"));
                    Comment referenceComment = referee.getComment();
                    if (referenceComment == null) {
                        subBody.addCell(applicationDownloadBuilderHelper.newContentCellMedium(null));
                    } else {
                        addBookmark(subBody, referenceComment);
                    }
                }
                
                closeSection(pdfDocument, subBody);
            }
        }
    }

    private void addDocumentsSection(ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws DocumentException {
        PdfPTable body = startSection(pdfDocument, "Documents");
        ApplicationDocument documents = applicationDownloadDTO.getApplication().getDocument();

        boolean includeAttachments = applicationDownloadDTO.isIncludeAttachments();
        addDocument(body, "Personal Statement", documents.getPersonalStatement(), includeAttachments);
        addDocument(body, "CV/Resume", documents.getCv(), includeAttachments);

        closeSection(pdfDocument, body);
    }

    private void addAdditionalInformationSection(ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws DocumentException {
        if (applicationDownloadDTO.isIncludeEqualOpportunitiesData()) {
            PdfPTable body = startSection(pdfDocument, "Additional Information");
            ApplicationAdditionalInformation additionalInformation = applicationDownloadDTO.getApplication().getAdditionalInformation();
            
            addContentRowMedium("Unspent Criminal Convictions", additionalInformation == null ? null : additionalInformation.getConvictionsText(), body);
            
            closeSection(pdfDocument, body);
        }
    }

    private void addSupportingDocuments(ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument, PdfWriter pdfWriter) throws DocumentException {
        if (applicationDownloadDTO.isIncludeAttachments()) {
            for (int i = 0; i < bookmarks.size(); i++) {
                pdfDocument.newPage();

                NewPageEvent pageEvent = (NewPageEvent) pdfWriter.getPageEvent();
                pageEvent.setApplyHeaderFooter(true);
                
                Object object = bookmarks.get(i);
                pdfDocument.add(new Chunk("APPENDIX (" + (i + 1) + ")").setLocalDestination(new Integer(i).toString()));
                if (object.getClass().equals(com.zuehlke.pgadmissions.domain.Document.class)) {
                    com.zuehlke.pgadmissions.domain.Document document = (com.zuehlke.pgadmissions.domain.Document) object;
                    
                    if (document != null) {
                        if (document.getApplicationLanguageQualification() != null) {
                            pdfDocument.add(new Chunk(" - Language Qualification Transcript"));
                        } else if (document.getApplicationQualification() != null) {
                            pdfDocument.add(new Chunk(" - Qualification Transcript"));
                        } else if (document.getApplicationFunding() != null) {
                            pdfDocument.add(new Chunk(" - Proof of Funding"));
                        } else if (document.getApplicationPersonalStatement() != null) {
                            pdfDocument.add(new Chunk(" - Personal Statement"));
                        } else if (document.getApplicationCv() != null) {
                            pdfDocument.add(new Chunk(" - CV/Resume"));
                        } 

                        try {
                            readPdf(pdfDocument, document, pdfWriter);
                        } catch (Exception e) {
                            LOGGER.warn("Error reading PDF document", e.getMessage());
                        }
                    }
                } else if (object.getClass().equals(Comment.class)) {
                    Comment reference = (Comment) object;
                    pdfDocument.add(new Chunk(" - Reference"));
                    
                    pdfDocument.add(applicationDownloadBuilderHelper.newSectionSeparator());
                    PdfPTable subBody = startSubection(pdfDocument, "Reference Comment");
                    
                    addContentRowMedium("Referee", reference.getUser().getDisplayName(), subBody);
                    addContentRowMedium("Comment", reference.getContent(), subBody);
                    addContentRowMedium("Rating", reference.getRating(), subBody);
                    addContentRowMedium("Suitable for Recruiting Institution?", ConversionUtils.booleanToString(reference.getSuitableForInstitution(), "Yes", "No"), subBody);
                    addContentRowMedium("Suitable for Recruiting Position?", ConversionUtils.booleanToString(reference.getSuitableForOpportunity(), "Yes", "No"), subBody);

                    closeSection(pdfDocument, subBody);
                    for (com.zuehlke.pgadmissions.domain.Document document : reference.getDocuments()) {
                        try {
                            readPdf(pdfDocument, document, pdfWriter);
                        } catch (Exception e) {
                            LOGGER.warn("Error reading PDF document", e);
                        }
                    }
                }
            }
        }
    }

    private void readPdf(Document pdfDocument, com.zuehlke.pgadmissions.domain.Document document, PdfWriter pdfWriter) throws IOException {
        PdfReader pdfReader = new PdfReader(document.getContent());
        PdfContentByte cb = pdfWriter.getDirectContent();
        for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
            PdfImportedPage page = pdfWriter.getImportedPage(pdfReader, i);
            pdfDocument.setPageSize(new Rectangle(page.getWidth(), page.getHeight()));
            pdfDocument.newPage();
            NewPageEvent pageEvent = (NewPageEvent) pdfWriter.getPageEvent();
            pageEvent.setApplyHeaderFooter(false);
            cb.addTemplate(page, 0, 0);
            pdfDocument.setPageSize(PageSize.A4);
        }
    }
    
    private PdfPTable startSection(Document pdfDocument, String title) throws DocumentException {
        pdfDocument.add(applicationDownloadBuilderHelper.newSectionHeader(title));
        pdfDocument.add(applicationDownloadBuilderHelper.newSectionSeparator());
        return applicationDownloadBuilderHelper.newSectionBody();
    }

    private PdfPTable startSubection(Document pdfDocument, String title) throws DocumentException {
        PdfPTable subBody = applicationDownloadBuilderHelper.newSectionBody();
        PdfPCell header = new PdfPCell(applicationDownloadBuilderHelper.newSubsectionHeader(title));
        header.setColspan(2);
        subBody.addCell(header);
        return subBody;
    }

    private void addApplicationSummary(Application application, PdfPTable table, ApplicationDownloadBuilderFontSize fontSize) {
        addContentRow("Program", application.getProgramTitle(), fontSize, table);
        addContentRow("Project", application.getProjectTitle(), fontSize, table);

        ApplicationProgramDetail programDetail = application.getProgramDetail();
        addContentRow("Study Option", programDetail == null ? null : importedEntityService.getName(programDetail.getStudyOption()), fontSize, table);
    }

    private void addApplicationSummaryExtended(Application application, PdfPTable table, ApplicationDownloadBuilderFontSize fontSize) {
        addApplicationSummary(application, table, fontSize);

        addContentRow("Application Code", application.getCode(), fontSize, table);
        addContentRow("Application Closing Date", application.getClosingDate(dateFormat), fontSize, table);
        addContentRow("Application Submission Date", application.getSubmittedTimestamp(dateFormat), fontSize, table);
    }

    private void addContentRow(String title, String content, ApplicationDownloadBuilderFontSize fontSize, PdfPTable table) {
        String fontSizePostfix = WordUtils.capitalizeFully(fontSize.name());
        try {
            table.addCell((PdfPCell) MethodUtils.invokeExactMethod(applicationDownloadBuilderHelper, "newTitleCell" + fontSizePostfix, title));
            table.addCell((PdfPCell) MethodUtils.invokeExactMethod(applicationDownloadBuilderHelper, "newContentCell" + fontSizePostfix, content));
        } catch (Exception e) {
            LOGGER.error("No such helper method", e);
        }
    }

    private void addDocument(PdfPTable table, String rowTitle, com.zuehlke.pgadmissions.domain.Document document, boolean includeAttachments) {
        applicationDownloadBuilderHelper.newTitleCellLarge(rowTitle);
        if (includeAttachments) {
            if (document == null) {
                table.addCell(applicationDownloadBuilderHelper.newContentCellMedium(notProvided));
            } else {
                addBookmark(table, document);
            }
        } else {
            table.addCell(applicationDownloadBuilderHelper.newContentCellMedium(document == null ? null : provided));
        }
    }

    private void addBookmark(PdfPTable table, Object object) {
        int index = bookmarks.size();
        table.addCell(applicationDownloadBuilderHelper.newBookmarkCellMedium("See APPENDIX(" + (index + 1) + ")", index));
        bookmarks.add(object);
    }

    private void closeSection(Document pdfDocument, PdfPTable body) throws DocumentException {
        pdfDocument.add(body);
        pdfDocument.add(applicationDownloadBuilderHelper.newSectionSeparator());
    }

    @Component
    @Scope("prototype")
    public class ApplicationDownloadAlternativeQualificationBuilder {

        @Value("${xml.export.not.provided}")
        String notProvided;

        @Value("${xml.export.system.qualification}")
        String alternativeQualification;

        @Autowired
        private CommentService commentService;

        @Autowired
        private ApplicationDownloadBuilderHelper applicationDownloadBuilderHelper;

        public byte[] build(final Application application) {
            try {
                Document exportDocument = new Document(PageSize.A4, 50, 50, 100, 50);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                PdfWriter.getInstance(exportDocument, outputStream);
                exportDocument.open();

                PdfPTable table = new PdfPTable(1);
                table.setWidthPercentage(ApplicationDownloadBuilderConfiguration.PAGE_WIDTH);
                table.addCell(applicationDownloadBuilderHelper.newSectionHeader("No Transcripts Provided"));
                exportDocument.add(table);
                exportDocument.add(applicationDownloadBuilderHelper.newSectionSeparator());

                Comment approvalComment = commentService.getLatestComment(application, PrismAction.APPLICATION_ASSIGN_SUPERVISORS);
                exportDocument.add(applicationDownloadBuilderHelper.newContentCellMedium("Comment: "
                        + (approvalComment == null ? notProvided : alternativeQualification)));

                exportDocument.close();
                return outputStream.toByteArray();
            } catch (Exception e) {
                throw new PdfDocumentBuilderException(e);
            }
        }

    }

    @Component
    @Scope("prototype")
    public class ApplicationDownloadReferenceBuilder {

        @Value("${xml.export.system.reference}")
        private String noReferenceExplanation;

        @Autowired
        ApplicationDownloadBuilderHelper applicationModelBuilderHelper;

        public void build(final Application application, final Comment referenceComment, final OutputStream outputStream) {
            try {
                Document document = new Document(PageSize.A4, 50, 50, 100, 50);
                PdfWriter writer = PdfWriter.getInstance(document, outputStream);
                writer.setCloseStream(false);
                document.open();

                PdfPTable table = new PdfPTable(1);
                table.setWidthPercentage(ApplicationDownloadBuilderConfiguration.PAGE_WIDTH);
                table.addCell(applicationModelBuilderHelper.newSectionHeader("Referee Comment"));
                document.add(table);
                document.add(applicationModelBuilderHelper.newSectionSeparator());

                if (referenceComment == null) {
                    document.add(applicationModelBuilderHelper.newContentCellMedium("Comment: "
                            + (application.getState().getId().getStateGroup() == PrismStateGroup.APPLICATION_APPROVED ? noReferenceExplanation
                                    : "Reference not yet provided at time of outcome.")));
                } else {
                    document.add(applicationModelBuilderHelper.newContentCellMedium("Comment: "
                            + (BooleanUtils.isTrue(referenceComment.isDeclinedResponse()) ? "Declined to provide a reference." : referenceComment.getContent())));

                    PdfContentByte cb = writer.getDirectContent();
                    for (com.zuehlke.pgadmissions.domain.Document input : referenceComment.getDocuments()) {
                        try {
                            PdfReader reader = new PdfReader(input.getContent());
                            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                                document.newPage();
                                PdfImportedPage page = writer.getImportedPage(reader, i);
                                cb.addTemplate(page, 0, 0);
                            }
                        } catch (IllegalArgumentException e) {
                            throw new Error(e);
                        }
                    }
                }

                document.newPage();
                document.close();
            } catch (Exception e) {
                throw new PdfDocumentBuilderException(e);
            }
        }

    }

    private class NewPageEvent extends PdfPageEventHelper {

        private Application application;

        private boolean applyHeaderFooter = true;

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            if (applyHeaderFooter) {
                try {
                    addHeader(writer, document);
                    addFooter(writer, document);
                } catch (Exception e) {
                    LOGGER.error("Error applying header/footer to download", e);
                    throw new RuntimeException(e);
                }
            }
        }

        private void addHeader(PdfWriter writer, Document pdfDocument) throws DocumentException, BadElementException, IOException {
            float textCellWidth = 65f;
            
            PdfPTable body = new PdfPTable(2);
            body.setTotalWidth(pdfDocument.getPageSize().getWidth());
            body.setWidths(new float[] {textCellWidth, logoFileWidthPercentage});

            PdfPTable subBody = new PdfPTable(2);
            body.setTotalWidth(textCellWidth * ApplicationDownloadBuilderConfiguration.PAGE_WIDTH);
            subBody.setWidths(new float[] {25f, 75f});
            addApplicationSummaryExtended(application, subBody, ApplicationDownloadBuilderFontSize.SMALL);
            
            body.addCell(subBody);
            
            PdfPCell logoCell = new PdfPCell(applicationDownloadBuilderHelper.newLogoImage());
            logoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            logoCell.setVerticalAlignment(Element.ALIGN_TOP);
            body.addCell(logoCell);
            
            body.writeSelectedRows(0, -1, pdfDocument.left(), pdfDocument.top() + 55f, writer.getDirectContent());
            
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.drawLine(writer.getDirectContent(), pdfDocument.left(), pdfDocument.right(), pdfDocument.top() + 10f);
        }
        
        private void addFooter(PdfWriter writer, Document document) {
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.drawLine(writer.getDirectContent(), document.left(), document.right(), document.bottom() - 15f);
            Phrase footerPhrase = new Phrase("Page " + (1 + document.getPageNumber()),
                    ApplicationDownloadBuilderConfiguration.getFont(ApplicationDownloadBuilderFontSize.SMALL));
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, footerPhrase, document.left(), document.bottom() - 25f, 0);
        }
        
        public void setApplyHeaderFooter(boolean applyHeader) {
            this.applyHeaderFooter = applyHeader;
        }

        public NewPageEvent withApplication(Application application) {
            this.application = application;
            return this;
        }

    }

}
