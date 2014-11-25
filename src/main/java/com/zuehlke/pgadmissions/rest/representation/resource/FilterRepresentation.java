package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.FilterExpression;
import com.zuehlke.pgadmissions.domain.definitions.ResourceListFilterProperty;
import com.zuehlke.pgadmissions.domain.definitions.FilterPropertyType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class FilterRepresentation {

    private ResourceListFilterProperty propertyName;

    private List<FilterExpression> permittedExpressions;

    private FilterPropertyType valueType;

    private List<PrismScope> permittedScopes;

    public FilterRepresentation(ResourceListFilterProperty propertyName, List<FilterExpression> permittedExpressions, FilterPropertyType valueType, List<PrismScope> permittedScopes) {
        this.propertyName = propertyName;
        this.permittedExpressions = permittedExpressions;
        this.valueType = valueType;
        this.permittedScopes = permittedScopes;
    }

    public ResourceListFilterProperty getPropertyName() {
        return propertyName;
    }

    public List<FilterExpression> getPermittedExpressions() {
        return permittedExpressions;
    }

    public FilterPropertyType getValueType() {
        return valueType;
    }

    public List<PrismScope> getPermittedScopes() {
        return permittedScopes;
    }
}
