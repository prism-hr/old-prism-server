package com.zuehlke.pgadmissions.exceptions;

import com.zuehlke.pgadmissions.mail.refactor.PrismEmailMessage;

public class PrismMailMessageException extends RuntimeException {

    private static final long serialVersionUID = -4021614115790613028L;

    private final PrismEmailMessage emailMessage;
    
    public PrismMailMessageException(final PrismEmailMessage emailMessage) {
        super();
        this.emailMessage = emailMessage;
    }

    public PrismMailMessageException(final String message, final PrismEmailMessage emailMessage) {
        super(message);
        this.emailMessage = emailMessage;
    }

    public PrismMailMessageException(final Throwable cause, final PrismEmailMessage emailMessage) {
        super(cause);
        this.emailMessage = emailMessage;
    }

    public PrismMailMessageException(final String message, final Throwable cause, final PrismEmailMessage emailMessage) {
        super(message, cause);
        this.emailMessage = emailMessage;
    }

	public PrismEmailMessage getEmailMessage() {
        return emailMessage;
    }
}
