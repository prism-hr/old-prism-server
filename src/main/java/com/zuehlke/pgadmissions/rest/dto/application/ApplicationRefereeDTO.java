package com.zuehlke.pgadmissions.rest.dto.application;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.zuehlke.pgadmissions.rest.dto.AssignedUserDTO;
import org.hibernate.validator.constraints.NotEmpty;

public class ApplicationRefereeDTO {

    private Integer id;

    @NotNull
    @Valid
    private AssignedUserDTO user;

    @NotEmpty
    @Size(max = 50)
    private String phone;

    @Size(min = 6, max = 32)
    private String skype;

    @NotEmpty
    @Size(max = 200)
    private String jobEmployer;

    @NotEmpty
    @Size(max = 200)
    private String jobTitle;

    @NotNull
    private AddressDTO address;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AssignedUserDTO getUser() {
        return user;
    }

    public void setUser(AssignedUserDTO user) {
        this.user = user;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public String getJobEmployer() {
        return jobEmployer;
    }

    public void setJobEmployer(String jobEmployer) {
        this.jobEmployer = jobEmployer;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }
}
