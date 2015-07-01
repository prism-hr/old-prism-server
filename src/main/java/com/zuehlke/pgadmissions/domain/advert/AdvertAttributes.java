package com.zuehlke.pgadmissions.domain.advert;

public abstract class AdvertAttributes {

    public abstract void clearAttributes(Object value);
    
    public abstract void storeAttribute(AdvertAttribute<?> value);

}
