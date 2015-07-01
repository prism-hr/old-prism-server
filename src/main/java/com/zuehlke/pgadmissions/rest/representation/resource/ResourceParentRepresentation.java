package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertRepresentation;

public class ResourceParentRepresentation extends ResourceRepresentationExtended {

    private AdvertRepresentation advert;
    
    private Integer backgroundImage;

    private List<PrismAction> partnerActions;

    public AdvertRepresentation getAdvert() {
        return advert;
    }

    public void setAdvert(AdvertRepresentation advert) {
        this.advert = advert;
    }

    public Integer getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Integer backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public List<PrismAction> getPartnerActions() {
        return partnerActions;
    }

    public void setPartnerActions(List<PrismAction> partnerActions) {
        this.partnerActions = partnerActions;
    }

}
