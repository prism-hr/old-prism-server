package com.zuehlke.pgadmissions.rest.dto.resource;

import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;

import uk.co.alumeni.prism.api.model.resource.ResourceParentDivisionDefinition;

public class ResourceParentDivisionDTO extends ResourceParentDTO implements ResourceParentDivisionDefinition<AdvertDTO> {

    private ResourceDTO parentResource;

    private String importedCode;

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

}
