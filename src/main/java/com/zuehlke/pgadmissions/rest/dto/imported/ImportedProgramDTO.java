package com.zuehlke.pgadmissions.rest.dto.imported;

import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;

public class ImportedProgramDTO extends ImportedEntityDTO {

    @NotNull
    private ImportedInstitutionDTO institution;

    @NotNull
    private ImportedEntityDTO qualificationType;

    @URL
    private String homepage;

    public ImportedInstitutionDTO getInstitution() {
        return institution;
    }

    public void setInstitution(ImportedInstitutionDTO institution) {
        this.institution = institution;
    }

    public ImportedEntityDTO getQualificationType() {
        return qualificationType;
    }

    public void setQualificationType(ImportedEntityDTO qualificationType) {
        this.qualificationType = qualificationType;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }
}
