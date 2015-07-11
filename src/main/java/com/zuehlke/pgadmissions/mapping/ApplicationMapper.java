package com.zuehlke.pgadmissions.mapping;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.address.AddressApplication;
import com.zuehlke.pgadmissions.domain.application.*;
import com.zuehlke.pgadmissions.domain.comment.*;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ApplicationProcessingSummaryDTO;
import com.zuehlke.pgadmissions.dto.UserSelectionDTO;
import com.zuehlke.pgadmissions.rest.representation.address.AddressApplicationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.*;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationSummaryRepresentation.DocumentSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationSummaryRepresentation.EmploymentPositionSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationSummaryRepresentation.QualificationSummaryRepresentation;
import com.zuehlke.pgadmissions.services.*;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import uk.co.alumeni.prism.api.model.imported.ImportedEntityResponseDefinition;
import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.utils.PrismConversionUtils.doubleToBigDecimal;
import static com.zuehlke.pgadmissions.utils.PrismConversionUtils.longToInteger;

@Service
@Transactional
public class ApplicationMapper {

    @Inject
    private AddressMapper addressMapper;

    @Inject
    private AdvertMapper advertMapper;

    @Inject
    private AdvertService advertService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private CommentMapper commentMapper;

    @Inject
    private CommentService commentService;

    @Inject
    private DocumentMapper documentMapper;

    @Inject
    private ImportedEntityMapper importedEntityMapper;

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private ResourceService resourceService;

    @Inject
    private UserMapper userMapper;

    @Inject
    private UserService userService;

    @Inject
    private ApplicationContext applicationContext;

    public ApplicationRepresentationClient getApplicationRepresentationClient(Application application) throws Exception {
        ApplicationRepresentationClient representation = getApplicationRepresentation(application, null, ApplicationRepresentationClient.class);

        representation.setPossibleThemes(advertService.getAdvertThemes(application.getAdvert()));

        Resource parent = application.getParentResource();
        if (ResourceOpportunity.class.isAssignableFrom(parent.getClass())) {
            ResourceOpportunity opportunity = (ResourceOpportunity) parent;
            List<ImportedEntityResponse> studyOptions = resourceService.getStudyOptions(opportunity).stream().map(studyOption -> (ImportedEntityResponse) importedEntityMapper.getImportedEntityRepresentation(studyOption)).collect(Collectors.toList());
            representation.setPossibleStudyOptions(studyOptions);
            representation.setPossibleLocations(resourceService.getStudyLocations(opportunity));
        }

        List<UserSelectionDTO> usersInterested = userService.getUsersInterestedInApplication(application);
        representation.setUsersInterestedInApplication(userMapper.getUserRepresentations(usersInterested));
        representation.setUsersPotentiallyInterestedInApplication(userMapper.getUserRepresentations(userService.getUsersPotentiallyInterestedInApplication(
                application, usersInterested)));

        representation.setInterview(getApplicationInterviewRepresentation(application));
        representation.setOfferRecommendation(getApplicationOfferRecommendationRepresentation(application));
        representation.setAssignedSupervisors(getApplicationSupervisorRepresentations(application));

        representation.setResourceSummary(getApplicationSummary(application));
        representation.setRecommendedAdverts(advertMapper.getRecommendedAdvertRepresentations(application));
        return representation;
    }

    public ApplicationRepresentationExport getApplicationRepresentationExport(Application application) throws Exception {
        return (ApplicationRepresentationExport) getApplicationRepresentation(application, application.getInstitution(), ApplicationRepresentationExport.class);
    }

