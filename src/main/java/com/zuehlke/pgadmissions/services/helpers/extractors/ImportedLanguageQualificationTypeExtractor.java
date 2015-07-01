package com.zuehlke.pgadmissions.services.helpers.extractors;

import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareBooleanForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareCellsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareDecimalForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForSqlInsert;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.referencedata.jaxb.data.LanguageQualificationTypes.LanguageQualificationType;

@Component
public class ImportedLanguageQualificationTypeExtractor implements ImportedEntityExtractor {

    @Override
    public List<String> extract(PrismImportedEntity prismImportedEntity, List<Object> definitions, boolean enable) throws Exception {
        List<String> rows = Lists.newLinkedList();
        for (Object definition : definitions) {
            LanguageQualificationType data = (LanguageQualificationType) definition;
            List<String> cells = Lists.newLinkedList();
            cells.add(prepareStringForSqlInsert(data.getName()));
            cells.add(prepareDecimalForSqlInsert(data.getMinimumOverallScore()));
            cells.add(prepareDecimalForSqlInsert(data.getMaximumOverallScore()));
            cells.add(prepareDecimalForSqlInsert(data.getMinimumReadingScore()));
            cells.add(prepareDecimalForSqlInsert(data.getMaximumReadingScore()));
            cells.add(prepareDecimalForSqlInsert(data.getMinimumWritingScore()));
            cells.add(prepareDecimalForSqlInsert(data.getMaximumWritingScore()));
            cells.add(prepareDecimalForSqlInsert(data.getMinimumSpeakingScore()));
            cells.add(prepareDecimalForSqlInsert(data.getMaximumSpeakingScore()));
            cells.add(prepareDecimalForSqlInsert(data.getMinimumListeningScore()));
            cells.add(prepareDecimalForSqlInsert(data.getMaximumListeningScore()));
            cells.add(prepareBooleanForSqlInsert(enable));
            rows.add(prepareCellsForSqlInsert(cells));
        }
        return rows;
    }

}
