package com.zuehlke.pgadmissions.domain;

import java.math.BigDecimal;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang3.text.WordUtils;

public abstract class ParentResource extends Resource {

    public abstract Integer getApplicationCreatedCount();
    
    public abstract void setApplicationCreatedCount(Integer applicationCreatedCount);
    
    public abstract Integer getApplicationSubmittedCount();
    
    public abstract void setApplicationSubmittedCount(Integer applicationSubmittedCount);
    
    public abstract Integer getApplicationApprovedCount();
    
    public abstract void setApplicationApprovedCount(Integer applicationApprovedCount);
    
    public abstract Integer getApplicationRejectedCount();
    
    public abstract void setApplicationRejectedCount(Integer applicationRejectedCount);
    
    public abstract Integer getApplicationWithdrawnCount();
    
    public abstract void setApplicationWithdrawnCount(Integer applicationWithdrawnCount);
    
    public abstract BigDecimal getApplicationRatingCountAverage();
    
    public abstract void setApplicationRatingCountAverage(BigDecimal applicationRatingAverage);

    public abstract BigDecimal getApplicationRatingAverage();
    
    public abstract void setApplicationRatingAverage(BigDecimal applicationRatingAverage);
    
    public abstract Integer getApplicationRatingCount05();

    public abstract void setApplicationRatingCount05(Integer applicationRatingCount05);

    public abstract Integer getApplicationRatingCount20();

    public abstract void setApplicationRatingCount20(Integer applicationRatingCount20);

    public abstract Integer getApplicationRatingCount35();

    public abstract void setApplicationRatingCount35(Integer applicationRatingCount35);

    public abstract Integer getApplicationRatingCount50();

    public abstract void setApplicationRatingCount50(Integer applicationRatingCount50);

    public abstract Integer getApplicationRatingCount65();

    public abstract void setApplicationRatingCount65(Integer applicationRatingCount65);

    public abstract Integer getApplicationRatingCount80();

    public abstract void setApplicationRatingCount80(Integer applicationRatingCount80);

    public abstract Integer getApplicationRatingCount95();

    public abstract void setApplicationRatingCount95(Integer applicationRatingCount95);
    
    public abstract BigDecimal getApplicationRatingAverage05();

    public abstract void setApplicationRatingAverage05(BigDecimal applicationRatingAverage05);

    public abstract BigDecimal getApplicationRatingAverage20();

    public abstract void setApplicationRatingAverage20(BigDecimal applicationRatingAverage20);

    public abstract BigDecimal getApplicationRatingAverage35();

    public abstract void setApplicationRatingAverage35(BigDecimal applicationRatingAverage35);

    public abstract BigDecimal getApplicationRatingAverage50();

    public abstract void setApplicationRatingAverage50(BigDecimal applicationRatingAverage50);

    public abstract BigDecimal getApplicationRatingAverage65();

    public abstract void setApplicationRatingAverage65(BigDecimal applicationRatingAverage65);

    public abstract BigDecimal getApplicationRatingAverage80();

    public abstract void setApplicationRatingAverage80(BigDecimal applicationRatingAverage80);

    public abstract BigDecimal getApplicationRatingAverage95();

    public abstract void setApplicationRatingAverage95(BigDecimal applicationRatingAverage95);

    public void setPercentileValue(String property, Integer percentile, Object value) {
        try {
            MethodUtils.invokeMethod(this, "set" + WordUtils.capitalize(property) + String.format("%02d", percentile), value);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

}
