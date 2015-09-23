package com.zuehlke.pgadmissions.domain.advert;

import com.zuehlke.pgadmissions.domain.UniqueEntity;

public abstract class AdvertAttribute<T> implements UniqueEntity {

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract Advert getAdvert();

    public abstract void setAdvert(Advert advert);

    public abstract T getValue();

    public abstract void setValue(T value);

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("advert", getAdvert()).addProperty("value", getValue());
    }

}
