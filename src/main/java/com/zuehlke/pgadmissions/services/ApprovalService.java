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
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.dto.ConfirmSupervisionDTO;
import com.zuehlke.pgadmissions.mail.NotificationService;

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
    private NotificationService mailSendingService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ApplicationService applicationsService;

    @Autowired
    private ApplicationContext applicationContext;

    public void confirmOrDeclineSupervision(Application form, ConfirmSupervisionDTO confirmSupervisionDTO) {
        Comment approvalComment = applicationsService.getLatestStateChangeComment(form,
                PrismAction.APPLICATION_COMPLETE_APPROVAL_STAGE);

        if (BooleanUtils.isTrue(approvalComment.getDeclinedResponse())) {
            form.setDueDate(new LocalDate());
        }

    }

    public Comment initiateApprovalComment(String applicationId) {
        Application application = applicationsService.getByApplicationNumber(applicationId);
        Comment approvalComment = new Comment();
        Comment latestApprovalComment = applicationsService.getLatestStateChangeComment(application, PrismAction.APPLICATION_COMPLETE_APPROVAL_STAGE);
        Project project = application.getProject();
        LocalDate startDate = application.getProgramDetails().getStartDate();
        if (latestApprovalComment != null) {
            List<CommentAssignedUser> supervisors = commentService.getNotDecliningSupervisorsFromLatestApprovalStage(application);
            approvalComment.getCommentAssignedUsers().addAll(supervisors);
            approvalComment.setPositionTitle(latestApprovalComment.getPositionTitle());
            approvalComment.setPositionDescription(latestApprovalComment.getPositionDescription());
            approvalComment.setAppointmentConditions(latestApprovalComment.getAppointmentConditions());
        } else if (project != null) {
            User primarySupervisor = roleService.getUserInRole(project, Authority.PROJECT_PRIMARY_SUPERVISOR);
            User secondarySupervisor = roleService.getUserInRole(project, Authority.PROJECT_SECONDARY_SUPERVISOR);
            commentService.assignUser(approvalComment, primarySupervisor, true);
            if (secondarySupervisor != null) {
                commentService.assignUser(approvalComment, secondarySupervisor, false);
            }
            approvalComment.setPositionTitle(project.getTitle());
        }

//        if (!programInstanceService.isPrefferedStartDateWithinBounds(application, startDate)) {
//            startDate = programInstanceService.getEarliestPossibleStartDate(application);
//        }

        approvalComment.setPositionProvisionalStartDate(startDate);
        commentService.save(approvalComment);
        return approvalComment;
    }

    public void moveApplicationToApproval(Application form, Comment newComment, User initiator) {
        checkSendToPorticoStatus(form);
     // TODO: remove class and integrate with workflow engine
  //      applicationsService.setApplicationStatus(form, PrismState.APPLICATION_APPROVAL);

        Comment approvalComment = new Comment();
        approvalComment.setApplication(form);
        approvalComment.setContent(StringUtils.EMPTY);
        approvalComment.setPositionDescription(newComment.getPositionDescription());
        approvalComment.setPositionTitle(newComment.getPositionTitle());
        approvalComment.setAppointmentConditions(newComment.getAppointmentConditions());
        approvalComment.setPositionProvisionalStartDate(newComment.getPositionProvisionalStartDate());
        approvalComment.getCommentAssignedUsers().addAll(newComment.getCommentAssignedUsers());
        approvalComment.setUser(userService.getCurrentUser());

        commentService.save(approvalComment);
    }

    private void checkSendToPorticoStatus(Application form) {
        // TODO check if explanation provided if not enough qualifications
        // if (!form.hasEnoughReferencesToSendToPortico() || (!form.hasEnoughQualificationsToSendToPortico())) {
        // throw new IllegalStateException("Send to portico data is not valid");
        // }
    }

}