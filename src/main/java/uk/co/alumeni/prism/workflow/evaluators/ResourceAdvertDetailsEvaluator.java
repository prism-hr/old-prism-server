package uk.co.alumeni.prism.workflow.evaluators;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.resource.ResourceParent;

@Component
public class ResourceAdvertDetailsEvaluator implements ResourceCompletenessEvaluator<ResourceParent> {

    @Override
    public boolean evaluate(ResourceParent resource) {
        return resource.getAdvert().getDescription() != null;
    }

}
