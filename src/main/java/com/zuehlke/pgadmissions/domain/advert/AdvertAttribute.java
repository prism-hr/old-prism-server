package com.zuehlke.pgadmissions.domain.advert;

import com.zuehlke.pgadmissions.domain.UniqueEntity;

public abstract class AdvertAttribute implements UniqueEntity {

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract Advert getAdvert();

    public abstract void setAdvert(Advert advert);
    
    public abstract Object getValue();

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("advert", getAdvert());
    }

}
