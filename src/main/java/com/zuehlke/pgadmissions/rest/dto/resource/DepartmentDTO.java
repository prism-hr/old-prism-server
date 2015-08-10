package com.zuehlke.pgadmissions.rest.dto.resource;

import com.zuehlke.pgadmissions.rest.dto.imported.ImportedEntityDTO;

import javax.validation.Valid;
import java.util.List;

public class DepartmentDTO extends ResourceParentDivisionDTO {

    @Valid
    private List<ImportedEntityDTO> importedPrograms;

    public List<ImportedEntityDTO> getImportedPrograms() {
        return importedPrograms;
    }

    public void setImportedPrograms(List<ImportedEntityDTO> importedPrograms) {
        this.importedPrograms = importedPrograms;
    }

}
