package com.zuehlke.pgadmissions.pagemodels;

import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class RegisterPageModel extends PageModel{

	private RegisteredUser record;
	private String message;
	private String url;
	private Integer isSuggestedUser;

	public RegisteredUser getRecord() {
		return record;
	}
	public void setRecord(RegisteredUser record) {
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
