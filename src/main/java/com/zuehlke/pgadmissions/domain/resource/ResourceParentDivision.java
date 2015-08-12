package com.zuehlke.pgadmissions.domain.resource;

import uk.co.alumeni.prism.api.model.resource.ResourceParentDivisionDefinition;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.workflow.user.PrismUserReassignmentProcessor;

public abstract class ResourceParentDivision<T extends PrismUserReassignmentProcessor> extends ResourceParent<T> implements
        ResourceParentDivisionDefinition<Advert> {

    public abstract String getImportedCode();

    public abstract void setImportedCode(String importedCode);

}