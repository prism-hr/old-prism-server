package com.zuehlke.pgadmissions.utils;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Component
public class UserFactory {

	private final RoleDAO roleDAO;
	private final EncryptionUtils encryptionUtils;

	UserFactory(){
		this(null, null);
	}
	
	@Autowired
	public UserFactory(RoleDAO roleDAO, EncryptionUtils encryptionUtils) {
		this.roleDAO = roleDAO;
		this.encryptionUtils = encryptionUtils;
	}

	public RegisteredUser createNewUserInRoles(String firstname, String lastname, String email, Authority... authorities) {
		RegisteredUser user = new RegisteredUser();

		user.setFirstName(firstname);
		user.setLastName(lastname);
		user.setUsername(email);
		user.setEmail(email);
		user.setAccountNonExpired(true);
		user.setAccountNonLocked(true);
		user.setEnabled(false);
		user.setCredentialsNonExpired(true);
		user.setActivationCode(encryptionUtils.generateUUID());
		for (Authority authority : authorities) {
			user.getRoles().add(roleDAO.getRoleByAuthority(authority));	
		}
		
		return user;
	}

}
