package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Person;

public class Personuilder {

	private Integer id;
	private String email;
	private String firstname;
	private String lastname;

	public Personuilder id(Integer id) {
		this.id = id;
		return this;
	}

	public Personuilder email(String email) {
		this.email = email;
		return this;
	}

	public Personuilder firstname(String firstname) {
		this.firstname = firstname;
		return this;
	}
	
	
	public Personuilder lastname(String lastname) {
		this.lastname = lastname;
		return this;
	}
	
	public Person toRegistryUser() {
		Person registryUser = new Person();
		registryUser.setEmail(email);
		registryUser.setId(id);
		registryUser.setFirstname(firstname);
		registryUser.setLastname(lastname);
		return registryUser;
	}
}
