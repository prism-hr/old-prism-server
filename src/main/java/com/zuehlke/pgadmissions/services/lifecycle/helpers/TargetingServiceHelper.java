package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.TargetingService;

@Component
public class TargetingServiceHelper implements PrismServiceHelper {

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private TargetingService targetingService;

    @Override
    public void execute() throws Exception {
        indexImportedPrograms(importedEntityService.getUnindexedImportedUcasPrograms());

        importedEntityService.fillImportedUcasProgramCount();
        indexImportedPrograms(importedEntityService.getUnindexedImportedNonUcasPrograms());

        for (ImportedInstitution importedInstitution : importedEntityService.getUnindexedImportedInstitutions()) {
            targetingService.indexImportedInstitution(importedInstitution);
        }
    }

    private void indexImportedPrograms(List<ImportedProgram> importedPrograms) {
        for (ImportedProgram importedProgram : importedPrograms) {
            targetingService.indexImportedProgram(importedProgram);
        }
    }

}
