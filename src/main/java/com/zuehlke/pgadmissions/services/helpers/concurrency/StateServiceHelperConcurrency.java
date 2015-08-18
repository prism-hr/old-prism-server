package com.zuehlke.pgadmissions.services.helpers.concurrency;

import static com.zuehlke.pgadmissions.utils.PrismThreadUtils.concludeThreads;
import static com.zuehlke.pgadmissions.utils.PrismThreadUtils.dispatchThread;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.resource.ResourceListRowDTO;
import com.zuehlke.pgadmissions.services.StateService;

@Component
public class StateServiceHelperConcurrency {

    @Inject
    private StateService stateService;

    public void appendSecondaryStates(final PrismScope resourceScope, List<ResourceListRowDTO> rows, int maxRecords) throws InterruptedException {
        List<Thread> workers = Lists.newArrayListWithCapacity(maxRecords);
        for (final ResourceListRowDTO row : rows) {
            Runnable runner = new Runnable() {
                @Override
                public void run() {
                    row.setSecondaryStateIds(stateService.getSecondaryResourceStates(resourceScope, row.getResourceId()));
                }
            };
            dispatchThread(workers, runner);
        }

        concludeThreads(workers);
    }

}
