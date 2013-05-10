package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.zuehlke.pgadmissions.domain.enums.CommentType;

@Entity(name = "INTERVIEW_VOTE_COMMENT")
public class InterviewVoteComment extends Comment {

	private static final long serialVersionUID = -3138212534729565852L;

	@Enumerated(EnumType.STRING)
	@Column(name = "comment_type")
	private CommentType type = CommentType.INTERVIEW_VOTE;

	public CommentType getType() {
		return type;
	}

	public void setType(CommentType type) {
	}
	

}
