package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.AssignSupervisorsComment;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.SupervisionConfirmationComment;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.SystemAction;
import com.zuehlke.pgadmissions.dto.ConfirmSupervisionDTO;
import com.zuehlke.pgadmissions.mail.MailSendingService;

@Service
@Transactional
public class ApprovalService {

    @Autowired
    private StateService stateService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private MailSendingService mailSendingService;

    @Autowired
    private WorkflowService applicationFormUserRoleService;

    @Autowired
    private ProgramInstanceService programInstanceService;
    
    @Autowired
    private RoleService roleService;

    @Autowired
    private ApplicationService applicationsService;

    @Autowired
    private ApplicationContext applicationContext;

    public void confirmOrDeclineSupervision(Application form, ConfirmSupervisionDTO confirmSupervisionDTO) {
        ApprovalService thisBean = applicationContext.getBean(ApprovalService.class);

        AssignSupervisorsComment approvalComment = (AssignSupervisorsComment) applicationsService
                .getLatestStateChangeComment(form, SystemAction.APPLICATION_COMPLETE_APPROVAL_STAGE);
        SupervisionConfirmationComment supervisionConfirmationComment = thisBean.createSupervisionConfirmationComment(approvalComment, confirmSupervisionDTO);

        if (BooleanUtils.isTrue(supervisionConfirmationComment.getDeclined())) {
            form.setDueDate(new LocalDate());
        }

        applicationFormUserRoleService.supervisionConfirmed(supervisionConfirmationComment);
    }

    protected SupervisionConfirmationComment createSupervisionConfirmationComment(AssignSupervisorsComment approvalComment, ConfirmSupervisionDTO confirmSupervisionDTO) {
        // TODO use approval comment (before: approvalRound), fix tests and ftl's.

        SupervisionConfirmationComment supervisionConfirmationComment = new SupervisionConfirmationComment();
        // supervisionConfirmationComment.setApplication(approvalComment.getApplication());
        // supervisionConfirmationComment.setDate(new Date());
        // supervisionConfirmationComment.setSupervisor(supervisor);
        // supervisionConfirmationComment.setType(CommentType.SUPERVISION_CONFIRMATION);
        // supervisionConfirmationComment.setUser(userService.getCurrentUser());
        // supervisionConfirmationComment.setComment(StringUtils.EMPTY);
        // supervisionConfirmationComment.setSecondarySupervisor(approvalComment.getSecondarySupervisor());
        //
        // if (BooleanUtils.isTrue(confirmSupervisionDTO.getConfirmedSupervision())) {
        // supervisionConfirmationComment.setProjectTitle(confirmSupervisionDTO.getProjectTitle());
        // supervisionConfirmationComment.setProjectAbstract(confirmSupervisionDTO.getProjectAbstract());
        // supervisionConfirmationComment.setRecommendedStartDate(confirmSupervisionDTO.getRecommendedStartDate());
        // Boolean recommendedConditionsAvailable = confirmSupervisionDTO.getRecommendedConditionsAvailable();
        // supervisionConfirmationComment.setRecommendedConditionsAvailable(recommendedConditionsAvailable);
        // if (BooleanUtils.isTrue(recommendedConditionsAvailable)) {
        // supervisionConfirmationComment.setRecommendedConditions(confirmSupervisionDTO.getRecommendedConditions());
        // } else {
        // supervisionConfirmationComment.setRecommendedConditions(null);
        // }
        // }
        //
        // commentService.save(supervisionConfirmationComment);
        return supervisionConfirmationComment;
    }

