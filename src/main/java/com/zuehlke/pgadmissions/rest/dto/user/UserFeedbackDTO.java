package com.zuehlke.pgadmissions.rest.dto.user;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class UserFeedbackDTO extends UserFeedbackDeclineDTO {

	@NotNull
	private Integer rating;

	@NotEmpty
	private String content;

    private String featureRequests;

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFeatureRequests() {
        return featureRequests;
    }

    public void setFeatureRequests(String featureRequests) {
        this.featureRequests = featureRequests;
    }
}
