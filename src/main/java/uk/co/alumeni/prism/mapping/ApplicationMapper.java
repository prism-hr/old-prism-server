package uk.co.alumeni.prism.mapping;

import static java.util.Arrays.asList;
import static uk.co.alumeni.prism.PrismConstants.START_DATE_EARLIEST_BUFFER;
import static uk.co.alumeni.prism.PrismConstants.START_DATE_LATEST_BUFFER;
import static uk.co.alumeni.prism.PrismConstants.START_DATE_RECOMMENDED_BUFFER;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_PREFERRED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_HIRING_MANAGERS;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_INTERVIEWERS;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_OFFER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_HIRING_MANAGER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeCategory.ORGANIZATION;
import static uk.co.alumeni.prism.utils.PrismConversionUtils.doubleToBigDecimal;
import static uk.co.alumeni.prism.utils.PrismConversionUtils.longToInteger;
import static uk.co.alumeni.prism.utils.PrismDateUtils.getNextMonday;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import jersey.repackaged.com.google.common.collect.Maps;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.application.ApplicationProgramDetail;
import uk.co.alumeni.prism.domain.application.ApplicationReferee;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.comment.CommentAppointmentTimeslot;
import uk.co.alumeni.prism.domain.comment.CommentAssignedUser;
import uk.co.alumeni.prism.domain.comment.CommentOfferDetail;
import uk.co.alumeni.prism.domain.comment.CommentPositionDetail;
import uk.co.alumeni.prism.domain.definitions.PrismStudyOption;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceOpportunity;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.dto.ApplicationProcessingSummaryDTO;
import uk.co.alumeni.prism.dto.UserSelectionDTO;
import uk.co.alumeni.prism.rest.representation.advert.AdvertCategoriesRepresentation;
import uk.co.alumeni.prism.rest.representation.comment.CommentInterviewAppointmentRepresentation;
import uk.co.alumeni.prism.rest.representation.comment.CommentInterviewInstructionRepresentation;
import uk.co.alumeni.prism.rest.representation.comment.CommentRepresentation;
import uk.co.alumeni.prism.rest.representation.profile.ProfileRefereeRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationRelation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceSummaryPlotDataRepresentation.ApplicationProcessingSummaryRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.application.ApplicationAssignedHiringManagerRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.application.ApplicationInterviewRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.application.ApplicationLocationRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.application.ApplicationOfferRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.application.ApplicationProgramDetailRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.application.ApplicationRepresentationClient;
import uk.co.alumeni.prism.rest.representation.resource.application.ApplicationRepresentationExtended;
import uk.co.alumeni.prism.rest.representation.resource.application.ApplicationRepresentationSimple;
import uk.co.alumeni.prism.rest.representation.resource.application.ApplicationStartDateRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.application.ApplicationThemeRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation.AppointmentActivityRepresentation;
import uk.co.alumeni.prism.services.ApplicationService;
import uk.co.alumeni.prism.services.CommentService;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.SystemService;
import uk.co.alumeni.prism.services.UserService;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;

@Service
@Transactional
public class ApplicationMapper {
    
    @Inject
    private ApplicationService applicationService;

    @Inject
    private AdvertMapper advertMapper;

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
    private SystemService systemService;

    @Inject
    private UserMapper userMapper;

    @Inject
    private UserService userService;

    @Inject
    private ApplicationContext applicationContext;

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

        Advert advert = application.getAdvert();
        AdvertCategoriesRepresentation advertCategories = advertMapper.getAdvertCategoriesRepresentation(advert);
        representation.setPossibleThemes(advertCategories.getThemes());
        representation.setPossibleLocations(advertCategories.getLocations());

        List<UserSelectionDTO> usersInterested = userService.getUsersInterestedInApplication(application);
        representation.setUsersInterestedInApplication(userMapper.getUserRepresentations(usersInterested));
        
        representation.setUsersPotentiallyInterestedInApplication(
                userMapper.getUserRepresentations(userService.getUsersPotentiallyInterestedInApplication(application, usersInterested)));

        representation.setInterview(getApplicationInterviewRepresentation(application));
        representation.setOfferRecommendation(getApplicationOfferRecommendationRepresentation(application));
        representation.setAssignedSupervisors(getApplicationSupervisorRepresentations(application));
        
        representation.setCompetences(advertMapper.getAdvertCompetenceRepresentations(advert));
        
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
                ResourceRepresentationRelation application = resourceMapper.getResourceRepresentationActivity(appointment);
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

