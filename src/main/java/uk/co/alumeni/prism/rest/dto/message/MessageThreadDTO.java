package uk.co.alumeni.prism.rest.dto.message;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import uk.co.alumeni.prism.rest.dto.resource.ResourceDTO;

public class MessageThreadDTO {

    private Integer id;

    @Valid
    @NotNull
    private ResourceDTO resource;

    @NotEmpty
    @Size(max = 255)
    private String subject;

    @Valid
    @NotNull
    private MessageDTO message;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ResourceDTO getResource() {
        return resource;
    }

    public void setResource(ResourceDTO resource) {
        this.resource = resource;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public MessageDTO getMessage() {
        return message;
    }

    public void setMessage(MessageDTO message) {
        this.message = message;
    }

}
