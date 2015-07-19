package com.zuehlke.pgadmissions.services.helpers.extractors;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.api.model.imported.request.ImportedEntityRequest;

import java.util.List;

import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.*;

@Component
public class ImportedEntitySimpleExtractor implements ImportedEntityExtractor<ImportedEntityRequest> {

    @Override
    public List<String> extract(PrismImportedEntity prismImportedEntity, List<ImportedEntityRequest> definitions, boolean enable) {
        List<String> rows = Lists.newLinkedList();
        for (ImportedEntityRequest definition : definitions) {
            List<String> cells = Lists.newLinkedList();
            cells.add(prepareStringForSqlInsert(prismImportedEntity.name()));
            cells.add(prepareStringForSqlInsert((definition.getName())));
            cells.add(prepareBooleanForSqlInsert(enable));
            rows.add(prepareCellsForSqlInsert(cells));
        }
        return rows;
    }

}
