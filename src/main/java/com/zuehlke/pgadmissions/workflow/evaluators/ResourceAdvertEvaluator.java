package com.zuehlke.pgadmissions.workflow.evaluators;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;

@Component
public class ResourceAdvertEvaluator implements ResourceCompletenessEvaluator<ResourceParent<?>> {

    @Override
    public boolean evaluate(ResourceParent<?> resource) {
        Advert advert = resource.getAdvert();
        return !(advert.getDescription() == null || advert.getCategories() == null);
    }

}
