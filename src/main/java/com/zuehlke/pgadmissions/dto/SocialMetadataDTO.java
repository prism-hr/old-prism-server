package com.zuehlke.pgadmissions.dto;

public class SocialMetadataDTO {

	private String author;

	private String title;

	private String description;

	private String thumbnailUrl;

	private String resourceUrl;

	public final String getAuthor() {
		return author;
	}

	public final String getTitle() {
		return title;
	}

	public final String getDescription() {
		return description;
	}

	public final String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public final String getResourceUrl() {
		return resourceUrl;
	}

	public SocialMetadataDTO withAuthor(String author) {
		this.author = author;
		return this;
	}

	public SocialMetadataDTO withTitle(String title) {
		this.title = title;
		return this;
	}

	public SocialMetadataDTO withDescription(String description) {
		this.description = description;
		return this;
	}

	public SocialMetadataDTO withThumbnailUrl(String imageUrl) {
		this.thumbnailUrl = imageUrl;
		return this;
	}

	public SocialMetadataDTO withResourceUrl(String openGraphUrl) {
		this.resourceUrl = openGraphUrl;
		return this;
	}

}
