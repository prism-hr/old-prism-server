package com.zuehlke.pgadmissions.domain.imported;

public class ImportedInstitutionSubjectAreaDTO {

    private Integer institution;

    private Integer subjectArea;

    private Long relationStrength;

    public Integer getInstitution() {
        return institution;
    }

    public void setInstitution(Integer institution) {
        this.institution = institution;
    }

    public Integer getSubjectArea() {
        return subjectArea;
    }

    public void setSubjectArea(Integer subjectArea) {
        this.subjectArea = subjectArea;
    }

    public Long getRelationStrength() {
        return relationStrength;
    }

    public void setRelationStrength(Long relationStrength) {
        this.relationStrength = relationStrength;
    }

}
