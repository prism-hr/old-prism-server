package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity(name="REVIEW_STATE_CHANGE_EVENT")
@Access(AccessType.FIELD)
public class ReviewStateChangeEvent extends StateChangeEvent {

	private static final long serialVersionUID = -67490702968630612L;
	
	@OneToOne
	@JoinColumn(name = "review_round_id")
	private ReviewRound reviewRound;
	
	
	public ReviewRound getReviewRound() {
		return reviewRound;
	}

	public void setReviewRound(ReviewRound reviewRound) {
		this.reviewRound = reviewRound;
	}
}