    public AssignSupervisorsComment initiateApprovalComment(String applicationId) {
        Application application = applicationsService.getByApplicationNumber(applicationId);
        AssignSupervisorsComment approvalComment = new AssignSupervisorsComment();
        Comment latestApprovalComment = applicationsService.getLatestStateChangeComment(application, SystemAction.APPLICATION_COMPLETE_APPROVAL_STAGE);
        Project project = application.getProject();
        LocalDate startDate = application.getProgramDetails().getStartDate();
        if (latestApprovalComment != null) {
            List<CommentAssignedUser> supervisors = commentService.getNotDecliningSupervisorsFromLatestApprovalStage(application);
            approvalComment.getAssignedUsers().addAll(supervisors);
            if (latestApprovalComment.getProjectDescriptionAvailable() != null) {
                approvalComment.setProjectDescriptionAvailable(latestApprovalComment.getProjectDescriptionAvailable());
                approvalComment.setProjectTitle(latestApprovalComment.getProjectTitle());
                approvalComment.setProjectAbstract(latestApprovalComment.getProjectAbstract());
            }
            startDate = latestApprovalComment.getRecommendedStartDate();
            if (latestApprovalComment.getRecommendedConditionsAvailable() != null) {
                approvalComment.setRecommendedConditionsAvailable(latestApprovalComment.getRecommendedConditionsAvailable());
                approvalComment.setRecommendedConditions(latestApprovalComment.getRecommendedConditions());
            }
        } else if (project != null) {
            User primarySupervisor = roleService.getUserInRole(project, Authority.PROJECT_PRIMARY_SUPERVISOR);
            User secondarySupervisor = roleService.getUserInRole(project, Authority.PROJECT_SECONDARY_SUPERVISOR);
            commentService.assignUser(approvalComment, primarySupervisor, true);
            if (secondarySupervisor != null) {
                commentService.assignUser(approvalComment, secondarySupervisor, false);
            }
            approvalComment.setProjectDescriptionAvailable(true);
            approvalComment.setProjectTitle(project.getTitle());
        }

        if (!programInstanceService.isPrefferedStartDateWithinBounds(application, startDate)) {
            startDate = programInstanceService.getEarliestPossibleStartDate(application);
        }

        approvalComment.setRecommendedStartDate(startDate);
        commentService.save(approvalComment);
        return approvalComment;
    }

    public void moveApplicationToApproval(Application form, Comment newComment, User initiator) {
        checkSendToPorticoStatus(form);

        boolean sendReferenceRequest = form.getState().getId() == PrismState.APPLICATION_VALIDATION;

        applicationsService.setApplicationStatus(form, PrismState.APPLICATION_APPROVAL);

        AssignSupervisorsComment approvalComment = new AssignSupervisorsComment();
        approvalComment.setApplication(form);
        approvalComment.setContent(StringUtils.EMPTY);
        approvalComment.setProjectAbstract(newComment.getProjectAbstract());
        approvalComment.setProjectDescriptionAvailable(newComment.getProjectDescriptionAvailable());
        approvalComment.setProjectTitle(newComment.getProjectTitle());
        approvalComment.setRecommendedConditions(newComment.getRecommendedConditions());
        approvalComment.setRecommendedConditionsAvailable(newComment.getRecommendedConditionsAvailable());
        approvalComment.setRecommendedStartDate(newComment.getRecommendedStartDate());
        approvalComment.getAssignedUsers().addAll(newComment.getAssignedUsers());
        approvalComment.setUser(userService.getCurrentUser());

        if (sendReferenceRequest) {
            mailSendingService.sendReferenceRequest(form.getReferees(), form);
            Comment latestStateChangeComment = applicationsService.getLatestStateChangeComment(form, null);
            form.setUseCustomReferenceQuestions(latestStateChangeComment.getUseCustomReferenceQuestions());
            applicationFormUserRoleService.validationStageCompleted(form);
        }

        commentService.save(approvalComment);
        applicationFormUserRoleService.movedToApprovalStage(approvalComment);
        applicationFormUserRoleService.applicationUpdated(form, initiator);
    }

    private void checkSendToPorticoStatus(Application form) {
        // TODO check if explanation provided if not enough qualifications
//        if (!form.hasEnoughReferencesToSendToPortico() || (!form.hasEnoughQualificationsToSendToPortico())) {
//            throw new IllegalStateException("Send to portico data is not valid");
//        }
    }

}