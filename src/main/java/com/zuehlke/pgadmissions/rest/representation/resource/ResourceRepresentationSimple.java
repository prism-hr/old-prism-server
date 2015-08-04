package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.state.StateRepresentationSimple;

public class ResourceRepresentationSimple extends ResourceRepresentationIdentity {

    private String code;

    private String importedCode;

    private StateRepresentationSimple state;

    private DocumentRepresentation logoImage;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImportedCode() {
        return importedCode;
    }

    public void setImportedCode(String importedCode) {
        this.importedCode = importedCode;
    }

    public StateRepresentationSimple getState() {
        return state;
    }

    public void setState(StateRepresentationSimple state) {
        this.state = state;
    }

    public DocumentRepresentation getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(DocumentRepresentation logoImage) {
        this.logoImage = logoImage;
    }

    public ResourceRepresentationSimple withScope(final com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope scope) {
        setScope(scope);
        return this;
    }

    public ResourceRepresentationSimple withId(Integer id) {
        setId(id);
        return this;
    }

    public ResourceRepresentationSimple withName(String name) {
        setName(name);
        return this;
    }

    public ResourceRepresentationSimple withLogoImage(DocumentRepresentation logoImage) {
        this.logoImage = logoImage;
        return this;
    }

}
