package com.zuehlke.pgadmissions.rest.dto.user;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserAccountDTO {

    @NotNull
    private Boolean sendApplicationRecommendationNotification;

    @Size(min = 8, max = 15)
    private String password;

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
