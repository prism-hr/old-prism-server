package com.zuehlke.pgadmissions.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name="REVIEW_EVALUATION_COMMENT")
public class ReviewEvaluationComment extends StateChangeComment {

	private static final long serialVersionUID = 2184172372328153404L;
	
	@ManyToOne(fetch = FetchType.LAZY) 
	@JoinColumn(name="review_round_id")
	private ReviewRound reviewRound = null;

	public ReviewRound getReviewRound() {
		return reviewRound;
	}

	public void setReviewRound(ReviewRound reviewRound) {
		this.reviewRound = reviewRound;
	}
}
