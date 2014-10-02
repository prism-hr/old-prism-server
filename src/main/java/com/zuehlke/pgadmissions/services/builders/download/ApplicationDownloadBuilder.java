package com.zuehlke.pgadmissions.services.builders.download;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
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
import com.zuehlke.pgadmissions.dto.ApplicationDownloadDTO;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;
import com.zuehlke.pgadmissions.services.builders.download.ApplicationDownloadBuilderConfiguration.ApplicationDownloadBuilderFontSize;
import com.zuehlke.pgadmissions.utils.ConversionUtils;

@Component
@Scope("prototype")
public class ApplicationDownloadBuilder {

    private static Logger LOGGER = LoggerFactory.getLogger(ApplicationDownloadBuilder.class);

    private final List<Object> bookmarks = Lists.newLinkedList();

    @Value("${xml.export.logo.file.width.percentage}")
    private Float logoFileWidthPercentage;

    @Value("${xml.export.provided}")
    private String provided;

    @Value("${xml.export.date.format}")
    private String dateFormat;

    @Autowired
    private ApplicationDownloadBuilderHelper applicationDownloadBuilderHelper;

    @Autowired
    private ApplicationContext applicationContext;

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
            addDocumentSection(applicationDownloadDTO, pdfDocument);
            addAdditionalInformationSection(applicationDownloadDTO, pdfDocument);
            addSupportingDocuments(applicationDownloadDTO, pdfDocument, writer);
        } catch (Exception e) {
            LOGGER.error("Error building download for application " + applicationDownloadDTO.getApplication().getCode(), e);
            throw new PdfDocumentBuilderException(e.getMessage(), e);
        }
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

        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, "Application");

        applicationDownloadBuilderHelper.addContentRowMedium("Applicant", application.getUser().getDisplayName(), body);
        applicationDownloadBuilderHelper.addContentRowMedium("Applicant Rating", application.getApplicationRatingAverageDisplay(), body);
        addApplicationSummaryExtended(application, body, ApplicationDownloadBuilderFontSize.MEDIUM);

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        pdfDocument.newPage();
    }

    private void addProgramSection(Application application, Document pdfDocument) throws DocumentException {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, "Program Detail");
        addApplicationSummary(application, body, ApplicationDownloadBuilderFontSize.MEDIUM);

        ApplicationProgramDetail programDetail = application.getProgramDetail();
        boolean programDetailNull = programDetail == null;

        String tentativeStartDate = programDetailNull ? null : programDetail.getStartDateDisplay(dateFormat);
        String confirmedStartDate = programDetailNull ? null : application.getConfirmedStartDateDisplay(dateFormat);

        applicationDownloadBuilderHelper.addContentRowMedium(confirmedStartDate == null ? "Start Date" : "Confirmed Start Date",
                confirmedStartDate == null ? tentativeStartDate : confirmedStartDate, body);
        applicationDownloadBuilderHelper.addContentRowMedium("How did you find us?", programDetailNull ? null : programDetail.getReferralSourceDisplay(), body);

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
    }

    private void addSupervisorSection(Application application, Document pdfDocument) throws DocumentException {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, "Supervisors");
        Set<ApplicationSupervisor> supervisors = application.getSupervisors();

        if (supervisors.isEmpty()) {
            applicationDownloadBuilderHelper.addContentRowMedium("Supervisor", null, body);
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        } else {
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);

            int counter = 1;
            for (ApplicationSupervisor supervisor : supervisors) {
                PdfPTable subBody = applicationDownloadBuilderHelper.startSubection(pdfDocument, "Supervisor (" + counter++ + ")");

                applicationDownloadBuilderHelper.addContentRowMedium("Supervisor First Name", supervisor.getUser().getFirstName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium("Supervisor Last Name", supervisor.getUser().getLastName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium("Supervisor Email", supervisor.getUser().getEmail(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium("Is this supervisor aware of your application?",
                        ConversionUtils.booleanToString(supervisor.getAcceptedSupervision(), "Yes", "No"), subBody);

                applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
            }
        }
    }

    private void addPersonalDetailSection(ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws DocumentException {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, "Applicant Detail");
        Application application = applicationDownloadDTO.getApplication();

        ApplicationPersonalDetail personalDetail = application.getPersonalDetail();
        boolean personalDetailNull = personalDetail == null;

        applicationDownloadBuilderHelper.addContentRowMedium("Title", personalDetailNull ? null : personalDetail.getTitleDisplay(), body);

        User applicationCreator = application.getUser();

        applicationDownloadBuilderHelper.addContentRowMedium("First Name", applicationCreator.getFirstName(), body);
        applicationDownloadBuilderHelper.addContentRowMedium("First Name 2", applicationCreator.getFirstName2(), body);
        applicationDownloadBuilderHelper.addContentRowMedium("First Name 3", applicationCreator.getFirstName3(), body);
        applicationDownloadBuilderHelper.addContentRowMedium("Last Name", applicationCreator.getLastName(), body);
        applicationDownloadBuilderHelper.addContentRowMedium("Email", applicationCreator.getEmail(), body);

        applicationDownloadBuilderHelper.addContentRowMedium("Telephone Number", personalDetail == null ? null : personalDetail.getPhone(), body);
        applicationDownloadBuilderHelper.addContentRowMedium("Skype", personalDetailNull ? null : personalDetail.getSkype(), body);

        applicationDownloadBuilderHelper.addContentRowMedium("Gender", personalDetailNull ? null : personalDetail.getGenderDisplay(), body);
        applicationDownloadBuilderHelper.addContentRowMedium("Date of Birth", personalDetailNull ? null : personalDetail.getDateOfBirth(dateFormat), body);
        applicationDownloadBuilderHelper.addContentRowMedium("Country of Birth", personalDetailNull ? null : personalDetail.getCountryDisplay(), body);
        applicationDownloadBuilderHelper.addContentRowMedium("Country of Domicile", personalDetailNull ? null : personalDetail.getDomicileDisplay(), body);
        applicationDownloadBuilderHelper.addContentRowMedium("Nationality", personalDetailNull ? null : personalDetail.getNationalityDisplay(), body);

        if (applicationDownloadDTO.isIncludeEqualOpportunitiesData()) {
            applicationDownloadBuilderHelper.addContentRowMedium("Ethnicity", personalDetailNull ? null : personalDetail.getEthnicityDisplay(), body);
            applicationDownloadBuilderHelper.addContentRowMedium("Disability", personalDetailNull ? null : personalDetail.getDisabilityDisplay(), body);
        }

        applicationDownloadBuilderHelper.addContentRowMedium("Do you Require a Visa to Study in the UK?",
                ConversionUtils.booleanToString(personalDetail == null ? false : personalDetail.getVisaRequired(), "Yes", "No"), body);

        boolean passportAvailable = personalDetailNull ? false : personalDetail.getPassportAvailable();

        applicationDownloadBuilderHelper.addContentRowMedium("Do you have a passport?", ConversionUtils.booleanToString(passportAvailable, "Yes", "No"), body);
        applicationDownloadBuilderHelper.addContentRowMedium("Is the specified language of work your first language?", personalDetailNull ? null
                : ConversionUtils.booleanToString(personalDetail.getFirstLanguageLocale(), "Yes", "No"), body);

        boolean languageQualificationAvailable = personalDetailNull ? false : personalDetail.getLanguageQualificationAvailable();

        applicationDownloadBuilderHelper.addContentRowMedium("Do you have a language qualification?",
                personalDetailNull ? null : ConversionUtils.booleanToString(languageQualificationAvailable, "Yes", "No"), body);

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);

        if (passportAvailable) {
            addPassport(pdfDocument, personalDetail.getPassport());
        }

        if (languageQualificationAvailable) {
            addLanquageQualification(pdfDocument, applicationDownloadDTO, personalDetail.getLanguageQualification());
        }
    }

    private void addPassport(Document pdfDocument, ApplicationPassport passport) throws DocumentException {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, "Passport");

        applicationDownloadBuilderHelper.addContentRowMedium("Passport Number", passport.getNumber(), body);
        applicationDownloadBuilderHelper.addContentRowMedium("Name on Passport", passport.getName(), body);
        applicationDownloadBuilderHelper.addContentRowMedium("Passport Issue Date", passport.getIssueDateDisplay(dateFormat), body);
        applicationDownloadBuilderHelper.addContentRowMedium("Passport Expiry Date", passport.getExipryDateDisplay(dateFormat), body);

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
    }

    private void addLanquageQualification(Document pdfDocument, ApplicationDownloadDTO applicationDownloadDTO,
            ApplicationLanguageQualification languageQualification) throws DocumentException {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, "Language Qualification");

        applicationDownloadBuilderHelper.addContentRowMedium("Qualification Type", languageQualification.getTypeDisplay(), body);
        applicationDownloadBuilderHelper.addContentRowMedium("Date of Examination", languageQualification.getExamDateDisplay(dateFormat), body);
        applicationDownloadBuilderHelper.addContentRowMedium("Overall Score", languageQualification.getOverallScore(), body);
        applicationDownloadBuilderHelper.addContentRowMedium("Reading Score", languageQualification.getReadingScore(), body);
        applicationDownloadBuilderHelper.addContentRowMedium("Essay/Writing Score", languageQualification.getWritingScore(), body);
        applicationDownloadBuilderHelper.addContentRowMedium("Speaking Score", languageQualification.getSpeakingScore(), body);
        applicationDownloadBuilderHelper.addContentRowMedium("Listening Score", languageQualification.getListeningScore(), body);
        addDocument(body, "Proof of Award", languageQualification.getDocument(), applicationDownloadDTO.isIncludeAttachments());

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
    }

    private void addAddressSection(Application application, Document pdfDocument) throws DocumentException {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, "Address Detail");
        ApplicationAddress address = application.getAddress();

        applicationDownloadBuilderHelper.addContentRowMedium("Current Address", address == null ? null : address.getCurrentAddressDisplay(), body);
        applicationDownloadBuilderHelper.addContentRowMedium("Contact Address", address == null ? null : address.getConcactAddressDisplay(), body);

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
    }

    private void addQualificationSection(ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws DocumentException {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, "Qualifications");
        Set<ApplicationQualification> qualifications = applicationDownloadDTO.getApplication().getQualifications();

        if (qualifications.isEmpty()) {
            applicationDownloadBuilderHelper.addContentRowMedium("Qualification", null, body);
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        } else {
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);

            int counter = 1;
            for (ApplicationQualification qualification : qualifications) {
                PdfPTable subBody = applicationDownloadBuilderHelper.startSubection(pdfDocument, "Qualification (" + counter++ + ")");

                ImportedInstitution institution = qualification.getInstitution();
                boolean institutionNull = institution == null;

                applicationDownloadBuilderHelper.addContentRowMedium("Study Country", institutionNull ? null : institution.getDomicileDisplay(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium("Study Provider", institutionNull ? null : institution.getName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium("Qualification Type", qualification.getTypeDisplay(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium("Qualification Title", qualification.getTitle(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium("Qualification Subject", qualification.getSubject(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium("Language of Study", qualification.getLanguage(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium("Start Date", qualification.getStartDateDisplay(dateFormat), subBody);

                boolean completed = BooleanUtils.isTrue(qualification.getCompleted());

                applicationDownloadBuilderHelper.addContentRowMedium("Has this Qualification been awarded",
                        ConversionUtils.booleanToString(completed, "Yes", "No"), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(completed ? "Confirmed Grade/Result/GPA" : "Expected Grade/Result/GPA",
                        qualification.getGrade(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(completed ? "Confirmed Award Date" : "Expected Award Date",
                        qualification.getAwardDateDisplay(dateFormat), subBody);
                addDocument(subBody, completed ? "Final Transcript" : "Interim Transcript", qualification.getDocument(),
                        applicationDownloadDTO.isIncludeAttachments());

                applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
            }
        }
    }

    private void addEmploymentSection(Application application, Document pdfDocument) throws DocumentException {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, "Employment Positions");
        Set<ApplicationEmploymentPosition> positions = application.getEmploymentPositions();

        if (positions.isEmpty()) {
            applicationDownloadBuilderHelper.addContentRowMedium("Position", null, body);
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        } else {
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);

            int counter = 1;
            for (ApplicationEmploymentPosition position : positions) {
                PdfPTable subBody = applicationDownloadBuilderHelper.startSubection(pdfDocument, "Position (" + counter++ + ")");

                applicationDownloadBuilderHelper.addContentRowMedium("Employer Name", position.getEmployerName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium("Employer Address", position.getEmployerAddressLocation(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium("Position Title", position.getPosition(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium("Position Remit", position.getRemit(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium("Start Date", position.getStartDateDisplay(dateFormat), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium("Is this your Current Position",
                        ConversionUtils.booleanToString(position.getCurrent(), "Yes", "No"), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium("End Date", position.getEndDateDisplay(dateFormat), subBody);

                applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
            }
        }
    }

    private void addFundingSection(ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws DocumentException {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, "Funding Awards");
        Set<ApplicationFunding> fundings = applicationDownloadDTO.getApplication().getFundings();

        if (fundings.isEmpty()) {
            applicationDownloadBuilderHelper.addContentRowMedium("Award", null, body);
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        } else {
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);

            int counter = 1;
            for (ApplicationFunding funding : fundings) {
                PdfPTable subBody = applicationDownloadBuilderHelper.startSubection(pdfDocument, "Award (" + counter++ + ")");

                applicationDownloadBuilderHelper.addContentRowMedium("Award Type", funding.getFundingSourceDisplay(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium("Award Description", funding.getDescription(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium("Award Value", funding.getValue(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium("Award Date", funding.getAwardDateDisplay(dateFormat), subBody);
                addDocument(subBody, "Proof of Award", funding.getDocument(), applicationDownloadDTO.isIncludeAttachments());

                applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
            }
        }
    }

    private void addReferencesSection(ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws DocumentException {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, "Referees");
        Set<ApplicationReferee> referees = applicationDownloadDTO.getApplication().getReferees();

        if (referees.isEmpty()) {
            applicationDownloadBuilderHelper.addContentRowMedium("Referee", null, body);
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        } else {
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);

            int counter = 1;
            for (ApplicationReferee referee : referees) {
                PdfPTable subBody = applicationDownloadBuilderHelper.startSubection(pdfDocument, "Referee (" + counter++ + ")");

                User user = referee.getUser();
                boolean userNull = user == null;

                applicationDownloadBuilderHelper.addContentRowMedium("First Name", userNull ? null : user.getFirstName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium("Last Name", userNull ? null : user.getLastName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium("Email", userNull ? null : user.getEmail(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium("Address", referee.getAddressDisplay(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium("Telephone Number", referee.getPhoneNumber(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium("Skype", referee.getSkype(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium("Employer", referee.getJobEmployer(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium("Position Title", referee.getJobTitle(), subBody);

                if (applicationDownloadDTO.isIncludeReferences()) {
                    subBody.addCell(applicationDownloadBuilderHelper.newContentCellMedium("Reference"));
                    Comment referenceComment = referee.getComment();
                    if (referenceComment == null) {
                        subBody.addCell(applicationDownloadBuilderHelper.newContentCellMedium(null));
                    } else {
                        addBookmark(subBody, referenceComment);
                    }
                }

                applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
            }
        }
    }

    private void addDocumentSection(ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws DocumentException {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, "Documents");
        ApplicationDocument documentSection = applicationDownloadDTO.getApplication().getDocument();

        boolean includeAttachments = applicationDownloadDTO.isIncludeAttachments();
        addDocument(body, "Personal Statement", documentSection.getPersonalStatement(), includeAttachments);
        addDocument(body, "CV/Resume", documentSection.getCv(), includeAttachments);

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
    }

    private void addAdditionalInformationSection(ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws DocumentException {
        if (applicationDownloadDTO.isIncludeEqualOpportunitiesData()) {
            PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, "Additional Information");
            ApplicationAdditionalInformation additionalInformation = applicationDownloadDTO.getApplication().getAdditionalInformation();

            applicationDownloadBuilderHelper.addContentRowMedium("Unspent Criminal Convictions",
                    additionalInformation == null ? null : additionalInformation.getConvictionsText(), body);

            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
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
                            addDocument(pdfDocument, document, pdfWriter);
                        } catch (Exception e) {
                            LOGGER.warn("Error reading PDF document", e.getMessage());
                        }
                    }
                } else if (object.getClass().equals(Comment.class)) {
                    Comment referenceComment = (Comment) object;
                    pdfDocument.add(new Chunk(" - Reference"));

                    pdfDocument.add(applicationDownloadBuilderHelper.newSectionSeparator());

                    PdfPTable subBody = applicationDownloadBuilderHelper.startSubection(pdfDocument, "Reference Comment");
                    applicationContext.getBean(ApplicationDownloadReferenceBuilder.class).addReferenceComment(pdfDocument, subBody, pdfWriter,
                            applicationDownloadDTO.getApplication(), referenceComment);
                }
            }
        }
    }

    private void addDocument(Document pdfDocument, com.zuehlke.pgadmissions.domain.Document document, PdfWriter pdfWriter) throws IOException {
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

    private void addApplicationSummary(Application application, PdfPTable table, ApplicationDownloadBuilderFontSize fontSize) {
        applicationDownloadBuilderHelper.addContentRow("Program", application.getProgramDisplay(), fontSize, table);
        applicationDownloadBuilderHelper.addContentRow("Project", application.getProjectDisplay(), fontSize, table);

        ApplicationProgramDetail programDetail = application.getProgramDetail();
        applicationDownloadBuilderHelper.addContentRow("Study Option", programDetail == null ? null : programDetail.getStudyOptionDisplay(), fontSize, table);
    }

    private void addApplicationSummaryExtended(Application application, PdfPTable table, ApplicationDownloadBuilderFontSize fontSize) {
        addApplicationSummary(application, table, fontSize);

        applicationDownloadBuilderHelper.addContentRow("Application Code", application.getCode(), fontSize, table);
        applicationDownloadBuilderHelper.addContentRow("Application Closing Date", application.getClosingDateDisplay(dateFormat), fontSize, table);
        applicationDownloadBuilderHelper.addContentRow("Application Submission Date", application.getSubmittedTimestampDisplay(dateFormat), fontSize, table);
    }

    private void addDocument(PdfPTable table, String rowTitle, com.zuehlke.pgadmissions.domain.Document document, boolean includeAttachments) {
        applicationDownloadBuilderHelper.newTitleCellLarge(rowTitle);
        if (includeAttachments) {
            if (document == null) {
                table.addCell(applicationDownloadBuilderHelper.newContentCellMedium(null));
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
            body.setWidths(new float[] { textCellWidth, logoFileWidthPercentage });

            PdfPTable subBody = new PdfPTable(2);
            body.setTotalWidth(textCellWidth * ApplicationDownloadBuilderConfiguration.PAGE_WIDTH);
            subBody.setWidths(new float[] { 25f, 75f });
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
