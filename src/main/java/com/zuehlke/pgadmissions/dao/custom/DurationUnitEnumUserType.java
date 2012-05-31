package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

public class DurationUnitEnumUserType extends EnumUserType<DurationUnitEnum> {
	public DurationUnitEnumUserType() {
		super(DurationUnitEnum.class);

	}
}

