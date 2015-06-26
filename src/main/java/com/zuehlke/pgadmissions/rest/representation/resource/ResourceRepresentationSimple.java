package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class ResourceRepresentationSimple {

    private PrismScope resourceScope;

    private Integer id;

    private String code;

    private String importedCode;

    private String title;

    public PrismScope getResourceScope() {
        return resourceScope;
    }

    public void setResourceScope(PrismScope resourceScope) {
        this.resourceScope = resourceScope;
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

    public ResourceRepresentationSimple withResourceScope(PrismScope resourceScope) {
        this.resourceScope = resourceScope;
        return this;
    }

    public ResourceRepresentationSimple withId(Integer id) {
        this.id = id;
        return this;
    }
    
    public ResourceRepresentationSimple withCode(String code) {
        this.code = code;
        return this;
    }
    
    public ResourceRepresentationSimple withImportedCode(String importedCode) {
        this.importedCode = importedCode;
        return this;
    }
    
    public ResourceRepresentationSimple withTitle(String title) {
        this.title = title;
        return this;
    }
    
}
