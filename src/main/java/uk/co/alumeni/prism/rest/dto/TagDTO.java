package uk.co.alumeni.prism.rest.dto;

import javax.validation.constraints.Size;

public class TagDTO {

    @Size(max = 255)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
