package com.zuehlke.pgadmissions.rest.dto.resource;

import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedEntityDTO;
import uk.co.alumeni.prism.api.model.resource.ResourceParentDivisionDefinition;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class ResourceParentDivisionDTO extends ResourceParentDTO implements ResourceCreationDTO, ResourceParentDivisionDefinition<AdvertDTO> {

    private ResourceDTO parentResource;

    private String importedCode;

    @Size(min = 1)
    @Valid
    @NotNull
    private List<ImportedEntityDTO> importedPrograms;

    @Override
    public ResourceDTO getParentResource() {
        return parentResource;
    }

    @Override
    public void setParentResource(ResourceDTO parentResource) {
        this.parentResource = parentResource;
    }

    @Override
    public String getImportedCode() {
        return importedCode;
    }

    @Override
    public void setImportedCode(String importedCode) {
        this.importedCode = importedCode;
    }

    public List<ImportedEntityDTO> getImportedPrograms() {
        return importedPrograms;
    }

    public void setImportedPrograms(List<ImportedEntityDTO> importedPrograms) {
        this.importedPrograms = importedPrograms;
    }

}
