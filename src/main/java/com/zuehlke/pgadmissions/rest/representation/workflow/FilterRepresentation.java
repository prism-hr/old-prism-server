package com.zuehlke.pgadmissions.rest.representation.workflow;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.FilterExpression;
import com.zuehlke.pgadmissions.domain.definitions.FilterProperty;
import com.zuehlke.pgadmissions.domain.definitions.FilterValueType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class FilterRepresentation {

    private FilterProperty propertyName;

    private List<FilterExpression> permittedExpressions;

    private FilterValueType valueType;

    private List<PrismScope> permittedScopes;

    public FilterRepresentation(FilterProperty propertyName, List<FilterExpression> permittedExpressions, FilterValueType valueType, List<PrismScope> permittedScopes) {
        this.propertyName = propertyName;
        this.permittedExpressions = permittedExpressions;
        this.valueType = valueType;
        this.permittedScopes = permittedScopes;
    }

    public FilterProperty getPropertyName() {
        return propertyName;
    }

    public List<FilterExpression> getPermittedExpressions() {
        return permittedExpressions;
    }

    public FilterValueType getValueType() {
        return valueType;
    }

    public List<PrismScope> getPermittedScopes() {
        return permittedScopes;
    }
}
