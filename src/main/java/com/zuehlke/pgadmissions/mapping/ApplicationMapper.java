package com.zuehlke.pgadmissions.mapping;

import static com.zuehlke.pgadmissions.domain.definitions.PrismUserInstitutionIdentity.STUDY_APPLICANT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_INTERVIEWERS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_SUPERVISORS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_PRIMARY_SUPERVISION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.utils.PrismConversionUtils.doubleToBigDecimal;
import static com.zuehlke.pgadmissions.utils.PrismConversionUtils.longToInteger;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.address.AddressApplication;
import com.zuehlke.pgadmissions.domain.advert.AdvertStudyOptionInstance;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationAdditionalInformation;
import com.zuehlke.pgadmissions.domain.application.ApplicationAddress;
import com.zuehlke.pgadmissions.domain.application.ApplicationDemographic;
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
import com.zuehlke.pgadmissions.domain.comment.CommentAppointmentTimeslot;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.comment.CommentOfferDetail;
import com.zuehlke.pgadmissions.domain.comment.CommentPositionDetail;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ApplicationProcessingSummaryDTO;
import com.zuehlke.pgadmissions.dto.ApplicationReferenceDTO;
import com.zuehlke.pgadmissions.dto.UserSelectionDTO;
import com.zuehlke.pgadmissions.dto.resource.ResourceSimpleDTO;
import com.zuehlke.pgadmissions.rest.representation.address.AddressApplicationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationAdditionalInformationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationAddressRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationAssignedSupervisorRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationDemographicRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationDocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationEmploymentPositionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationFundingRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationInterviewRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationLanguageQualificationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationOfferRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationPassportRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationPersonalDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationPrizeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationProgramDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationQualificationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRefereeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRepresentationClient;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRepresentationExport;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationStudyDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationSupervisorRepresentation;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.UserService;

import uk.co.alumeni.prism.api.model.imported.ImportedEntityResponseDefinition;
import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;

@Service
@Transactional
public class ApplicationMapper {

    @Inject
    private AddressMapper addressMapper;

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

    public ApplicationRepresentationClient getApplicationRepresentationClient(Application application, List<PrismRole> overridingRoles) {
        ApplicationRepresentationClient representation = getApplicationRepresentationExtended(application, null, ApplicationRepresentationClient.class, overridingRoles);
        representation.setPossibleThemes(advertService.getAdvertThemes(application.getAdvert()));

        Resource parent = application.getParentResource();
        if (ResourceOpportunity.class.isAssignableFrom(parent.getClass())) {
            ResourceOpportunity opportunity = (ResourceOpportunity) parent;
            List<ImportedEntityResponse> studyOptions = resourceService.getStudyOptions(opportunity).stream()
                    .map(studyOption -> (ImportedEntityResponse) importedEntityMapper.getImportedEntityRepresentation(studyOption))
                    .collect(Collectors.toList());
            representation.setPossibleStudyOptions(studyOptions);
            representation.setPossibleLocations(resourceService.getStudyLocations(opportunity));
        }

        List<UserSelectionDTO> usersInterested = userService.getUsersInterestedInApplication(application);
        representation.setUsersInterestedInApplication(userMapper.getUserRepresentations(usersInterested));
        representation.setUsersPotentiallyInterestedInApplication(
                userMapper.getUserRepresentations(userService.getUsersPotentiallyInterestedInApplication(application, usersInterested)));

        representation.setInterview(getApplicationInterviewRepresentation(application));
        representation.setOfferRecommendation(getApplicationOfferRecommendationRepresentation(application));
        representation.setAssignedSupervisors(getApplicationSupervisorRepresentations(application));

        Long providedReferenceCount = applicationService.getProvidedReferenceCount(application);
        representation.setReferenceProvidedCount(providedReferenceCount == null ? null : providedReferenceCount.intValue());

        Long declinedReferenceCount = applicationService.getDeclinedReferenceCount(application);
        representation.setReferenceDeclinedCount(declinedReferenceCount == null ? null : declinedReferenceCount.intValue());

        List<ResourceRepresentationSimple> otherLiveApplications = Lists.newLinkedList();
        for (ResourceSimpleDTO otherLiveApplication : applicationService.getOtherLiveApplications(application)) {
            otherLiveApplications.add(resourceMapper.getResourceRepresentationSimple(APPLICATION, otherLiveApplication));
        }
        representation.setOtherLiveApplications(otherLiveApplications);

        return representation;
    }

