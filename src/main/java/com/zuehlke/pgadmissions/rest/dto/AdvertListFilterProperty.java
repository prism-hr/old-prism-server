package com.zuehlke.pgadmissions.rest.dto;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.FilterExpression;
import com.zuehlke.pgadmissions.domain.definitions.FilterPropertyType;

public enum AdvertListFilterProperty implements FilterProperty {

//    INSTITUTION(),
//    PROGRAM(),
//    PROJECT(),
//    STUDY_OPTION(),
//    FEE(),
//    PAY(),
//    DURATION();
;

    private AdvertListFilterProperty(String propertyName, List<FilterExpression> permittedExpressions, FilterPropertyType propertyType) {
        this.propertyName = propertyName;
        this.permittedExpressions = permittedExpressions;
        this.propertyType = propertyType;
    }

    private String propertyName;

    private List<FilterExpression> permittedExpressions;

    private FilterPropertyType propertyType;

    @Override
    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public List<FilterExpression> getPermittedExpressions() {
        return permittedExpressions;
    }

    @Override
    public FilterPropertyType getPropertyType() {
        return propertyType;
    }

}