    public <T extends ApplicationProcessingSummaryRepresentation> T getApplicationProcessingSummaryRepresentation(
            ApplicationProcessingSummaryDTO applicationProcessingSummary, Class<T> returnType) {
        T representation = BeanUtils.instantiate(returnType);

        representation.setAdvertCount(longToInteger(applicationProcessingSummary.getAdvertCount()));
        representation.setSubmittedApplicationCount(longToInteger(applicationProcessingSummary.getSubmittedApplicationCount()));
        representation.setApprovedApplicationCount(longToInteger(applicationProcessingSummary.getApprovedApplicationCount()));
        representation.setRejectedApplicationCount(longToInteger(applicationProcessingSummary.getRejectedApplicationCount()));
        representation.setWithdrawnApplicationCount(longToInteger(applicationProcessingSummary.getWithdrawnApplicationCount()));
        representation.setSubmittedApplicationRatio(doubleToBigDecimal(applicationProcessingSummary.getSubmittedApplicationRatio(), 2));
        representation.setApprovedApplicationRatio(doubleToBigDecimal(applicationProcessingSummary.getApprovedApplicationRatio(), 2));
        representation.setRejectedApplicationRatio(doubleToBigDecimal(applicationProcessingSummary.getRejectedApplicationRatio(), 2));
        representation.setWithdrawnApplicationRatio(doubleToBigDecimal(applicationProcessingSummary.getWithdrawnApplicationRatio(), 2));
        representation.setAverageRating(doubleToBigDecimal(applicationProcessingSummary.getAverageRating(), 2));
        representation.setAverageProcessingTime(doubleToBigDecimal(applicationProcessingSummary.getAverageProcessingTime(), 2));

        return representation;
    }

    private <T extends ApplicationRepresentation> T getApplicationRepresentation(Application application, Institution institution, Class<T> returnType)
            throws Exception {
        T representation = resourceMapper.getResourceRepresentationExtended(application, returnType);

        representation.setClosingDate(application.getClosingDate());
        representation.setSubmittedTimestamp(application.getSubmittedTimestamp());
        representation.setPreviousApplication(application.getPreviousApplication());
        representation.setProgramDetail(getApplicationProgramDetailRepresentation(application, institution));
        representation.setStudyDetail(getApplicationStudyDetailRepresentation(application));
        representation.setPrimaryThemes(getApplicationThemeRepresentation(application.getPrimaryTheme()));
        representation.setSecondaryThemes(getApplicationThemeRepresentation(application.getSecondaryTheme()));
        representation.setSupervisors(getApplicationSupervisorsRepresentation(application));
        representation.setPersonalDetail(getApplicationPersonalDetailRepresentation(application, institution));
        representation.setAddress(getApplicationAddressRepresentation(application, institution));
        representation.setQualifications(getApplicationQualificationRepresentations(application, institution));
        representation.setEmploymentPositions(getApplicationEmploymentPositionRepresentations(application, institution));
        representation.setFundings(getApplicationFundingRepresentations(application, institution));
        representation.setPrizes(getApplicationPrizeRepresentations(application, institution));
        representation.setReferees(getApplicationRefereeRepresentations(application, institution));
        representation.setDocument(getApplicationDocumentRepresentation(application));
        representation.setAdditionalInformation(getApplicationAdditionalInformationRepresentation(application));

        return representation;
    }

    private ApplicationProgramDetailRepresentation getApplicationProgramDetailRepresentation(Application application,
                                                                                             Institution institution) {
        ApplicationProgramDetail applicationProgramDetail = application.getProgramDetail();
        if (applicationProgramDetail != null) {
            return new ApplicationProgramDetailRepresentation()
                    .withStudyOption(getImportedEntityRepresentation(applicationProgramDetail.getStudyOption(), institution))
                    .withStartDate(applicationProgramDetail.getStartDate())
                    .withReferralSource(getImportedEntityRepresentation(applicationProgramDetail.getReferralSource(), institution));
        }
        return null;
    }

    private ApplicationStudyDetailRepresentation getApplicationStudyDetailRepresentation(Application application) {
        ApplicationStudyDetail applicationStudyDetail = application.getStudyDetail();

        if (applicationStudyDetail != null) {
            return new ApplicationStudyDetailRepresentation().withStudyLocation(applicationStudyDetail.getStudyLocation()).withStudyDivision(
                    applicationStudyDetail.getStudyDivision()).withStudyArea(applicationStudyDetail.getStudyArea())
                    .withStudyApplicationId(applicationStudyDetail.getStudyApplicationId()).withStudyStartDate(applicationStudyDetail.getStudyStartDate());
        }

        return null;
    }

    private List<ApplicationSupervisorRepresentation> getApplicationSupervisorsRepresentation(Application application) {
        return application.getSupervisors().stream()
                .map(this::getApplicationSupervisorRepresentation)
                .collect(Collectors.toList());
    }

