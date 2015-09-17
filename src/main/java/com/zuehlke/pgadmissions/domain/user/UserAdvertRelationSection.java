package com.zuehlke.pgadmissions.domain.user;

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.profile.ProfileAdvertRelationSection;

public abstract class UserAdvertRelationSection implements ProfileAdvertRelationSection<UserAccount>, UniqueEntity {

    public abstract UserAccount getAssociation();

    public abstract void setAssociation(UserAccount association);

    public abstract Advert getAdvert();

    public abstract void setAdvert(Advert advert);

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("association", getAssociation()).addProperty("advert", getAdvert());
    }

}
