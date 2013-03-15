package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class RoleBuilder {

	private Authority authorityEnum;
	private Integer id;
	
	public RoleBuilder id(Integer id){
		this.id =id;
		return this;
	}
	
	public RoleBuilder authorityEnum(Authority authorityEnum){
		this.authorityEnum =authorityEnum;
		return this;
	}
	
	public Role build() {
		Role role = new Role();
		role.setId(id);
		role.setAuthorityEnum(authorityEnum);
		return role;
	}
}
