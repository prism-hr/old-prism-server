package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.state.StateRepresentationSimple;

public class ResourceRepresentationSimple extends ResourceRepresentationIdentity {

    private String code;

    private String importedCode;

    private StateRepresentationSimple state;
    
    private String telephone;

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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public ResourceRepresentationSimple withScope(PrismScope scope) {
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
        setLogoImage(logoImage);
        return this;
    }

    public ResourceRepresentationSimple withTelephone(String telephone) {
        this.telephone = telephone;
        return this;
    }
    
}
