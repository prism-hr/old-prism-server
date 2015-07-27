package com.zuehlke.pgadmissions.dto;

public class ImportedProgramSubjectAreaDTO {

    private Integer program;
    
    private Integer subjectArea;
    
    private Integer weight;

    public ImportedProgramSubjectAreaDTO(Integer program, Integer subjectArea, Integer weight) {
        this.program = program;
        this.subjectArea = subjectArea;
        this.weight = weight;
    }

    public Integer getProgram() {
        return program;
    }

    public Integer getSubjectArea() {
        return subjectArea;
    }

    public Integer getWeight() {
        return weight;
    }
    
    @Override
    public String toString() {
        return "(" + program + ", " + subjectArea + ", " + weight + ")";
    }
    
}
