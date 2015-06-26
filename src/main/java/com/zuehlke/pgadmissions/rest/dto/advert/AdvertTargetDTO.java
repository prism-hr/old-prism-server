package com.zuehlke.pgadmissions.rest.dto.advert;

import java.math.BigDecimal;

public class AdvertTargetDTO {

    private Integer value;
    
    private BigDecimal importance;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public BigDecimal getImportance() {
        return importance;
    }

    public void setImportance(BigDecimal importance) {
        this.importance = importance;
    }

}
