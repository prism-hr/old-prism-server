package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertRepresentation;

public class ResourceParentRepresentation extends ResourceRepresentationExtended {

    private AdvertRepresentation advert;

    public AdvertRepresentation getAdvert() {
        return advert;
    }

    public void setAdvert(AdvertRepresentation advert) {
        this.advert = advert;
    }

}
