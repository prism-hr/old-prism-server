package com.zuehlke.pgadmissions.dto;

import java.math.BigDecimal;

public class DepartmentImportedSubjectAreaDTO {

    private Integer department;

    private Integer subjectArea;

    private BigDecimal programRelationStrength;

    private BigDecimal institutionRelationStrength;

    public Integer getDepartment() {
        return department;
    }

    public void setDepartment(Integer department) {
        this.department = department;
    }

    public Integer getSubjectArea() {
        return subjectArea;
    }

    public void setSubjectArea(Integer subjectArea) {
        this.subjectArea = subjectArea;
    }

    public BigDecimal getProgramRelationStrength() {
        return programRelationStrength;
    }

    public void setProgramRelationStrength(BigDecimal programRelationStrength) {
        this.programRelationStrength = programRelationStrength;
    }

    public BigDecimal getInstitutionRelationStrength() {
        return institutionRelationStrength;
    }

    public void setInstitutionRelationStrength(BigDecimal institutionRelationStrength) {
        this.institutionRelationStrength = institutionRelationStrength;
    }

}
