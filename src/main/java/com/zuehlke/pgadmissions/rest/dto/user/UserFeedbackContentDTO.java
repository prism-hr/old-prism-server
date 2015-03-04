package com.zuehlke.pgadmissions.rest.dto.user;

import javax.validation.constraints.NotNull;

public class UserFeedbackContentDTO {

	@NotNull
	private Integer rating;

	private String content;

    private String featureRequest;

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

    public String getFeatureRequest() {
        return featureRequest;
    }

    public void setFeatureRequest(String featureRequest) {
        this.featureRequest = featureRequest;
    }

}
