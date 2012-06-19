package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;

import com.zuehlke.pgadmissions.domain.enums.Authority;

@Entity(name = "APPLICATION_ROLE")
@Access(AccessType.FIELD)
public class Role extends DomainObject<Integer> implements GrantedAuthority {

	private static final long serialVersionUID = 4265990408553249748L;

	@Type(type = "com.zuehlke.pgadmissions.dao.custom.AuthorityEnumUserType")
	@Column(name = "authority")
	private Authority authorityEnum;

	public Authority getAuthorityEnum() {
		return authorityEnum;
	}

	public void setAuthorityEnum(Authority authorityEnum) {
		this.authorityEnum = authorityEnum;
	}

	@Override
	@Transient
	public String getAuthority() {
		if (authorityEnum != null) {
			return getAuthorityEnum().toString();
		}
		return null;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	@Id
	@GeneratedValue
	@Access(AccessType.PROPERTY)
	public Integer getId() {
		return id;
	}

}
