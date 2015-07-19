package com.zuehlke.pgadmissions.services.helpers.extractors;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.api.model.imported.request.ImportedSubjectAreaRequest;

import java.util.List;

import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.*;

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
