package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Immutable;

@Entity(name = "REJECT_REASON")
@Immutable
@Access(AccessType.FIELD)
public class RejectReason extends DomainObject<Integer> {
	private static final long serialVersionUID = 2745896114174369017L;

	private String text;

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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "RejectReason [" + (id != null ? "id=" + id + ", " : "id=<null>") + "text=" + text + "]";
	}

}
