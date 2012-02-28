package com.zuehlke.pgadmissions.pagemodels;

import java.util.List;

import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class ReviewersListModel extends PageModel {

	private List<RegisteredUser> reviewers;

	public void setReviewers(List<RegisteredUser> reviewers) {
		this.reviewers = reviewers;
	}

	public List<RegisteredUser> getReviewers() {
		return reviewers;
	}

}
