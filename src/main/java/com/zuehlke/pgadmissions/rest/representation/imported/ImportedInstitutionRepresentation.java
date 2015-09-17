package com.zuehlke.pgadmissions.rest.representation.imported;

import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;

public class ImportedInstitutionRepresentation extends ImportedEntityResponse {

    private boolean requiresDepartment;

    public boolean isRequiresDepartment() {
        return requiresDepartment;
    }

    public void setRequiresDepartment(boolean requiresDepartment) {
        this.requiresDepartment = requiresDepartment;
    }

}
