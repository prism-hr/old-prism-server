package com.zuehlke.pgadmissions.pdf;

import org.springframework.stereotype.Component;


@Component
public class PdfAttachmentInputSourceFactory {

	public PdfAttachmentInputSource getAttachmentDataSource(String attachmentFilename, byte[] pdf) {
		return new PdfAttachmentInputSource(attachmentFilename, pdf);
	}
}
