package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.AddressPurpose;
import com.zuehlke.pgadmissions.domain.enums.AddressStatus;


@Entity(name="APPLICATION_FORM_ADDRESS")
@Access(AccessType.FIELD) 
public class Address extends DomainObject<Integer>{

	private static final long serialVersionUID = 2746228908173552617L;

	@ManyToOne
	@JoinColumn(name="application_form_id")
	private ApplicationForm application = null;

	@Column(name="post_code")
	private String postCode;

	private String country;
	
	@Type(type = "com.zuehlke.pgadmissions.dao.custom.AddressPurposeEnumUserType")
	private AddressPurpose purpose;

	@Temporal(TemporalType.DATE)
	@Column(name="start_date")
	private Date startDate;

	@Temporal(TemporalType.DATE)
	@Column(name="end_date")
	private Date endDate;
	
	@Type(type = "com.zuehlke.pgadmissions.dao.custom.AddressStatusEnumUserType")
	@Column(name = "contact_address")
	private AddressStatus contactAddress;

	public ApplicationForm getApplication() {
		return application;
	}

	public void setApplication(ApplicationForm application) {
		this.application = application;
	}

	private String location;
	
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

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public AddressPurpose getPurpose() {
		return purpose;
	}
	
	public void setPurpose(AddressPurpose purpose) {
		this.purpose = purpose;
	}
	
	public AddressStatus getContactAddress() {
		return contactAddress;
	}
	
	public void setContactAddress(AddressStatus contactAddress) {
		this.contactAddress = contactAddress;
	}
}

