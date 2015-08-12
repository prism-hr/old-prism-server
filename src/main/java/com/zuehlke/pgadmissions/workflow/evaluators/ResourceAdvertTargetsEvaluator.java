package com.zuehlke.pgadmissions.workflow.evaluators;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.AdvertTargets;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;

@Component
public class ResourceAdvertTargetsEvaluator implements ResourceCompletenessEvaluator<ResourceParent<?>> {

    @Override
    public boolean evaluate(ResourceParent<?> resource) {
        AdvertTargets targets = resource.getAdvert().getTargets();
        return !(targets == null || CollectionUtils.isEmpty(targets.getInstitutions()) || CollectionUtils.isEmpty(targets.getDepartments()));
    }

}
