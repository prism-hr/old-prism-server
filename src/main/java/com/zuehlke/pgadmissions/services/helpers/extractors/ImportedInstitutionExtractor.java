package com.zuehlke.pgadmissions.services.helpers.extractors;

import static com.zuehlke.pgadmissions.utils.PrismConstants.NULL;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareBooleanForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareCellsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareIntegerForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForSqlInsert;

import java.util.List;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.api.model.imported.request.ImportedInstitutionRequest;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedInstitutionImportDTO;

@Component
public class ImportedInstitutionExtractor<T extends ImportedInstitutionRequest> implements ImportedEntityExtractor<T> {

    @Override
    public List<String> extract(PrismImportedEntity prismImportedEntity, List<T> definitions, boolean enable) {
        List<String> rows = Lists.newLinkedList();
        if (!definitions.isEmpty()) {
            boolean systemImport = definitions.get(0).getClass().equals(ImportedInstitutionImportDTO.class);
            for (ImportedInstitutionRequest definition : definitions) {
                List<String> cells = Lists.newLinkedList();
                cells.add(prepareIntegerForSqlInsert(definition.getDomicile()));
                cells.add(prepareStringForSqlInsert(definition.getName()));

                if (systemImport) {
                    cells.add(prepareIntegerForSqlInsert(((ImportedInstitutionImportDTO) definition).getUcasId()));
                    cells.add(prepareStringForSqlInsert(((ImportedInstitutionImportDTO) definition).getFacebookId()));
                } else {
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
