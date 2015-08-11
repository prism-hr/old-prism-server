package com.zuehlke.pgadmissions.exceptions;

import static com.zuehlke.pgadmissions.utils.PrismConstants.SPACE;

public class PrismExceptionForJuan extends Exception {

    private static final long serialVersionUID = 1753525852969611818L;

    private static final String introductionForJuan = "FOR THE ATTENTION OF JUAN. If you're reading "
            + "this exception message, and you've found the part for you, don't be scared, it's here to "
            + "help you, really. Here's a few pointers that might get you up and running again quickly:";

    private static final String conclusionForJuan = "Some horrible stuff about where the problem actually "
            + "occurred in the Java code will now follow. Don't worry about that. It's for us. We'll look "
            + "into it if we need to ;-).";

    public PrismExceptionForJuan(String message) {
        super(introductionForJuan + SPACE + message + SPACE + conclusionForJuan);
    }

    public PrismExceptionForJuan(String message, Throwable cause) {
        super(introductionForJuan + SPACE + message + SPACE + conclusionForJuan, cause);
    }

}
