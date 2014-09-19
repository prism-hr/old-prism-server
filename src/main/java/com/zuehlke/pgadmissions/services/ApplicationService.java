package com.zuehlke.pgadmissions.services;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.components.ApplicationCopyHelper;
import com.zuehlke.pgadmissions.dao.ApplicationDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.ApplicationSupervisor;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentAppointmentPreference;
import com.zuehlke.pgadmissions.domain.CommentAppointmentTimeslot;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.ProgramStudyOption;
import com.zuehlke.pgadmissions.domain.RejectionReason;
import com.zuehlke.pgadmissions.domain.ResidenceState;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.PrismOfferType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.PrismValidationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.rest.dto.ApplicationDTO;
import com.zuehlke.pgadmissions.rest.dto.CommentAssignedUserDTO;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.FileDTO;
import com.zuehlke.pgadmissions.rest.dto.UserDTO;
import com.zuehlke.pgadmissions.rest.validation.validator.CompleteApplicationValidator;

@Service
@Transactional
public class ApplicationService {

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private ActionService actionService;

    @Autowired
    private AdvertService advertService;

    @Autowired
    private ApplicationSummaryService applicationSummaryService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationCopyHelper applicationCopyHelper;

    @Autowired
    private ProgramService programService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CommentService commentService;
    
    @Autowired
    private ApplicationExportService applicationExportService;

    @Autowired
    private CompleteApplicationValidator completeApplicationValidator;

    public Application getById(Integer id) {
        return entityService.getById(Application.class, id);
    }

    public Application create(User user, ApplicationDTO applicationDTO) {
        Advert advert = advertService.getById(applicationDTO.getAdvertId());
        Application application = new Application().withUser(user).withParentResource(advert.getParentResource()).withDoRetain(false)
                .withCreatedTimestamp(new DateTime());

        Application previousApplication = getPreviousApplication(application);
        if (previousApplication != null) {
            applicationCopyHelper.copyApplicationFormData(application, previousApplication);
        }

        AdvertClosingDate closingDate = advert.getClosingDate();
        application.setClosingDate(closingDate == null ? null : closingDate.getClosingDate());

        return application;
    }

    public void save(Application application) {
        entityService.save(application);
    }

    public Application getByCode(String code) {
        return entityService.getByProperty(Application.class, "code", code);
    }

    // TODO: handle null response - study option expired
    public LocalDate getEarliestStartDate(Application application) {
        ProgramStudyOption studyOption = programService.getEnabledProgramStudyOption(application.getProgram(), application.getProgramDetail().getStudyOption());

        if (studyOption == null) {
            return null;
        }

        LocalDate baseline = new LocalDate();
        LocalDate studyOptionStart = studyOption.getApplicationStartDate();

        LocalDate earliestStartDate = studyOptionStart.isBefore(baseline) ? baseline : studyOptionStart;
        return earliestStartDate.withDayOfWeek(DateTimeConstants.MONDAY);
    }

    // TODO: handle null response - study option expired
    public LocalDate getRecommendedStartDate(Application application) {
        ProgramStudyOption studyOption = programService.getEnabledProgramStudyOption(application.getProgram(), application.getProgramDetail().getStudyOption());

        if (studyOption == null) {
            return null;
        }

        LocalDate studyOptionStart = studyOption.getApplicationStartDate();
        LocalDate studyOptionClose = studyOption.getApplicationCloseDate();

        LocalDate recommendedStartDate = application.getRecommendedStartDate();

        if (recommendedStartDate.isAfter(studyOptionClose)) {
            recommendedStartDate = studyOptionClose;
        } else if (recommendedStartDate.isBefore(studyOptionStart)) {
            recommendedStartDate = studyOptionStart;
        }

        return recommendedStartDate.withDayOfWeek(DateTimeConstants.MONDAY);
    }

    // TODO: handle null response - study option expired
    public LocalDate getLatestStartDate(Application application) {
        ProgramStudyOption studyOption = programService.getEnabledProgramStudyOption(application.getProgram(), application.getProgramDetail().getStudyOption());
        return studyOption == null ? null : studyOption.getApplicationCloseDate().withDayOfWeek(DateTimeConstants.MONDAY);
    }

    public String getApplicationExportReference(Application application) {
        return applicationDAO.getApplicationExportReference(application);
    }

    public String getApplicationCreatorIpAddress(Application application) {
        return applicationDAO.getApplicationCreatorIpAddress(application);
    }

    public User getPrimarySupervisor(Comment offerRecommendationComment) {
        return applicationDAO.getPrimarySupervisor(offerRecommendationComment);
    }

    public List<ApplicationQualification> getApplicationExportQualifications(Application application) {
        return applicationDAO.getApplicationExportQualifications(application);
    }

    public List<ApplicationReferee> getApplicationExportReferees(Application application) {
        return applicationDAO.getApplicationExportReferees(application);
    }

