package com.zuehlke.pgadmissions.services.builders.pdf.mail;

import org.springframework.stereotype.Component;

@Component
public class AttachmentInputSourceFactory {

	public AttachmentInputSource getAttachmentDataSource(String attachmentFilename, byte[] pdf) {
		return new AttachmentInputSource(attachmentFilename, pdf);
	}
	
}
