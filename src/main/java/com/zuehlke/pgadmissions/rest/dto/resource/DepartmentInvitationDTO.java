package com.zuehlke.pgadmissions.rest.dto.resource;

import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class DepartmentInvitationDTO {

    @NotNull
    @Valid
    private DepartmentDTO department;

    @Valid
    private UserDTO departmentUser;

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
