package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.Reference;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.ReferenceService;

public class FileDownloadControllerTest {
	private DocumentService documentServiceMock;
	private FileDownloadController controller;
	private RegisteredUser currentUser;
	private ReferenceService referenceServiceMock;
	private RefereeService refereeServiceMock;

	@Test
	public void shouldGetApplicationFormDocumentFromServiceAndWriteContentToResponse() throws IOException {

		Document document = new DocumentBuilder().content("aaaa".getBytes()).id(1).toDocument();
		EasyMock.expect(documentServiceMock.getDocumentById(1)).andReturn(document);
		EasyMock.replay(documentServiceMock);

		HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ServletOutputStream servletOutputStream = new ServletOutputStream() {

			@Override
			public void write(int b) throws IOException {
				byteArrayOutputStream.write(b);
			}
		};

		EasyMock.expect(responseMock.getOutputStream()).andReturn(servletOutputStream);
		responseMock.setHeader("Expires", "0");
		responseMock.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		responseMock.setHeader("Pragma", "public");
		responseMock.setHeader("Content-Disposition", "inline; filename=\"" + document.getFileName() + "\"");
		responseMock.setContentType("application/pdf");
		responseMock.setContentLength(document.getContent().length);
		EasyMock.replay(responseMock);
		controller.downloadApplicationDocument(1, responseMock);

		EasyMock.verify(documentServiceMock, responseMock);

		byte[] byteArray = byteArrayOutputStream.toByteArray();
		assertEquals("aaaa", new String(byteArray));
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfDocumentTypeIsReference() throws IOException {
		Document document = new DocumentBuilder().type(DocumentType.REFERENCE).content("aaaa".getBytes()).id(1).toDocument();
		EasyMock.expect(documentServiceMock.getDocumentById(1)).andReturn(document);
		EasyMock.replay(documentServiceMock);

		controller.downloadApplicationDocument(1, new MockHttpServletResponse());
	}

	@Test
	public void shouldNotThrowResourceNotFoundExceptionIfDocumentTypeIsProofOfAward() throws IOException {

		Document document = new DocumentBuilder().type(DocumentType.PROOF_OF_AWARD).content("aaaa".getBytes()).id(1).toDocument();

		EasyMock.expect(documentServiceMock.getDocumentById(1)).andReturn(document);
		EasyMock.replay(documentServiceMock);

		controller.downloadApplicationDocument(1, new MockHttpServletResponse());
	}

	@Test
	public void shouldGetReferenceDocumentFromServiceAndWriteContentToResponse() throws IOException {

		ApplicationForm applicationForm = new ApplicationFormBuilder().id(3).toApplicationForm();
		Document document = new DocumentBuilder().content("aaaa".getBytes()).id(101).toDocument();
		Reference reference = new ReferenceBuilder().id(1).referee(new RefereeBuilder().application(applicationForm).toReferee()).document(document)
				.toReference();
		HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);

		EasyMock.expect(referenceServiceMock.getReferenceById(1)).andReturn(reference);
		EasyMock.replay(referenceServiceMock);

		EasyMock.expect(currentUser.isInRole(Authority.APPLICANT)).andReturn(false);
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);

		EasyMock.replay(currentUser);

		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ServletOutputStream servletOutputStream = new ServletOutputStream() {

			@Override
			public void write(int b) throws IOException {
				byteArrayOutputStream.write(b);
			}
		};

		EasyMock.expect(responseMock.getOutputStream()).andReturn(servletOutputStream);
		responseMock.setHeader("Expires", "0");
		responseMock.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		responseMock.setHeader("Pragma", "public");
		responseMock.setHeader("Content-Disposition", "inline; filename=\"" + document.getFileName() + "\"");
		responseMock.setContentType("application/pdf");
		responseMock.setContentLength(document.getContent().length);
		EasyMock.replay(responseMock);
		controller.downloadReferenceDocument(1, responseMock);

		EasyMock.verify(referenceServiceMock, responseMock);

