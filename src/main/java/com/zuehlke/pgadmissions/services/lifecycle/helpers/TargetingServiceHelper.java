package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import static com.google.common.collect.Sets.newHashSet;
import static com.zuehlke.pgadmissions.services.lifecycle.helpers.TargetingServiceHelper.PrismTargetingIndexationState.INDEXING_INSTITUTIONS;
import static com.zuehlke.pgadmissions.services.lifecycle.helpers.TargetingServiceHelper.PrismTargetingIndexationState.INDEXING_NON_UCAS_PROGRAMS;
import static com.zuehlke.pgadmissions.services.lifecycle.helpers.TargetingServiceHelper.PrismTargetingIndexationState.INDEXING_UCAS_PROGRAMS;
import static com.zuehlke.pgadmissions.services.lifecycle.helpers.TargetingServiceHelper.PrismTargetingIndexationState.SCORING_INSTIUTTION_SUBJECT_AREAS;
import static com.zuehlke.pgadmissions.utils.PrismExecutorUtils.shutdownExecutor;
import static com.zuehlke.pgadmissions.utils.PrismTargetingUtils.PRECISION;
import static java.math.RoundingMode.HALF_UP;

import java.math.BigDecimal;
import java.util.Collection;
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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.annotations.TargetingCalibrationSource;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.dto.ForDTO;
import com.zuehlke.pgadmissions.dto.ImportedInstitutionSubjectAreaDTO;
import com.zuehlke.pgadmissions.dto.TargetingParameterDTO;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.TargetingService;

@Component
public class TargetingServiceHelper implements PrismServiceHelper {

    private static Logger logger = LoggerFactory.getLogger(TargetingServiceHelper.class);

    private static int threadCount = 10;

    private static ForDTO<Integer> concentration = new ForDTO<Integer>(1, 1, 10);

    private static ForDTO<BigDecimal> proliferation = new ForDTO<BigDecimal>(new BigDecimal(0.01), new BigDecimal(0.01), new BigDecimal(0.10));

    // Data from: http://www.thecompleteuniversityguide.co.uk/league-tables/
    @TargetingCalibrationSource(subjectArea = 1, sources = { "Medicine", "Dentistry", "Nursing", "Anatomy & Physiology", "Aural & Oral Sciences",
            "Pharmacology & Pharmacy", "Complementary Medicine", "Opthalmics", "Medical Technology", "Physiotherapy" })
    @TargetingCalibrationSource(subjectArea = 2, sources = { "Biological Sciences", "Psychology", "Sports Science" })
    @TargetingCalibrationSource(subjectArea = 3, sources = { "Veterinary Medicine", "Agriculture & Forestry", "Food Science" })
    @TargetingCalibrationSource(subjectArea = 4, sources = { "Chemistry", "Materials Technology", "Physics & Astronomy", "Archaeology", "Geology",
            "Geography & Environmental Science" })
    @TargetingCalibrationSource(subjectArea = 5, sources = { "Mathematics" })
    @TargetingCalibrationSource(subjectArea = 6, sources = { "General Engineering", "Civil Engineering", "Mechanical Engineering",
            "Aeronautical & Manufacturing Engineering", "Chemical Engineering" })
    @TargetingCalibrationSource(subjectArea = 7, sources = { "Computer Science" })
    @TargetingCalibrationSource(subjectArea = 8, sources = { "Materials Technology", "Medical Technology" })
    @TargetingCalibrationSource(subjectArea = 9, sources = { "Architecture", "Building", "Town & Country Planning and Landscape Design" })
    @TargetingCalibrationSource(subjectArea = 10, sources = { "Economics", "Politics", "Sociology", "Social Policy", "Anthropology",
            "Geography & Environmental Science", "Social Work" })
    @TargetingCalibrationSource(subjectArea = 11, sources = { "Law" })
    @TargetingCalibrationSource(subjectArea = 12, sources = { "Accounting & Finance", "Business & Management Studies",
            "Hospitality, Leisure, Recreation & Tourism", "Land & Property Management", "Marketing" })
    @TargetingCalibrationSource(subjectArea = 13, sources = { "Communication & Media Studies", "Librarianship & Information Management" })
    @TargetingCalibrationSource(subjectArea = 14, sources = { "Celtic Studies", "Classics & Ancient History", "Linguistics", "English" })
    @TargetingCalibrationSource(subjectArea = 15, sources = { "French", "German", "Iberian Languages", "Italian", "Russian & East European Languages" })
    @TargetingCalibrationSource(subjectArea = 16, sources = { "American Studies", "East & South Asian Studies", "Middle Eastern & African Studies" })
    @TargetingCalibrationSource(subjectArea = 17, sources = { "History", "History of Art, Architecture & Design", "Philosophy", "Theology & Religious Studies" })
    @TargetingCalibrationSource(subjectArea = 18, sources = { "Art & Design", "Drama, Dance & Cinematics", "Philosophy", "Music" })
    @TargetingCalibrationSource(subjectArea = 19, sources = { "Education" })
    private static Multimap<Integer, Integer> topInstitutionsBySubjectArea = ArrayListMultimap.create();