    private ApplicationSupervisorRepresentation getApplicationSupervisorRepresentation(ApplicationSupervisor applicationSupervisor) {
        return new ApplicationSupervisorRepresentation().withId(applicationSupervisor.getId())
                .withUser(userMapper.getUserRepresentationSimple(applicationSupervisor.getUser()))
                .withAcceptedSupervisor(applicationSupervisor.getAcceptedSupervision());
    }

    private ApplicationPersonalDetailRepresentation getApplicationPersonalDetailRepresentation(Application application, Institution institution) {
        ApplicationPersonalDetail applicationPersonalDetail = application.getPersonalDetail();

        if (applicationPersonalDetail != null) {
            ApplicationPersonalDetailRepresentation representation = new ApplicationPersonalDetailRepresentation()
                    .withTitle(getImportedEntityRepresentation(applicationPersonalDetail.getTitle(), institution))
                    .withGender(getImportedEntityRepresentation(applicationPersonalDetail.getGender(), institution))
                    .withDateOfBirth(applicationPersonalDetail.getDateOfBirth())
                    .withAgeRange(getImportedEntityRepresentation(applicationPersonalDetail.getAgeRange(), institution))
                    .withFirstNationality(
                            getImportedEntityRepresentation(applicationPersonalDetail.getFirstNationality(), institution))
                    .withSecondNationality(
                            getImportedEntityRepresentation(applicationPersonalDetail.getSecondNationality(), institution))
                    .withCountry(getImportedEntityRepresentation(applicationPersonalDetail.getCountry(), institution))
                    .withFirstLanguageLocale(applicationPersonalDetail.getFirstLanguageLocale())
                    .withLanguageQualification(getApplicationLanguageQualificationRepresentation(applicationPersonalDetail, institution))
                    .withDomicile(getImportedEntityRepresentation(applicationPersonalDetail.getDomicile(), institution))
                    .withVisaRequired(applicationPersonalDetail.getVisaRequired())
                    .withPassport(getApplicationPassportRepresentation(applicationPersonalDetail, institution)).withPhone(applicationPersonalDetail.getPhone())
                    .withSkype(applicationPersonalDetail.getSkype());

            if (applicationService.isCanViewEqualOpportunitiesData(application, userService.getCurrentUser())) {
                representation.setEthnicity(getImportedEntityRepresentation(applicationPersonalDetail.getEthnicity(), institution));
                representation.setDisability(getImportedEntityRepresentation(applicationPersonalDetail.getDisability(), institution));
            }

            return representation;
        }

        return null;
    }

    private List<String> getApplicationThemeRepresentation(String themes) {
        return StringUtils.isEmpty(themes) ? Collections.<String>emptyList() : Arrays.asList(themes.split("\\|"));
    }

    private ApplicationLanguageQualificationRepresentation getApplicationLanguageQualificationRepresentation(
            ApplicationPersonalDetail applicationPersonalDetail, Institution institution) {
        ApplicationLanguageQualification applicationLanguageQualification = applicationPersonalDetail.getLanguageQualification();

        if (applicationLanguageQualification != null) {
            Document document = applicationLanguageQualification.getDocument();
            return new ApplicationLanguageQualificationRepresentation()
                    .withLanguageQualificationType(
                            getImportedEntityRepresentation(applicationLanguageQualification.getLanguageQualificationType(), institution))
                    .withExamDate(applicationLanguageQualification.getExamDate()).withOverallScore(applicationLanguageQualification.getOverallScore())
                    .withReadingScore(applicationLanguageQualification.getReadingScore()).withWritingScore(applicationLanguageQualification.getWritingScore())
                    .withSpeakingScore(applicationLanguageQualification.getSpeakingScore())
                    .withListeningScore(applicationLanguageQualification.getListeningScore())
                    .withDocument(document == null ? null : documentMapper.getDocumentRepresentation(document));
        }

        return null;
    }

    private ApplicationPassportRepresentation getApplicationPassportRepresentation(ApplicationPersonalDetail applicationPersonalDetail, Institution institution) {
        ApplicationPassport applicationPassport = applicationPersonalDetail.getPassport();

        if (applicationPassport != null) {
            return new ApplicationPassportRepresentation().withNumber(applicationPassport.getNumber()).withName(applicationPassport.getName())
                    .withIssueDate(applicationPassport.getIssueDate()).withExpiryDate(applicationPassport.getExpiryDate());
        }

        return null;
    }

