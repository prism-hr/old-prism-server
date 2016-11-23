package uk.co.alumeni.prism.workflow.evaluators;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.services.AdvertService;

import javax.inject.Inject;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Component
public class ResourceTargetsEvaluator implements ResourceCompletenessEvaluator<ResourceParent> {

    @Inject
    private AdvertService advertService;

    @Override
    public boolean evaluate(ResourceParent resource) {
        return isNotEmpty(advertService.getAdvertTargets(resource.getAdvert()));
    }

}
