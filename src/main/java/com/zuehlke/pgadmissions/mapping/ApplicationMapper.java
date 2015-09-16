package com.zuehlke.pgadmissions.mapping;

import static com.zuehlke.pgadmissions.PrismConstants.START_DATE_EARLIEST_BUFFER;
import static com.zuehlke.pgadmissions.PrismConstants.START_DATE_RECOMMENDED_BUFFER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.IMPORTED_STUDY_OPTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOauthProvider.LINKEDIN;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_HIRING_MANAGERS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_INTERVIEWERS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_APPOINTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_HIRING_MANAGER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.utils.PrismConversionUtils.doubleToBigDecimal;
import static com.zuehlke.pgadmissions.utils.PrismConversionUtils.longToInteger;
import static com.zuehlke.pgadmissions.utils.PrismDateUtils.getNextMonday;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.PrismConstants;
import com.zuehlke.pgadmissions.domain.address.Address;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationAdditionalInformation;
import com.zuehlke.pgadmissions.domain.application.ApplicationAddress;
import com.zuehlke.pgadmissions.domain.application.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.application.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.application.ApplicationPersonalDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationProgramDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAppointmentTimeslot;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.comment.CommentOfferDetail;
import com.zuehlke.pgadmissions.domain.comment.CommentPositionDetail;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ApplicationProcessingSummaryDTO;
import com.zuehlke.pgadmissions.dto.UserSelectionDTO;
import com.zuehlke.pgadmissions.dto.resource.ResourceSimpleDTO;
import com.zuehlke.pgadmissions.rest.representation.address.AddressRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentInterviewAppointmentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentInterviewInstructionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationActivity;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationAdditionalInformationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationAddressRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationAssignedHiringManagerRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationDocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationEmploymentPositionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationInterviewRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationOfferRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationPersonalDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationProgramDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationQualificationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRefereeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRepresentationClient;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationStartDateRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserActivityRepresentation.AppointmentActivityRepresentation;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
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
    private ImportedEntityService importedEntityService;

    @Inject
    private UserMapper userMapper;

    @Inject
    private UserService userService;

    public ApplicationRepresentationClient getApplicationRepresentationClient(Application application, List<PrismRole> overridingRoles) {
        ApplicationRepresentationClient representation = getApplicationRepresentationExtended(application, ApplicationRepresentationClient.class, overridingRoles);

        List<ImportedEntitySimple> studyOptions;
        Resource parent = application.getParentResource();
        if (ResourceOpportunity.class.isAssignableFrom(parent.getClass())) {
            ResourceOpportunity opportunity = (ResourceOpportunity) parent;
            studyOptions = resourceService.getStudyOptions(opportunity);
        } else {
            studyOptions = importedEntityService.getEnabledImportedEntities(IMPORTED_STUDY_OPTION);
        }

        List<ImportedEntityResponse> studyOptionRepresentations = studyOptions.stream()
                .map(studyOption -> (ImportedEntityResponse) importedEntityMapper.getImportedEntityRepresentation(studyOption))
                .collect(Collectors.toList());

        representation.setPossibleStudyOptions(studyOptionRepresentations);

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

    public ApplicationRepresentationExtended getApplicationRepresentationExtended(Application application, List<PrismRole> overridingRoles) {
        return getApplicationRepresentationExtended(application, ApplicationRepresentationExtended.class, overridingRoles);
    }

    public <T extends ApplicationRepresentationExtended> T getApplicationRepresentationExtended(Application application, Class<T> returnType, List<PrismRole> overridingRoles) {
        T representation = getApplicationRepresentation(application, returnType, overridingRoles);
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

    public List<AppointmentActivityRepresentation> getApplicationAppointmentRepresentations(User user) {
        LocalDate baseline = new LocalDate();
        List<AppointmentActivityRepresentation> representations = Lists.newLinkedList();

        applicationService.getApplicationAppointments(user).forEach(appointment -> {
            ResourceRepresentationActivity application = resourceMapper.getResourceRepresentationActivity(appointment);
            application.setCode(appointment.getApplicationCode());

            LocalDateTime interviewDateTime = appointment.getInterviewDateTime();
            if (interviewDateTime.toLocalDate().isAfter(baseline)) {
                representations.add(new AppointmentActivityRepresentation().withResource(application)
                        .withAppointment(new CommentInterviewAppointmentRepresentation().withInterviewDateTime(interviewDateTime)
                                .withInterviewTimeZone(appointment.getInterviewTimeZone()).withInterviewDuration(appointment.getInterviewDuration()))
                        .withInstruction(new CommentInterviewInstructionRepresentation().withInterviewLocation(appointment.getInterviewLocation())));
            }
        });

        return representations;
    }

    public ApplicationStartDateRepresentation getApplicationStartDateRepresentation(LocalDate baseline) {
        return new ApplicationStartDateRepresentation().withEarliestDate(getNextMonday(baseline.plusDays(START_DATE_EARLIEST_BUFFER)))
                .withRecommendedDate(getNextMonday(baseline.plusMonths(START_DATE_RECOMMENDED_BUFFER)))
                .withLatestDate(getNextMonday(baseline.plusYears(PrismConstants.START_DATE_LATEST_BUFFER)));
    }

    private <T extends ApplicationRepresentationSimple> T getApplicationRepresentation(Application application, Class<T> returnType, List<PrismRole> overridingRoles) {
        T representation = resourceMapper.getResourceRepresentationExtended(application, returnType, overridingRoles);

        representation.setClosingDate(application.getClosingDate());
        representation.setSubmittedTimestamp(application.getSubmittedTimestamp());
        representation.setProgramDetail(getApplicationProgramDetailRepresentation(application));
        representation.setPersonalDetail(getApplicationPersonalDetailRepresentation(application));
        representation.setAddress(getApplicationAddressRepresentation(application));
        representation.setQualifications(getApplicationQualificationRepresentations(application));
        representation.setEmploymentPositions(getApplicationEmploymentPositionRepresentations(application));
        representation.setReferees(getApplicationRefereeRepresentations(application, overridingRoles));
        representation.setDocument(getApplicationDocumentRepresentation(application));
        representation.setAdditionalInformation(getApplicationAdditionalInformationRepresentation(application));

        return representation;
    }

    private ApplicationProgramDetailRepresentation getApplicationProgramDetailRepresentation(Application application) {
        ApplicationProgramDetail applicationProgramDetail = application.getProgramDetail();
        if (applicationProgramDetail != null) {
            return new ApplicationProgramDetailRepresentation()
                    .withStudyOption(getImportedEntityRepresentation(applicationProgramDetail.getStudyOption()))
                    .withStartDate(applicationProgramDetail.getStartDate()).withLastUpdatedTimestamp(applicationProgramDetail.getLastUpdatedTimestamp());
        }
        return null;
    }

    private ApplicationPersonalDetailRepresentation getApplicationPersonalDetailRepresentation(Application application) {
        ApplicationPersonalDetail applicationPersonalDetail = application.getPersonalDetail();

        if (applicationPersonalDetail != null) {
            return new ApplicationPersonalDetailRepresentation().withTitle(getImportedEntityRepresentation(applicationPersonalDetail.getTitle()))
                    .withGender(getImportedEntityRepresentation(applicationPersonalDetail.getGender()))
                    .withDateOfBirth(application.getUser().getUserAccount().getPersonalDetail().getDateOfBirth())
                    .withAgeRange(getImportedEntityRepresentation(applicationPersonalDetail.getAgeRange()))
                    .withFirstNationality(getImportedEntityRepresentation(applicationPersonalDetail.getNationality()))
                    .withDomicile(getImportedEntityRepresentation(applicationPersonalDetail.getDomicile()))
                    .withVisaRequired(applicationPersonalDetail.getVisaRequired()).withPhone(applicationPersonalDetail.getPhone())
                    .withSkype(applicationPersonalDetail.getSkype()).withEthnicity(getImportedEntityRepresentation(applicationPersonalDetail.getEthnicity()))
                    .withDisability(getImportedEntityRepresentation(applicationPersonalDetail.getDisability()))
                    .withLastUpdatedTimestamp(applicationPersonalDetail.getLastUpdatedTimestamp());
        }

        return null;
    }

    private ApplicationAddressRepresentation getApplicationAddressRepresentation(Application application) {
        ApplicationAddress applicationAddress = application.getAddress();

        if (applicationAddress != null) {
            return new ApplicationAddressRepresentation().withCurrentAddress(
                    getAddressApplicationRepresentation(applicationAddress.getCurrentAddress())).withContactAddress(
                            getAddressApplicationRepresentation(applicationAddress.getContactAddress()))
                    .withLastUpdatedTimestamp(applicationAddress.getLastUpdatedTimestamp());
        }

        return null;
    }

    private List<ApplicationQualificationRepresentation> getApplicationQualificationRepresentations(Application application) {
        return application.getQualifications().stream().map(qualification -> getApplicationQualificationRepresentation(qualification)).collect(Collectors.toList());
    }

    private ApplicationQualificationRepresentation getApplicationQualificationRepresentation(ApplicationQualification applicationQualification) {
        Document document = applicationQualification.getDocument();
        return new ApplicationQualificationRepresentation().withId(applicationQualification.getId())
                .withProgram(resourceMapper.getResourceRepresentationActivity(applicationQualification.getAdvert().getResource()))
                .withStartYear(applicationQualification.getStartYear()).withStartMonth(applicationQualification.getStartMonth())
                .withAwardYear(applicationQualification.getAwardYear()).withAwardMonth(applicationQualification.getAwardMonth())
                .withCompleted(applicationQualification.getCompleted()).withDocumentRepresentation(document == null ? null : documentMapper.getDocumentRepresentation(document))
                .withLastUpdatedTimestamp(applicationQualification.getLastUpdatedTimestamp());
    }

    private List<ApplicationEmploymentPositionRepresentation> getApplicationEmploymentPositionRepresentations(Application application) {
        return application.getEmploymentPositions().stream()
                .map(employmentPosition -> getApplicationEmploymentPositionRepresentation(employmentPosition))
                .collect(Collectors.toList());
    }

    private ApplicationEmploymentPositionRepresentation getApplicationEmploymentPositionRepresentation(ApplicationEmploymentPosition applicationEmploymentPosition) {
        return new ApplicationEmploymentPositionRepresentation().withStartYear(applicationEmploymentPosition.getStartYear())
                .withStartMonth(applicationEmploymentPosition.getStartMonth()).withEndYear(applicationEmploymentPosition.getEndYear())
                .withEndMonth(applicationEmploymentPosition.getEndMonth()).withCurrent(applicationEmploymentPosition.getCurrent())
                .withLastUpdatedTimestamp(applicationEmploymentPosition.getLastUpdatedTimestamp());
    }

    private List<ApplicationRefereeRepresentation> getApplicationRefereeRepresentations(Application application, List<PrismRole> overridingRoles) {
        return application.getReferees().stream().map(referee -> getApplicationRefereeRepresentation(referee, overridingRoles)).collect(Collectors.toList());
    }

    private ApplicationRefereeRepresentation getApplicationRefereeRepresentation(ApplicationReferee applicationReferee, List<PrismRole> overridingRoles) {
        return new ApplicationRefereeRepresentation().withId(applicationReferee.getId())
                .withUser(userMapper.getUserRepresentationSimple(applicationReferee.getUser()))
                .withResource(resourceMapper.getResourceRepresentationActivity(applicationReferee.getAdvert().getResource()))
                .withPhone(applicationReferee.getPhone()).withSkype(applicationReferee.getSkype())
                .withComment(getApplicationReferenceRepresentation(applicationReferee.getComment(), overridingRoles))
                .withLastUpdatedTimestamp(applicationReferee.getLastUpdatedTimestamp());
    }

    private ApplicationDocumentRepresentation getApplicationDocumentRepresentation(Application application) {
        ApplicationDocument applicationDocument = application.getDocument();

        if (applicationDocument != null) {
            ApplicationDocumentRepresentation representation = new ApplicationDocumentRepresentation();
            representation.setPersonalSummary(applicationDocument.getPersonalSummary());

            Document cv = applicationDocument.getCv();
            representation.setCv(cv == null ? null : documentMapper.getDocumentRepresentation(cv));

            Document coveringLetter = applicationDocument.getCoveringLetter();
            representation.setCoveringLetter(coveringLetter == null ? null : documentMapper.getDocumentRepresentation(coveringLetter));

            representation.setLinkedinProfileUrl(userService.getOauthProfileUrl(application.getUser(), LINKEDIN));
        }

        return null;
    }

    private ApplicationAdditionalInformationRepresentation getApplicationAdditionalInformationRepresentation(Application application) {
        ApplicationAdditionalInformation additionalInformation = application.getAdditionalInformation();

        if (additionalInformation != null && applicationService.isCanViewEqualOpportunitiesData(application, userService.getCurrentUser())) {
            return new ApplicationAdditionalInformationRepresentation().withConvictionsText(additionalInformation.getConvictionsText())
                    .withLastUpdatedTimestamp(additionalInformation.getLastUpdatedTimestamp());
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

        sourceComment = commentService.getLatestComment(application, APPLICATION_ASSIGN_HIRING_MANAGERS);
        if (sourceComment != null) {
            ApplicationOfferRepresentation offerRepresentation = getApplicationOfferRecommendationRepresentation(sourceComment);

            User manager = Iterables.getFirst(commentService.getAssignedUsers(sourceComment, APPLICATION_HIRING_MANAGER), null);
            if (manager != null) {
                sourceComment = commentService.getLatestComment(application, APPLICATION_CONFIRM_APPOINTMENT, manager, sourceComment.getCreatedTimestamp());
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

    private List<ApplicationAssignedHiringManagerRepresentation> getApplicationSupervisorRepresentations(Application application) {
        Comment assignmentComment = commentService.getLatestComment(application, APPLICATION_CONFIRM_OFFER_RECOMMENDATION);

        if (assignmentComment != null) {
            return Lists.newArrayList(getApplicationHiringManagerRepresentations(assignmentComment));
        } else {
            assignmentComment = commentService.getLatestComment(application, APPLICATION_ASSIGN_HIRING_MANAGERS);

            if (assignmentComment != null) {
                Set<ApplicationAssignedHiringManagerRepresentation> assignedSupervisors = getApplicationHiringManagerRepresentations(assignmentComment);

                List<String> declinedSupervisors = commentService.getDeclinedHiringManagers(assignmentComment);

                for (ApplicationAssignedHiringManagerRepresentation assignedSupervisor : assignedSupervisors) {
                    if (declinedSupervisors.contains(assignedSupervisor.getUser().getEmail())) {
                        assignedSupervisors.remove(assignedSupervisor);
                    }
                }

                return Lists.newArrayList(assignedSupervisors);
            }
        }

        return Lists.newArrayList();
    }

    private Set<ApplicationAssignedHiringManagerRepresentation> getApplicationHiringManagerRepresentations(Comment comment) {
        Set<ApplicationAssignedHiringManagerRepresentation> supervisors = Sets.newLinkedHashSet();

        for (CommentAssignedUser assignee : commentService.getAssignedHiringManagers(comment)) {
            ApplicationAssignedHiringManagerRepresentation assignedSupervisorRepresentation = new ApplicationAssignedHiringManagerRepresentation()
                    .withUser(userMapper.getUserRepresentationSimple(assignee.getUser())).withRole(assignee.getRole().getId()).withApprovedAppointment(true);
            supervisors.add(assignedSupervisorRepresentation);
        }

        return supervisors;
    }

    public AddressRepresentation getAddressApplicationRepresentation(Address address) {
        AddressRepresentation representation = addressMapper.transform(address, AddressRepresentation.class);
        representation.setDomicile(getImportedEntityRepresentation(address.getDomicile()));
        return representation;
    }

    @SuppressWarnings("unchecked")
    private <T extends ImportedEntity<?>, U extends ImportedEntityResponseDefinition<?>> U getImportedEntityRepresentation(T entity) {
        return entity == null ? null : (U) importedEntityMapper.getImportedEntityRepresentation(entity);
    }

}
