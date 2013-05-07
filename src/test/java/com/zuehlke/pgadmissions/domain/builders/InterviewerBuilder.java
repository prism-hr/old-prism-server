package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class InterviewerBuilder {
	private Integer id;
	private RegisteredUser user;
	private Date lastNotified;
	private Interview interview;
	private boolean requiresAdminNotification;
	private Date dateAdminsNotified;
	private boolean firstAdminNotification;
	private InterviewComment interviewComment;

	public InterviewerBuilder interviewComment(InterviewComment interviewComment) {
		this.interviewComment = interviewComment;
		return this;
	}

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

	public InterviewerBuilder firstAdminNotification(boolean firstNotification) {
		this.firstAdminNotification = firstNotification;
		return this;
	}

	public Interviewer build() {
		Interviewer interviewer = new Interviewer();
		interviewer.setId(id);
		interviewer.setRequiresAdminNotification(requiresAdminNotification);
		interviewer.setFirstAdminNotification(firstAdminNotification);
		interviewer.setUser(user);
		interviewer.setLastNotified(lastNotified);
		interviewer.setInterview(interview);
		interviewer.setDateAdminsNotified(dateAdminsNotified);
		interviewer.setInterviewComment(interviewComment);
		return interviewer;
	}
}
