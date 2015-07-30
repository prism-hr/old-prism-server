package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.utils.PrismStringUtils.tokenize;
import static java.math.RoundingMode.HALF_UP;
import static org.apache.commons.lang3.StringUtils.rightPad;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.api.model.imported.request.ImportedEntityRequest;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitutionSubjectAreaDTO;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.imported.ImportedSubjectArea;
import com.zuehlke.pgadmissions.dto.ImportedProgramSubjectAreaDTO;
import com.zuehlke.pgadmissions.services.indices.ImportedSubjectAreaIndex;

@Service
@Transactional
public class TargetingService {

    private static final Logger logger = LoggerFactory.getLogger(TargetingService.class);

    private static final Integer TARGETING_PRECISION = 9;

    private static final BigDecimal TARGETING_KEYWORD_THRESHOLD = new BigDecimal(0.5);

    private static final BigDecimal TARGETING_SUBJECT_THRESHOLD = new BigDecimal(0.1);

    private static final String IMPORTED_ENTITY_RELATION_UPDATE = "relation_strength = values(relation_strength)";

    @Inject
    private EntityService entityService;

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private ImportedSubjectAreaIndex importedSubjectAreaIndex;

    public void indexImportedProgram(ImportedProgram importedProgram) {
        Integer importedProgramId = importedProgram.getId();
        String inserts = getImportedProgramSubjectAreaInserts(importedProgram);
        if (inserts != null) {
            logger.info("Indexing imported program: " + importedProgramId.toString() + "-" + importedProgram.getInstitution().getName() + "-"
                    + importedProgram.getName());
            importedEntityService.executeBulkMerge("imported_program_subject_area", "imported_program_id, imported_subject_area_id, relation_strength",
                    inserts, IMPORTED_ENTITY_RELATION_UPDATE);
        }
        importedEntityService.setImportedInstitutionIndexed(importedProgram.getInstitution().getId(), false);
        importedEntityService.setImportedProgramIndexed(importedProgramId, true);
        entityService.flush();
    }

    public void indexImportedInstitution(ImportedInstitution importedInstitution) {
        Integer importedInstitutionId = importedInstitution.getId();
        List<ImportedInstitutionSubjectAreaDTO> definitions = importedEntityService.getImportedInstitutionSubjectAreas(importedInstitution);
        List<String> inserts = Lists.newArrayListWithExpectedSize(definitions.size());
        if (!definitions.isEmpty()) {
            logger.info("Indexing imported institution: " + importedInstitutionId.toString() + "-" + importedInstitution.getName());
            for (ImportedInstitutionSubjectAreaDTO definition : definitions) {
                inserts.add(getImportedInstitutionSubjectAreaRowDefinition(importedInstitution, definition));
            }
            importedEntityService.executeBulkMerge("imported_institution_subject_area", "imported_institution_id, imported_subject_area_id, relation_strength",
                    Joiner.on(", ").join(inserts), IMPORTED_ENTITY_RELATION_UPDATE);
        }
        importedEntityService.setImportedInstitutionIndexed(importedInstitutionId, true);
        entityService.flush();
    }

