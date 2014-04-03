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
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
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
        Comment stateChangeComment = null;
        
        switch (status) {
            case VALIDATION:
                ValidationComment validationComment = new ValidationComment();
                validationComment.setQualifiedForPhd(stateChangeDTO.getQualifiedForPhd());
                validationComment.setEnglishCompetencyOk(stateChangeDTO.getEnglishCompentencyOk());
                validationComment.setHomeOrOverseas(stateChangeDTO.getHomeOrOverseas());
                stateChangeComment = validationComment;
                stateChangeComment.setUseCustomReferenceQuestions(BooleanUtils.toBooleanObject(stateChangeDTO.getUseCustomReferenceQuestions()));
                break;
            case REVIEW:
                stateChangeComment = new CompleteReviewComment();
                break;
            case INTERVIEW:
                stateChangeComment = new CompleteInterviewComment();
                break;
            case APPROVAL:
                stateChangeComment = new CompleteApprovalComment();
                break;
            default:
                throw new ActionNoLongerRequiredException(applicationForm.getApplicationNumber());
        }
        
        stateChangeComment.setApplication(applicationForm);
        stateChangeComment.setUser(registeredUser);
        stateChangeComment.setContent(stateChangeDTO.getComment());
        stateChangeComment.getDocuments().addAll(stateChangeDTO.getDocuments());
        stateChangeComment.setUseCustomQuestions(BooleanUtils.toBoolean(stateChangeDTO.getUseCustomQuestions()));
        
        ApplicationFormStatus nextStatus = stateChangeDTO.getNextStatus();
        stateChangeComment.setNextStatus(nextStatus);
        stateChangeComment.setDelegateAdministrator(null);
        
        // TODO check if has global administration rights (PermissionsService) 
        if (true) {
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
