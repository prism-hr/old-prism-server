package com.zuehlke.pgadmissions.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name="INTERVIEW_EVALUATION_COMMENT")
public class InterviewEvaluationComment extends StateChangeComment {

	private static final long serialVersionUID = 2184172372328153404L;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="interview_id")
	private Interview interview = null;

	public Interview getInterview() {
		return interview;
	}

	public void setInterview(Interview interview) {
		this.interview = interview;
	}
	
}