package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.ApprovalStatus;

public class ApprovalStatusEnumUserType extends EnumUserType<ApprovalStatus> {

	public ApprovalStatusEnumUserType(){
		super(ApprovalStatus.class);
	}
}
