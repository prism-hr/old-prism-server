package com.zuehlke.pgadmissions.rest.dto.application;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import uk.co.alumeni.prism.utils.validation.PhoneNumber;

import com.zuehlke.pgadmissions.domain.definitions.PrismRefereeType;
import com.zuehlke.pgadmissions.rest.dto.AssignedUserDTO;

public class ApplicationRefereeDTO {

    private Integer id;

    @NotNull
    @Valid
    private AssignedUserDTO user;

    @NotNull
    private PrismRefereeType refereeType;

    @NotEmpty
    @Size(max = 200)
    private String jobEmployer;

    @NotEmpty
    @Size(max = 200)
    private String jobTitle;

    @NotNull
    private AddressApplicationDTO address;

    @NotEmpty
    @PhoneNumber
    private String phone;

    @Size(min = 6, max = 32)
    private String skype;

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

    public final PrismRefereeType getRefereeType() {
        return refereeType;
    }

    public final void setRefereeType(PrismRefereeType refereeType) {
        this.refereeType = refereeType;
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

    public AddressApplicationDTO getAddress() {
        return address;
    }

    public void setAddress(AddressApplicationDTO address) {
        this.address = address;
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

}
