package com.zuehlke.pgadmissions.dto;

import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class TimelinePhase extends TimelineObject {

	private ApplicationFormStatus status = null;
	private Date exitedPhaseDate = null;

	private SortedSet<Comment> comments = new TreeSet<Comment>();
	
	public ApplicationFormStatus getStatus() {
		return status;
	}

	public void setStatus(ApplicationFormStatus status) {
		this.status = status;
	}


	public void setExitedPhaseDate(Date exitedPhaseDate) {
		this.exitedPhaseDate = exitedPhaseDate;
	}

	public Date getExitedPhaseDate() {
		return exitedPhaseDate;
	}

	
	public SortedSet<Comment> getComments() {		
		return comments;
	}

	@Override
	public String getType() {
		return status.displayValue().toLowerCase().replace(" ", "_");
	}


	

}
