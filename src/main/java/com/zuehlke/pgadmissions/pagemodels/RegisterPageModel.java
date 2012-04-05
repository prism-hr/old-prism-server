package com.zuehlke.pgadmissions.pagemodels;

import com.zuehlke.pgadmissions.dto.RegistrationDTO;

public class RegisterPageModel extends PageModel{

	private RegistrationDTO record;
	private String message;
	private String url;
	private Integer isSuggestedUser;

	public RegistrationDTO getRecord() {
		return record;
	}

	public void setRecord(RegistrationDTO record) {
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
	
	public Integer getIsSuggestedUser() {
		return isSuggestedUser;
	}
	
	public void setIsSuggestedUser(Integer isSuggestedUser) {
		this.isSuggestedUser = isSuggestedUser;
	}
}
