package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.utils.PrismConstants.MAX_BATCH_INSERT_SIZE;
import static com.zuehlke.pgadmissions.utils.PrismStringUtils.cleanStringToLowerCase;
import static com.zuehlke.pgadmissions.utils.PrismStringUtils.wrapInCharacterWildcard;
import static com.zuehlke.pgadmissions.utils.PrismStringUtils.wrapInWordBoundary;
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
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitutionSubjectArea;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitutionSubjectAreaDTO;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgramSubjectArea;
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
                            String jacsCodeParent = rightPad(jacsCode.substring(0, i), 4, "0");
                            if (!jacsCodeParent.equals(jacsCode)) {
                                assignImportedSubjectArea(insertDefinitions, subjectAreaIndex, program, jacsCodeParent, weight);
                            }
                        }
                    }
                }
            }

            if (insertDefinitions.get(program).isEmpty()) {
                String programName = cleanStringToLowerCase(programDefinition.getName());
                for (ImportedSubjectAreaDTO subjectArea : subjectAreas) {
                    Integer subjectAreaId = subjectArea.getId();
                    String subjectAreaName = subjectArea.getName();

                    if (cleanStringToLowerCase(programName).contains(subjectAreaName)) {
                        insertDefinitions.put(program, new ImportedProgramSubjectAreaDTO(subjectAreaId, weight));
                    } else {
                        int confidence = 0;
                        for (Set<String> subjectAreaToken : subjectArea.getTokens()) {
                            for (String word : subjectAreaToken) {
                                if (programName.matches(wrapInCharacterWildcard(wrapInWordBoundary(word)))) {
                                    confidence++;
                                    break;
                                }
                            }
                        }

                        if (confidence > 0) {
                            insertDefinitions.put(program, new ImportedProgramSubjectAreaDTO(subjectAreaId, weight, confidence));
                            maxConfidence = confidence > maxConfidence ? confidence : maxConfidence;
                        }
                    }
                }
            }

            if (insertDefinitions.get(program).isEmpty()) {
                for (Integer ucasSubject : programDefinition.getUcasSubjects()) {
                    for (ImportedSubjectAreaDTO subjectArea : subjectAreaIndex.getByUcasSubject(ucasSubject)) {
                        insertDefinitions.put(program, new ImportedProgramSubjectAreaDTO(subjectArea.getId(), weight));
                    }
                }
            }

            for (ImportedProgramSubjectAreaDTO programSubjectArea : insertDefinitions.get(program)) {
                ImportedSubjectAreaDTO subjectArea = subjectAreaIndex.getById(programSubjectArea.getId());
                assignImportedSubjectAreaParent(insertDefinitionsParent, subjectAreaIndex, program, subjectArea.getParent(), weight,
                        programSubjectArea.getConfidence());
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
            inserts.add(getImportedProgramSubjectAreaRowDefinitions(subjectAreaIndex, program, insertDefinitions.get(program), maxWeight, maxConfidence));
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
            subjectArea.prepare();
            index.addById(subjectArea.getId(), subjectArea);

            for (String jacsCode : subjectArea.getJacsCodes()) {
                index.addByJacsCode(jacsCode, subjectArea);
            }

            String[] jacsCodesOld = subjectArea.getJacsCodesOld();
            if (jacsCodesOld != null) {
                for (String jacsCodeOld : jacsCodesOld) {
                    index.addByJacsCodeOld(jacsCodeOld, subjectArea);
                }
            }

            index.addByUcasSubject(subjectArea.getUcasSubject(), subjectArea);
        }
        return index;
    }

    private ImportedSubjectAreaDTO assignImportedSubjectArea(HashMultimap<Integer, ImportedProgramSubjectAreaDTO> insertDefinitions,
            ImportedSubjectAreaIndexDTO subjectAreaIndex, Integer program, String programCode, Integer weight) {
        ImportedSubjectAreaDTO subjectArea = subjectAreaIndex.getByJacsCode(programCode);
        subjectArea = subjectArea == null ? subjectAreaIndex.getByJacsCodeOld(programCode) : subjectArea;
        if (subjectArea != null) {
            insertDefinitions.put(program, new ImportedProgramSubjectAreaDTO(subjectArea.getId(), weight));
        }
        return subjectArea;
    }

    private void assignImportedSubjectAreaParent(HashMultimap<Integer, ImportedProgramSubjectAreaDTO> insertDefinitions,
            ImportedSubjectAreaIndexDTO subjectAreaIndex, Integer program, Integer subjectAreaParentId, Integer weight, Integer confidence) {
        ImportedSubjectAreaDTO subjectAreaParent = subjectAreaIndex.getById(subjectAreaParentId);
        if (subjectAreaParent != null) {
            insertDefinitions.put(program, new ImportedProgramSubjectAreaDTO(subjectAreaParentId, weight, confidence));
            assignImportedSubjectAreaParent(insertDefinitions, subjectAreaIndex, program, subjectAreaParent.getParent(), weight, confidence);
        }
    }

    private String getImportedProgramSubjectAreaRowDefinitions(ImportedSubjectAreaIndexDTO subjectAreaIndex, Integer program,
            Set<ImportedProgramSubjectAreaDTO> programSubjectAreas, Integer maxWeight, Integer maxConfidence) {
        List<String> values = Lists.newArrayList();
        Integer weightModifier = (maxWeight + 1 - programSubjectAreas.size());
        for (ImportedProgramSubjectAreaDTO programSubjectArea : programSubjectAreas) {
            Integer subjectAreaId = programSubjectArea.getId();
            Integer confidence = programSubjectArea.getConfidence();
            confidence = confidence == null ? maxConfidence : confidence;
            ImportedSubjectAreaDTO subjectArea = subjectAreaIndex.getById(subjectAreaId);
            values.add("(" + Joiner.on(", ").join(program.toString(), subjectAreaId.toString(), //
                    new Integer(weightModifier * confidence * subjectArea.getSpecificity() * programSubjectArea.getWeight()).toString(), "1") + ")");
        }
        return Joiner.on(", ").join(values);
    }

    private String getImportedInstitutionSubjectAreaRowDefinition(ImportedInstitutionSubjectAreaDTO importedInstitutionSubjectArea) {
        return "(" + importedInstitutionSubjectArea.getInstitution().toString() + ", "
                + importedInstitutionSubjectArea.getSubjectArea().toString() + ", " + importedInstitutionSubjectArea.getRelationStrength().toString()
                + ", " + "1)";
    }

}
