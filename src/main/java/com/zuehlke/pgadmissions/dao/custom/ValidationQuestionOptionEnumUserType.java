package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;

public class ValidationQuestionOptionEnumUserType extends EnumUserType<ValidationQuestionOptions> {

	public ValidationQuestionOptionEnumUserType() {
		super(ValidationQuestionOptions.class);
		
	}

}
