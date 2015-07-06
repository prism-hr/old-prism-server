package com.zuehlke.pgadmissions.domain.resource;

import uk.co.alumeni.prism.api.model.resource.ResourceParentDivisionDefinition;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.user.User;

public abstract class ResourceParentDivision extends ResourceParent implements ResourceParentDivisionDefinition<User, Advert> {

    public abstract String getImportedCode();

    public abstract void setImportedCode(String importedCode);

}
