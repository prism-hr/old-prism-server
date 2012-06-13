package com.zuehlke.pgadmissions.dto;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.RegisteredUser;

public abstract class TimelineObject implements Comparable<TimelineObject> {

	protected Date eventDate = null;

	protected RegisteredUser author;


	
	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date date) {
		this.eventDate = date;
	}

	public RegisteredUser getAuthor() {
		return author;
	}

	public void setAuthor(RegisteredUser author) {
		this.author = author;
	}

	public abstract String getMessageCode(); 
	@Override
	public int compareTo(TimelineObject otherPhase) {
		return otherPhase.getMostRecentActivityDate().compareTo(this.getMostRecentActivityDate());
	}

	public abstract String getType();

	public abstract Date getMostRecentActivityDate();



}