package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.AwareStatus;


public class AwareStatusEnumUserType extends EnumUserType<AwareStatus> {

	public AwareStatusEnumUserType(){
		super(AwareStatus.class);
	}
}
