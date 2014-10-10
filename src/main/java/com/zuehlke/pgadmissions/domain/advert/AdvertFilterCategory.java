package com.zuehlke.pgadmissions.domain.advert;

import com.zuehlke.pgadmissions.domain.IUniqueEntity;

public abstract class AdvertFilterCategory implements IUniqueEntity {

    public abstract Integer getId();
    
    public abstract void setId(Integer id);
    
    public abstract Advert getAdvert();
    
    public abstract void setAdvert(Advert advert);
    
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
