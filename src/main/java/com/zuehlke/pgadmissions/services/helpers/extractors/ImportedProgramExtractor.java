package com.zuehlke.pgadmissions.services.helpers.extractors;

import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareBooleanForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareCellsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareIntegerForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForSqlInsert;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.api.model.imported.request.ImportedProgramRequest;

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
            Map<Integer, Integer> importedInstitutionsByUcasId = importedEntityService.getImportedInstitutionsByUcasId();
            if (systemImport) {
                importedInstitutionsByUcasId = importedEntityService.getImportedInstitutionsByUcasId();
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
                } else {
                    cells.add(prepareIntegerForSqlInsert(definition.getInstitution()));
                }

                cells.add(prepareIntegerForSqlInsert(definition.getQualificationType()));
                // TODO: map to PRiSM qualification types
                cells.add(prepareStringForSqlInsert(definition.getLevel()));
                cells.add(prepareStringForSqlInsert(definition.getQualification()));
                cells.add(prepareStringForSqlInsert(definition.getName()));
                cells.add(prepareStringForSqlInsert(definition.getCode()));
                cells.add(prepareBooleanForSqlInsert(false));                
                cells.add(prepareBooleanForSqlInsert(enable));
                rows.add(prepareCellsForSqlInsert(cells));
            }
        }
        return rows;
    }

}
