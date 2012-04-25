package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;

public class ProgramInstanceBuilder {
	private Date applicationDeadline;
	private StudyOption studyOption;
	private Integer id;
	private int sequence;
	
	

	public ProgramInstanceBuilder sequence(int sequence){
		this.sequence = sequence;
		return this;
	} 
	
	public ProgramInstanceBuilder id(Integer id){
		this.id = id;
		return this;
	} 
	public ProgramInstanceBuilder applicationDeadline(Date applicationDeadline){
		this.applicationDeadline = applicationDeadline;
		return this;
	}
	
	public ProgramInstanceBuilder studyOption(StudyOption studyOption){
		this.studyOption = studyOption;
		return this;
	}
	
	public ProgramInstance toProgramInstance(){
		ProgramInstance programInstance = new ProgramInstance();
		programInstance.setApplicationDeadline(applicationDeadline);
		programInstance.setStudyOption(studyOption);
		programInstance.setId(id);
		programInstance.setSequence(sequence);
		return programInstance;
		
	}
}
