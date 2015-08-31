package com.zuehlke.pgadmissions.rest.representation.imported;

import uk.co.alumeni.prism.api.model.imported.response.ImportedProgramResponse;

public class ImportedProgramRepresentation extends ImportedProgramResponse {

    private boolean requiresDepartment;

    public boolean isRequiresDepartment() {
        return requiresDepartment;
    }

    public void setRequiresDepartment(boolean requiresDepartment) {
        this.requiresDepartment = requiresDepartment;
    }

}
