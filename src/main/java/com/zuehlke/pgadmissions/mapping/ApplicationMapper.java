package com.zuehlke.pgadmissions.mapping;

import static com.zuehlke.pgadmissions.PrismConstants.START_DATE_EARLIEST_BUFFER;
import static com.zuehlke.pgadmissions.PrismConstants.START_DATE_LATEST_BUFFER;
import static com.zuehlke.pgadmissions.PrismConstants.START_DATE_RECOMMENDED_BUFFER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_HIRING_MANAGERS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_INTERVIEWERS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_OFFER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_HIRING_MANAGER;
import static com.zuehlke.pgadmissions.utils.PrismConversionUtils.doubleToBigDecimal;
import static com.zuehlke.pgadmissions.utils.PrismConversionUtils.longToInteger;
import static com.zuehlke.pgadmissions.utils.PrismDateUtils.getNextMonday;
import static java.util.Arrays.asList;

import java.util.List;
import java.util.Map;
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
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationProgramDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAppointmentTimeslot;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.comment.CommentOfferDetail;
import com.zuehlke.pgadmissions.domain.comment.CommentPositionDetail;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ApplicationProcessingSummaryDTO;
import com.zuehlke.pgadmissions.dto.UserSelectionDTO;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentInterviewAppointmentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentInterviewInstructionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.profile.ProfileRefereeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationActivity;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationAssignedHiringManagerRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationInterviewRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationOfferRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationProgramDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRepresentationClient;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationStartDateRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserActivityRepresentation.AppointmentActivityRepresentation;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.UserService;

import jersey.repackaged.com.google.common.collect.Maps;

@Service
@Transactional
public class ApplicationMapper {

    @Inject
    private ApplicationService applicationService;

    @Inject
    private CommentMapper commentMapper;

    @Inject
    private CommentService commentService;

    @Inject
    private ProfileMapper profileMapper;

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private ResourceService resourceService;

    @Inject
    private AdvertMapper advertMapper;

    @Inject
    private UserMapper userMapper;

    @Inject
    private UserService userService;

    public ApplicationRepresentationClient getApplicationRepresentationClient(Application application, List<PrismRole> overridingRoles) {
        ApplicationRepresentationClient representation = getApplicationRepresentationExtended(application, ApplicationRepresentationClient.class, overridingRoles);

        List<PrismStudyOption> studyOptions;
        Resource parent = application.getParentResource();
        if (ResourceOpportunity.class.isAssignableFrom(parent.getClass())) {
            ResourceOpportunity opportunity = (ResourceOpportunity) parent;
            studyOptions = resourceService.getStudyOptions(opportunity);
        } else {
            studyOptions = asList(PrismStudyOption.values());
        }

        representation.setPossibleStudyOptions(studyOptions);

        List<UserSelectionDTO> usersInterested = userService.getUsersInterestedInApplication(application);
        representation.setUsersInterestedInApplication(userMapper.getUserRepresentations(usersInterested));
        representation.setUsersPotentiallyInterestedInApplication(
                userMapper.getUserRepresentations(userService.getUsersPotentiallyInterestedInApplication(application, usersInterested)));

        representation.setInterview(getApplicationInterviewRepresentation(application));
        representation.setOfferRecommendation(getApplicationOfferRecommendationRepresentation(application));
        representation.setAssignedSupervisors(getApplicationSupervisorRepresentations(application));
        representation.setCompetences(advertMapper.getAdvertCompetenceRepresentations(application.getAdvert()));
        return representation;
    }

    public ApplicationRepresentationExtended getApplicationRepresentationExtended(Application application, List<PrismRole> overridingRoles) {
        return getApplicationRepresentationExtended(application, ApplicationRepresentationExtended.class, overridingRoles);
    }

