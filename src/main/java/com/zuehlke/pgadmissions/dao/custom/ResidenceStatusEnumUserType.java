package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.ResidenceStatus;

public class ResidenceStatusEnumUserType extends EnumUserType<ResidenceStatus> {

	public ResidenceStatusEnumUserType(){
		super(ResidenceStatus.class);
	}
}
