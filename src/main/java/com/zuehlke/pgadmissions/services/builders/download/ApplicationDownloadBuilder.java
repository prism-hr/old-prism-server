package com.zuehlke.pgadmissions.services.builders.download;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.events.PdfPageEventForwarder;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;
import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.address.AddressApplicationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.application.*;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.builders.download.ApplicationDownloadBuilderConfiguration.ApplicationDownloadBuilderFontSize;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;
import uk.co.alumeni.prism.api.model.imported.response.ImportedInstitutionResponse;
import uk.co.alumeni.prism.api.model.imported.response.ImportedProgramResponse;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.*;
import static com.zuehlke.pgadmissions.services.builders.download.ApplicationDownloadBuilderConfiguration.ApplicationDownloadBuilderFontSize.MEDIUM;
import static com.zuehlke.pgadmissions.utils.PrismStringUtils.getBigDecimalAsString;
import static com.zuehlke.pgadmissions.utils.PrismStringUtils.getImportedEntityAsString;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ApplicationDownloadBuilder {

    private PropertyLoader propertyLoader;

    private final Map<String, Bookmark<?>> bookmarks = Maps.newLinkedHashMap();

    private ApplicationDownloadBuilderHelper applicationDownloadBuilderHelper;

    @Value("${xml.export.logo.file.width.percentage}")
    private Float logoFileWidthPercentage;

    @Inject
    private DocumentService documentService;

    @Inject
    private ApplicationContext applicationContext;

    public void build(ApplicationRepresentationExport application, Document pdfDocument, PdfWriter writer) throws PdfDocumentBuilderException {
        try {
            addCoverPage(application, pdfDocument, writer);
            writer.setPageEvent(new NewPageEvent().withApplication(application));
            addProgramSection(application, pdfDocument);
            addSupervisorSection(application, pdfDocument);
            addPersonalDetailSection(application, pdfDocument);
            addAddressSection(application, pdfDocument);
            addQualificationSection(application, pdfDocument);
            addEmploymentSection(application, pdfDocument);
            addFundingSection(application, pdfDocument);
            addPrizesSection(application, pdfDocument);
            addReferencesSection(application, pdfDocument);
            addDocumentSection(application, pdfDocument);
            addAdditionalInformationSection(application, pdfDocument);
            addSupportingDocuments(application, pdfDocument, writer);
        } catch (Exception e) {
            throw new PdfDocumentBuilderException(e);
        }
    }

    public ApplicationDownloadBuilder localize(PropertyLoader propertyLoader, ApplicationDownloadBuilderHelper applicationDownloadBuilderHelper) {
        this.propertyLoader = propertyLoader;
        this.applicationDownloadBuilderHelper = applicationDownloadBuilderHelper;
        return this;
    }

    private void addCoverPage(ApplicationRepresentationExport application, Document pdfDocument, PdfWriter writer) throws Exception {
        pdfDocument.newPage();
        addLogoImage(pdfDocument);

        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.drawLine(writer.getDirectContent(), pdfDocument.left(), pdfDocument.right(), pdfDocument.top() + 10f);

        pdfDocument.add(applicationDownloadBuilderHelper.newSectionSeparator());
        pdfDocument.add(applicationDownloadBuilderHelper.newSectionSeparator());

        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_HEADER));

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_CREATOR), application.getUser().getFullName(), body);

        BigDecimal applicationRatingAverage = application.getApplicationRatingAverage();
        if (applicationRatingAverage != null) {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_AVERAGE_RATING),
                    getBigDecimalAsString(application.getApplicationRatingAverage()), body);
        }

        addApplicationSummaryExtended(application, body, MEDIUM);
        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        pdfDocument.newPage();
    }

    private void addProgramSection(ApplicationRepresentationExport application, Document pdfDocument) throws Exception {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_PROGRAM_DETAIL_HEADER));
        addApplicationSummary(application, body, MEDIUM);

        ApplicationProgramDetailRepresentation programDetail = application.getProgramDetail();

        if (programDetail != null) {
            LocalDate startDate = programDetail.getStartDate();
            ApplicationOfferRepresentation offer = application.getOfferRecommendation();
            LocalDate confirmedStartDate = offer == null ? null : offer.getPositionProvisionalStartDate();

            String dateFormat = propertyLoader.load(SYSTEM_DATE_FORMAT);

            applicationDownloadBuilderHelper.addContentRowMedium(
                    propertyLoader.load(APPLICATION_PROGRAM_DETAIL_START_DATE_LABEL, APPLICATION_CONFIRMED_START_DATE,
                            confirmedStartDate == null), confirmedStartDate == null ? startDate.toString(dateFormat) : confirmedStartDate.toString(dateFormat),
                    body);

            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_REFERRAL_SOURCE),
                    getImportedEntityAsString(programDetail.getReferralSource()), body);
        }

        addStudyDetailSection(application, body);
        addThemeSection(application, body);

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
    }

    private void addStudyDetailSection(ApplicationRepresentationExport application, PdfPTable body) throws Exception {
        ApplicationStudyDetailRepresentation studyDetail = application.getStudyDetail();
        if (studyDetail != null) {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_STUDY_DETAIL_LOCATION_LABEL), studyDetail.getStudyLocation(),
                    body);
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_STUDY_DETAIL_DIVISION_LABEL), studyDetail.getStudyDivision(),
                    body);
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_STUDY_DETAIL_AREA_LABEL), studyDetail.getStudyArea(), body);
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_STUDY_DETAIL_APPLICATION_ID_LABEL),
                    studyDetail.getStudyApplicationId(), body);
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_STUDY_DETAIL_START_DATE_LABEL), studyDetail
                    .getStudyStartDate().toString((propertyLoader.load(SYSTEM_DATE_FORMAT))), body);
        }
    }

    private void addThemeSection(ApplicationRepresentationExport application, PdfPTable body) throws Exception {
        List<String> primaryThemes = application.getPrimaryThemes();
        if (!primaryThemes.isEmpty()) {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PROGRAM_DETAIL_PRIMARY_THEME_LABEL),
                    Joiner.on(", ").join(primaryThemes), body);
        }

        List<String> secondaryThemes = application.getSecondaryThemes();
        if (!secondaryThemes.isEmpty()) {
            applicationDownloadBuilderHelper.addContentRowMedium(
                    propertyLoader.load(PrismDisplayPropertyDefinition.APPLICATION_PROGRAM_DETAIL_SECONDARY_THEME_LABEL),
                    Joiner.on(", ").join(secondaryThemes), body);
        }
    }

    private void addSupervisorSection(ApplicationRepresentationExport application, Document pdfDocument) throws Exception {
        List<ApplicationSupervisorRepresentation> supervisors = application.getSupervisors();
        if (!supervisors.isEmpty()) {
            PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_SUPERVISOR_HEADER));

            String subheader = propertyLoader.load(APPLICATION_SUPERVISOR_SUBHEADER);

            if (supervisors.isEmpty()) {
                applicationDownloadBuilderHelper.addContentRowMedium(subheader, null, body);
                applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
            } else {
                int counter = 1;
                for (ApplicationSupervisorRepresentation supervisor : supervisors) {
                    PdfPTable subBody = applicationDownloadBuilderHelper.startSubSection(subheader + "(" + counter++ + ")");

                    UserRepresentationSimple user = supervisor.getUser();

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

    private void addPersonalDetailSection(ApplicationRepresentationExport application, Document pdfDocument)
            throws Exception {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_PERSONAL_DETAIL_HEADER));

        ApplicationPersonalDetailRepresentation personalDetail = application.getPersonalDetail();
        boolean personalDetailNull = personalDetail == null;

        if (personalDetailNull) {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_TITLE_LABEL),
                    getImportedEntityAsString(personalDetail.getTitle()), body);
        }

        UserRepresentationSimple applicationCreator = application.getUser();

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_FIRST_NAME), applicationCreator.getFirstName(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_FIRST_NAME_2), applicationCreator.getFirstName2(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_FIRST_NAME_3), applicationCreator.getFirstName3(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_HTML_GENERAL_FIELD_LAST_NAME_LABEL), applicationCreator.getLastName(),
                body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_EMAIL), applicationCreator.getEmail(), body);

        if (personalDetailNull) {
            applicationDownloadBuilderHelper
                    .addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_TELEPHONE_LABEL), personalDetail.getPhone(), body);
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_SKYPE_LABEL), personalDetail.getSkype(), body);

            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_GENDER_LABEL),
                    getImportedEntityAsString(personalDetail.getGender()), body);
            applicationDownloadBuilderHelper.addContentRowMedium(
                    propertyLoader.load(PrismDisplayPropertyDefinition.APPLICATION_PERSONAL_DETAIL_DATE_OF_BIRTH_LABEL), personalDetail.getDateOfBirth()
                            .toString(propertyLoader.load(SYSTEM_DATE_FORMAT)), body);
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_COUNTRY_OF_BIRTH_LABEL),
                    getImportedEntityAsString(personalDetail.getCountry()), body);
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_DOMICILE_LABEL),
                    getImportedEntityAsString(personalDetail.getDomicile()), body);
            applicationDownloadBuilderHelper.addContentRowMedium(
                    propertyLoader.load(APPLICATION_PERSONAL_DETAIL_NATIONALITY_LABEL),
                    Joiner.on(", ")
                            .skipNulls()
                            .join(getImportedEntityAsString(personalDetail.getFirstNationality()),
                                    getImportedEntityAsString(personalDetail.getSecondNationality())), body);

            ApplicationDemographicRepresentation demographic = personalDetail.getDemographic();
            if (demographic != null) {
                appendEqualOpportunitiesSection(personalDetail.getDemographic(), body);
            }

            boolean passportAvailable = false;
            Boolean addPassport = personalDetail.getVisaRequired();
            if (addPassport != null) {
                passportAvailable = addPassportHeader(personalDetail, body);
            }

            boolean languageQualificationAvailable = false;
            Boolean addLanguageQualification = personalDetail.getFirstLanguageLocale();
            if (addLanguageQualification != null) {
                languageQualificationAvailable = addLanguageQualificationHeader(personalDetail, body);
            }

            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);

            if (passportAvailable) {
                addPassportSection(personalDetail.getPassport(), pdfDocument);
            }

            if (languageQualificationAvailable) {
                addLanguageQualificationSection(application, personalDetail.getLanguageQualification(), pdfDocument);
            }
        }
    }

    private void appendEqualOpportunitiesSection(ApplicationDemographicRepresentation demographic, PdfPTable body) throws Exception {
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_ETHNICITY_LABEL),
                getImportedEntityAsString(demographic.getEthnicity()), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_DISABILITY_LABEL),
                getImportedEntityAsString(demographic.getDisability()), body);
    }

    private boolean addPassportHeader(ApplicationPersonalDetailRepresentation personalDetail, PdfPTable body) throws Exception {
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_VISA_REQUIRED_LABEL),
                propertyLoader.load(SYSTEM_YES, SYSTEM_NO, BooleanUtils.toBoolean(personalDetail.getVisaRequired())), body);

        ApplicationPassportRepresentation passport = personalDetail.getPassport();
        if (passport != null) {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_PASSPORT_AVAILABLE_LABEL),
                    propertyLoader.load(SYSTEM_YES, SYSTEM_NO, true), body);
            return true;
        }
        return false;
    }

    private boolean addLanguageQualificationHeader(ApplicationPersonalDetailRepresentation personalDetail, PdfPTable body) throws Exception {
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_FIRST_LANGUAGE_LOCALE_LABEL),
                propertyLoader.load(SYSTEM_YES, SYSTEM_NO, personalDetail.getFirstLanguageLocale()), body);

        ApplicationLanguageQualificationRepresentation languageQualification = personalDetail.getLanguageQualification();
        if (languageQualification != null) {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_LANGUAGE_QUALIFICATION_AVAILABLE_LABEL),
                    propertyLoader.load(SYSTEM_YES, SYSTEM_NO, true), body);
            return true;
        }
        return false;
    }

    private void addPassportSection(ApplicationPassportRepresentation passport, Document pdfDocument) throws Exception {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_PASSPORT_HEADER));

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PASSPORT_NUMBER_LABEL), passport.getNumber(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PASSPORT_NAME_LABEL), passport.getName(), body);

        String dateFormat = propertyLoader.load(SYSTEM_DATE_FORMAT);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PASSPORT_ISSUE_DATE_LABEL),
                passport.getIssueDate().toString(dateFormat), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PASSPORT_EXPIRY_DATE_LABEL),
                passport.getExpiryDate().toString(dateFormat), body);

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
    }

    private void addLanguageQualificationSection(ApplicationRepresentationExport application,
            ApplicationLanguageQualificationRepresentation languageQualification, Document pdfDocument) throws Exception {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_LANGUAGE_QUALIFICATION_HEADER));

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_LANGUAGE_QUALIFICATION_TYPE_LABEL),
                getImportedEntityAsString(languageQualification.getType()), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_LANGUAGE_QUALIFICATION_EXAM_DATE_LABEL),
                languageQualification.getExamDate().toString(propertyLoader.load(SYSTEM_DATE_FORMAT)), body);
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

        DocumentRepresentation proofOfAward = languageQualification.getDocument();
        if (proofOfAward != null) {
            addBookmark(application, new Bookmark<DocumentRepresentation>().withLabel(APPLICATION_LANGUAGE_QUALIFICATION_APPENDIX).withContent(proofOfAward),
                    body, propertyLoader.load(APPLICATION_PROOF_OF_AWARD));
        }

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
    }

    private void addAddressSection(ApplicationRepresentationExport application, Document pdfDocument) throws Exception {
        ApplicationAddressRepresentation address = application.getAddress();

        if (address != null) {
            PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument,
                    propertyLoader.load(PrismDisplayPropertyDefinition.APPLICATION_ADDRESS_HEADER));

            AddressApplicationRepresentation currentAddress = address.getCurrentAddress();
            if (currentAddress != null) {
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_ADDRESS_CURRENT_HEADER),
                        address == null ? null : currentAddress.getLocationString(), body);
            }

            AddressApplicationRepresentation contactAddress = address.getContactAddress();
            if (contactAddress != null) {
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_ADDRESS_CONTACT_HEADER),
                        address == null ? null : contactAddress.getLocationString(), body);
            }

            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        }
    }

    private void addQualificationSection(ApplicationRepresentationExport application, Document pdfDocument) throws Exception {
        List<ApplicationQualificationRepresentation> qualifications = application.getQualifications();

        if (!qualifications.isEmpty()) {
            applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_QUALIFICATION_HEADER));

            int counter = 1;
            for (ApplicationQualificationRepresentation qualification : qualifications) {
                PdfPTable subBody = applicationDownloadBuilderHelper.startSubSection(propertyLoader.load(APPLICATION_QUALIFICATION_SUBHEADER) + "("
                        + counter++ + ")");

                String dateFormat = propertyLoader.load(SYSTEM_DATE_FORMAT);

                ImportedProgramResponse program = qualification.getProgram();
                ImportedInstitutionResponse institution = program.getInstitution();

                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_DOMICILE_LABEL),
                        getImportedEntityAsString(institution.getDomicile()), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_PROVIDER_LABEL), institution.getName(),
                        subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_TYPE_LABEL),
                        getImportedEntityAsString(program.getQualificationType()), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_TITLE_LABEL),
                        program.getQualification(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_SUBJECT_LABEL),
                        program.getName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_LANGUAGE_LABEL),
                        qualification.getLanguage(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_START_DATE_LABEL),
                        qualification.getStartDate().toString(dateFormat), subBody);

                boolean completed = BooleanUtils.isTrue(qualification.getCompleted());

                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_COMPLETED_LABEL),
                        propertyLoader.load(SYSTEM_YES, SYSTEM_NO, completed), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(
                        propertyLoader.load(APPLICATION_QUALIFICATION_CONFIRMED_RESULT_LABEL, APPLICATION_QUALIFICATION_EXPECTED_RESULT_LABEL, completed),
                        qualification.getGrade(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(
                        propertyLoader.load(APPLICATION_QUALIFICATION_CONFIRMED_AWARD_DATE_LABEL, APPLICATION_QUALIFICATION_EXPECTED_AWARD_DATE_LABEL,
                                completed),
                        qualification.getAwardDate().toString(dateFormat), subBody);

                DocumentRepresentation proofOfAward = qualification.getDocument();
                if (proofOfAward != null) {
                    addBookmark(application, new Bookmark<DocumentRepresentation>().withLabel(APPLICATION_QUALIFICATION_APPENDIX).withContent(proofOfAward),
                            subBody, propertyLoader.load(APPLICATION_QUALIFICATION_DOCUMENT_LABEL));
                }

                applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
            }
        }
    }

    private void addEmploymentSection(ApplicationRepresentationExport application, Document pdfDocument) throws Exception {
        List<ApplicationEmploymentPositionRepresentation> positions = application.getEmploymentPositions();

        if (!positions.isEmpty()) {
            applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_HEADER));

            int counter = 1;
            for (ApplicationEmploymentPositionRepresentation position : positions) {
                PdfPTable subBody = applicationDownloadBuilderHelper.startSubSection(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_SUBHEADER) + "("
                        + counter++ + ")");

                String dateFormat = propertyLoader.load(SYSTEM_DATE_FORMAT);

                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_EMPLOYER_NAME_LABEL),
                        position.getEmployerName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_EMPLOYER_ADDRESS_LABEL),
                        position.getEmployerAddress().getLocationString(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_TITLE_LABEL),
                        position.getPosition(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_REMIT_LABEL), position.getRemit(),
                        subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_START_DATE_LABEL),
                        position.getStartDate().toString(dateFormat), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_CURRENT_LABEL),
                        propertyLoader.load(SYSTEM_YES, SYSTEM_NO, position.getCurrent()), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_END_DATE_LABEL),
                        position.getEndDate().toString(dateFormat), subBody);

                applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
            }
        }
    }

    private void addFundingSection(ApplicationRepresentationExport application, Document pdfDocument) throws Exception {
        List<ApplicationFundingRepresentation> fundings = application.getFundings();

        if (!fundings.isEmpty()) {
            applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_FUNDING_HEADER));

            int counter = 1;
            for (ApplicationFundingRepresentation funding : fundings) {
                PdfPTable subBody = applicationDownloadBuilderHelper.startSubSection(propertyLoader.load(APPLICATION_FUNDING_SUBHEADER) + "(" + counter++
                        + ")");
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_FUNDING_TYPE_LABEL),
                        getImportedEntityAsString(funding.getFundingSource()), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_FUNDING_SPONSOR_LABEL), funding.getSponsor(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_FUNDING_DESCRIPTION_LABEL), funding.getDescription(),
                        subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_FUNDING_VALUE_LABEL), funding.getValue(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_FUNDING_AWARD_DATE_LABEL),
                        funding.getAwardDate().toString(propertyLoader.load(SYSTEM_DATE_FORMAT)), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_FUNDING_TERMS_LABEL), funding.getTerms(), subBody);

                DocumentRepresentation proofOfAward = funding.getDocument();
                if (proofOfAward != null) {
                    addBookmark(application, new Bookmark<DocumentRepresentation>().withLabel(APPLICATION_FUNDING_DOCUMENT_LABEL).withContent(proofOfAward),
                            subBody, propertyLoader.load(APPLICATION_PROOF_OF_AWARD));
                }

                applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
            }
        }
    }

    private void addPrizesSection(ApplicationRepresentationExport application, Document pdfDocument) throws Exception {
        List<ApplicationPrizeRepresentation> prizes = application.getPrizes();

        if (!prizes.isEmpty()) {
            applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_PRIZE_HEADER));

            int counter = 1;
            for (ApplicationPrizeRepresentation prize : prizes) {
                PdfPTable subBody = applicationDownloadBuilderHelper.startSubSection(propertyLoader.load(APPLICATION_PRIZE_SUBHEADER) + "(" + counter++
                        + ")");
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PRIZE_PROVIDER_LABEL), prize.getProvider(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PRIZE_TITLE_LABEL), prize.getTitle(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PRIZE_DESCRIPTION_LABEL), prize.getDescription(),
                        subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PRIZE_AWARD_DATE_LABEL),
                        prize.getAwardDate().toString(propertyLoader.load(SYSTEM_DATE_FORMAT)), subBody);

                applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
            }
        }
    }

    private void addReferencesSection(ApplicationRepresentationExport application, Document pdfDocument) throws Exception {
        List<ApplicationRefereeRepresentation> referees = application.getReferees();

        if (!referees.isEmpty()) {
            applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_REFEREE_HEADER));

            int counter = 1;
            for (ApplicationRefereeRepresentation referee : referees) {
                PdfPTable subBody = applicationDownloadBuilderHelper
                        .startSubSection(propertyLoader.load(APPLICATION_REFEREE_SUBHEADER) + "(" + counter++ + ")");

                UserRepresentationSimple user = referee.getUser();
                boolean userNull = user == null;

                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_FIRST_NAME), userNull ? null : user.getFirstName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_HTML_GENERAL_FIELD_LAST_NAME_LABEL),
                        userNull ? null : user.getLastName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_EMAIL), userNull ? null : user.getEmail(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_REFEREE_EMPLOYER_ADDRESS_LABEL),
                        referee.getAddress().getLocationString(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_TELEPHONE_LABEL), referee.getPhone(),
                        subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_SKYPE_LABEL), referee.getSkype(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_EMPLOYER_NAME_LABEL),
                        referee.getJobEmployer(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_REFEREE_POSITION_LABEL), referee.getJobTitle(), subBody);

                CommentRepresentation referenceComment = referee.getComment();
                if (referenceComment != null) {
                    addBookmark(application,
                            new Bookmark<CommentRepresentation>().withLabel(APPLICATION_REFEREE_REFERENCE_APPENDIX).withContent(referenceComment), subBody,
                            propertyLoader.load(APPLICATION_REFEREE_REFERENCE_APPENDIX));
                }

                applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
            }
        }
    }

    private void addDocumentSection(ApplicationRepresentationExport application, Document pdfDocument) throws Exception {
        ApplicationDocumentRepresentation document = application.getDocument();

        if (document != null) {
            PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_DOCUMENT_HEADER));

            DocumentRepresentation personalStatement = document.getPersonalStatement();
            if (personalStatement != null) {
                addBookmark(application,
                        new Bookmark<DocumentRepresentation>().withLabel(APPLICATION_DOCUMENT_PERSONAL_STATEMENT_LABEL).withContent(personalStatement), body,
                        propertyLoader.load(APPLICATION_DOCUMENT_PERSONAL_STATEMENT_LABEL));
            }

            DocumentRepresentation cv = document.getCv();
            if (cv != null) {
                addBookmark(application, new Bookmark<DocumentRepresentation>().withLabel(APPLICATION_DOCUMENT_CV_LABEL).withContent(cv), body,
                        propertyLoader.load(APPLICATION_DOCUMENT_CV_LABEL));
            }

            DocumentRepresentation researchStatement = document.getResearchStatement();
            if (researchStatement != null) {
                addBookmark(application,
                        new Bookmark<DocumentRepresentation>().withLabel(APPLICATION_DOCUMENT_RESEARCH_STATEMENT_LABEL).withContent(researchStatement), body,
                        propertyLoader.load(APPLICATION_DOCUMENT_RESEARCH_STATEMENT_LABEL));
            }

            DocumentRepresentation coveringLetter = document.getCoveringLetter();
            if (coveringLetter != null) {
                addBookmark(application,
                        new Bookmark<DocumentRepresentation>().withLabel(APPLICATION_DOCUMENT_COVERING_LETTER_LABEL).withContent(coveringLetter), body,
                        propertyLoader.load(APPLICATION_DOCUMENT_COVERING_LETTER_LABEL));
            }

            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        }
    }

    private void addAdditionalInformationSection(ApplicationRepresentationExport application, Document pdfDocument) throws Exception {
        ApplicationAdditionalInformationRepresentation additionalInformation = application.getAdditionalInformation();

        if (additionalInformation != null) {
            PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_ADDITIONAL_INFORMATION_HEADER));

            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_ADDITIONAL_INFORMATION_CONVICTION_LABEL),
                    additionalInformation.getConvictionsText(), body);

            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        }
    }

    private void addSupportingDocuments(ApplicationRepresentationExport application, Document pdfDocument, PdfWriter pdfWriter) throws Exception {
        int index = 0;
        for (Map.Entry<String, Bookmark<?>> entry : bookmarks.entrySet()) {
            pdfDocument.newPage();

            NewPageEvent pageEvent = (NewPageEvent) pdfWriter.getPageEvent();
            pageEvent.setApplyHeaderFooter(true);

            String bookmarkKey = entry.getKey();
            Anchor anchor = new Anchor();
            anchor.setName(bookmarkKey);

            Bookmark<?> bookmark = entry.getValue();
            Object content = bookmark.getContent();
            pdfDocument.add(new Chunk(propertyLoader.load(SYSTEM_APPENDIX) + "(" + (index + 1) + ")").setLocalDestination(bookmarkKey));
            if (content instanceof DocumentRepresentation) {
                DocumentRepresentation document = (DocumentRepresentation) content;
                pdfDocument.add(buildTarget(bookmark.getLabel(), anchor));
                addDocument(document, pdfDocument, pdfWriter);
            } else if (content instanceof Comment) {
                CommentRepresentation referenceComment = (CommentRepresentation) content;
                pdfDocument.add(buildTarget(APPLICATION_REFEREE_REFERENCE_APPENDIX, anchor));

                pdfDocument.add(applicationDownloadBuilderHelper.newSectionSeparator());

                PdfPTable subBody = applicationDownloadBuilderHelper.startSubSection(propertyLoader.load(APPLICATION_REFEREE_REFERENCE_COMMENT));
                applicationContext.getBean(ApplicationDownloadReferenceBuilder.class).localize(propertyLoader, applicationDownloadBuilderHelper)
                        .addReferenceComment(pdfDocument, subBody, pdfWriter, application, referenceComment);

                for (DocumentRepresentation document : referenceComment.getDocuments()) {
                    addDocument(document, pdfDocument, pdfWriter);
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

    private void addDocument(DocumentRepresentation document, Document pdfDocument, PdfWriter pdfWriter) throws IOException,
            IntegrationException {
        PdfReader pdfReader = new PdfReader(documentService.getDocumentContent(document.getId()));
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

    private void addApplicationSummary(ApplicationRepresentationExport application, PdfPTable table, ApplicationDownloadBuilderFontSize fontSize)
            throws Exception {
        applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(SYSTEM_INSTITUTION), application.getInstitution().getName(), fontSize, table);

        ResourceRepresentationSimple department = application.getDepartment();
        if (department != null) {
            applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(SYSTEM_DEPARTMENT), department.getName(), fontSize, table);
        }

        ResourceRepresentationSimple program = application.getProgram();
        if (program != null) {
            applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(SYSTEM_PROGRAM), program.getName(), fontSize, table);
        }

        ResourceRepresentationSimple project = application.getProject();
        if (project != null) {
            applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(SYSTEM_PROJECT), project.getName(), fontSize, table);
        }

        applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(APPLICATION_PREVIOUS_APPLICATION),
                propertyLoader.load(SYSTEM_YES, SYSTEM_NO, BooleanUtils.toBoolean(application.getPreviousApplication())), fontSize, table);

        ApplicationProgramDetailRepresentation programDetail = application.getProgramDetail();
        if (programDetail != null) {
            ImportedEntityResponse studyOption = programDetail.getStudyOption();
            applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(APPLICATION_PROGRAM_DETAIL_STUDY_OPTION_LABEL),
                    studyOption == null ? null : studyOption.getName(), fontSize, table);
        }
    }

    private void addApplicationSummaryExtended(ApplicationRepresentationExport application, PdfPTable table, ApplicationDownloadBuilderFontSize fontSize)
            throws Exception {
        addApplicationSummary(application, table, fontSize);

        String dateFormat = propertyLoader.load(SYSTEM_DATE_FORMAT);

        applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(APPLICATION_CODE), application.getCode(), fontSize, table);

        LocalDate closingDate = application.getClosingDate();
        if (closingDate != null) {
            applicationDownloadBuilderHelper
                    .addContentRow(propertyLoader.load(SYSTEM_CLOSING_DATE), closingDate.toString(dateFormat), fontSize, table);
        }

        DateTime submittedTimestamp = application.getSubmittedTimestamp();
        if (submittedTimestamp != null) {
            applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(APPLICATION_SUBMISSION_DATE), submittedTimestamp.toString(dateFormat), fontSize,
                    table);
        }
    }

    private <T> void addBookmark(ApplicationRepresentationExport application, Bookmark<T> bookmark, PdfPTable table, String rowTitle) throws Exception {
        table.addCell(applicationDownloadBuilderHelper.newTitleCellMedium(rowTitle));
        T content = bookmark.getContent();
        if (content == null) {
            table.addCell(applicationDownloadBuilderHelper.newContentCellMedium(content == null ? null : propertyLoader.load(SYSTEM_VALUE_PROVIDED)));
        } else {
            int index = bookmarks.size();
            String anchor = application.getCode() + "-" + index;
            table.addCell(applicationDownloadBuilderHelper.newBookmarkCellMedium(
                    propertyLoader.load(SYSTEM_SEE) + " " + propertyLoader.load(SYSTEM_APPENDIX) + " (" + (index + 1) + ")", anchor));
            bookmarks.put(anchor, bookmark);
        }
    }

    private void addLogoImage(Document pdfDocument) throws IOException, DocumentException {
        Image logoImage = applicationDownloadBuilderHelper.newLogoImage();
        logoImage.setAbsolutePosition(pdfDocument.right() - logoImage.getScaledWidth(), pdfDocument.top() + 20f);
        pdfDocument.add(logoImage);
    }

    private class NewPageEvent extends PdfPageEventForwarder {

        private ApplicationRepresentationExport application;

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

        public NewPageEvent withApplication(ApplicationRepresentationExport application) {
            this.application = application;
            return this;
        }

    }

    private static class Bookmark<T> {

        private PrismDisplayPropertyDefinition label;

        private T content;

        public PrismDisplayPropertyDefinition getLabel() {
            return label;
        }

        public T getContent() {
            return content;
        }

        public Bookmark<T> withLabel(PrismDisplayPropertyDefinition label) {
            this.label = label;
            return this;
        }

        public Bookmark<T> withContent(T content) {
            this.content = content;
            return this;
        }

    }

}
