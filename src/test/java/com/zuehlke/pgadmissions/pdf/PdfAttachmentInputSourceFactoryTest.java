package com.zuehlke.pgadmissions.pdf;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class PdfAttachmentInputSourceFactoryTest {

	@Test
	public void shouldGenerateInputSourceWihtPdfByteArrayStream() throws IOException {
		byte[] pdf = "pdf".getBytes();
		PdfAttachmentInputSource source = new PdfAttachmentInputSourceFactory().getAttachmentDataSource("filename", pdf);

		InputStream inputStream = source.getInputStream();
		assertTrue(inputStream instanceof ByteArrayInputStream);

		byte[] bytes = new byte[pdf.length];
		((ByteArrayInputStream) inputStream).read(bytes);

		assertArrayEquals(pdf, bytes);
		assertEquals("filename", source.getAttachmentFilename());

	}

}
