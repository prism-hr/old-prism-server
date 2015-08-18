package com.zuehlke.pgadmissions.domain.resource;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.workflow.user.PrismUserReassignmentProcessor;

import uk.co.alumeni.prism.api.model.resource.ResourceParentDivisionDefinition;

public abstract class ResourceParentDivision<T extends PrismUserReassignmentProcessor> extends ResourceParent<T> implements
        ResourceParentDivisionDefinition<Advert> {

    public abstract String getImportedCode();

    public abstract void setImportedCode(String importedCode);

}
