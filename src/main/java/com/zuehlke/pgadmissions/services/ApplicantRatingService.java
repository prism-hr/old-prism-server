package com.zuehlke.pgadmissions.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.InterviewDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.Interviewer;

@Service
@Transactional
public class ApplicantRatingService {

    @Autowired
    private InterviewDAO interviewDAO;

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

    public void computeAverageRating(ApplicationForm application) {
        application.getApplicationComments();
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
