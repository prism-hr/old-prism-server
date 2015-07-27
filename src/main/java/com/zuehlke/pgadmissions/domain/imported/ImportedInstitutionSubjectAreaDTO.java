package com.zuehlke.pgadmissions.domain.imported;

public class ImportedInstitutionSubjectAreaDTO {

    private Integer importedInstitution;

    private Integer importedSubjectArea;

    private Long relationStrength;

    public Integer getImportedInstitution() {
        return importedInstitution;
    }

    public void setImportedInstitution(Integer importedInstitution) {
        this.importedInstitution = importedInstitution;
    }

    public Integer getImportedSubjectArea() {
        return importedSubjectArea;
    }

    public void setImportedSubjectArea(Integer importedSubjectArea) {
        this.importedSubjectArea = importedSubjectArea;
    }

    public Long getRelationStrength() {
        return relationStrength;
    }

    public void setRelationStrength(Long relationStrength) {
        this.relationStrength = relationStrength;
    }
    
    public String getInsertDefinition() {
        return "(" + importedInstitution + ", " + importedSubjectArea + ", " + relationStrength.toString() + ", " + "1)";
    }

}
