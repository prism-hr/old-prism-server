package com.zuehlke.pgadmissions.domain.user;

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.profile.ProfileAdvertRelationSection;

public abstract class UserAdvertRelationSection implements ProfileAdvertRelationSection<UserAccount>, UniqueEntity {

    @Override
    public abstract UserAccount getAssociation();

    @Override
    public abstract void setAssociation(UserAccount association);

    @Override
    public abstract User getUser();

    @Override
    public abstract void setUser(User user);

    @Override
    public abstract Advert getAdvert();

    @Override
    public abstract void setAdvert(Advert advert);

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("association", getAssociation()).addProperty("advert", getAdvert());
    }

}
