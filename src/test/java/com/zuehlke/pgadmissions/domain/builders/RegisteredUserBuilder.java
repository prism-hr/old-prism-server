package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.List;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;

public class RegisteredUserBuilder {

	private String username;
	private String password;
	private Integer id;
	private boolean enabled = true;
	private boolean accountNonExpired = true;
	private boolean accountNonLocked = true;
	private boolean credentialsNonExpired = true;

	private List<Role> roles = new ArrayList<Role>();

	public RegisteredUserBuilder role(Role role) {
		this.roles.add(role);
		return this;
	}

	public RegisteredUserBuilder roles(Role... roles) {
		for (Role role : roles) {
			this.roles.add(role);
		}
		return this;
	}

	public RegisteredUserBuilder username(String username) {
		this.username = username;
		return this;
	}

	public RegisteredUserBuilder password(String password) {
		this.password = password;
		return this;
	}

	public RegisteredUserBuilder id(Integer id) {
		this.id = id;
		return this;
	}

	public RegisteredUserBuilder enabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public RegisteredUserBuilder accountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
		return this;
	}

	public RegisteredUserBuilder accountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
		return this;
	}

	public RegisteredUserBuilder credentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
		return this;
	}

	public RegisteredUser toUser() {
		RegisteredUser user = new RegisteredUser();
		user.setId(id);
		user.setUsername(username);
		user.setPassword(password);
		user.setEnabled(enabled);
		user.setAccountNonExpired(accountNonExpired);
		user.setAccountNonLocked(accountNonLocked);
		user.setCredentialsNonExpired(credentialsNonExpired);
		user.getRoles().addAll(roles);
		return user;
	}

}
