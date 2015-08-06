package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismQualificationLevel.values;
import static com.zuehlke.pgadmissions.domain.definitions.PrismTargetingMatchType.JACS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismTargetingMatchType.PARENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismTargetingMatchType.TOKEN;
import static com.zuehlke.pgadmissions.domain.definitions.PrismTargetingMatchType.UCAS;
import static com.zuehlke.pgadmissions.utils.PrismStringUtils.tokenize;
import static com.zuehlke.pgadmissions.utils.PrismTargetingUtils.STOP_WORDS;
import static com.zuehlke.pgadmissions.utils.PrismTargetingUtils.isValidUcasCodeFormat;
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
import com.zuehlke.pgadmissions.domain.definitions.PrismQualificationLevel;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.imported.ImportedSubjectArea;
import com.zuehlke.pgadmissions.dto.ImportedInstitutionSubjectAreaDTO;
import com.zuehlke.pgadmissions.dto.ImportedInstitutionSubjectAreasDTO;
import com.zuehlke.pgadmissions.dto.ImportedProgramSubjectAreaDTO;
import com.zuehlke.pgadmissions.dto.ImportedProgramSubjectAreasDTO;
import com.zuehlke.pgadmissions.dto.TokenizedStringDTO;
import com.zuehlke.pgadmissions.services.indices.ImportedSubjectAreaIndex;

@Service
@Transactional
public class TargetingService {

    private static final Logger logger = LoggerFactory.getLogger(TargetingService.class);

    private static final String IMPORTED_ENTITY_RELATION_UPDATE = "relation_strength = values(relation_strength)";

    private static final Integer PRECISION = 9;

    private static final BigDecimal DIVISOR = new BigDecimal(2);

    private static final Integer THRESHOLD_TOKEN = 2;

    private static final BigDecimal THRESHOLD_UCAS = new BigDecimal(0.05);

    private static final BigDecimal CONFIDENCE_UCAS = new BigDecimal(0.25);

    private static final Integer CONCENTRATION_OPTIMUM = 5;

    private static final BigDecimal PROLIFERATION_PENALTY = new BigDecimal(0.025);

    @Inject
    private EntityService entityService;

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private ImportedSubjectAreaIndex importedSubjectAreaIndex;

    public void indexImportedProgram(ImportedProgram program) {
        Integer importedProgramId = program.getId();
        String inserts = getImportedProgramSubjectAreaInserts(program);
        if (inserts != null) {
            logger.info("Indexing imported program: " + importedProgramId.toString() + "-" + program.getInstitution().getName() + "-"
                    + program.getName());
            importedEntityService.executeBulkMerge("imported_program_subject_area", //
                    "imported_program_id, imported_subject_area_id, match_type, relation_strength", //
                    inserts, IMPORTED_ENTITY_RELATION_UPDATE);
        }
        importedEntityService.setImportedInstitutionIndexed(program.getInstitution().getId(), false);
        importedEntityService.setImportedProgramIndexed(importedProgramId, true);
        entityService.flush();
    }

    public void indexImportedInstitution(ImportedInstitution institution) {
        Integer importedInstitutionId = institution.getId();
        String inserts = getImportedInstitutionSubjectAreaInserts(institution);
        if (inserts != null) {
            logger.info("Indexing imported institution: " + importedInstitutionId.toString() + "-" + institution.getName());
            importedEntityService.executeBulkMerge("imported_institution_subject_area", //
                    "imported_institution_id, imported_subject_area_id, relation_strength", //
                    inserts, IMPORTED_ENTITY_RELATION_UPDATE);
        }
        importedEntityService.setImportedInstitutionIndexed(importedInstitutionId, true);
        entityService.flush();
    }

    private <T extends ImportedEntityRequest> String getImportedProgramSubjectAreaInserts(ImportedProgram program) {
        ImportedProgramSubjectAreasDTO inserts = new ImportedProgramSubjectAreasDTO();
        BigDecimal weight = new BigDecimal(1).divide(new BigDecimal(program.getUcasProgramCount()), PRECISION, HALF_UP);

        assignImportedSubjectAreasByJacsCode(inserts, program, weight);
        assignImportedSubjectAreasByKeyword(inserts, program, weight);
        assignImportedSubjectAreasByUcasSubject(inserts, program, weight);
        assignImportedSubjectAreasByParent(inserts, weight);

        return inserts.isEmpty() ? null : getImportedProgramSubjectAreaRowDefinitions(program, inserts);
    }

