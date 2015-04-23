package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;

public class ResourceParentDTO {

    private ResourceParent resource;
    
    private Advert advert;

    public ResourceParent getResource() {
        return resource;
    }

    public void setResource(ResourceParent resource) {
        this.resource = resource;
    }

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }
    
}
