package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

@Entity(name="STATECHANGE_COMMENT")
@Access(AccessType.FIELD) 
public class StateChangeComment extends Comment {


	private static final long serialVersionUID = 7106729861627717600L;

	@Type(type = "com.zuehlke.pgadmissions.dao.custom.CommentTypeEnumUserType")
	@Column(name="comment_type")
	private CommentType type;

	@Type(type = "com.zuehlke.pgadmissions.dao.custom.ApplicationFormStatusEnumUserType")
	@Column(name="next_status")
	private ApplicationFormStatus nextStatus = null;
	
	public CommentType getType() {
		return type;
	}

	public void setType(CommentType type) {
		this.type = type;
	}

	public ApplicationFormStatus getNextStatus() {
		return nextStatus;
	}

	public void setNextStatus(ApplicationFormStatus nextStatus) {
		this.nextStatus = nextStatus;
	}
	

}
