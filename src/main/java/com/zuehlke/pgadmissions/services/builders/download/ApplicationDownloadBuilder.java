package com.zuehlke.pgadmissions.services.builders.download;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_THEME_SECONDARY;
import static com.zuehlke.pgadmissions.services.builders.download.ApplicationDownloadBuilderConfiguration.ApplicationDownloadBuilderFontSize.MEDIUM;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
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
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
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
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ApplicationDownloadDTO;
import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.CustomizationService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.builders.download.ApplicationDownloadBuilderConfiguration.ApplicationDownloadBuilderFontSize;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ApplicationDownloadBuilder {

    private PropertyLoader propertyLoader;

    private final List<Object> bookmarks = Lists.newLinkedList();

    private ApplicationDownloadBuilderHelper applicationDownloadBuilderHelper;

    @Value("${xml.export.logo.file.width.percentage}")
    private Float logoFileWidthPercentage;

    @Autowired
    private ActionService actionService;

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
            addCoverPage(application, pdfDocument, writer);
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

    private void addCoverPage(Application application, Document pdfDocument, PdfWriter writer) throws IOException, DocumentException {
        pdfDocument.newPage();
        addLogoImage(pdfDocument);

        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.drawLine(writer.getDirectContent(), pdfDocument.left(), pdfDocument.right(), pdfDocument.top() + 10f);

        pdfDocument.add(applicationDownloadBuilderHelper.newSectionSeparator());
        pdfDocument.add(applicationDownloadBuilderHelper.newSectionSeparator());

        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_HEADER));

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_CREATOR), application.getUser().getFullName(), body);

        if (!actionService.hasRedactions(application, userService.getCurrentUser())) {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_AVERAGE_RATING), application.getApplicationRatingAverageDisplay(),
                    body);
        }

        addApplicationSummaryExtended(application, body, MEDIUM);
        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        pdfDocument.newPage();
    }

    private void addProgramSection(Application application, Document pdfDocument) throws DocumentException {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_PROGRAM_DETAIL_HEADER));
        addApplicationSummary(application, body, MEDIUM);

        ApplicationProgramDetail programDetail = application.getProgramDetail();
        boolean programDetailNull = programDetail == null;

        String dateFormat = propertyLoader.load(SYSTEM_DATE_FORMAT);
        String startDate = programDetailNull ? null : programDetail.getStartDateDisplay(dateFormat);
        String confirmedStartDate = programDetailNull ? null : application.getConfirmedStartDateDisplay(dateFormat);

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_START_DATE, APPLICATION_CONFIRMED_START_DATE,
                confirmedStartDate == null), confirmedStartDate == null ? startDate : confirmedStartDate, body);

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_REFERRAL_SOURCE),
                programDetailNull ? null : programDetail.getReferralSourceDisplay(), body);

        addStudyDetailSection(application, body);
        addThemeSection(application, body);

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
    }

    private void addStudyDetailSection(Application application, PdfPTable body) {
        if (customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application,
                PrismWorkflowPropertyDefinition.APPLICATION_STUDY_DETAIL)) {
            ApplicationStudyDetail studyDetail = application.getStudyDetail();
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_STUDY_LOCATION),
                    studyDetail == null ? null : studyDetail.getStudyLocation(), body);
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_STUDY_DIVISION),
                    studyDetail == null ? null : studyDetail.getStudyDivision(), body);
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_STUDY_AREA),
                    studyDetail == null ? null : studyDetail.getStudyArea(), body);
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_STUDY_APPLICATION_ID), studyDetail == null ? null
                    : studyDetail.getStudyApplicationId(), body);
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_STUDY_START_DATE),
                    studyDetail == null ? null : studyDetail.getStudyStartDateDisplay(propertyLoader.load(SYSTEM_DATE_FORMAT)), body);
        }
    }

    private void addThemeSection(Application application, PdfPTable body) {
        if (customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application,
                PrismWorkflowPropertyDefinition.APPLICATION_THEME_PRIMARY)) {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PRIMARY_THEME), application.getPrimaryThemeDisplay(), body);
        }

        if (customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application, APPLICATION_THEME_SECONDARY)) {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(PrismDisplayPropertyDefinition.APPLICATION_SECONDARY_THEME),
                    application.getSecondaryThemeDisplay(), body);
        }
    }

    private void addSupervisorSection(Application application, Document pdfDocument) throws DocumentException {
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
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_LAST_NAME), user.getLastName(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_EMAIL), user.getEmail(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_SUPERVISOR_AWARE_OF_APPLICATION),
                            propertyLoader.load(SYSTEM_YES, SYSTEM_NO, supervisor.getAcceptedSupervision()), subBody);

                    applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
                }
            }
        }
    }

    private void addPersonalDetailSection(Application application, ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument)
            throws DocumentException {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_PERSONAL_DETAIL_HEADER));

        ApplicationPersonalDetail personalDetail = application.getPersonalDetail();
        boolean personalDetailNull = personalDetail == null;

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_TITLE), personalDetailNull ? null : personalDetail.getTitleDisplay(),
                body);

        User applicationCreator = application.getUser();

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_FIRST_NAME), applicationCreator.getFirstName(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_FIRST_NAME_2), applicationCreator.getFirstName2(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_FIRST_NAME_3), applicationCreator.getFirstName3(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_LAST_NAME), applicationCreator.getLastName(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_EMAIL), applicationCreator.getEmail(), body);

        applicationDownloadBuilderHelper
                .addContentRowMedium(propertyLoader.load(SYSTEM_TELEPHONE), personalDetailNull ? null : personalDetail.getPhone(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_SKYPE), personalDetailNull ? null : personalDetail.getSkype(), body);

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_GENDER), personalDetailNull ? null
                : personalDetail.getGenderDisplay(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(PrismDisplayPropertyDefinition.APPLICATION_PERSONAL_DETAIL_DATE_OF_BIRTH),
                personalDetailNull ? null : personalDetail.getDateOfBirth(propertyLoader.load(SYSTEM_DATE_FORMAT)), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_COUNTRY_OF_BIRTH), personalDetailNull ? null
                : personalDetail.getCountryDisplay(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_COUNTRY_OF_DOMICILE), personalDetailNull ? null
                : personalDetail.getDomicileDisplay(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_NATIONALITY), personalDetailNull ? null
                : personalDetail.getNationalityDisplay(), body);

        appendEqualOpportunitiesSection(applicationDownloadDTO, body, application, personalDetail, personalDetailNull);

        boolean passportAvailable = addPassportHeader(application, personalDetail, personalDetailNull, body);
        boolean languageQualificationAvailable = addLanguageQualificationHeader(application, personalDetail, personalDetailNull, body);

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);

        if (passportAvailable) {
            addPassportSection(personalDetail.getPassport(), pdfDocument);
        }

        if (languageQualificationAvailable) {
            addLanquageQualificationSection(application, personalDetail.getLanguageQualification(), applicationDownloadDTO, pdfDocument);
        }
    }

    private void appendEqualOpportunitiesSection(ApplicationDownloadDTO applicationDownloadDTO, PdfPTable body, Application application,
            ApplicationPersonalDetail personalDetail, boolean personalDetailNull) {
        if (applicationDownloadDTO.isIncludeEqualOpportunities()) {
            if (customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application,
                    PrismWorkflowPropertyDefinition.APPLICATION_DEMOGRAPHIC)) {
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_ETHNICITY), personalDetailNull ? null
                        : personalDetail.getEthnicityDisplay(), body);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_DISABILITY), personalDetailNull ? null
                        : personalDetail.getDisabilityDisplay(), body);
            }
        }
    }

    private boolean addPassportHeader(Application application, ApplicationPersonalDetail personalDetail, boolean personalDetailNull, PdfPTable body) {
        boolean passportAvailable = false;
        if (customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application,
                PrismWorkflowPropertyDefinition.APPLICATION_RESIDENCE)) {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_REQUIRE_VISA),
                    propertyLoader.load(SYSTEM_YES, SYSTEM_NO, personalDetail.getVisaRequired()), body);

            passportAvailable = personalDetailNull ? false : personalDetail.getPassportAvailable();

            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_PASSPORT_AVAILABLE),
                    propertyLoader.load(SYSTEM_YES, SYSTEM_NO, passportAvailable), body);
        }
        return passportAvailable;
    }

    private boolean addLanguageQualificationHeader(Application application, ApplicationPersonalDetail personalDetail, boolean personalDetailNull, PdfPTable body) {
        boolean languageQualificationAvailable = false;
        if (customizationService
                .isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application, PrismWorkflowPropertyDefinition.APPLICATION_LANGUAGE)) {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_WORK_LANGUAGE_FIRST_LANGUAGE),
                    personalDetailNull ? null : propertyLoader.load(SYSTEM_YES, SYSTEM_NO, personalDetail.getFirstLanguageLocale()), body);

            languageQualificationAvailable = personalDetailNull ? false : personalDetail.getLanguageQualificationAvailable();

            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_LANGUAGE_QUALIFICATION_AVAILABLE),
                    personalDetailNull ? null : propertyLoader.load(SYSTEM_YES, SYSTEM_NO, languageQualificationAvailable), body);
        }
        return languageQualificationAvailable;
    }

    private void addPassportSection(ApplicationPassport passport, Document pdfDocument) throws DocumentException {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_PASSPORT_HEADER));

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PASSPORT_NUMBER), passport.getNumber(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PASSPORT_NAME), passport.getName(), body);

        String dateFormat = propertyLoader.load(SYSTEM_DATE_FORMAT);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PASSPORT_ISSUE_DATE), passport.getIssueDateDisplay(dateFormat),
                body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PASSPORT_EXPIRY_DATE), passport.getExipryDateDisplay(dateFormat),
                body);

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
    }

    private void addLanquageQualificationSection(Application application, ApplicationLanguageQualification languageQualification,
            ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws DocumentException {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_LANGUAGE_QUALIFICATION_HEADER));

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_TYPE), languageQualification.getTypeDisplay(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_LANGUAGE_QUALIFICATION_EXAM_DATE),
                languageQualification.getExamDateDisplay(propertyLoader.load(SYSTEM_DATE_FORMAT)), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_LANGUAGE_QUALIFICATION_OVERALL_SCORE),
                languageQualification.getOverallScore(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_LANGUAGE_QUALIFICATION_READING_SCORE),
                languageQualification.getReadingScore(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_LANGUAGE_QUALIFICATION_WRITING_SCORE),
                languageQualification.getWritingScore(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_LANGUAGE_QUALIFICATION_SPEAKING_SCORE),
                languageQualification.getSpeakingScore(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_LANGUAGE_QUALIFICATION_LISTENING_SCORE),
                languageQualification.getListeningScore(), body);

        if (customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application,
                PrismWorkflowPropertyDefinition.APPLICATION_LANGUAGE_PROOF_OF_AWARD)) {
            addBookmark(body, propertyLoader.load(APPLICATION_PROOF_OF_AWARD), languageQualification.getDocument(), applicationDownloadDTO);
        }

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
    }

    private void addAddressSection(Application application, Document pdfDocument) throws DocumentException {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument,
                propertyLoader.load(PrismDisplayPropertyDefinition.APPLICATION_ADDRESS_HEADER));
        ApplicationAddress address = application.getAddress();

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_ADDRESS_CURRENT),
                address == null ? null : address.getCurrentAddressDisplay(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_ADDRESS_CONTACT),
                address == null ? null : address.getConcactAddressDisplay(), body);

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
    }

    private void addQualificationSection(Application application, ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws DocumentException {
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

                    ImportedInstitution institution = qualification.getInstitution();
                    boolean institutionNull = institution == null;

                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_COUNTRY), institutionNull ? null
                            : institution.getDomicileDisplay(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_PROVIDER), institutionNull ? null
                            : institution.getName(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_TYPE), qualification.getTypeDisplay(),
                            subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_TITLE), qualification.getTitle(),
                            subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_SUBJECT), qualification.getSubject(),
                            subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_LANGUAGE), qualification.getLanguage(),
                            subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_START_DATE),
                            qualification.getStartDateDisplay(dateFormat), subBody);

                    boolean completed = BooleanUtils.isTrue(qualification.getCompleted());

                    applicationDownloadBuilderHelper.addContentRowMedium("Has this Qualification been awarded",
                            propertyLoader.load(SYSTEM_YES, SYSTEM_NO, completed), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(
                            propertyLoader.load(APPLICATION_QUALIFICATION_CONFIRMED_RESULT, APPLICATION_QUALIFICATION_EXPECTED_RESULT, completed),
                            qualification.getGrade(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(
                            propertyLoader.load(APPLICATION_QUALIFICATION_CONFIRMED_AWARD_DATE, APPLICATION_QUALIFICATION_EXPECTED_AWARD_DATE, completed),
                            qualification.getAwardDateDisplay(dateFormat), subBody);

                    if (documentEnabled) {
                        addBookmark(subBody, propertyLoader.load(APPLICATION_QUALIFICATION_FINAL_TRANSCRIPT,
                                PrismDisplayPropertyDefinition.APPLICATION_QUALIFICATION_INTERIM_TRANSCRIPT, completed), qualification.getDocument(),
                                applicationDownloadDTO);
                    }

                    applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
                }
            }
        }
    }

    private void addEmploymentSection(Application application, Document pdfDocument) throws DocumentException {
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

                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYER_NAME), position.getEmployerName(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_EMPLOYER_ADDRESS),
                            position.getEmployerAddressLocation(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_POSITION_TITLE), position.getPosition(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_REMIT), position.getRemit(),
                            subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_START_DATE), position.getStartDateDisplay(dateFormat),
                            subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_IS_CURRENT),
                            propertyLoader.load(SYSTEM_YES, SYSTEM_NO, position.getCurrent()), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_END_DATE), position.getEndDateDisplay(dateFormat),
                            subBody);

                    applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
                }
            }
        }
    }

    private void addFundingSection(Application application, ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws DocumentException {
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
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_FUNDING_TYPE), funding.getFundingSourceDisplay(),
                            subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_FUNDING_SPONSOR), funding.getSponsor(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_FUNDING_DESCRIPTION), funding.getDescription(),
                            subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_FUNDING_VALUE), funding.getValue(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_FUNDING_AWARD_DATE),
                            funding.getAwardDateDisplay(propertyLoader.load(SYSTEM_DATE_FORMAT)), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_FUNDING_TERMS), funding.getTerms(), subBody);

                    if (documentEnabled) {
                        addBookmark(subBody, propertyLoader.load(APPLICATION_PROOF_OF_AWARD), funding.getDocument(), applicationDownloadDTO);
                    }

                    applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
                }
            }
        }
    }

    private void addPrizesSection(Application application, Document pdfDocument) throws DocumentException {
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
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PRIZE_PROVIDER), prize.getProvider(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PRIZE_TITLE), prize.getTitle(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PRIZE_DESCRIPTION), prize.getDescription(), subBody);
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PRIZE_AWARD_DATE),
                            prize.getAwardDateDisplay(propertyLoader.load(SYSTEM_DATE_FORMAT)), subBody);

                    applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
                }
            }
        }
    }

    private void addReferencesSection(ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws DocumentException {
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
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_LAST_NAME), userNull ? null : user.getLastName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_EMAIL), userNull ? null : user.getEmail(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYER_ADDRESS), referee.getAddressDisplay(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_TELEPHONE), referee.getPhone(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_SKYPE), referee.getSkype(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYER_NAME), referee.getJobEmployer(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_POSITION_TITLE), referee.getJobTitle(), subBody);

                ApplicationDownloadMode downloadMode = applicationDownloadDTO.getDownloadMode();
                boolean includeReferences = applicationDownloadDTO.isIncludeReferences();
                if ((downloadMode == ApplicationDownloadMode.SYSTEM && includeReferences)
                        || (downloadMode == ApplicationDownloadMode.USER && (includeReferences || userService.isCurrentUser(user)))) {
                    addBookmark(subBody, propertyLoader.load(APPLICATION_REFEREE_REFERENCE_APPENDIX), referee.getComment(), applicationDownloadDTO);
                }

                applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
            }
        }
    }

    private void addDocumentSection(Application application, ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws DocumentException {
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
                addBookmark(body, propertyLoader.load(APPLICATION_DOCUMENT_PERSONAL_STATEMENT_APPENDIX),
                        documentSection == null ? null : documentSection.getPersonalStatement(), applicationDownloadDTO);
            }

            if (cvEnabled) {
                addBookmark(body, propertyLoader.load(APPLICATION_DOCUMENT_CV_APPENDIX),
                        documentSection == null ? null : documentSection.getPersonalStatement(), applicationDownloadDTO);
            }

            if (researchStatementEnabled) {
                addBookmark(body, propertyLoader.load(APPLICATION_DOCUMENT_RESEARCH_STATEMENT_APPENDIX),
                        documentSection == null ? null : documentSection.getResearchStatement(), applicationDownloadDTO);
            }

            if (coveringLetterEnabled) {
                addBookmark(body, propertyLoader.load(APPLICATION_DOCUMENT_COVERING_LETTER_APPENDIX),
                        documentSection == null ? null : documentSection.getCoveringLetter(), applicationDownloadDTO);
            }

            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        }
    }

    private void addAdditionalInformationSection(Application application, ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument)
            throws DocumentException {
        if (customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, application,
                PrismWorkflowPropertyDefinition.APPLICATION_CRIMINAL_CONVICTION) && applicationDownloadDTO.isIncludeEqualOpportunities()) {
            PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_ADDITIONAL_INFORMATION_HEADER));
            ApplicationAdditionalInformation additionalInformation = application.getAdditionalInformation();

            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_ADDITIONAL_INFORMATION_CONVICTION),
                    additionalInformation == null ? null : additionalInformation.getConvictionsText(), body);

            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        }
    }

    private void addSupportingDocuments(ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument, PdfWriter pdfWriter) throws DocumentException,
            IOException, IntegrationException {
        for (int i = 0; i < bookmarks.size(); i++) {
            pdfDocument.newPage();

            NewPageEvent pageEvent = (NewPageEvent) pdfWriter.getPageEvent();
            pageEvent.setApplyHeaderFooter(true);

            Anchor anchor = new Anchor();
            anchor.setName(String.valueOf(i));

            Object object = bookmarks.get(i);
            pdfDocument.add(new Chunk(propertyLoader.load(SYSTEM_APPENDIX) + "(" + (i + 1) + ")").setLocalDestination(new Integer(i).toString()));
            if (object instanceof com.zuehlke.pgadmissions.domain.document.Document) {
                com.zuehlke.pgadmissions.domain.document.Document document = (com.zuehlke.pgadmissions.domain.document.Document) object;

                if (document.getApplicationLanguageQualification() != null) {
                    pdfDocument.add(buildTarget(APPLICATION_LANGUAGE_QUALIFICATION_APPENDIX, anchor));
                } else if (document.getApplicationQualification() != null) {
                    pdfDocument.add(buildTarget(APPLICATION_QUALIFICATION_APPENDIX, anchor));
                } else if (document.getApplicationFunding() != null) {
                    pdfDocument.add(buildTarget(APPLICATION_FUNDING_APPENDIX, anchor));
                } else if (document.getApplicationPersonalStatement() != null) {
                    pdfDocument.add(buildTarget(APPLICATION_DOCUMENT_PERSONAL_STATEMENT_APPENDIX, anchor));
                } else if (document.getApplicationCv() != null) {
                    pdfDocument.add(buildTarget(APPLICATION_DOCUMENT_CV_APPENDIX, anchor));
                }

                addDocument(pdfDocument, document, pdfWriter);
            } else if (object instanceof Comment) {
                Comment referenceComment = (Comment) object;
                pdfDocument.add(buildTarget(APPLICATION_REFEREE_REFERENCE_APPENDIX, anchor));

                pdfDocument.add(applicationDownloadBuilderHelper.newSectionSeparator());

                PdfPTable subBody = applicationDownloadBuilderHelper.startSubSection(propertyLoader.load(APPLICATION_REFEREE_REFERENCE_COMMENT));
                applicationContext.getBean(ApplicationDownloadReferenceBuilder.class).localize(propertyLoader, applicationDownloadBuilderHelper)
                        .addReferenceComment(pdfDocument, subBody, pdfWriter, applicationDownloadDTO.getApplication(), referenceComment);

                for (com.zuehlke.pgadmissions.domain.document.Document document : referenceComment.getDocuments()) {
                    addDocument(pdfDocument, document, pdfWriter);
                }
            }
        }
    }

    private Phrase buildTarget(PrismDisplayPropertyDefinition title, Anchor anchor) {
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

    private void addApplicationSummary(Application application, PdfPTable table, ApplicationDownloadBuilderFontSize fontSize) {
        applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(INSTITUTION_HEADER), application.getInstitutionDisplay(), fontSize, table);
        applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(PROGRAM_HEADER), application.getProgramDisplay(), fontSize, table);

        Project project = application.getProject();
        if (project != null) {
            applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(PROJECT_HEADER), application.getProjectDisplay(), fontSize, table);
        }

        applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(APPLICATION_PREVIOUS_APPLICATION),
                propertyLoader.load(SYSTEM_YES, SYSTEM_NO, BooleanUtils.toBoolean(application.getPreviousApplication())), fontSize, table);

        ApplicationProgramDetail programDetail = application.getProgramDetail();
        PrismStudyOption studyOption = programDetail == null ? null : programDetail.getStudyOptionDisplay();
        applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(PROGRAM_STUDY_OPTION),
                studyOption == null ? null : propertyLoader.load(studyOption.getDisplayProperty()), fontSize, table);
    }

    private void addApplicationSummaryExtended(Application application, PdfPTable table, ApplicationDownloadBuilderFontSize fontSize) {
        addApplicationSummary(application, table, fontSize);

        String dateFormat = propertyLoader.load(SYSTEM_DATE_FORMAT);

        applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(APPLICATION_CODE), application.getCode(), fontSize, table);
        applicationDownloadBuilderHelper
                .addContentRow(propertyLoader.load(SYSTEM_CLOSING_DATE), application.getClosingDateDisplay(dateFormat), fontSize, table);
        applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(APPLICATION_SUBMISSION_DATE), application.getSubmittedTimestampDisplay(dateFormat),
                fontSize, table);
    }

    private void addBookmark(PdfPTable table, String rowTitle, Object object, ApplicationDownloadDTO applicationDownloadDTO) {
        table.addCell(applicationDownloadBuilderHelper.newTitleCellMedium(rowTitle));
        ApplicationDownloadMode downloadMode = applicationDownloadDTO.getDownloadMode();
        boolean includeAttachments = applicationDownloadDTO.isIncludeAttachments();
        if ((downloadMode == ApplicationDownloadMode.SYSTEM && includeAttachments)
                || (downloadMode == ApplicationDownloadMode.USER && (includeAttachments || userService.isCurrentUser((User) ReflectionUtils.getProperty(object,
                        "user"))))) {
            if (object == null) {
                table.addCell(applicationDownloadBuilderHelper.newContentCellMedium(null));
            } else {
                int index = bookmarks.size();
                table.addCell(applicationDownloadBuilderHelper.newBookmarkCellMedium(
                        propertyLoader.load(SYSTEM_SEE) + " " + propertyLoader.load(SYSTEM_APPENDIX) + " (" + (index + 1) + ")", index));
                bookmarks.add(object);
            }
        } else {
            table.addCell(applicationDownloadBuilderHelper.newContentCellMedium(object == null ? null : propertyLoader.load(SYSTEM_VALUE_PROVIDED)));
        }
    }

    private void addLogoImage(Document pdfDocument) throws IOException, DocumentException {
        Image logoImage = applicationDownloadBuilderHelper.newLogoImage();
        logoImage.setAbsolutePosition(pdfDocument.right() - logoImage.getScaledWidth(), pdfDocument.top() + 20f);
        pdfDocument.add(logoImage);
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
                    throw new RuntimeException(e);
                }
            }
        }

        private void addHeader(PdfWriter writer, Document pdfDocument) throws DocumentException, IOException {
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

        private void addFooter(PdfWriter writer, Document document) {
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
