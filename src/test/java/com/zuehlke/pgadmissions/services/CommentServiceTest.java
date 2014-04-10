package com.zuehlke.pgadmissions.services;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class CommentServiceTest {

    @Mock @InjectIntoByType
    private CommentDAO commentDAO;
    
    @Mock @InjectIntoByType
    private ApplicationFormService applicationsService;
    
    @Mock @InjectIntoByType 
    private WorkflowService applicationFormUserRoleService;
    
    @Mock @InjectIntoByType 
    private UserService userService;
    
    @Mock @InjectIntoByType
    private StateDAO stateDAO;
    
    @TestedObject
    private CommentService service;

    @Test
    public void shouldDelegateCommentSaveToDAO() {
        Comment appReview = EasyMock.createMock(Comment.class);
        commentDAO.save(appReview);
        EasyMock.replay(appReview, commentDAO);
        service.save(appReview);
        EasyMock.verify(commentDAO);
    }

    @Test
    public void shouldDeclineReview() {
        ApplicationForm application = new ApplicationForm();
        User reviewerUser = new UserBuilder().id(1).build();
        final ReviewComment reviewComment = new ReviewCommentBuilder().id(1).user(reviewerUser).build();

        service.declineReview(reviewerUser, application);

        Assert.assertTrue(reviewComment.getDeclined());
        Assert.assertEquals(reviewerUser, reviewComment.getUser());
        Assert.assertEquals(application, reviewComment.getApplication());

        EasyMock.verify(commentDAO);
    }

}