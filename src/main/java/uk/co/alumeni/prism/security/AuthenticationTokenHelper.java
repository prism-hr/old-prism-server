package uk.co.alumeni.prism.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.services.SystemService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class AuthenticationTokenHelper {

    public static final long MINUTE = 1000L * 60;

    public static final long EXPIRY_PERIOD = 1000L * 20;

    public static final long RENEW_PERIOD = MINUTE * 5;

    @Autowired
    private SystemService systemService;

    public String createToken(UserDetails userDetails) {
        /* Expires in one hour */
        long created = System.currentTimeMillis();
        long expires = created + EXPIRY_PERIOD;

        User user = (User) userDetails;
        return user.getId() + ":" + created + ":" + expires + ":" + computeSignature(user, created, expires);
    }

    public String computeSignature(User user, long created, long expires) {
        StringBuilder signatureBuilder = new StringBuilder();
        signatureBuilder.append(created);
        signatureBuilder.append(":");
        signatureBuilder.append(expires);
        signatureBuilder.append(":");
        signatureBuilder.append(user.getId());
        signatureBuilder.append(":");
        signatureBuilder.append(systemService.getSystem().getCipherSalt());

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No MD5 algorithm available!");
        }

        return new String(Hex.encode(digest.digest(signatureBuilder.toString().getBytes())));
    }

    public Integer getIdFromToken(String authToken) {
        if (null == authToken) {
            return null;
        }

        String[] parts = authToken.split(":");
        return Integer.parseInt(parts[0]);
    }

    public TokenValidityStatus validateToken(String authToken, UserDetails userDetails) {
        User user = (User) userDetails;
        try {
            String[] parts = authToken.split(":");
            long created = Long.parseLong(parts[1]);
            long expires = Long.parseLong(parts[2]);
            String signature = parts[3];

            long now = System.currentTimeMillis();

            boolean valid = expires >= now && signature.equals(computeSignature(user, created, expires));
            String renewedToken = null;
            if (valid) {
                if (now > created + RENEW_PERIOD) {
                    renewedToken = createToken(user);
                }
            }
            return new TokenValidityStatus(valid, renewedToken);
        } catch (NumberFormatException e) {
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        return new TokenValidityStatus(false, null);
    }
}
