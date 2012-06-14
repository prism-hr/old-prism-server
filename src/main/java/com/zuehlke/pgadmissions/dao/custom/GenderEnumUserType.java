package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.Gender;

public class GenderEnumUserType extends EnumUserType<Gender> {

	public GenderEnumUserType(){
		super(Gender.class);
	}
}
