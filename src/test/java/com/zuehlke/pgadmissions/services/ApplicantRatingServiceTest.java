package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.InterviewDAO;
import com.zuehlke.pgadmissions.dao.ReviewRoundDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ApplicantRatingServiceTest {

    @TestedObject
    private ApplicantRatingService applicantRatingService;

    @Mock
    @InjectIntoByType
    private InterviewDAO interviewDAO;

    @Mock
    @InjectIntoByType
    private ReviewRoundDAO reviewRoundDAO;

    @Mock
    @InjectIntoByType
    private ApplicationFormDAO applicationFormDAO;

    @Test
    public void shouldComputeAverageApplicationFormRating() {
        InterviewComment interviewComment = new InterviewCommentBuilder().applicantRating(1).build();
        ReviewComment reviewComment = new ReviewCommentBuilder().applicantRating(2).build();
        ReferenceComment referenceComment = new ReferenceCommentBuilder().applicantRating(3).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().comments(interviewComment, reviewComment, referenceComment).build();

        applicationFormDAO.save(applicationForm);

        replay();
        applicantRatingService.computeAverageRating(applicationForm);
        verify();

        BigDecimal averageRating = applicationForm.getAverageRating();
        assertEquals(new BigDecimal(2), averageRating);
    }

    @Test
    public void shouldComputeAverageApplicationFormRatingWithNoComments() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().build();

        applicationFormDAO.save(applicationForm);

        replay();
        applicantRatingService.computeAverageRating(applicationForm);
        verify();

        BigDecimal averageRating = applicationForm.getAverageRating();
        assertNull(averageRating);
    }

    @Test
    public void shouldComputeAverageInterviewRating() {
        InterviewComment interviewComment1 = new InterviewCommentBuilder().applicantRating(1).build();
        InterviewComment interviewComment2 = new InterviewCommentBuilder().applicantRating(3).build();

        Interviewer interviewer1 = new InterviewerBuilder().interviewComment(interviewComment1).build();
        Interviewer interviewer2 = new InterviewerBuilder().interviewComment(interviewComment2).build();

        Interview interview = new InterviewBuilder().interviewers(interviewer1, interviewer2).build();

        interviewDAO.save(interview);

        replay();
        applicantRatingService.computeAverageRating(interview);
        verify();

        BigDecimal averageRating = interview.getAverageRating();
        assertEquals(new BigDecimal(2), averageRating);
    }

    @Test
    public void shouldComputeAverageReviewRoundRating() {
        ReviewComment reviewComment1 = new ReviewCommentBuilder().applicantRating(1).build();
        ReviewComment reviewComment2 = new ReviewCommentBuilder().applicantRating(2).build();

        Reviewer reviewer1 = new ReviewerBuilder().review(reviewComment1).build();
        Reviewer reviewer2 = new ReviewerBuilder().review(reviewComment2).build();

        ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(reviewer1, reviewer2).build();

        reviewRoundDAO.save(reviewRound);

        replay();
        applicantRatingService.computeAverageRating(reviewRound);
        verify();

        BigDecimal averageRating = reviewRound.getAverageRating();
        assertEquals(new BigDecimal(1.5), averageRating);
    }

}
