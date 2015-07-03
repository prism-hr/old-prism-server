package com.zuehlke.pgadmissions.services.helpers.extractors;

import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareBooleanForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareCellsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.getProperty;

import java.util.List;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.api.model.imported.ImportedEntity;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;

@Component
public class ImportedEntitySimpleExtractor implements ImportedEntityExtractor<ImportedEntity> {

    @Override
    public List<String> extract(PrismImportedEntity prismImportedEntity, List<ImportedEntity> definitions, boolean enable) throws Exception {
        List<String> rows = Lists.newLinkedList();
        for (Object definition : definitions) {
            List<String> cells = Lists.newLinkedList();
            cells.add(prepareStringForSqlInsert(prismImportedEntity.name()));
            cells.add(prepareStringForSqlInsert((String) getProperty(definition, "name")));
            cells.add(prepareBooleanForSqlInsert(enable));
            rows.add(prepareCellsForSqlInsert(cells));
        }
        return rows;
    }

}
