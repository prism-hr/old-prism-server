package uk.co.alumeni.prism.rest;

import static com.google.common.base.Objects.equal;
import static org.apache.commons.lang.BooleanUtils.isTrue;

public abstract class UserDescriptorExtended<T, U> extends UserDescriptor {

    public abstract String getFirstName2();

    public abstract void setFirstName2(String firstName2);

    public abstract String getFirstName3();

    public abstract void setFirstName3(String firstName3);

    public abstract String getFullName();

    public abstract void setFullName(String fullName);

    public abstract Boolean getEnabled();

    public abstract void setEnabled(Boolean enabled);

    public abstract String getLinkedinProfileUrl();

    public abstract void setLinkedinProfileUrl(String linkedinProfileUrl);

    public abstract String getLinkedinImageUrl();

    public abstract void setLinkedinImageUrl(String linkedinImageUrl);

    public abstract T getPortraitImage();

    public abstract void setPortraitImage(T portraitImage);

    public abstract U getCreatorUser();

    public abstract void setCreatorUser(U creatorUser);

    public boolean isEnabled() {
        return isTrue(getEnabled());
    }

    public boolean checkUserEditable(UserDescriptor currentUser) {
        if (currentUser != null) {
            return !isEnabled() && equal(getCreatorUser(), currentUser.getId());
        }
        return false;
    }

}
