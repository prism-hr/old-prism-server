package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;

public class ResourceRepresentationIdentity {

    private PrismScope scope;

    private Integer id;

    private String name;
    
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public DocumentRepresentation getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(DocumentRepresentation logoImage) {
        this.logoImage = logoImage;
    }

}
