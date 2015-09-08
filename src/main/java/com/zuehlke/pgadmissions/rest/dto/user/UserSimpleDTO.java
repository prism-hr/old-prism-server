package com.zuehlke.pgadmissions.rest.dto.user;

import javax.validation.constraints.Size;

public class UserSimpleDTO extends UserDTO {

    @Size(max = 30)
    private String firstName2;

    @Size(max = 30)
    private String firstName3;

    private Integer portraitDocument;

    private Boolean sendApplicationRecommendationNotification;

    @Size(min = 8, max = 15)
    private String password;

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
