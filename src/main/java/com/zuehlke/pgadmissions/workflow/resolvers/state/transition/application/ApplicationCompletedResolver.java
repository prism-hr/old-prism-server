package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_IDENTIFICATION;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ApplicationCompletedResolver implements StateTransitionResolver<Application> {

    @Inject
    private AdvertService advertService;

    @Inject
    private StateService stateService;

    @Inject
    private ApplicationIdentifiedResolver applicationIdentifiedResolver;

    @Override
    public StateTransition resolve(Application resource, Comment comment) {
        Advert advert = resource.getAdvert();
        List<Integer> advertSelectedTargetAdverts = advertService.getAdvertSelectedTargetAdverts(advert);
        if (advertSelectedTargetAdverts.isEmpty()) {
            return applicationIdentifiedResolver.resolve(resource, comment);
        } else {
            List<Integer> advertsUserIdentifiedFor = advertService.getAdvertsUserIdentifiedFor(comment.getUser());
            if (advertsUserIdentifiedFor.isEmpty() || !CollectionUtils.containsAny(advertSelectedTargetAdverts, advertsUserIdentifiedFor)) {
                return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_IDENTIFICATION);
            }
            return applicationIdentifiedResolver.resolve(resource, comment);
        }
    }

}
