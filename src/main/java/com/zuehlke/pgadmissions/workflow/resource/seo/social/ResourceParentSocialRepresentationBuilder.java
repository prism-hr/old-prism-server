package com.zuehlke.pgadmissions.workflow.resource.seo.social;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.dto.SocialMetadataDTO;
import com.zuehlke.pgadmissions.services.ResourceService;

@Component
public class ResourceParentSocialRepresentationBuilder implements SocialRepresentationBuilder {

    @Inject
    private ResourceService resourceService;
    
    @Override
    public SocialMetadataDTO build(Advert advert) throws Exception {
        Resource parentResource = advert.getResource();
        return new SocialMetadataDTO().withAuthor(parentResource.getUser().getFullName()).withTitle(advert.getTitle()).withDescription(advert.getSummary())
                .withThumbnailUrl(resourceService.getSocialThumbnailUrl(parentResource)).withResourceUrl(resourceService.getSocialResourceUrl(parentResource));
    }

}
