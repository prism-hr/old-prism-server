package com.zuehlke.pgadmissions.dto;

import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class TimelinePhase extends TimelineObject {


	private ApplicationFormStatus status = null;
	private Date exitedPhaseDate = null;
	private ReviewRound reviewRound = null;
	private Interview interview = null;
	private ApprovalRound approvalRound = null;
	
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

	public ReviewRound getReviewRound() {
		return reviewRound;
	}

	public void setReviewRound(ReviewRound reviewRound) {
		this.reviewRound = reviewRound;
	}

	public Interview getInterview() {
		return interview;
	}

	public void setInterview(Interview interview) {
		this.interview = interview;
	}

	public ApprovalRound getApprovalRound() {
		return approvalRound;
	}

	public void setApprovalRound(ApprovalRound approvalRound) {
		this.approvalRound = approvalRound;
	}

	public String getMessageCode() {
		return "timeline.phase." + status.displayValue().toLowerCase().replace(" ", "_");
	}

	@Override
	public Date getMostRecentActivityDate() {	
		if(comments.isEmpty()){
			return eventDate;
		}
		return comments.iterator().next().getDate();
	}

	

}
