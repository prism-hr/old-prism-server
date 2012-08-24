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
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name="APPLICATION_FORM_EMPLOYMENT_POSITION")
@Access(AccessType.FIELD) 
public class EmploymentPosition extends DomainObject<Integer> implements FormSectionObject{

	private static final long serialVersionUID = 4492119755495402951L;

	@Transient
	private boolean acceptedTerms;
	
	@Column(name="employer_name")
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 150)
	private String employerName;
	
	@Column(name="employer_address")
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 200)
	private String employerAddress;
	
	@ManyToOne
	@JoinColumn(name="employer_country_id")
	@Valid
	private Country employerCountry;
	
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
	private String position;
	
	private boolean current;
	
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 250)
	private String remit;
	
	@Temporal(TemporalType.DATE)
	@Column(name="start_date")
	private Date startDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name="end_date")
	private Date endDate;
	
	@OneToOne
	@JoinColumn(name = "language_id")
	@Valid
	private Language language;
	
	@ManyToOne
	@JoinColumn(name="application_form_id")
	private ApplicationForm application;
	

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
	
	public ApplicationForm getApplication() {
		return application;
	}
	public void setApplication(ApplicationForm application) {
		this.application = application;
	}
	public String getEmployerName() {
		return employerName;
	}
	public void setEmployerName(String employer) {
		this.employerName = employer;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String title) {
		this.position = title;
	}
	public String getRemit() {
		return remit;
	}
	public void setRemit(String remit) {
		this.remit = remit;
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
	public Language getLanguage() {
		return language;
	}
	public void setLanguage(Language language) {
		this.language = language;
	}


	public String getEmployerAddress() {
		return employerAddress;
	}

	public void setEmployerAddress(String employerAdress) {
		this.employerAddress = employerAdress;
	}

	public Country getEmployerCountry() {
		return employerCountry;
	}

	public void setEmployerCountry(Country employerCountry) {
		this.employerCountry = employerCountry;
	}

	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}

	public boolean isAcceptedTerms() {
		return acceptedTerms;
	}

	public void setAcceptedTerms(boolean acceptedTerms) {
		this.acceptedTerms = acceptedTerms;
	}


}
