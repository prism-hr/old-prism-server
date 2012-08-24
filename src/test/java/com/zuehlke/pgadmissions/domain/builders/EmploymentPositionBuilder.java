package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Language;

public class EmploymentPositionBuilder {
	
	private String employerName;
	
	private String position;
	
	private String remit;
	
	private Language language;
	
	private Date startDate;
	
	private Date endDate;

	private ApplicationForm application;
	
	private Integer id;	
	
	private String employerAdress;
	
	private Country employerCountry;
	
	private boolean current;
	
	public EmploymentPositionBuilder current(boolean current){
		this.current = current;
		return this;
	}
	
	public EmploymentPositionBuilder employerAdress(String employerAdress){
		this.employerAdress = employerAdress;
		return this;
	}
	public EmploymentPositionBuilder employerCountry(Country employerCountry){
		this.employerCountry = employerCountry;
		return this;
	}
	
	
	public EmploymentPositionBuilder employerName(String employerName){
		this.employerName = employerName;
		return this;
	}
	public EmploymentPositionBuilder position(String title){
		this.position = title;
		return this;
	}
	public EmploymentPositionBuilder remit(String remit){
		this.remit = remit;
		return this;
	}
	
	public EmploymentPositionBuilder id(Integer id){
		this.id = id;
		return this;
	}

	public EmploymentPositionBuilder startDate(Date startDate){
		this.startDate = startDate;
		return this;
	}
	public EmploymentPositionBuilder endDate(Date endDate){
		this.endDate = endDate;
		return this;
	}
	public EmploymentPositionBuilder application(ApplicationForm application){
		this.application = application;
		return this;
	}
	public EmploymentPosition toEmploymentPosition(){
		EmploymentPosition employment = new EmploymentPosition();
		employment.setApplication(application);
		employment.setEmployerName(employerName);
		employment.setEmployerAddress(employerAdress);
		employment.setEmployerCountry(employerCountry);
		employment.setEndDate(endDate);
		employment.setRemit(remit);
		employment.setStartDate(startDate);
		employment.setPosition(position);
		employment.setId(id);
		employment.setCurrent(current);
		return employment;
	}
}
