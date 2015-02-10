package com.zuehlke.pgadmissions.services.builders.pdf.mail;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class AttachmentInputSourceFactoryTest {

	@Test
	public void shouldGenerateInputSourceWithPdfByteArrayStream() throws IOException {
		byte[] pdf = "pdf".getBytes();
		AttachmentInputSource source = new AttachmentInputSourceFactory().getAttachmentDataSource("filename", pdf);

		InputStream inputStream = source.getInputStream();
		assertTrue(inputStream instanceof ByteArrayInputStream);

		byte[] bytes = new byte[pdf.length];
		inputStream.read(bytes);

		assertArrayEquals(pdf, bytes);
		assertEquals("filename", source.getAttachmentFilename());

	}

}
