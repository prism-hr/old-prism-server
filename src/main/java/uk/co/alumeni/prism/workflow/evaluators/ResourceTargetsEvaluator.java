package uk.co.alumeni.prism.workflow.evaluators;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.services.AdvertService;

@Component
public class ResourceTargetsEvaluator implements ResourceCompletenessEvaluator<ResourceParent> {

    @Inject
    private AdvertService advertService;

    @Override
    public boolean evaluate(ResourceParent resource) {
        return isNotEmpty(advertService.getAdvertTargets(resource.getAdvert()));
    }

}
