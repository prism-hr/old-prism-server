package com.zuehlke.pgadmissions.rest.dto.resource;

import uk.co.alumeni.prism.api.model.resource.ResourceParentDivisionDefinition;

import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;

public class ResourceParentDivisionDTO extends ResourceParentDTO implements ResourceParentDivisionDefinition<AdvertDTO> {

    private String importedCode;

    @Override
    public String getImportedCode() {
        return importedCode;
    }

    @Override
    public void setImportedCode(String importedCode) {
        this.importedCode = importedCode;
    }

}
