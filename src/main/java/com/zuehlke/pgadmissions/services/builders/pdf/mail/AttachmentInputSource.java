package com.zuehlke.pgadmissions.services.builders.pdf.mail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.springframework.core.io.InputStreamSource;

public class AttachmentInputSource implements InputStreamSource {

	private final String attachmentFilename;
	
	private byte[] pdf = null;
	
	public AttachmentInputSource(String attachmentFilename, byte[] pdf) {
		if (pdf != null) {
		    this.pdf = Arrays.copyOf(pdf, pdf.length);
		}
		this.attachmentFilename = attachmentFilename;
	}
	
	@Override
	public InputStream getInputStream() throws IOException {	
		return new ByteArrayInputStream(pdf);
	}

	public String getAttachmentFilename() {
		return attachmentFilename;
	}
	
}
