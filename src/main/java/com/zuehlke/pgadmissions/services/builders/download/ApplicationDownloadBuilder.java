package com.zuehlke.pgadmissions.services.builders.download;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.*;
import static com.zuehlke.pgadmissions.services.builders.download.ApplicationDownloadBuilderConfiguration.ApplicationDownloadBuilderFontSize.MEDIUM;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
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
import com.zuehlke.pgadmissions.domain.application.ApplicationSupervisor;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ApplicationDownloadDTO;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;
import com.zuehlke.pgadmissions.services.builders.download.ApplicationDownloadBuilderConfiguration.ApplicationDownloadBuilderFontSize;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ApplicationDownloadBuilder {

    private static Logger LOGGER = LoggerFactory.getLogger(ApplicationDownloadBuilder.class);

    private final List<Object> bookmarks = Lists.newLinkedList();

    private PropertyLoader propertyLoader;

    private ApplicationDownloadBuilderHelper applicationDownloadBuilderHelper;

    @Value("${xml.export.logo.file.width.percentage}")
    private Float logoFileWidthPercentage;

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
            addPrizesSection(applicationDownloadDTO, pdfDocument);
            addReferencesSection(applicationDownloadDTO, pdfDocument);
            addDocumentSection(applicationDownloadDTO, pdfDocument);
            addAdditionalInformationSection(applicationDownloadDTO, pdfDocument);
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

    private void addCoverPage(Application application, Document pdfDocument, PdfWriter writer) throws MalformedURLException, IOException, DocumentException {
        pdfDocument.newPage();

        Image logoImage = applicationDownloadBuilderHelper.newLogoImage();
        logoImage.scaleToFit(logoImage.getWidth() * 0.5f, logoImage.getHeight() * 0.5f);

        logoImage.setAbsolutePosition(pdfDocument.right() - logoImage.getScaledWidth(), pdfDocument.top() + 20f);
        pdfDocument.add(logoImage);

        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.drawLine(writer.getDirectContent(), pdfDocument.left(), pdfDocument.right(), pdfDocument.top() + 10f);

        pdfDocument.add(applicationDownloadBuilderHelper.newSectionSeparator());
        pdfDocument.add(applicationDownloadBuilderHelper.newSectionSeparator());

        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_HEADER));

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_CREATOR), application.getUser().getFullName(), body);
        applicationDownloadBuilderHelper
                .addContentRowMedium(propertyLoader.load(SYSTEM_AVERAGE_RATING), application.getApplicationRatingAverageDisplay(), body);

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

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_START_DATE, APPLICATION_CONFIRMED_START_DATE,
                confirmedStartDate == null), confirmedStartDate == null ? startDate : confirmedStartDate, body);

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_STUDY_LOCATION), application.getStudyLocation(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_STUDY_DIVISION), application.getStudyDivision(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_STUDY_AREA), application.getStudyArea(), body);

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_REFERRAL_SOURCE),
                programDetailNull ? null : programDetail.getReferralSourceDisplay(), body);

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PRIMARY_THEME), application.getPrimaryThemeDisplay(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_SECONDARY_THEME), application.getSecondaryThemeDisplay(), body);

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
    }

    private void addSupervisorSection(Application application, Document pdfDocument) throws DocumentException {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_SUPERVISOR_HEADER));
        Set<ApplicationSupervisor> supervisors = application.getSupervisors();

        String subheader = propertyLoader.load(APPLICATION_SUPERVISOR_SUBHEADER);

        if (supervisors.isEmpty()) {
            applicationDownloadBuilderHelper.addContentRowMedium(subheader, null, body);
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        } else {
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);

            int counter = 1;
            for (ApplicationSupervisor supervisor : supervisors) {
                PdfPTable subBody = applicationDownloadBuilderHelper.startSubection(pdfDocument, subheader + "(" + counter++ + ")");

                User user = supervisor.getUser();

                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_FIRST_NAME), user.getFirstName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_LAST_NAME), user.getLastName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_EMAIL), user.getEmail(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_SUPERVISOR_AWARE_OF_APPLICATION),
                        propertyLoader.load(SYSTEM_YES, SYSTEM_NO, supervisor.getAcceptedSupervision()), subBody);

                applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
            }
        }
    }

    private void addPersonalDetailSection(ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws DocumentException {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_PERSONAL_DETAIL_HEADER));
        Application application = applicationDownloadDTO.getApplication();

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

        if (applicationDownloadDTO.isIncludeEqualOpportunitiesData()) {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_ETHNICITY), personalDetailNull ? null
                    : personalDetail.getEthnicityDisplay(), body);
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_DISABILITY), personalDetailNull ? null
                    : personalDetail.getDisabilityDisplay(), body);
        }

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_REQUIRE_VISA),
                propertyLoader.load(SYSTEM_YES, SYSTEM_NO, personalDetail.getVisaRequired()), body);

        boolean passportAvailable = personalDetailNull ? false : personalDetail.getPassportAvailable();

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_PASSPORT_AVAILABLE),
                propertyLoader.load(SYSTEM_YES, SYSTEM_NO, personalDetail.getPassportAvailable()), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_WORK_LANGUAGE_FIRST_LANGUAGE),
                personalDetailNull ? null : propertyLoader.load(SYSTEM_YES, SYSTEM_NO, personalDetail.getFirstLanguageLocale()), body);

        boolean languageQualificationAvailable = personalDetailNull ? false : personalDetail.getLanguageQualificationAvailable();

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PERSONAL_DETAIL_LANGUAGE_QUALIFICATION_AVAILABLE),
                personalDetailNull ? null : propertyLoader.load(SYSTEM_YES, SYSTEM_NO, languageQualificationAvailable), body);

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);

        if (passportAvailable) {
            addPassport(pdfDocument, personalDetail.getPassport());
        }

        if (languageQualificationAvailable) {
            addLanquageQualification(pdfDocument, applicationDownloadDTO, personalDetail.getLanguageQualification());
        }
    }

    private void addPassport(Document pdfDocument, ApplicationPassport passport) throws DocumentException {
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

    private void addLanquageQualification(Document pdfDocument, ApplicationDownloadDTO applicationDownloadDTO,
            ApplicationLanguageQualification languageQualification) throws DocumentException {
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
        addDocument(body, propertyLoader.load(APPLICATION_PROOF_OF_AWARD), languageQualification.getDocument(), applicationDownloadDTO.isIncludeAttachments());

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

    private void addQualificationSection(ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws DocumentException {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_QUALIFICATION_HEADER));
        Set<ApplicationQualification> qualifications = applicationDownloadDTO.getApplication().getQualifications();

        if (qualifications.isEmpty()) {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_SUBHEADER), null, body);
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        } else {
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);

            int counter = 1;
            for (ApplicationQualification qualification : qualifications) {
                PdfPTable subBody = applicationDownloadBuilderHelper.startSubection(pdfDocument, propertyLoader.load(APPLICATION_QUALIFICATION_SUBHEADER) + "("
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
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_QUALIFICATION_TITLE), qualification.getTitle(), subBody);
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
                addDocument(subBody, propertyLoader.load(APPLICATION_QUALIFICATION_FINAL_TRANSCRIPT,
                        PrismDisplayPropertyDefinition.APPLICATION_QUALIFICATION_INTERIM_TRANSCRIPT, completed), qualification.getDocument(),
                        applicationDownloadDTO.isIncludeAttachments());

                applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
            }
        }
    }

    private void addEmploymentSection(Application application, Document pdfDocument) throws DocumentException {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_HEADER));
        Set<ApplicationEmploymentPosition> positions = application.getEmploymentPositions();

        if (positions.isEmpty()) {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_SUBHEADER), null, body);
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        } else {
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);

            int counter = 1;
            for (ApplicationEmploymentPosition position : positions) {
                PdfPTable subBody = applicationDownloadBuilderHelper.startSubection(pdfDocument, propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_SUBHEADER)
                        + "(" + counter++ + ")");

                String dateFormat = propertyLoader.load(SYSTEM_DATE_FORMAT);

                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYER_NAME), position.getEmployerName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_EMPLOYER_ADDRESS),
                        position.getEmployerAddressLocation(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_POSITION_TITLE), position.getPosition(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_REMIT), position.getRemit(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_START_DATE), position.getStartDateDisplay(dateFormat),
                        subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYMENT_POSITION_IS_CURRENT),
                        propertyLoader.load(SYSTEM_YES, SYSTEM_NO, position.getCurrent()), subBody);
                applicationDownloadBuilderHelper
                        .addContentRowMedium(propertyLoader.load(APPLICATION_END_DATE), position.getEndDateDisplay(dateFormat), subBody);

                applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
            }
        }
    }

    private void addFundingSection(ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws DocumentException {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_FUNDING_HEADER));
        Set<ApplicationFunding> fundings = applicationDownloadDTO.getApplication().getFundings();

        if (fundings.isEmpty()) {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_FUNDING_SUBHEADER), null, body);
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        } else {
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);

            int counter = 1;
            for (ApplicationFunding funding : fundings) {
                PdfPTable subBody = applicationDownloadBuilderHelper.startSubection(pdfDocument, propertyLoader.load(APPLICATION_FUNDING_SUBHEADER) + "("
                        + counter++ + ")");
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_FUNDING_TYPE), funding.getFundingSourceDisplay(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_FUNDING_DESCRIPTION), funding.getDescription(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_FUNDING_VALUE), funding.getValue(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_FUNDING_AWARD_DATE),
                        funding.getAwardDateDisplay(propertyLoader.load(SYSTEM_DATE_FORMAT)), subBody);
                addDocument(subBody, propertyLoader.load(APPLICATION_PROOF_OF_AWARD), funding.getDocument(), applicationDownloadDTO.isIncludeAttachments());

                applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
            }
        }
    }

    private void addPrizesSection(ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws DocumentException {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_FUNDING_HEADER));
        Set<ApplicationPrize> prizes = applicationDownloadDTO.getApplication().getPrizes();

        if (prizes.isEmpty()) {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PRIZE_SUBHEADER), null, body);
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        } else {
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);

            int counter = 1;
            for (ApplicationPrize prize : prizes) {
                PdfPTable subBody = applicationDownloadBuilderHelper.startSubection(pdfDocument, propertyLoader.load(APPLICATION_PRIZE_SUBHEADER) + "("
                        + counter++ + ")");
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PRIZE_PROVIDER), prize.getProvider(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PRIZE_TITLE), prize.getTitle(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PRIZE_DESCRIPTION), prize.getDescription(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_PRIZE_AWARD_DATE),
                        prize.getAwardDateDisplay(propertyLoader.load(SYSTEM_DATE_FORMAT)), subBody);

                applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
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
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);

            int counter = 1;
            for (ApplicationReferee referee : referees) {
                PdfPTable subBody = applicationDownloadBuilderHelper.startSubection(pdfDocument, propertyLoader.load(APPLICATION_REFEREE_SUBHEADER) + "("
                        + counter++ + ")");

                User user = referee.getUser();
                boolean userNull = user == null;

                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_FIRST_NAME), userNull ? null : user.getFirstName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_LAST_NAME), userNull ? null : user.getLastName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_EMAIL), userNull ? null : user.getEmail(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_ADDRESS), referee.getAddressDisplay(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_TELEPHONE), referee.getPhone(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_SKYPE), referee.getSkype(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_EMPLOYER_NAME), referee.getJobEmployer(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_POSITION_TITLE), referee.getJobTitle(), subBody);

                if (applicationDownloadDTO.isIncludeReferences()) {
                    subBody.addCell(applicationDownloadBuilderHelper.newContentCellMedium(propertyLoader.load(APPLICATION_REFEREE_REFERENCE_APPENDIX)));
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
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_DOCUMENT_HEADER));
        ApplicationDocument documentSection = applicationDownloadDTO.getApplication().getDocument();

        boolean includeAttachments = applicationDownloadDTO.isIncludeAttachments();
        addDocument(body, propertyLoader.load(APPLICATION_DOCUMENT_PERSONAL_STATEMENT_APPENDIX),
                documentSection == null ? null : documentSection.getPersonalStatement(), includeAttachments);
        addDocument(body, propertyLoader.load(APPLICATION_DOCUMENT_CV_APPENDIX), documentSection == null ? null : documentSection.getCv(), includeAttachments);

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
    }

    private void addAdditionalInformationSection(ApplicationDownloadDTO applicationDownloadDTO, Document pdfDocument) throws DocumentException {
        if (applicationDownloadDTO.isIncludeEqualOpportunitiesData()) {
            PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_ADDITIONAL_INFORMATION_HEADER));
            ApplicationAdditionalInformation additionalInformation = applicationDownloadDTO.getApplication().getAdditionalInformation();

            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_ADDITIONAL_INFORMATION_CONVICTION),
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
                pdfDocument.add(new Chunk(propertyLoader.load(SYSTEM_APPENDIX) + "(" + (i + 1) + ")").setLocalDestination(new Integer(i).toString()));
                if (object.getClass().equals(com.zuehlke.pgadmissions.domain.document.Document.class)) {
                    com.zuehlke.pgadmissions.domain.document.Document document = (com.zuehlke.pgadmissions.domain.document.Document) object;

                    if (document != null) {
                        if (document.getApplicationLanguageQualification() != null) {
                            pdfDocument.add(new Chunk(" - " + propertyLoader.load(APPLICATION_LANGUAGE_QUALIFICATION_APPENDIX)));
                        } else if (document.getApplicationQualification() != null) {
                            pdfDocument.add(new Chunk(" - " + propertyLoader.load(APPLICATION_QUALIFICATION_APPENDIX)));
                        } else if (document.getApplicationFunding() != null) {
                            pdfDocument.add(new Chunk(" - " + propertyLoader.load(APPLICATION_FUNDING_APPENDIX)));
                        } else if (document.getApplicationPersonalStatement() != null) {
                            pdfDocument.add(new Chunk(" - " + propertyLoader.load(APPLICATION_DOCUMENT_PERSONAL_STATEMENT_APPENDIX)));
                        } else if (document.getApplicationCv() != null) {
                            pdfDocument.add(new Chunk(" - " + propertyLoader.load(APPLICATION_DOCUMENT_CV_APPENDIX)));
                        }

                        try {
                            addDocument(pdfDocument, document, pdfWriter);
                        } catch (Exception e) {
                            LOGGER.warn("Error reading PDF document", e.getMessage());
                        }
                    }
                } else if (object.getClass().equals(Comment.class)) {
                    Comment referenceComment = (Comment) object;
                    pdfDocument.add(new Chunk(" - " + propertyLoader.load(APPLICATION_REFEREE_REFERENCE_APPENDIX)));

                    pdfDocument.add(applicationDownloadBuilderHelper.newSectionSeparator());

                    PdfPTable subBody = applicationDownloadBuilderHelper
                            .startSubection(pdfDocument, propertyLoader.load(APPLICATION_REFEREE_REFERENCE_COMMENT));
                    applicationContext.getBean(ApplicationDownloadReferenceBuilder.class).localize(propertyLoader, applicationDownloadBuilderHelper)
                            .addReferenceComment(pdfDocument, subBody, pdfWriter, applicationDownloadDTO.getApplication(), referenceComment);
                }
            }
        }
    }

    private void addDocument(Document pdfDocument, com.zuehlke.pgadmissions.domain.document.Document document, PdfWriter pdfWriter) throws IOException {
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
        applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(INSTITUTION_HEADER), application.getInstitutionDisplay(), fontSize, table);
        applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(PROGRAM_HEADER), application.getProgramDisplay(), fontSize, table);
        applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(PROJECT_HEADER), application.getProjectDisplay(), fontSize, table);

        ApplicationProgramDetail programDetail = application.getProgramDetail();
        applicationDownloadBuilderHelper.addContentRow(propertyLoader.load(PROGRAM_STUDY_OPTION),
                programDetail == null ? null : programDetail.getStudyOptionDisplay(), fontSize, table);
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

    private void addDocument(PdfPTable table, String rowTitle, com.zuehlke.pgadmissions.domain.document.Document document, boolean includeAttachments) {
        applicationDownloadBuilderHelper.newTitleCellLarge(rowTitle);
        if (includeAttachments) {
            if (document == null) {
                table.addCell(applicationDownloadBuilderHelper.newContentCellMedium(null));
            } else {
                addBookmark(table, document);
            }
        } else {
            table.addCell(applicationDownloadBuilderHelper.newContentCellMedium(document == null ? null : propertyLoader.load(SYSTEM_VALUE_PROVIDED)));
        }
    }

    private void addBookmark(PdfPTable table, Object object) {
        int index = bookmarks.size();
        table.addCell(applicationDownloadBuilderHelper.newBookmarkCellMedium(propertyLoader.load(SYSTEM_SEE) + " " + propertyLoader.load(SYSTEM_APPENDIX)
                + (index + 1) + ")", index));
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
