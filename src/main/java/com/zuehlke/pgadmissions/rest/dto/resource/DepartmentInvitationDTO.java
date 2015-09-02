package com.zuehlke.pgadmissions.rest.dto.resource;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;

public class DepartmentInvitationDTO {

    @NotNull
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
