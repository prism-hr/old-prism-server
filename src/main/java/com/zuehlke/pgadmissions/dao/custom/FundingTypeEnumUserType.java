package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.FundingType;

public class FundingTypeEnumUserType extends EnumUserType<FundingType> {
		public FundingTypeEnumUserType(){
			super(FundingType.class);
		}
	}
