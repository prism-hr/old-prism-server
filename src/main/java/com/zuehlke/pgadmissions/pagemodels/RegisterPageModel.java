package com.zuehlke.pgadmissions.pagemodels;

import com.zuehlke.pgadmissions.dto.RegisteredUserDTO;

public class RegisterPageModel extends PageModel{

	private RegisteredUserDTO record;
	private String message;
	private String url;

	public RegisteredUserDTO getRecord() {
		return record;
	}

	public void setRecord(RegisteredUserDTO record) {
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
