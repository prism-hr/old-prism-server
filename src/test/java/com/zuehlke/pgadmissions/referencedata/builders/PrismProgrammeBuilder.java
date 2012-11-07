package com.zuehlke.pgadmissions.referencedata.builders;

import com.zuehlke.pgadmissions.referencedata.jaxb.Programmes.Programme;
import com.zuehlke.pgadmissions.referencedata.jaxb.Programmes.Programme.ModeOfAttendance;

public class PrismProgrammeBuilder {
	private String academicYear;
	private String code;
	private String endDate;
	private String name;
	private String startDate;
	private String studyOptionCode;
	private String studyOption;

	public PrismProgrammeBuilder academicYear(String academicYear){
	    this.academicYear = academicYear;
	    return this;
	} 
	
	public PrismProgrammeBuilder code(String code) {
		this.code = code;
		return this;
	}
	
	public PrismProgrammeBuilder endDate(String endDate) {
		this.endDate = endDate;
		return this;
	}
	
	public PrismProgrammeBuilder name(String name) {
		this.name = name;
		return this;
	}
	
	public PrismProgrammeBuilder startDate(String startDate) {
		this.startDate = startDate;
		return this;
	}
	
	public PrismProgrammeBuilder studyOption(String studyOption) {
		this.studyOption = studyOption;
		return this;
	}
	
	public PrismProgrammeBuilder studyOptionCode(String studyOptionCode) {
		this.studyOptionCode = studyOptionCode;
		return this;
	}
	
	public Programme toPrismProgramme(){
		Programme programme = new Programme();
		ModeOfAttendance modeOfAttendance = new ModeOfAttendance();
		modeOfAttendance.setCode(studyOptionCode);
		modeOfAttendance.setName(studyOption);
		programme.setModeOfAttendance(modeOfAttendance);
		programme.setAcademicYear(academicYear);
		programme.setCode(code);
		programme.setEndDate(endDate);
		programme.setName(name);
		programme.setStartDate(startDate);
		return programme;
		
	}
}
