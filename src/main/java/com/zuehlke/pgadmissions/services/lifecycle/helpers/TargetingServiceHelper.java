package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import static com.zuehlke.pgadmissions.services.lifecycle.helpers.TargetingServiceHelper.PrismTargetingIndexationState.INDEXING_INSTITUTIONS;
import static com.zuehlke.pgadmissions.services.lifecycle.helpers.TargetingServiceHelper.PrismTargetingIndexationState.INDEXING_NON_UCAS_PROGRAMS;
import static com.zuehlke.pgadmissions.services.lifecycle.helpers.TargetingServiceHelper.PrismTargetingIndexationState.INDEXING_UCAS_PROGRAMS;
import static com.zuehlke.pgadmissions.services.lifecycle.helpers.TargetingServiceHelper.PrismTargetingIndexationState.SCORING_INSTIUTTION_SUBJECT_AREAS;
import static com.zuehlke.pgadmissions.utils.PrismExecutorUtils.shutdownExecutor;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.dto.ForDTO;
import com.zuehlke.pgadmissions.dto.TargetingParameterDTO;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.TargetingService;

@Component
public class TargetingServiceHelper implements PrismServiceHelper {

    private static Logger logger = LoggerFactory.getLogger(TargetingServiceHelper.class);

    private static int threadCount = 10;

    private static ForDTO<Integer> concentration = new ForDTO<Integer>(1, 1, 10);

    private static ForDTO<BigDecimal> proliferation = new ForDTO<BigDecimal>(new BigDecimal(0.01), new BigDecimal(0.01), new BigDecimal(0.10));

    private int activeExecutions = 0;

    private PrismTargetingIndexationState algorithmState;

    private TargetingParameterDTO parameterSetToScore = null;

    private Set<TargetingParameterDTO> parameterSets = Sets.newLinkedHashSet();

    private Map<Integer, BigDecimal> topScoresBySubjectArea = Maps.newHashMap();

    private ExecutorService executorService;

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private TargetingService targetingService;

    @PostConstruct
    public void postConstruct() {
        for (int i = concentration.getMinimum(); i <= concentration.getMaximum(); i = (i + concentration.getInterval())) {
            for (BigDecimal j = proliferation.getMinimum(); j.compareTo(proliferation.getMaximum()) <= 0; j = j.add(proliferation.getInterval())) {
                parameterSets.add(new TargetingParameterDTO(i, j));
            }
        }
    }

    @Override
    public synchronized void execute() {
        if (activeExecutions == 0) {
            boolean unindexedPrograms = importedEntityService.getUnindexedProgramCount() > 0;
            if (unindexedPrograms || parameterSets.isEmpty()) {
                executorService = executorService == null ? Executors.newFixedThreadPool(threadCount) : executorService;
                if (unindexedPrograms) {
                    algorithmState = INDEXING_UCAS_PROGRAMS;
                    indexImportedPrograms(importedEntityService.getUnindexedImportedUcasPrograms());

                    if (importedEntityService.getUnindexedUcasProgramCount() == 0) {
                        algorithmState = INDEXING_NON_UCAS_PROGRAMS;
                        importedEntityService.fillImportedUcasProgramCount();
                        indexImportedPrograms(importedEntityService.getUnindexedImportedNonUcasPrograms());
                    }
                } else if (parameterSetToScore == null) {
                    Iterator<TargetingParameterDTO> iterator = parameterSets.iterator();
                    while (iterator.hasNext()) {
                        algorithmState = INDEXING_INSTITUTIONS;
                        TargetingParameterDTO parameterSet = iterator.next();
                        indexImportedInstitutions(importedEntityService.getImportedInstitutions(), parameterSet);
                        parameterSetToScore = parameterSet;
                        iterator.remove();
                    }
                } else {
                    algorithmState = SCORING_INSTIUTTION_SUBJECT_AREAS;
                    for (Integer subjectArea : importedEntityService.getRootImportedSubjectAreas()) {
                        scoreInstitutionSubjectAreas(subjectArea, parameterSetToScore);
                    }
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

    private synchronized void indexImportedInstitutions(List<ImportedInstitution> institutions, TargetingParameterDTO parameterSet) {
        int batchSize = (int) Math.ceil(institutions.size() / (double) threadCount);
        List<List<ImportedInstitution>> batches = Lists.partition(institutions, batchSize);
        if (!institutions.isEmpty()) {
            parameterSetToScore = parameterSet;
            final Integer concentrationFactor = parameterSet.getConcentration();
            final BigDecimal proliferationFactor = parameterSet.getProliferation();
            for (List<ImportedInstitution> batch : batches) {
                activeExecutions++;
                executorService.submit(() -> {
                    new InsitutionIndexer().index(batch, concentrationFactor, proliferationFactor);
                });
            }
        }
    }

    private void scoreInstitutionSubjectAreas(final Integer subjectArea, final TargetingParameterDTO parameterSet) {
        activeExecutions++;
        executorService.submit(() -> {
            try {
                targetingService.scoreImportedInstitutionSubjectAreas(subjectArea, parameterSet, topScoresBySubjectArea);
            } catch (Exception e) {
                logger.error("Failed to score subject area: " + subjectArea.toString() + " with parameters (" + parameterSet.toString() + ")", e);
            } finally {
                decrementActiveExecutions();
            }
        });
    }

    private synchronized void decrementActiveExecutions() {
        activeExecutions--;
        if (activeExecutions == 0) {
            if (algorithmState.equals(SCORING_INSTIUTTION_SUBJECT_AREAS)) {
                algorithmState = null;
                parameterSetToScore = null;
            }
            execute();
        }
    }

    private class ProgramIndexer extends Indexer<ImportedProgram> {

        @Override
        protected void indexEntities(List<ImportedProgram> batch, Object... arguments) {
            for (ImportedProgram program : batch) {
                targetingService.indexImportedProgram(program);
            }
        }

    }

    private class InsitutionIndexer extends Indexer<ImportedInstitution> {

        @Override
        protected void indexEntities(List<ImportedInstitution> batch, Object... arguments) {
            for (ImportedInstitution institution : batch) {
                targetingService.indexImportedInstitution(institution, (TargetingParameterDTO) arguments[0]);
            }
        }

    }

    private abstract class Indexer<T extends ImportedEntity<?, ?>> {

        public void index(List<T> batch, Object... arguments) {
            try {
                indexEntities(batch, arguments);
            } catch (Exception e) {
                logger.error("Failed to index " + getClass().getSimpleName().toLowerCase().replace("indexer", "") + "s: ", e);
            } finally {
                decrementActiveExecutions();
            }
        }

        protected abstract void indexEntities(List<T> batch, Object... arguments);

    }

    public static enum PrismTargetingIndexationState {

        INDEXING_UCAS_PROGRAMS,
        INDEXING_NON_UCAS_PROGRAMS,
        INDEXING_INSTITUTIONS,
        SCORING_INSTIUTTION_SUBJECT_AREAS;

    }

}
