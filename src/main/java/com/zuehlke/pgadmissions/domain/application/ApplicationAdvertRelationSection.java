package com.zuehlke.pgadmissions.domain.application;

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.profile.ProfileAdvertRelationSection;

public abstract class ApplicationAdvertRelationSection extends ApplicationSection implements ProfileAdvertRelationSection<Application>, UniqueEntity {

    public abstract Application getAssociation();

    public abstract void setAssociation(Application association);

    public abstract Advert getAdvert();

    public abstract void setAdvert(Advert advert);

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("association", getAssociation()).addProperty("advert", getAdvert());
    }

}
