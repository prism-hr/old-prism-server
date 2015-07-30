package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.utils.PrismConstants.MAX_BATCH_INSERT_SIZE;
import static com.zuehlke.pgadmissions.utils.PrismStringUtils.cleanStringToLowerCase;
import static com.zuehlke.pgadmissions.utils.PrismStringUtils.tokenize;
import static org.apache.commons.lang3.StringUtils.rightPad;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.api.model.imported.request.ImportedEntityRequest;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitutionSubjectAreaDTO;
import com.zuehlke.pgadmissions.dto.ImportedProgramSubjectAreaDTO;
import com.zuehlke.pgadmissions.dto.ImportedSubjectAreaDTO;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedProgramImportDTO;
import com.zuehlke.pgadmissions.services.indexers.ImportedSubjectAreaIndex;

@Service
@Transactional
public class TargetingService {

    private static final String IMPORTED_ENTITY_RELATION_UPDATE = "relation_strength = values(relation_strength)";

    @Inject
    private EntityService entityService;

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private ImportedSubjectAreaIndex importedSubjectAreaIndex;

    public void mergeImportedProgramSubjectAreas(List<ImportedProgramImportDTO> programDefinitions) {
        Map<Integer, String> inserts = getImportedProgramSubjectAreaInserts(programDefinitions);
        if (!inserts.isEmpty()) {
            int counter = 0;
            int insertCount = inserts.size();
            Map<Integer, String> insertBatch = Maps.newHashMapWithExpectedSize(MAX_BATCH_INSERT_SIZE);
            for (Map.Entry<Integer, String> insert : inserts.entrySet()) {
                insertBatch.put(insert.getKey(), insert.getValue());
                if (insertBatch.size() == MAX_BATCH_INSERT_SIZE || counter == (insertCount - 1)) {
                    importedEntityService.executeBulkMerge("imported_program_subject_area", "imported_program_id, imported_subject_area_id, relation_strength",
                            Joiner.on(", ").join(insertBatch.values()), IMPORTED_ENTITY_RELATION_UPDATE);
                    importedEntityService.setImportedProgramsIndexed(insertBatch.keySet());
                    entityService.flush();
                    insertBatch.clear();
                }
                counter++;
            }
        }
    }

    public void mergeImportedInstitutionSubjectAreas() {
        List<List<ImportedInstitutionSubjectAreaDTO>> importedInstitutionSubjectAreaInsertDefinitions = Lists.partition(
                importedEntityService.getImportedInstitutionSubjectAreas(), MAX_BATCH_INSERT_SIZE);
        for (List<ImportedInstitutionSubjectAreaDTO> importedInstitutionSubjectAreaInserts : importedInstitutionSubjectAreaInsertDefinitions) {
            List<String> importedInstitutionSubjectAreaValues = Lists.newArrayListWithExpectedSize(importedInstitutionSubjectAreaInsertDefinitions.size());
            for (ImportedInstitutionSubjectAreaDTO importedInstitutionSubjectAreaInsert : importedInstitutionSubjectAreaInserts) {
                importedInstitutionSubjectAreaValues.add(getImportedInstitutionSubjectAreaRowDefinition(importedInstitutionSubjectAreaInsert));
            }
            importedEntityService.executeBulkMerge("imported_institution_subject_area",
                    "imported_institution_id, imported_subject_area_id, relation_strength",
                    Joiner.on(", ").join(importedInstitutionSubjectAreaValues), IMPORTED_ENTITY_RELATION_UPDATE);
        }
    }

