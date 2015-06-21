package com.zuehlke.pgadmissions.services.helpers.extractors;

import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareCellsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.getProperty;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;

@Component
public class ImportedEntitySimpleExtractor implements ImportedEntityExtractor {

    @Override
    public List<String> extract(PrismImportedEntity prismImportedEntity, List<Object> definitions, boolean enable) throws Exception {
        List<String> rows = Lists.newLinkedList();
        for (Object definition : definitions) {
            List<String> cells = Lists.newLinkedList();
            cells.add(prepareStringForSqlInsert(prismImportedEntity.name()));
            cells.add(prepareStringForSqlInsert((String) getProperty(definition, "name")));
            cells.add(prepareStringForSqlInsert(new Integer(BooleanUtils.toInteger(enable)).toString()));
            String row = prepareCellsForSqlInsert(cells);
            rows.add(row);
        }
        return rows;
    }

}
