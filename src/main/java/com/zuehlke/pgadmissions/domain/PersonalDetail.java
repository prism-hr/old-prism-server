package com.zuehlke.pgadmissions.domain;

import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import com.zuehlke.pgadmissions.domain.enums.Gender;

@Entity(name = "APPLICATION_FORM_PERSONAL_DETAIL")
@Access(AccessType.FIELD)

public class PersonalDetail extends DomainObject<Integer> {

	private static final long serialVersionUID = 6549850558507667533L;
	
	@Column(name = "skype")
	private String messenger;
	
	@OneToMany(orphanRemoval=true, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "personal_detail_id")
	private List<Telephone> phoneNumbers=  new ArrayList<Telephone>();

	@OneToMany(orphanRemoval=true, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "personal_details_id")
	@Where(clause="nationality_type='CANDIDATE'")
	private List<Nationality> candidateNationalities= new ArrayList<Nationality>();
	
	@OneToMany(orphanRemoval=true, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "personal_details_id")
	@Where(clause="nationality_type='MATERNAL_GUARDIAN'")
	private List<Nationality> maternalGuardianNationalities= new ArrayList<Nationality>();
	
	
	@OneToMany(orphanRemoval=true, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "personal_details_id")
	@Where(clause="nationality_type='PATERNAL_GUARDIAN'")
	private List<Nationality> paternalGuardianNationalities= new ArrayList<Nationality>();
	
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

	@ManyToOne
	@JoinColumn(name = "country_id")
	private Country country;

	@ManyToOne
	@JoinColumn(name = "residence_country_id")
	private Country residenceCountry;

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
		this.phoneNumbers.clear();
		for (Telephone telephone : phoneNumbers) {
			if(telephone != null){
				this.phoneNumbers.add(telephone);
			}
		}
		
	}


	public List<Nationality> getCandidateNationalities() {
		return candidateNationalities;
	}

	public void setCandidateNationalities(List<Nationality> candiateNationalities) {
		this.candidateNationalities.clear();
		for (Nationality nationality : candiateNationalities) {
			if(nationality != null){
				this.candidateNationalities.add(nationality);
			}
		}
		
	}

	public List<Nationality> getMaternalGuardianNationalities() {
		return maternalGuardianNationalities;
	}

	public void setMaternalGuardianNationalities(List<Nationality> maternalGuardianNationalities) {
		this.maternalGuardianNationalities.clear();
		for (Nationality nationality : maternalGuardianNationalities) {
			if(nationality != null){
				this.maternalGuardianNationalities.add(nationality);
			}
		}
	}

	public List<Nationality> getPaternalGuardianNationalities() {
		return paternalGuardianNationalities;
	}

	public void setPaternalGuardianNationalities(List<Nationality> paternalGuardianNationalities) {
		this.paternalGuardianNationalities.clear();
		for (Nationality nationality : paternalGuardianNationalities) {
			if(nationality != null){
				this.paternalGuardianNationalities.add(nationality);
			}
		}
	}

	public String getMessenger() {
		return messenger;
	}

	public void setMessenger(String messenger) {
		this.messenger = messenger;
	}

	
}
