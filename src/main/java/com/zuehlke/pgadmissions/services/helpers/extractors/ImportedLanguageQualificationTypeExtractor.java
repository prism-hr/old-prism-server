package com.zuehlke.pgadmissions.services.helpers.extractors;

import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareCellsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareDecimalForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForSqlInsert;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.referencedata.jaxb.LanguageQualificationTypes.LanguageQualificationType;

@Component
public class ImportedLanguageQualificationTypeExtractor implements ImportedEntityExtractor {

    @Override
    public List<String> extract(Institution institution, PrismImportedEntity prismImportedEntity, List<Object> definitions) throws Exception {
        List<String> rows = Lists.newLinkedList();
        for (Object definition : definitions) {
            LanguageQualificationType languageQualificationType = (LanguageQualificationType) definition;

            List<String> cells = Lists.newLinkedList();
            cells.add(prepareStringForSqlInsert(institution.getId().toString()));
            cells.add(prepareStringForSqlInsert(languageQualificationType.getCode()));
            cells.add(prepareStringForSqlInsert(languageQualificationType.getName()));
            cells.add(prepareDecimalForSqlInsert(languageQualificationType.getMinimumOverallScore()));
            cells.add(prepareDecimalForSqlInsert(languageQualificationType.getMaximumOverallScore()));
            cells.add(prepareDecimalForSqlInsert(languageQualificationType.getMinimumReadingScore()));
            cells.add(prepareDecimalForSqlInsert(languageQualificationType.getMaximumReadingScore()));
            cells.add(prepareDecimalForSqlInsert(languageQualificationType.getMinimumWritingScore()));
            cells.add(prepareDecimalForSqlInsert(languageQualificationType.getMaximumWritingScore()));
            cells.add(prepareDecimalForSqlInsert(languageQualificationType.getMinimumSpeakingScore()));
            cells.add(prepareDecimalForSqlInsert(languageQualificationType.getMaximumSpeakingScore()));
            cells.add(prepareDecimalForSqlInsert(languageQualificationType.getMinimumListeningScore()));
            cells.add(prepareDecimalForSqlInsert(languageQualificationType.getMaximumListeningScore()));
            cells.add(prepareStringForSqlInsert(new Integer(1).toString()));

            String row = prepareCellsForSqlInsert(cells);
            rows.add(row);
        }
        return rows;
    }

}