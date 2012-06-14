package com.zuehlke.pgadmissions.dto;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Referee;

public class TimelineReference extends TimelineObject {
	
	private Referee referee;
	
	@Override
	public String getType() {
		return "reference";
	}
	public Referee getReferee() {
		return referee;
	}
	public void setReferee(Referee referee) {
		this.referee = referee;
	}
	public String getMessageCode() {
		return "timeline.reference.responded";
	}
	@Override
	public Date getMostRecentActivityDate() {
		return eventDate;
	}
	@Override
	public String getUserCapacity() {
		return "referee";
	}
	
	

	
}
