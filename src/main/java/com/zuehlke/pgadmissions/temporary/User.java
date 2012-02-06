package com.zuehlke.pgadmissions.temporary;

import java.util.ArrayList;
import java.util.List;

public class User {

	private String firstName;

	private String lastName;

	private List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();

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

	public List<PhoneNumber> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

}
