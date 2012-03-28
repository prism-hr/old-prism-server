package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;

public class SubmissionStatusEnumUserType extends EnumUserType<SubmissionStatus> {

	public SubmissionStatusEnumUserType(){
		super(SubmissionStatus.class);
	}
}
