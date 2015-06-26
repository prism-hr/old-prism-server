package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class ResourceRepresentationSimpleWithImages extends ResourceRepresentationSimple {

    private Integer logoImage;

    private Integer backgroundImage;

    public Integer getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(Integer logoImage) {
        this.logoImage = logoImage;
    }

    public Integer getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Integer backgroundImage) {
        this.backgroundImage = backgroundImage;
    }
    
    public ResourceRepresentationSimpleWithImages withResourceScope(PrismScope resourceScope) {
        setResourceScope(resourceScope);
        return this;
    }

    public ResourceRepresentationSimpleWithImages withId(Integer id) {
        setId(id);
        return this;
    }
    
    public ResourceRepresentationSimpleWithImages withCode(String code) {
        setCode(code);
        return this;
    }
    
    public ResourceRepresentationSimpleWithImages withImportedCode(String importedCode) {
        setImportedCode(importedCode);
        return this;
    }
    
    public ResourceRepresentationSimpleWithImages withTitle(String title) {
        setTitle(title);
        return this;
    }

    public ResourceRepresentationSimpleWithImages withLogoImage(Integer logoImage) {
        this.logoImage = logoImage;
        return this;
    }

    public ResourceRepresentationSimpleWithImages withBackgroundImage(Integer backgroundImage) {
        this.backgroundImage = backgroundImage;
        return this;
    }

}
