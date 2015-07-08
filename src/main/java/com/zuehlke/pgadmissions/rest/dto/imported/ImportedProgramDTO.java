package com.zuehlke.pgadmissions.rest.dto.imported;

import javax.validation.constraints.NotNull;

public class ImportedProgramDTO extends ImportedEntityDTO {

    @NotNull
    private ImportedInstitutionDTO institution;
    
    @NotNull
    private Integer qualificationType;

    public ImportedInstitutionDTO getInstitution() {
        return institution;
    }

    public void setInstitution(ImportedInstitutionDTO institution) {
        this.institution = institution;
    }

    public Integer getQualificationType() {
        return qualificationType;
    }

    public void setQualificationType(Integer qualificationType) {
        this.qualificationType = qualificationType;
    }

}
