package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ScoreBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.scoring.jaxb.QuestionType;

public class CommentDAOTest extends AutomaticRollbackTestCase {

    private CommentDAO commentDAO;

    private User user;

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        CommentDAO reviewDAO = new CommentDAO();
        Comment review = new CommentBuilder().id(1).build();
        reviewDAO.save(review);
    }

    @Before
    public void prepare() {
        commentDAO = new CommentDAO(sessionFactory);
    }

    @Test
    public void shouldSaveAndLoadGenericComment() {
        User user = testObjectProvider.getEnabledUserInRole(Authority.APPLICATION_CREATOR);
        Application application = testObjectProvider.getApplication(PrismState.APPLICATION_APPROVAL);

        Comment review = new Comment().withApplication(application).withContent("Excellent Application!!!").withUser(user).withCreatedTimestamp(new DateTime())
                .withRole("ADMIN");
        review.setUser(user);

        assertNull(review.getId());

        commentDAO.save(review);

        assertNotNull(review.getId());
        Integer id = review.getId();
        Comment reloadedReview = commentDAO.get(id);
        assertSame(review, reloadedReview);

        flushAndClearSession();

        reloadedReview = commentDAO.get(id);
        assertNotSame(review, reloadedReview);
        assertEquals(review.getId(), reloadedReview.getId());
        assertEquals(review.getUser().getId(), user.getId());
        assertEquals(review.getContent(), reloadedReview.getContent());
    }

    @Test
    public void shouldReturnCommentWithTwoScores() {
        User user = testObjectProvider.getEnabledUserInRole(Authority.APPLICATION_CREATOR);
        Application application = testObjectProvider.getApplication(PrismState.APPLICATION_APPROVAL);
        
        Score score1 = new ScoreBuilder().dateResponse(new Date()).question("1??").questionType(QuestionType.RATING).ratingResponse(4).build();
        Score score2 = new ScoreBuilder().dateResponse(new Date()).question("2??").questionType(QuestionType.TEXTAREA).textResponse("aaa").build();
        Comment comment = new Comment().withApplication(application).withContent("Excellent Application!!!").withUser(user).withCreatedTimestamp(new DateTime())
                .withRole("ADMIN");
        
        save(user, application, comment);
        flushAndClearSession();

        Integer commentId = comment.getId();

        ReferenceComment returnedComment = (ReferenceComment) commentDAO.get(commentId);
        assertNotNull(returnedComment);
        assertEquals(2, returnedComment.getScores().size());
    }

}
