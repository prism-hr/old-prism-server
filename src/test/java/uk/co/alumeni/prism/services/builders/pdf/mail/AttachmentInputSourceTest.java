package uk.co.alumeni.prism.services.builders.pdf.mail;

import static org.junit.Assert.assertNotSame;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class AttachmentInputSourceTest {

	@Test
	public void shouldReturnNewInputStreamOnEveryCall() throws IOException{
		byte[] bytes ="bytes".getBytes();
		AttachmentInputSource pdfAttachmentInputSource = new AttachmentInputSource("hi", bytes);
		InputStream inputStreamOnce = pdfAttachmentInputSource.getInputStream();
		InputStream inputStreamTwice = pdfAttachmentInputSource.getInputStream();
		assertNotSame(inputStreamOnce, inputStreamTwice);
	}
}
