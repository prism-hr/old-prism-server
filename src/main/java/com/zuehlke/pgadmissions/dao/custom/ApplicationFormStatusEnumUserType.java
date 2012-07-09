package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class ApplicationFormStatusEnumUserType extends EnumUserType<ApplicationFormStatus> {

	public ApplicationFormStatusEnumUserType(){
		super(ApplicationFormStatus.class);
	}
}
