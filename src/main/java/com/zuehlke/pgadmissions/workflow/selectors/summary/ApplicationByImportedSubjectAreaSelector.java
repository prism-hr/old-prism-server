package com.zuehlke.pgadmissions.workflow.selectors.summary;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.services.ApplicationService;

@Component
public class ApplicationByImportedSubjectAreaSelector implements PrismResourceSummarySelector {

    @Inject
    private ApplicationService applicationService;

    @Override
    public List<Integer> getPossible(PrismScope scope, Resource parent, Set<Integer> importedEntities) {
        return applicationService.getApplicationsByImportedProgram((ResourceParent) parent, importedEntities);
    }

}
