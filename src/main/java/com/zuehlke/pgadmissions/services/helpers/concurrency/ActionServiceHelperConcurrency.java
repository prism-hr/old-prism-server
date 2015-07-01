package com.zuehlke.pgadmissions.services.helpers.concurrency;

import static com.zuehlke.pgadmissions.utils.PrismThreadUtils.concludeThreads;
import static com.zuehlke.pgadmissions.utils.PrismThreadUtils.dispatchThread;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ActionDTO;
import com.zuehlke.pgadmissions.dto.ResourceListRowDTO;
import com.zuehlke.pgadmissions.services.ActionService;

@Component
public class ActionServiceHelperConcurrency {
    
    @Inject
    private ActionService actionService;

    public void appendActions(final PrismScope resourceScope, final User user, List<ResourceListRowDTO> rows,
            final HashMultimap<Integer, ActionDTO> creationActions, int maxRecords) throws InterruptedException {
        List<Thread> workers = Lists.newArrayListWithCapacity(maxRecords);
        for (final ResourceListRowDTO row : rows) {
            Runnable runner = new Runnable() {
                @Override
                public void run() {
                    Set<ActionDTO> actions = Sets.newLinkedHashSet();
                    actions.addAll(actionService.getPermittedActions(resourceScope, row, user));
                    actions.addAll(creationActions.get(row.getResourceId()));
                    row.setActions(actions);
                }
            };
            dispatchThread(workers, runner);
        }
        
        concludeThreads(workers);
    }
    
}
