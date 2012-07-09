package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.CommentType;

@Entity(name = "REQUEST_RESTART_COMMENT")
@Access(AccessType.FIELD)
public class RequestRestartComment extends Comment {

	private static final long serialVersionUID = -4781109606355068105L;
	
	@Type(type = "com.zuehlke.pgadmissions.dao.custom.CommentTypeEnumUserType")
	@Column(name="comment_type")
	private CommentType type = CommentType.REQUEST_RESTART; 
	

	@Override
	public CommentType getType() {
		return type;
	}




	
}
