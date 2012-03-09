package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.AddressStatus;


public class AddressStatusEnumUserType extends EnumUserType<AddressStatus> {

	public AddressStatusEnumUserType(){
		super(AddressStatus.class);
	}
}
