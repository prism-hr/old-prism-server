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
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;

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
    private StateDAO stateDAO;

    public void save(Comment comment) {
        commentDAO.save(comment);
    }
    
    public List<Comment> getVisibleComments(RegisteredUser user, ApplicationForm applicationForm) {
        return commentDAO.getVisibleComments(user, applicationForm);
    }
    
    public void declineReview(RegisteredUser user, ApplicationForm application) {
        // check if user has already responded 
//        if (!commentDAO.getReviewCommentsForReviewerAndApplication(user, application).isEmpty()) {
//            return;
//        }

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

    public CommentAssignedUser assignUser(AssignSupervisorsComment approvalComment, RegisteredUser user, boolean isPrimary) {
        CommentAssignedUser assignedUser = new CommentAssignedUser();
        assignedUser.setUser(user);
        assignedUser.setPrimary(isPrimary);
        approvalComment.getAssignedUsers().add(assignedUser);
        return assignedUser;
    }
    
    public void postStateChangeComment(StateChangeDTO stateChangeDTO) {
        ApplicationForm applicationForm = stateChangeDTO.getApplicationForm();
        RegisteredUser registeredUser = stateChangeDTO.getRegisteredUser();
        ApplicationFormStatus status = applicationForm.getStatus().getId();
        StateChangeComment stateChangeComment = null;
        
        switch (status) {
            case VALIDATION:
                ValidationComment validationComment = new ValidationComment();
                validationComment.setQualifiedForPhd(stateChangeDTO.getQualifiedForPhd());
                validationComment.setEnglishCompentencyOk(stateChangeDTO.getEnglishCompentencyOk());
                validationComment.setHomeOrOverseas(stateChangeDTO.getHomeOrOverseas());
                stateChangeComment = validationComment;
                stateChangeComment.setUseCustomReferenceQuestions(BooleanUtils.toBooleanObject(stateChangeDTO.getUseCustomReferenceQuestions()));
                stateChangeComment.setUseCustomQuestions(BooleanUtils.toBoolean(stateChangeDTO.getUseCustomQuestions()));
                stateChangeComment.setType(CommentType.VALIDATION);
                break;
            case REVIEW:
                stateChangeComment = new ReviewEvaluationComment();
                stateChangeComment.setType(CommentType.REVIEW_EVALUATION);
                stateChangeComment.setUseCustomQuestions(BooleanUtils.toBoolean(stateChangeDTO.getUseCustomQuestions()));
                break;
            case INTERVIEW:
                stateChangeComment = new InterviewEvaluationComment();
                stateChangeComment.setType(CommentType.INTERVIEW_EVALUATION);
                stateChangeComment.setUseCustomQuestions(BooleanUtils.toBoolean(stateChangeDTO.getUseCustomQuestions()));
                break;
            case APPROVAL:
                stateChangeComment = new ApprovalEvaluationComment();
                stateChangeComment.setType(CommentType.APPROVAL_EVALUATION);
                stateChangeComment.setUseCustomQuestions(BooleanUtils.toBoolean(stateChangeDTO.getUseCustomQuestions()));
                break;
            default:
                throw new ActionNoLongerRequiredException(applicationForm.getApplicationNumber());
        }
        
        stateChangeComment.setApplication(applicationForm);
        stateChangeComment.setUser(registeredUser);
        stateChangeComment.setComment(stateChangeDTO.getComment());
        stateChangeComment.setDocuments(stateChangeDTO.getDocuments());
        
        ApplicationFormStatus nextStatus = stateChangeDTO.getNextStatus();
        stateChangeComment.setNextStatus(nextStatus);
        stateChangeComment.setDelegateAdministrator(null);
        
        if (BooleanUtils.isTrue(stateChangeDTO.hasGlobalAdministrationRights())) {
            if (BooleanUtils.isTrue(stateChangeDTO.getDelegate())) {
                String delegateAdministratorEmail = stateChangeDTO.getDelegateEmail();
                RegisteredUser userToSaveAsDelegate = userService.getUserByEmailIncludingDisabledAccounts(delegateAdministratorEmail);
                
                if (userToSaveAsDelegate == null) {
                    userToSaveAsDelegate = userService.createNewUserInRole(stateChangeDTO.getDelegateFirstName(), stateChangeDTO.getDelegateLastName(), 
                            delegateAdministratorEmail, Authority.STATEADMINISTRATOR);
                }
                
                stateChangeComment.setDelegateAdministrator(userToSaveAsDelegate);
            }
        } else {
            if (status == nextStatus) {
                stateChangeComment.setDelegateAdministrator(registeredUser);
            }
        }
        
        applicationForm.setNextStatus(stateDAO.getById(nextStatus));
        save(stateChangeComment);
        applicationsService.save(applicationForm);
        applicationsService.refresh(applicationForm);
        applicationFormUserRoleService.stateChanged(stateChangeComment);
        applicationFormUserRoleService.insertApplicationUpdate(applicationForm, registeredUser, ApplicationUpdateScope.ALL_USERS);  
    }
    
}
