package com.zuehlke.pgadmissions.workflow.evaluators;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.services.AdvertService;

@Component
public class ResourceAdvertConnectionsEvaluator implements ResourceCompletenessEvaluator<ResourceParent> {

    @Inject
    private AdvertService advertService;

    @Override
    public boolean evaluate(ResourceParent resource) {
        return isEmpty(advertService.getAdvertConnections(resource.getAdvert()));
    }

}
