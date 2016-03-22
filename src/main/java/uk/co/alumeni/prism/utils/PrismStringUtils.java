package uk.co.alumeni.prism.utils;

import static com.google.common.collect.Lists.newLinkedList;
import static uk.co.alumeni.prism.PrismConstants.ASTERISK;
import static uk.co.alumeni.prism.PrismConstants.AT;

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

    public static String getObfuscatedEmail(String email) {
        char hashChar = ASTERISK.charAt(0);
        String[] emailParts = email.split("@");

        List<String> newNameParts = newLinkedList();
        String[] nameParts = emailParts[0].split("\\.");
        for (String namePart : nameParts) {
            int subPartLength = namePart.length();
            for (int i = 0; i < subPartLength; i++) {
                if (i > 0 && !(i > 2 && i == (subPartLength - 1))) {
                    StringBuilder addressPartBuilder = new StringBuilder(namePart);
                    addressPartBuilder.setCharAt(i, hashChar);
                    namePart = addressPartBuilder.toString();
                }
            }
            newNameParts.add(namePart);
        }

        return Joiner.on(".").join(newNameParts) + AT + emailParts[1];
    }

}
