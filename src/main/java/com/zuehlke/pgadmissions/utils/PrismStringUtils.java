package com.zuehlke.pgadmissions.utils;

import static com.zuehlke.pgadmissions.utils.PrismConstants.CHARACTER_WILDCARD;
import static com.zuehlke.pgadmissions.utils.PrismConstants.SPACE;
import static com.zuehlke.pgadmissions.utils.PrismConstants.WORD_BOUDARY;
import static org.apache.commons.lang.StringUtils.isNumeric;

import java.math.BigDecimal;
import java.util.Set;

import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;

import com.google.common.collect.Sets;

public class PrismStringUtils {

    private static final String[] STOP_CHARS = new String[] { "/", ",", ":", ";", ".", "|", "(", ")", "{", "}", "[", "]" };

    private static final String[] STOP_WORDS = new String[] { "and", "in", "of", "including", "as", "a", "or", "for", "in", "via", "its", "it's" };

    private static final String[] JOIN_CHARS = new String[] { "-", "'" };

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
        String cleanString = cleanString(replaceEachWord(replaceEachChar(string.toLowerCase(), STOP_CHARS, SPACE), STOP_WORDS, SPACE));

        Set<Set<String>> tokens = Sets.newLinkedHashSet();
        for (String word : cleanString.split(" ")) {
            if (isValidToken(word)) {
                Set<String> token = Sets.newHashSet(word);
                String alternateWord = replaceEachChar(word, JOIN_CHARS, SPACE);
                if (isValidToken(alternateWord)) {
                    token.add(alternateWord);
                }
                tokens.add(token);
            }
        }

        return tokens;
    }

    public static String wrapInWordBoundary(String string) {
        return WORD_BOUDARY + string + WORD_BOUDARY;
    }

    public static String wrapInCharacterWildcard(String string) {
        return CHARACTER_WILDCARD + string + CHARACTER_WILDCARD;
    }

    private static String replaceEachChar(String string, String[] oldStrings, String newString) {
        for (String oldString : oldStrings) {
            string = string.replace(oldString, newString);
        }
        return string;
    }

    private static String replaceEachWord(String string, String[] oldStrings, String newString) {
        for (String oldString : oldStrings) {
            string = string.replaceAll(wrapInWordBoundary(oldString), newString);
        }
        return string;
    }
    
    private static boolean isValidToken(String word) {
        return !(isNumeric(word) || word.length() == 1);
    }

}
