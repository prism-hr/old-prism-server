package uk.co.alumeni.prism.rest.representation.user;

import java.util.List;

import uk.co.alumeni.prism.domain.definitions.PrismRoleContext;
import uk.co.alumeni.prism.rest.representation.DocumentRepresentation;

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
