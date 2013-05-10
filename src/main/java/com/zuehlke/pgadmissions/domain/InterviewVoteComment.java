package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.zuehlke.pgadmissions.domain.enums.CommentType;

@Entity(name = "INTERVIEW_VOTE_COMMENT")
public class InterviewVoteComment extends Comment {

	private static final long serialVersionUID = -3138212534729565852L;

	@Enumerated(EnumType.STRING)
	@Column(name = "comment_type")
	private CommentType type = CommentType.INTERVIEW_VOTE;
	
	@OneToOne(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "interview_participant_id")
	private InterviewParticipant interviewParticipant;

	public CommentType getType() {
		return type;
	}

	public void setType(CommentType type) {
	}

	public InterviewParticipant getInterviewParticipant() {
	    return interviewParticipant;
    }

	public void setInterviewParticipant(InterviewParticipant interviewParticipant) {
	    this.interviewParticipant = interviewParticipant;
    }

}
