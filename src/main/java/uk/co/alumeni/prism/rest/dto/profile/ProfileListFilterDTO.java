package uk.co.alumeni.prism.rest.dto.profile;

import java.util.List;

import uk.co.alumeni.prism.rest.dto.ListFilterDTO;

public class ProfileListFilterDTO extends ListFilterDTO {

    private List<Integer> userIds;

    public List<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Integer> userIds) {
        this.userIds = userIds;
    }

    public ProfileListFilterDTO withUserIds(List<Integer> userIds) {
        this.userIds = userIds;
        return this;
    }

}
