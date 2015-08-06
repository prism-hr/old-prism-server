package com.zuehlke.pgadmissions.services.helpers.extractors;

import static com.zuehlke.pgadmissions.domain.definitions.PrismQualificationLevel.getByUcasLevel;
import static com.zuehlke.pgadmissions.utils.PrismConstants.NULL;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareBooleanForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareCellsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareIntegerForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForSqlInsert;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.api.model.imported.request.ImportedProgramRequest;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedProgramImportDTO;
import com.zuehlke.pgadmissions.services.ImportedEntityService;

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
            Map<Integer, Integer> importedInstitutionsByUcasId = importedEntityService.getImportedUcasInstitutions();
            if (systemImport) {
                importedInstitutionsByUcasId = importedEntityService.getImportedUcasInstitutions();
            }

            for (ImportedProgramRequest definition : definitions) {
                List<String> cells = Lists.newLinkedList();

                if (systemImport) {
                    Integer ucasId = definition.getInstitution();
                    Integer institution = importedInstitutionsByUcasId.get(ucasId);
                    if (institution == null) {
                        logger.error("No imported institution for ucas institution: " + ucasId);
                        continue;
                    }
                    cells.add(prepareIntegerForSqlInsert(institution));
                    cells.add(NULL);
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
                rows.add(prepareCellsForSqlInsert(cells));
            }
        }
        return rows;
    }

}
