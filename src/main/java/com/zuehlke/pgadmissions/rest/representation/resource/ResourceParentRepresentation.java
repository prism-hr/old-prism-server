package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.rest.representation.advert.AdvertRepresentationSimple;

public class ResourceParentRepresentation extends ResourceRepresentationExtended {

    private String importedCode;

    private AdvertRepresentationSimple advert;

    @Override
    public String getImportedCode() {
        return importedCode;
    }

    @Override
    public void setImportedCode(String importedCode) {
        this.importedCode = importedCode;
    }

    public AdvertRepresentationSimple getAdvert() {
        return advert;
    }

    public void setAdvert(AdvertRepresentationSimple advert) {
        this.advert = advert;
    }

}
