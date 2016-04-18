package uk.co.alumeni.prism.domain.user;

import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.profile.ProfileAdvertRelationSection;

public abstract class UserAdvertRelationSection extends UserSection implements ProfileAdvertRelationSection<UserAccount> {

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
        return super.getEntitySignature().addProperty("advert", getAdvert());
    }

}
