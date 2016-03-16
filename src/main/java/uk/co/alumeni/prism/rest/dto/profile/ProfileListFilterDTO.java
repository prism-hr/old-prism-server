package uk.co.alumeni.prism.rest.dto.profile;

import uk.co.alumeni.prism.rest.dto.ListFilterDTO;

public class ProfileListFilterDTO extends ListFilterDTO {

    private Integer userId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public ProfileListFilterDTO withUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

}
