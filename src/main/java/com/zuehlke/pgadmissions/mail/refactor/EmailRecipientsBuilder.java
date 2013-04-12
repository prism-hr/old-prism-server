package com.zuehlke.pgadmissions.mail.refactor;

import java.util.List;

import com.zuehlke.pgadmissions.domain.RegisteredUser;

public interface EmailRecipientsBuilder {

    List<RegisteredUser> build();
}
