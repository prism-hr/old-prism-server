package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.AddressPurpose;


public class AddressPurposeEnumUserType extends EnumUserType<AddressPurpose> {

	public AddressPurposeEnumUserType(){
		super(AddressPurpose.class);
	}
}
