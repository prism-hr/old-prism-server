package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.ValidationStage;;

public class ValidationStageEnumUserType extends EnumUserType<ValidationStage> {

	public ValidationStageEnumUserType(){
		super(ValidationStage.class);
	}
}
