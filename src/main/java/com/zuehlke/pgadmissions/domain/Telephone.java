package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.PhoneType;

@Entity(name = "TELEPHONE")
@Access(AccessType.FIELD)
public class Telephone extends DomainObject<Integer>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3758468813312267227L;

	@ManyToOne(cascade={javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE})
	@org.hibernate.annotations.Cascade( {org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	@Access(AccessType.PROPERTY)
	@JoinColumn(name = "referee_id")
	private Referee referee = null;


	public Referee getReferee() {
		return referee;
	}

	public void setReferee(Referee referee) {
		this.referee = referee;
	}
	
	@Column(name = "telephone_type")
	@Type(type = "com.zuehlke.pgadmissions.dao.custom.PhoneTypeEnumUserType")
	private PhoneType telephoneType;
	
	@Column(name = "number")
	private String telephoneNumber;
	
	public PhoneType getTelephoneType() {
		return telephoneType;
	}

	public void setTelephoneType(PhoneType telephoneType) {
		this.telephoneType = telephoneType;
	}

	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
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