    private ApplicationAddressRepresentation getApplicationAddressRepresentation(Application application, Institution institution) {
        ApplicationAddress applicationAddress = application.getAddress();

        if (applicationAddress != null) {
            return new ApplicationAddressRepresentation().withCurrentAddress(
                    getAddressApplicationRepresentation(applicationAddress.getCurrentAddress(), institution)).withContactAddress(
                    getAddressApplicationRepresentation(applicationAddress.getContactAddress(), institution));
        }

        return null;
    }

    private List<ApplicationQualificationRepresentation> getApplicationQualificationRepresentations(Application application, Institution institution) {
        return application.getQualifications().stream().map(qualification -> getApplicationQualificationRepresentation(qualification, institution))
                .collect(Collectors.toList());
    }

    private ApplicationQualificationRepresentation getApplicationQualificationRepresentation(
            ApplicationQualification applicationQualification, Institution institution) {
        Document document = applicationQualification.getDocument();
        return new ApplicationQualificationRepresentation().withId(applicationQualification.getId()).withProgram(
                getImportedEntityRepresentation(applicationQualification.getProgram(), institution))
                .withStartDate(applicationQualification.getStartDate()).withAwardDate(applicationQualification.getAwardDate())
                .withLanguage(applicationQualification.getLanguage()).withGrade(applicationQualification.getGrade())
                .withDocumentRepresentation(document == null ? null : documentMapper.getDocumentRepresentation(document))
                .withCompleted(applicationQualification.getCompleted());
    }

    private List<ApplicationEmploymentPositionRepresentation> getApplicationEmploymentPositionRepresentations(Application application, Institution institution) {
        return application.getEmploymentPositions().stream()
                .map(employmentPosition -> getApplicationEmploymentPositionRepresentation(employmentPosition, institution))
                .collect(Collectors.toList());
    }

    private ApplicationEmploymentPositionRepresentation getApplicationEmploymentPositionRepresentation(
            ApplicationEmploymentPosition applicationEmploymentPosition, Institution institution) {
        return new ApplicationEmploymentPositionRepresentation().withEmployerName(applicationEmploymentPosition.getEmployerName()).withEmployerAddress(
                getAddressApplicationRepresentation(applicationEmploymentPosition.getEmployerAddress(), institution))
                .withPosition(applicationEmploymentPosition.getPosition()).withRemit(applicationEmploymentPosition.getRemit())
                .withStartDate(applicationEmploymentPosition.getStartDate()).withCurrent(applicationEmploymentPosition.getCurrent())
                .withEndDate(applicationEmploymentPosition.getEndDate());
    }

    private List<ApplicationFundingRepresentation> getApplicationFundingRepresentations(Application application, Institution institution) {
        return application.getFundings().stream()
                .map(funding -> getApplicationFundingRepresentation(funding, institution))
                .collect(Collectors.toList());
    }

    private ApplicationFundingRepresentation getApplicationFundingRepresentation(ApplicationFunding applicationFunding, Institution institution) {
        Document document = applicationFunding.getDocument();
        return new ApplicationFundingRepresentation().withId(applicationFunding.getId()).withFundingSource(
                getImportedEntityRepresentation(applicationFunding.getFundingSource(), institution)).withSponsor(applicationFunding.getSponsor())
                .withDescription(applicationFunding.getDescription()).withValue(applicationFunding.getValue()).withAwardDate(applicationFunding.getAwardDate())
                .withTerms(applicationFunding.getTerms()).withDocument(document == null ? null : documentMapper.getDocumentRepresentation(document));
    }

    private List<ApplicationPrizeRepresentation> getApplicationPrizeRepresentations(Application application, Institution institution) {
        return application.getPrizes().stream().map(prize -> getApplicationPrizeRepresentation(prize, institution)).collect(Collectors.toList());
    }

    private ApplicationPrizeRepresentation getApplicationPrizeRepresentation(ApplicationPrize applicationPrize, Institution institution) {
        return new ApplicationPrizeRepresentation().withId(applicationPrize.getId()).withProvider(applicationPrize.getProvider())
                .withTitle(applicationPrize.getTitle()).withDescription(applicationPrize.getDescription()).withAwardDate(applicationPrize.getAwardDate());
    }

