package com.zuehlke.pgadmissions.dto;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

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
	
	@Override
	public int compareTo(TimelineObject otherPhase) {
		return otherPhase.getMostRecentActivityDate().compareTo(this.getMostRecentActivityDate());
	}

	abstract String getMessageCode(); 

	abstract String getType();
	
	abstract Date getMostRecentActivityDate();
	
	abstract String getUserCapacity();
	
	public String getTooltipMessage() {
	    String userCapacity = StringUtils.EMPTY;
	    if (StringUtils.equalsIgnoreCase("admin", getUserCapacity())) {
	        userCapacity = "Administrator";
	    } else {
	        userCapacity = StringUtils.capitalize(getUserCapacity());
	    }
	    return String.format("%s %s (%s) as: %s", author.getFirstName(), author.getLastName(), author.getEmail(), userCapacity); 
	}
}