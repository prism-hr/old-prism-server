package com.zuehlke.pgadmissions.dto.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InstitutionLookupResponseDTO {

    @JsonProperty("universal-name")
    private String linkedinIdentifier;
    
    private String description;
    
    @JsonProperty("website-url")
    private String homepage;
    
    @JsonProperty("square-logo-url")
    private String logoUri;

    public final String getLinkedinIdentifier() {
        return linkedinIdentifier;
    }

    public final void setLinkedinIdentifier(String linkedinIdentifier) {
        this.linkedinIdentifier = linkedinIdentifier;
    }

    public final String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public final String getHomepage() {
        return homepage;
    }

    public final void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public final String getLogoUri() {
        return logoUri;
    }

    public final void setLogoUri(String logoUri) {
        this.logoUri = logoUri;
    }
    
}
