package com.zuehlke.pgadmissions.rest.representation.resource;

import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;

import java.util.List;

public class DepartmentRepresentation extends ResourceParentDivisionRepresentation {

    private List<ImportedEntityResponse> importedPrograms;

    public List<ImportedEntityResponse> getImportedPrograms() {
        return importedPrograms;
    }

    public void setImportedPrograms(List<ImportedEntityResponse> importedPrograms) {
        this.importedPrograms = importedPrograms;
    }
}
