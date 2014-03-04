package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class ReviewCommentMappingTest extends AutomaticRollbackTestCase {

    @Test
    public void shouldSaveAndLoadReviewComment() {

        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        RegisteredUser reviewerUser = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username2")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        Reviewer reviewer = new ReviewerBuilder().user(reviewerUser).build();
        save(applicant, reviewerUser, reviewer);

        ApplicationForm applicationForm = new ApplicationFormBuilder().applicant(applicant).advert(testObjectProvider.getEnabledProgram()).build();
        save(applicationForm);

        flushAndClearSession();

        ReviewComment reviewComment = new ReviewCommentBuilder().reviewer(reviewer).adminsNotified(false).commentType(CommentType.REVIEW)
                .comment("This is a review comment").suitableCandidateForUCL(false).user(reviewerUser).application(applicationForm).decline(true)
                .willingToInterview(false).build();
        save(reviewComment);

        assertNotNull(reviewComment.getId());
        Integer id = reviewComment.getId();

        ReviewComment reloadedComment = (ReviewComment) sessionFactory.getCurrentSession().get(ReviewComment.class, id);
        assertSame(reviewComment, reloadedComment);

        flushAndClearSession();

        reloadedComment = (ReviewComment) sessionFactory.getCurrentSession().get(ReviewComment.class, id);
        assertNotSame(reviewComment, reloadedComment);
        assertEquals(reviewComment.getId(), reloadedComment.getId());

        assertEquals(reviewerUser.getId(), reloadedComment.getUser().getId());
        assertEquals(reviewer.getId(), reloadedComment.getReviewer().getId());
        assertEquals("This is a review comment", reloadedComment.getComment());
        assertFalse(reloadedComment.getSuitableCandidateForUcl());
        assertFalse(reloadedComment.isAdminsNotified());
        assertFalse(reloadedComment.getWillingToInterview());
        assertTrue(reloadedComment.isDecline());
        assertEquals(DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE), DateUtils.truncate(reloadedComment.getDate(), Calendar.DATE));

    }

}
