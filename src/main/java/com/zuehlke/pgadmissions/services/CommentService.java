package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.EntityDAO;
import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.AssignSupervisorsComment;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.CompleteApprovalComment;
import com.zuehlke.pgadmissions.domain.CompleteInterviewComment;
import com.zuehlke.pgadmissions.domain.CompleteReviewComment;
import com.zuehlke.pgadmissions.domain.PrismResourceTransient;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.dto.StateChangeDTO;
import com.zuehlke.pgadmissions.exceptions.CannotExecuteActionException;

@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentDAO commentDAO;

    @Autowired
    private ApplicationService applicationsService;

    @Autowired
    private WorkflowService applicationFormUserRoleService;

    @Autowired
    private UserService userService;

    @Autowired
    private ManageUsersService manageUsersService;

    @Autowired
    private StateDAO stateDAO;
    
    @Autowired
    private SystemService systemService;
    
    @Autowired
    private EntityDAO entityDAO;
    
    public Comment getById(int id) {
        return entityDAO.getById(Comment.class, id);
    }
    
    public void save(Comment comment) {
        entityDAO.save(comment);
    }
    
    public Comment getLastComment(PrismResourceTransient resource) {
        return commentDAO.getLastComment(resource);
    }
    
    public <T extends Comment> T getLastCommentOfType(PrismResourceTransient resource, Class<T> clazz) {
        return getLastCommentOfType(resource, clazz);
    }
    
    public <T extends Comment> T getLastCommentOfType(PrismResourceTransient resource, Class<T> clazz, User author) {
        return commentDAO.getLastCommentOfType(resource, clazz, author);
    }
    
    public List<User> getAssignedUsersByRole(Comment comment, Role role, User invoker) {
        return commentDAO.getAssignedUsersByRole(comment, role, invoker);
    }
    
    // TODO: rewrite below

    public List<Comment> getVisibleComments(User user, Application applicationForm) {
        return commentDAO.getVisibleComments(user, applicationForm);
    }

    public void declineReview(User user, Application application) {
        // check if user has already responded
        // if (!commentDAO.getReviewCommentsForReviewerAndApplication(user, application).isEmpty()) {
        // return;
        // }

        ReviewComment reviewComment = new ReviewComment();
        reviewComment.setApplication(application);
        reviewComment.setUser(user);
        reviewComment.setDeclinedResponse(true);
        reviewComment.setContent(StringUtils.EMPTY);

        save(reviewComment);
    }

    public List<CommentAssignedUser> getNotDecliningSupervisorsFromLatestApprovalStage(Application application) {
        return commentDAO.getNotDecliningSupervisorsFromLatestApprovalStage(application);
    }

    public CommentAssignedUser assignUser(AssignSupervisorsComment approvalComment, User user, boolean isPrimary) {
        CommentAssignedUser assignedUser = new CommentAssignedUser();
        assignedUser.setUser(user);
        approvalComment.getCommentAssignedUsers().add(assignedUser);
        return assignedUser;
    }

    public void postStateChangeComment(StateChangeDTO stateChangeDTO) {
        Application applicationForm = stateChangeDTO.getApplicationForm();
        User user = stateChangeDTO.getUser();
        PrismState status = applicationForm.getState().getId();
        Comment stateChangeComment = null;

        switch (status) {
        case APPLICATION_VALIDATION:
            ValidationComment validationComment = new ValidationComment();
            validationComment.setQualified(stateChangeDTO.getQualifiedForPhd());
            validationComment.setCompetentInWorkLanguage(stateChangeDTO.getEnglishCompentencyOk());
            validationComment.setResidenceStatus(stateChangeDTO.getHomeOrOverseas());
            stateChangeComment = validationComment;
            stateChangeComment.setUseCustomRefereeQuestions(BooleanUtils.toBooleanObject(stateChangeDTO.getUseCustomReferenceQuestions()));
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
            throw new CannotExecuteActionException(applicationForm);
        }

        stateChangeComment.setApplication(applicationForm);
        stateChangeComment.setUser(user);
        stateChangeComment.setContent(stateChangeDTO.getComment());
        stateChangeComment.getDocuments().addAll(stateChangeDTO.getDocuments());
        stateChangeComment.setUseCustomRecruiterQuestions(BooleanUtils.toBoolean(stateChangeDTO.getUseCustomQuestions()));

        // TODO set relevant state
//        applicationForm.setNextState(stateDAO.getById(nextStatus));
        save(stateChangeComment);
        applicationsService.save(applicationForm);
        applicationsService.refresh(applicationForm);
        applicationFormUserRoleService.stateChanged(stateChangeComment);
        applicationFormUserRoleService.applicationUpdated(applicationForm, user);
    }

}
