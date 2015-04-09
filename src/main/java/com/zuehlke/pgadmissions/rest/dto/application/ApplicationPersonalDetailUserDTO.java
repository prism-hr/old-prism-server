package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class ApplicationPersonalDetailUserDTO {

    @NotEmpty
    @Size(max = 30)
    private String firstName;

    @Size(max = 30)
    private String firstName2;

    @Size(max = 30)
    private String firstName3;

    @NotEmpty
    @Size(max = 40)
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName2() {
        return firstName2;
    }

    public void setFirstName2(String firstName2) {
        this.firstName2 = firstName2;
    }

    public String getFirstName3() {
        return firstName3;
    }

    public void setFirstName3(String firstName3) {
        this.firstName3 = firstName3;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
