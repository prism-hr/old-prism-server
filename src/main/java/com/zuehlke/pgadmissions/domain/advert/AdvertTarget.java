package com.zuehlke.pgadmissions.domain.advert;

import java.math.BigDecimal;

public abstract class AdvertTarget<T> extends AdvertAttribute<T> {

    public abstract BigDecimal getImportance();

    public abstract void setImportance(BigDecimal importance);
    
    public abstract String getTitle();

}
