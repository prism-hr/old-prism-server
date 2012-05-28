package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;

public class InterviewerBuilder {
	private Integer id;
	private RegisteredUser user;
	private Date lastNotified;
	private Interview interview;
	private boolean requiresAdminNotification;
	private Date dateAdminsNotified;

	public InterviewerBuilder dateAdminsNotified(Date dateAdminsNotified) {
		this.dateAdminsNotified = dateAdminsNotified;
		return this;
	}

	public InterviewerBuilder requiresAdminNotification(boolean requiresAdminNotification) {
		this.requiresAdminNotification = requiresAdminNotification;
		return this;
	}

	public InterviewerBuilder interview(Interview interview) {
		this.interview = interview;
		return this;
	}

	public InterviewerBuilder lastNotified(Date lastNotified) {
		this.lastNotified = lastNotified;
		return this;
	}

	public InterviewerBuilder id(Integer id) {
		this.id = id;
		return this;
	}

	public InterviewerBuilder user(RegisteredUser user) {
		this.user = user;
		return this;
	}

	public Interviewer toInterviewer() {
		Interviewer interviewer = new Interviewer();
		interviewer.setId(id);
		interviewer.setRequiresAdminNotification(requiresAdminNotification);
		interviewer.setUser(user);
		interviewer.setLastNotified(lastNotified);
		interviewer.setInterview(interview);
		interviewer.setDateAdminsNotified(dateAdminsNotified);
		return interviewer;
	}
}