    public ApplicationRepresentationExport getApplicationRepresentationExport(Application application, List<PrismRole> overridingRoles) throws Exception {
        ApplicationRepresentationExport representation = getApplicationRepresentationExtended(application, application.getInstitution(), ApplicationRepresentationExport.class,
                overridingRoles);

        representation.setUserInstitutionIdentity(userMapper.getUserInstitutionIdentityRepresentation(application.getUser(), application.getInstitution(),
                STUDY_APPLICANT));

        ResourceParent parentResource = application.getParentResource();
        if (ResourceOpportunity.class.isAssignableFrom(parentResource.getClass())) {
            AdvertStudyOptionInstance resourceStudyOptionInstance = advertService.getFirstStudyOptionInstance(parentResource.getAdvert(),
                    application.getProgramDetail().getStudyOption());
            representation.setResourceStudyOptionInstance(resourceStudyOptionInstance == null ? null : resourceMapper
                    .getResourceStudyOptionInstanceRepresentation(resourceStudyOptionInstance));
        }

        return representation;
    }

    public <T extends ApplicationRepresentationExtended> T getApplicationRepresentationExtended(Application application, Institution institution, Class<T> returnType,
            List<PrismRole> overridingRoles) {
        T representation = getApplicationRepresentation(application, institution, returnType, overridingRoles);
        representation.setOfferRecommendation(getApplicationOfferRecommendationRepresentation(application));
        representation.setAssignedSupervisors(getApplicationSupervisorRepresentations(application));
        return representation;
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

    public ApplicationRefereeRepresentation getApplicationRefereeRepresentation(ApplicationReferenceDTO reference, Institution institution, List<PrismRole> overridingRoles) {
        Comment referenceComment = reference.getComment();
        return new ApplicationRefereeRepresentation().withId(reference.getId()).withUser(userMapper.getUserRepresentationSimple(reference.getUser()))
                .withRefereeType(reference.getRefereeType()).withJobEmployer(reference.getJobEmployer()).withJobTitle(reference.getJobTitle())
                .withAddress(getAddressApplicationRepresentation(reference.getAddress(), institution)).withPhone(reference.getPhone())
                .withSkype(reference.getSkype()).withComment(getApplicationReferenceRepresentation(referenceComment, overridingRoles));
    }

    private <T extends ApplicationRepresentationSimple> T getApplicationRepresentation(Application application, Institution institution, Class<T> returnType,
            List<PrismRole> overridingRoles) {
        T representation = resourceMapper.getResourceRepresentationExtended(application, returnType, overridingRoles);

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
        representation.setReferees(getApplicationRefereeRepresentations(application, institution, overridingRoles));
        representation.setDocument(getApplicationDocumentRepresentation(application));
        representation.setAdditionalInformation(getApplicationAdditionalInformationRepresentation(application));

        return representation;
    }

    private ApplicationProgramDetailRepresentation getApplicationProgramDetailRepresentation(
            Application application, Institution institution) {
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
                    .withPassport(getApplicationPassportRepresentation(applicationPersonalDetail)).withPhone(applicationPersonalDetail.getPhone())
                    .withSkype(applicationPersonalDetail.getSkype());

            if (applicationService.isCanViewEqualOpportunitiesData(application, userService.getCurrentUser())) {
                ApplicationDemographic demographic = applicationPersonalDetail.getDemographic();
                if (demographic != null) {
                    representation.setDemographic(new ApplicationDemographicRepresentation().withEthnicity(
                            getImportedEntityRepresentation(demographic.getEthnicity(), institution)).withDisability(
                                    getImportedEntityRepresentation(demographic.getDisability(), institution)));
                }
            }

            return representation;
        }

        return null;
    }

