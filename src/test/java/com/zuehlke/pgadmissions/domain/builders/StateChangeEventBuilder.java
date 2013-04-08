package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class StateChangeEventBuilder {

	private Integer id;
	private Date eventDate;	
	private ApplicationFormStatus newStatus;
	private RegisteredUser user;
	private ApplicationForm application;
	
	public StateChangeEventBuilder application(ApplicationForm application){
		this.application = application;
		return this;
	} 
	
	public StateChangeEventBuilder user(RegisteredUser user){
		this.user = user;
		return this;
	}
	
	public StateChangeEventBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public StateChangeEventBuilder date(Date eventDate){
		this.eventDate = eventDate;
		return this;
	}
	
	public StateChangeEventBuilder newStatus(ApplicationFormStatus newStatus){
		this.newStatus = newStatus;
		return this;
	}
	
	public StateChangeEvent build() {
		StateChangeEvent event = new StateChangeEvent();	
		event.setId(id);
		event.setDate(eventDate);
		event.setNewStatus(newStatus);
		event.setUser(user);
		event.setApplication(application);
		return event;
	}
}