    private <T extends ImportedEntityRequest> String getImportedProgramSubjectAreaInserts(ImportedProgram program) {
        Set<ImportedProgramSubjectAreaDTO> insertDefinitions = Sets.newHashSet();
        Set<ImportedProgramSubjectAreaDTO> insertDefinitionsParent = Sets.newHashSet();

        BigDecimal weight = new BigDecimal(1).divide(new BigDecimal(program.getUcasProgramCount()), TARGETING_PRECISION, HALF_UP);

        String jacsCodes = program.getJacsCodes();
        if (jacsCodes != null) {
            for (String jacsCode : jacsCodes.split("\\|")) {
                BigDecimal confidence = new BigDecimal(1);
                if (!assignImportedSubjectArea(insertDefinitions, jacsCode, weight, confidence)) {
                    if (Character.isUpperCase(jacsCode.charAt(0)) && !jacsCode.endsWith("000")) {
                        int confidenceDivisor = 2;
                        for (int i = 3; i > 0; i--) {
                            String jacsCodeParent = rightPad(jacsCode.substring(0, i), 4, "0");
                            if (!jacsCodeParent.equals(jacsCode)) {
                                assignImportedSubjectArea(insertDefinitions, jacsCodeParent, weight,
                                        confidence.divide(new BigDecimal(confidenceDivisor), TARGETING_PRECISION, HALF_UP));
                            }
                            confidenceDivisor++;
                        }
                    }
                }
            }
        }

        Map<ImportedSubjectArea, Integer> keywordMatches = Maps.newHashMap();
        Set<String> programNameTokens = tokenize(program.getName()).getTokens();
        for (String programNameToken : programNameTokens) {
            Set<ImportedSubjectArea> hits = importedSubjectAreaIndex.getByKeyword(programNameToken);
            for (ImportedSubjectArea hit : hits) {
                Integer hitCount = keywordMatches.get(hit);
                keywordMatches.put(hit, hitCount == null ? 1 : hitCount++);
            }
        }

        for (Map.Entry<ImportedSubjectArea, Integer> keywordMatch : keywordMatches.entrySet()) {
            ImportedSubjectArea matchedSubjectArea = keywordMatch.getKey();
            BigDecimal confidence = new BigDecimal(keywordMatch.getValue()).divide(
                    new BigDecimal(importedSubjectAreaIndex.getUniqueTokenCount(matchedSubjectArea)), TARGETING_PRECISION, HALF_UP);
            if (confidence.compareTo(TARGETING_KEYWORD_THRESHOLD) >= 0) {
                insertDefinitions.add(new ImportedProgramSubjectAreaDTO(matchedSubjectArea.getId(), weight, confidence));
            }
        }

        String ucasSubjects = program.getUcasSubjects();
        if (ucasSubjects != null) {
            String[] ucasSubjectsSplit = ucasSubjects.split("\\|");
            BigDecimal confidence = new BigDecimal(1).divide(new BigDecimal(ucasSubjectsSplit.length), TARGETING_PRECISION, HALF_UP);
            if (confidence.compareTo(TARGETING_SUBJECT_THRESHOLD) >= 0) {
                for (String ucasSubject : ucasSubjects.split("\\|")) {
                    for (ImportedSubjectArea subjectArea : importedSubjectAreaIndex.getByUcasSubject(Integer.parseInt(ucasSubject))) {
                        insertDefinitions.add(new ImportedProgramSubjectAreaDTO(subjectArea.getId(), weight, confidence));
                    }
                }
            }
        }

        for (ImportedProgramSubjectAreaDTO programSubjectArea : insertDefinitions) {
            ImportedSubjectArea subjectArea = importedSubjectAreaIndex.getById(programSubjectArea.getId());
            for (ImportedSubjectArea subjectAreaParent : importedSubjectAreaIndex.getParents(subjectArea)) {
                insertDefinitionsParent.add(new ImportedProgramSubjectAreaDTO(subjectAreaParent.getId(), weight, programSubjectArea.getConfidence()));
            }
        }

        insertDefinitions.addAll(insertDefinitionsParent);
        return insertDefinitions.isEmpty() ? null : getImportedProgramSubjectAreaRowDefinitions(program, insertDefinitions);
    }

    private boolean assignImportedSubjectArea(Set<ImportedProgramSubjectAreaDTO> insertDefinitions, String programCode, BigDecimal weight,
            BigDecimal confidence) {
        ImportedSubjectArea subjectArea = importedSubjectAreaIndex.getByJacsCode(programCode);
        subjectArea = subjectArea == null ? importedSubjectAreaIndex.getByJacsCodeOld(programCode) : subjectArea;
        if (subjectArea != null) {
            insertDefinitions.add(new ImportedProgramSubjectAreaDTO(subjectArea.getId(), weight, confidence));
            return true;
        }
        return false;
    }

    private String getImportedProgramSubjectAreaRowDefinitions(ImportedProgram program, Set<ImportedProgramSubjectAreaDTO> relations) {
        List<String> values = Lists.newArrayList();
        BigDecimal fidelity = new BigDecimal(1).divide(new BigDecimal(relations.size()), TARGETING_PRECISION, HALF_UP);
        for (ImportedProgramSubjectAreaDTO programSubjectArea : relations) {
            Integer subjectAreaId = programSubjectArea.getId();
            ImportedSubjectArea subjectArea = importedSubjectAreaIndex.getById(subjectAreaId);
            values.add("(" + Joiner.on(", ").join(program.getId().toString(), subjectAreaId.toString(), //
                    fidelity.multiply(programSubjectArea.getWeight()).multiply(programSubjectArea.getConfidence()) //
                            .multiply(new BigDecimal(importedSubjectAreaIndex.getSpecificity(subjectArea))).toPlainString()) + ")");
        }
        return Joiner.on(", ").join(values);
    }

    private String getImportedInstitutionSubjectAreaRowDefinition(ImportedInstitution institution, ImportedInstitutionSubjectAreaDTO relation) {
        return "(" + Joiner.on(", ").join(institution.getId().toString(), relation.getSubjectArea().toString(), //
                relation.getRelationStrength().toString()) + ")";
    }

}
