package com.zuehlke.pgadmissions.pdf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.InputStreamSource;

public class PdfAttachmentInputSource implements InputStreamSource{

	
	private final String attachmentFilename;
	private final byte[] pdf;
	
	public PdfAttachmentInputSource(String attachmentFilename, byte[] pdf){
		this.pdf = pdf;		
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