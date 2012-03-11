package com.zuehlke.pgadmissions.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;



@Entity(name="MESSENGER")
@Access(AccessType.FIELD)
public class Messenger extends DomainObject<Integer>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8563165735037557238L;

	@Column(name = "messenger_type")
	private String messengerType;
	
	@Column(name = "address")
	private String messengerAddress;
	

	@ManyToOne(cascade={javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE})
	@org.hibernate.annotations.Cascade( {org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	@Access(AccessType.PROPERTY)
	@JoinColumn(name = "referee_id")
	private Referee referee = new Referee();

	
	public String getMessengerType() {
		return messengerType;
	}

	public void setMessengerType(String messengerType) {
		this.messengerType = messengerType;
	}

	public String getMessengerAddress() {
		return messengerAddress;
	}

	public void setMessengerAddress(String messengerAddress) {
		this.messengerAddress = messengerAddress;
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

	public Referee getReferee() {
		return referee;
	}

	public void setReferee(Referee referee) {
		this.referee = referee;
	}
}
