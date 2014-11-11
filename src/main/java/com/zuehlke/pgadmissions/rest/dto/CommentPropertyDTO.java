package com.zuehlke.pgadmissions.rest.dto;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.definitions.ActionPropertyType;

public class CommentPropertyDTO {

    @NotNull
    private ActionPropertyType propertyType;

    @NotNull
    private String propertyLabel;

    @NotNull
    private String propertyValue;

    @DecimalMin("0.01")
    @DecimalMax("1.00")
    private BigDecimal propertyWeight;

    public final ActionPropertyType getPropertyType() {
        return propertyType;
    }

    public final void setPropertyType(ActionPropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public final String getPropertyLabel() {
        return propertyLabel;
    }

    public final void setPropertyLabel(String propertyLabel) {
        this.propertyLabel = propertyLabel;
    }

    public final String getPropertyValue() {
        return propertyValue;
    }

    public final void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public final BigDecimal getPropertyWeight() {
        return propertyWeight;
    }

    public final void setPropertyWeight(BigDecimal propertyWeight) {
        this.propertyWeight = propertyWeight;
    }
    
}
