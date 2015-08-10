package com.zuehlke.pgadmissions.services.helpers.extractors;

import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareBooleanForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareColumnsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareDecimalForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForSqlInsert;

import java.util.List;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.api.model.imported.request.ImportedLanguageQualificationTypeRequest;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;

@Component
public class ImportedLanguageQualificationTypeExtractor implements ImportedEntityExtractor<ImportedLanguageQualificationTypeRequest> {

    @Override
    public List<String> extract(PrismImportedEntity prismImportedEntity, List<ImportedLanguageQualificationTypeRequest> definitions, boolean enable) {
        List<String> rows = Lists.newLinkedList();
        for (ImportedLanguageQualificationTypeRequest definition : definitions) {
            List<String> cells = Lists.newLinkedList();
            cells.add(prepareStringForSqlInsert(definition.getName()));
            cells.add(prepareDecimalForSqlInsert(definition.getMinimumOverallScore()));
            cells.add(prepareDecimalForSqlInsert(definition.getMaximumOverallScore()));
            cells.add(prepareDecimalForSqlInsert(definition.getMinimumReadingScore()));
            cells.add(prepareDecimalForSqlInsert(definition.getMaximumReadingScore()));
            cells.add(prepareDecimalForSqlInsert(definition.getMinimumWritingScore()));
            cells.add(prepareDecimalForSqlInsert(definition.getMaximumWritingScore()));
            cells.add(prepareDecimalForSqlInsert(definition.getMinimumSpeakingScore()));
            cells.add(prepareDecimalForSqlInsert(definition.getMaximumSpeakingScore()));
            cells.add(prepareDecimalForSqlInsert(definition.getMinimumListeningScore()));
            cells.add(prepareDecimalForSqlInsert(definition.getMaximumListeningScore()));
            cells.add(prepareBooleanForSqlInsert(enable));
            rows.add(prepareColumnsForSqlInsert(cells));
        }
        return rows;
    }

}
