package com.zuehlke.pgadmissions.domain.imported;

import java.math.BigDecimal;

public class ImportedInstitutionSubjectAreaDTO {

    private Integer subjectArea;

    private BigDecimal relationStrength;

    public Integer getSubjectArea() {
        return subjectArea;
    }

    public void setSubjectArea(Integer subjectArea) {
        this.subjectArea = subjectArea;
    }

    public BigDecimal getRelationStrength() {
        return relationStrength;
    }

    public void setRelationStrength(BigDecimal relationStrength) {
        this.relationStrength = relationStrength;
    }

}
