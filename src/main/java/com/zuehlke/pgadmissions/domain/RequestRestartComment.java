package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.zuehlke.pgadmissions.domain.enums.CommentType;

@Entity(name = "REQUEST_RESTART_COMMENT")
public class RequestRestartComment extends Comment {

	private static final long serialVersionUID = -4781109606355068105L;
	
	@Enumerated(EnumType.STRING)
	@Column(name="comment_type")
	private CommentType type = CommentType.REQUEST_RESTART; 
	
	@Override
	public CommentType getType() {
		return type;
	}
	
}
