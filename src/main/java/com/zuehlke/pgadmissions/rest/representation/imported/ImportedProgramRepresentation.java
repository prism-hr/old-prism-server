package com.zuehlke.pgadmissions.rest.representation.imported;

public class ImportedProgramRepresentation extends ImportedEntitySimpleRepresentation {

    private ImportedEntitySimpleRepresentation institutionMapping;

    private ImportedEntitySimpleRepresentation qualificationTypeMapping;

    private String level;

    private String qualification;

    private String homepage;

    public Integer getInstitution() {
        return institutionMapping.getId();
    }

    public void setInstitution(Integer institution) {
        this.institutionMapping = new ImportedInstitutionRepresentation().withId(institution);
    }

    public ImportedEntitySimpleRepresentation getInstitutionMapping() {
        return institutionMapping;
    }

    public void setInstitutionMapping(ImportedEntitySimpleRepresentation institutionMapping) {
        this.institutionMapping = institutionMapping;
    }

    public Integer getQualificationType() {
        return qualificationTypeMapping.getId();
    }

    public void setQualificationType(Integer qualificationType) {
        this.qualificationTypeMapping = new ImportedEntitySimpleRepresentation().withId(qualificationType);
    }

    public ImportedEntitySimpleRepresentation getQualificationTypeMapping() {
        return qualificationTypeMapping;
    }

    public void setQualificationTypeMapping(ImportedEntitySimpleRepresentation qualificationTypeMapping) {
        this.qualificationTypeMapping = qualificationTypeMapping;
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
