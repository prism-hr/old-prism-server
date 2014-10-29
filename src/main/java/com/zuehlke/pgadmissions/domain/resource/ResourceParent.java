package com.zuehlke.pgadmissions.domain.resource;

import java.math.BigDecimal;

public abstract class ResourceParent extends Resource {

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
    
    public abstract Integer getApplicationRatingCount();
    
    public abstract void setApplicationRatingCount(Integer applicationRatingCount);
    
    public abstract BigDecimal getApplicationRatingCountAverageNonZero();
    
    public abstract void setApplicationRatingCountAverageNonZero(BigDecimal applicationRatingCountAverageNonZero);

    public abstract BigDecimal getApplicationRatingAverage();
    
    public abstract void setApplicationRatingAverage(BigDecimal applicationRatingAverage);

}
