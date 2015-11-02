package com.zuehlke.pgadmissions.services.builders.download;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_CODE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_CONFIRMED_START_DATE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_DOCUMENT_PERSONAL_SUMMARY_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_PROGRAM_DETAIL_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_PROGRAM_DETAIL_START_DATE_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_PROGRAM_DETAIL_STUDY_OPTION_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_SUBMISSION_DATE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_ADDITIONAL_INFORMATION_CONVICTION_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_ADDITIONAL_INFORMATION_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_ADDRESS_CONTACT_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_ADDRESS_CURRENT_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_DOCUMENT_COVERING_LETTER_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_DOCUMENT_CV_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_DOCUMENT_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_EMPLOYMENT_POSITION_CURRENT_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_EMPLOYMENT_POSITION_EMPLOYER_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_EMPLOYMENT_POSITION_END_DATE_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_EMPLOYMENT_POSITION_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_EMPLOYMENT_POSITION_START_DATE_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_EMPLOYMENT_POSITION_SUBHEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_PERSONAL_DETAIL_DATE_OF_BIRTH_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_PERSONAL_DETAIL_DOMICILE_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_PERSONAL_DETAIL_GENDER_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_PERSONAL_DETAIL_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_PERSONAL_DETAIL_NATIONALITY_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_PERSONAL_DETAIL_SKYPE_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_PERSONAL_DETAIL_TELEPHONE_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_QUALIFICATION_APPENDIX;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_QUALIFICATION_COMPLETED_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_QUALIFICATION_CONFIRMED_AWARD_DATE_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_QUALIFICATION_CONFIRMED_RESULT_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_QUALIFICATION_DOCUMENT_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_QUALIFICATION_EXPECTED_AWARD_DATE_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_QUALIFICATION_EXPECTED_RESULT_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_QUALIFICATION_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_QUALIFICATION_PROVIDER_PROGRAM_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_QUALIFICATION_START_DATE_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_QUALIFICATION_SUBHEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_REFEREE_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_REFEREE_POSITION_EMPLOYER_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_REFEREE_REFERENCE_APPENDIX;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_REFEREE_REFERENCE_COMMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_REFEREE_SUBHEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPENDIX;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_AVERAGE_RATING;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_CLOSING_DATE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DATE_FORMAT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_EMAIL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_FIRST_NAME;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_FIRST_NAME_2;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_FIRST_NAME_3;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_HTML_GENERAL_FIELD_LAST_NAME_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_LAST_NAME;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NO;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_PAGE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_SEE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_VALUE_PROVIDED;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_YES;
import static com.zuehlke.pgadmissions.services.builders.download.ApplicationDownloadBuilderConfiguration.ApplicationDownloadBuilderFontSize.MEDIUM;
import static com.zuehlke.pgadmissions.utils.PrismStringUtils.getBigDecimalAsString;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
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
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismDomicile;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;
import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.address.AddressRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.profile.ProfileAdditionalInformationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.profile.ProfileAddressRepresentation;
import com.zuehlke.pgadmissions.rest.representation.profile.ProfileDocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.profile.ProfileEmploymentPositionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.profile.ProfilePersonalDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.profile.ProfileQualificationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.profile.ProfileRefereeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationOfferRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationProgramDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.builders.download.ApplicationDownloadBuilderConfiguration.ApplicationDownloadBuilderFontSize;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ApplicationDownloadBuilder {

    private final Map<String, Bookmark<?>> bookmarks = Maps.newLinkedHashMap();
    private PropertyLoader propertyLoader;
    private ApplicationDownloadBuilderHelper applicationDownloadBuilderHelper;

    @Value("${export.logo.file.width.percentage}")
    private Float logoFileWidthPercentage;

    @Inject
    private DocumentService documentService;

    @Inject
    private ApplicationContext applicationContext;

    public void build(ApplicationRepresentationExtended application, Document pdfDocument, PdfWriter writer) throws PdfDocumentBuilderException {
        try {
            addCoverPage(application, pdfDocument, writer);
            writer.setPageEvent(new NewPageEvent().withApplication(application));
            addProgramSection(application, pdfDocument);
            addPersonalDetailSection(application, pdfDocument);
            addAddressSection(application, pdfDocument);
            addQualificationSection(application, pdfDocument);
            addEmploymentSection(application, pdfDocument);
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

    private void addCoverPage(ApplicationRepresentationExtended application, Document pdfDocument, PdfWriter writer) throws Exception {
        pdfDocument.newPage();
        addLogoImage(pdfDocument);

        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.drawLine(writer.getDirectContent(), pdfDocument.left(), pdfDocument.right(), pdfDocument.top() + 10f);

        pdfDocument.add(applicationDownloadBuilderHelper.newSectionSeparator());
        pdfDocument.add(applicationDownloadBuilderHelper.newSectionSeparator());

        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.loadLazy(APPLICATION_HEADER));

        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(APPLICATION_CREATOR), application.getUser().getFullName(), body);

        BigDecimal applicationRatingAverage = application.getApplicationRatingAverage();
        if (applicationRatingAverage != null) {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(SYSTEM_AVERAGE_RATING),
                    getBigDecimalAsString(application.getApplicationRatingAverage()), body);
        }

        addApplicationSummaryExtended(application, body, MEDIUM);
        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        pdfDocument.newPage();
    }

    private void addProgramSection(ApplicationRepresentationExtended application, Document pdfDocument) throws Exception {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.loadLazy(APPLICATION_PROGRAM_DETAIL_HEADER));
        addApplicationSummary(application, body, MEDIUM);

        ApplicationProgramDetailRepresentation programDetail = application.getProgramDetail();

        if (programDetail != null) {
            LocalDate startDate = programDetail.getStartDate();
            ApplicationOfferRepresentation offer = application.getOfferRecommendation();
            LocalDate confirmedStartDate = offer == null ? null : offer.getPositionProvisionalStartDate();

            String dateFormat = propertyLoader.loadLazy(SYSTEM_DATE_FORMAT);

            applicationDownloadBuilderHelper.addContentRowMedium(
                    propertyLoader.loadLazy(APPLICATION_PROGRAM_DETAIL_START_DATE_LABEL, APPLICATION_CONFIRMED_START_DATE,
                            confirmedStartDate == null),
                    confirmedStartDate == null ? startDate.toString(dateFormat) : confirmedStartDate.toString(dateFormat), body);
        }

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
    }

    private void addPersonalDetailSection(ApplicationRepresentationExtended application, Document pdfDocument)
            throws Exception {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.loadLazy(PROFILE_PERSONAL_DETAIL_HEADER));

        UserRepresentationSimple applicationCreator = application.getUser();
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(SYSTEM_FIRST_NAME), applicationCreator.getFirstName(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(SYSTEM_FIRST_NAME_2), applicationCreator.getFirstName2(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(SYSTEM_FIRST_NAME_3), applicationCreator.getFirstName3(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(SYSTEM_LAST_NAME), applicationCreator.getLastName(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(SYSTEM_EMAIL), applicationCreator.getEmail(), body);

        ProfilePersonalDetailRepresentation personalDetail = application.getPersonalDetail();
        if (personalDetail != null) {
            applicationDownloadBuilderHelper
                    .addContentRowMedium(propertyLoader.loadLazy(PROFILE_PERSONAL_DETAIL_TELEPHONE_LABEL), personalDetail.getPhone(), body);
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_PERSONAL_DETAIL_SKYPE_LABEL), personalDetail.getSkype(), body);

            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_PERSONAL_DETAIL_GENDER_LABEL),
                    propertyLoader.loadLazy(personalDetail.getGender().getDisplayProperty()), body);
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_PERSONAL_DETAIL_DATE_OF_BIRTH_LABEL),
                    personalDetail.getDateOfBirth().toString(propertyLoader.loadLazy(SYSTEM_DATE_FORMAT)), body);

            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_PERSONAL_DETAIL_NATIONALITY_LABEL),
                    propertyLoader.loadLazy(personalDetail.getNationality().getDisplayProperty()), body);
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_PERSONAL_DETAIL_DOMICILE_LABEL),
                    propertyLoader.loadLazy(personalDetail.getDomicile().getDisplayProperty()), body);

            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        }
    }

    private void addAddressSection(ApplicationRepresentationExtended application, Document pdfDocument) throws Exception {
        ProfileAddressRepresentation address = application.getAddress();

        if (address != null) {
            PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument,
                    propertyLoader.loadLazy(PrismDisplayPropertyDefinition.PROFILE_ADDRESS_HEADER));

            AddressRepresentation currentAddress = address.getCurrentAddress();
            if (currentAddress != null) {
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_ADDRESS_CURRENT_HEADER),
                        address == null ? null : getAddressDisplayString(currentAddress), body);
            }

            AddressRepresentation contactAddress = address.getContactAddress();
            if (contactAddress != null) {
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_ADDRESS_CONTACT_HEADER),
                        address == null ? null : getAddressDisplayString(contactAddress), body);
            }

            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        }
    }

    private String getAddressDisplayString(AddressRepresentation address) {
        PrismDomicile domicile = address.getDomicile();
        return domicile == null ? address.getLocationString() : address.getLocationString() + ", " + propertyLoader.loadLazy(domicile.getDisplayProperty());
    }

    private void addQualificationSection(ApplicationRepresentationExtended application, Document pdfDocument) throws Exception {
        List<ProfileQualificationRepresentation> qualifications = application.getQualifications();

        if (!qualifications.isEmpty()) {
            applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.loadLazy(PROFILE_QUALIFICATION_HEADER));

            int counter = 1;
            for (ProfileQualificationRepresentation qualification : qualifications) {
                PdfPTable subBody = applicationDownloadBuilderHelper.startSubSection(propertyLoader.loadLazy(PROFILE_QUALIFICATION_SUBHEADER) + "("
                        + counter++ + ")");

                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_QUALIFICATION_PROVIDER_PROGRAM_LABEL),
                        qualification.getResource().getResource().getDisplayName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_QUALIFICATION_START_DATE_LABEL),
                        qualification.getStartDateDisplay(), subBody);

                boolean completed = BooleanUtils.isTrue(qualification.getCompleted());

                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_QUALIFICATION_COMPLETED_LABEL),
                        propertyLoader.loadLazy(SYSTEM_YES, SYSTEM_NO, completed), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(
                        propertyLoader.loadLazy(PROFILE_QUALIFICATION_CONFIRMED_RESULT_LABEL, PROFILE_QUALIFICATION_EXPECTED_RESULT_LABEL, completed),
                        qualification.getGrade(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(
                        propertyLoader.loadLazy(PROFILE_QUALIFICATION_CONFIRMED_AWARD_DATE_LABEL, PROFILE_QUALIFICATION_EXPECTED_AWARD_DATE_LABEL,
                                completed),
                        qualification.getAwardDateDisplay(), subBody);

                DocumentRepresentation proofOfAward = qualification.getDocument();
                if (proofOfAward != null) {
                    addBookmark(application, new Bookmark<DocumentRepresentation>().withLabel(PROFILE_QUALIFICATION_APPENDIX).withContent(proofOfAward),
                            subBody, propertyLoader.loadLazy(PROFILE_QUALIFICATION_DOCUMENT_LABEL));
                }

                applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
            }
        }
    }

    private void addEmploymentSection(ApplicationRepresentationExtended application, Document pdfDocument) throws Exception {
        List<ProfileEmploymentPositionRepresentation> positions = application.getEmploymentPositions();

        if (!positions.isEmpty()) {
            applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.loadLazy(PROFILE_EMPLOYMENT_POSITION_HEADER));

            int counter = 1;
            for (ProfileEmploymentPositionRepresentation position : positions) {
                PdfPTable subBody = applicationDownloadBuilderHelper.startSubSection(propertyLoader.loadLazy(PROFILE_EMPLOYMENT_POSITION_SUBHEADER) + "("
                        + counter++ + ")");

                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_EMPLOYMENT_POSITION_EMPLOYER_LABEL),
                        position.getResource().getResource().getDisplayName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_EMPLOYMENT_POSITION_START_DATE_LABEL),
                        position.getStartDateDisplay(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_EMPLOYMENT_POSITION_CURRENT_LABEL),
                        propertyLoader.loadLazy(SYSTEM_YES, SYSTEM_NO, position.getCurrent()), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_EMPLOYMENT_POSITION_END_DATE_LABEL), position.getEndDateDisplay(),
                        subBody);

                applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
            }
        }
    }

    private void addReferencesSection(ApplicationRepresentationExtended application, Document pdfDocument) throws Exception {
        List<ProfileRefereeRepresentation> referees = application.getReferees();

        if (!referees.isEmpty()) {
            applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.loadLazy(PROFILE_REFEREE_HEADER));

            int counter = 1;
            for (ProfileRefereeRepresentation referee : referees) {
                PdfPTable subBody = applicationDownloadBuilderHelper
                        .startSubSection(propertyLoader.loadLazy(PROFILE_REFEREE_SUBHEADER) + "(" + counter++ + ")");

                UserRepresentationSimple user = referee.getResource().getUser();
                boolean userNull = user == null;

                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(SYSTEM_FIRST_NAME), userNull ? null : user.getFirstName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(SYSTEM_HTML_GENERAL_FIELD_LAST_NAME_LABEL),
                        userNull ? null : user.getLastName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(SYSTEM_EMAIL), userNull ? null : user.getEmail(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_REFEREE_POSITION_EMPLOYER_LABEL),
                        referee.getResource().getResource().getDisplayName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_PERSONAL_DETAIL_TELEPHONE_LABEL), referee.getPhone(),
                        subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_PERSONAL_DETAIL_SKYPE_LABEL), referee.getSkype(), subBody);

                CommentRepresentation referenceComment = referee.getComment();
                if (referenceComment != null) {
                    addBookmark(application,
                            new Bookmark<CommentRepresentation>().withLabel(PROFILE_REFEREE_REFERENCE_APPENDIX).withContent(referenceComment), subBody,
                            propertyLoader.loadLazy(PROFILE_REFEREE_REFERENCE_APPENDIX));
                }

                applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
            }
        }
    }

    private void addDocumentSection(ApplicationRepresentationExtended application, Document pdfDocument) throws Exception {
        ProfileDocumentRepresentation document = application.getDocument();

        if (document != null) {
            PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.loadLazy(PROFILE_DOCUMENT_HEADER));

            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(APPLICATION_DOCUMENT_PERSONAL_SUMMARY_LABEL), document.getPersonalSummary(), body);

            DocumentRepresentation cv = document.getCv();
            if (cv != null) {
                addBookmark(application, new Bookmark<DocumentRepresentation>().withLabel(PROFILE_DOCUMENT_CV_LABEL).withContent(cv), body,
                        propertyLoader.loadLazy(PROFILE_DOCUMENT_CV_LABEL));
            }

            DocumentRepresentation coveringLetter = document.getCoveringLetter();
            if (coveringLetter != null) {
                addBookmark(application,
                        new Bookmark<DocumentRepresentation>().withLabel(PROFILE_DOCUMENT_COVERING_LETTER_LABEL).withContent(coveringLetter), body,
                        propertyLoader.loadLazy(PROFILE_DOCUMENT_COVERING_LETTER_LABEL));
            }

            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        }
    }

    private void addAdditionalInformationSection(ApplicationRepresentationExtended application, Document pdfDocument) throws Exception {
        ProfileAdditionalInformationRepresentation additionalInformation = application.getAdditionalInformation();

        if (additionalInformation != null) {
            PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.loadLazy(PROFILE_ADDITIONAL_INFORMATION_HEADER));

            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_ADDITIONAL_INFORMATION_CONVICTION_LABEL),
                    additionalInformation.getConvictions(), body);

            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        }
    }

    private void addSupportingDocuments(ApplicationRepresentationExtended application, Document pdfDocument, PdfWriter pdfWriter) throws Exception {
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
            pdfDocument.add(new Chunk(propertyLoader.loadLazy(SYSTEM_APPENDIX) + "(" + (index + 1) + ")").setLocalDestination(bookmarkKey));
            if (content instanceof DocumentRepresentation) {
                DocumentRepresentation document = (DocumentRepresentation) content;
                pdfDocument.add(buildTarget(bookmark.getLabel(), anchor));
                addDocument(document, pdfDocument, pdfWriter);
            } else if (content instanceof Comment) {
                CommentRepresentation referenceComment = (CommentRepresentation) content;
                pdfDocument.add(buildTarget(PROFILE_REFEREE_REFERENCE_APPENDIX, anchor));

                pdfDocument.add(applicationDownloadBuilderHelper.newSectionSeparator());

                PdfPTable subBody = applicationDownloadBuilderHelper.startSubSection(propertyLoader.loadLazy(PROFILE_REFEREE_REFERENCE_COMMENT));
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
        Phrase phrase = new Phrase(" - " + propertyLoader.loadLazy(title));
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

    private void addApplicationSummary(ApplicationRepresentationExtended application, PdfPTable table, ApplicationDownloadBuilderFontSize fontSize)
            throws Exception {
        applicationDownloadBuilderHelper.addContentRow(propertyLoader.loadLazy(SYSTEM_INSTITUTION), application.getInstitution().getName(), fontSize, table);

        ResourceRepresentationSimple department = application.getDepartment();
        if (department != null) {
            applicationDownloadBuilderHelper.addContentRow(propertyLoader.loadLazy(SYSTEM_DEPARTMENT), department.getName(), fontSize, table);
        }

        ResourceRepresentationSimple program = application.getProgram();
        if (program != null) {
            applicationDownloadBuilderHelper.addContentRow(propertyLoader.loadLazy(SYSTEM_PROGRAM), program.getName(), fontSize, table);
        }

        ResourceRepresentationSimple project = application.getProject();
        if (project != null) {
            applicationDownloadBuilderHelper.addContentRow(propertyLoader.loadLazy(SYSTEM_PROJECT), project.getName(), fontSize, table);
        }

        ApplicationProgramDetailRepresentation programDetail = application.getProgramDetail();
        if (programDetail != null) {
            PrismStudyOption studyOption = programDetail.getStudyOption();
            applicationDownloadBuilderHelper.addContentRow(propertyLoader.loadLazy(APPLICATION_PROGRAM_DETAIL_STUDY_OPTION_LABEL),
                    studyOption == null ? null : propertyLoader.loadLazy(studyOption.getDisplayProperty()), fontSize, table);
        }
    }

    private void addApplicationSummaryExtended(ApplicationRepresentationExtended application, PdfPTable table, ApplicationDownloadBuilderFontSize fontSize)
            throws Exception {
        addApplicationSummary(application, table, fontSize);

        String dateFormat = propertyLoader.loadLazy(SYSTEM_DATE_FORMAT);

        applicationDownloadBuilderHelper.addContentRow(propertyLoader.loadLazy(APPLICATION_CODE), application.getCode(), fontSize, table);

        LocalDate closingDate = application.getClosingDate();
        if (closingDate != null) {
            applicationDownloadBuilderHelper
                    .addContentRow(propertyLoader.loadLazy(SYSTEM_CLOSING_DATE), closingDate.toString(dateFormat), fontSize, table);
        }

        DateTime submittedTimestamp = application.getSubmittedTimestamp();
        if (submittedTimestamp != null) {
            applicationDownloadBuilderHelper.addContentRow(propertyLoader.loadLazy(APPLICATION_SUBMISSION_DATE), submittedTimestamp.toString(dateFormat), fontSize,
                    table);
        }
    }

    private <T> void addBookmark(ApplicationRepresentationExtended application, Bookmark<T> bookmark, PdfPTable table, String rowTitle) throws Exception {
        table.addCell(applicationDownloadBuilderHelper.newTitleCellMedium(rowTitle));
        T content = bookmark.getContent();
        if (content == null) {
            table.addCell(applicationDownloadBuilderHelper.newContentCellMedium(content == null ? null : propertyLoader.loadLazy(SYSTEM_VALUE_PROVIDED)));
        } else {
            int index = bookmarks.size();
            String anchor = application.getCode() + "-" + index;
            table.addCell(applicationDownloadBuilderHelper.newBookmarkCellMedium(
                    propertyLoader.loadLazy(SYSTEM_SEE) + " " + propertyLoader.loadLazy(SYSTEM_APPENDIX) + " (" + (index + 1) + ")", anchor));
            bookmarks.put(anchor, bookmark);
        }
    }

    private void addLogoImage(Document pdfDocument) throws IOException, DocumentException {
        Image logoImage = applicationDownloadBuilderHelper.newLogoImage();
        logoImage.setAbsolutePosition(pdfDocument.right() - logoImage.getScaledWidth(), pdfDocument.top() + 20f);
        pdfDocument.add(logoImage);
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

    private class NewPageEvent extends PdfPageEventForwarder {

        private ApplicationRepresentationExtended application;

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
            Phrase footerPhrase = new Phrase(propertyLoader.loadLazy(SYSTEM_PAGE) + " " + (1 + document.getPageNumber()),
                    ApplicationDownloadBuilderConfiguration.getFont(ApplicationDownloadBuilderFontSize.SMALL));
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, footerPhrase, document.left(), document.bottom() - 25f, 0);
        }

        public void setApplyHeaderFooter(boolean applyHeader) {
            this.applyHeaderFooter = applyHeader;
        }

        public NewPageEvent withApplication(ApplicationRepresentationExtended application) {
            this.application = application;
            return this;
        }

    }

}
