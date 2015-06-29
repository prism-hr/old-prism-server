package com.zuehlke.pgadmissions.services.builders.download;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_THEME_SECONDARY;
import static com.zuehlke.pgadmissions.services.builders.download.ApplicationDownloadBuilderConfiguration.ApplicationDownloadBuilderFontSize.MEDIUM;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.itextpdf.text.Anchor;
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
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.events.PdfPageEventForwarder;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationAdditionalInformation;
import com.zuehlke.pgadmissions.domain.application.ApplicationAddress;
import com.zuehlke.pgadmissions.domain.application.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.application.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.application.ApplicationFunding;
import com.zuehlke.pgadmissions.domain.application.ApplicationLanguageQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationPassport;
import com.zuehlke.pgadmissions.domain.application.ApplicationPersonalDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationPrize;
import com.zuehlke.pgadmissions.domain.application.ApplicationProgramDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.application.ApplicationStudyDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationSupervisor;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.ApplicationDownloadMode;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ApplicationDownloadDTO;
import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.CustomizationService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.builders.download.ApplicationDownloadBuilderConfiguration.ApplicationDownloadBuilderFontSize;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ApplicationDownloadBuilder {

    private PropertyLoader propertyLoader;

    private final Map<String, Object> bookmarks = Maps.newLinkedHashMap();

    private ApplicationDownloadBuilderHelper applicationDownloadBuilderHelper;

    @Value("${xml.export.logo.file.width.percentage}")
    private Float logoFileWidthPercentage;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CustomizationService customizationService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationContext applicationContext;

    public void build(ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument, PdfWriter writer) throws PdfDocumentBuilderException {
        try {
            Application application = applicationDownloadDTO.getApplication();
            addCoverPage(application, applicationDownloadDTO, pdfDocument, writer);
            writer.setPageEvent(new NewPageEvent().withApplication(application));
            addProgramSection(application, pdfDocument);
            addSupervisorSection(application, pdfDocument);
            addPersonalDetailSection(application, applicationDownloadDTO, pdfDocument);
            addAddressSection(application, pdfDocument);
            addQualificationSection(application, applicationDownloadDTO, pdfDocument);
            addEmploymentSection(application, pdfDocument);
            addFundingSection(application, applicationDownloadDTO, pdfDocument);
            addPrizesSection(application, pdfDocument);
            addReferencesSection(applicationDownloadDTO, pdfDocument);
            addDocumentSection(application, applicationDownloadDTO, pdfDocument);
            addAdditionalInformationSection(application, applicationDownloadDTO, pdfDocument);
            addSupportingDocuments(applicationDownloadDTO, pdfDocument, writer);
        } catch (Exception e) {
            throw new PdfDocumentBuilderException(e);
        }
    }

    public ApplicationDownloadBuilder localize(PropertyLoader propertyLoader, ApplicationDownloadBuilderHelper applicationDownloadBuilderHelper) {
        this.propertyLoader = propertyLoader;
        this.applicationDownloadBuilderHelper = applicationDownloadBuilderHelper;
        return this;
    }

    private void addCoverPage(Application application, ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument, PdfWriter writer)
            throws Exception {
        pdfDocument.newPage();
        addLogoImage(pdfDocument);

        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.drawLine(writer.getDirectContent(), pdfDocument.left(), pdfDocument.right(), pdfDocument.top() + 10f);

        pdfDocument.add(applicationDownloadBuilderHelper.newSectionSeparator());
        pdfDocument.add(applicationDownloadBuilderHelper.newSectionSeparator());

        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_HEADER));

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_CREATOR), application.getUser().getFullName(), body);

        if (applicationDownloadDTO.isIncludeAssessments()) {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_AVERAGE_RATING), application.getApplicationRatingAverageDisplay(),
                    body);
        }

        addApplicationSummaryExtended(application, body, MEDIUM);
        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        pdfDocument.newPage();
    }

    private void addProgramSection(Application application, Document pdfDocument) throws Exception {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_PROGRAM_DETAIL_HEADER));
        addApplicationSummary(application, body, MEDIUM);

        ApplicationProgramDetail programDetail = application.getProgramDetail();
        boolean programDetailNull = programDetail == null;

        String dateFormat = propertyLoader.load(SYSTEM_DATE_FORMAT);
        String startDate = programDetailNull ? null : programDetail.getStartDateDisplay(dateFormat);
        String confirmedStartDate = programDetailNull ? null : application.getConfirmedStartDateDisplay(dateFormat);

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PROGRAM_DETAIL_START_DATE_LABEL, APPLICATION_CONFIRMED_START_DATE,
                confirmedStartDate == null), confirmedStartDate == null ? startDate : confirmedStartDate, body);

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_REFERRAL_SOURCE),
                programDetailNull ? null : programDetail.getReferralSourceDisplay(), body);

        addStudyDetailSection(application, body);
        addThemeSection(application, body);

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
    }

    private void addStudyDetailSection(Application application, PdfPTable body) throws Exception {
        if (customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application,
                PrismWorkflowPropertyDefinition.APPLICATION_STUDY_DETAIL)) {
            ApplicationStudyDetail studyDetail = application.getStudyDetail();
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_STUDY_DETAIL_LOCATION_LABEL),
                    studyDetail == null ? null : studyDetail.getStudyLocation(), body);
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_STUDY_DETAIL_DIVISION_LABEL),
                    studyDetail == null ? null : studyDetail.getStudyDivision(), body);
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_STUDY_DETAIL_AREA_LABEL),
                    studyDetail == null ? null : studyDetail.getStudyArea(), body);
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_STUDY_DETAIL_APPLICATION_ID_LABEL), studyDetail == null ? null
                    : studyDetail.getStudyApplicationId(), body);
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_STUDY_DETAIL_START_DATE_LABEL),
                    studyDetail == null ? null : studyDetail.getStudyStartDateDisplay(propertyLoader.load(SYSTEM_DATE_FORMAT)), body);
        }
    }

    private void addThemeSection(Application application, PdfPTable body) throws Exception {
        if (customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application,
                PrismWorkflowPropertyDefinition.APPLICATION_THEME_PRIMARY)) {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PROGRAM_DETAIL_PRIMARY_THEME_LABEL),
                    application.getPrimaryThemeDisplay(), body);
        }

        if (customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application, APPLICATION_THEME_SECONDARY)) {
            applicationDownloadBuilderHelper.addContentRowMedium(
                    propertyLoader.load(PrismDisplayPropertyDefinition.APPLICATION_PROGRAM_DETAIL_SECONDARY_THEME_LABEL),
                    application.getSecondaryThemeDisplay(), body);
        }
    }

    private void addSupervisorSection(Application application, Document pdfDocument) throws Exception {
        if (customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application,
                PrismWorkflowPropertyDefinition.APPLICATION_ASSIGN_SUGGESTED_SUPERVISOR)) {
            PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_SUPERVISOR_HEADER));
            Set<ApplicationSupervisor> supervisors = application.getSupervisors();

            String subheader = propertyLoader.load(APPLICATION_SUPERVISOR_SUBHEADER);

            if (supervisors.isEmpty()) {
                applicationDownloadBuilderHelper.addContentRowMedium(subheader, null, body);
                applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
            } else {
                int counter = 1;
                for (ApplicationSupervisor supervisor : supervisors) {
                    PdfPTable subBody = applicationDownloadBuilderHelper.startSubSection(subheader + "(" + counter++ + ")");

                    User user = supervisor.getUser();

                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_FIRST_NAME), user.getFirstName(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_HTML_GENERAL_FIELD_LAST_NAME_LABEL), user.getLastName(),
                            subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_EMAIL), user.getEmail(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_SUPERVISOR_ACCEPTED_SUPERVISION_LABEL),
                            propertyLoader.load(SYSTEM_YES, SYSTEM_NO, supervisor.getAcceptedSupervision()), subBody);

                    applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
                }
            }
        }
    }

    private void addPersonalDetailSection(Application application, ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument)
            throws Exception {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_PERSONAL_DETAIL_HEADER));

        ApplicationPersonalDetail personalDetail = application.getPersonalDetail();
        boolean personalDetailNull = personalDetail == null;

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_TITLE_LABEL), personalDetailNull ? null
                : personalDetail.getTitleDisplay(),
                body);

        User applicationCreator = application.getUser();

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_FIRST_NAME), applicationCreator.getFirstName(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_FIRST_NAME_2), applicationCreator.getFirstName2(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_FIRST_NAME_3), applicationCreator.getFirstName3(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_HTML_GENERAL_FIELD_LAST_NAME_LABEL), applicationCreator.getLastName(),
                body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_EMAIL), applicationCreator.getEmail(), body);

        applicationDownloadBuilderHelper
                .addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_TELEPHONE_LABEL), personalDetailNull ? null : personalDetail.getPhone(),
                        body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_SKYPE_LABEL), personalDetailNull ? null
                : personalDetail.getSkype(), body);

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_GENDER_LABEL), personalDetailNull ? null
                : personalDetail.getGenderDisplay(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(
                propertyLoader.load(PrismDisplayPropertyDefinition.APPLICATION_PERSONAL_DETAIL_DATE_OF_BIRTH_LABEL),
                personalDetailNull ? null : personalDetail.getDateOfBirth(propertyLoader.load(SYSTEM_DATE_FORMAT)), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_COUNTRY_OF_BIRTH_LABEL), personalDetailNull ? null
                : personalDetail.getCountryDisplay(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_DOMICILE_LABEL), personalDetailNull ? null
                : personalDetail.getDomicileDisplay(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_NATIONALITY_LABEL), personalDetailNull ? null
                : personalDetail.getNationalityDisplay(), body);

        appendEqualOpportunitiesSection(applicationDownloadDTO, body, application, personalDetail, personalDetailNull);

        boolean passportAvailable = addPassportHeader(application, personalDetail, personalDetailNull, body);
        boolean languageQualificationAvailable = addLanguageQualificationHeader(application, personalDetail, personalDetailNull, body);

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);

        if (passportAvailable) {
            addPassportSection(personalDetail.getPassport(), pdfDocument);
        }

        if (languageQualificationAvailable) {
            addLanguageQualificationSection(application, personalDetail.getLanguageQualification(), applicationDownloadDTO, pdfDocument);
        }
    }

    private void appendEqualOpportunitiesSection(ApplicationDownloadDTO applicationDownloadDTO, PdfPTable body, Application application,
            ApplicationPersonalDetail personalDetail, boolean personalDetailNull) throws Exception {
        if (applicationDownloadDTO.isIncludeEqualOpportunities()) {
            if (customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application,
                    PrismWorkflowPropertyDefinition.APPLICATION_DEMOGRAPHIC)) {
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_ETHNICITY_LABEL),
                        personalDetailNull ? null
                                : personalDetail.getEthnicityDisplay(), body);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_DISABILITY_LABEL),
                        personalDetailNull ? null
                                : personalDetail.getDisabilityDisplay(), body);
            }
        }
    }

    private boolean addPassportHeader(Application application, ApplicationPersonalDetail personalDetail, boolean personalDetailNull, PdfPTable body)
            throws Exception {
        boolean passportAvailable = false;
        if (customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application,
                PrismWorkflowPropertyDefinition.APPLICATION_RESIDENCE)) {

            boolean visaRequired = personalDetailNull ? false : personalDetail.getVisaRequired();

            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_VISA_REQUIRED_LABEL),
                    propertyLoader.load(SYSTEM_YES, SYSTEM_NO, visaRequired), body);

            passportAvailable = personalDetailNull ? false : personalDetail.getPassportAvailable();

            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_PASSPORT_AVAILABLE_LABEL),
                    propertyLoader.load(SYSTEM_YES, SYSTEM_NO, passportAvailable), body);
        }
        return passportAvailable;
    }

    private boolean addLanguageQualificationHeader(Application application, ApplicationPersonalDetail personalDetail, boolean personalDetailNull, PdfPTable body)
            throws Exception {
        boolean languageQualificationAvailable = false;
        if (customizationService
                .isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application, PrismWorkflowPropertyDefinition.APPLICATION_LANGUAGE)) {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_FIRST_LANGUAGE_LOCALE_LABEL),
                    personalDetailNull ? null : propertyLoader.load(SYSTEM_YES, SYSTEM_NO, personalDetail.getFirstLanguageLocale()), body);

            languageQualificationAvailable = personalDetailNull ? false : personalDetail.getLanguageQualificationAvailable();

            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_LANGUAGE_QUALIFICATION_AVAILABLE_LABEL),
                    personalDetailNull ? null : propertyLoader.load(SYSTEM_YES, SYSTEM_NO, languageQualificationAvailable), body);
        }
        return languageQualificationAvailable;
    }

    private void addPassportSection(ApplicationPassport passport, Document pdfDocument) throws Exception {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_PASSPORT_HEADER));

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PASSPORT_NUMBER_LABEL), passport.getNumber(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PASSPORT_NAME_LABEL), passport.getName(), body);

        String dateFormat = propertyLoader.load(SYSTEM_DATE_FORMAT);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PASSPORT_ISSUE_DATE_LABEL),
                passport.getIssueDateDisplay(dateFormat),
                body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PASSPORT_EXPIRY_DATE_LABEL),
                passport.getExipryDateDisplay(dateFormat),
                body);

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
    }

    private void addLanguageQualificationSection(Application application, ApplicationLanguageQualification languageQualification,
            ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws Exception {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_LANGUAGE_QUALIFICATION_HEADER));

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_LANGUAGE_QUALIFICATION_TYPE_LABEL),
                languageQualification.getTypeDisplay(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_LANGUAGE_QUALIFICATION_EXAM_DATE_LABEL),
                languageQualification.getExamDateDisplay(propertyLoader.load(SYSTEM_DATE_FORMAT)), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_LANGUAGE_QUALIFICATION_OVERALL_SCORE_LABEL),
                languageQualification.getOverallScore(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_LANGUAGE_QUALIFICATION_READING_SCORE_LABEL),
                languageQualification.getReadingScore(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_LANGUAGE_QUALIFICATION_WRITING_SCORE_LABEL),
                languageQualification.getWritingScore(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_LANGUAGE_QUALIFICATION_SPEAKING_SCORE_LABEL),
                languageQualification.getSpeakingScore(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_LANGUAGE_QUALIFICATION_LISTENING_SCORE_LABEL),
                languageQualification.getListeningScore(), body);

        if (customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application,
                PrismWorkflowPropertyDefinition.APPLICATION_LANGUAGE_PROOF_OF_AWARD)) {
            addBookmark(body, propertyLoader.load(APPLICATION_PROOF_OF_AWARD), languageQualification.getDocument(), applicationDownloadDTO);
        }

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
    }

    private void addAddressSection(Application application, Document pdfDocument) throws Exception {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument,
                propertyLoader.load(PrismDisplayPropertyDefinition.APPLICATION_ADDRESS_HEADER));
        ApplicationAddress address = application.getAddress();

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_ADDRESS_CURRENT_HEADER),
                address == null ? null : address.getCurrentAddressDisplay(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_ADDRESS_CONTACT_HEADER),
                address == null ? null : address.getConcactAddressDisplay(), body);

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
    }

    private void addQualificationSection(Application application, ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws Exception {
        if (customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application,
                PrismWorkflowPropertyDefinition.APPLICATION_QUALIFICATION)) {
            PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_QUALIFICATION_HEADER));
            Set<ApplicationQualification> qualifications = application.getQualifications();

            if (qualifications.isEmpty()) {
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_SUBHEADER), null, body);
                applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
            } else {
                boolean documentEnabled = customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application,
                        PrismWorkflowPropertyDefinition.APPLICATION_QUALIFICATION_PROOF_OF_AWARD);

                int counter = 1;
                for (ApplicationQualification qualification : qualifications) {
                    PdfPTable subBody = applicationDownloadBuilderHelper.startSubSection(propertyLoader.load(APPLICATION_QUALIFICATION_SUBHEADER) + "("
                            + counter++ + ")");

                    String dateFormat = propertyLoader.load(SYSTEM_DATE_FORMAT);

                    ImportedProgram program = qualification.getProgram();
                    ImportedInstitution institution = program.getInstitution();

                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_DOMICILE_LABEL),
                            institution.getDomicileDisplay(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_PROVIDER_LABEL), institution.getName(),
                            subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_TYPE_LABEL),
                            qualification.getQualificationTypeDisplay(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_TITLE_LABEL),
                            program.getQualification(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_SUBJECT_LABEL),
                            program.getName(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_LANGUAGE_LABEL),
                            qualification.getLanguage(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_START_DATE_LABEL),
                            qualification.getStartDateDisplay(dateFormat), subBody);

                    boolean completed = BooleanUtils.isTrue(qualification.getCompleted());

                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_COMPLETED_LABEL),
                            propertyLoader.load(SYSTEM_YES, SYSTEM_NO, completed), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(
                            propertyLoader.load(APPLICATION_QUALIFICATION_CONFIRMED_RESULT_LABEL, APPLICATION_QUALIFICATION_EXPECTED_RESULT_LABEL, completed),
                            qualification.getGrade(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(
                            propertyLoader.load(APPLICATION_QUALIFICATION_CONFIRMED_AWARD_DATE_LABEL, APPLICATION_QUALIFICATION_EXPECTED_AWARD_DATE_LABEL,
                                    completed),
                            qualification.getAwardDateDisplay(dateFormat), subBody);

                    if (documentEnabled) {
                        addBookmark(subBody, propertyLoader.load(APPLICATION_QUALIFICATION_DOCUMENT_LABEL), qualification.getDocument(),
                                applicationDownloadDTO);
                    }

                    applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
                }
            }
        }
    }

    private void addEmploymentSection(Application application, Document pdfDocument) throws Exception {
        if (customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application,
                PrismWorkflowPropertyDefinition.APPLICATION_EMPLOYMENT_POSITION)) {
            PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_HEADER));
            Set<ApplicationEmploymentPosition> positions = application.getEmploymentPositions();

            if (positions.isEmpty()) {
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_SUBHEADER), null, body);
                applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
            } else {
                int counter = 1;
                for (ApplicationEmploymentPosition position : positions) {
                    PdfPTable subBody = applicationDownloadBuilderHelper.startSubSection(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_SUBHEADER) + "("
                            + counter++ + ")");

                    String dateFormat = propertyLoader.load(SYSTEM_DATE_FORMAT);

                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_EMPLOYER_NAME_LABEL),
                            position.getEmployerName(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_EMPLOYER_ADDRESS_LABEL),
                            position.getEmployerAddressLocation(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_TITLE_LABEL),
                            position.getPosition(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_REMIT_LABEL), position.getRemit(),
                            subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_START_DATE_LABEL),
                            position.getStartDateDisplay(dateFormat),
                            subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_CURRENT_LABEL),
                            propertyLoader.load(SYSTEM_YES, SYSTEM_NO, position.getCurrent()), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_END_DATE_LABEL),
                            position.getEndDateDisplay(dateFormat),
                            subBody);

                    applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
                }
            }
        }
    }

    private void addFundingSection(Application application, ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws Exception {
        if (customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application, PrismWorkflowPropertyDefinition.APPLICATION_FUNDING)) {
            PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_FUNDING_HEADER));
            Set<ApplicationFunding> fundings = application.getFundings();

            if (fundings.isEmpty()) {
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_FUNDING_SUBHEADER), null, body);
                applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
            } else {
                boolean documentEnabled = customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application,
                        PrismWorkflowPropertyDefinition.APPLICATION_FUNDING_PROOF_OF_AWARD);

                int counter = 1;
                for (ApplicationFunding funding : fundings) {
                    PdfPTable subBody = applicationDownloadBuilderHelper.startSubSection(propertyLoader.load(APPLICATION_FUNDING_SUBHEADER) + "(" + counter++
                            + ")");
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_FUNDING_TYPE_LABEL),
                            funding.getFundingSourceDisplay(),
                            subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_FUNDING_SPONSOR_LABEL), funding.getSponsor(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_FUNDING_DESCRIPTION_LABEL), funding.getDescription(),
                            subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_FUNDING_VALUE_LABEL), funding.getValue(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_FUNDING_AWARD_DATE_LABEL),
                            funding.getAwardDateDisplay(propertyLoader.load(SYSTEM_DATE_FORMAT)), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_FUNDING_TERMS_LABEL), funding.getTerms(), subBody);

                    if (documentEnabled) {
                        addBookmark(subBody, propertyLoader.load(APPLICATION_PROOF_OF_AWARD), funding.getDocument(), applicationDownloadDTO);
                    }

                    applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
                }
            }
        }
    }

    private void addPrizesSection(Application application, Document pdfDocument) throws Exception {
        if (customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application, PrismWorkflowPropertyDefinition.APPLICATION_PRIZE)) {
            PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_PRIZE_HEADER));
            Set<ApplicationPrize> prizes = application.getPrizes();

            if (prizes.isEmpty()) {
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PRIZE_SUBHEADER), null, body);
                applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
            } else {
                int counter = 1;
                for (ApplicationPrize prize : prizes) {
                    PdfPTable subBody = applicationDownloadBuilderHelper.startSubSection(propertyLoader.load(APPLICATION_PRIZE_SUBHEADER) + "(" + counter++
                            + ")");
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PRIZE_PROVIDER_LABEL), prize.getProvider(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PRIZE_TITLE_LABEL), prize.getTitle(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PRIZE_DESCRIPTION_LABEL), prize.getDescription(),
                            subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PRIZE_AWARD_DATE_LABEL),
                            prize.getAwardDateDisplay(propertyLoader.load(SYSTEM_DATE_FORMAT)), subBody);

                    applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
                }
            }
        }
    }

    private void addReferencesSection(ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws Exception {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_REFEREE_HEADER));
        Set<ApplicationReferee> referees = applicationDownloadDTO.getApplication().getReferees();

        if (referees.isEmpty()) {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_REFEREE_SUBHEADER), null, body);
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        } else {
            int counter = 1;
            for (ApplicationReferee referee : referees) {
                PdfPTable subBody = applicationDownloadBuilderHelper
                        .startSubSection(propertyLoader.load(APPLICATION_REFEREE_SUBHEADER) + "(" + counter++ + ")");

                User user = referee.getUser();
                boolean userNull = user == null;

                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_FIRST_NAME), userNull ? null : user.getFirstName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_HTML_GENERAL_FIELD_LAST_NAME_LABEL),
                        userNull ? null : user.getLastName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_EMAIL), userNull ? null : user.getEmail(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_REFEREE_EMPLOYER_ADDRESS_LABEL),
                        referee.getAddressDisplay(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_TELEPHONE_LABEL), referee.getPhone(),
                        subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_SKYPE_LABEL), referee.getSkype(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_EMPLOYER_NAME_LABEL),
                        referee.getJobEmployer(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_REFEREE_POSITION_LABEL), referee.getJobTitle(), subBody);

                Comment referenceComment = referee.getComment();
                if (referenceComment != null) {
                    ApplicationDownloadMode downloadMode = applicationDownloadDTO.getDownloadMode();
                    boolean includeReferences = applicationDownloadDTO.isIncludeAssessments();
                    if ((downloadMode == ApplicationDownloadMode.SYSTEM && includeReferences)
                            || (downloadMode == ApplicationDownloadMode.USER && (includeReferences || commentService.isCommentOwner(referenceComment,
                                    userService.getCurrentUser())))) {
                        addBookmark(subBody, propertyLoader.load(APPLICATION_REFEREE_REFERENCE_APPENDIX), referee.getComment(), applicationDownloadDTO);
                    }
                }

                applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
            }
        }
    }

    private void addDocumentSection(Application application, ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws Exception {
        boolean personalStatementEnabled = customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application,
                PrismWorkflowPropertyDefinition.APPLICATION_DOCUMENT_PERSONAL_STATEMENT);
        boolean cvEnabled = customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application,
                PrismWorkflowPropertyDefinition.APPLICATION_DOCUMENT_CV);
        boolean researchStatementEnabled = customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application,
                PrismWorkflowPropertyDefinition.APPLICATION_DOCUMENT_RESEARCH_STATEMENT);
        boolean coveringLetterEnabled = customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application,
                PrismWorkflowPropertyDefinition.APPLICATION_DOCUMENT_COVERING_LETTER);
        if (personalStatementEnabled || cvEnabled || researchStatementEnabled || coveringLetterEnabled) {
            PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_DOCUMENT_HEADER));
            ApplicationDocument documentSection = applicationDownloadDTO.getApplication().getDocument();

            if (personalStatementEnabled) {
                addBookmark(body, propertyLoader.load(APPLICATION_DOCUMENT_PERSONAL_STATEMENT_LABEL),
                        documentSection == null ? null : documentSection.getPersonalStatement(), applicationDownloadDTO);
            }

            if (cvEnabled) {
                addBookmark(body, propertyLoader.load(APPLICATION_DOCUMENT_CV_LABEL),
                        documentSection == null ? null : documentSection.getCv(), applicationDownloadDTO);
            }

            if (researchStatementEnabled) {
                addBookmark(body, propertyLoader.load(APPLICATION_DOCUMENT_RESEARCH_STATEMENT_LABEL),
                        documentSection == null ? null : documentSection.getResearchStatement(), applicationDownloadDTO);
            }

            if (coveringLetterEnabled) {
                addBookmark(body, propertyLoader.load(APPLICATION_DOCUMENT_COVERING_LETTER_LABEL),
                        documentSection == null ? null : documentSection.getCoveringLetter(), applicationDownloadDTO);
            }

            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        }
    }

    private void addAdditionalInformationSection(Application application, ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument)
            throws Exception {
        if (customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application,
                PrismWorkflowPropertyDefinition.APPLICATION_CRIMINAL_CONVICTION) && applicationDownloadDTO.isIncludeEqualOpportunities()) {
            PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_ADDITIONAL_INFORMATION_HEADER));
            ApplicationAdditionalInformation additionalInformation = application.getAdditionalInformation();

            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_ADDITIONAL_INFORMATION_CONVICTION_LABEL),
                    additionalInformation == null ? null : additionalInformation.getConvictionsText(), body);

            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        }
    }

    private void addSupportingDocuments(ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument, PdfWriter pdfWriter) throws Exception {
        int index = 0;
        for (Map.Entry<String, Object> bookmark : bookmarks.entrySet()) {
            pdfDocument.newPage();

            NewPageEvent pageEvent = (NewPageEvent) pdfWriter.getPageEvent();
            pageEvent.setApplyHeaderFooter(true);

            String bookmarkKey = bookmark.getKey();
            Anchor anchor = new Anchor();
            anchor.setName(bookmarkKey);

            Object content = bookmark.getValue();
            pdfDocument.add(new Chunk(propertyLoader.load(SYSTEM_APPENDIX) + "(" + (index + 1) + ")").setLocalDestination(bookmarkKey));
            if (content instanceof com.zuehlke.pgadmissions.domain.document.Document) {
                com.zuehlke.pgadmissions.domain.document.Document document = (com.zuehlke.pgadmissions.domain.document.Document) content;

                if (document.getApplicationLanguageQualification() != null) {
                    pdfDocument.add(buildTarget(APPLICATION_LANGUAGE_QUALIFICATION_APPENDIX, anchor));
                } else if (document.getApplicationQualification() != null) {
                    pdfDocument.add(buildTarget(APPLICATION_QUALIFICATION_APPENDIX, anchor));
                } else if (document.getApplicationFunding() != null) {
                    pdfDocument.add(buildTarget(APPLICATION_FUNDING_DOCUMENT_LABEL, anchor));
                } else if (document.getApplicationPersonalStatement() != null) {
                    pdfDocument.add(buildTarget(APPLICATION_DOCUMENT_PERSONAL_STATEMENT_LABEL, anchor));
                } else if (document.getApplicationCv() != null) {
                    pdfDocument.add(buildTarget(APPLICATION_DOCUMENT_CV_LABEL, anchor));
                } else if (document.getApplicationResearchStatement() != null) {
                    pdfDocument.add(buildTarget(APPLICATION_DOCUMENT_RESEARCH_STATEMENT_LABEL, anchor));
                } else if (document.getApplicationCoveringLetter() != null) {
                    pdfDocument.add(buildTarget(APPLICATION_DOCUMENT_COVERING_LETTER_LABEL, anchor));
                }

                addDocument(pdfDocument, document, pdfWriter);
            } else if (content instanceof Comment) {
                Comment referenceComment = (Comment) content;
                pdfDocument.add(buildTarget(APPLICATION_REFEREE_REFERENCE_APPENDIX, anchor));

                pdfDocument.add(applicationDownloadBuilderHelper.newSectionSeparator());

                PdfPTable subBody = applicationDownloadBuilderHelper.startSubSection(propertyLoader.load(APPLICATION_REFEREE_REFERENCE_COMMENT));
                applicationContext.getBean(ApplicationDownloadReferenceBuilder.class).localize(propertyLoader, applicationDownloadBuilderHelper)
                        .addReferenceComment(pdfDocument, subBody, pdfWriter, applicationDownloadDTO.getApplication(), referenceComment);

                for (com.zuehlke.pgadmissions.domain.document.Document document : referenceComment.getDocuments()) {
                    addDocument(pdfDocument, document, pdfWriter);
                }
            }

            index++;
        }
    }

    private Phrase buildTarget(PrismDisplayPropertyDefinition title, Anchor anchor) throws Exception {
        Phrase phrase = new Phrase(" - " + propertyLoader.load(title));
        phrase.add(anchor);
        return phrase;
    }

    private void addDocument(Document pdfDocument, com.zuehlke.pgadmissions.domain.document.Document document, PdfWriter pdfWriter) throws IOException,
            IntegrationException {
        PdfReader pdfReader = new PdfReader(documentService.getDocumentContent(document));
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

    private void addApplicationSummary(Application application, PdfPTable table, ApplicationDownloadBuilderFontSize fontSize) throws Exception {
        applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(SYSTEM_INSTITUTION), application.getInstitutionDisplay(), fontSize, table);

        Department department = application.getDepartment();
        if (department != null) {
            applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(SYSTEM_DEPARTMENT), application.getDepartmentDisplay(), fontSize, table);
        }

        applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(SYSTEM_PROGRAM), application.getProgramDisplay(), fontSize, table);

        Project project = application.getProject();
        if (project != null) {
            applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(SYSTEM_PROJECT), application.getProjectDisplay(), fontSize, table);
        }

        applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(APPLICATION_PREVIOUS_APPLICATION),
                propertyLoader.load(SYSTEM_YES, SYSTEM_NO, BooleanUtils.toBoolean(application.getPreviousApplication())), fontSize, table);

        ApplicationProgramDetail programDetail = application.getProgramDetail();
        PrismStudyOption studyOption = programDetail == null ? null : programDetail.getStudyOptionDisplay();
        applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(APPLICATION_PROGRAM_DETAIL_STUDY_OPTION_LABEL),
                studyOption == null ? null : propertyLoader.load(studyOption.getDisplayProperty()), fontSize, table);
    }

    private void addApplicationSummaryExtended(Application application, PdfPTable table, ApplicationDownloadBuilderFontSize fontSize) throws Exception {
        addApplicationSummary(application, table, fontSize);

        String dateFormat = propertyLoader.load(SYSTEM_DATE_FORMAT);

        applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(APPLICATION_CODE), application.getCode(), fontSize, table);
        applicationDownloadBuilderHelper
                .addContentRow(propertyLoader.load(SYSTEM_CLOSING_DATE), application.getClosingDateDisplay(dateFormat), fontSize, table);
        applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(APPLICATION_SUBMISSION_DATE), application.getSubmittedTimestampDisplay(dateFormat),
                fontSize, table);
    }

    private void addBookmark(PdfPTable table, String rowTitle, Object content, ApplicationDownloadDTO applicationDownloadDTO) throws Exception {
        table.addCell(applicationDownloadBuilderHelper.newTitleCellMedium(rowTitle));
        ApplicationDownloadMode downloadMode = applicationDownloadDTO.getDownloadMode();
        boolean includeAttachments = applicationDownloadDTO.isIncludeAttachments();
        if ((downloadMode == ApplicationDownloadMode.SYSTEM && includeAttachments)
                || (downloadMode == ApplicationDownloadMode.USER && (includeAttachments || userService.isCurrentUser((User) PrismReflectionUtils.getProperty(
                        content, "user"))))) {
            if (content == null) {
                table.addCell(applicationDownloadBuilderHelper.newContentCellMedium(null));
            } else {
                int index = bookmarks.size();
                String anchor = applicationDownloadDTO.getApplication().getCode() + "-" + index;
                table.addCell(applicationDownloadBuilderHelper.newBookmarkCellMedium(
                        propertyLoader.load(SYSTEM_SEE) + " " + propertyLoader.load(SYSTEM_APPENDIX) + " (" + (index + 1) + ")", anchor));
                bookmarks.put(anchor, content);
            }
        } else {
            table.addCell(applicationDownloadBuilderHelper.newContentCellMedium(content == null ? null : propertyLoader.load(SYSTEM_VALUE_PROVIDED)));
        }
    }

    private void addLogoImage(Document pdfDocument) throws IOException, DocumentException {
        Image logoImage = applicationDownloadBuilderHelper.newLogoImage();
        logoImage.setAbsolutePosition(pdfDocument.right() - logoImage.getScaledWidth(), pdfDocument.top() + 20f);
        pdfDocument.add(logoImage);
    }

    private class NewPageEvent extends PdfPageEventForwarder {

        private Application application;

        private boolean applyHeaderFooter = true;

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            if (applyHeaderFooter) {
                try {
                    addHeader(writer, document);
                    addFooter(writer, document);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private void addHeader(PdfWriter writer, Document pdfDocument) throws Exception {
            float pageWidth = pdfDocument.getPageSize().getWidth();
            PdfPTable body = new PdfPTable(1);
            body.setTotalWidth(pageWidth * 0.5f);

            PdfPCell content = applicationDownloadBuilderHelper.newContentCell(application.getUser().getFullName() + " (" + application.getCode() + ")",
                    ApplicationDownloadBuilderFontSize.SMALL);
            content.setPadding(0f);
            content.setBorderWidth(0f);
            body.addCell(content);

            body.writeSelectedRows(0, 1, pdfDocument.left(), pdfDocument.top() + 27f, writer.getDirectContent());
            addLogoImage(pdfDocument);

            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.drawLine(writer.getDirectContent(), pdfDocument.left(), pdfDocument.right(), pdfDocument.top() + 10f);
        }

        private void addFooter(PdfWriter writer, Document document) throws Exception {
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.drawLine(writer.getDirectContent(), document.left(), document.right(), document.bottom() - 15f);
            Phrase footerPhrase = new Phrase(propertyLoader.load(SYSTEM_PAGE) + " " + (1 + document.getPageNumber()),
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
