package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name="REVIEW_EVALUATION_COMMENT")
@Access(AccessType.FIELD)
public class ReviewEvaluationComment extends StateChangeComment {


	private static final long serialVersionUID = 2184172372328153404L;
	
	@ManyToOne
	@JoinColumn(name="review_round_id")
	private ReviewRound reviewRound = null;

	public ReviewRound getReviewRound() {
		return reviewRound;
	}

	public void setReviewRound(ReviewRound reviewRound) {
		this.reviewRound = reviewRound;
	}
}
