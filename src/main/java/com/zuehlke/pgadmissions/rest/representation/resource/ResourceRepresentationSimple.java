package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.state.StateRepresentationSimple;

public class ResourceRepresentationSimple {

    private PrismScope scope;

    private Integer id;

    private String code;

    private String importedCode;

    private String name;

    private StateRepresentationSimple state;

    private DocumentRepresentation logoImage;

    public PrismScope getScope() {
        return scope;
    }

    public void setScope(PrismScope scope) {
        this.scope = scope;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public ResourceRepresentationSimple withId(Integer resourceId) {
        this.id = resourceId;
        return this;
    }

    public ResourceRepresentationSimple withName(String name) {
        this.name = name;
        return this;
    }

    public ResourceRepresentationSimple withLogoImage(DocumentRepresentation logoImage) {
        this.logoImage = logoImage;
        return this;
    }

}
