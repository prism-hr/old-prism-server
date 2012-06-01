package com.zuehlke.pgadmissions.dto;

import java.util.ArrayList;
import java.util.List;

import com.zuehlke.pgadmissions.domain.Person;

public class RegistryUserDTO {
	
	private List<Person> registryUsers = new ArrayList<Person>();

	public List<Person> getRegistryUsers() {
		return registryUsers;
	}

	public void setRegistryUsers(List<Person> registryUsers) {
		for (Person registryUser : registryUsers) {
			if(registryUser != null){
				this.registryUsers.add(registryUser);
			}
		}
	}

}
