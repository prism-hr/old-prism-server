package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ScoreBuilder;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.scoring.jaxb.QuestionType;

public class CommentDAOTest extends AutomaticRollbackTestCase {

    private CommentDAO commentDAO;

    private User user;
    private Program program;

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        CommentDAO reviewDAO = new CommentDAO();
        Comment review = new CommentBuilder().id(1).build();
        reviewDAO.save(review);
    }

    @Before
    public void prepare() {
        commentDAO = new CommentDAO(sessionFactory);
        user = new User().withFirstName("Jane").withLastName("Doe").withEmail("email2@test.com").withActivationCode("code")
                .withAccount(new UserAccount().withEnabled(false).withPassword("haslo"));
        save(user);
        flushAndClearSession();
        program = testObjectProvider.getEnabledProgram();
    }

    @Test
    public void shouldSaveAndLoadGenericComment() {
        Application application = testObjectProvider.getApplication(PrismState.APPLICATION_APPROVAL);

        Comment review = new Comment();
        review.setApplication(application);
        review.setContent("Excellent Application!!!");
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
    public void shouldSaveAndLoadReviewComment() {
        Application application = new ApplicationFormBuilder().id(1).program(program).applicant(user).build();
        save(application);
        flushAndClearSession();

        Comment comment = new Comment();

        assertNull(comment.getId());

        commentDAO.save(comment);

        assertNotNull(comment.getId());
        Integer id = comment.getId();
        Comment reloadedComment = commentDAO.get(id);
        assertSame(comment, reloadedComment);

        flushAndClearSession();

        reloadedComment = commentDAO.get(id);

        assertNotSame(comment, reloadedComment);
        assertEquals(comment.getId(), reloadedComment.getId());
        assertEquals(user.getId(), reloadedComment.getUser().getId());
        assertTrue(reloadedComment instanceof ReviewComment);
    }

    @Test
    public void shouldReturnCommentWithTwoScores() {
        User user = new User().withFirstName("Jane").withLastName("Doe").withEmail("email@test.com")
                .withAccount(new UserAccount().withPassword("password").withEnabled(false));

        Application application = new ApplicationFormBuilder().program(program).applicant(user).build();
        Score score1 = new ScoreBuilder().dateResponse(new Date()).question("1??").questionType(QuestionType.RATING).ratingResponse(4).build();
        Score score2 = new ScoreBuilder().dateResponse(new Date()).question("2??").questionType(QuestionType.TEXTAREA).textResponse("aaa").build();
        Comment comment = new Comment(); //.comment("reference").user(user).application(application).scores(score1, score2).build();

        save(user, application, comment);
        flushAndClearSession();

        Integer commentId = comment.getId();

        ReferenceComment returnedComment = (ReferenceComment) commentDAO.get(commentId);
        assertNotNull(returnedComment);
        assertEquals(2, returnedComment.getScores().size());
    }

}
