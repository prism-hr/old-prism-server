package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.utils.PrismConstants.MAX_BATCH_INSERT_SIZE;
import static com.zuehlke.pgadmissions.utils.PrismStringUtils.cleanStringToLowerCase;
import static com.zuehlke.pgadmissions.utils.PrismStringUtils.tokenize;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.api.model.imported.request.ImportedEntityRequest;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitutionSubjectArea;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitutionSubjectAreaDTO;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgramSubjectArea;
import com.zuehlke.pgadmissions.domain.imported.ImportedSubjectArea;
import com.zuehlke.pgadmissions.dto.ImportedProgramSubjectAreaDTO;
import com.zuehlke.pgadmissions.dto.ImportedSubjectAreaDTO;
import com.zuehlke.pgadmissions.dto.ImportedSubjectAreaIndexDTO;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedProgramImportDTO;

@Service
@Transactional
public class TargetingService {

    private static final String IMPORTED_ENTITY_RELATION_UPDATE = "relation_strength = values(relation_strength), enabled = values(enabled)";

    @Inject
    private EntityService entityService;

    @Inject
    private ImportedEntityService importedEntityService;

    public void mergeImportedProgramSubjectAreas(List<ImportedProgramImportDTO> programDefinitions) {
        List<String> inserts = getImportedProgramSubjectAreaInserts(programDefinitions);
        if (!inserts.isEmpty()) {
            importedEntityService.disableImportedEntityRelations(ImportedProgramSubjectArea.class);
            entityService.flush();
            for (List<String> values : Lists.partition(inserts, MAX_BATCH_INSERT_SIZE)) {
                importedEntityService.executeBulkMerge("imported_program_subject_area",
                        "imported_program_id, imported_subject_area_id, relation_strength, enabled",
                        Joiner.on(", ").join(values), IMPORTED_ENTITY_RELATION_UPDATE);
            }
        }
    }

    public void mergeImportedInstitutionSubjectAreas() {
        importedEntityService.disableImportedEntityRelations(ImportedInstitutionSubjectArea.class);
        entityService.flush();
        List<List<ImportedInstitutionSubjectAreaDTO>> importedInstitutionSubjectAreaInsertDefinitions = Lists.partition(
                importedEntityService.getImportedInstitutionSubjectAreas(), MAX_BATCH_INSERT_SIZE);
        for (List<ImportedInstitutionSubjectAreaDTO> importedInstitutionSubjectAreaInserts : importedInstitutionSubjectAreaInsertDefinitions) {
            List<String> importedInstitutionSubjectAreaValues = Lists.newArrayListWithExpectedSize(importedInstitutionSubjectAreaInsertDefinitions.size());
            for (ImportedInstitutionSubjectAreaDTO importedInstitutionSubjectAreaInsert : importedInstitutionSubjectAreaInserts) {
                importedInstitutionSubjectAreaValues.add(getImportedInstitutionSubjectAreaRowDefinition(importedInstitutionSubjectAreaInsert));
            }
            importedEntityService.executeBulkMerge("imported_institution_subject_area",
                    "imported_institution_id, imported_subject_area_id, relation_strength, enabled",
                    Joiner.on(", ").join(importedInstitutionSubjectAreaValues), IMPORTED_ENTITY_RELATION_UPDATE);
        }
    }

