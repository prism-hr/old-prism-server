package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class CommentTypeEnumUserType extends EnumUserType<CommentType> {

	public CommentTypeEnumUserType() {
		super(CommentType.class);
	}
}