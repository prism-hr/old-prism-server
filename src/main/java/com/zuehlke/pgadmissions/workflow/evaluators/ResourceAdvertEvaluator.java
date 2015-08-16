package com.zuehlke.pgadmissions.workflow.evaluators;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.resource.ResourceParent;

@Component
public class ResourceAdvertEvaluator implements ResourceCompletenessEvaluator<ResourceParent<?>> {

    @Override
    public boolean evaluate(ResourceParent<?> resource) {
        return resource.getAdvert().getDescription() != null;
    }

}
