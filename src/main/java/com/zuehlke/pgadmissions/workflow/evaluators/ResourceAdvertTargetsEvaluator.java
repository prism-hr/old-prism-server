package com.zuehlke.pgadmissions.workflow.evaluators;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.services.AdvertService;

@Component
public class ResourceAdvertTargetsEvaluator implements ResourceCompletenessEvaluator<ResourceParent> {

    @Inject
    private AdvertService advertService;

    @Override
    public boolean evaluate(ResourceParent resource) {
        return isNotEmpty(advertService.getAdvertTargets(resource.getAdvert()));
    }

}
