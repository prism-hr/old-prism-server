package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.RegistryUser;

public class RegistryUserBuilder {

	private Integer id;
	private String email;
	private String firstname;
	private String lastname;

	public RegistryUserBuilder id(Integer id) {
		this.id = id;
		return this;
	}

	public RegistryUserBuilder email(String email) {
		this.email = email;
		return this;
	}

	public RegistryUserBuilder firstname(String firstname) {
		this.firstname = firstname;
		return this;
	}
	
	
	public RegistryUserBuilder lastname(String lastname) {
		this.lastname = lastname;
		return this;
	}
	
	public RegistryUser toRegistryUser() {
		RegistryUser registryUser = new RegistryUser();
		registryUser.setEmail(email);
		registryUser.setId(id);
		registryUser.setFirstname(firstname);
		registryUser.setLastname(lastname);
		return registryUser;
	}
}
