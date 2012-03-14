package com.zuehlke.pgadmissions.pagemodels;

import com.zuehlke.pgadmissions.domain.ApplicantRecord;



public class RegisterPageModel extends PageModel{

	private ApplicantRecord record;

	public ApplicantRecord getRecord() {
		return record;
	}

	public void setRecord(ApplicantRecord record) {
		this.record = record;
	}
}
