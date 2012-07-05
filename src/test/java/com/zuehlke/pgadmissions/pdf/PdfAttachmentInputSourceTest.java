package com.zuehlke.pgadmissions.pdf;

import static org.junit.Assert.assertNotSame;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class PdfAttachmentInputSourceTest {

	@Test
	public void shouldReturnNewInputStreamOnEveryCall() throws IOException{
		byte[] bytes ="bytes".getBytes();
		PdfAttachmentInputSource pdfAttachmentInputSource = new PdfAttachmentInputSource("hi", bytes);
		InputStream inputStreamOnce = pdfAttachmentInputSource.getInputStream();
		InputStream inputStreamTwice = pdfAttachmentInputSource.getInputStream();
		assertNotSame(inputStreamOnce, inputStreamTwice);
	}
}
