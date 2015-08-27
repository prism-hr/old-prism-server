package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertRepresentationSimple;

public class ResourceParentRepresentation extends ResourceRepresentationExtended {

    private AdvertRepresentationSimple advert;

    private List<PrismAction> partnerActions;

    private List<ResourceRepresentationIdentity> resourcesNotYetEndorsedFor;

    public AdvertRepresentationSimple getAdvert() {
        return advert;
    }

    public void setAdvert(AdvertRepresentationSimple advert) {
        this.advert = advert;
    }

    public List<PrismAction> getPartnerActions() {
        return partnerActions;
    }

    public void setPartnerActions(List<PrismAction> partnerActions) {
        this.partnerActions = partnerActions;
    }

    public List<ResourceRepresentationIdentity> getResourcesNotYetEndorsedFor() {
        return resourcesNotYetEndorsedFor;
    }

    public void setResourcesNotYetEndorsedFor(List<ResourceRepresentationIdentity> resourcesNotYetEndorsedFor) {
        this.resourcesNotYetEndorsedFor = resourcesNotYetEndorsedFor;
    }

}
