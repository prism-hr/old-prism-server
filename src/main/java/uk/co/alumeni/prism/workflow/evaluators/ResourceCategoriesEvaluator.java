package uk.co.alumeni.prism.workflow.evaluators;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.advert.AdvertCategories;
import uk.co.alumeni.prism.domain.resource.ResourceParent;

@Component
public class ResourceCategoriesEvaluator implements ResourceCompletenessEvaluator<ResourceParent> {

    @Override
    public boolean evaluate(ResourceParent resource) {
        AdvertCategories categories = resource.getAdvert().getCategories();
        return !(categories == null || (isEmpty(categories.getFunctions()) && isEmpty(categories.getIndustries())));
    }

}
