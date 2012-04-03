package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;

public class EmploymentPositionBuilder {
	
	private String employer;
	
	private String title;
	
	private String remit;
	
	private Language language;
	
	private Date startDate;
	
	private Date endDate;

	private ApplicationForm application;
	
	private Integer id;
	
	private CheckedStatus completed;
	
	public EmploymentPositionBuilder employer(String employer){
		this.employer = employer;
		return this;
	}
	public EmploymentPositionBuilder title(String title){
		this.title = title;
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
	
	public EmploymentPositionBuilder isCompleted(CheckedStatus isCompleted){
		this.completed = isCompleted;
		return this;
	}
	
	public EmploymentPositionBuilder language(Language language){
		this.language = language;
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
		EmploymentPosition position = new EmploymentPosition();
		position.setApplication(application);
		position.setPosition_employer(employer);
		position.setPosition_endDate(endDate);
		position.setPosition_language(language);
		position.setPosition_remit(remit);
		position.setPosition_startDate(startDate);
		position.setPosition_title(title);
		position.setCompleted(completed);
		position.setId(id);
		return position;
	}
}
