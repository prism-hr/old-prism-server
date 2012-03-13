package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationReview;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class ApplicationReviewBuilder {
	
	private ApplicationForm application;
	private RegisteredUser user;
	private String comment;
	private Integer id;
	
	public ApplicationReviewBuilder id(Integer id) {
		this.id = id;
		return this;
	}
	
	public ApplicationReviewBuilder application (ApplicationForm application){
		this.application = application;
		return this;
	}
	public ApplicationReviewBuilder comment (String comment){
		this.comment = comment;
		return this;
	}
	public ApplicationReviewBuilder user (RegisteredUser user){
		this.user = user;
		return this;
	}
	
	public ApplicationReview toApplicationReview() {
		ApplicationReview applicationReview = new ApplicationReview();
		applicationReview.setApplication(application);
		applicationReview.setComment(comment);
		applicationReview.setId(id);
		applicationReview.setUser(user);
		return applicationReview;
	}
}
