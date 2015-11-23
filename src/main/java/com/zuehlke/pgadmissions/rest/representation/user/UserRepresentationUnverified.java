package com.zuehlke.pgadmissions.rest.representation.user;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismRoleContext;
import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;

public class UserRepresentationUnverified extends UserRepresentationSimple {

    private List<PrismRoleContext> contexts;

    public List<PrismRoleContext> getContexts() {
        return contexts;
    }

    public void setContexts(List<PrismRoleContext> contexts) {
        this.contexts = contexts;
    }

    public UserRepresentationUnverified withId(Integer id) {
        setId(id);
        return this;
    }

    public UserRepresentationUnverified withFirstName(String firstName) {
        setFirstName(firstName);
        return this;
    }

    public UserRepresentationUnverified withLastName(String lastName) {
        setLastName(lastName);
        return this;
    }

    public UserRepresentationUnverified withEmail(String email) {
        setEmail(email);
        return this;
    }

    public UserRepresentationUnverified withAccountProfileUrl(String accountProfileUrl) {
        setAccountProfileUrl(accountProfileUrl);
        return this;
    }

    public UserRepresentationUnverified withAccountImageUrl(String accountImageUrl) {
        setAccountImageUrl(accountImageUrl);
        return this;
    }

    public UserRepresentationUnverified withPortraitImage(DocumentRepresentation portraitImage) {
        setPortraitImage(portraitImage);
        return this;
    }

}
