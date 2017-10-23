package uk.co.alumeni.prism.rest.dto.profile;

import uk.co.alumeni.prism.rest.dto.ListFilterDTO;

import java.util.List;

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
