package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;

public class ReviewerBuilder {
	private Integer id;
	private RegisteredUser user;
	private ApplicationForm application;
	private Date lastNotified;
	
	public ReviewerBuilder lastNotified(Date lastNotified){
		this.lastNotified = lastNotified;
		return this;
	}
	
	public ReviewerBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public ReviewerBuilder application(ApplicationForm application){
		this.application = application;
		return this;
	}
	
	public ReviewerBuilder user(RegisteredUser user){
		this.user = user;
		return this;
	}
	
	public  Reviewer toReviewer(){
		Reviewer reviewer = new Reviewer();
		reviewer.setId(id);
		reviewer.setApplication(application);
		reviewer.setUser(user);
		reviewer.setLastNotified(lastNotified);
		return reviewer;
	}
}
