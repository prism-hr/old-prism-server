package com.zuehlke.pgadmissions.services.helpers.extractors;

import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareBooleanForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareColumnsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForSqlInsert;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;

import uk.co.alumeni.prism.api.model.imported.request.ImportedEntityRequest;

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
            rows.add(prepareColumnsForSqlInsert(cells));
        }
        return rows;
    }

}
