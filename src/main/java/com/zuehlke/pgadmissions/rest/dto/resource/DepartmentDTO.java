package com.zuehlke.pgadmissions.rest.dto.resource;

import com.zuehlke.pgadmissions.rest.dto.imported.ImportedEntityDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class DepartmentDTO extends ResourceParentDivisionDTO {

    @Size(min = 1)
    @Valid
    @NotNull
    private List<ImportedEntityDTO> importedPrograms;

    public List<ImportedEntityDTO> getImportedPrograms() {
        return importedPrograms;
    }

    public void setImportedPrograms(List<ImportedEntityDTO> importedPrograms) {
        this.importedPrograms = importedPrograms;
    }

}
