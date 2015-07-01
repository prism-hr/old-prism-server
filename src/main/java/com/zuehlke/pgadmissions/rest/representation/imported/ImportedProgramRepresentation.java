package com.zuehlke.pgadmissions.rest.representation.imported;

public class ImportedProgramRepresentation extends ImportedEntitySimpleRepresentation {

    private ImportedInstitutionRepresentation institution;

    private ImportedEntitySimpleRepresentation qualificationType;

    private String level;

    private String qualification;

    private String homepage;
    
    public ImportedInstitutionRepresentation getInstitution() {
        return institution;
    }

    public void setInstitution(ImportedInstitutionRepresentation institution) {
        this.institution = institution;
    }

    public ImportedEntitySimpleRepresentation getQualificationType() {
        return qualificationType;
    }

    public void setQualificationType(ImportedEntitySimpleRepresentation qualificationType) {
        this.qualificationType = qualificationType;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public ImportedProgramRepresentation withId(Integer id) {
        setId(id);
        return this;
    }

}
