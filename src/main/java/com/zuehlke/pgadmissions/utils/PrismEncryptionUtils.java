package com.zuehlke.pgadmissions.utils;

import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;

public class PrismEncryptionUtils {

    private static final int PASSWORD_LENGTH = 8;

    private static final char[] PASSWORD_CHARACTERS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
            'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2',
            '3', '4', '5', '6', '7', '8', '9' };

    public static String getMD5(String string) {
        return DigestUtils.md5Hex(string);
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    public static String getTemporaryPassword() {
        return RandomStringUtils.random(PASSWORD_LENGTH, PASSWORD_CHARACTERS);
    }

}
