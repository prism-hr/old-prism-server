package com.zuehlke.pgadmissions.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity(name="INTERVIEW_STATE_CHANGE_EVENT")
public class InterviewStateChangeEvent extends StateChangeEvent {

	private static final long serialVersionUID = 2241617661528328806L;
		
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "interview_id")
	private Interview interview;

	public Interview getInterview() {
		return interview;
	}

	public void setInterview(Interview interview) {
		this.interview = interview;
	}
}
