package uk.co.alumeni.prism.domain.application;

import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.profile.ProfileAdvertRelationSection;
import uk.co.alumeni.prism.domain.user.User;

public abstract class ApplicationAdvertRelationSection extends ApplicationSection implements ProfileAdvertRelationSection<Application>, UniqueEntity {

    @Override
    public abstract Application getAssociation();

    @Override
    public abstract void setAssociation(Application association);

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
