package com.zuehlke.pgadmissions.services.helpers.extractors;

import static com.zuehlke.pgadmissions.utils.PrismConstants.NULL;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareBooleanForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareColumnsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForSqlInsert;

import java.util.List;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.api.model.imported.request.ImportedSubjectAreaRequest;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedSubjectAreaImportDTO;
import com.zuehlke.pgadmissions.utils.PrismQueryUtils;

@Component
public class ImportedSubjectAreaExtractor<T extends ImportedSubjectAreaRequest> implements ImportedEntityExtractor<T> {

    @Override
    public List<String> extract(PrismImportedEntity prismImportedEntity, List<T> definitions, boolean enable) {
        List<String> rows = Lists.newLinkedList();
        if (!definitions.isEmpty()) {
            boolean systemImport = definitions.get(0).getClass().equals(ImportedSubjectAreaImportDTO.class);
            for (T definition : definitions) {
                List<String> cells = Lists.newLinkedList();
                cells.add(PrismQueryUtils.prepareIntegerForSqlInsert(definition.getId()));
                cells.add(prepareStringForSqlInsert(definition.getJacsCode()));
                
                if (systemImport) {
                    cells.add(prepareStringForSqlInsert(((ImportedSubjectAreaImportDTO) definition).getJacsCodeOld()));
                } else {
                    cells.add(NULL);
                }
                
                cells.add(prepareStringForSqlInsert(definition.getName()));
                cells.add(prepareStringForSqlInsert(definition.getDescription()));
                
                if (systemImport) {
                    cells.add(PrismQueryUtils.prepareIntegerForSqlInsert(((ImportedSubjectAreaImportDTO) definition).getUcasSubject()));
                    cells.add(PrismQueryUtils.prepareIntegerForSqlInsert(((ImportedSubjectAreaImportDTO) definition).getParent()));
                } else {
                    cells.add(NULL);
                    cells.add(NULL);
                }

                cells.add(prepareBooleanForSqlInsert(enable));
                rows.add(prepareColumnsForSqlInsert(cells));
            }
        }
        return rows;
    }

}