    private List<ApplicationRefereeRepresentation> getApplicationRefereeRepresentations(Application application, Institution institution) {
        return application.getReferees().stream().map(referee -> getApplicationRefereeRepresentation(referee, institution)).collect(Collectors.toList());
    }

    private ApplicationRefereeRepresentation getApplicationRefereeRepresentation(ApplicationReferee applicationReferee, Institution institution) {
        Comment comment = applicationReferee.getComment();
        return new ApplicationRefereeRepresentation().withId(applicationReferee.getId())
                .withUser(userMapper.getUserRepresentationSimple(applicationReferee.getUser())).withRefereeType(applicationReferee.getRefereeType())
                .withJobTitle(applicationReferee.getJobTitle()).withJobEmployer(applicationReferee.getJobEmployer())
                .withAddress(getAddressApplicationRepresentation(applicationReferee.getAddress(), institution)).withPhone(applicationReferee.getPhone())
                .withSkype(applicationReferee.getSkype()).withComment(comment == null ? null : comment.getId());
    }

    private ApplicationDocumentRepresentation getApplicationDocumentRepresentation(Application application) {
        ApplicationDocument applicationDocument = application.getDocument();

        if (applicationDocument != null) {
            ApplicationDocumentRepresentation representation = new ApplicationDocumentRepresentation();

            Document personalStatement = applicationDocument.getPersonalStatement();
            representation.setPersonalStatement(personalStatement == null ? null : documentMapper.getDocumentRepresentation(personalStatement));

            Document cv = applicationDocument.getCv();
            representation.setCv(cv == null ? null : documentMapper.getDocumentRepresentation(cv));

            Document coveringLetter = applicationDocument.getCoveringLetter();
            representation.setCoveringLetter(coveringLetter == null ? null : documentMapper.getDocumentRepresentation(coveringLetter));

            Document researchStatement = applicationDocument.getResearchStatement();
            representation.setResearchStatement(researchStatement == null ? null : documentMapper.getDocumentRepresentation(researchStatement));
        }

        return null;
    }

    private ApplicationAdditionalInformationRepresentation getApplicationAdditionalInformationRepresentation(Application application) {
        ApplicationAdditionalInformation additionalInformation = application.getAdditionalInformation();

        if (additionalInformation != null && applicationService.isCanViewEqualOpportunitiesData(application, userService.getCurrentUser())) {
            return new ApplicationAdditionalInformationRepresentation().withConvictionsText(additionalInformation.getConvictionsText());
        }

        return null;
    }

    private ApplicationInterviewRepresentation getApplicationInterviewRepresentation(Application application) {
        Comment schedulingComment = commentService.getLatestComment(application, APPLICATION_ASSIGN_INTERVIEWERS);

        if (schedulingComment != null) {
            ApplicationInterviewRepresentation representation = new ApplicationInterviewRepresentation();

            Set<CommentAppointmentTimeslot> timeslots = schedulingComment.getAppointmentTimeslots();
            representation.setAppointmentTimeslots(commentMapper.getCommentAppointmentTimeslotRepresentations(timeslots));
            representation.setAppointmentPreferences(commentMapper.getCommentAppointmentPreferenceRepresentations(schedulingComment, timeslots));

            representation.setInterviewAppointment(commentMapper.getCommentInterviewAppointmentRepresentation(schedulingComment));
            representation.setInterviewInstruction(commentMapper.getCommentInterviewInstructionRepresentation(schedulingComment, true));

            return representation;
        }

        return null;
    }

