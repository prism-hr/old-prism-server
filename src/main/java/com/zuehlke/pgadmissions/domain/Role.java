package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;

import com.zuehlke.pgadmissions.domain.enums.Authority;

@Entity(name = "APPLICATION_ROLE")
public class Role implements GrantedAuthority, Serializable {

	private static final long serialVersionUID = 4265990408553249748L;

	@Id
	@GeneratedValue
	private Integer id;

	@Enumerated(EnumType.STRING)
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

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}
}
