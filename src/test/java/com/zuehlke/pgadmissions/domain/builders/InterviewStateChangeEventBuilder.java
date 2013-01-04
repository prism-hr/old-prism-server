package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewStateChangeEvent;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class InterviewStateChangeEventBuilder {

	private Integer id;
	private Date eventDate;	
	private ApplicationFormStatus newStatus;
	private RegisteredUser user;
	private Interview interview;
	
	public InterviewStateChangeEventBuilder interview(Interview interview){
		this.interview = interview;
		return this;
	}
	
	public InterviewStateChangeEventBuilder user(RegisteredUser user){
		this.user = user;
		return this;
	}
	
	public InterviewStateChangeEventBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public InterviewStateChangeEventBuilder date(Date eventDate){
		this.eventDate = eventDate;
		return this;
	}
	
	public InterviewStateChangeEventBuilder newStatus(ApplicationFormStatus newStatus){
		this.newStatus = newStatus;
		return this;
	}
	
	public InterviewStateChangeEvent build() {
		InterviewStateChangeEvent event = new InterviewStateChangeEvent();
		event.setId(id);
		event.setDate(eventDate);
		event.setNewStatus(newStatus);
		event.setUser(user);
		event.setInterview(interview);
		return event;
	}
}
