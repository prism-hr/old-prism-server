package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertRepresentationSimple;

public class ResourceParentRepresentation extends ResourceRepresentationExtended {

    private AdvertRepresentationSimple advert;

    private ResourceEmailListRepresentation recruiterEmailList;

    private ResourceEmailListRepresentation applicantEmailList;

    private List<PrismAction> partnerActions;

    private List<ResourceRepresentationIdentity> resourcesNotYetEndorsedFor;

    public AdvertRepresentationSimple getAdvert() {
        return advert;
    }

    public void setAdvert(AdvertRepresentationSimple advert) {
        this.advert = advert;
    }

    public ResourceEmailListRepresentation getRecruiterEmailList() {
        return recruiterEmailList;
    }

    public void setRecruiterEmailList(ResourceEmailListRepresentation recruiterEmailList) {
        this.recruiterEmailList = recruiterEmailList;
    }

    public ResourceEmailListRepresentation getApplicantEmailList() {
        return applicantEmailList;
    }

    public void setApplicantEmailList(ResourceEmailListRepresentation applicantEmailList) {
        this.applicantEmailList = applicantEmailList;
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
