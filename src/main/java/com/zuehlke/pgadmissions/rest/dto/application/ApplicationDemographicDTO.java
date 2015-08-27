package com.zuehlke.pgadmissions.rest.dto.application;

import com.zuehlke.pgadmissions.rest.dto.imported.ImportedEntityDTO;

public class ApplicationDemographicDTO {

    private ImportedEntityDTO ethnicity;

    private ImportedEntityDTO disability;

    public ImportedEntityDTO getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(ImportedEntityDTO ethnicity) {
        this.ethnicity = ethnicity;
    }

    public ImportedEntityDTO getDisability() {
        return disability;
    }

    public void setDisability(ImportedEntityDTO disability) {
        this.disability = disability;
    }
}
