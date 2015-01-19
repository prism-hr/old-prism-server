package com.zuehlke.pgadmissions.rest.dto;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.FilterExpression;
import com.zuehlke.pgadmissions.domain.definitions.FilterPropertyType;

public interface FilterProperty {

    public abstract String getPropertyName();

    public abstract List<FilterExpression> getPermittedExpressions();

    public abstract FilterPropertyType getPropertyType();

}
