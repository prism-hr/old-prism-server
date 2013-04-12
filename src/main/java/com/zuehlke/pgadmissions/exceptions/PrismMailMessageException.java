package com.zuehlke.pgadmissions.exceptions;


public class PrismMailMessageException extends Exception {

	private static final long serialVersionUID = -3023145400071399808L;

	public PrismMailMessageException() {
		super();
	}
	
	public PrismMailMessageException(String message) {
		super(message);
	}
}
