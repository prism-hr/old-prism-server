package com.zuehlke.pgadmissions.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.zuehlke.pgadmissions.domain.user.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Hex;

public class AuthenticationTokenUtils {

    public static final String MAGIC_KEY = "obfuscate";

    public static String createToken(UserDetails userDetails) {
        /* Expires in one hour */
        long expires = System.currentTimeMillis() + 1000L * 60 * 60 * 5;

        return userDetails.getUsername() + ":" + expires + ":" + AuthenticationTokenUtils.computeSignature(userDetails, expires);
    }

    public static String computeSignature(UserDetails userDetails, long expires) {
        User user = (User) userDetails;
        StringBuilder signatureBuilder = new StringBuilder();
        signatureBuilder.append(expires);
        signatureBuilder.append(":");
        signatureBuilder.append(user.getActivationCode());

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No MD5 algorithm available!");
        }

        return new String(Hex.encode(digest.digest(signatureBuilder.toString().getBytes())));
    }

    public static String getUserNameFromToken(String authToken) {
        if (null == authToken) {
            return null;
        }

        String[] parts = authToken.split(":");
        return parts[0];
    }

    public static boolean validateToken(String authToken, UserDetails userDetails) {
        String[] parts = authToken.split(":");
        long expires = Long.parseLong(parts[1]);
        String signature = parts[2];

        if (expires < System.currentTimeMillis()) {
            return false;
        }

        return signature.equals(AuthenticationTokenUtils.computeSignature(userDetails, expires));
    }
}
