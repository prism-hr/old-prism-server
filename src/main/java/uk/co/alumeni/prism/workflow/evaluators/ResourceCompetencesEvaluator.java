package uk.co.alumeni.prism.workflow.evaluators;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.resource.ResourceParent;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Component
public class ResourceCompetencesEvaluator implements ResourceCompletenessEvaluator<ResourceParent> {

    @Override
    public boolean evaluate(ResourceParent resource) {
        return isNotEmpty((resource.getAdvert().getCompetences()));
    }

}
