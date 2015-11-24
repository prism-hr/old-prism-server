package uk.co.alumeni.prism.dto;

import com.google.common.base.Objects;

import uk.co.alumeni.prism.domain.user.User;

public class UserConnectionDTO {

    private User user;

    private Integer advert;

    public UserConnectionDTO(User user, Integer advert) {
        this.user = user;
        this.advert = advert;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getAdvert() {
        return advert;
    }

    public void setAdvert(Integer advert) {
        this.advert = advert;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(user, advert);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        UserConnectionDTO other = (UserConnectionDTO) object;
        return Objects.equal(user, other.getUser()) && Objects.equal(advert, other.getAdvert());
    }

}