    private <T extends ImportedEntityRequest> Map<Integer, String> getImportedProgramSubjectAreaInserts(
            List<ImportedProgramImportDTO> programDefinitions) {
        HashMultimap<Integer, ImportedProgramSubjectAreaDTO> insertDefinitions = HashMultimap.create();
        HashMultimap<Integer, ImportedProgramSubjectAreaDTO> insertDefinitionsParent = HashMultimap.create();

        Map<String, Integer> programIndex = getImportedProgramsIndex();

        int maxConfidence = 0;
        for (ImportedProgramImportDTO programDefinition : programDefinitions) {
            Integer program = programIndex.get(cleanStringToLowerCase(programDefinition.index()));
            Integer weight = programDefinition.getWeight();

            Set<String> jacsCodes = programDefinition.getJacsCodes();
            if (jacsCodes != null) {
                for (String jacsCode : jacsCodes) {
                    assignImportedSubjectArea(insertDefinitions, program, jacsCode, weight);
                    if (Character.isUpperCase(jacsCode.charAt(0)) && !jacsCode.endsWith("000")) {
                        for (int i = 3; i > 0; i--) {
                            String jacsCodeParent = rightPad(jacsCode.substring(0, i), 4, "0");
                            if (!jacsCodeParent.equals(jacsCode)) {
                                assignImportedSubjectArea(insertDefinitions, program, jacsCodeParent, weight);
                            }
                        }
                    }
                }
            }

            Map<Integer, Integer> matches = Maps.newHashMap();
            Set<String> programNameTokens = tokenize(programDefinition.getName());
            for (String programNameToken : programNameTokens) {
                Set<ImportedSubjectAreaDTO> hits = importedSubjectAreaIndex.getByKeyword(programNameToken);
                for (ImportedSubjectAreaDTO hit : hits) {
                    Integer confidence = matches.get(hit);
                    matches.put(hit.getId(), confidence == null ? 1 : confidence + 1);
                }
            }

            for (Map.Entry<Integer, Integer> match : matches.entrySet()) {
                Integer confidence = match.getValue();
                insertDefinitions.put(program, new ImportedProgramSubjectAreaDTO(match.getKey(), weight, confidence));
                maxConfidence = confidence > maxConfidence ? confidence : maxConfidence;
            }

            if (insertDefinitions.get(program).isEmpty()) {
                for (Integer ucasSubject : programDefinition.getUcasSubjects()) {
                    for (ImportedSubjectAreaDTO subjectArea : importedSubjectAreaIndex.getByUcasSubject(ucasSubject)) {
                        insertDefinitions.put(program, new ImportedProgramSubjectAreaDTO(subjectArea.getId(), weight));
                    }
                }
            }

            for (ImportedProgramSubjectAreaDTO programSubjectArea : insertDefinitions.get(program)) {
                ImportedSubjectAreaDTO subjectArea = importedSubjectAreaIndex.getById(programSubjectArea.getId());
                assignImportedSubjectAreaParent(insertDefinitionsParent, program, subjectArea.getParent(), weight, programSubjectArea.getConfidence());
            }
        }

        insertDefinitions.putAll(insertDefinitionsParent);

        Integer maxWeight = 0;
        for (Integer program : insertDefinitions.keySet()) {
            Integer programWeight = insertDefinitions.get(program).size();
            maxWeight = maxWeight < programWeight ? programWeight : maxWeight;
        }

        Map<Integer, String> inserts = Maps.newHashMapWithExpectedSize(insertDefinitions.size());
        for (Integer program : insertDefinitions.keySet()) {
            inserts.put(program, getImportedProgramSubjectAreaRowDefinitions(program, insertDefinitions.get(program), maxWeight, maxConfidence));
        }

        return inserts;
    }

    private Map<String, Integer> getImportedProgramsIndex() {
        Map<String, Integer> index = Maps.newHashMap();
        List<com.zuehlke.pgadmissions.dto.ImportedProgramDTO> programs = importedEntityService.getImportedUcasPrograms();
        for (com.zuehlke.pgadmissions.dto.ImportedProgramDTO program : programs) {
            index.put(cleanStringToLowerCase(program.index()), program.getId());
        }
        return index;
    }

    private ImportedSubjectAreaDTO assignImportedSubjectArea(HashMultimap<Integer, ImportedProgramSubjectAreaDTO> insertDefinitions, Integer program,
            String programCode, Integer weight) {
        ImportedSubjectAreaDTO subjectArea = importedSubjectAreaIndex.getByJacsCode(programCode);
        subjectArea = subjectArea == null ? importedSubjectAreaIndex.getByJacsCodeOld(programCode) : subjectArea;
        if (subjectArea != null) {
            insertDefinitions.put(program, new ImportedProgramSubjectAreaDTO(subjectArea.getId(), weight));
        }
        return subjectArea;
    }

    private void assignImportedSubjectAreaParent(HashMultimap<Integer, ImportedProgramSubjectAreaDTO> insertDefinitions, Integer program,
            Integer subjectAreaParentId, Integer weight, Integer confidence) {
        ImportedSubjectAreaDTO subjectAreaParent = importedSubjectAreaIndex.getById(subjectAreaParentId);
        if (subjectAreaParent != null) {
            insertDefinitions.put(program, new ImportedProgramSubjectAreaDTO(subjectAreaParentId, weight, confidence));
            assignImportedSubjectAreaParent(insertDefinitions, program, subjectAreaParent.getParent(), weight, confidence);
        }
    }

    private String getImportedProgramSubjectAreaRowDefinitions(Integer program, Set<ImportedProgramSubjectAreaDTO> programSubjectAreas, Integer maxWeight,
            Integer maxConfidence) {
        List<String> values = Lists.newArrayList();
        Integer weightModifier = (maxWeight + 1 - programSubjectAreas.size());
        for (ImportedProgramSubjectAreaDTO programSubjectArea : programSubjectAreas) {
            Integer subjectAreaId = programSubjectArea.getId();
            Integer confidence = programSubjectArea.getConfidence();
            confidence = confidence == null ? maxConfidence : confidence;
            ImportedSubjectAreaDTO subjectArea = importedSubjectAreaIndex.getById(subjectAreaId);
            values.add("(" + Joiner.on(", ").join(program.toString(), subjectAreaId.toString(), //
                    new Integer(weightModifier * confidence * subjectArea.getSpecificity() * programSubjectArea.getWeight()).toString()) + ")");
        }
        return Joiner.on(", ").join(values);
    }

    private String getImportedInstitutionSubjectAreaRowDefinition(ImportedInstitutionSubjectAreaDTO importedInstitutionSubjectArea) {
        return "(" + Joiner.on(", ").join(importedInstitutionSubjectArea.getInstitution().toString(), //
                importedInstitutionSubjectArea.getSubjectArea().toString(), //
                importedInstitutionSubjectArea.getRelationStrength().toString()) + ")";
    }

}
