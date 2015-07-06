package com.zuehlke.pgadmissions.domain.resource;

import uk.co.alumeni.prism.api.model.resource.ResourceParentDivisionDefinition;

import com.zuehlke.pgadmissions.domain.advert.Advert;

public abstract class ResourceParentDivision extends ResourceParent implements ResourceParentDivisionDefinition<Advert> {

    public abstract String getImportedCode();

    public abstract void setImportedCode(String importedCode);

}
