package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.LanguageAptitude;


public class LanguageAptitudeEnumUserType extends EnumUserType<LanguageAptitude> {

	public LanguageAptitudeEnumUserType(){
		super(LanguageAptitude.class);
	}
}
