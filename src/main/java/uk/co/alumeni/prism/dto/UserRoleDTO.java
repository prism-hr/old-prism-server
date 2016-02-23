package uk.co.alumeni.prism.dto;

import static org.apache.commons.lang3.ObjectUtils.compare;
import static uk.co.alumeni.prism.utils.PrismComparisonUtils.compareRoles;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.user.User;

public class UserRoleDTO implements Comparable<UserRoleDTO> {

    private User user;

    private PrismRole role;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PrismRole getRole() {
        return role;
    }

    public void setRole(PrismRole role) {
        this.role = role;
    }

    @Override
    public int compareTo(UserRoleDTO other) {
        int compare = compareRoles(role, other.getRole());
        return compare == 0 ? compare(user, other.getUser()) : compare;
    }

}