    private List<String> getApplicationThemeRepresentation(String themes) {
        return StringUtils.isEmpty(themes) ? Collections.<String> emptyList() : Arrays.asList(themes.split("\\|"));
    }

    private ApplicationLanguageQualificationRepresentation getApplicationLanguageQualificationRepresentation(
            ApplicationPersonalDetail applicationPersonalDetail, Institution institution) {
        ApplicationLanguageQualification applicationLanguageQualification = applicationPersonalDetail.getLanguageQualification();

        if (applicationLanguageQualification != null) {
            Document document = applicationLanguageQualification.getDocument();
            return new ApplicationLanguageQualificationRepresentation()
                    .withType(getImportedEntityRepresentation(applicationLanguageQualification.getLanguageQualificationType(), institution))
                    .withExamDate(applicationLanguageQualification.getExamDate()).withOverallScore(applicationLanguageQualification.getOverallScore())
                    .withReadingScore(applicationLanguageQualification.getReadingScore()).withWritingScore(applicationLanguageQualification.getWritingScore())
                    .withSpeakingScore(applicationLanguageQualification.getSpeakingScore())
                    .withListeningScore(applicationLanguageQualification.getListeningScore())
                    .withDocument(document == null ? null : documentMapper.getDocumentRepresentation(document));
        }

        return null;
    }

    private ApplicationPassportRepresentation getApplicationPassportRepresentation(ApplicationPersonalDetail applicationPersonalDetail) {
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

    private List<ApplicationRefereeRepresentation> getApplicationRefereeRepresentations(Application application, Institution institution, List<PrismRole> overridingRoles) {
        return application.getReferees().stream().map(referee -> getApplicationRefereeRepresentation(referee, institution, overridingRoles)).collect(Collectors.toList());
    }

    private ApplicationRefereeRepresentation getApplicationRefereeRepresentation(ApplicationReferee applicationReferee, Institution institution, List<PrismRole> overridingRoles) {
        return new ApplicationRefereeRepresentation().withId(applicationReferee.getId())
                .withUser(userMapper.getUserRepresentationSimple(applicationReferee.getUser()))
                .withRefereeType(applicationReferee.getRefereeType()).withJobTitle(applicationReferee.getJobTitle())
                .withJobEmployer(applicationReferee.getJobEmployer())
                .withAddress(getAddressApplicationRepresentation(applicationReferee.getAddress(), institution))
                .withPhone(applicationReferee.getPhone()).withSkype(applicationReferee.getSkype())
                .withComment(getApplicationReferenceRepresentation(applicationReferee.getComment(), overridingRoles));
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
            return representation;
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

    private CommentRepresentation getApplicationReferenceRepresentation(Comment referenceComment, List<PrismRole> overridingRoles) {
        return referenceComment == null ? null : commentMapper.getCommentRepresentation(userService.getCurrentUser(), referenceComment, overridingRoles);
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

    public AddressApplicationRepresentation getAddressApplicationRepresentation(AddressApplication address, Institution institution) {
        AddressApplicationRepresentation representation = addressMapper.transform(address, AddressApplicationRepresentation.class);
        representation.setDomicile(getImportedEntityRepresentation(address.getDomicile(), institution));
        return representation;
    }

    @SuppressWarnings("unchecked")
    private <T extends ImportedEntity<?, ?>, U extends ImportedEntityResponseDefinition<?>> U getImportedEntityRepresentation(T entity, Institution institution) {
        return entity == null ? null : (U) importedEntityMapper.getImportedEntityRepresentation(entity, institution);
    }

}
