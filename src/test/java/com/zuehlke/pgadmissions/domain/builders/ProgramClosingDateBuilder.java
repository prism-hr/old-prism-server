package com.zuehlke.pgadmissions.domain.builders;


import java.util.Date;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;

public class ProgramClosingDateBuilder {

    private Integer id;
    private Program program;
    private Integer studyPlaces;
    private Date closingDate;
    
    public ProgramClosingDateBuilder id(Integer id){
        this.id = id;
        return this;
    }
    
    public ProgramClosingDateBuilder program(Program program){
        this.program = program;
        return this;
    }
    
    public ProgramClosingDateBuilder studyPlaces(Integer studyPlaces){
        this.studyPlaces = studyPlaces;
        return this;
    }
    
    
    public ProgramClosingDateBuilder closingDate(Date closingDate){
        this.closingDate = closingDate;
        return this;
    }
    
    public ProgramClosingDate build(){
    	ProgramClosingDate programClosingDate = new ProgramClosingDate();
        programClosingDate.setId(id);
        programClosingDate.setProgram(program);
        programClosingDate.setStudyPlaces(studyPlaces);
        programClosingDate.setClosingDate(this.closingDate);
        return programClosingDate;
    }
}
