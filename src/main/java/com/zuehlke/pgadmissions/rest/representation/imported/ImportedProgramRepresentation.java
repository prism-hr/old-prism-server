package com.zuehlke.pgadmissions.rest.representation.imported;

public class ImportedProgramRepresentation extends ImportedEntitySimpleRepresentation {

    private Integer institution;

    private Integer qualificationType;

    private String level;

    private String qualification;

    private String homepage;

    public Integer getInstitution() {
        return institution;
    }

    public void setInstitution(Integer institution) {
        this.institution = institution;
    }

    public Integer getQualificationType() {
        return qualificationType;
    }

    public void setQualificationType(Integer qualificationType) {
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

}
