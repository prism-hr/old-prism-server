package com.zuehlke.pgadmissions.services.helpers.extractors;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.api.model.imported.request.ImportedAdvertDomicileRequest;

import java.util.List;

import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.*;

@Component
public class ImportedAdvertDomicileExtractor implements ImportedEntityExtractor<ImportedAdvertDomicileRequest> {

    @Override
    public List<String> extract(PrismImportedEntity prismImportedEntity, List<ImportedAdvertDomicileRequest> definitions, boolean enable) {
        List<String> rows = Lists.newLinkedList();
        for (ImportedAdvertDomicileRequest definition : definitions) {
            List<String> cells = Lists.newLinkedList();
            cells.add(prepareStringForSqlInsert(definition.getId()));
            cells.add(prepareStringForSqlInsert(definition.getName()));
            cells.add(prepareStringForSqlInsert(definition.getCurrency()));
            cells.add(prepareBooleanForSqlInsert(enable));
            rows.add(prepareCellsForSqlInsert(cells));
        }
        return rows;
    }

}
