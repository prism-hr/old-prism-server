package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;

@Entity(name="APPLICATION_FORM_QUALIFICATION")
@Access(AccessType.FIELD) 
public class Qualification extends DomainObject<Integer>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8949535622435302565L;


	private String degree;
	private String date_taken;
	private String institution;
	private String grade;
	
	
	@ManyToOne
	@Cascade( { org.hibernate.annotations.CascadeType.ALL } )
	@JoinColumn(name="application_form_id", insertable=false, updatable=false, nullable=false)
	private ApplicationForm application = null;
	
	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public String getDate_taken() {
		return date_taken;
	}

	public void setDate_taken(String date_taken) {
		this.date_taken = date_taken;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public ApplicationForm getApplication() {
		return application;
	}

	public void setApplication(ApplicationForm application) {
		this.application = application;
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

	public boolean isAttachedToApplication(
			ApplicationForm applicationForm, Qualification qualification) {
		return qualification.getApplication().equals(applicationForm);
	}

}
