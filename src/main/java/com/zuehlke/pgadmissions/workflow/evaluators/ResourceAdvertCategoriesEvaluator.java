package com.zuehlke.pgadmissions.workflow.evaluators;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.AdvertCategories;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;

@Component
public class ResourceAdvertCategoriesEvaluator implements ResourceCompletenessEvaluator<ResourceParent> {

    @Override
    public boolean evaluate(ResourceParent resource) {
        AdvertCategories categories = resource.getAdvert().getCategories();
        return !(categories == null || (isEmpty(categories.getFunctions()) && isEmpty(categories.getIndustries())));
    }

}