    private ApplicationOfferRepresentation getApplicationOfferRecommendationRepresentation(Application application) {
        Comment sourceComment = commentService.getLatestComment(application, APPLICATION_CONFIRM_OFFER_RECOMMENDATION);

        if (sourceComment != null) {
            return getApplicationOfferRecommendationRepresentation(sourceComment);
        }

        sourceComment = commentService.getLatestComment(application, APPLICATION_ASSIGN_SUPERVISORS);
        if (sourceComment != null) {
            ApplicationOfferRepresentation offerRepresentation = getApplicationOfferRecommendationRepresentation(sourceComment);

            User primarySupervisor = Iterables.getFirst(commentService.getAssignedUsers(sourceComment, APPLICATION_PRIMARY_SUPERVISOR), null);
            if (primarySupervisor != null) {
                sourceComment = commentService.getLatestComment(application, APPLICATION_CONFIRM_PRIMARY_SUPERVISION, primarySupervisor,
                        sourceComment.getCreatedTimestamp());
            }

            if (sourceComment != null) {
                String positionTitle = null;
                String positionDescription = null;

                CommentPositionDetail positionDetail = sourceComment.getPositionDetail();
                if (positionDetail != null) {
                    positionTitle = positionDetail.getPositionTitle();
                    positionDescription = positionDetail.getPositionDescription();
                }

                LocalDate positionProvisionalStartDate = null;
                String appointmentConditions = null;

                CommentOfferDetail offerDetail = sourceComment.getOfferDetail();
                if (offerDetail != null) {
                    positionProvisionalStartDate = offerDetail.getPositionProvisionalStartDate();
                    appointmentConditions = offerDetail.getAppointmentConditions();
                }

                offerRepresentation.setPositionTitle(positionTitle);
                offerRepresentation.setPositionDescription(positionDescription);
                offerRepresentation.setPositionProvisionalStartDate(positionProvisionalStartDate);
                offerRepresentation.setAppointmentConditions(appointmentConditions);
            }

            return offerRepresentation;
        }

        return new ApplicationOfferRepresentation();
    }

    private ApplicationOfferRepresentation getApplicationOfferRecommendationRepresentation(Comment comment) {
        CommentPositionDetail positionDetail = comment.getPositionDetail();
        CommentOfferDetail offerDetail = comment.getOfferDetail();

        boolean positionDetailNull = positionDetail == null;
        boolean offerDetailNull = offerDetail == null;

        return new ApplicationOfferRepresentation().withPositionTitle(positionDetailNull ? null : positionDetail.getPositionTitle())
                .withPositionDescription(positionDetailNull ? null : positionDetail.getPositionDescription())
                .withPositionProvisionalStartDate(offerDetailNull ? null : offerDetail.getPositionProvisionalStartDate())
                .withAppointmentConditions(offerDetailNull ? null : offerDetail.getAppointmentConditions());
    }

    private List<ApplicationAssignedSupervisorRepresentation> getApplicationSupervisorRepresentations(Application application) {
        Comment assignmentComment = commentService.getLatestComment(application, APPLICATION_CONFIRM_OFFER_RECOMMENDATION);

        if (assignmentComment != null) {
            return Lists.newArrayList(getApplicationAssignedSupervisorRepresentations(assignmentComment));
        } else {
            assignmentComment = commentService.getLatestComment(application, APPLICATION_ASSIGN_SUPERVISORS);

            if (assignmentComment != null) {
                Set<ApplicationAssignedSupervisorRepresentation> assignedSupervisors = getApplicationAssignedSupervisorRepresentations(assignmentComment);

                List<String> declinedSupervisors = commentService.getDeclinedSupervisors(assignmentComment);

                for (ApplicationAssignedSupervisorRepresentation assignedSupervisor : assignedSupervisors) {
                    if (declinedSupervisors.contains(assignedSupervisor.getUser().getEmail())) {
                        assignedSupervisors.remove(assignedSupervisor);
                    }
                }

                return Lists.newArrayList(assignedSupervisors);
            }
        }

        return Lists.newArrayList();
    }

    private Set<ApplicationAssignedSupervisorRepresentation> getApplicationAssignedSupervisorRepresentations(Comment comment) {
        Set<ApplicationAssignedSupervisorRepresentation> supervisors = Sets.newLinkedHashSet();

        for (CommentAssignedUser assignee : commentService.getAssignedSupervisors(comment)) {
            ApplicationAssignedSupervisorRepresentation assignedSupervisorRepresentation = new ApplicationAssignedSupervisorRepresentation()
                    .withUser(userMapper.getUserRepresentationSimple(assignee.getUser())).withRole(assignee.getRole().getId()).withAcceptedSupervision(true);
            supervisors.add(assignedSupervisorRepresentation);
        }

        return supervisors;
    }

