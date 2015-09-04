package com.zuehlke.pgadmissions.domain.resource;

import com.zuehlke.pgadmissions.domain.advert.Advert;

import uk.co.alumeni.prism.api.model.resource.ResourceParentDivisionDefinition;

public abstract class ResourceParentDivision extends ResourceParent implements ResourceParentDivisionDefinition<Advert> {

    public abstract String getImportedCode();

    public abstract void setImportedCode(String importedCode);

}
