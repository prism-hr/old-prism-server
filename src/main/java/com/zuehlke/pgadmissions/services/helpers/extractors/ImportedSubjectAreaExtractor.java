package com.zuehlke.pgadmissions.services.helpers.extractors;

import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareBooleanForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareCellsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForSqlInsert;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedSubjectAreaImportDTO;
import com.zuehlke.pgadmissions.utils.PrismQueryUtils;

@Component
public class ImportedSubjectAreaExtractor implements ImportedEntityExtractor<ImportedSubjectAreaImportDTO> {

    @Override
    public List<String> extract(PrismImportedEntity prismImportedEntity, List<ImportedSubjectAreaImportDTO> definitions, boolean enable) {
        List<String> rows = Lists.newLinkedList();
        for (ImportedSubjectAreaImportDTO definition : definitions) {
            List<String> cells = Lists.newLinkedList();
            cells.add(PrismQueryUtils.prepareIntegerForSqlInsert(definition.getId()));
            cells.add(prepareStringForSqlInsert(definition.getJacsCode()));
            cells.add(prepareStringForSqlInsert(definition.getJacsCodeOld()));
            cells.add(prepareStringForSqlInsert(definition.getName()));
            cells.add(prepareStringForSqlInsert(definition.getDescription()));
            cells.add(PrismQueryUtils.prepareIntegerForSqlInsert(definition.getUcasSubject()));
            cells.add(PrismQueryUtils.prepareIntegerForSqlInsert(definition.getParent()));
            cells.add(prepareBooleanForSqlInsert(enable));
            rows.add(prepareCellsForSqlInsert(cells));
        }
        return rows;
    }

}