    private <T extends ImportedEntityRequest> List<String> getImportedProgramSubjectAreaInserts(List<ImportedProgramImportDTO> programDefinitions) {
        HashMultimap<Integer, ImportedProgramSubjectAreaDTO> insertDefinitions = HashMultimap.create();
        HashMultimap<Integer, ImportedProgramSubjectAreaDTO> insertDefinitionsParent = HashMultimap.create();

        Map<String, Integer> programIndex = getImportedProgramsIndex();

        List<ImportedSubjectAreaDTO> subjectAreas = importedEntityService.getImportedSubjectAreas();
        ImportedSubjectAreaIndexDTO subjectAreaIndex = getImportedSubjectAreaIndex(subjectAreas);
        HashMultimap<Integer, ImportedSubjectAreaDTO> parentSubjectAreaIndex = getParentImportedSubjectAreas();

        int maxConfidence = 0;
        for (ImportedProgramImportDTO programDefinition : programDefinitions) {
            Integer program = programIndex.get(cleanStringToLowerCase(programDefinition.index()));
            Integer weight = programDefinition.getWeight();

            Set<String> jacsCodes = programDefinition.getJacsCodes();
            if (jacsCodes != null) {
                for (String jacsCode : jacsCodes) {
                    assignImportedSubjectArea(insertDefinitions, subjectAreaIndex, program, jacsCode, weight);
                    if (Character.isUpperCase(jacsCode.charAt(0)) && !jacsCode.endsWith("000")) {
                        for (int i = 3; i > 0; i--) {
                            String jacsCodeParent = StringUtils.rightPad(jacsCode.substring(0, i), 4, "0");
                            if (!jacsCodeParent.equals(jacsCode)) {
                                assignImportedSubjectArea(insertDefinitions, subjectAreaIndex, program, jacsCodeParent, weight);
                            }
                        }
                    }
                }
            }

            if (insertDefinitions.get(program).isEmpty()) {
                for (Integer ucasSubject : programDefinition.getUcasSubjects()) {
                    for (ImportedSubjectAreaDTO subjectArea : subjectAreaIndex.getByUcasSubject(ucasSubject)) {
                        insertDefinitions.put(program, new ImportedProgramSubjectAreaDTO(subjectArea.getId(), subjectArea.getJacsCode(), weight));
                    }
                }
            }

            if (insertDefinitions.get(program).isEmpty()) {
                String programName = cleanStringToLowerCase(programDefinition.getName());
                for (ImportedSubjectAreaDTO subjectArea : subjectAreas) {
                    Integer subjectAreaId = subjectArea.getId();
                    String subjectAreaName = subjectArea.getName();
                    String jacsCode = subjectArea.getJacsCode();

                    if (cleanStringToLowerCase(subjectAreaName).contains(programName)) {
                        insertDefinitions.put(program, new ImportedProgramSubjectAreaDTO(subjectAreaId, jacsCode, weight));
                    } else {
                        int confidence = 0;
                        for (Set<String> subjectAreaToken : tokenize(subjectAreaName)) {
                            for (String word : subjectAreaToken) {
                                if (programName.contains(word)) {
                                    confidence++;
                                    break;
                                }
                            }
                        }

                        if (confidence > 0) {
                            insertDefinitionsParent.put(program, new ImportedProgramSubjectAreaDTO(subjectAreaId, jacsCode, weight, confidence));
                            maxConfidence = confidence > maxConfidence ? confidence : maxConfidence;
                        }
                    }
                }
            }

            for (ImportedProgramSubjectAreaDTO subjectArea : insertDefinitions.get(program)) {
                for (ImportedSubjectAreaDTO parent : parentSubjectAreaIndex.get(subjectArea.getId())) {
                    insertDefinitionsParent.put(program, new ImportedProgramSubjectAreaDTO(parent.getId(), parent.getJacsCode(), weight));
                }
            }
        }

        insertDefinitions.putAll(insertDefinitionsParent);

        Integer maxWeight = 0;
        for (Integer program : insertDefinitions.keySet()) {
            Integer programWeight = insertDefinitions.get(program).size();
            maxWeight = maxWeight < programWeight ? programWeight : maxWeight;
        }

        List<String> inserts = Lists.newArrayListWithExpectedSize(insertDefinitions.size());
        for (Integer program : insertDefinitions.keySet()) {
            inserts.add(getImportedProgramSubjectAreaRowDefinitions(program, insertDefinitions.get(program), maxWeight, maxConfidence));
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

    private ImportedSubjectAreaIndexDTO getImportedSubjectAreaIndex(List<ImportedSubjectAreaDTO> subjectAreas) {
        ImportedSubjectAreaIndexDTO index = new ImportedSubjectAreaIndexDTO();
        for (ImportedSubjectAreaDTO subjectArea : subjectAreas) {

            for (String jacsCode : subjectArea.getJacsCode().split("\\|")) {
                index.addJacsCode(jacsCode, subjectArea);
            }

            String jacsCodesOld = subjectArea.getJacsCodeOld();
            if (jacsCodesOld != null) {
                for (String jacsCodeOld : jacsCodesOld.split("\\|")) {
                    index.addJacsCodeOld(jacsCodeOld, subjectArea);
                }
            }

            index.addUcasSubject(subjectArea.getUcasSubject(), subjectArea);
        }
        return index;
    }

    private ImportedSubjectAreaDTO assignImportedSubjectArea(HashMultimap<Integer, ImportedProgramSubjectAreaDTO> insertDefinitions,
            ImportedSubjectAreaIndexDTO subjectAreaIndex, Integer program, String jacsCode, Integer weight) {
        ImportedSubjectAreaDTO subjectArea = subjectAreaIndex.getByJacsCode(jacsCode);
        subjectArea = subjectArea == null ? subjectAreaIndex.getByJacsCodeOld(jacsCode) : subjectArea;
        if (subjectArea != null) {
            insertDefinitions.put(program, new ImportedProgramSubjectAreaDTO(subjectArea.getId(), jacsCode, weight));
        }
        return subjectArea;
    }

    private HashMultimap<Integer, ImportedSubjectAreaDTO> getParentImportedSubjectAreas() {
        HashMultimap<Integer, ImportedSubjectAreaDTO> index = HashMultimap.create();
        for (ImportedSubjectArea child : importedEntityService.getChildImportedSubjectAreas()) {
            ImportedSubjectArea parent = child.getParent();
            indexParentImportedSubjectArea(index, child.getId(), parent);
        }
        return index;
    }

    private void indexParentImportedSubjectArea(HashMultimap<Integer, ImportedSubjectAreaDTO> index, Integer child, ImportedSubjectArea parent) {
        index.put(child, new ImportedSubjectAreaDTO().withId(parent.getId()).withJacsCode(parent.getJacsCode()));
        ImportedSubjectArea grandParent = parent.getParent();
        if (grandParent != null) {
            indexParentImportedSubjectArea(index, child, grandParent);
        }
    }

    private String getImportedProgramSubjectAreaRowDefinitions(Integer program, Set<ImportedProgramSubjectAreaDTO> subjectAreas,
            Integer maxProgramSubjectAreaConnectionCount, Integer maxProgramSubjectAreaConnectionConfidence) {
        List<String> values = Lists.newArrayList();
        Integer weightModifier = (maxProgramSubjectAreaConnectionCount + 1 - subjectAreas.size());
        for (ImportedProgramSubjectAreaDTO subjectArea : subjectAreas) {
            Integer confidence = subjectArea.getConfidence();
            confidence = confidence == null ? maxProgramSubjectAreaConnectionConfidence : confidence;
            values.add("(" + program + ", " + subjectArea.getId().toString() + ", "
                    + new Integer(weightModifier * confidence * subjectArea.getSpecificity() * subjectArea.getWeight()).toString() + ", 1)");
        }
        return Joiner.on(", ").join(values);
    }

    private String getImportedInstitutionSubjectAreaRowDefinition(ImportedInstitutionSubjectAreaDTO importedInstitutionSubjectArea) {
        return "(" + importedInstitutionSubjectArea.getInstitution().toString() + ", "
                + importedInstitutionSubjectArea.getSubjectArea().toString() + ", " + importedInstitutionSubjectArea.getRelationStrength().toString()
                + ", " + "1)";
    }

}
