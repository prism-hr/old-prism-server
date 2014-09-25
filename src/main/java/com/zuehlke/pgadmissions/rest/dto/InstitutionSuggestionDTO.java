package com.zuehlke.pgadmissions.rest.dto;

public class InstitutionSuggestionDTO {

    private Integer id;
    
    private String title;
    
    private String fullAddress;
    
    private String googleIdentifier;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final String getTitle() {
        return title;
    }

    public final void setTitle(String title) {
        this.title = title;
    }

    public final String getFullAddress() {
        return fullAddress;
    }

    public final void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public final String getGoogleIdentifier() {
        return googleIdentifier;
    }

    public final void setGoogleIdentifier(String googleIdentifier) {
        this.googleIdentifier = googleIdentifier;
    }
    
}
