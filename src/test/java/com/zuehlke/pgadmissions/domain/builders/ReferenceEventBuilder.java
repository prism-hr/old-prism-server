package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceEvent;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class ReferenceEventBuilder {

	private Integer id;
	private Date eventDate;	
	private RegisteredUser user;	
	private Referee referee;
	
	public ReferenceEventBuilder user(RegisteredUser user){
		this.user = user;
		return this;
	}
	
	public ReferenceEventBuilder referee(Referee referee){
		this.referee = referee;
		return this;
	} 
	public ReferenceEventBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public ReferenceEventBuilder date(Date eventDate){
		this.eventDate = eventDate;
		return this;
	}

	public ReferenceEvent build() {
		ReferenceEvent event = new ReferenceEvent();
		event.setId(id);
		event.setDate(eventDate);
		event.setReferee(referee);
		event.setUser(user);
		return event;
	}
}
