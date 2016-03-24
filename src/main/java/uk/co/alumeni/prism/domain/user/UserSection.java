package uk.co.alumeni.prism.domain.user;

import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.profile.ProfileSection;

public abstract class UserSection implements ProfileSection<UserAccount>, UniqueEntity {

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract UserAccount getAssociation();

    public abstract void setAssociation(UserAccount association);

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("association", getAssociation());
    }

}
