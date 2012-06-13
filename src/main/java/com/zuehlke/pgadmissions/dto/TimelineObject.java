package com.zuehlke.pgadmissions.dto;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.RegisteredUser;

public abstract class TimelineObject implements Comparable<TimelineObject> {

	protected Date date = null;
	protected RegisteredUser author;


	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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
		return otherPhase.getDate().compareTo(this.getDate());
	}

	public abstract String getType();


}