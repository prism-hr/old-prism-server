package com.zuehlke.pgadmissions.rest.representation.resource.application;

import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;

public class ApplicationDemographicRepresentation {

    private ImportedEntityResponse ethnicity;

    private ImportedEntityResponse disability;

    public ImportedEntityResponse getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(ImportedEntityResponse ethnicity) {
        this.ethnicity = ethnicity;
    }

    public ImportedEntityResponse getDisability() {
        return disability;
    }

    public void setDisability(ImportedEntityResponse disability) {
        this.disability = disability;
    }

    public ApplicationDemographicRepresentation withEthnicity(ImportedEntityResponse ethnicity) {
        this.ethnicity = ethnicity;
        return this;
    }

    public ApplicationDemographicRepresentation withDisability(ImportedEntityResponse disability) {
        this.disability = disability;
        return this;
    }

}
