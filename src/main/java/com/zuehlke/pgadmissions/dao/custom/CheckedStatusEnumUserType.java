package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;


public class CheckedStatusEnumUserType extends EnumUserType<CheckedStatus> {

	public CheckedStatusEnumUserType(){
		super(CheckedStatus.class);
	}
}
