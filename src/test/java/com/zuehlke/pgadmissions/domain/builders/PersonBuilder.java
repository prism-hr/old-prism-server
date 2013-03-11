package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Person;

public class PersonBuilder {

	private Integer id;
	private String email;
	private String firstname;
	private String lastname;

	public PersonBuilder id(Integer id) {
		this.id = id;
		return this;
	}

	public PersonBuilder email(String email) {
		this.email = email;
		return this;
	}

	public PersonBuilder firstname(String firstname) {
		this.firstname = firstname;
		return this;
	}
	
	
	public PersonBuilder lastname(String lastname) {
		this.lastname = lastname;
		return this;
	}
	
	public Person build() {
		Person registryUser = new Person();
		registryUser.setEmail(email);
		registryUser.setId(id);
		registryUser.setFirstname(firstname);
		registryUser.setLastname(lastname);
		return registryUser;
	}
}
