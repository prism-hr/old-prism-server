package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;

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
