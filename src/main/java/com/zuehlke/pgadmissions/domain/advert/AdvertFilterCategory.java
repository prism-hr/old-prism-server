package com.zuehlke.pgadmissions.domain.advert;

import com.zuehlke.pgadmissions.domain.UniqueEntity;

public abstract class AdvertFilterCategory implements UniqueEntity {

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract Advert getAdvert();

    public abstract void setAdvert(Advert advert);

    public abstract Object getValue();

    @SuppressWarnings("unchecked")
    public <T extends AdvertFilterCategory> T withAdvert(Advert advert) {
        setAdvert(advert);
        return (T) this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("advert", getAdvert());
    }

}
