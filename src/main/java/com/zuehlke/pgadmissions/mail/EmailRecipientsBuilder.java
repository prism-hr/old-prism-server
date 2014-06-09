package com.zuehlke.pgadmissions.mail;

import java.util.List;

import com.zuehlke.pgadmissions.domain.User;

public interface EmailRecipientsBuilder {

    List<User> build();
}
