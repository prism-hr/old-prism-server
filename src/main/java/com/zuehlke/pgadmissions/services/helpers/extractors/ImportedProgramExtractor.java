package com.zuehlke.pgadmissions.services.helpers.extractors;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.api.model.imported.request.ImportedProgramRequest;

import java.util.List;

import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.*;

@Component
public class ImportedProgramExtractor implements ImportedEntityExtractor<ImportedProgramRequest> {

    @Override
    public List<String> extract(PrismImportedEntity prismImportedEntity, List<ImportedProgramRequest> definitions, boolean enable) {
        List<String> rows = Lists.newLinkedList();
        for (ImportedProgramRequest definition : definitions) {
            List<String> cells = Lists.newLinkedList();
            cells.add(prepareIntegerForSqlInsert(definition.getInstitution()));
            cells.add(prepareIntegerForSqlInsert(definition.getQualificationType())); // TODO: map to PRiSM qualification types
            cells.add(prepareStringForSqlInsert(definition.getLevel()));
            String qualification = definition.getQualification();
            cells.add(prepareStringForSqlInsert(qualification));
            cells.add(prepareStringForSqlInsert(qualification + " " + definition.getName()));
            cells.add("null"); // TODO: implement JACS code as derived from UCAS
            cells.add(prepareBooleanForSqlInsert(enable));
            rows.add(prepareCellsForSqlInsert(cells));
        }
        return rows;
    }

}
