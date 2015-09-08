package com.zuehlke.pgadmissions.workflow.evaluators;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.AdvertCategories;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;

@Component
public class ResourceAdvertCategoriesEvaluator implements ResourceCompletenessEvaluator<ResourceParent> {

    @Override
    public boolean evaluate(ResourceParent resource) {
        AdvertCategories categories = resource.getAdvert().getCategories();
        if (categories == null) {
            return false;
        }
        return !categories.getFunctions().isEmpty() || !categories.getIndustries().isEmpty() || !categories.getThemes().isEmpty();
    }

}
