package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

import com.zuehlke.pgadmissions.domain.enums.CommentType;

@Entity(name="VALIDATION_COMMENT")
@Access(AccessType.FIELD) 
public class ValidationComment extends Comment {


	private static final long serialVersionUID = 7106729861627717600L;

	@Override
	public CommentType getType() {
		return CommentType.VALIDATION;
	}

}
