package com.zuehlke.pgadmissions.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.ResidenceStatus;

@Entity(name = "APPLICATION_FORM_PERSONAL_DETAIL")
@Access(AccessType.FIELD)
public class PersonalDetail extends DomainObject<Integer> {

	private static final long serialVersionUID = 6549850558507667533L;

	@OneToMany(cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "personal_detail_id")
	private List<Telephone> phoneNumbers;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Type(type = "com.zuehlke.pgadmissions.dao.custom.GenderEnumUserType")
	private Gender gender;
	private String email;

	@Column(name = "date_of_birth")
	@Temporal(TemporalType.DATE)
	private Date dateOfBirth;

	@OneToOne
	@JoinColumn(name = "country_id")
	private Country country;

	@OneToOne
	@JoinColumn(name = "residence_country_id")
	private Country residenceCountry;

	@Type(type = "com.zuehlke.pgadmissions.dao.custom.ResidenceStatusEnumUserType")
	@Column(name = "residence_status")
	private ResidenceStatus residenceStatus;

	@OneToOne
	@JoinColumn(name = "application_form_id")
	private ApplicationForm application = null;

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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Country getResidenceCountry() {
		return residenceCountry;
	}

	public void setResidenceCountry(Country residenceCountry) {
		this.residenceCountry = residenceCountry;
	}

	public ResidenceStatus getResidenceStatus() {
		return residenceStatus;
	}

	public void setResidenceStatus(ResidenceStatus residenceStatus) {
		this.residenceStatus = residenceStatus;
	}

	public ApplicationForm getApplication() {
		return application;
	}

	public void setApplication(ApplicationForm application) {
		this.application = application;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public List<Telephone> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<Telephone> phoneNumbers) {
		this.phoneNumbers =phoneNumbers;
		
		
	}
}
