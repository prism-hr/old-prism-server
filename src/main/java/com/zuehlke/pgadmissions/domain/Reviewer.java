package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity(name = "REVIEWER")
public class Reviewer implements Serializable {

	private static final long serialVersionUID = 7813331086711135352L;

    @Id
    @GeneratedValue
    private Integer id;

	@OneToOne(mappedBy = "reviewer")
	private ReviewComment review;

	@ManyToOne
	@JoinColumn(name = "review_round_id")
	private ReviewRound reviewRound;
	
	@ManyToOne
	@JoinColumn(name = "registered_user_id")
	private RegisteredUser user;

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public RegisteredUser getUser() {
		return user;
	}

	public void setUser(RegisteredUser user) {
		this.user = user;
	}

	public ReviewComment getReview() {
		return review;
	}

	public void setReview(ReviewComment review) {
		this.review = review;
	}

	public ReviewRound getReviewRound() {
		return reviewRound;
	}

	public void setReviewRound(ReviewRound reviewRound) {
		this.reviewRound = reviewRound;
	}

}