package com.zuehlke.pgadmissions.utils;

import java.math.BigDecimal;

import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;

public class PrismStringUtils {

    public static String getBigDecimalAsString(BigDecimal value) {
        return value == null ? null : value.toPlainString();
    }

    public static <T extends ImportedEntityResponse> String getImportedEntityAsString(T importedEntity) {
        return importedEntity == null ? null : importedEntity.getName();
    }

}
