package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.definitions.OauthProvider;
import com.zuehlke.pgadmissions.domain.user.UserAccount;
import com.zuehlke.pgadmissions.domain.user.UserAccountExternal;

public class UserAccountToLinkedinProfileUrlConverter extends DozerConverter<UserAccount, String> {

    public UserAccountToLinkedinProfileUrlConverter() {
        super(UserAccount.class, String.class);
    }

    @Override
    public String convertTo(UserAccount source, String destination) {
        if (source == null) return null;

        UserAccountExternal externalAccount = null;
        for (UserAccountExternal external : source.getExternalAccounts()) {
            if (external.getAccountType() == OauthProvider.LINKEDIN) {
                externalAccount = external;
            }
        }

        return externalAccount != null ? externalAccount.getAccountProfileUrl() : null;
    }

    @Override
    public UserAccount convertFrom(String source, UserAccount destination) {
        throw new UnsupportedOperationException();
    }

}
