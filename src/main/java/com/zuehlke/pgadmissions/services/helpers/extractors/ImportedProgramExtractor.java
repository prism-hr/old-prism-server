package com.zuehlke.pgadmissions.services.helpers.extractors;

import static com.zuehlke.pgadmissions.PrismConstants.NULL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismQualificationLevel.OTHER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismQualificationLevel.POSTGRADUATE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismQualificationLevel.UNDERGRADUATE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismQualificationLevel.getByUcasLevel;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareBooleanForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareColumnsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareIntegerForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForSqlInsert;
import static java.util.Arrays.asList;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismQualificationLevel;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedProgramImportDTO;
import com.zuehlke.pgadmissions.services.ImportedEntityService;

import uk.co.alumeni.prism.api.model.imported.request.ImportedProgramRequest;

@Component
public class ImportedProgramExtractor<T extends ImportedProgramRequest> implements ImportedEntityExtractor<T> {

    private static final Logger logger = LoggerFactory.getLogger(ImportedProgramExtractor.class);

    @Inject
    private ImportedEntityService importedEntityService;

    @Override
    public List<String> extract(PrismImportedEntity prismImportedEntity, List<T> definitions, boolean enable) {
        List<String> rows = Lists.newLinkedList();
        if (!definitions.isEmpty()) {
            boolean systemImport = definitions.get(0).getClass().equals(ImportedProgramImportDTO.class);

            Map<Integer, Integer> importedInstitutionsByUcasId = null;
            Map<String, Integer> importedQualificationTypesByUclCode = null;
            if (systemImport) {
                importedInstitutionsByUcasId = importedEntityService.getImportedUcasInstitutions();
                importedQualificationTypesByUclCode = importedEntityService.getImportedQualificationTypesByUclCode();
            }

            for (ImportedProgramRequest definition : definitions) {
                List<String> cells = Lists.newLinkedList();

                if (systemImport) {
                    Integer institution = getImportedInstitution(definition, importedInstitutionsByUcasId);
                    if (institution == null) {
                        logger.error("No imported institution for ucas institution: " + definition.getInstitution());
                        continue;
                    }

                    Integer qualificationType = getImportedQualification(definition, importedQualificationTypesByUclCode);
                    if (qualificationType == null) {
                        logger.error("No imported qualification type for ucas qualification: " + definition.getQualification());
                    }

                    cells.add(prepareIntegerForSqlInsert(institution));
                    cells.add(prepareIntegerForSqlInsert(qualificationType));
                    cells.add(prepareStringForSqlInsert(getByUcasLevel(definition.getLevel()).name()));
                    cells.add(prepareStringForSqlInsert(definition.getQualification()));
                } else {
                    cells.add(prepareIntegerForSqlInsert(definition.getInstitution()));
                    cells.add(prepareIntegerForSqlInsert(definition.getQualificationType()));
                    cells.add(NULL);
                    cells.add(NULL);
                }

                cells.add(prepareStringForSqlInsert(definition.getName()));

                if (systemImport) {
                    cells.add(prepareStringForSqlInsert(definition.getCode()));
                    cells.add(prepareIntegerForSqlInsert(((ImportedProgramImportDTO) definition).getWeight()));

                    Set<String> jacsCodes = ((ImportedProgramImportDTO) definition).getJacsCodes();
                    if (jacsCodes == null) {
                        cells.add(NULL);
                    } else {
                        cells.add(prepareStringForSqlInsert(Joiner.on("|").join(((ImportedProgramImportDTO) definition).getJacsCodes())));
                    }

                    Set<Integer> ucasSubjects = ((ImportedProgramImportDTO) definition).getUcasSubjects();
                    if (ucasSubjects == null) {
                        cells.add(NULL);
                    } else {
                        cells.add(prepareStringForSqlInsert(Joiner.on("|").join(ucasSubjects)));
                    }
                } else {
                    cells.add(NULL);
                    cells.add(NULL);
                    cells.add(NULL);
                    cells.add(NULL);
                }

                cells.add(prepareBooleanForSqlInsert(false));
                cells.add(prepareBooleanForSqlInsert(enable));
                rows.add(prepareColumnsForSqlInsert(cells));
            }
        }
        return rows;
    }

    private Integer getImportedInstitution(ImportedProgramRequest definition, Map<Integer, Integer> importedInstitutionsByUcasId) {
        Integer ucasId = definition.getInstitution();
        Integer institution = importedInstitutionsByUcasId.get(ucasId);
        return institution;
    }

    private Integer getImportedQualification(ImportedProgramRequest definition, Map<String, Integer> importedQualificationTypesByUclCode) {
        String qualification = definition.getQualification();
        PrismQualificationLevel level = getByUcasLevel(definition.getLevel());

        if (level.equals(OTHER) || level.name().startsWith("HE_LEVEL")) {
            return importedQualificationTypesByUclCode.get("6");
        } else if (level.equals(POSTGRADUATE) && qualification.equals("Qualification PhD")) {
            return importedQualificationTypesByUclCode.get("PHD");
        } else if (level.equals(POSTGRADUATE) && qualification.equals("Qualification MPhil")) {
            return importedQualificationTypesByUclCode.get("MPHIL");
        } else if (level.equals(POSTGRADUATE)) {
            return importedQualificationTypesByUclCode.get("MSTCRD");
        } else if (level.equals(UNDERGRADUATE) && asList("Qualification MEng", "Qualification MEng (Hons)").contains(qualification)) {
            return importedQualificationTypesByUclCode.get("DEGHONMENG");
        } else if (level.equals(UNDERGRADUATE) && asList("Qualification MSci", "Qualification MSci (Hons)").contains(qualification)) {
            return importedQualificationTypesByUclCode.get("DEGHONMSCI");
        } else if (level.equals(UNDERGRADUATE) && (qualification.startsWith("Qualification M") || qualification.startsWith("Qualification IPM"))) {
            return importedQualificationTypesByUclCode.get("MSTPAS");
        } else if (level.equals(UNDERGRADUATE)) {
            return importedQualificationTypesByUclCode.get("DEGHON");
        }

        return null;
    }

}
