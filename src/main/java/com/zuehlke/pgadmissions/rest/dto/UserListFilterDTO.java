package com.zuehlke.pgadmissions.rest.dto;

public class UserListFilterDTO {

	boolean invalidOnly;

	String searchTerm;

	Integer lastUserId;

	public boolean isInvalidOnly() {
		return invalidOnly;
	}

	public void setInvalidOnly(boolean invalidOnly) {
		this.invalidOnly = invalidOnly;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public Integer getLastUserId() {
		return lastUserId;
	}

	public void setLastUserId(Integer lastUserId) {
		this.lastUserId = lastUserId;
	}

}
