package com.zuehlke.pgadmissions.dto;

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
		if(referee.isDeclined()){
			return "timeline.reference.declined";
		}
		return "timeline.reference.uploaded";
	}
	
	

	
}
