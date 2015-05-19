package com.zuehlke.pgadmissions.workflow.resource.seo.social;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.dto.SocialMetadataDTO;

public interface SocialRepresentationBuilder {

    public SocialMetadataDTO build(Advert advert) throws Exception;

}
