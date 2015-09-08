package com.zuehlke.pgadmissions.dto;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;

public class ResourceRoleDTO {

    private Integer id;

    private PrismRole role;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PrismRole getRole() {
        return role;
    }

    public void setRole(PrismRole role) {
        this.role = role;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, role);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final ResourceRoleDTO other = (ResourceRoleDTO) object;
        return id.equals(other.getId()) && role.equals(other.getRole());
    }

}
