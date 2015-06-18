package com.zuehlke.pgadmissions.domain.advert;

import java.math.BigDecimal;

public abstract class AdvertTarget extends AdvertAttribute {

    public abstract BigDecimal getImportance();

    public abstract void setImportance(BigDecimal importance);

}