    static {
        topInstitutionsBySubjectArea.putAll(1, newHashSet(719, 2148, 6466, 6610, 6938));
        topInstitutionsBySubjectArea.putAll(1, newHashSet(719, 4083, 6721, 6908, 225219));
        topInstitutionsBySubjectArea.putAll(1, newHashSet(5106, 6673, 6721, 6823, 7068));
        topInstitutionsBySubjectArea.putAll(1, newHashSet(6466, 6610, 6668, 6673, 6938));
        topInstitutionsBySubjectArea.putAll(1, newHashSet(4812, 6815, 6908, 6986, 7043));
        topInstitutionsBySubjectArea.putAll(1, newHashSet(719, 4089, 5133, 6560, 6610));
        topInstitutionsBySubjectArea.putAll(1, newHashSet(600, 1702, 4812, 6725, 6743));
        topInstitutionsBySubjectArea.putAll(1, newHashSet(325, 719, 1702, 5254, 225219));
        topInstitutionsBySubjectArea.putAll(1, newHashSet(5094, 5133, 6701, 6815, 225175));
        topInstitutionsBySubjectArea.putAll(1, newHashSet(719, 5094, 5106, 6673, 6927));

        topInstitutionsBySubjectArea.putAll(2, newHashSet(1187, 2148, 6610, 6938, 225268));
        topInstitutionsBySubjectArea.putAll(2, newHashSet(6466, 6560, 6610, 6721, 6938));
        topInstitutionsBySubjectArea.putAll(2, newHashSet(1187, 3045, 5106, 6560, 6701));

        topInstitutionsBySubjectArea.putAll(3, newHashSet(6610, 6721, 6823, 6927, 6938));
        topInstitutionsBySubjectArea.putAll(3, newHashSet(4089, 4227, 6908, 6927, 6986));
        topInstitutionsBySubjectArea.putAll(3, newHashSet(1009, 2741, 6815, 6927, 7068));

        topInstitutionsBySubjectArea.putAll(4, newHashSet(1187, 2148, 6610, 6938, 7176));
        topInstitutionsBySubjectArea.putAll(4, newHashSet(2148, 4812, 6610, 6938, 7068));
        topInstitutionsBySubjectArea.putAll(4, newHashSet(1187, 2148, 6560, 6610, 6938));
        topInstitutionsBySubjectArea.putAll(4, newHashSet(1187, 6610, 6701, 6938, 225268));
        topInstitutionsBySubjectArea.putAll(4, newHashSet(1187, 2148, 6610, 6938, 7053));
        topInstitutionsBySubjectArea.putAll(4, newHashSet(1187, 3033, 6585, 6610, 6938));

        topInstitutionsBySubjectArea.putAll(5, newHashSet(2148, 6610, 6938, 7053, 7176));

        topInstitutionsBySubjectArea.putAll(6, newHashSet(1187, 2148, 6610, 6668, 6938));
        topInstitutionsBySubjectArea.putAll(6, newHashSet(2148, 6560, 6585, 6610, 7043));
        topInstitutionsBySubjectArea.putAll(6, newHashSet(2148, 6560, 6585, 6610, 7043));
        topInstitutionsBySubjectArea.putAll(6, newHashSet(2148, 6560, 6585, 6610, 7043));
        topInstitutionsBySubjectArea.putAll(6, newHashSet(2148, 5133, 6610, 6721, 7043));
        topInstitutionsBySubjectArea.putAll(6, newHashSet(1951, 2148, 5112, 6560, 6610));

        topInstitutionsBySubjectArea.putAll(7, newHashSet(2148, 6585, 6610, 6938, 7053));

        topInstitutionsBySubjectArea.putAll(8, newHashSet(2148, 4812, 6610, 6938, 7068));
        topInstitutionsBySubjectArea.putAll(8, newHashSet(5094, 5133, 6701, 6815, 225175));

        topInstitutionsBySubjectArea.putAll(9, newHashSet(719, 5112, 6560, 6610, 7025));
        topInstitutionsBySubjectArea.putAll(9, newHashSet(3045, 6466, 6823, 6986, 225219));
        topInstitutionsBySubjectArea.putAll(9, newHashSet(719, 1951, 6466, 6610, 6908));

        topInstitutionsBySubjectArea.putAll(10, newHashSet(3033, 6466, 6610, 6938, 7176));
        topInstitutionsBySubjectArea.putAll(10, newHashSet(3033, 6466, 6610, 6938, 7053));
        topInstitutionsBySubjectArea.putAll(10, newHashSet(1187, 5106, 6560, 6610, 7068));
        topInstitutionsBySubjectArea.putAll(10, newHashSet(3033, 5112, 6585, 6815, 6927));
        topInstitutionsBySubjectArea.putAll(10, newHashSet(3033, 6466, 6610, 6938, 7053));
        topInstitutionsBySubjectArea.putAll(10, newHashSet(1187, 3033, 6585, 6610, 6938));
        topInstitutionsBySubjectArea.putAll(10, newHashSet(2927, 5106, 6560, 6721, 7060));

        topInstitutionsBySubjectArea.putAll(11, newHashSet(1187, 3033, 6466, 6610, 6938));

        topInstitutionsBySubjectArea.putAll(12, newHashSet(2927, 5133, 6560, 6721, 7176));
        topInstitutionsBySubjectArea.putAll(12, newHashSet(3033, 3045, 6560, 7053, 7176));
        topInstitutionsBySubjectArea.putAll(12, newHashSet(3045, 3854, 5094, 5106, 7068));
        topInstitutionsBySubjectArea.putAll(12, newHashSet(3854, 4461, 5254, 6721, 6986));
        topInstitutionsBySubjectArea.putAll(12, newHashSet(2927, 5133, 6560, 6815, 6908));

        topInstitutionsBySubjectArea.putAll(13, newHashSet(2927, 3045, 5133, 6815, 7025));
        topInstitutionsBySubjectArea.putAll(13, newHashSet(600, 1075, 3045, 4461, 220180));

        topInstitutionsBySubjectArea.putAll(14, newHashSet(719, 6610, 6721, 6938, 225175, 225250, 225256));
        topInstitutionsBySubjectArea.putAll(14, newHashSet(1187, 6610, 6701, 6938, 7053));
        topInstitutionsBySubjectArea.putAll(14, newHashSet(2927, 5112, 6466, 6610, 6938));
        topInstitutionsBySubjectArea.putAll(14, newHashSet(1187, 6466, 6610, 6938, 7053));

        topInstitutionsBySubjectArea.putAll(15, newHashSet(1187, 6610, 6701, 6938, 225268));
        topInstitutionsBySubjectArea.putAll(15, newHashSet(1187, 6610, 6908, 6938, 7176));
        topInstitutionsBySubjectArea.putAll(15, newHashSet(1187, 6610, 6908, 6938, 7053));
        topInstitutionsBySubjectArea.putAll(15, newHashSet(1187, 6585, 6610, 6701, 225175));
        topInstitutionsBySubjectArea.putAll(15, newHashSet(1187, 6585, 6610, 6701, 6938));

        topInstitutionsBySubjectArea.putAll(16, newHashSet(5106, 6673, 7070, 7176, 225219));
        topInstitutionsBySubjectArea.putAll(16, newHashSet(5112, 6610, 6744, 6908, 6927, 6938));
        topInstitutionsBySubjectArea.putAll(16, newHashSet(1187, 5106, 5112, 6610, 7053));

        topInstitutionsBySubjectArea.putAll(17, newHashSet(1187, 6610, 6938, 7053, 7176));
        topInstitutionsBySubjectArea.putAll(17, newHashSet(6466, 6610, 6701, 6938, 225268));
        topInstitutionsBySubjectArea.putAll(17, newHashSet(2741, 3033, 6610, 6701, 6938));
        topInstitutionsBySubjectArea.putAll(17, newHashSet(1187, 6610, 6701, 6938, 7053));

        topInstitutionsBySubjectArea.putAll(18, newHashSet(2927, 3045, 5112, 6721, 6938));
        topInstitutionsBySubjectArea.putAll(18, newHashSet(5106, 6585, 6701, 7068, 7176));
        topInstitutionsBySubjectArea.putAll(18, newHashSet(1187, 5106, 6610, 6938, 225219));

        topInstitutionsBySubjectArea.putAll(19, newHashSet(1187, 6610, 6668, 6721, 7043));
    }

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
    public void queueParameterSets() {
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
                    for (Integer subjectArea : topInstitutionsBySubjectArea.keySet()) {
                        activeExecutions++;
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
        executorService.submit(() -> {
            try {
                Integer concentrationFactor = parameterSet.getConcentration();
                BigDecimal proliferationFactor = parameterSet.getProliferation();

                Integer[] subjectAreaFamily = (Integer[]) importedEntityService.getImportedSubjectAreaFamily(subjectArea).toArray();
                BigDecimal minimumRelationStrength = importedEntityService.getMinimumImportedInstitutionSubjectAreaRelationStrength(
                        topInstitutionsBySubjectArea.get(subjectArea), concentrationFactor, proliferationFactor, subjectAreaFamily);

                Map<Integer, Integer> institutionImportance = Maps.newHashMap();
                Collection<Integer> institutions = topInstitutionsBySubjectArea.get(subjectArea);
                for (Integer institutution : institutions) {
                    Integer oldImportance = institutionImportance.get(institutution);
                    Integer newImportance = oldImportance == null ? 1 : oldImportance + 1;
                    institutionImportance.put(institutution, newImportance);
                }

                List<ImportedInstitutionSubjectAreaDTO> relations = importedEntityService.getImportedInstitutionSubjectAreas(concentrationFactor,
                        proliferationFactor, minimumRelationStrength, subjectAreaFamily);

                int counter = 1;
                BigDecimal score = new BigDecimal(0);
                for (ImportedInstitutionSubjectAreaDTO relation : relations) {
                    Integer importance = institutionImportance.get(relation.getId());
                    if (importance != null) {
                        score = score.add(new BigDecimal(counter).divide(new BigDecimal(importance), PRECISION, HALF_UP));
                    }
                    counter++;
                }

                BigDecimal topScore = topScoresBySubjectArea.get(subjectArea);
                if (topScore == null || score.compareTo(topScore) > 0) {
                    topScoresBySubjectArea.put(subjectArea, topScore);
                    importedEntityService.enableImportedInstitutionSubjectAreas(concentrationFactor, proliferationFactor, subjectAreaFamily);
                } else {
                    importedEntityService.deleteImportedInstitutionSubjectAreas(concentrationFactor, proliferationFactor, subjectAreaFamily);
                }
            } catch (Exception e) {
                logger.error("Failed to index " + getClass().getSimpleName().toLowerCase().replace("indexer", "") + "s: ", e);
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
            Integer concentrationFactor = (Integer) arguments[0];
            BigDecimal proliferationFactor = ((BigDecimal) arguments[1]).setScale(PRECISION, HALF_UP);
            for (ImportedInstitution institution : batch) {
                targetingService.indexImportedInstitution(institution, concentrationFactor, proliferationFactor);
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
