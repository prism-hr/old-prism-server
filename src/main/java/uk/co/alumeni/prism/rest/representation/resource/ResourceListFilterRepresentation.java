package uk.co.alumeni.prism.rest.representation.resource;

import java.util.List;

import uk.co.alumeni.prism.domain.definitions.PrismResourceListConstraint;
import uk.co.alumeni.prism.domain.definitions.PrismResourceListFilterExpression;
import uk.co.alumeni.prism.domain.definitions.PrismResourceListFilterPropertyType;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;

public class ResourceListFilterRepresentation {

    private PrismResourceListConstraint propertyName;

    private List<FilterExpressionRepresentation> permittedExpressions;

    private PrismResourceListFilterPropertyType valueType;

    private List<PrismScope> permittedScopes;

    private boolean permittedInBulkMode;

    public ResourceListFilterRepresentation(PrismResourceListConstraint propertyName, List<FilterExpressionRepresentation> permittedExpressions,
            PrismResourceListFilterPropertyType valueType, List<PrismScope> permittedScopes, boolean permittedInBulkMode) {
        this.propertyName = propertyName;
        this.permittedExpressions = permittedExpressions;
        this.valueType = valueType;
        this.permittedScopes = permittedScopes;
        this.permittedInBulkMode = permittedInBulkMode;
    }

    public PrismResourceListConstraint getPropertyName() {
        return propertyName;
    }

    public List<FilterExpressionRepresentation> getPermittedExpressions() {
        return permittedExpressions;
    }

    public PrismResourceListFilterPropertyType getValueType() {
        return valueType;
    }

    public List<PrismScope> getPermittedScopes() {
        return permittedScopes;
    }

    public boolean isPermittedInBulkMode() {
        return permittedInBulkMode;
    }

    public static class FilterExpressionRepresentation {

        private PrismResourceListFilterExpression expressionName;

        private boolean negatable;

        public FilterExpressionRepresentation(PrismResourceListFilterExpression expressionName, boolean negatable) {
            this.expressionName = expressionName;
            this.negatable = negatable;
        }

        public PrismResourceListFilterExpression getExpressionName() {
            return expressionName;
        }

        public void setExpressionName(PrismResourceListFilterExpression expressionName) {
            this.expressionName = expressionName;
        }

        public boolean isNegatable() {
            return negatable;
        }

        public void setNegatable(boolean negatable) {
            this.negatable = negatable;
        }

    }

}
