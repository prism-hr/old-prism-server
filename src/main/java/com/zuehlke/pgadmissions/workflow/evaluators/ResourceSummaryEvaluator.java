package com.zuehlke.pgadmissions.workflow.evaluators;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.services.AdvertService;

@Component
public class ResourceSummaryEvaluator implements ResourceCompletenessEvaluator<ResourceParent<?>> {

    @Inject
    private AdvertService advertService;

    @Override
    public boolean evaluate(ResourceParent<?> resource) {
        if (resource.getClass().equals(Institution.class)) {
            return verifyBackgroundImage(resource) && ((Institution) resource).getLogoImage() != null;
        }
        return verifyBackgroundImage(resource);
    }

    private boolean verifyBackgroundImage(ResourceParent<?> resource) {
        return advertService.getBackgroundImage(resource.getAdvert()) != null;
    }

}
