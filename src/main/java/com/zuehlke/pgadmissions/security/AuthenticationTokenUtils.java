package com.zuehlke.pgadmissions.security;

import com.zuehlke.pgadmissions.domain.user.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthenticationTokenUtils {

    public static final String MAGIC_KEY = "obfuscate";

    public static String createToken(UserDetails userDetails) {
        /* Expires in one hour */
        long expires = System.currentTimeMillis() + 1000L * 60 * 60 * 5;

        User user = (User) userDetails;
        return user.getId() + ":" + expires + ":" + AuthenticationTokenUtils.computeSignature(user, expires);
    }

    public static String computeSignature(User user, long expires) {
        StringBuilder signatureBuilder = new StringBuilder();
        signatureBuilder.append(expires);
        signatureBuilder.append(":");
        signatureBuilder.append(user.getId());
        signatureBuilder.append(":");
        signatureBuilder.append(MAGIC_KEY); // TODO use database based magic key

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No MD5 algorithm available!");
        }

        return new String(Hex.encode(digest.digest(signatureBuilder.toString().getBytes())));
    }

    public static Integer getIdFromToken(String authToken) {
        if (null == authToken) {
            return null;
        }

        String[] parts = authToken.split(":");
        return Integer.parseInt(parts[0]);
    }

    public static boolean validateToken(String authToken, UserDetails userDetails) {
        String[] parts = authToken.split(":");
        long expires = Long.parseLong(parts[1]);
        String signature = parts[2];

        if (expires < System.currentTimeMillis()) {
            return false;
        }

        User user = (User) userDetails;
        return signature.equals(AuthenticationTokenUtils.computeSignature(user, expires));
    }
}
