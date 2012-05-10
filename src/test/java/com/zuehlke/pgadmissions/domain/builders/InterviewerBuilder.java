package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class InterviewerBuilder {
	private Integer id;
	private RegisteredUser user;
	private ApplicationForm application;
	private Date lastNotified;	
	
	public InterviewerBuilder lastNotified(Date lastNotified){
		this.lastNotified = lastNotified;
		return this;
	}
	
	public InterviewerBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public InterviewerBuilder application(ApplicationForm application){
		this.application = application;
		return this;
	}
	
	public InterviewerBuilder user(RegisteredUser user){
		this.user = user;
		return this;
	}
	
	public  Interviewer toInterviewer(){
		Interviewer interviewer = new Interviewer();
		interviewer.setId(id);
		interviewer.setApplication(application);
		interviewer.setUser(user);
		interviewer.setLastNotified(lastNotified);
		return interviewer;
	}
}
