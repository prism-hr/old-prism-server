package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.representation.FileRepresentation;

public class ResourceRepresentationSimple {

    private PrismScope scope;

    private Integer id;

    private String code;

    private String importedCode;

    private String title;

    private FileRepresentation logoImage;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public FileRepresentation getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(FileRepresentation logoImage) {
        this.logoImage = logoImage;
    }

    public ResourceRepresentationSimple withScope(PrismScope resourceScope) {
        this.scope = resourceScope;
        return this;
    }

    public ResourceRepresentationSimple withId(Integer resourceId) {
        this.id = resourceId;
        return this;
    }

    public ResourceRepresentationSimple withTitle(String title) {
        this.title = title;
        return this;
    }

    public ResourceRepresentationSimple withLogoImage(FileRepresentation logoImage) {
        this.logoImage = logoImage;
        return this;
    }

}
