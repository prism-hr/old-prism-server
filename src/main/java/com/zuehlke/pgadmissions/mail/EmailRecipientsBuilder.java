package com.zuehlke.pgadmissions.mail;

import java.util.List;

import com.zuehlke.pgadmissions.domain.RegisteredUser;

public interface EmailRecipientsBuilder {

    List<RegisteredUser> build();
}
