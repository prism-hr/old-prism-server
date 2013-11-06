package com.zuehlke.pgadmissions.dto;

import java.util.Date;

import org.springframework.util.StringUtils;

import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
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
		ReferenceComment comment = referee.getReference();
	    if(comment!=null){
			RegisteredUser providedBy = comment.getProvidedBy();
		    if (providedBy != null) {
		        return String.format("%s %s (%s) as: %s", providedBy.getFirstName(), providedBy.getLastName(), providedBy.getEmail(), StringUtils.capitalize(getUserCapacity()));
		    }
	    }
	    return super.getTooltipMessage();
	    
	}
	
    public String getTooltipMessage(final String role) {
        return super.getTooltipMessage(role);
    }
}
