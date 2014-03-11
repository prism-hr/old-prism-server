package com.zuehlke.pgadmissions.services;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.AssignSupervisorsComment;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.SupervisionConfirmationComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.dto.ConfirmSupervisionDTO;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.utils.DateUtils;

@Service
@Transactional
public class ApprovalService {

    @Autowired
    private ApplicationFormDAO applicationDAO;

    @Autowired
    private StageDurationService stageDurationService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private MailSendingService mailSendingService;

    @Autowired
    private ApplicationFormUserRoleService applicationFormUserRoleService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private ApplicationsService applicationsService;

    @Autowired
    private ApplicationContext applicationContext;

    public void confirmOrDeclineSupervision(ApplicationForm form, ConfirmSupervisionDTO confirmSupervisionDTO) {
        ApprovalService thisBean = applicationContext.getBean(ApprovalService.class);

        AssignSupervisorsComment approvalComment = (AssignSupervisorsComment) applicationsService
                .getLatestStateChangeComment(form, ApplicationFormAction.COMPLETE_APPROVAL_STAGE);
        SupervisionConfirmationComment supervisionConfirmationComment = thisBean.createSupervisionConfirmationComment(approvalComment, confirmSupervisionDTO);

        if (BooleanUtils.isTrue(supervisionConfirmationComment.getDeclined())) {
            form.setDueDate(new Date());
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
        ApplicationForm application = applicationDAO.getApplicationByApplicationNumber(applicationId);
        AssignSupervisorsComment approvalComment = new AssignSupervisorsComment();
        Comment latestApprovalComment = applicationsService.getLatestStateChangeComment(application, ApplicationFormAction.COMPLETE_APPROVAL_STAGE);
        Project project = application.getProject();
        Date startDate = application.getProgrammeDetails().getStartDate();
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
            commentService.assignUser(approvalComment, project.getPrimarySupervisor(), true);
            RegisteredUser secondarySupervisor = project.getSecondarySupervisor();
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

    public void moveApplicationToApproval(ApplicationForm form, Comment newComment, RegisteredUser initiator) {
        checkApplicationStatus(form);
        checkSendToPorticoStatus(form);

        StageDuration approveStageDuration = stageDurationService.getByStatus(ApplicationFormStatus.APPROVAL);
        DateTime dueDate = DateUtils.addWorkingDaysInMinutes(new DateTime(), approveStageDuration.getDurationInMinutes());
        form.setDueDate(dueDate.toDate());

        boolean sendReferenceRequest = form.getStatus() == ApplicationFormStatus.VALIDATION;

        form.setStatus(ApplicationFormStatus.APPROVAL);

        applicationDAO.save(form);

        Comment approvalComment = new AssignSupervisorsComment();
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
            applicationDAO.save(form);
            applicationFormUserRoleService.validationStageCompleted(form);
        }

        commentService.save(approvalComment);
        applicationFormUserRoleService.movedToApprovalStage(approvalComment);
        applicationFormUserRoleService.insertApplicationUpdate(form, initiator, ApplicationUpdateScope.ALL_USERS);
    }

    private void checkApplicationStatus(ApplicationForm form) {
        ApplicationFormStatus status = form.getStatus();
        switch (status) {
        case VALIDATION:
        case REVIEW:
        case INTERVIEW:
        case APPROVAL:
            break;
        default:
            throw new IllegalStateException(String.format("Application in invalid status: '%s'!", status));
        }
    }

    private void checkSendToPorticoStatus(ApplicationForm form) {
        // TODO check if explanation provided if not enough qualifications
        if (!form.hasEnoughReferencesToSendToPortico() || (!form.hasEnoughQualificationsToSendToPortico())) {
            throw new IllegalStateException("Send to portico data is not valid");
        }
    }

}