package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;

public class ProgramInstanceBuilder {
	private Date applicationDeadline;
	private Date applicationStartDate;
	private String academicYear;
	private String studyOption;
	private Integer studyOptionCode;
	private Integer id;
	private int sequence;
	
	private Program program;
	private Boolean enabled;

	public ProgramInstanceBuilder studyOptionCode(Integer id) {
        this.studyOptionCode = id;
        return this;
    }
    
	public ProgramInstanceBuilder studyOption(String option) {
        this.studyOption = option;
        return this;
    }
	
	public ProgramInstanceBuilder studyOption(Integer id, String option) {
        this.studyOption = option;
        this.studyOptionCode = id;
        return this;
    }
	
	public ProgramInstanceBuilder applicationStartDate(Date start){
	    this.applicationStartDate = start;
	    return this;
	} 

	public ProgramInstanceBuilder academicYear(String academicYear){
	    this.academicYear = academicYear;
	    return this;
	} 
	
	public ProgramInstanceBuilder program(Program program){
		this.program = program;
		return this;
	} 
	
	public ProgramInstanceBuilder sequence(int sequence){
		this.sequence = sequence;
		return this;
	} 
	
	public ProgramInstanceBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public ProgramInstanceBuilder enabled(Boolean enabled){
		this.enabled = enabled;
		return this;
	}
	
	public ProgramInstanceBuilder applicationDeadline(Date applicationDeadline){
		this.applicationDeadline = applicationDeadline;
		return this;
	}
	
	public ProgramInstance toProgramInstance(){
		ProgramInstance programInstance = new ProgramInstance();
		programInstance.setApplicationDeadline(applicationDeadline);
		programInstance.setStudyOption(studyOption);
		programInstance.setStudyOptionCode(studyOptionCode);
		programInstance.setId(id);
		programInstance.setSequence(sequence);
		programInstance.setProgram(program);
		programInstance.setAcademicYear(academicYear);
		programInstance.setApplicationStartDate(applicationStartDate);
		programInstance.setEnabled(enabled);
		return programInstance;
		
	}
}
