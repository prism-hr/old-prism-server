package uk.co.alumeni.prism.rest.dto.profile;

import org.hibernate.validator.constraints.NotEmpty;
import uk.co.alumeni.prism.rest.dto.application.ApplicationAdvertRelationSectionDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceRelationCreationDTO;
import uk.co.alumeni.prism.utils.validation.PhoneNumber;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProfileRefereeDTO extends ApplicationAdvertRelationSectionDTO {

    private Integer id;

    @Valid
    @NotNull
    private ResourceRelationCreationDTO resource;

    @NotEmpty
    @PhoneNumber
    private String phone;

    @Size(min = 6, max = 32)
    private String skype;
    
    @NotNull
    private Boolean supervisor;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public ResourceRelationCreationDTO getResource() {
        return resource;
    }

    @Override
    public void setResource(ResourceRelationCreationDTO resource) {
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
    
    public Boolean getSupervisor() {
        return supervisor;
    }
    
    public void setSupervisor(Boolean supervisor) {
        this.supervisor = supervisor;
    }
    
}
