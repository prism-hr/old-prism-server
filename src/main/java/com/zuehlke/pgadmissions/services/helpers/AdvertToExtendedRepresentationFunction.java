package com.zuehlke.pgadmissions.services.helpers;

import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertRepresentation;

@Component
public class AdvertToExtendedRepresentationFunction extends AdvertToRepresentationFunction implements Function<Advert, AdvertRepresentation> {

    @Override
    public AdvertRepresentation apply(Advert advert) {
        AdvertRepresentation representation = super.apply(advert);
        ResourceParent resource = advert.getResource();
        representation.setSponsorCount(resourceService.getResourceSponsorCount(resource));
        representation.setTopTenSponsors(resourceService.getResourceTopTenSponsors(resource));
        return representation;
    }

}
