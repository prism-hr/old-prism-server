package com.zuehlke.pgadmissions.mail.refactor;

import java.util.Collection;

import com.zuehlke.pgadmissions.domain.RegisteredUser;

public interface EmailRecipientsBuilder {

    Collection<RegisteredUser> build();
}
