package uk.co.alumeni.prism.security;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.utils.PrismEncryptionUtils;

@Service
public class
        UserAuthenticationService {

    public boolean validateCredentials(User user, String password) {
        return password != null && user != null && user.isEnabled() && checkPassword(user, password);
    }

    private boolean checkPassword(User user, String providedPassword) {
        return StringUtils.equals(user.getUserAccount().getPassword(), PrismEncryptionUtils.getMD5(providedPassword))
                || checkTemporaryPassword(user, providedPassword);
    }

    private boolean checkTemporaryPassword(User user, String providedPassword) {
        DateTime temporaryPasswordExpiryTimestamp = user.getUserAccount().getTemporaryPasswordExpiryTimestamp();
        return temporaryPasswordExpiryTimestamp != null && new DateTime().isBefore(temporaryPasswordExpiryTimestamp)
                && StringUtils.equals(user.getUserAccount().getTemporaryPassword(), PrismEncryptionUtils.getMD5(providedPassword));
    }

}
