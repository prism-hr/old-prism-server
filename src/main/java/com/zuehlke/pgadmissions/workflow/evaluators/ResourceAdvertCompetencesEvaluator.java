package com.zuehlke.pgadmissions.workflow.evaluators;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.AdvertTargets;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;

@Component
public class ResourceAdvertCompetencesEvaluator implements ResourceCompletenessEvaluator<ResourceParent> {

    @Override
    public boolean evaluate(ResourceParent resource) {
        AdvertTargets targets = resource.getAdvert().getTargets();
        return !(targets == null || isEmpty(targets.getCompetences()));
    }

}
