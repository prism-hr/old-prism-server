package com.zuehlke.pgadmissions.dto;

import org.joda.time.DateTime;

import com.google.common.base.Objects;

public class UserSelectionDTO {

	private Integer id;
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private DateTime eventTimestamp;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public DateTime getEventTimestamp() {
		return eventTimestamp;
	}

	public void setEventTimestamp(DateTime eventTimestamp) {
		this.eventTimestamp = eventTimestamp;
	}
	
	public String getIndexName() {
		return firstName + " " + lastName;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (getClass() != object.getClass()) {
			return false;
		}
		final UserSelectionDTO other = (UserSelectionDTO) object;
		return Objects.equal(id, other.getId());
	}
	
}
