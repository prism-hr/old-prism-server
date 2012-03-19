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

@Entity(name="APPLICATION_FORM_EMPLOYMENT_POSITION")
@Access(AccessType.FIELD) 
public class EmploymentPosition extends DomainObject<Integer>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4492119755495402951L;


	@Column(name="employer")
	private String position_employer;
	
	@Column(name="title")
	private String position_title;
	
	@Column(name="remit")
	private String position_remit;
	
	@Temporal(TemporalType.DATE)
	@Column(name="start_date")
	private Date position_startDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name="end_date")
	private Date position_endDate;
	
	@OneToOne
	@JoinColumn(name = "language_id")
	private Language position_language;
	
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
	public String getPosition_employer() {
		return position_employer;
	}
	public void setPosition_employer(String employer) {
		this.position_employer = employer;
	}
	public String getPosition_title() {
		return position_title;
	}
	public void setPosition_title(String title) {
		this.position_title = title;
	}
	public String getPosition_remit() {
		return position_remit;
	}
	public void setPosition_remit(String remit) {
		this.position_remit = remit;
	}
	public Date getPosition_startDate() {
		return position_startDate;
	}
	public void setPosition_startDate(Date startDate) {
		this.position_startDate = startDate;
	}
	public Date getPosition_endDate() {
		return position_endDate;
	}
	public void setPosition_endDate(Date endDate) {
		this.position_endDate = endDate;
	}
	public Language getPosition_language() {
		return position_language;
	}
	public void setPosition_language(Language language) {
		this.position_language = language;
	}

}
