package com.zuehlke.pgadmissions.dao.custom;

import com.zuehlke.pgadmissions.domain.enums.Authority;

public class AuthorityEnumUserType extends EnumUserType<Authority> {

	public AuthorityEnumUserType() {
		super(Authority.class);
		
	}

}
