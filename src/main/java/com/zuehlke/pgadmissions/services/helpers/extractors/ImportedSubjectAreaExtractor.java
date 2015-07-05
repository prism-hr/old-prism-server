package com.zuehlke.pgadmissions.services.helpers.extractors;

import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareBooleanForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareCellsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForSqlInsert;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.SubjectAreas.SubjectArea;

@Component
public class ImportedSubjectAreaExtractor implements ImportedEntityExtractor {

    @Override
    public List<String> extract(PrismImportedEntity prismImportedEntity, List<Object> definitions, boolean enable) throws Exception {
        List<String> rows = Lists.newLinkedList();
        for (Object definition : definitions) {
            SubjectArea data = (SubjectArea) definition;
            List<String> cells = Lists.newLinkedList();
            cells.add(prepareStringForSqlInsert(data.getName()));
            cells.add(prepareStringForSqlInsert(data.getCode()));
            cells.add(prepareBooleanForSqlInsert(enable));
            rows.add(prepareCellsForSqlInsert(cells));
        }
        return rows;
    }

}
