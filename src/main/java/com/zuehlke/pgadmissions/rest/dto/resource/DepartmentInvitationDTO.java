package com.zuehlke.pgadmissions.rest.dto.resource;

import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserSimpleDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class DepartmentInvitationDTO {

    private Integer advertId;

    @NotNull
    @Valid
    private ResourceParentDivisionDTO department;

    @Valid
    private UserDTO departmentUser;

    public Integer getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Integer advertId) {
        this.advertId = advertId;
    }

    public ResourceParentDivisionDTO getDepartment() {
        return department;
    }

    public void setDepartment(ResourceParentDivisionDTO department) {
        this.department = department;
    }

    public UserDTO getDepartmentUser() {
        return departmentUser;
    }

    public void setDepartmentUser(UserSimpleDTO departmentUser) {
        this.departmentUser = departmentUser;
    }

}