    private <T extends ApplicationRepresentationSimple> T getApplicationRepresentation(Application application, Class<T> returnType, List<PrismRole> overridingRoles) {
        T representation = resourceMapper.getResourceRepresentationExtended(application, returnType, overridingRoles);
        representation.setClosingDate(application.getClosingDate());
        representation.setSubmittedTimestamp(application.getSubmittedTimestamp());

        boolean viewEqualOpportunities = applicationService.isCanViewEqualOpportunities(application, userService.getCurrentUser());

        representation.setProgramDetail(getApplicationProgramDetailRepresentation(application));
        representation.setPersonalDetail(profileMapper.getPersonalDetailRepresentation(application.getPersonalDetail(), viewEqualOpportunities));
        representation.setAddress(profileMapper.getAddressRepresentation(application.getAddress()));
        representation.setQualifications(profileMapper.getQualificationRepresentations(application.getQualifications()));
        representation.setAwards(profileMapper.getAwardRepresentations(application.getAwards()));
        representation.setEmploymentPositions(profileMapper.getEmploymentPositionRepresentations(application.getEmploymentPositions()));
        representation.setReferees(getApplicationRefereeRepresentations(application.getReferees(), overridingRoles));
        representation.setDocument(profileMapper.getDocumentRepresentation(application.getDocument()));
        representation.setAdditionalInformation(profileMapper.getAdditionalInformationRepresentation(application.getAdditionalInformation(), viewEqualOpportunities));
        return representation;
    }

    private ApplicationProgramDetailRepresentation getApplicationProgramDetailRepresentation(Application application) {
        ApplicationProgramDetail applicationProgramDetail = application.getProgramDetail();
        if (applicationProgramDetail != null) {
            String preferredFlag = applicationContext.getBean(PropertyLoader.class).localizeLazy(systemService.getSystem()).loadLazy(SYSTEM_PREFERRED);
            return new ApplicationProgramDetailRepresentation().withPreferredFlag(preferredFlag).withStudyOption(applicationProgramDetail.getStudyOption())
                    .withStartDate(applicationProgramDetail.getStartDate()).withThemes(getApplicationThemeRepresentations(application))
                    .withLocations(getApplicationLocationRepresentations(application)).withLastUpdatedTimestamp(applicationProgramDetail.getLastUpdatedTimestamp());
        }
        return null;
    }

    private List<ApplicationThemeRepresentation> getApplicationThemeRepresentations(Application application) {
        List<ApplicationThemeRepresentation> representations = Lists.newLinkedList();
        application.getThemes().forEach(applicationTheme -> representations
                .add(new ApplicationThemeRepresentation().withId(applicationTheme.getTag().getId()).withName(applicationTheme.getTag().getName())
                        .withPreference(applicationTheme.getPreference()).withLastUpdateTimestamp(applicationTheme.getLastUpdatedTimestamp())));
        return representations;
    }

    private List<ApplicationLocationRepresentation> getApplicationLocationRepresentations(Application application) {
        List<ApplicationLocationRepresentation> representations = Lists.newLinkedList();
        application.getLocations().forEach(applicationLocation -> {
            Resource resource = applicationLocation.getTag().getResource();
            Integer descriptionYear = applicationLocation.getDescriptionYear();
            representations.add(new ApplicationLocationRepresentation()
                    .withResource(resource.getResourceScope().getScopeCategory().equals(ORGANIZATION) ? resourceMapper.getResourceRepresentationRelation(resource)
                            : resourceMapper.getResourceOpportunityRepresentationRelation(resource))
                    .withDescription(applicationLocation.getDescription())
                    .withDescriptionDate(descriptionYear == null ? null : new LocalDate(descriptionYear, applicationLocation.getDescriptionMonth(), 1))
                    .withPreference(applicationLocation.getPreference()).withLastUpdateTimestamp(applicationLocation.getLastUpdatedTimestamp()));
        });
        return representations;
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
                String positionName = null;
                String positionDescription = null;

                CommentPositionDetail positionDetail = sourceComment.getPositionDetail();
                if (positionDetail != null) {
                    positionName = positionDetail.getPositionName();
                    positionDescription = positionDetail.getPositionDescription();
                }

                LocalDate positionProvisionalStartDate = null;
                String appointmentConditions = null;

                CommentOfferDetail offerDetail = sourceComment.getOfferDetail();
                if (offerDetail != null) {
                    positionProvisionalStartDate = offerDetail.getPositionProvisionalStartDate();
                    appointmentConditions = offerDetail.getAppointmentConditions();
                }

                offerRepresentation.setPositionName(positionName);
                offerRepresentation.setPositionDescription(positionDescription);
                offerRepresentation.setPositionProvisionalStartDate(positionProvisionalStartDate);
                offerRepresentation.setAppointmentConditions(appointmentConditions);
            }

            return offerRepresentation;
        }

        LocalDate startDate = application.getProgramDetail() != null ? application.getProgramDetail().getStartDate() : null;
        return new ApplicationOfferRepresentation().withPositionName(application.getAdvert().getName())
                .withPositionDescription(application.getAdvert().getSummary())
                .withPositionProvisionalStartDate(startDate);
    }

    private ApplicationOfferRepresentation getApplicationOfferRecommendationRepresentation(Comment comment) {
        CommentPositionDetail positionDetail = comment.getPositionDetail();
        CommentOfferDetail offerDetail = comment.getOfferDetail();

        boolean positionDetailNull = positionDetail == null;
        boolean offerDetailNull = offerDetail == null;

        return new ApplicationOfferRepresentation().withPositionName(positionDetailNull ? null : positionDetail.getPositionName())
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
