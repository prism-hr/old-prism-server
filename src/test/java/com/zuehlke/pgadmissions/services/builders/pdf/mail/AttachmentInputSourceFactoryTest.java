package com.zuehlke.pgadmissions.services.builders.pdf.mail;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

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
