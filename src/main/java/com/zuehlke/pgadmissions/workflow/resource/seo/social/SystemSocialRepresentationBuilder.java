package com.zuehlke.pgadmissions.workflow.resource.seo.social;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.dto.SocialMetadataDTO;
import com.zuehlke.pgadmissions.services.SystemService;

@Component
public class SystemSocialRepresentationBuilder implements SocialRepresentationBuilder {

    @Inject
    private SystemService systemService;

    @Override
    public SocialMetadataDTO build(Advert advert) throws Exception {
        return systemService.getSocialMetadata();
    }

}
