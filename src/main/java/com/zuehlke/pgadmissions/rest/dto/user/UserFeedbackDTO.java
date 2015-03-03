package com.zuehlke.pgadmissions.rest.dto.user;

import javax.validation.constraints.NotNull;

public class UserFeedbackDTO extends UserFeedbackDeclineDTO {

	@NotNull
	private Integer rating;

	@NotNull
	private String content;

	private Boolean recommended;

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

	public Boolean getRecommended() {
		return recommended;
	}

	public void setRecommended(Boolean recommended) {
		this.recommended = recommended;
	}

}
