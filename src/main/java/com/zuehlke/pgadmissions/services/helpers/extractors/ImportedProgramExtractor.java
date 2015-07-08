package com.zuehlke.pgadmissions.services.helpers.extractors;

import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareBooleanForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareCellsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareIntegerForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForSqlInsert;

import java.util.List;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.api.model.imported.request.ImportedProgramRequest;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;

@Component
public class ImportedProgramExtractor implements ImportedEntityExtractor<ImportedProgramRequest> {

    @Override
    public List<String> extract(PrismImportedEntity prismImportedEntity, List<ImportedProgramRequest> definitions, boolean enable) throws Exception {
        List<String> rows = Lists.newLinkedList();
        for (ImportedProgramRequest definition : definitions) {
            List<String> cells = Lists.newLinkedList();
            cells.add(prepareIntegerForSqlInsert(definition.getInstitution()));
            cells.add(prepareStringForSqlInsert(definition.getLevel()));
            String qualification = definition.getQualification();
            cells.add(prepareStringForSqlInsert(qualification));
            cells.add(prepareStringForSqlInsert(qualification + " " + definition.getName()));
            cells.add(prepareBooleanForSqlInsert(enable));
            rows.add(prepareCellsForSqlInsert(cells));
        }
        return rows;
    }

}
