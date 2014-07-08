package com.zuehlke.pgadmissions.services.importers;

import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.LanguageQualificationType;
import org.apache.commons.beanutils.PropertyUtils;

import java.math.BigDecimal;

public class LanguageQualificationTypeImportConverter extends GenericEntityImportConverter<LanguageQualificationType> {

    public LanguageQualificationTypeImportConverter(Institution institution) {
        super(LanguageQualificationType.class, institution);
    }

    protected void setCustomProperties(Object input, LanguageQualificationType result) throws Exception {
        convertFloatToBigDecimal(input, result, "maximumListeningScore");
        convertFloatToBigDecimal(input, result, "maximumWritingScore");
        convertFloatToBigDecimal(input, result, "maximumSpeakingScore");
        convertFloatToBigDecimal(input, result, "minimumOverallScore");
        convertFloatToBigDecimal(input, result, "maximumListeningScore");
        convertFloatToBigDecimal(input, result, "maximumReadingScore");
        convertFloatToBigDecimal(input, result, "maximumOverallScore");
        convertFloatToBigDecimal(input, result, "minimumSpeakingScore");
        convertFloatToBigDecimal(input, result, "minimumReadingScore");
        convertFloatToBigDecimal(input, result, "minimumWritingScore");
        convertFloatToBigDecimal(input, result, "minimumListeningScore");
    }

    private void convertFloatToBigDecimal(Object input, LanguageQualificationType result, String propertyName) throws Exception {
        Float maximumListeningScore = (Float) PropertyUtils.getSimpleProperty(input, propertyName);
        result.setMaximumListeningScore(maximumListeningScore != null ? BigDecimal.valueOf(maximumListeningScore) : null);
    }

}