package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.PrimaryStatus;


public class PrimaryStatusEnumUserType extends EnumUserType<PrimaryStatus> {

	public PrimaryStatusEnumUserType(){
		super(PrimaryStatus.class);
	}
}
