package com.zuehlke.pgadmissions.workflow.evaluators;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.services.AdvertService;

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
