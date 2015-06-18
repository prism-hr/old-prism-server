package com.zuehlke.pgadmissions.security;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Service
public class
        UserAuthenticationService {

    public boolean validateCredentials(User user, String password) {
        return password != null && user != null && user.isEnabled() && checkPassword(user, password);
    }

    private boolean checkPassword(User user, String providedPassword) {
        return StringUtils.equals(user.getUserAccount().getPassword(), EncryptionUtils.getMD5(providedPassword))
                || checkTemporaryPassword(user, providedPassword);
    }

    private boolean checkTemporaryPassword(User user, String providedPassword) {
        DateTime temporaryPasswordExpiryTimestamp = user.getUserAccount().getTemporaryPasswordExpiryTimestamp();
        return temporaryPasswordExpiryTimestamp != null && new DateTime().isBefore(temporaryPasswordExpiryTimestamp)
                && StringUtils.equals(user.getUserAccount().getTemporaryPassword(), EncryptionUtils.getMD5(providedPassword));
    }

}
