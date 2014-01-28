package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;

public class ReviewerBuilder {
    private Integer id;
    private RegisteredUser user;
    private Date lastNotified;
    private ReviewComment review;
    private ReviewRound reviewRound;

    public ReviewerBuilder reviewRound(ReviewRound reviewRound) {
        this.reviewRound = reviewRound;
        return this;
    }

    public ReviewerBuilder review(ReviewComment review) {
        this.review = review;
        return this;
    }

    public ReviewerBuilder lastNotified(Date lastNotified) {
        this.lastNotified = lastNotified;
        return this;
    }

    public ReviewerBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ReviewerBuilder user(RegisteredUser user) {
        this.user = user;
        return this;
    }

    public Reviewer build() {
        Reviewer reviewer = new Reviewer();
        reviewer.setId(id);
        reviewer.setReviewRound(reviewRound);
        reviewer.setUser(user);
        reviewer.setLastNotified(lastNotified);
        reviewer.setReview(review);
        return reviewer;
    }
}
