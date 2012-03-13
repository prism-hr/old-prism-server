package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.*;

import java.io.IOException;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.services.DocumentService;

public class FileManagementControllerTest {

	private DocumentService documentServiceMock;
	private FileManagementController controller;

	@Test
	@Ignore
	public void shouldCreateDocumentFromFileAndSave() throws IOException {
		/*MultipartFile multipartFileMock = EasyMock.createMock(MultipartFile.class);
		EasyMock.expect(multipartFileMock.getOriginalFilename()).andReturn("filename");
		EasyMock.expect(multipartFileMock.getContentType()).andReturn("ContentType");
		EasyMock.expect(multipartFileMock.getBytes()).andReturn("lala".getBytes());
		EasyMock.replay(multipartFileMock);

		Document document = new DocumentBuilder().id(1).toDocument();
		documentServiceMock.save(document);
		EasyMock.replay(documentServiceMock);
		controller.uploadFile(document, multipartFileMock);
		EasyMock.verify(documentServiceMock);
		assertEquals("filename", document.getFileName());
		assertEquals("ContentType", document.getContentType());
		assertEquals("lala", new String(document.getContent()));*/

	}
	
	@Test
	@Ignore
	public void shouldReturnCorrectModelAndView() throws IOException {
		/*MultipartFile multipartFileMock = EasyMock.createMock(MultipartFile.class);
		EasyMock.expect(multipartFileMock.getOriginalFilename()).andReturn("filename");
		EasyMock.expect(multipartFileMock.getContentType()).andReturn("ContentType");
		EasyMock.expect(multipartFileMock.getBytes()).andReturn("lala".getBytes());
		EasyMock.replay(multipartFileMock);

		Document document = new DocumentBuilder().id(1).toDocument();
		documentServiceMock.save(document);
		EasyMock.replay(documentServiceMock);
		ModelAndView modelAndView = controller.uploadFile(document, multipartFileMock);
		assertEquals("private/common/parts/supportingDocument", modelAndView.getViewName());
		assertEquals(document, modelAndView.getModel().get("document"));*/
		
	}
	@Before
	public void setup() {
		/*documentServiceMock = EasyMock.createMock(DocumentService.class);
		controller = new FileManagementController(documentServiceMock);*/
	}

}
