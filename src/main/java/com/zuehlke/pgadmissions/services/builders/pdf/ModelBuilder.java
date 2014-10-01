package com.zuehlke.pgadmissions.services.builders.pdf;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
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
import com.zuehlke.pgadmissions.domain.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.ApplicationSupervisor;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.dto.ApplicationDownloadDTO;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;

@Component
public class ModelBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelBuilder.class);

    @Value("${xml.export.provided}")
    public String provided;

    @Value("${xml.export.not.provided}")
    public String notProvided;

    @Value("${xml.export.not.required}")
    public String notRequired;

    @Value("${xml.export.date.applicationat}")
    public String dateFormat;

    @Value("${xml.export.logo.file.location}")
    public String logoFileLocation;

    @Autowired
    private ModelBuilderHelper modelBuilderHelper;
    
    @Autowired
    private NewPageEvent newPageEvent;
    
    private ApplicationDownloadDTO applicationDownloadDTO;

    private final List<Object> bookmarks = Lists.newLinkedList();

    public void build(final ApplicationDownloadDTO applicationDownloadDTO, final Document document, final PdfWriter writer) throws PdfDocumentBuilderException {
        try {
            initialize(applicationDownloadDTO);
            Application application = applicationDownloadDTO.getApplication();
            addCoverPage(application, document, writer);
            writer.setPageEvent(newPageEvent);
            addProgramSection(application, document);
            document.add(modelBuilderHelper.newSectionSeparator());
            addPersonalDetailSection(application, document);
            document.add(modelBuilderHelper.newSectionSeparator());
            addAddressSection(application, document);
            document.add(modelBuilderHelper.newSectionSeparator());
            addQualificationSection(application, document);
            document.add(modelBuilderHelper.newSectionSeparator());
            addEmploymentSection(application, document);
            document.add(modelBuilderHelper.newSectionSeparator());
            addFundingSection(application, document);
            document.add(modelBuilderHelper.newSectionSeparator());
            addReferencesSection(application, document);
            document.add(modelBuilderHelper.newSectionSeparator());
            addDocumentsSection(application, document);
            document.add(modelBuilderHelper.newSectionSeparator());
            addAdditionalInformationSection(application, document);
            document.add(modelBuilderHelper.newSectionSeparator());
            addSupportingDocuments(application, document, writer);
        } catch (Exception e) {
            LOGGER.error("Error building download for application " + applicationDownloadDTO.getApplication().getCode(), e);
            throw new PdfDocumentBuilderException(e.getMessage(), e);
        }
    }

    private void initialize(final ApplicationDownloadDTO applicationDownloadDTO) {
        this.applicationDownloadDTO = applicationDownloadDTO;
        newPageEvent.setApplication(applicationDownloadDTO.getApplication());
        newPageEvent.setApplyHeaderFooter(true);
        bookmarks.clear();
    }

    private void addCoverPage(final Application application, final Document pdfDocument, final PdfWriter writer) throws MalformedURLException, IOException,
            DocumentException {
        pdfDocument.newPage();

        Image image = Image.getInstance(this.getClass().getResource(logoFileLocation));
        image.scalePercent(50f);

        image.setAbsolutePosition(pdfDocument.right() - image.getWidth() * 0.5f, pdfDocument.top() + 20f);
        pdfDocument.add(image);

        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.drawLine(writer.getDirectContent(), pdfDocument.left(), pdfDocument.right(), pdfDocument.top() + 10f);

        pdfDocument.add(modelBuilderHelper.newSectionSeparator());
        pdfDocument.add(modelBuilderHelper.newSectionSeparator());

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
        table.addCell(modelBuilderHelper.newColoredTableCell("APPLICATION", ModelBuilderConfiguration.BOLD_FONT));
        pdfDocument.add(table);
        pdfDocument.add(modelBuilderHelper.newSectionSeparator());
        table = new PdfPTable(2);
        table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
        table.addCell(modelBuilderHelper.newTableCell("Applicant", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));

        User applicant = application.getUser();
        String fullName = Joiner.on(" ").skipNulls()
                .join(applicant.getFirstName(), applicant.getFirstName2(), applicant.getFirstName3(), applicant.getLastName());
        table.addCell(modelBuilderHelper.newTableCell(fullName, ModelBuilderConfiguration.MEDIUM_FONT));
        table.addCell(modelBuilderHelper.newTableCell("Program", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        table.addCell(modelBuilderHelper.newTableCell(application.getProgram().getTitle(), ModelBuilderConfiguration.MEDIUM_FONT));

        addProjectTitleToTable(table, application);
        addClosingDateToTable(table, application);

        table.addCell(modelBuilderHelper.newTableCell("Application Number", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        table.addCell(modelBuilderHelper.newTableCell(application.getCode(), ModelBuilderConfiguration.MEDIUM_FONT));

        if (application.getSubmittedTimestamp() != null) {
            table.addCell(modelBuilderHelper.newTableCell("Submission date", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
            table.addCell(modelBuilderHelper.newTableCell(application.getSubmittedTimestamp().toString(dateFormat), ModelBuilderConfiguration.MEDIUM_FONT));
        }

        pdfDocument.add(table);
        pdfDocument.newPage();
    }

    private void addProgramSection(final Application application, Document pdfDocument) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
        table.addCell(modelBuilderHelper.newColoredTableCell("PROGRAMME", ModelBuilderConfiguration.BOLD_FONT));

        pdfDocument.add(table);
        pdfDocument.add(modelBuilderHelper.newSectionSeparator());

        table = new PdfPTable(2);
        table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
        table.addCell(modelBuilderHelper.newTableCell("Program", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        table.addCell(modelBuilderHelper.newTableCell(application.getProgram().getTitle(), ModelBuilderConfiguration.MEDIUM_FONT));

        table.addCell(modelBuilderHelper.newTableCell("Study Option", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        table.addCell(createPropertyCell(application, "programDetail.studyOption"));

        addProjectTitleToTable(table, application);

        addClosingDateToTable(table, application);

        table.addCell(modelBuilderHelper.newTableCell("Start Date", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        if (application.getProgramDetail().getStartDate() != null) {
            table.addCell(modelBuilderHelper.newTableCell(application.getProgramDetail().getStartDate().toString(dateFormat),
                    ModelBuilderConfiguration.MEDIUM_FONT));
        } else {
            table.addCell(modelBuilderHelper.newTableCell(notProvided, ModelBuilderConfiguration.MEDIUM_GREY_FONT));
        }

        table.addCell(modelBuilderHelper.newTableCell("How did you find us?", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        if (application.getProgramDetail().getReferralSource() != null) {
            table.addCell(modelBuilderHelper.newTableCell(application.getProgramDetail().getReferralSource().getName(), ModelBuilderConfiguration.MEDIUM_FONT));
        } else {
            table.addCell(modelBuilderHelper.newTableCell(notProvided, ModelBuilderConfiguration.MEDIUM_GREY_FONT));
        }

        pdfDocument.add(table);
        pdfDocument.add(modelBuilderHelper.newSectionSeparator());

        if (application.getSupervisors().isEmpty()) {
            table = new PdfPTable(2);
            table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
            table.addCell(modelBuilderHelper.newTableCell("Supervisor", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
            table.addCell(modelBuilderHelper.newTableCell(notProvided, ModelBuilderConfiguration.MEDIUM_GREY_FONT));
            pdfDocument.add(table);
            pdfDocument.add(modelBuilderHelper.newSectionSeparator());
        } else {
            int counter = 1;
            for (ApplicationSupervisor supervisor : application.getSupervisors()) {
                table = new PdfPTable(2);
                table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
                PdfPCell headerCell = modelBuilderHelper.newTableCell("Supervisor (" + counter++ + ")", ModelBuilderConfiguration.MEDIUM_BOLD_FONT);
                headerCell.setColspan(2);
                table.addCell(headerCell);

                table.addCell(modelBuilderHelper.newTableCell("Supervisor First Name", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(supervisor.getUser().getFirstName(), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Supervisor Last Name", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(supervisor.getUser().getLastName(), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Supervisor Email", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(supervisor.getUser().getEmail(), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Is this supervisor aware of your application?", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));

                if (BooleanUtils.isTrue(supervisor.getAcceptedSupervision())) {
                    table.addCell(modelBuilderHelper.newTableCell("Yes", ModelBuilderConfiguration.MEDIUM_FONT));
                } else {
                    table.addCell(modelBuilderHelper.newTableCell("No", ModelBuilderConfiguration.MEDIUM_FONT));
                }
                pdfDocument.add(table);
                pdfDocument.add(modelBuilderHelper.newSectionSeparator());
            }
        }
    }

    private void addPersonalDetailSection(final Application application, Document pdfDocument) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
        table.addCell(modelBuilderHelper.newColoredTableCell("PERSONAL DETAILS", ModelBuilderConfiguration.BOLD_FONT));
        pdfDocument.add(table);
        pdfDocument.add(modelBuilderHelper.newSectionSeparator());

        ApplicationPersonalDetail personalDetail = application.getPersonalDetail();
        table = new PdfPTable(2);
        table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);

        table.addCell(modelBuilderHelper.newTableCell("Title", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        table.addCell(createPropertyCell(application, "personalDetail.title.displayValue"));

        table.addCell(modelBuilderHelper.newTableCell("First Name", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        table.addCell(createPropertyCell(application, "applicant.firstName"));

        table.addCell(modelBuilderHelper.newTableCell("First Name 2", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        table.addCell(createPropertyCell(application, "applicant.firstName2"));

        table.addCell(modelBuilderHelper.newTableCell("First Name 3", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        table.addCell(createPropertyCell(application, "applicant.firstName3"));

        table.addCell(modelBuilderHelper.newTableCell("Last Name", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        table.addCell(createPropertyCell(application, "applicant.lastName"));

        table.addCell(modelBuilderHelper.newTableCell("Gender", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        table.addCell(createPropertyCell(application, "personalDetail.gender.displayValue"));

        table.addCell(modelBuilderHelper.newTableCell("Date of Birth", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        table.addCell(createPropertyCell(application, "personalDetail.dateOfBirth"));

        table.addCell(modelBuilderHelper.newTableCell("Country of Birth", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        table.addCell(createPropertyCell(application, "personalDetail.country.name"));

        table.addCell(modelBuilderHelper.newTableCell("Nationality", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        StringBuilder sb = new StringBuilder();

        if (personalDetail != null && personalDetail.getFirstNationality() != null) {
            sb.append(personalDetail.getFirstNationality().getName());
        }
        if (personalDetail != null && personalDetail.getSecondNationality() != null) {
            sb.append(", ").append(personalDetail.getSecondNationality().getName());
        }

        table.addCell(modelBuilderHelper.newTableCell(sb.toString(), ModelBuilderConfiguration.MEDIUM_FONT));

        table.addCell(modelBuilderHelper.newTableCell("Is English your first language?", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        if (personalDetail == null || personalDetail.getFirstLanguageEnglish() == null) {
            table.addCell(modelBuilderHelper.newTableCell(null, ModelBuilderConfiguration.MEDIUM_FONT));
        } else {
            if (BooleanUtils.isTrue(personalDetail.getFirstLanguageEnglish())) {
                table.addCell(modelBuilderHelper.newTableCell("Yes", ModelBuilderConfiguration.MEDIUM_FONT));
            } else {
                table.addCell(modelBuilderHelper.newTableCell("No", ModelBuilderConfiguration.MEDIUM_FONT));
            }
        }

        table.addCell(modelBuilderHelper.newTableCell("Do you have an English language qualification?", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        if (personalDetail == null || personalDetail.getLanguageQualificationAvailable() == null) {
            table.addCell(modelBuilderHelper.newTableCell(null, ModelBuilderConfiguration.MEDIUM_FONT));
        } else {
            if (BooleanUtils.isTrue(personalDetail.getLanguageQualificationAvailable())) {
                table.addCell(modelBuilderHelper.newTableCell("Yes", ModelBuilderConfiguration.MEDIUM_FONT));
            } else {
                table.addCell(modelBuilderHelper.newTableCell("No", ModelBuilderConfiguration.MEDIUM_FONT));
            }
        }

        table.addCell(modelBuilderHelper.newTableCell("Country of Residence", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        if (personalDetail == null || personalDetail.getResidenceCountry() == null) {
            table.addCell(modelBuilderHelper.newTableCell(null, ModelBuilderConfiguration.MEDIUM_FONT));
        } else {
            table.addCell(modelBuilderHelper.newTableCell(personalDetail.getResidenceCountry().getName(), ModelBuilderConfiguration.MEDIUM_FONT));
        }

        table.addCell(modelBuilderHelper.newTableCell("Do you Require a Visa to Study in the UK?", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        if (personalDetail == null || personalDetail.getVisaRequired() == null) {
            table.addCell(modelBuilderHelper.newTableCell(null, ModelBuilderConfiguration.MEDIUM_FONT));
        } else {
            if (BooleanUtils.isTrue(personalDetail.getVisaRequired())) {
                table.addCell(modelBuilderHelper.newTableCell("Yes", ModelBuilderConfiguration.MEDIUM_FONT));
            } else {
                table.addCell(modelBuilderHelper.newTableCell("No", ModelBuilderConfiguration.MEDIUM_FONT));
            }
        }

        table.addCell(modelBuilderHelper.newTableCell("Do you have a passport?", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        if (personalDetail == null || personalDetail.getPassportAvailable() == null) {
            table.addCell(modelBuilderHelper.newTableCell(null, ModelBuilderConfiguration.MEDIUM_FONT));
        } else {
            if (BooleanUtils.isTrue(personalDetail.getPassportAvailable())) {
                table.addCell(modelBuilderHelper.newTableCell("Yes", ModelBuilderConfiguration.MEDIUM_FONT));
            } else {
                table.addCell(modelBuilderHelper.newTableCell("No", ModelBuilderConfiguration.MEDIUM_FONT));
            }
        }

        if (personalDetail != null && BooleanUtils.isTrue(personalDetail.getVisaRequired())) {
            ApplicationPassport passportInformation = personalDetail.getPassport();
            if (passportInformation != null) {
                table.addCell(modelBuilderHelper.newTableCell("Passport Number", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));

                if (StringUtils.isBlank(passportInformation.getNumber())) {
                    table.addCell(modelBuilderHelper.newTableCell(null, ModelBuilderConfiguration.MEDIUM_FONT));
                } else {
                    table.addCell(modelBuilderHelper.newTableCell(passportInformation.getNumber(), ModelBuilderConfiguration.MEDIUM_FONT));
                }

                table.addCell(modelBuilderHelper.newTableCell("Name on Passport", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                if (StringUtils.isBlank(passportInformation.getName())) {
                    table.addCell(modelBuilderHelper.newTableCell(null, ModelBuilderConfiguration.MEDIUM_FONT));
                } else {
                    table.addCell(modelBuilderHelper.newTableCell(passportInformation.getName(), ModelBuilderConfiguration.MEDIUM_FONT));
                }

                table.addCell(modelBuilderHelper.newTableCell("Passport Issue Date", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                if (passportInformation.getIssueDate() == null) {
                    table.addCell(modelBuilderHelper.newTableCell(null, ModelBuilderConfiguration.MEDIUM_FONT));
                } else {
                    table.addCell(modelBuilderHelper.newTableCell(passportInformation.getIssueDate().toString(dateFormat),
                            ModelBuilderConfiguration.MEDIUM_FONT));
                }

                table.addCell(modelBuilderHelper.newTableCell("Passport Expiry Date", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                if (passportInformation.getExpiryDate() == null) {
                    table.addCell(modelBuilderHelper.newTableCell(null, ModelBuilderConfiguration.MEDIUM_FONT));
                } else {
                    table.addCell(modelBuilderHelper.newTableCell(passportInformation.getExpiryDate().toString(dateFormat),
                            ModelBuilderConfiguration.MEDIUM_FONT));
                }
            }
        }

        table.addCell(modelBuilderHelper.newTableCell("Email", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        table.addCell(modelBuilderHelper.newTableCell(application.getUser().getEmail(), ModelBuilderConfiguration.MEDIUM_FONT));

        table.addCell(modelBuilderHelper.newTableCell("Telephone", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        table.addCell(createPropertyCell(application, "personalDetail.phoneNumber"));

        table.addCell(modelBuilderHelper.newTableCell("Skype", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        table.addCell(createPropertyCell(application, "personalDetail.messenger"));

        if (applicationDownloadDTO.isIncludeEqualOpportunitiesData()) {
            table.addCell(modelBuilderHelper.newTableCell("Ethnicity", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
            table.addCell(createPropertyCell(application, "personalDetail.ethnicity.name"));
            table.addCell(modelBuilderHelper.newTableCell("Disability", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
            table.addCell(createPropertyCell(application, "personalDetail.disability.name"));
        }

        pdfDocument.add(table);
        pdfDocument.add(modelBuilderHelper.newSectionSeparator());

        if (personalDetail == null || personalDetail.getLanguageQualification() == null) {
            table = new PdfPTable(2);
            table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
            table.addCell(modelBuilderHelper.newTableCell("English Language Qualification", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
            table.addCell(modelBuilderHelper.newTableCell(null, ModelBuilderConfiguration.MEDIUM_FONT));
        } else {
            ApplicationLanguageQualification languageQualification = personalDetail.getLanguageQualification();
            table = new PdfPTable(2);
            table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
            PdfPCell headerCell = modelBuilderHelper.newTableCell("English Language Qualification", ModelBuilderConfiguration.MEDIUM_BOLD_FONT);
            headerCell.setColspan(2);
            table.addCell(headerCell);

            table.addCell(modelBuilderHelper.newTableCell("Qualification Type", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
            table.addCell(modelBuilderHelper.newTableCell(languageQualification.getType().getName(), ModelBuilderConfiguration.MEDIUM_FONT));

            table.addCell(modelBuilderHelper.newTableCell("Date of Examination", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
            table.addCell(modelBuilderHelper.newTableCell(languageQualification.getExamDate().toString(dateFormat), ModelBuilderConfiguration.MEDIUM_FONT));

            table.addCell(modelBuilderHelper.newTableCell("Overall Score", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
            table.addCell(modelBuilderHelper.newTableCell(languageQualification.getOverallScore(), ModelBuilderConfiguration.MEDIUM_FONT));

            table.addCell(modelBuilderHelper.newTableCell("Reading Score", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
            table.addCell(modelBuilderHelper.newTableCell(languageQualification.getReadingScore(), ModelBuilderConfiguration.MEDIUM_FONT));

            table.addCell(modelBuilderHelper.newTableCell("Essay / Writing Score", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
            table.addCell(modelBuilderHelper.newTableCell(languageQualification.getWritingScore(), ModelBuilderConfiguration.MEDIUM_FONT));

            table.addCell(modelBuilderHelper.newTableCell("Speaking Score", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
            table.addCell(modelBuilderHelper.newTableCell(languageQualification.getSpeakingScore(), ModelBuilderConfiguration.MEDIUM_FONT));

            table.addCell(modelBuilderHelper.newTableCell("Listening Score", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
            table.addCell(modelBuilderHelper.newTableCell(languageQualification.getListeningScore(), ModelBuilderConfiguration.MEDIUM_FONT));

            table.addCell(modelBuilderHelper.newTableCell("Did you sit the exam online?", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
            addDocument(table, "Certificate (PDF)", languageQualification.getDocument());
        }

        pdfDocument.add(table);
    }

    private void addAddressSection(final Application application, Document pdfDocument) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
        table.addCell(modelBuilderHelper.newColoredTableCell("ADDRESS", ModelBuilderConfiguration.BOLD_FONT));

        pdfDocument.add(table);
        pdfDocument.add(modelBuilderHelper.newSectionSeparator());

        table = new PdfPTable(2);
        table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);

        ApplicationAddress address = application.getAddress();
        table.addCell(modelBuilderHelper.newTableCell("Current Address", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        if (address.getCurrentAddress() == null) {
            table.addCell(modelBuilderHelper.newTableCell(null, ModelBuilderConfiguration.MEDIUM_FONT));
        } else {
            table.addCell(modelBuilderHelper.newTableCell(address.getCurrentAddress().getLocationString(), ModelBuilderConfiguration.MEDIUM_FONT));
        }

        table.addCell(modelBuilderHelper.newTableCell("Country", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        if (address.getCurrentAddress() == null) {
            table.addCell(modelBuilderHelper.newTableCell(null, ModelBuilderConfiguration.MEDIUM_FONT));
        } else {
            table.addCell(modelBuilderHelper.newTableCell(address.getCurrentAddress().getDomicile().getName(), ModelBuilderConfiguration.MEDIUM_FONT));
        }

        table.addCell(modelBuilderHelper.newTableCell("Contact Address", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        if (address.getContactAddress() == null) {
            table.addCell(modelBuilderHelper.newTableCell(null, ModelBuilderConfiguration.MEDIUM_FONT));
        } else {
            table.addCell(modelBuilderHelper.newTableCell(address.getContactAddress().getLocationString(), ModelBuilderConfiguration.MEDIUM_FONT));
        }

        table.addCell(modelBuilderHelper.newTableCell("Country", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        if (address.getContactAddress() == null) {
            table.addCell(modelBuilderHelper.newTableCell(null, ModelBuilderConfiguration.MEDIUM_FONT));
        } else {
            table.addCell(modelBuilderHelper.newTableCell(address.getContactAddress().getDomicile().getName(), ModelBuilderConfiguration.MEDIUM_FONT));
        }
        pdfDocument.add(table);
    }

    private void addQualificationSection(final Application application, Document pdfDocument) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
        table.addCell(modelBuilderHelper.newColoredTableCell("QUALIFICATIONS", ModelBuilderConfiguration.BOLD_FONT));

        pdfDocument.add(table);
        pdfDocument.add(modelBuilderHelper.newSectionSeparator());

        if (application.getQualifications().isEmpty()) {
            table = new PdfPTable(2);
            table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
            table.addCell(modelBuilderHelper.newTableCell("Qualification", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
            table.addCell(modelBuilderHelper.newTableCell(null, ModelBuilderConfiguration.MEDIUM_FONT));
            pdfDocument.add(table);
        } else {
            int counter = 1;
            for (ApplicationQualification qualification : application.getQualifications()) {
                table = new PdfPTable(2);
                table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
                PdfPCell headerCell = modelBuilderHelper.newTableCell("Qualification (" + counter++ + ")", ModelBuilderConfiguration.MEDIUM_BOLD_FONT);
                headerCell.setColspan(2);
                table.addCell(headerCell);
                table.addCell(modelBuilderHelper.newTableCell("Institution Country", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(qualification.getInstitution().getDomicile().getName(), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Institution/Provider Name", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(qualification.getInstitution().getName(), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Qualification Type", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(qualification.getType().getName(), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Qualification Title", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(qualification.getTitle(), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Qualification Subject", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(qualification.getSubject(), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Language of Study", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(qualification.getLanguage(), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Start Date", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(qualification.getStartDate().toString(dateFormat), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Has this Qualification been awarded", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));

                if (BooleanUtils.isTrue(qualification.getCompleted())) {
                    table.addCell(modelBuilderHelper.newTableCell("Yes", ModelBuilderConfiguration.MEDIUM_FONT));
                } else {
                    table.addCell(modelBuilderHelper.newTableCell("No", ModelBuilderConfiguration.MEDIUM_FONT));
                }

                if (qualification.getCompleted()) {
                    table.addCell(modelBuilderHelper.newTableCell("Grade/Result/GPA", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                } else {
                    table.addCell(modelBuilderHelper.newTableCell("Expected Grade/Result/GPA", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                }

                table.addCell(modelBuilderHelper.newTableCell(qualification.getGrade(), ModelBuilderConfiguration.MEDIUM_FONT));

                if (BooleanUtils.isTrue(qualification.getCompleted())) {
                    table.addCell(modelBuilderHelper.newTableCell("Award Date", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                } else {
                    table.addCell(modelBuilderHelper.newTableCell("Expected Award Date", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                }

                boolean awarded = qualification.getAwardDate() != null;

                if (awarded) {
                    table.addCell(modelBuilderHelper.newTableCell(notProvided, ModelBuilderConfiguration.MEDIUM_GREY_FONT));
                } else {
                    table.addCell(modelBuilderHelper.newTableCell(qualification.getAwardDate().toString(dateFormat), ModelBuilderConfiguration.MEDIUM_FONT));
                }

                addDocument(table, awarded ? "Proof of award" : "Interim Transcript", qualification.getDocument());

                pdfDocument.add(table);
                pdfDocument.add(modelBuilderHelper.newSectionSeparator());
            }
        }
    }

    private void addEmploymentSection(final Application application, Document pdfDocument) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
        table.addCell(modelBuilderHelper.newColoredTableCell("EMPLOYMENT", ModelBuilderConfiguration.BOLD_FONT));

        pdfDocument.add(table);
        pdfDocument.add(modelBuilderHelper.newSectionSeparator());

        if (application.getEmploymentPositions().isEmpty()) {
            table = new PdfPTable(2);
            table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
            table.addCell(modelBuilderHelper.newTableCell("Position", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
            table.addCell(modelBuilderHelper.newTableCell(null, ModelBuilderConfiguration.MEDIUM_FONT));
            pdfDocument.add(table);
        } else {
            int counter = 1;
            for (ApplicationEmploymentPosition position : application.getEmploymentPositions()) {
                table = new PdfPTable(2);
                table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
                PdfPCell headerCell = modelBuilderHelper.newTableCell("Position (" + counter++ + ")", ModelBuilderConfiguration.MEDIUM_BOLD_FONT);
                headerCell.setColspan(2);
                table.addCell(headerCell);
                table.addCell(modelBuilderHelper.newTableCell("Country", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(position.getEmployerAddress().getDomicile().getName(), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Employer Name", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(position.getEmployerName(), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Employer Address", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(position.getEmployerAddress().getLocationString(), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Position", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(position.getPosition(), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Roles and Responsibilities", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(position.getRemit(), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Start Date", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(position.getStartDate().toString(dateFormat), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Is this your Current Position", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                if (BooleanUtils.isTrue(position.isCurrent())) {
                    table.addCell(modelBuilderHelper.newTableCell("Yes", ModelBuilderConfiguration.MEDIUM_FONT));
                } else {
                    table.addCell(modelBuilderHelper.newTableCell("No", ModelBuilderConfiguration.MEDIUM_FONT));
                }

                table.addCell(modelBuilderHelper.newTableCell("End Date", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));

                if (position.getEndDate() == null) {
                    table.addCell(modelBuilderHelper.newTableCell(null, ModelBuilderConfiguration.MEDIUM_GREY_FONT));
                } else {
                    table.addCell(modelBuilderHelper.newTableCell(position.getEndDate().toString(dateFormat), ModelBuilderConfiguration.MEDIUM_FONT));
                }

                pdfDocument.add(table);
                pdfDocument.add(modelBuilderHelper.newSectionSeparator());
            }
        }
    }

    private void addFundingSection(final Application application, Document pdfDocument) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
        table.addCell(modelBuilderHelper.newColoredTableCell("FUNDING", ModelBuilderConfiguration.BOLD_FONT));

        pdfDocument.add(table);
        pdfDocument.add(modelBuilderHelper.newSectionSeparator());

        if (application.getFundings().isEmpty()) {
            table = new PdfPTable(2);
            table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
            table.addCell(modelBuilderHelper.newTableCell("Funding", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
            table.addCell(modelBuilderHelper.newTableCell(null, ModelBuilderConfiguration.MEDIUM_FONT));
            pdfDocument.add(table);
        } else {
            int counter = 1;
            for (ApplicationFunding funding : application.getFundings()) {
                table = new PdfPTable(2);
                table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
                PdfPCell headerCell = modelBuilderHelper.newTableCell("Funding (" + counter++ + ")", ModelBuilderConfiguration.MEDIUM_BOLD_FONT);
                headerCell.setColspan(2);
                table.addCell(headerCell);
                table.addCell(modelBuilderHelper.newTableCell("Funding Type", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(funding.getFundingSource().getName(), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Description", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(funding.getDescription(), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Value of Award (GBP)", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(funding.getValue(), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Award Date", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(funding.getAwardDate().toString(dateFormat), ModelBuilderConfiguration.MEDIUM_FONT));

                addDocument(table, "Proof Of Award", funding.getDocument());

                pdfDocument.add(table);
                pdfDocument.add(modelBuilderHelper.newSectionSeparator());
            }
        }
    }

    private void addReferencesSection(final Application application, Document pdfDocument) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
        table.addCell(modelBuilderHelper.newColoredTableCell("REFERENCES", ModelBuilderConfiguration.BOLD_FONT));

        pdfDocument.add(table);
        pdfDocument.add(modelBuilderHelper.newSectionSeparator());

        if (application.getReferees().isEmpty()) {
            table = new PdfPTable(2);
            table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
            table.addCell(modelBuilderHelper.newTableCell("Reference", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
            table.addCell(modelBuilderHelper.newTableCell(null, ModelBuilderConfiguration.MEDIUM_FONT));
            pdfDocument.add(table);
        } else {
            int counter = 1;
            for (ApplicationReferee referee : application.getReferees()) {
                table = new PdfPTable(2);
                table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
                PdfPCell headerCell = modelBuilderHelper.newTableCell("Reference (" + counter++ + ")", ModelBuilderConfiguration.MEDIUM_BOLD_FONT);
                headerCell.setColspan(2);
                table.addCell(headerCell);
                table.addCell(modelBuilderHelper.newTableCell("First Name", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(referee.getUser().getFirstName(), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Last Name", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(referee.getUser().getLastName(), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Employer", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(referee.getJobEmployer(), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Position", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(referee.getJobTitle(), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Address", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(referee.getAddress().getLocationString(), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Country", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(referee.getAddress().getDomicile().getName(), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Email", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(referee.getUser().getEmail(), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Telephone", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(referee.getPhoneNumber(), ModelBuilderConfiguration.MEDIUM_FONT));

                table.addCell(modelBuilderHelper.newTableCell("Skype", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                table.addCell(modelBuilderHelper.newTableCell(referee.getSkype(), ModelBuilderConfiguration.MEDIUM_FONT));

                if (applicationDownloadDTO.isIncludeReferences()) {
                    table.addCell(modelBuilderHelper.newTableCell("Reference", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                    Comment referenceComment = referee.getComment();
                    if (referenceComment != null) {
                        addBookmark(table, referenceComment);
                    } else {
                        table.addCell(modelBuilderHelper.newTableCell(notProvided, ModelBuilderConfiguration.MEDIUM_GREY_FONT));
                    }
                }
                pdfDocument.add(table);
                pdfDocument.add(modelBuilderHelper.newSectionSeparator());
            }
        }
    }

    private void addDocumentsSection(final Application application, Document pdfDocument) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
        table.addCell(modelBuilderHelper.newColoredTableCell("DOCUMENTS", ModelBuilderConfiguration.BOLD_FONT));

        pdfDocument.add(table);
        pdfDocument.add(modelBuilderHelper.newSectionSeparator());

        table = new PdfPTable(2);
        table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);

        ApplicationDocument documents = application.getDocument();

        addDocument(table, "Personal Statement", documents.getPersonalStatement());
        addDocument(table, "CV/Resume", documents.getCv());

        pdfDocument.add(table);
    }

    private void addAdditionalInformationSection(final Application application, Document pdfDocument) throws DocumentException {
        if (applicationDownloadDTO.isIncludeEqualOpportunitiesData()) {
            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
            table.addCell(modelBuilderHelper.newColoredTableCell("ADDITIONAL INFORMATION", ModelBuilderConfiguration.BOLD_FONT));

            pdfDocument.add(table);
            pdfDocument.add(modelBuilderHelper.newSectionSeparator());

            ApplicationAdditionalInformation additionalInformation = application.getAdditionalInformation();
            table = new PdfPTable(2);
            table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
            table.addCell(modelBuilderHelper.newTableCell("Do you have any unspent Criminial Convictions?", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
            if (additionalInformation == null) {
                table.addCell(modelBuilderHelper.newTableCell(notProvided, ModelBuilderConfiguration.MEDIUM_GREY_FONT));
            } else if (BooleanUtils.isTrue(additionalInformation.getConvictionsText() != null)) {
                table.addCell(modelBuilderHelper.newTableCell("Yes", ModelBuilderConfiguration.MEDIUM_FONT));
            } else {
                table.addCell(modelBuilderHelper.newTableCell("No", ModelBuilderConfiguration.MEDIUM_FONT));
            }

            table.addCell(modelBuilderHelper.newTableCell("Description", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
            String convictionsText = additionalInformation == null ? null : Strings.nullToEmpty(additionalInformation.getConvictionsText());
            table.addCell(modelBuilderHelper.newTableCell(convictionsText, ModelBuilderConfiguration.MEDIUM_FONT));

            pdfDocument.add(table);
        }
    }

    private void addSupportingDocuments(final Application application, final Document pdfDocument, final PdfWriter pdfWriter) throws DocumentException {
        if (applicationDownloadDTO.isIncludeAttachments()) {
            for (Integer i = 0; i < bookmarks.size(); i++) {
                pdfDocument.newPage();

                NewPageEvent pageEvent = (NewPageEvent) pdfWriter.getPageEvent();
                pageEvent.setApplyHeaderFooter(true);

                Object object = bookmarks.get(i);
                String index = i.toString();
                if (object instanceof com.zuehlke.pgadmissions.domain.Document) {
                    com.zuehlke.pgadmissions.domain.Document document = (com.zuehlke.pgadmissions.domain.Document) object;

                    if (document != null) {
                        pdfDocument.add(new Chunk("APPENDIX (" + index + ")").setLocalDestination(index));

                        if (document.getApplicationPersonalStatement() != null) {
                            pdfDocument.add(new Chunk(" - Personal Statement"));
                        } else if (document.getApplicationCv() != null) {
                            pdfDocument.add(new Chunk(" - CV"));
                        } else if (document.getApplicationFunding() != null) {
                            pdfDocument.add(new Chunk(" - Funding proof of award"));
                        } else if (document.getApplicationQualification() != null) {
                            pdfDocument.add(new Chunk(" - Qualification Transcript"));
                        } else if (document.getApplicationLanguageQualification() != null) {
                            pdfDocument.add(new Chunk(" - English Language Certificate"));
                        }

                        try {
                            readPdf(pdfDocument, document, pdfWriter);
                        } catch (Exception e) {
                            LOGGER.warn("Error reading PDF document", e.getMessage());
                        }
                    }
                } else if (object instanceof Comment) {
                    Comment reference = (Comment) object;
                    if (reference.getAction().getId() == PrismAction.APPLICATION_PROVIDE_REFERENCE) {
                        pdfDocument.add(new Chunk("APPENDIX (" + index + ")").setLocalDestination(index));

                        pdfDocument.add(new Chunk(" - Reference"));
                        pdfDocument.add(modelBuilderHelper.newSectionSeparator());
                        pdfDocument.add(modelBuilderHelper.newSectionSeparator());
                        pdfDocument.add(modelBuilderHelper.newSectionSeparator());
                        pdfDocument.add(modelBuilderHelper.newSectionSeparator());

                        PdfPTable table = new PdfPTable(1);
                        table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
                        table.addCell(modelBuilderHelper.newColoredTableCell("REFERENCE", ModelBuilderConfiguration.BOLD_FONT));
                        pdfDocument.add(table);
                        pdfDocument.add(modelBuilderHelper.newSectionSeparator());

                        table = new PdfPTable(2);
                        table.setWidthPercentage(ModelBuilderConfiguration.WIDTH_PERCENTAGE);
                        table.addCell(modelBuilderHelper.newTableCell("Referee", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                        table.addCell(modelBuilderHelper.newTableCell(reference.getUser().getFirstName() + " " + reference.getUser().getLastName(),
                                ModelBuilderConfiguration.MEDIUM_FONT));
                        table.addCell(modelBuilderHelper.newTableCell("Comment", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                        table.addCell(modelBuilderHelper.newTableCell(reference.getContent(), ModelBuilderConfiguration.MEDIUM_FONT));
                        table.addCell(modelBuilderHelper.newTableCell("Is the applicant suitable for postgraduate study at UCL?",
                                ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                        if (BooleanUtils.isTrue(reference.getSuitableForInstitution())) {
                            table.addCell(modelBuilderHelper.newTableCell("Yes", ModelBuilderConfiguration.MEDIUM_FONT));
                        } else {
                            table.addCell(modelBuilderHelper.newTableCell("No", ModelBuilderConfiguration.MEDIUM_FONT));
                        }
                        table.addCell(modelBuilderHelper.newTableCell("Is the applicant suitable for their chosen postgraduate study program?",
                                ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
                        if (BooleanUtils.isTrue(reference.getSuitableForOpportunity())) {
                            table.addCell(modelBuilderHelper.newTableCell("Yes", ModelBuilderConfiguration.MEDIUM_FONT));
                        } else {
                            table.addCell(modelBuilderHelper.newTableCell("No", ModelBuilderConfiguration.MEDIUM_FONT));
                        }

                        pdfDocument.add(table);
                        for (com.zuehlke.pgadmissions.domain.Document document : reference.getDocuments()) {
                            try {
                                readPdf(pdfDocument, document, pdfWriter);
                            } catch (Exception e) {
                                LOGGER.warn("Error reading PDF document", e.getMessage());
                            }
                        }
                    }
                }
            }
        }
    }

    private void readPdf(final Document pdfDocument, final com.zuehlke.pgadmissions.domain.Document document, final PdfWriter pdfWriter) throws IOException {
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

    private void addClosingDateToTable(PdfPTable table, final Application application) {
        table.addCell(modelBuilderHelper.newTableCell("Closing date", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        LocalDate closingDate = application.getClosingDate();
        table.addCell(modelBuilderHelper.newTableCell(closingDate == null ? notRequired : closingDate.toString(dateFormat),
                ModelBuilderConfiguration.MEDIUM_FONT));
    }

    private void addProjectTitleToTable(PdfPTable table, final Application application) {
        table.addCell(modelBuilderHelper.newTableCell("Project", ModelBuilderConfiguration.MEDIUM_BOLD_FONT));
        String projectTitle;
        if (application.getProject() == null) {
            projectTitle = notRequired;
        } else {
            projectTitle = application.getProject().getTitle();
        }
        table.addCell(modelBuilderHelper.newTableCell(projectTitle, ModelBuilderConfiguration.MEDIUM_FONT));
    }

    private PdfPCell createPropertyCell(Object bean, String propertyName) {
        Object value;
        try {
            value = PropertyUtils.getNestedProperty(bean, propertyName);
        } catch (NestedNullException nextedException) {
            value = null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String valueString;
        Class<?> classValue = value.getClass();

        if (classValue.equals(LocalDate.class) || classValue.equals(DateTime.class)) {
            DateTime valueDateTime = new DateTime(value);
            valueString = valueDateTime.toString(dateFormat);
        } else {
            valueString = (String) value;
        }

        return modelBuilderHelper.newTableCell(valueString, ModelBuilderConfiguration.MEDIUM_FONT);
    }

    private void addDocument(PdfPTable table, String rowTitle, com.zuehlke.pgadmissions.domain.Document document) {
        modelBuilderHelper.newTableCell(rowTitle, ModelBuilderConfiguration.MEDIUM_BOLD_FONT);
        if (applicationDownloadDTO.isIncludeAttachments()) {
            if (document != null) {
                addBookmark(table, document);
            } else {
                table.addCell(modelBuilderHelper.newTableCell(notProvided, ModelBuilderConfiguration.MEDIUM_GREY_FONT));
            }
        } else {
            if (document != null) {
                table.addCell(modelBuilderHelper.newTableCell(provided, ModelBuilderConfiguration.MEDIUM_FONT));
            } else {
                table.addCell(modelBuilderHelper.newTableCell(notProvided, ModelBuilderConfiguration.MEDIUM_GREY_FONT));
            }
        }
    }

    private void addBookmark(PdfPTable table, Object object) {
        int index = bookmarks.size();
        table.addCell(modelBuilderHelper.newAppendixTableCell("See APPENDIX(" + index + ")", ModelBuilderConfiguration.MEDIUM_LINK_FONT, index));
        bookmarks.add(object);
    }

    @Component
    private class NewPageEvent extends PdfPageEventHelper {

        private Application application;

        private boolean applyHeaderFooter = true;

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            if (applyHeaderFooter) {
                try {
                    addHeaderToPage(writer, document);
                    addFooterToPage(writer, document);
                } catch (Exception e) {
                    LOGGER.error("Error applying header/footer to download", e);
                    throw new RuntimeException(e);
                }
            }
        }

        private void addFooterToPage(PdfWriter writer, Document document) {
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.drawLine(writer.getDirectContent(), document.left(), document.right(), document.bottom() - 15f);
            Phrase footerPhrase = new Phrase("Page " + (1 + document.getPageNumber()), ModelBuilderConfiguration.SMALL_FONT);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, footerPhrase, document.left(), document.bottom() - 25f, 0);
        }

        private void addHeaderToPage(PdfWriter writer, Document document) throws DocumentException, BadElementException, IOException {
            PdfPTable table = new PdfPTable(2);
            table.setTotalWidth(0.65f * document.getPageSize().getWidth());
            table.setWidths(new float[] { 25f, 75f });

            addHeaderRow(table, "Program", application.getProgram().getTitle());
            Project project = application.getProject();
            addHeaderRow(table, "Project", project == null ? null : project.getTitle());
            addHeaderRow(table, "Applicant", application.getUser().toString());
            addHeaderRow(table, "Application", application.getCode());
            addHeaderRow(table, "Submitted", application.getSubmittedTimestamp().toString(dateFormat));

            table.writeSelectedRows(0, -1, document.left(), document.top() + 55f, writer.getDirectContent());

            Image image = Image.getInstance(this.getClass().getResource(logoFileLocation));
            image.scalePercent(50f);

            image.setAbsolutePosition(document.right() - image.getWidth() * 0.5f, document.top() + 20f);
            document.add(image);
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.drawLine(writer.getDirectContent(), document.left(), document.right(), document.top() + 10f);
        }

        private void addHeaderRow(PdfPTable table, String rowTitle, String rowData) {
            PdfPCell cell = new PdfPCell(new Phrase(rowTitle, ModelBuilderConfiguration.SMALL_BOLD_FONT));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(rowData == null ? notRequired : rowData, ModelBuilderConfiguration.SMALL_FONT));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
        }

        public void setApplyHeaderFooter(boolean applyHeader) {
            this.applyHeaderFooter = applyHeader;
        }

        public final void setApplication(Application application) {
            this.application = application;
        }

    }

}
