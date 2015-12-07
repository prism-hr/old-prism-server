package uk.co.alumeni.prism.workflow.evaluators;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.services.AdvertService;

@Component
public class ResourceDetailsEvaluator implements ResourceCompletenessEvaluator<ResourceParent> {

    @Inject
    private AdvertService advertService;

    @Override
    public boolean evaluate(ResourceParent resource) {
        if (resource.getClass().equals(Institution.class)) {
            return verifySummary(resource) && ((Institution) resource).getLogoImage() != null;
        }
        return verifySummary(resource);
    }

    private boolean verifySummary(ResourceParent resource) {
        Advert advert = resource.getAdvert();
        return !(advert.getSummary() == null || advert.getTelephone() == null || advertService.getBackgroundImage(advert) == null);
    }

}
