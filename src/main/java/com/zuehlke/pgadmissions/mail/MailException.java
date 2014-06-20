package com.zuehlke.pgadmissions.mail;

public class MailException extends RuntimeException {

    private static final long serialVersionUID = -4021614115790613028L;

    private final MailMessageDTO emailMessage;
    
    public MailException(final MailMessageDTO emailMessage) {
        super();
        this.emailMessage = emailMessage;
    }

    public MailException(final String message, final MailMessageDTO emailMessage) {
        super(message);
        this.emailMessage = emailMessage;
    }

    public MailException(final Throwable cause, final MailMessageDTO emailMessage) {
        super(cause);
        this.emailMessage = emailMessage;
    }

    public MailException(final String message, final Throwable cause, final MailMessageDTO emailMessage) {
        super(message, cause);
        this.emailMessage = emailMessage;
    }

	public MailMessageDTO getEmailMessage() {
        return emailMessage;
    }
}