    private String getImportedInstitutionSubjectAreaInserts(ImportedInstitution institution) {
        ImportedInstitutionSubjectAreasDTO inserts = new ImportedInstitutionSubjectAreasDTO(CONCENTRATION_OPTIMUM);  
        for (ImportedInstitutionSubjectAreaDTO relation : importedEntityService.getImportedInstitutionSubjectAreas(institution)) {
            inserts.add(relation);
        }
        return inserts.isEmpty() ? null : getImportedInstitutionSubjectAreaRowDefinitions(institution, inserts);
    }

    private void assignImportedSubjectAreasByJacsCode(ImportedProgramSubjectAreasDTO inserts, ImportedProgram program, BigDecimal weight) {
        String ucasCode = program.getUcasCode();
        if (ucasCode != null && isValidUcasCodeFormat(ucasCode)) {
            String jacsCodes = program.getJacsCodes();
            if (jacsCodes != null) {
                String[] jacsCodesArray = jacsCodes.split("\\|");
                for (String jacsCode : jacsCodesArray) {
                    BigDecimal confidence = new BigDecimal(1).divide(new BigDecimal(jacsCodesArray.length), PRECISION, HALF_UP);
                    if (!assignImportedSubjectAreaByJacsCode(inserts, jacsCode, weight, confidence)) {
                        if (Character.isUpperCase(jacsCode.charAt(0)) && !jacsCode.endsWith("000")) {
                            BigDecimal divisor = DIVISOR;
                            for (int i = 3; i > 0; i--) {
                                String jacsCodeParent = rightPad(jacsCode.substring(0, i), 4, "0");
                                if (!jacsCodeParent.equals(jacsCode)) {
                                    assignImportedSubjectAreaByJacsCode(inserts, jacsCodeParent, weight, confidence.divide(divisor, PRECISION, HALF_UP));
                                }
                                divisor = divisor.multiply(DIVISOR);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean assignImportedSubjectAreaByJacsCode(ImportedProgramSubjectAreasDTO inserts, String programCode, BigDecimal weight,
            BigDecimal confidence) {
        ImportedSubjectArea subjectArea = importedSubjectAreaIndex.getByJacsCode(programCode);
        subjectArea = subjectArea == null ? importedSubjectAreaIndex.getByJacsCodeOld(programCode) : subjectArea;
        if (subjectArea != null) {
            inserts.add(new ImportedProgramSubjectAreaDTO(subjectArea.getId(), JACS, weight, confidence));
            return true;
        }
        return false;
    }

    private void assignImportedSubjectAreasByKeyword(ImportedProgramSubjectAreasDTO inserts, ImportedProgram program, BigDecimal weight) {
        TokenizedStringDTO programTokens = tokenize(program.getName(), STOP_WORDS);
        Map<ImportedSubjectArea, Integer> tokenMatches = Maps.newHashMap();

        for (String programToken : programTokens.getTokens()) {
            Set<ImportedSubjectArea> hits = importedSubjectAreaIndex.getByKeyword(programToken);
            for (ImportedSubjectArea hit : hits) {
                Integer count = tokenMatches.get(hit);
                tokenMatches.put(hit, count == null ? 1 : (count + 1));
            }
        }

        int programTokenCount = programTokens.getUniqueTokenCount();
        BigDecimal programConfidenceRequired = getTokenRequiredConfidence(programTokenCount, THRESHOLD_TOKEN);

        for (Map.Entry<ImportedSubjectArea, Integer> tokenMatch : tokenMatches.entrySet()) {
            ImportedSubjectArea token = tokenMatch.getKey();
            int matchCount = tokenMatch.getValue();
            int subjectTokenCount = importedSubjectAreaIndex.getUniqueTokenCount(token);

            BigDecimal subjectConfidence = new BigDecimal(matchCount).divide(new BigDecimal(subjectTokenCount), PRECISION, HALF_UP);
            BigDecimal subjectConfidenceRequired = getTokenRequiredConfidence(subjectTokenCount, THRESHOLD_TOKEN);
            BigDecimal programConfidence = new BigDecimal(matchCount).divide(new BigDecimal(programTokenCount), PRECISION, HALF_UP);

            if (programConfidence.compareTo(programConfidenceRequired) >= 0 && subjectConfidence.compareTo(subjectConfidenceRequired) >= 0) {
                BigDecimal confidence = programConfidence.multiply(subjectConfidence).setScale(PRECISION, HALF_UP);
                inserts.add(new ImportedProgramSubjectAreaDTO(token.getId(), TOKEN, weight, confidence));
            }
        }
    }

    private void assignImportedSubjectAreasByUcasSubject(ImportedProgramSubjectAreasDTO inserts, ImportedProgram program, BigDecimal weight) {
        String ucasSubjects = program.getUcasSubjects();
        if (ucasSubjects != null) {
            String[] ucasSubjectsSplit = ucasSubjects.split("\\|");
            BigDecimal threshold = new BigDecimal(1).divide(new BigDecimal(ucasSubjectsSplit.length), PRECISION, HALF_UP);
            if (threshold.compareTo(THRESHOLD_UCAS) >= 0) {
                BigDecimal confidence = threshold.multiply(CONFIDENCE_UCAS).setScale(PRECISION, HALF_UP);
                for (String ucasSubject : ucasSubjects.split("\\|")) {
                    for (ImportedSubjectArea subjectArea : importedSubjectAreaIndex.getByUcasSubject(Integer.parseInt(ucasSubject))) {
                        inserts.add(new ImportedProgramSubjectAreaDTO(subjectArea.getId(), UCAS, weight, confidence));
                    }
                }
            }
        }
    }

    private void assignImportedSubjectAreasByParent(ImportedProgramSubjectAreasDTO inserts, BigDecimal weight) {
        ImportedProgramSubjectAreasDTO insertsParent = new ImportedProgramSubjectAreasDTO();
        for (ImportedProgramSubjectAreaDTO insert : inserts.values()) {
            ImportedSubjectArea subjectArea = importedSubjectAreaIndex.getById(insert.getId());
            for (ImportedSubjectArea parent : importedSubjectAreaIndex.getParents(subjectArea)) {
                ImportedProgramSubjectAreaDTO insertParent = new ImportedProgramSubjectAreaDTO(parent.getId(), PARENT, weight, insert.getConfidence());
                insertsParent.add(insertParent);
            }
        }
        inserts.putAll(insertsParent);
    }

    private String getImportedProgramSubjectAreaRowDefinitions(ImportedProgram program, ImportedProgramSubjectAreasDTO inserts) {
        String programId = program.getId().toString();

        PrismQualificationLevel qualificationLevel = program.getLevel();
        Integer qualificationLevelFactor = qualificationLevel == null ? values().length : (qualificationLevel.ordinal() + 1);
        BigDecimal difficulty = new BigDecimal(1).divide(new BigDecimal(qualificationLevelFactor).multiply(DIVISOR), PRECISION, HALF_UP);

        List<String> values = Lists.newArrayListWithExpectedSize(inserts.size());
        for (ImportedProgramSubjectAreaDTO insert : inserts.values()) {
            values.add("(" + Joiner.on(", ").join(programId, insert.getId().toString(), "'" + insert.getMatchType().name() + "'", //
                    insert.getConfidence().multiply(difficulty).toPlainString()) + ")");
        }
        return Joiner.on(", ").join(values);
    }

    private String getImportedInstitutionSubjectAreaRowDefinitions(ImportedInstitution institution, ImportedInstitutionSubjectAreasDTO inserts) {
        String institutionId = institution.getId().toString();

        List<String> values = Lists.newArrayListWithExpectedSize(inserts.size());
        for (ImportedInstitutionSubjectAreaDTO insert : inserts.values()) {
            BigDecimal penalty = new BigDecimal(1).add(new BigDecimal(insert.getTailLength()).multiply(PROLIFERATION_PENALTY)).setScale(PRECISION, HALF_UP);

            values.add("(" + Joiner.on(", ").join(institutionId, insert.getId().toString(), //
                    insert.getHead().divide(penalty, PRECISION, HALF_UP).toPlainString()) + ")");
        }
        return Joiner.on(", ").join(values);
    }

    private BigDecimal getTokenRequiredConfidence(int tokenCount, int threshold) {
        if (tokenCount <= threshold) {
            return new BigDecimal(THRESHOLD_TOKEN).divide(new BigDecimal(threshold), PRECISION, HALF_UP);
        }
        return getTokenRequiredConfidence(tokenCount, (threshold * THRESHOLD_TOKEN));
    }

}
