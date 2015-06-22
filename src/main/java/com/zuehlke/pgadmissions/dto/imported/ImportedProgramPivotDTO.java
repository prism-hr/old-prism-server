package com.zuehlke.pgadmissions.dto.imported;

import com.google.common.base.Objects;

public class ImportedProgramPivotDTO extends ImportedEntityPivotDTO {

    private Integer program;

    private String qualification;
    
    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public Integer getProgram() {
        return program;
    }

    public void setProgram(Integer program) {
        this.program = program;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(program, qualification, getName());
    }
    
    @Override
    public boolean equals(Object object) {
        if (!super.equals(object)) {
            return false;
        }
        ImportedProgramPivotDTO other = (ImportedProgramPivotDTO) object;
        return Objects.equal(program, other.getProgram()) && Objects.equal(qualification, other.getQualification());
    }
    
}
