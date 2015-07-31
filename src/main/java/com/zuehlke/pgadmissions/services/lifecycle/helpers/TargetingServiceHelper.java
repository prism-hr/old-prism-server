package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.TargetingService;

@Component
public class TargetingServiceHelper implements PrismServiceHelper {

    private static final Logger logger = LoggerFactory.getLogger(TargetingServiceHelper.class);

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private TargetingService targetingService;

    @Override
    public void execute() throws Exception {
        if (importedEntityService.getUnindexedProgramCount() > 0) {
            indexImportedPrograms(importedEntityService.getUnindexedImportedUcasPrograms(), "Indexing UCAS programs");

            importedEntityService.fillImportedUcasProgramCount();
            indexImportedPrograms(importedEntityService.getUnindexedImportedNonUcasPrograms(), "Indexing non UCAS programs");

            List<ImportedInstitution> importedInstitutions = importedEntityService.getUnindexedImportedInstitutions();
            if (!importedInstitutions.isEmpty()) {
                logger.info("Indexing institutions");
                for (ImportedInstitution importedInstitution : importedInstitutions) {
                    targetingService.indexImportedInstitution(importedInstitution);
                }
                logger.info("Finished indexing institutions");
            }
        }
    }

    private void indexImportedPrograms(List<ImportedProgram> importedPrograms, String logMessage) {
        if (!importedPrograms.isEmpty()) {
            logger.info(logMessage);
            for (ImportedProgram importedProgram : importedPrograms) {
                targetingService.indexImportedProgram(importedProgram);
            }
            logger.info("Finished " + StringUtils.uncapitalize(logMessage));
        }
    }

}
