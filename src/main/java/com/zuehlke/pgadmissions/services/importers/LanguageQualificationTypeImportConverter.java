package com.zuehlke.pgadmissions.services.importers;

import java.math.BigDecimal;

import org.apache.commons.beanutils.PropertyUtils;

import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.LanguageQualificationType;

public class LanguageQualificationTypeImportConverter extends GenericEntityImportConverter<LanguageQualificationType> {

    public LanguageQualificationTypeImportConverter(Institution institution) {
        super(institution, LanguageQualificationType.class);
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
        Float propertyValue = (Float) PropertyUtils.getSimpleProperty(input, propertyName);
        result.setMaximumListeningScore(propertyValue != null ? BigDecimal.valueOf(propertyValue) : null);
    }

}
