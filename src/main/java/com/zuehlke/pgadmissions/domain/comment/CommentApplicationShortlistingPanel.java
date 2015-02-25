package com.zuehlke.pgadmissions.domain.comment;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.definitions.MeetingMode;

@Embeddable
public class CommentApplicationShortlistingPanel {

	@Column(name = "application_shortlisting_mode", nullable = false)
	private MeetingMode mode;

	@Column(name = "application_shortlisting_deadline", nullable = false)
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime deadline;

	public MeetingMode getMode() {
		return mode;
	}

	public void setMode(MeetingMode mode) {
		this.mode = mode;
	}

	public DateTime getDeadline() {
		return deadline;
	}

	public void setDeadline(DateTime deadline) {
		this.deadline = deadline;
	}

	public CommentApplicationShortlistingPanel withMode(MeetingMode mode) {
		this.mode = mode;
		return this;
	}

	public CommentApplicationShortlistingPanel withDeadline(DateTime deadline) {
		this.deadline = deadline;
		return this;
	}

}
