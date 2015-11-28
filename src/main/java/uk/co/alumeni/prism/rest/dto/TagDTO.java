package uk.co.alumeni.prism.rest.dto;

import javax.validation.constraints.Size;

public class TagDTO {

    @Size(max = 255)
    private String name;

    @Size(max = 2000)
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
