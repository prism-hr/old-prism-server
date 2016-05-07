package uk.co.alumeni.prism.workflow.evaluators;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.services.AdvertService;

@Component
public class ResourceImagesEvaluator implements ResourceCompletenessEvaluator<ResourceParent> {

    @Inject
    private AdvertService advertService;

    @Override
    public boolean evaluate(ResourceParent resource) {
        boolean logoProvided = true;
        if (resource.getClass().equals(Institution.class)) {
            logoProvided = ((Institution) resource).getLogoImage() != null;
        }

        Advert advert = resource.getAdvert();
        boolean backgroundProvided = advertService.getBackgroundImage(advert) != null;
        return logoProvided && backgroundProvided;
    }

}
