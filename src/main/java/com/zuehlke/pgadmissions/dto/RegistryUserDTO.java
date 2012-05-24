package com.zuehlke.pgadmissions.dto;

import java.util.ArrayList;
import java.util.List;

import com.zuehlke.pgadmissions.domain.RegistryUser;

public class RegistryUserDTO {
	
	private List<RegistryUser> registryUsers = new ArrayList<RegistryUser>();

	public List<RegistryUser> getRegistryUsers() {
		return registryUsers;
	}

	public void setRegistryUsers(List<RegistryUser> registryUsers) {
		for (RegistryUser registryUser : registryUsers) {
			if(registryUser != null){
				this.registryUsers.add(registryUser);
			}
		}
	}

}
