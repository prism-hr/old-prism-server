package com.zuehlke.pgadmissions.services.helpers.extractors;

import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareBooleanForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareColumnsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForSqlInsert;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;

import uk.co.alumeni.prism.api.model.imported.request.ImportedDomicileRequest;

@Component
public class ImportedDomicileExtractor implements ImportedEntityExtractor<ImportedDomicileRequest> {

    @Override
    public List<String> extract(PrismImportedEntity prismImportedEntity, List<ImportedDomicileRequest> definitions, boolean enable) {
        List<String> rows = Lists.newLinkedList();
        for (ImportedDomicileRequest definition : definitions) {
            List<String> cells = Lists.newLinkedList();
            cells.add(prepareStringForSqlInsert(definition.getId()));
            cells.add(prepareStringForSqlInsert(definition.getName()));
            cells.add(prepareStringForSqlInsert(definition.getCurrency()));
            cells.add(prepareBooleanForSqlInsert(enable));
            rows.add(prepareColumnsForSqlInsert(cells));
        }
        return rows;
    }

}
