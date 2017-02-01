package uk.co.alumeni.prism.services.builders.download;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.events.PdfPageEventForwarder;
import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.definitions.*;
import uk.co.alumeni.prism.exceptions.IntegrationException;
import uk.co.alumeni.prism.exceptions.PdfDocumentBuilderException;
import uk.co.alumeni.prism.rest.representation.DocumentRepresentation;
import uk.co.alumeni.prism.rest.representation.address.AddressRepresentation;
import uk.co.alumeni.prism.rest.representation.comment.CommentCompetenceGroupRepresentation;
import uk.co.alumeni.prism.rest.representation.comment.CommentRepresentation;
import uk.co.alumeni.prism.rest.representation.profile.*;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationRelation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationSimple;
import uk.co.alumeni.prism.rest.representation.resource.application.ApplicationOfferRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.application.ApplicationProgramDetailRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.application.ApplicationRepresentationExtended;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;
import uk.co.alumeni.prism.services.DocumentService;
import uk.co.alumeni.prism.services.builders.download.ApplicationDownloadBuilderConfiguration.ApplicationDownloadBuilderFontSize;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static uk.co.alumeni.prism.PrismConstants.COLON;
import static uk.co.alumeni.prism.PrismConstants.SPACE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROJECT;
import static uk.co.alumeni.prism.services.builders.download.ApplicationDownloadBuilderConfiguration.ApplicationDownloadBuilderFontSize.MEDIUM;
import static uk.co.alumeni.prism.utils.PrismStringUtils.getBigDecimalAsString;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ApplicationDownloadBuilder {
    
    private PropertyLoader propertyLoader;
    
    private final Map<String, Bookmark<?>> bookmarks = Maps.newLinkedHashMap();
    
    private ApplicationDownloadBuilderHelper applicationDownloadBuilderHelper;

    @Value("${export.logo.file.width.percentage}")
    private Float logoFileWidthPercentage;

    @Inject
    private DocumentService documentService;

    public void build(ApplicationRepresentationExtended application, Document pdfDocument, PdfWriter writer) throws PdfDocumentBuilderException {
        try {
            addCoverPage(application, pdfDocument, writer);
            writer.setPageEvent(new NewPageEvent().withApplication(application));
            addProgramSection(application, pdfDocument);
            addPersonalDetailSection(application, pdfDocument);
            addAddressSection(application, pdfDocument);
            addQualificationSection(application, pdfDocument);
            addAwardSection(application, pdfDocument);
            addEmploymentSection(application, pdfDocument);
            addReferencesSection(application, pdfDocument);
            addDocumentSection(application, pdfDocument);
            addAdditionalInformationSection(application, pdfDocument);
            addSupportingDocuments(pdfDocument, writer);
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
                    propertyLoader.loadLazy(APPLICATION_PROGRAM_DETAIL_START_DATE_LABEL, APPLICATION_CONFIRMED_START_DATE, confirmedStartDate == null),
                    confirmedStartDate == null ? startDate.toString(dateFormat) : confirmedStartDate.toString(dateFormat), body);

            addThemeSection(programDetail, body);
            addLocationSection(programDetail, body);
        }

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
    }

    public void addThemeSection(ApplicationProgramDetailRepresentation programDetail, PdfPTable body) {
        String themes = programDetail.getThemesDisplay();
        if (themes != null) {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(APPLICATION_PROGRAM_DETAIL_THEMES_HEADER), themes, body);
        }
    }

    public void addLocationSection(ApplicationProgramDetailRepresentation programDetail, PdfPTable body) {
        String locations = programDetail.getLocationsDisplay();
        if (locations != null) {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(APPLICATION_PROGRAM_DETAIL_LOCATIONS_HEADER), locations, body);
        }
    }

    private void addPersonalDetailSection(ApplicationRepresentationExtended application, Document pdfDocument) throws Exception {
        PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.loadLazy(PROFILE_PERSONAL_DETAIL_HEADER));

        UserRepresentationSimple applicationCreator = application.getUser();
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(SYSTEM_FIRST_NAME), applicationCreator.getFirstName(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(SYSTEM_FIRST_NAME_2), applicationCreator.getFirstName2(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(SYSTEM_FIRST_NAME_3), applicationCreator.getFirstName3(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(SYSTEM_LAST_NAME), applicationCreator.getLastName(), body);
        applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(SYSTEM_EMAIL), applicationCreator.getEmail(), body);

        ProfilePersonalDetailRepresentation personalDetail = application.getPersonalDetail();
        if (personalDetail != null) {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_PERSONAL_DETAIL_TELEPHONE_LABEL), personalDetail.getPhone(),
                    body);
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_PERSONAL_DETAIL_SKYPE_LABEL), personalDetail.getSkype(), body);

            PrismGender gender = personalDetail.getGender();
            if (gender != null) {
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_PERSONAL_DETAIL_GENDER_LABEL),
                        propertyLoader.loadLazy(gender.getDisplayProperty()), body);
            }

            LocalDate dateOfBirth = personalDetail.getDateOfBirth();
            if (dateOfBirth != null) {
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_PERSONAL_DETAIL_DATE_OF_BIRTH_LABEL),
                        dateOfBirth.toString(propertyLoader.loadLazy(SYSTEM_DATE_FORMAT)), body);
            }

            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_PERSONAL_DETAIL_NATIONALITY_LABEL),
                    propertyLoader.loadLazy(personalDetail.getNationality().getDisplayProperty()), body);
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_PERSONAL_DETAIL_DOMICILE_LABEL),
                    propertyLoader.loadLazy(personalDetail.getDomicile().getDisplayProperty()), body);

            PrismEthnicity ethnicity = personalDetail.getEthnicity();
            if (ethnicity != null) {
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_PERSONAL_DETAIL_ETHNICITY_LABEL),
                        propertyLoader.loadLazy(ethnicity.getDisplayProperty()), body);
            }

            PrismDisability disability = personalDetail.getDisability();
            if (disability != null) {
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_PERSONAL_DETAIL_DISABILITY_LABEL),
                        propertyLoader.loadLazy(disability.getDisplayProperty()), body);
            }

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
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_ADDRESS_CURRENT_HEADER), address == null ? null
                        : getAddressDisplayString(currentAddress), body);
            }

            AddressRepresentation contactAddress = address.getContactAddress();
            if (contactAddress != null) {
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_ADDRESS_CONTACT_HEADER), address == null ? null
                        : getAddressDisplayString(contactAddress), body);
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
                PdfPTable subBody = applicationDownloadBuilderHelper.startSubSection(propertyLoader.loadLazy(PROFILE_QUALIFICATION_SUBHEADER) + "(" + counter++
                        + ")");

                ResourceRepresentationRelation resource = qualification.getResource().getResource();
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_QUALIFICATION_PROVIDER_PROGRAM_LABEL),
                        resource.getDisplayName(), subBody);

                if (resource.getScope().equals(PROJECT)) {
                    applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(SYSTEM_HTML_RESOURCE_FAMILY_QUALIFICATION_DESCRIPTION_LABEL),
                            resource.getAdvert().getSummary(), subBody);
                }

                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_QUALIFICATION_START_DATE_LABEL),
                        qualification.getStartDateDisplay(), subBody);

                boolean completed = BooleanUtils.isTrue(qualification.getCompleted());

                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_QUALIFICATION_COMPLETED_LABEL),
                        propertyLoader.loadLazy(SYSTEM_YES, SYSTEM_NO, completed), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(
                        propertyLoader.loadLazy(PROFILE_QUALIFICATION_CONFIRMED_RESULT_LABEL, PROFILE_QUALIFICATION_EXPECTED_RESULT_LABEL, completed),
                        qualification.getGrade(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(
                        propertyLoader.loadLazy(PROFILE_QUALIFICATION_CONFIRMED_AWARD_DATE_LABEL, PROFILE_QUALIFICATION_EXPECTED_AWARD_DATE_LABEL, completed),
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

    private void addAwardSection(ApplicationRepresentationExtended application, Document pdfDocument) throws Exception {
        List<ProfileAwardRepresentation> awards = application.getAwards();

        if (!awards.isEmpty()) {
            applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.loadLazy(PROFILE_AWARD_HEADER));

            int counter = 1;
            for (ProfileAwardRepresentation award : awards) {
                PdfPTable subBody = applicationDownloadBuilderHelper.startSubSection(propertyLoader.loadLazy(PROFILE_AWARD_SUBHEADER) + "(" + counter++ + ")");

                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_AWARD_NAME_LABEL), award.getName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_AWARD_DESCRIPTION_LABEL), award.getDescription(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_AWARD_DATE_LABEL), award.getAwardDateDisplay(), subBody);

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

                ResourceRepresentationRelation resource = position.getResource().getResource();
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_EMPLOYMENT_POSITION_EMPLOYER_LABEL),
                        resource.getDisplayName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(SYSTEM_HTML_RESOURCE_FAMILY_QUALIFICATION_DESCRIPTION_LABEL),
                        resource.getAdvert().getSummary(), subBody);

                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_EMPLOYMENT_POSITION_START_DATE_LABEL),
                        position.getStartDateDisplay(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_EMPLOYMENT_POSITION_CURRENT_LABEL),
                        propertyLoader.loadLazy(SYSTEM_YES, SYSTEM_NO, position.getCurrent()), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_EMPLOYMENT_POSITION_END_DATE_LABEL),
                        position.getEndDateDisplay(), subBody);

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
                applicationDownloadBuilderHelper.addContentRowMedium(
                        propertyLoader.loadLazy(SYSTEM_HTML_GENERAL_FIELD_LAST_NAME_LABEL), userNull ? null : user.getLastName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(SYSTEM_EMAIL), userNull ? null : user.getEmail(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(
                        propertyLoader.loadLazy(PROFILE_REFEREE_POSITION_EMPLOYER_LABEL), referee.getResource().getResource().getDisplayName(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_PERSONAL_DETAIL_TELEPHONE_LABEL), referee.getPhone(), subBody);
                applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_PERSONAL_DETAIL_SKYPE_LABEL), referee.getSkype(), subBody);

                CommentRepresentation referenceComment = referee.getComment();
                if (referenceComment != null) {
                    addBookmark(application, new Bookmark<CommentRepresentation>().withLabel(PROFILE_REFEREE_REFERENCE_APPENDIX).withContent(referenceComment),
                            subBody, propertyLoader.loadLazy(PROFILE_REFEREE_REFERENCE_APPENDIX));
                }

                applicationDownloadBuilderHelper.closeSection(pdfDocument, subBody);
            }
        }
    }

    private void addDocumentSection(ApplicationRepresentationExtended application, Document pdfDocument) throws Exception {
        ProfileDocumentRepresentation document = application.getDocument();

        if (document != null) {
            PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.loadLazy(PROFILE_DOCUMENT_HEADER));

            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(APPLICATION_DOCUMENT_PERSONAL_SUMMARY_LABEL),
                    document.getPersonalSummary(), body);

            DocumentRepresentation cv = document.getCv();
            if (cv != null) {
                addBookmark(application, new Bookmark<DocumentRepresentation>().withLabel(PROFILE_DOCUMENT_CV_LABEL).withContent(cv), body,
                        propertyLoader.loadLazy(PROFILE_DOCUMENT_CV_LABEL));
            }

            DocumentRepresentation coveringLetter = document.getCoveringLetter();
            if (coveringLetter != null) {
                addBookmark(application, new Bookmark<DocumentRepresentation>().withLabel(PROFILE_DOCUMENT_COVERING_LETTER_LABEL).withContent(coveringLetter),
                        body, propertyLoader.loadLazy(PROFILE_DOCUMENT_COVERING_LETTER_LABEL));
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

    private void addSupportingDocuments(Document pdfDocument, PdfWriter pdfWriter) throws Exception {
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
            } else if (content instanceof CommentRepresentation) {
                CommentRepresentation commentRepresentation = (CommentRepresentation) content;
                pdfDocument.add(buildTarget(PROFILE_REFEREE_REFERENCE_APPENDIX, anchor));

                pdfDocument.add(applicationDownloadBuilderHelper.newSectionSeparator());
                addReferenceComment(pdfDocument, commentRepresentation);

                List<CommentCompetenceGroupRepresentation> competenceGroupRepresentations = commentRepresentation.getCompetenceGroups();
                if (isNotEmpty(competenceGroupRepresentations)) {
                    addReferenceCommentAssessmentCriteria(pdfDocument, competenceGroupRepresentations);
                }

                List<DocumentRepresentation> documents = commentRepresentation.getDocuments();
                if (isNotEmpty(documents)) {
                    for (DocumentRepresentation document : documents) {
                        addDocument(document, pdfDocument, pdfWriter);
                    }
                }
            }

            index++;
        }
    }

    private void addReferenceComment(Document pdfDocument, CommentRepresentation commentRepresentation) throws Exception {
        PdfPTable body = applicationDownloadBuilderHelper.startSubSection(propertyLoader.loadLazy(PROFILE_REFEREE_REFERENCE_COMMENT));

        String commentHeader = propertyLoader.loadLazy(SYSTEM_COMMENT_HEADER);
        if (commentRepresentation.getDeclinedResponse()) {
            applicationDownloadBuilderHelper.addContentRowMedium(commentHeader, propertyLoader.loadLazy(APPLICATION_COMMENT_DECLINED_REFEREE), body);
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        } else {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_REFEREE_SUBHEADER), commentRepresentation.getUser()
                    .getFullName(), body);
            applicationDownloadBuilderHelper.addContentRowMedium(commentHeader, commentRepresentation.getContent(), body);

            BigDecimal rating = commentRepresentation.getRating();
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(SYSTEM_RATING), rating == null ? null : rating.toPlainString(), body);
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        }
    }

    private void addReferenceCommentAssessmentCriteria(Document pdfDocument, List<CommentCompetenceGroupRepresentation> competenceGroupRepresentations) throws Exception {
        Joiner joiner = Joiner.on(COLON + SPACE).skipNulls();

        PdfPTable body = applicationDownloadBuilderHelper.startSubSection(propertyLoader.loadLazy(SYSTEM_RESOURCE_COMPETENCES_HEADER));
        competenceGroupRepresentations.stream().forEach(
                competenceGroupRepresentation -> {
                    competenceGroupRepresentation
                            .getCompetences()
                            .stream()
                            .forEach(
                                    competenceRepresentation -> {
                                        applicationDownloadBuilderHelper.addContentRowMedium(competenceRepresentation.getName(),
                                                joiner.join(competenceRepresentation.getRating().toString(),
                                                        competenceRepresentation.getRemark()), body);
                                    });
                });

        applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
    }

    private Phrase buildTarget(PrismDisplayPropertyDefinition title, Anchor anchor) throws Exception {
        Phrase phrase = new Phrase(" - " + propertyLoader.loadLazy(title));
        phrase.add(anchor);
        return phrase;
    }

    private void addDocument(DocumentRepresentation document, Document pdfDocument, PdfWriter pdfWriter) throws IOException, IntegrationException {
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
            applicationDownloadBuilderHelper.addContentRow(propertyLoader.loadLazy(APPLICATION_PROGRAM_DETAIL_STUDY_OPTION_LABEL), studyOption == null ? null
                    : propertyLoader.loadLazy(studyOption.getDisplayProperty()), fontSize, table);
        }
    }

    private void addApplicationSummaryExtended(ApplicationRepresentationExtended application, PdfPTable table, ApplicationDownloadBuilderFontSize fontSize)
            throws Exception {
        addApplicationSummary(application, table, fontSize);

        String dateFormat = propertyLoader.loadLazy(SYSTEM_DATE_FORMAT);

        applicationDownloadBuilderHelper.addContentRow(propertyLoader.loadLazy(APPLICATION_CODE), application.getCode(), fontSize, table);

        LocalDate closingDate = application.getClosingDate();
        if (closingDate != null) {
            applicationDownloadBuilderHelper.addContentRow(propertyLoader.loadLazy(SYSTEM_CLOSING_DATE), closingDate.toString(dateFormat), fontSize, table);
        }

        DateTime submittedTimestamp = application.getSubmittedTimestamp();
        if (submittedTimestamp != null) {
            applicationDownloadBuilderHelper.addContentRow(propertyLoader.loadLazy(APPLICATION_SUBMISSION_DATE), submittedTimestamp.toString(dateFormat),
                    fontSize, table);
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