		byte[] byteArray = byteArrayOutputStream.toByteArray();
		assertEquals("aaaa", new String(byteArray));
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfReferenceDoesNotHaveDocument() throws IOException {

		Reference reference = new ReferenceBuilder().id(1).toReference();
		HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
		EasyMock.expect(referenceServiceMock.getReferenceById(1)).andReturn(reference);
		EasyMock.replay(referenceServiceMock);
		controller.downloadReferenceDocument(1, responseMock);

		EasyMock.verify(referenceServiceMock);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfReferenceDoesNotExistt() throws IOException {

		HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
		EasyMock.expect(referenceServiceMock.getReferenceById(1)).andReturn(null);
		EasyMock.replay(referenceServiceMock);
		controller.downloadReferenceDocument(1, responseMock);

		EasyMock.verify(referenceServiceMock);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionUserCannotSeeApplicationToWhichReferenceApplies() throws IOException {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(3).toApplicationForm();
		Document document = new DocumentBuilder().content("aaaa".getBytes()).id(101).toDocument();
		Reference reference = new ReferenceBuilder().id(1).referee(new RefereeBuilder().application(applicationForm).toReferee()).document(document)
				.toReference();
		HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
		EasyMock.expect(referenceServiceMock.getReferenceById(1)).andReturn(reference);
		EasyMock.replay(referenceServiceMock);
		EasyMock.expect(currentUser.isInRole(Authority.APPLICANT)).andReturn(false);
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(false);
		EasyMock.replay(currentUser);

		controller.downloadReferenceDocument(1, responseMock);

		EasyMock.verify(referenceServiceMock);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionUseIsApplicant() throws IOException {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(3).toApplicationForm();
		Document document = new DocumentBuilder().content("aaaa".getBytes()).id(101).toDocument();
		Reference reference = new ReferenceBuilder().id(1).referee(new RefereeBuilder().application(applicationForm).toReferee()).document(document)
				.toReference();
		HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
		EasyMock.expect(referenceServiceMock.getReferenceById(1)).andReturn(reference);
		EasyMock.replay(referenceServiceMock);
		EasyMock.expect(currentUser.isInRole(Authority.APPLICANT)).andReturn(true);
		EasyMock.replay(currentUser);

		controller.downloadReferenceDocument(1, responseMock);

		EasyMock.verify(referenceServiceMock);
	}

	@Test
	public void shouldGetReferenceDocumentFromServiceByActivationCodeAndWriteContentToResponse() throws IOException {
		Document document = new DocumentBuilder().content("aaaa".getBytes()).id(101).toDocument();
		Reference reference = new ReferenceBuilder().id(1).document(document).toReference();
		Referee referee = new RefereeBuilder().id(6).reference(reference).toReferee();

		EasyMock.expect(refereeServiceMock.getRefereeByActivationCode("abc")).andReturn(referee);
		EasyMock.replay(refereeServiceMock);

		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ServletOutputStream servletOutputStream = new ServletOutputStream() {

			@Override
			public void write(int b) throws IOException {
				byteArrayOutputStream.write(b);
			}
		};

		HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
		EasyMock.expect(responseMock.getOutputStream()).andReturn(servletOutputStream);

		responseMock.setHeader("Expires", "0");
		responseMock.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		responseMock.setHeader("Pragma", "public");
		responseMock.setHeader("Content-Disposition", "inline; filename=\"" + document.getFileName() + "\"");
		responseMock.setContentType("application/pdf");
		responseMock.setContentLength(document.getContent().length);
		EasyMock.replay(responseMock);

		controller.downloadReferenceDocumentForReferee("abc", responseMock);

		EasyMock.verify(refereeServiceMock, responseMock);

		byte[] byteArray = byteArrayOutputStream.toByteArray();
		assertEquals("aaaa", new String(byteArray));
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfNoRefereForActivationCode() throws IOException {

		EasyMock.expect(refereeServiceMock.getRefereeByActivationCode("abc")).andReturn(null);
		EasyMock.replay(refereeServiceMock);
		HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
		controller.downloadReferenceDocumentForReferee("abc", responseMock);

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfReferessReferenceDoesNotHaveDocument() throws IOException {
		Reference reference = new ReferenceBuilder().id(1).toReference();
		Referee referee = new RefereeBuilder().id(6).reference(reference).toReferee();

		EasyMock.expect(refereeServiceMock.getRefereeByActivationCode("abc")).andReturn(referee);
		EasyMock.replay(refereeServiceMock);

		HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);

		controller.downloadReferenceDocumentForReferee("abc", responseMock);

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfRefereeDoesNotHaveReference() throws IOException {

		Referee referee = new RefereeBuilder().id(6).toReferee();

		EasyMock.expect(refereeServiceMock.getRefereeByActivationCode("abc")).andReturn(referee);
		EasyMock.replay(refereeServiceMock);

		HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);

		controller.downloadReferenceDocumentForReferee("abc", responseMock);

	}

	@Before
	public void setup() {

		documentServiceMock = EasyMock.createMock(DocumentService.class);
		referenceServiceMock = EasyMock.createMock(ReferenceService.class);
		refereeServiceMock = EasyMock.createMock(RefereeService.class);
		controller = new FileDownloadController(documentServiceMock, referenceServiceMock, refereeServiceMock);
		currentUser = EasyMock.createMock(RegisteredUser.class);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);

		authenticationToken.setDetails(currentUser);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
