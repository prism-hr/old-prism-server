package uk.co.alumeni.prism.utils;

import static com.google.common.collect.Lists.newLinkedList;
import static java.util.stream.Collectors.toList;
import static uk.co.alumeni.prism.PrismConstants.HASH;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.base.Joiner;

public class PrismStringUtils {

    public static String getBigDecimalAsString(BigDecimal value) {
        return value == null ? null : value.toPlainString();
    }

    public static String cleanString(String string) {
        return string.replace("\n", " ").replace("\r", " ").replace("\t", " ").replaceAll(" +", " ").trim();
    }

    public static String cleanStringToLowerCase(String string) {
        return cleanString(string).toLowerCase();
    }

    public static String obfuscateEmail(String email) {
        char hashChar = HASH.charAt(0);
        String[] parts = email.split("@");

        List<List<String>> newParts = newLinkedList();
        for (String part : parts) {
            String[] subParts = part.split("\\.");
            List<String> newSubParts = newLinkedList();
            for (String subPart : subParts) {
                int subPartLength = subPart.length();
                for (int i = 0; i < subPartLength; i++) {
                    if (i > 0 && !(i > 2 && i == (subPartLength - 1))) {
                        StringBuilder subPartBuilder = new StringBuilder(subPart);
                        subPartBuilder.setCharAt(i, hashChar);
                    }
                }
                newSubParts.add(subPart);
            }
            newParts.add(newSubParts);
        }

        return Joiner.on("@").join(newParts.stream().map(newPart -> Joiner.on(".").join(newPart)).collect(toList()));
    }

}
