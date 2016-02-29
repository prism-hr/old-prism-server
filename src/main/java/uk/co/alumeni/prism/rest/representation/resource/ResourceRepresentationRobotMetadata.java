package uk.co.alumeni.prism.rest.representation.resource;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.common.base.Joiner;

public class ResourceRepresentationRobotMetadata extends ResourceRepresentationIdentity {

    public String author;

    public String summary;

    public String description;

    public String homepage;

    public String thumbnailUrl;

    public String resourceUrl;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public ResourceRepresentationRobotMetadata withId(Integer id) {
        setId(id);
        return this;
    }

    public ResourceRepresentationRobotMetadata withName(String name) {
        setName(name);
        return this;
    }

    public ResourceRepresentationRobotMetadata withAuthor(String author) {
        this.author = author;
        return this;
    }

    public ResourceRepresentationRobotMetadata withSummmary(String summary) {
        this.summary = summary;
        return this;
    }

    public ResourceRepresentationRobotMetadata withThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
        return this;
    }

    public ResourceRepresentationRobotMetadata withResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
        return this;
    }

    public String getDescriptionDisplay(String homepageLabel) {
        return Joiner.on("").skipNulls().join(StringEscapeUtils.escapeHtml(summary), wrapString(description), buildHyperLink(homepage, homepageLabel));
    }

    private String wrapString(String input) {
        return input == null ? null : ("<p>" + input + "</p>").replace("<p><p>", "<p>").replace("</p></p>", "</p>");
    }

    private String buildHyperLink(String url, String title) {
        return url == null ? null : "<p><a href=\"" + url + "\">" + title + "</a></p>";
    }

}
