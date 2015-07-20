package com.zuehlke.pgadmissions.services.helpers.extractors;

import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareBooleanForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareCellsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareIntegerForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForSqlInsert;

import java.util.List;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.api.model.imported.request.ImportedSubjectAreaRequest;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;

@Component
public class ImportedSubjectAreaExtractor implements ImportedEntityExtractor<ImportedSubjectAreaRequest> {

    @Override
    public List<String> extract(PrismImportedEntity prismImportedEntity, List<ImportedSubjectAreaRequest> definitions, boolean enable) {
        List<String> rows = Lists.newLinkedList();
        for (ImportedSubjectAreaRequest definition : definitions) {
            List<String> cells = Lists.newLinkedList();
            cells.add(prepareStringForSqlInsert(definition.getName()));
            cells.add(prepareStringForSqlInsert(definition.getJacsCode()));
            cells.add(prepareIntegerForSqlInsert(definition.getParentSubjectArea()));
            cells.add(prepareBooleanForSqlInsert(enable));
            rows.add(prepareCellsForSqlInsert(cells));
        }
        return rows;
    }

}
