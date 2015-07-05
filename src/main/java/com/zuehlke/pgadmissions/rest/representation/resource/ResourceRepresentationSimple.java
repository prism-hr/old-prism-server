package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class ResourceRepresentationSimple {

    private PrismScope resourceScope;

    private Integer resourceId;

    private String code;

    private String importedCode;

    private String title;

    private Integer logoImage;

    public PrismScope getResourceScope() {
        return resourceScope;
    }

    public void setResourceScope(PrismScope resourceScope) {
        this.resourceScope = resourceScope;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
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

    public Integer getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(Integer logoImage) {
        this.logoImage = logoImage;
    }
    
    public ResourceRepresentationSimple withResourceScope(PrismScope resourceScope) {
        this.resourceScope = resourceScope;
        return this;
    }
    
    public ResourceRepresentationSimple withResourceId(Integer resourceId) {
        this.resourceId = resourceId;
        return this;
    }
    
    public ResourceRepresentationSimple withTitle(String title) {
        this.title = title;
        return this;
    }
    
    public ResourceRepresentationSimple withLogoImage(Integer logoImage) {
        this.logoImage = logoImage;
        return this;
    }

}
