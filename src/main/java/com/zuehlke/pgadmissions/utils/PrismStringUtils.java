package com.zuehlke.pgadmissions.utils;

import static com.zuehlke.pgadmissions.utils.PrismConstants.CHARACTER_WILDCARD;
import static com.zuehlke.pgadmissions.utils.PrismConstants.SPACE;
import static com.zuehlke.pgadmissions.utils.PrismConstants.WORD_BOUNDARY;
import static org.apache.commons.lang.StringUtils.isNumeric;

import java.math.BigDecimal;
import java.util.Set;

import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dto.TokenizedStringDTO;

public class PrismStringUtils {

    public static final String[] STOP_CHARS = new String[] { ",", ":", ";", ".", "|", "(", ")", "{", "}", "[", "]" };

    public static final String[] STOP_WORDS = new String[] { "and", "in", "of", "including", "as", "a", "or", "for", "in", "via", "its", "it's", "with", "up" };

    public static final String[] JOIN_CHARS = new String[] { "-", "'", "/" };

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

    public static TokenizedStringDTO tokenize(String string, String... optionalStopWords) {
        String cleanString = cleanString(replaceEachChar(string.toLowerCase(), STOP_CHARS, SPACE));
        cleanString = cleanString(replaceEachWord(cleanString, STOP_WORDS, SPACE));

        if (optionalStopWords != null) {
            cleanString = replaceEachWord(cleanString, optionalStopWords, SPACE);
        }

        int alternateCount = 0;
        Set<String> tokens = Sets.newLinkedHashSet();
        for (String token : cleanString.split(" ")) {
            if (isValidToken(token)) {
                tokens.add(token);
                String alternateWord = replaceEachChar(token, JOIN_CHARS, SPACE);
                if (isValidToken(alternateWord) && !tokens.contains(alternateWord)) {
                    tokens.add(alternateWord);
                    alternateCount++;
                }
            }
        }

        return new TokenizedStringDTO(tokens, (tokens.size() - alternateCount));
    }

    public static String wrapInWordBoundary(String string) {
        return WORD_BOUNDARY + string + WORD_BOUNDARY;
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
