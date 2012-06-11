package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class EventBuilder {

	private Integer id;
	private Date eventDate;	
	private ApplicationFormStatus newStatus;
	private RegisteredUser user;
	
	public EventBuilder user(RegisteredUser user){
		this.user = user;
		return this;
	}
	
	public EventBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public EventBuilder date(Date eventDate){
		this.eventDate = eventDate;
		return this;
	}
	
	public EventBuilder newStatus(ApplicationFormStatus newStatus){
		this.newStatus = newStatus;
		return this;
	}
	
	public Event toEvent(){
		Event event = new Event();
		event.setId(id);
		event.setDate(eventDate);
		event.setNewStatus(newStatus);
		event.setUser(user);
		return event;
	}
}
