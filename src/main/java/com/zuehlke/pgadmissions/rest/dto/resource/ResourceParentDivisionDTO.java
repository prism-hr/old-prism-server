package com.zuehlke.pgadmissions.rest.dto.resource;

import uk.co.alumeni.prism.api.model.resource.ResourceParentDivisionDefinition;

import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;

public class ResourceParentDivisionDTO extends ResourceParentDTO implements ResourceCreationDTO, ResourceParentDivisionDefinition<AdvertDTO> {

    private ResourceDTO parentResource;
    
    private InstitutionDTO newInstitution;
    
    private String importedCode;
    
    @Override
    public ResourceDTO getParentResource() {
        return parentResource;
    }

    @Override
    public void setParentResource(ResourceDTO parentResource) {
        this.parentResource = parentResource;
    }

    public InstitutionDTO getNewInstitution() {
        return newInstitution;
    }

    public void setNewInstitution(InstitutionDTO newInstitution) {
        this.newInstitution = newInstitution;
    }

    @Override
    public String getImportedCode() {
        return importedCode;
    }

    @Override
    public void setImportedCode(String importedCode) {
        this.importedCode = importedCode;
    }
    
    public ResourceParentDTO getNewParentResource() {
        return newInstitution;
    }

}
