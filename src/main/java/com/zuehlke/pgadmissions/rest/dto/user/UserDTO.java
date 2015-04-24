package com.zuehlke.pgadmissions.rest.dto.user;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class UserDTO {

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

    @NotEmpty
    @Email
    private String email;

    private Integer portraitDocument;

    private Boolean sendApplicationRecommendationNotification;

    @Size(min = 8, max = 15)
    private String password;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getPortraitDocument() {
        return portraitDocument;
    }

    public void setPortraitDocument(Integer portraitDocument) {
        this.portraitDocument = portraitDocument;
    }

    public Boolean getSendApplicationRecommendationNotification() {
        return sendApplicationRecommendationNotification;
    }

    public void setSendApplicationRecommendationNotification(Boolean sendApplicationRecommendationNotification) {
        this.sendApplicationRecommendationNotification = sendApplicationRecommendationNotification;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
