package com.zuehlke.pgadmissions.pagemodels;

import com.zuehlke.pgadmissions.dto.ApplicantRecordDTO;

public class RegisterPageModel extends PageModel{

	private ApplicantRecordDTO record;
	private String message;
	private String url;

	public ApplicantRecordDTO getRecord() {
		return record;
	}

	public void setRecord(ApplicantRecordDTO record) {
		this.record = record;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
}
