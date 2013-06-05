package com.zuehlke.pgadmissions.dto;

import java.util.Date;

import org.springframework.util.StringUtils;

import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

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
	
	@Override
	public String getTooltipMessage() {
	    RegisteredUser providedBy = referee.getReference().getProvidedBy();
	    return String.format("%s %s (%s) as: %s", providedBy.getFirstName(), providedBy.getLastName(), providedBy.getEmail(), StringUtils.capitalize(getUserCapacity())); 
	}
	
    public String getTooltipMessage(final String role) {
        RegisteredUser providedBy = referee.getReference().getProvidedBy();
        return String.format("%s %s (%s) as: %s", providedBy.getFirstName(), providedBy.getLastName(), providedBy.getEmail(), StringUtils.capitalize(role)); 
    }
}
