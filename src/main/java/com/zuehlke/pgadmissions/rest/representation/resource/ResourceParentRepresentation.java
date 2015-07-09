package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.rest.representation.FileRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertRepresentation;

import java.util.List;

public class ResourceParentRepresentation extends ResourceRepresentationExtended {

    private AdvertRepresentation advert;

    private FileRepresentation backgroundImage;

    private List<PrismAction> partnerActions;

    public AdvertRepresentation getAdvert() {
        return advert;
    }

    public void setAdvert(AdvertRepresentation advert) {
        this.advert = advert;
    }

    public FileRepresentation getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(FileRepresentation backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public List<PrismAction> getPartnerActions() {
        return partnerActions;
    }

    public void setPartnerActions(List<PrismAction> partnerActions) {
        this.partnerActions = partnerActions;
    }

}