    public void validateApplicationCompleteness(Integer applicationId) {
        Application application = entityService.getById(Application.class, applicationId);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(application, "application");
        ValidationUtils.invokeValidator(completeApplicationValidator, application, errors);
        if (errors.hasErrors()) {
            throw new PrismValidationException("Application not completed", errors);
        }
    }

    public LocalDate resolveDueDateBaseline(Application application, Comment comment) {
        if (comment.isApplicationAssignReviewersComment()) {
            LocalDate closingDate = application.getClosingDate();

            if (closingDate != null) {
                application.setClosingDate(null);
                application.setPreviousClosingDate(closingDate);
                return closingDate;
            }
        }
        return null;
    }

    public void postProcessApplication(Application application, Comment comment) throws DeduplicationException {
        if (comment.isApplicationCreatedComment()) {
            applicationSummaryService.incrementApplicationCreatedCount(application);
        }

        if (comment.isProjectCreateComment()) {
            synchroniseProjectSupervisors(application);
        }

        if (comment.isApplicationProvideReferenceComment()) {
            synchroniseReferees(application, comment);
        }

        if (comment.isApplicationConfirmOfferRecommendationComment()) {
            synchroniseOfferRecommendation(application, comment);
        }

        if (comment.isRatingComment()) {
            applicationSummaryService.summariseApplication(application, comment);
        }

        if (comment.isTransitionComment()) {
            applicationSummaryService.summariseApplicationProcessing(application);
        }

        if (comment.isApplicationSubmittedComment()) {
            applicationSummaryService.incrementApplicationSubmittedCount(application);
        }

        if (comment.isApplicationApprovedComment()) {
            applicationSummaryService.incrementApplicationApprovedCount(application);
        }

        if (comment.isApplicationRejectedComment()) {
            applicationSummaryService.incrementApplicationRejectedCount(application);
        }

        if (comment.isApplicationWithdrawnComment()) {
            applicationSummaryService.incrementApplicationWithdrawnCount(application);
        }

        if (comment.isApplicationCompletionComment()) {
            application.setCompletionDate(comment.getCreatedTimestamp().toLocalDate());
        }
        
        if(comment.isApplicationExportComment()) {
            applicationExportService.export(comment.getApplication());
        }
        
        if (comment.isApplicationPurgeComment()) {
            purgeApplication(application, comment);
        }
    }

