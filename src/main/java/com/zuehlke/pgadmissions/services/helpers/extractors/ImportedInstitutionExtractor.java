package com.zuehlke.pgadmissions.services.helpers.extractors;

import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareBooleanForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareCellsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareIntegerForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForSqlInsert;

import java.util.List;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.api.model.imported.request.ImportedInstitutionRequest;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;

@Component
public class ImportedInstitutionExtractor implements ImportedEntityExtractor<ImportedInstitutionRequest> {

    @Override
    public List<String> extract(PrismImportedEntity prismImportedEntity, List<ImportedInstitutionRequest> definitions, boolean enable) {
        List<String> rows = Lists.newLinkedList();
        for (ImportedInstitutionRequest definition : definitions) {
            List<String> cells = Lists.newLinkedList();
            cells.add(prepareIntegerForSqlInsert(definition.getDomicile()));
            cells.add(prepareStringForSqlInsert(definition.getName()));
            cells.add(prepareStringForSqlInsert(definition.getUcasId()));
            cells.add(prepareStringForSqlInsert(definition.getFacebookId()));
            cells.add(prepareBooleanForSqlInsert(enable));
            rows.add(prepareCellsForSqlInsert(cells));
        }
        return rows;
    }

}
