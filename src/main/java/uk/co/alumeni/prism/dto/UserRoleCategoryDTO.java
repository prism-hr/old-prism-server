package uk.co.alumeni.prism.dto;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;

import com.google.common.base.Objects;

public class UserRoleCategoryDTO {

    private User user;

    private Resource resource;

    private PrismRoleCategory roleCategory;

    public UserRoleCategoryDTO(User user, Resource resource, PrismRoleCategory roleCategory) {
        this.user = user;
        this.resource = resource;
        this.roleCategory = roleCategory;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public PrismRoleCategory getRoleCategory() {
        return roleCategory;
    }

    public void setRoleCategory(PrismRoleCategory roleCategory) {
        this.roleCategory = roleCategory;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(user, roleCategory, resource.getResourceScope(), resource.getId());
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        UserRoleCategoryDTO other = (UserRoleCategoryDTO) object;
        return Objects.equal(user, other.getUser()) && Objects.equal(resource.getResourceScope(), other.getResource().getResourceScope())
                && Objects.equal(resource.getId(), other.getResource().getId()) && Objects.equal(roleCategory, other.getRoleCategory());
    }

}
