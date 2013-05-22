package com.zuehlke.pgadmissions.referencedata.builders;

import com.zuehlke.pgadmissions.referencedata.v2.jaxb.ProgrammeOccurrences.ProgrammeOccurrence;
import com.zuehlke.pgadmissions.referencedata.v2.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.ModeOfAttendance;
import com.zuehlke.pgadmissions.referencedata.v2.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.Programme;

public class PrismProgrammeBuilder {
	private String academicYear;
	private String code;
	private String endDate;
	private String name;
	private String startDate;
	private String studyOptionCode;
	private String studyOption;
	private String identifier;
    private boolean atasRequired;

    public PrismProgrammeBuilder atasRequired(boolean atasRequired){
        this.atasRequired = atasRequired;
        return this;
    } 
    
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
	
	public PrismProgrammeBuilder identifier(String identifier) {
		this.identifier = identifier;
		return this;
	}
	
	public ProgrammeOccurrence toPrismProgramme(){
		ProgrammeOccurrence programmeOccurrence = new ProgrammeOccurrence();
		ModeOfAttendance modeOfAttendance = new ModeOfAttendance();
		Programme programme= new Programme();
		modeOfAttendance.setCode(studyOptionCode);
		modeOfAttendance.setName(studyOption);
		programme.setCode(code);
		programme.setName(name);
		programme.setAtasRegistered(atasRequired);
		programmeOccurrence.setModeOfAttendance(modeOfAttendance);
		programmeOccurrence.setAcademicYear(academicYear);
		programmeOccurrence.setProgramme(programme);
		programmeOccurrence.setIdentifier(identifier);
		programmeOccurrence.setEndDate(endDate);
		programmeOccurrence.setStartDate(startDate);
		return programmeOccurrence;
		
	}
}
