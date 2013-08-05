package com.zuehlke.pgadmissions.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.InterviewDAO;
import com.zuehlke.pgadmissions.dao.ReviewRoundDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;

@Service
@Transactional
public class ApplicantRatingService {

    @Autowired
    private InterviewDAO interviewDAO;

    @Autowired
    private ReviewRoundDAO reviewRoundDAO;
    
    @Autowired
    private ApplicationFormDAO applicationFormDAO;

    public void computeAverageRating(Interview interview) {
        List<Integer> ratings = Lists.newArrayList();
        for (Interviewer interviewer : interview.getInterviewers()) {
            InterviewComment comment = interviewer.getInterviewComment();
            if (comment != null && comment.getApplicantRating() != null) {
                ratings.add(comment.getApplicantRating());
            }
        }
        BigDecimal avgDecimal = computeAverage(ratings);

        interview.setAverageRating(avgDecimal);
        interviewDAO.save(interview);
    }

    public void computeAverageRating(ReviewRound reviewRound) {
        List<Integer> ratings = Lists.newArrayList();
        for (Reviewer reviewer : reviewRound.getReviewers()) {
            ReviewComment comment = reviewer.getReview();
            if (comment != null && comment.getApplicantRating() != null) {
                ratings.add(comment.getApplicantRating());
            }
        }
        BigDecimal avgDecimal = computeAverage(ratings);

        reviewRound.setAverageRating(avgDecimal);
        reviewRoundDAO.save(reviewRound);
    }

    public void computeAverageRating(ApplicationForm application) {
        List<Comment> comments = application.getApplicationComments();
        List<Integer> ratings = Lists.newArrayList();
        for (Comment comment : comments) {
            if (comment instanceof InterviewComment) {
                ratings.add(((InterviewComment) comment).getApplicantRating());
            } else if (comment instanceof ReviewComment) {
                ratings.add(((ReviewComment) comment).getApplicantRating());
            } else if (comment instanceof ReferenceComment) {
                ratings.add(((ReferenceComment) comment).getApplicantRating());
            }
        }
        
        BigDecimal avgDecimal = computeAverage(ratings);

        application.setAverageRating(avgDecimal);
        applicationFormDAO.save(application);
    }

    private BigDecimal computeAverage(List<Integer> ratings) {
        int sum = 0;
        for (int rating : ratings) {
            sum += rating;
        }
        Double avg = (double) sum / ratings.size();
        BigDecimal avgDecimal = new BigDecimal(avg);
        avgDecimal.setScale(2, RoundingMode.HALF_UP);
        return avgDecimal;
    }
}
