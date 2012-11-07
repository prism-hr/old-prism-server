package com.zuehlke.pgadmissions.exceptions;

public class DocumentExportException extends Exception{
	private static final long serialVersionUID = 1L;

	public DocumentExportException() {
		super();
	}
	
	public DocumentExportException(String message) {
		super(message);
	}

	public DocumentExportException(String message, Throwable cause) {
		super(message, cause);
	}

}
