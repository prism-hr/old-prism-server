package com.zuehlke.pgadmissions.utils;

import static com.zuehlke.pgadmissions.utils.PrismConstants.SPACE;

import java.math.BigDecimal;
import java.util.Set;

import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;

import com.google.common.collect.Sets;

public class PrismStringUtils {

    private static final String[] STOP_WORDS = new String[] { "/", ",", ":", ";", ".", "|", "(", ")", "{", "}", "[", "]", "and", "in", "of", "including", "as",
            "a", "or", "for", "in", "via", "its", "it's" };

    private static final String[] TOGGLE_WORDS = new String[] { "-", "'" };

    public static String getBigDecimalAsString(BigDecimal value) {
        return value == null ? null : value.toPlainString();
    }

    public static <T extends ImportedEntityResponse> String getImportedEntityAsString(T importedEntity) {
        return importedEntity == null ? null : importedEntity.getName();
    }

    public static String cleanString(String string) {
        return string.replace("\n", " ").replace("\r", " ").replace("\t", " ").replaceAll(" +", " ").trim();
    }

    public static String cleanStringToLowerCase(String string) {
        return cleanString(string).toLowerCase();
    }

    public static Set<Set<String>> tokenize(String string) {
        replaceEach(string, STOP_WORDS, SPACE);
        String cleanString = cleanStringToLowerCase(string);

        Set<Set<String>> tokens = Sets.newLinkedHashSet();
        for (String word : cleanString.split(" ")) {
            tokens.add(Sets.newHashSet(word, replaceEach(word, TOGGLE_WORDS, SPACE)));
        }

        return tokens;
    }

    private static String replaceEach(String string, String[] oldStrings, String newString) {
        for (String oldString : oldStrings) {
            string.replace(oldString, newString);
        }
        return string;
    }

}