    public ApplicationSummaryRepresentation getApplicationSummary(Application application) {
        PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localize(application);
        String dateFormat = loader.load(SYSTEM_DATE_FORMAT);

        ApplicationProgramDetail programDetail = application.getProgramDetail();
        ApplicationPersonalDetail personalDetail = application.getPersonalDetail();

        boolean programDetailNull = programDetail == null;
        boolean personalDetailNull = personalDetail == null;

        PrismStudyOption studyOption = programDetailNull ? null : programDetail.getStudyOptionDisplay();
        ApplicationSummaryRepresentation representation = new ApplicationSummaryRepresentation()
                .withCreatedDate(application.getCreatedTimestampDisplay(dateFormat))
                .withSubmittedDate(application.getSubmittedTimestampDisplay(dateFormat)).withClosingDate(application.getClosingDateDisplay(dateFormat))
                .withPrimaryThemes(application.getPrimaryThemeDisplay()).withSecondaryThemes(application.getSecondaryThemeDisplay())
                .withPhone(personalDetail == null ? null : personalDetail.getPhone()).withSkype(personalDetailNull ? null : personalDetail.getSkype())
                .withStudyOption(studyOption == null ? null : loader.load(programDetail.getStudyOptionDisplay().getDisplayProperty()))
                .withReferralSource(programDetail == null ? null : programDetail.getReferralSourceDisplay());

        ApplicationQualification latestQualification = applicationService.getLatestApplicationQualification(application);
        if (latestQualification != null) {
            ImportedProgram importedProgram = latestQualification.getProgram();
            representation.setLatestQualification(new QualificationSummaryRepresentation().withTitle(importedProgram.getQualification())
                    .withSubject(importedProgram.getName()).withGrade(latestQualification.getGrade())
                    .withInstitution(importedProgram.getInstitution().getName()).withStartDate(latestQualification.getStartDateDisplay(dateFormat))
                    .withEndDate(latestQualification.getAwardDateDisplay(dateFormat)));
        }

        ApplicationEmploymentPosition latestEmploymentPosition = applicationService.getLatestApplicationEmploymentPosition(application);
        if (latestEmploymentPosition != null) {
            representation.setLatestEmploymentPosition(new EmploymentPositionSummaryRepresentation().withPosition(latestEmploymentPosition.getPosition())
                    .withEmployer(latestEmploymentPosition.getEmployerName()).withStartDate(latestEmploymentPosition.getStartDateDisplay(dateFormat))
                    .withEndDate(latestEmploymentPosition.getEndDateDisplay(dateFormat)));
        }

        ApplicationDocument applicationDocument = application.getDocument();
        if (applicationDocument != null) {
            Map<String, PrismDisplayPropertyDefinition> documentProperties = ImmutableMap.of( //
                    "personalStatement", APPLICATION_DOCUMENT_PERSONAL_STATEMENT_LABEL, //
                    "researchStatement", APPLICATION_DOCUMENT_RESEARCH_STATEMENT_LABEL, //
                    "cv", APPLICATION_DOCUMENT_CV_LABEL, //
                    "coveringLetter", APPLICATION_DOCUMENT_COVERING_LETTER_LABEL);

            for (Entry<String, PrismDisplayPropertyDefinition> documentProperty : documentProperties.entrySet()) {
                Document document = (Document) PrismReflectionUtils.getProperty(applicationDocument, documentProperty.getKey());
                if (document != null) {
                    representation
                            .addDocument(new DocumentSummaryRepresentation().withId(document.getId()).withLabel(loader.load(documentProperty.getValue())));
                }
            }
        }

        Long providedReferenceCount = applicationService.getProvidedReferenceCount(application);
        representation.setReferenceProvidedCount(providedReferenceCount == null ? null : providedReferenceCount.intValue());

        Long declinedReferenceCount = applicationService.getDeclinedReferenceCount(application);
        representation.setReferenceDeclinedCount(declinedReferenceCount == null ? null : declinedReferenceCount.intValue());

        representation.setOtherLiveApplications(applicationService.getOtherLiveApplications(application));
        return representation;
    }

    private AddressApplicationRepresentation getAddressApplicationRepresentation(AddressApplication address, Institution institution) {
        AddressApplicationRepresentation representation = addressMapper.transform(address, AddressApplicationRepresentation.class);
        representation.setDomicile(getImportedEntityRepresentation(address.getDomicile(), institution));
        return representation;
    }

    @SuppressWarnings("unchecked")
    private <T extends ImportedEntity<?, ?>, U extends ImportedEntityResponseDefinition<?>> U getImportedEntityRepresentation(T entity, Institution institution) {
        return entity == null ? null : (U) importedEntityMapper.getImportedEntityRepresentation(entity, institution);
    }

}
