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

    public MailException(final MailMessageDTO emailMessage, final Throwable cause) {
        super(cause);
        this.emailMessage = emailMessage;
    }

    public MailException(final String message, final MailMessageDTO emailMessage, final Throwable cause) {
        super(message, cause);
        this.emailMessage = emailMessage;
    }

	public MailMessageDTO getEmailMessage() {
        return emailMessage;
    }
}
