package com.zuehlke.pgadmissions.dto;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;

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
