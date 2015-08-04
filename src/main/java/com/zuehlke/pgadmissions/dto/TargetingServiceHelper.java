package com.zuehlke.pgadmissions.dto;

import static com.zuehlke.pgadmissions.utils.PrismExecutorUtils.shutdownExecutor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.TargetingService;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.PrismServiceHelper;

@Component
public class TargetingServiceHelper implements PrismServiceHelper {

    private static Logger logger = LoggerFactory.getLogger(TargetingServiceHelper.class);

    private static int threadCount = 10;

    private static ForDTO<Integer> concentrationSweep = new ForDTO<Integer>(1, 1, 10);

    private static ForDTO<BigDecimal> proliferationSweep = new ForDTO<BigDecimal>(new BigDecimal(0.01), new BigDecimal(0.001), new BigDecimal(0.10));

    private static HashMultimap<Integer, Integer> topInstitutionsBySubjectArea = HashMultimap.create();

    static {
        // target top five institutions by subject area
    }

    private Map<Integer, TargetingDTO> optimumTargetingParameters = Maps.newHashMap();

    private ExecutorService executorService;

    private int activeExecutions = 0;

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private TargetingService targetingService;

    @Override
    public synchronized void execute() {
        if (activeExecutions == 0) {
            boolean unindexedPrograms = importedEntityService.getUnindexedProgramCount() > 0;
            if (unindexedPrograms || importedEntityService.getUnindexedInstitutionCount() > 0) {
                executorService = executorService == null ? Executors.newFixedThreadPool(threadCount) : executorService;
                if (unindexedPrograms) {
                    indexImportedPrograms(importedEntityService.getUnindexedImportedUcasPrograms());

                    if (importedEntityService.getUnindexedUcasProgramCount() == 0) {
                        importedEntityService.fillImportedUcasProgramCount();
                        indexImportedPrograms(importedEntityService.getUnindexedImportedNonUcasPrograms());
                    }
                } else {
                    List<ImportedInstitution> institutions = importedEntityService.getUnindexedImportedInstitutions();
                    indexImportedInstitutions(institutions);
                }
            }
        }
    }

    @Override
    public void shutdown() {
        shutdownExecutor(executorService);
    }

    private synchronized void indexImportedPrograms(List<ImportedProgram> programs) {
        int batchSize = (int) Math.ceil(programs.size() / (double) threadCount);
        if (!programs.isEmpty()) {
            for (List<ImportedProgram> batch : Lists.partition(programs, batchSize)) {
                activeExecutions++;
                executorService.submit(() -> {
                    new ProgramIndexer().index(batch);
                });
            }
        }
    }

    private void optimizeParameters() {
        for (int i = concentrationSweep.getMinimum(); i <= concentrationSweep.getMaximum(); i = (i + concentrationSweep.getInterval())) {
            for (BigDecimal j = proliferationSweep.getMinimum(); j.compareTo(proliferationSweep.getMaximum()) <= 0; j = j.add(proliferationSweep.getInterval())) {
                // Find and store the parameter sets that give the best results
                // by subject area
            }
        }
    }

    private synchronized void indexImportedInstitutions(List<ImportedInstitution> institutions) {
        int batchSize = (int) Math.ceil(institutions.size() / (double) threadCount);
        if (!institutions.isEmpty()) {
            for (List<ImportedInstitution> batch : Lists.partition(institutions, batchSize)) {
                activeExecutions++;
                executorService.submit(() -> {
                    new InsitutionIndexer().index(batch);
                });
            }
        }
    }

    private synchronized void decrementActiveExecutions() {
        activeExecutions--;
        if (activeExecutions == 0) {
            execute();
        }
    }

    private class ProgramIndexer extends Indexer<ImportedProgram> {

        @Override
        protected void indexEntities(List<ImportedProgram> batch) {
            for (ImportedProgram program : batch) {
                targetingService.indexImportedProgram(program);
            }
        }

    }

    private class InsitutionIndexer extends Indexer<ImportedInstitution> {

        @Override
        protected void indexEntities(List<ImportedInstitution> batch) {
            for (ImportedInstitution institution : batch) {
                targetingService.indexImportedInstitution(institution);
            }
        }

    }

    private abstract class Indexer<T extends ImportedEntity<?, ?>> {

        public void index(List<T> batch) {
            try {
                indexEntities(batch);
            } catch (Exception e) {
                logger.error("Failed to index " + getClass().getSimpleName().toLowerCase().replace("indexer", "") + "s: ", e);
            } finally {
                decrementActiveExecutions();
            }
        }

        protected abstract void indexEntities(List<T> batch);

    }

}
