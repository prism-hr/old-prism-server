package com.zuehlke.pgadmissions.rest.dto.resource;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.rest.dto.user.UserSimpleDTO;

public class DepartmentInvitationDTO {

    private Integer advertId;

    @NotNull
    @Valid
    private DepartmentDTO department;

    @Valid
    private UserSimpleDTO departmentUser;

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

    public UserSimpleDTO getDepartmentUser() {
        return departmentUser;
    }

    public void setDepartmentUser(UserSimpleDTO departmentUser) {
        this.departmentUser = departmentUser;
    }

}
