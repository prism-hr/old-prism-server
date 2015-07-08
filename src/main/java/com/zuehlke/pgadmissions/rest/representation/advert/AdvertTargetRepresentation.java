package com.zuehlke.pgadmissions.rest.representation.advert;

import java.math.BigDecimal;

public class AdvertTargetRepresentation {

    private Integer id;
    
    private String title;
    
    private BigDecimal importance;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public BigDecimal getImportance() {
        return importance;
    }

    public void setImportance(BigDecimal importance) {
        this.importance = importance;
    }

    public AdvertTargetRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }
    
    public AdvertTargetRepresentation withTitle(String title) {
        this.title = title;
        return this;
    }
    
    public AdvertTargetRepresentation withImportance(BigDecimal importance) {
        this.importance = importance;
        return this;
    }
    
    
}
