package uk.co.alumeni.prism.domain.advert;

import uk.co.alumeni.prism.domain.UniqueEntity;

public abstract class AdvertAttribute implements UniqueEntity {

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract Advert getAdvert();

    public abstract void setAdvert(Advert advert);

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("advert", getAdvert());
    }

}