    public <T extends ApplicationRepresentationExtended> T getApplicationRepresentationExtended(Application application, Class<T> returnType, List<PrismRole> overridingRoles) {
        T representation = getApplicationRepresentation(application, returnType, overridingRoles);
        representation.setWithoutReference(
                applicationService.getApplicationRefereesNotResponded(application).stream().map(userMapper::getUserRepresentationSimple).collect(Collectors.toList()));
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
            LocalDateTime interviewDateTime = appointment.getInterviewDateTime();
            if (interviewDateTime.toLocalDate().isAfter(baseline)) {
                ResourceRepresentationActivity application = resourceMapper.getResourceRepresentationActivity(appointment);
                application.setCode(appointment.getApplicationCode());
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
                .withLatestDate(getNextMonday(baseline.plusYears(START_DATE_LATEST_BUFFER)));
    }

    public <T extends ApplicationRepresentationSimple> void fillApplicationProfileRepresentation(Application application, T representation, List<PrismRole> overridingRoles) {
        representation.setPersonalDetail(profileMapper.getPersonalDetailRepresentation(application.getPersonalDetail()));
        representation.setAddress(profileMapper.getAddressRepresentation(application.getAddress()));
        representation.setQualifications(profileMapper.getQualificationRepresentations(application.getQualifications()));
        representation.setEmploymentPositions(profileMapper.getEmploymentPositionRepresentations(application.getEmploymentPositions()));
        representation.setReferees(getApplicationRefereeRepresentations(application.getReferees(), overridingRoles));
        representation.setDocument(profileMapper.getDocumentRepresentation(application.getDocument()));
        representation.setAdditionalInformation(profileMapper.getAdditionalInformationRepresentation(application.getAdditionalInformation()));
    }

    private <T extends ApplicationRepresentationSimple> T getApplicationRepresentation(Application application, Class<T> returnType, List<PrismRole> overridingRoles) {
        T representation = resourceMapper.getResourceRepresentationExtended(application, returnType, overridingRoles);
        representation.setClosingDate(application.getClosingDate());
        representation.setSubmittedTimestamp(application.getSubmittedTimestamp());
        representation.setProgramDetail(getApplicationProgramDetailRepresentation(application));
        fillApplicationProfileRepresentation(application, representation, overridingRoles);
        return representation;
    }

    private ApplicationProgramDetailRepresentation getApplicationProgramDetailRepresentation(Application application) {
        ApplicationProgramDetail applicationProgramDetail = application.getProgramDetail();
        if (applicationProgramDetail != null) {
            return new ApplicationProgramDetailRepresentation().withStudyOption(applicationProgramDetail.getStudyOption()).withStartDate(applicationProgramDetail.getStartDate())
                    .withLastUpdatedTimestamp(applicationProgramDetail.getLastUpdatedTimestamp());
        }
        return null;
    }

    private List<ProfileRefereeRepresentation> getApplicationRefereeRepresentations(Set<ApplicationReferee> referees, List<PrismRole> overridingRoles) {
        Map<Integer, ApplicationReferee> index = Maps.newHashMap();
        referees.forEach(r -> {
            index.put(r.getId(), r);
        });

        List<ProfileRefereeRepresentation> representations = profileMapper.getRefereeRepresentations(referees);
        representations.forEach(r -> {
            r.setComment(getApplicationReferenceRepresentation(index.get(r.getId()).getComment(), overridingRoles));
        });

        return representations;
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
        Comment sourceComment = commentService.getLatestComment(application, APPLICATION_CONFIRM_OFFER);

        if (sourceComment != null) {
            return getApplicationOfferRecommendationRepresentation(sourceComment);
        }

        sourceComment = commentService.getLatestComment(application, APPLICATION_ASSIGN_HIRING_MANAGERS);
        if (sourceComment != null) {
            ApplicationOfferRepresentation offerRepresentation = getApplicationOfferRecommendationRepresentation(sourceComment);

            User manager = Iterables.getFirst(commentService.getAssignedUsers(sourceComment, APPLICATION_HIRING_MANAGER), null);
            if (manager != null) {
                sourceComment = commentService.getLatestComment(application, APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL, manager, sourceComment.getCreatedTimestamp());
            }

            if (sourceComment != null) {
                String positionTitle = null;
                String positionDescription = null;

                CommentPositionDetail positionDetail = sourceComment.getPositionDetail();
                if (positionDetail != null) {
                    positionTitle = positionDetail.getPositionName();
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

        return new ApplicationOfferRepresentation().withPositionTitle(positionDetailNull ? null : positionDetail.getPositionName())
                .withPositionDescription(positionDetailNull ? null : positionDetail.getPositionDescription())
                .withPositionProvisionalStartDate(offerDetailNull ? null : offerDetail.getPositionProvisionalStartDate())
                .withAppointmentConditions(offerDetailNull ? null : offerDetail.getAppointmentConditions());
    }

    private List<ApplicationAssignedHiringManagerRepresentation> getApplicationSupervisorRepresentations(Application application) {
        Comment assignmentComment = commentService.getLatestComment(application, APPLICATION_CONFIRM_OFFER);

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

    private CommentRepresentation getApplicationReferenceRepresentation(Comment referenceComment, List<PrismRole> overridingRoles) {
        return referenceComment == null ? null : commentMapper.getCommentRepresentation(userService.getCurrentUser(), referenceComment, overridingRoles);
    }

}
