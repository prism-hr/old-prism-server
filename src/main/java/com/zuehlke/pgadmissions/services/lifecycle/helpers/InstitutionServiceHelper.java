package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component
public class InstitutionServiceHelper implements AbstractServiceHelper {

	@Inject
	private InstitutionService institutionService;

	@Inject
	private ImportedEntityService importedEntityService;

	@Override
	public void execute() throws Exception {
		List<Integer> institutionIds = institutionService.getInstitutionsToActivate();
		for (Integer institutionId : institutionIds) {
			List<Integer> pendingImports = importedEntityService.getPendingImportedEntityFeeds(institutionId);
			if (pendingImports.isEmpty()) {
				institutionService.initializeInstitution(institutionId);
			}
		}
	}

}
