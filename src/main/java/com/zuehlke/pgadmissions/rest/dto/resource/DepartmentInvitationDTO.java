package com.zuehlke.pgadmissions.rest.dto.resource;

import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class DepartmentInvitationDTO {

    private Integer advertId;

    @NotNull
    @Valid
    private DepartmentDTO department;

    @Valid
    private UserDTO departmentUser;

    public Integer getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Integer advertId) {
        this.advertId = advertId;
    }

    public DepartmentDTO getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentDTO department) {
        this.department = department;
    }

    public UserDTO getDepartmentUser() {
        return departmentUser;
    }

    public void setDepartmentUser(UserDTO departmentUser) {
        this.departmentUser = departmentUser;
    }

}
