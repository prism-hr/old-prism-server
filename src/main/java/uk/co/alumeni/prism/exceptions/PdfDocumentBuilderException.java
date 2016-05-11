package uk.co.alumeni.prism.exceptions;

public class PdfDocumentBuilderException extends RuntimeException {

	private static final long serialVersionUID = -7436743296279853075L;

	public PdfDocumentBuilderException() {
		super();
	}

	public PdfDocumentBuilderException(String message, Throwable cause) {
		super(message, cause);
	}

	public PdfDocumentBuilderException(String message) {
		super(message);
	}

	public PdfDocumentBuilderException(Throwable cause) {
		super(cause);
	}
}
