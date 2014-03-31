package com.zuehlke.pgadmissions.services;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;

public class CommentServiceTest {

    private CommentDAO commentDAOMock;
    private CommentService service;

    @Test
    public void shouldDelegateCommentSaveToDAO() {
        Comment appReview = EasyMock.createMock(Comment.class);
        commentDAOMock.save(appReview);
        EasyMock.replay(appReview, commentDAOMock);
        service.save(appReview);
        EasyMock.verify(commentDAOMock);
    }

    @Test
    public void shouldDeclineReview() {
        ApplicationForm application = new ApplicationForm();
        RegisteredUser reviewerUser = new RegisteredUserBuilder().id(1).build();
        final ReviewComment reviewComment = new ReviewCommentBuilder().id(1).user(reviewerUser).build();

        service.declineReview(reviewerUser, application);

        Assert.assertTrue(reviewComment.getDeclined());
        Assert.assertEquals(reviewerUser, reviewComment.getUser());
        Assert.assertEquals(application, reviewComment.getApplication());

        EasyMock.verify(commentDAOMock);
    }

    @Before
    public void setUp() {
        commentDAOMock = EasyMock.createMock(CommentDAO.class);
        service = new CommentService(commentDAOMock);
    }
}