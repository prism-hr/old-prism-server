package uk.co.alumeni.prism.workflow.evaluators;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.resource.ResourceParent;

@Component
public class ResourceCompetencesEvaluator implements ResourceCompletenessEvaluator<ResourceParent> {

    @Override
    public boolean evaluate(ResourceParent resource) {
        return isNotEmpty((resource.getAdvert().getCompetences()));
    }

}
