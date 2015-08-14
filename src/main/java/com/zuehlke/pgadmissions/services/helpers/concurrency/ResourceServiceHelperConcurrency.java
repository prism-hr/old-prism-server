package com.zuehlke.pgadmissions.services.helpers.concurrency;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.services.ResourceService;
import org.hibernate.criterion.Junction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.zuehlke.pgadmissions.utils.PrismThreadUtils.concludeThreads;
import static com.zuehlke.pgadmissions.utils.PrismThreadUtils.dispatchThread;

@Component
public class ResourceServiceHelperConcurrency {

    private static final Logger log = LoggerFactory.getLogger(ResourceServiceHelperConcurrency.class);

    @Inject
    private ResourceService resourceService;

    public Set<Integer> getAssignedResources(final User user, final PrismScope scopeId, final List<PrismScope> parentScopeIds,
            final ResourceListFilterDTO filter, final String lastSequenceIdentifier, final Integer recordsToRetrieve) {
        final Set<Integer> assigned = Sets.newHashSet();
        final Junction condition = resourceService.getFilterConditions(scopeId, filter);
        final List<Thread> workers = Lists.newArrayListWithCapacity(6);

        dispatchThread(workers, () -> assigned.addAll(resourceService.getAssignedResources(user, scopeId, filter, lastSequenceIdentifier, recordsToRetrieve, condition)));

        for (final PrismScope parentScopeId : parentScopeIds) {
            dispatchThread(workers, () -> assigned.addAll(resourceService.getAssignedResources(user, scopeId, filter, lastSequenceIdentifier, recordsToRetrieve, condition, parentScopeId)));
        }

        try {
            concludeThreads(workers);
            return assigned;
        } catch (InterruptedException e) {
            log.error("Interrupted getting assigned resources");
            return Collections.emptySet();
        }
    }

}