    // TODO: set values for "doRetain" (application) and "sendRecommendationEmail" (user account)
    public ActionOutcomeDTO performAction(@PathVariable Integer applicationId, @Valid @RequestBody CommentDTO commentDTO) throws DeduplicationException {
        Application application = entityService.getById(Application.class, applicationId);
        PrismAction actionId = commentDTO.getAction();

        if (actionId == PrismAction.APPLICATION_COMPLETE) {
            validateApplicationCompleteness(applicationId);
        }

        Action action = actionService.getById(actionId);
        User user = userService.getById(commentDTO.getUser());
        User delegateUser = userService.getById(commentDTO.getDelegateUser());
        State transitionState = entityService.getById(State.class, commentDTO.getTransitionState());
        LocalDate positionProvisionalStartDate = commentDTO.getPositionProvisionalStartDate();
        Comment comment = new Comment().withContent(commentDTO.getContent()).withUser(user).withDelegateUser(delegateUser).withAction(action)
                .withTransitionState(transitionState).withCreatedTimestamp(new DateTime())
                .withDeclinedResponse(BooleanUtils.isTrue(commentDTO.getDeclinedResponse())).withQualified(commentDTO.getQualified())
                .withCompetentInWorkLanguage(commentDTO.getCompetentInWorkLanguage())
                .withInterviewDateTime(commentDTO.getInterviewDateTime()).withInterviewTimeZone(commentDTO.getInterviewTimeZone())
                .withInterviewDuration(commentDTO.getInterviewDuration()).withInterviewerInstructions(commentDTO.getInterviewerInstructions())
                .withIntervieweeInstructions(commentDTO.getIntervieweeInstructions()).withInterviewLocation(commentDTO.getInterviewLocation())
                .withSuitableForInstitution(commentDTO.getSuitableForInstitution()).withSuitableForOpportunity(commentDTO.getSuitableForOpportunity())
                .withDesireToInterview(commentDTO.getDesireToInterview()).withDesireToRecruit(commentDTO.getDesireToRecruit())
                .withPositionTitle(commentDTO.getPositionTitle()).withPositionDescription(commentDTO.getPositionDescription())
                .withPositionProvisionalStartDate(positionProvisionalStartDate).withAppointmentConditions(commentDTO.getAppointmentConditions());

        if (commentDTO.getResidenceState() != null) {
            ResidenceState residenceState = entityService.getById(ResidenceState.class, commentDTO.getResidenceState());
            comment.setResidenceState(residenceState);
        }

        if (commentDTO.getDocuments() != null) {
            for (FileDTO fileDTO : commentDTO.getDocuments()) {
                Document document = entityService.getById(Document.class, fileDTO.getId());
                comment.getDocuments().add(document);
            }
        }
        if (commentDTO.getRejectionReason() != null) {
            RejectionReason rejectionReason = entityService.getById(RejectionReason.class, commentDTO.getRejectionReason());
            comment.setContent(rejectionReason.getName());
        }
        if (commentDTO.getAppointmentTimeslots() != null) {
            for (LocalDateTime dateTime : commentDTO.getAppointmentTimeslots()) {
                CommentAppointmentTimeslot timeslot = new CommentAppointmentTimeslot();
                timeslot.setDateTime(dateTime);
                comment.getAppointmentTimeslots().add(timeslot);
            }
        }
        if (commentDTO.getAppointmentPreferences() != null) {
            for (Integer timeslotId : commentDTO.getAppointmentPreferences()) {
                CommentAppointmentTimeslot timeslot = entityService.getById(CommentAppointmentTimeslot.class, timeslotId);
                comment.getAppointmentPreferences().add(new CommentAppointmentPreference().withAppointmentTimeslot(timeslot));
            }
        }

        List<CommentAssignedUser> assignedUsers = Lists.newLinkedList();
        if (actionId.equals(PrismAction.APPLICATION_COMPLETE)) {
            Role refereeRole = entityService.getById(Role.class, PrismRole.APPLICATION_REFEREE);
            for (ApplicationReferee referee : application.getReferees()) {
                assignedUsers.add(new CommentAssignedUser().withComment(comment).withUser(referee.getUser()).withRole(refereeRole));
            }
            Role supervisorRole = entityService.getById(Role.class, PrismRole.APPLICATION_SUGGESTED_SUPERVISOR);
            for (ApplicationSupervisor supervisor : application.getSupervisors()) {
                assignedUsers.add(new CommentAssignedUser().withComment(comment).withUser(supervisor.getUser()).withRole(supervisorRole));
            }
        } else if (commentDTO.getAssignedUsers() != null) {
            for (CommentAssignedUserDTO assignedUserDTO : commentDTO.getAssignedUsers()) {
                UserDTO commentUserDTO = assignedUserDTO.getUser();

                try {
                    User commentUser = userService.getOrCreateUser(commentUserDTO.getFirstName(), commentUserDTO.getLastName(), commentUserDTO.getEmail());
                    assignedUsers.add(new CommentAssignedUser().withUser(commentUser).withRole(entityService.getById(Role.class, assignedUserDTO.getRole())));
                } catch (Exception e) {
                    throw new ResourceNotFoundException();
                }
            }
        }

        comment.getAssignedUsers().addAll(assignedUsers);
        return actionService.executeUserAction(application, action, comment);
    }

    private void purgeApplication(Application application, Comment comment) {
        if (!application.getRetain()) {
            application.setApplicationRatingCount(null);
            application.setApplicationRatingAverage(null);
            application.setProgramDetail(null);
            application.getSupervisors().clear();
            application.setPersonalDetail(null);
            application.setAddress(null);
            application.getQualifications().clear();
            application.getEmploymentPositions().clear();
            application.getFundings().clear();
            application.getReferees().clear();
            application.setDocument(null);
            application.setAdditionalInformation(null);
        }
        commentService.delete(application, comment);
    }

    private void synchroniseProjectSupervisors(Application application) {
        Role supervisorRole = roleService.getById(PrismRole.APPLICATION_SUGGESTED_SUPERVISOR);
        List<User> supervisorUsers = roleService.getRoleUsers(application, supervisorRole);

        for (User supervisorUser : supervisorUsers) {
            application.getSupervisors().add(new ApplicationSupervisor().withUser(supervisorUser).withAware(true));
        }
    }

    private void synchroniseReferees(Application application, Comment comment) {
        ApplicationReferee referee = applicationDAO.getRefereeByUser(application, comment.getUser());
        referee.setComment(comment);
    }

    private void synchroniseOfferRecommendation(Application application, Comment comment) {
        application.setConfirmedStartDate(comment.getPositionProvisionalStartDate());
        application.setConfirmedPrimarySupervisor(roleService.getRoleUsers(application, PrismRole.APPLICATION_PRIMARY_SUPERVISOR).get(0));
        application.setConfirmedSecondarySupervisor(roleService.getRoleUsers(application, PrismRole.APPLICATION_SECONDARY_SUPERVISOR).get(0));
        application.setConfirmedOfferType(comment.getAppointmentConditions() == null ? PrismOfferType.UNCONDITIONAL : PrismOfferType.CONDITIONAL);
    }

    private Application getPreviousApplication(Application application) {
        Application previousApplication = applicationDAO.getPreviousSubmittedApplication(application);
        if (previousApplication == null) {
            previousApplication = applicationDAO.getPreviousUnsubmittedApplication(application);
        }
        return previousApplication;
    }

}
