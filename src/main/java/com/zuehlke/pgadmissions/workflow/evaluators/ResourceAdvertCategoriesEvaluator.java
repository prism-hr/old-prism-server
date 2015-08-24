package com.zuehlke.pgadmissions.workflow.evaluators;

import com.zuehlke.pgadmissions.domain.advert.AdvertCategories;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import org.springframework.stereotype.Component;

@Component
public class ResourceAdvertCategoriesEvaluator implements ResourceCompletenessEvaluator<ResourceParent<?>> {

    @Override
    public boolean evaluate(ResourceParent<?> resource) {
        AdvertCategories categories = resource.getAdvert().getCategories();
        return !categories.getFunctions().isEmpty() || !categories.getIndustries().isEmpty() || !categories.getThemes().isEmpty();
    }

}
