package com.zuehlke.pgadmissions.services.helpers.extractors;

import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareBooleanForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareCellsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareIntegerForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForSqlInsert;

import java.util.List;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.api.model.imported.request.ImportedAgeRangeRequest;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;

@Component
public class ImportedAgeRangeExtractor implements ImportedEntityExtractor<ImportedAgeRangeRequest> {

    @Override
    public List<String> extract(PrismImportedEntity prismImportedEntity, List<ImportedAgeRangeRequest> definitions, boolean enable) throws Exception {
        List<String> rows = Lists.newLinkedList();
        for (ImportedAgeRangeRequest definition : definitions) {
            List<String> cells = Lists.newLinkedList();
            cells.add(prepareStringForSqlInsert(definition.getName()));
            cells.add(prepareStringForSqlInsert(definition.getLowerBound().toString()));
            cells.add(prepareIntegerForSqlInsert(definition.getUpperBound()));
            cells.add(prepareBooleanForSqlInsert(enable));
            rows.add(prepareCellsForSqlInsert(cells));
        }
        return rows;
    }

}
