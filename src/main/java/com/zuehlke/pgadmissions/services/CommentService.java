package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.AssignSupervisorsComment;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.CompleteApprovalComment;
import com.zuehlke.pgadmissions.domain.CompleteInterviewComment;
import com.zuehlke.pgadmissions.domain.CompleteReviewComment;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.StateChangeDTO;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;

@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentDAO commentDAO;

    @Autowired
    private ApplicationFormService applicationsService;

    @Autowired
    private WorkflowService applicationFormUserRoleService;

    @Autowired
    private UserService userService;

    @Autowired
    private ManageUsersService manageUsersService;

    @Autowired
    private StateDAO stateDAO;

    public void save(Comment comment) {
        commentDAO.save(comment);
    }
    
    public Comment getById(int id) {
        return commentDAO.getById(id);
    }

    public List<Comment> getVisibleComments(User user, ApplicationForm applicationForm) {
        return commentDAO.getVisibleComments(user, applicationForm);
    }

    public void declineReview(User user, ApplicationForm application) {
        // check if user has already responded
        // if (!commentDAO.getReviewCommentsForReviewerAndApplication(user, application).isEmpty()) {
        // return;
        // }

        ReviewComment reviewComment = new ReviewComment();
        reviewComment.setApplication(application);
        reviewComment.setUser(user);
        reviewComment.setDeclined(true);
        reviewComment.setContent(StringUtils.EMPTY);

        save(reviewComment);
    }

    public List<CommentAssignedUser> getNotDecliningSupervisorsFromLatestApprovalStage(ApplicationForm application) {
        return commentDAO.getNotDecliningSupervisorsFromLatestApprovalStage(application);
    }

    public CommentAssignedUser assignUser(AssignSupervisorsComment approvalComment, User user, boolean isPrimary) {
        CommentAssignedUser assignedUser = new CommentAssignedUser();
        assignedUser.setUser(user);
        assignedUser.setPrimary(isPrimary);
        approvalComment.getAssignedUsers().add(assignedUser);
        return assignedUser;
    }

    public void postStateChangeComment(StateChangeDTO stateChangeDTO) {
        ApplicationForm applicationForm = stateChangeDTO.getApplicationForm();
        User user = stateChangeDTO.getUser();
        ApplicationFormStatus status = applicationForm.getState().getId();
        Comment stateChangeComment = null;

        switch (status) {
        case APPLICATION_VALIDATION:
            ValidationComment validationComment = new ValidationComment();
            validationComment.setQualifiedForPhd(stateChangeDTO.getQualifiedForPhd());
            validationComment.setEnglishCompetencyOk(stateChangeDTO.getEnglishCompentencyOk());
            validationComment.setHomeOrOverseas(stateChangeDTO.getHomeOrOverseas());
            stateChangeComment = validationComment;
            stateChangeComment.setUseCustomReferenceQuestions(BooleanUtils.toBooleanObject(stateChangeDTO.getUseCustomReferenceQuestions()));
            break;
        case APPLICATION_REVIEW:
            stateChangeComment = new CompleteReviewComment();
            break;
        case APPLICATION_INTERVIEW:
            stateChangeComment = new CompleteInterviewComment();
            break;
        case APPLICATION_APPROVAL:
            stateChangeComment = new CompleteApprovalComment();
            break;
        default:
            throw new ActionNoLongerRequiredException(applicationForm.getApplicationNumber());
        }

        stateChangeComment.setApplication(applicationForm);
        stateChangeComment.setUser(user);
        stateChangeComment.setContent(stateChangeDTO.getComment());
        stateChangeComment.getDocuments().addAll(stateChangeDTO.getDocuments());
        stateChangeComment.setUseCustomQuestions(BooleanUtils.toBoolean(stateChangeDTO.getUseCustomQuestions()));

        ApplicationFormStatus nextStatus = stateChangeDTO.getNextStatus();
        stateChangeComment.setNextStatus(nextStatus);
        stateChangeComment.setDelegateAdministrator(null);

        // TODO check if has global administration rights (PermissionsService)
        if (true) {
            if (BooleanUtils.isTrue(stateChangeDTO.getDelegate())) {
                User userToSaveAsDelegate = manageUsersService.setUserRoles(stateChangeDTO.getDelegateFirstName(), stateChangeDTO.getDelegateLastName(),
                        stateChangeDTO.getDelegateEmail(), true, false, manageUsersService.getPrismSystem(), Authority.APPLICATION_ADMINISTRATOR);

                stateChangeComment.setDelegateAdministrator(userToSaveAsDelegate);
            }
        } else {
            if (status == nextStatus) {
                stateChangeComment.setDelegateAdministrator(user);
            }
        }

        // TODO set relevant state
//        applicationForm.setNextState(stateDAO.getById(nextStatus));
        save(stateChangeComment);
        applicationsService.save(applicationForm);
        applicationsService.refresh(applicationForm);
        applicationFormUserRoleService.stateChanged(stateChangeComment);
        applicationFormUserRoleService.applicationUpdated(applicationForm, user);
    }

    public <T extends Comment> T getLastCommentOfType(User user, ApplicationForm applicationForm, Class<T> clazz) {
        return commentDAO.getLastCommentOfType(user, applicationForm, clazz);
    }

    public <T extends Comment> T getLastCommentOfType(ApplicationForm applicationForm, Class<T> clazz) {
        return getLastCommentOfType(null, applicationForm, clazz);
    }

    public Comment getLastComment(ApplicationForm form) {
        // TODO Auto-generated method stub
        return null;
    }

}
