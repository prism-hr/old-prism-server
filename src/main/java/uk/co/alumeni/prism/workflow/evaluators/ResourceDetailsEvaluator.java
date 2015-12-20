package uk.co.alumeni.prism.workflow.evaluators;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.resource.ResourceOpportunity;
import uk.co.alumeni.prism.domain.resource.ResourceParent;

@Component
public class ResourceDetailsEvaluator implements ResourceCompletenessEvaluator<ResourceParent> {

    @Override
    public boolean evaluate(ResourceParent resource) {
        Advert advert = resource.getAdvert();
        boolean commonFieldsProvided = advert.getDescription() != null && advert.getSummary() != null && advert.getTelephone() != null;

        if (resource instanceof ResourceOpportunity) {
            ResourceOpportunity opportunity = (ResourceOpportunity) resource;
            boolean opportunityFieldsProvided = opportunity.getOpportunityType() != null && !opportunity.getResourceStudyOptions().isEmpty()
                    && advert.getPay() != null && advert.getPay().getOption() != null;
            return commonFieldsProvided && opportunityFieldsProvided;
        }
        return commonFieldsProvided;
    }

}
