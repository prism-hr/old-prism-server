package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ScoreBuilder;
import com.zuehlke.pgadmissions.scoring.jaxb.QuestionType;

public class CommentDAOTest extends AutomaticRollbackTestCase {

    private CommentDAO commentDAO;
    private RegisteredUser user;
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
        user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a24").domicileCode("AE").enabled(true).build();
        program = new ProgramBuilder().code("doesntexist").title("another title").institution(institution).build();

        save(user, institution, program);

        flushAndClearSession();
    }

    @Test
    public void shouldSaveAndLoadGenericComment() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).build();
        save(application);
        flushAndClearSession();

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
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).build();
        save(application);
        flushAndClearSession();

        ReviewComment reviewComment = new ReviewCommentBuilder().application(application).content("comment").user(user).build();

        assertNull(reviewComment.getId());

        commentDAO.save(reviewComment);

        assertNotNull(reviewComment.getId());
        Integer id = reviewComment.getId();
        Comment reloadedComment = commentDAO.get(id);
        assertSame(reviewComment, reloadedComment);

        flushAndClearSession();

        reloadedComment = commentDAO.get(id);

        assertNotSame(reviewComment, reloadedComment);
        assertEquals(reviewComment.getId(), reloadedComment.getId());
        assertEquals(user.getId(), reloadedComment.getUser().getId());
        assertTrue(reloadedComment instanceof ReviewComment);
    }

    @Test
    public void shouldGetAllReviewCommentsDueAdminEmailNotification() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).build();
        save(application);
        flushAndClearSession();

        Comment comment = new CommentBuilder().user(user).comment("comment").application(application).build();
        ReviewComment reviewComment = new ReviewCommentBuilder().application(application).content("comment").user(user).build();
        ReviewComment reviewComment1 = new ReviewCommentBuilder().application(application).content("comment").user(user).build();
        ReviewComment reviewComment2 = new ReviewCommentBuilder().application(application).content("comment").user(user).build();

        save(comment, reviewComment, reviewComment1, reviewComment2);

        flushAndClearSession();

        List<ReviewComment> reloadedComments = commentDAO.getReviewCommentsDueNotification();

        assertFalse(listContainsId(comment, reloadedComments));
        assertFalse(listContainsId(reviewComment2, reloadedComments));
        assertFalse(listContainsId(reviewComment1, reloadedComments));
        assertTrue(listContainsId(reviewComment, reloadedComments));
    }

    @Test
    public void shouldGetAllInterviewCommentsDueAdminEmailNotification() {
        ApplicationForm application = new ApplicationFormBuilder().id(1).program(program).applicant(user).build();
        save(application);
        flushAndClearSession();

        Comment comment = new CommentBuilder().user(user).comment("comment").application(application).build();
        InterviewComment interviewComment1 = new InterviewCommentBuilder().user(user).application(application).content("comment").build();
        InterviewComment interviewComment2 = new InterviewCommentBuilder().user(user).application(application).content("comment").build();
        InterviewComment interviewComment3 = new InterviewCommentBuilder().user(user).application(application).content("comment").build();
        InterviewComment interviewComment4 = new InterviewCommentBuilder().user(user).application(application).content("comment").build();

        save(comment, interviewComment1, interviewComment2, interviewComment3, interviewComment4);

        flushAndClearSession();

        List<InterviewComment> reloadedComments = commentDAO.getInterviewCommentsDueNotification();

        assertFalse(listContainsId(comment, reloadedComments));
        assertTrue(listContainsId(interviewComment1, reloadedComments));
        assertFalse(listContainsId(interviewComment2, reloadedComments));
        assertTrue(listContainsId(interviewComment3, reloadedComments));
        assertFalse(listContainsId(interviewComment4, reloadedComments));
    }

    @Test
    public void shouldReturnNoValidationCommentForApplication() {
        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("834734374lksdh")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).build();

        save(user, application);
        flushAndClearSession();

        ValidationComment returnedComment = commentDAO.getValidationCommentForApplication(application);
        assertNull(returnedComment);
    }

    @Test
    public void shouldReturnCommentWithTwoScores() {
        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("834734374lksdh")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).build();
        Score score1 = new ScoreBuilder().dateResponse(new Date()).question("1??").questionType(QuestionType.RATING).ratingResponse(4).build();
        Score score2 = new ScoreBuilder().dateResponse(new Date()).question("2??").questionType(QuestionType.TEXTAREA).textResponse("aaa").build();
        ReferenceComment comment = new ReferenceCommentBuilder().comment("reference").user(user).application(application).scores(score1, score2).build();

        save(user, application, comment);
        flushAndClearSession();

        Integer commentId = comment.getId();

        ReferenceComment returnedComment = (ReferenceComment) commentDAO.get(commentId);
        assertNotNull(returnedComment);
        assertEquals(2, returnedComment.getScores().size());
    }

    private boolean listContainsId(Comment comment, List<? extends Comment> reloadedComments) {
        for (Comment entry : reloadedComments) {
            if (entry.getId().equals(comment.getId())) {
                return true;
            }
        }
        return false;
    }

}
