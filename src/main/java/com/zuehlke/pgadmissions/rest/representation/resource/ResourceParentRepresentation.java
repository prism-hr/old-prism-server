package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertRepresentationSimple;

public class ResourceParentRepresentation extends ResourceRepresentationExtended {

    private String importedCode;

    private AdvertRepresentationSimple advert;

    private List<PrismActionCondition> externalConditions;

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

    public List<PrismActionCondition> getExternalConditions() {
        return externalConditions;
    }

    public void setExternalConditions(List<PrismActionCondition> externalConditions) {
        this.externalConditions = externalConditions;
    }

}
