package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.zuehlke.pgadmissions.rest.dto.resource.ResourceCreationDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;

import uk.co.alumeni.prism.utils.validation.PhoneNumber;

public class ApplicationRefereeDTO extends ApplicationAdvertRelationSectionDTO {

    private Integer id;

    @Valid
    @NotNull
    private UserDTO user;

    @Valid
    @NotNull
    private ResourceCreationDTO resource;

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

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public ResourceCreationDTO getResource() {
        return resource;
    }

    @Override
    public void setResource(ResourceCreationDTO resource) {
        this.resource = resource;
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
