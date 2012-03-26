package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.Reference;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.validators.DocumentValidator;

public class ReferencesControllerTest {

	ReferencesController controller;
	private RefereeService refereeServiceMock;
	private RegisteredUser currentUser;
	private DocumentValidator documentValidatorMock;
	private BindingResult errors;
	private Document document;

	@Test
	public void shouldGetRefereeFromService() throws IOException {
		
		Referee referee = new RefereeBuilder().id(1).toReferee();
		EasyMock.expect(refereeServiceMock.getRefereeById(1)).andReturn(referee);
		EasyMock.replay(refereeServiceMock);
		Referee returnedReferee = controller.getReferee(1);
		assertEquals(referee, returnedReferee);		
		
	}

	@Test(expected=ResourceNotFoundException.class)
	public void shoudThrowResourceNotFoundExceptionIfRefereeIsNull() throws IOException {
		
		EasyMock.expect(refereeServiceMock.getRefereeById(1)).andReturn(null);
		EasyMock.replay(refereeServiceMock);
		controller.getReferee(1);
	}
	
	@Test
	public void shouldSetNewReferenceOnServiceIfNull() throws IOException {
		Referee referee = new RefereeBuilder().id(1).toReferee();
		EasyMock.expect(refereeServiceMock.getRefereeById(1)).andReturn(referee);
		EasyMock.replay(refereeServiceMock);
		controller.getReferee(1);
		assertNotNull(referee.getReference());
		assertNull(referee.getReference().getId());
	}


	@Test
	public void shouldRedirectToSuccessViewAfterSuccesfultSubmit() throws IOException {
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		Referee referee = new RefereeBuilder().id(1).application(form).reference(new ReferenceBuilder().comment("hi").toReference()).activationCode("1234").toReferee();
		MultipartFile multipartFileMock = EasyMock.createMock(MultipartFile.class);
		EasyMock.expect(multipartFileMock.getOriginalFilename()).andReturn("");
		EasyMock.replay(multipartFileMock);

		refereeServiceMock.save(referee);
		EasyMock.replay(refereeServiceMock);

		ModelAndView modelAndView = controller.submitReference(referee, multipartFileMock);
		EasyMock.verify(refereeServiceMock);
		assertEquals("redirect:/addReferences/referenceuploaded", modelAndView.getViewName());

	}

	@Test
	public void shouldCreateDocumentFromFileAndValidate() throws IOException {
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		Referee referee = new RefereeBuilder().id(1).application(form).reference(new Reference()).activationCode("1234").toReferee();
		MultipartFile multipartFileMock = EasyMock.createMock(MultipartFile.class);
		EasyMock.expect(multipartFileMock.getOriginalFilename()).andReturn("filename");
		EasyMock.expect(multipartFileMock.getContentType()).andReturn("ContentType");
		EasyMock.expect(multipartFileMock.getBytes()).andReturn("lala".getBytes());
		EasyMock.replay(multipartFileMock);

		refereeServiceMock.save(referee);
		documentValidatorMock.validate(document, errors);
		EasyMock.expect(errors.hasFieldErrors("fileName")).andReturn(false);
		EasyMock.replay(refereeServiceMock, documentValidatorMock, errors);

		ModelAndView modelAndView = controller.submitReference(referee, multipartFileMock);

		assertEquals("redirect:/addReferences/referenceuploaded", modelAndView.getViewName());
		EasyMock.verify(refereeServiceMock, documentValidatorMock);
		assertNotNull(referee.getReference().getDocument());
		Document document = referee.getReference().getDocument();

		assertEquals("filename", document.getFileName());
		assertEquals("ContentType", document.getContentType());
		assertEquals("lala", new String(document.getContent()));
		assertEquals(DocumentType.REFERENCE, document.getType());

	}

	@Test
	public void shouldNotReturnToViewWithErrorsIfDocumentValidationFails() throws IOException {
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		Referee referee = new RefereeBuilder().id(1).application(form).activationCode("1234").toReferee();
		MultipartFile multipartFileMock = EasyMock.createMock(MultipartFile.class);
		EasyMock.expect(multipartFileMock.getOriginalFilename()).andReturn("filename");
		EasyMock.expect(multipartFileMock.getContentType()).andReturn("ContentType");
		EasyMock.expect(multipartFileMock.getBytes()).andReturn("lala".getBytes());
		EasyMock.replay(multipartFileMock);


		documentValidatorMock.validate(document, errors);
		EasyMock.expect(errors.hasFieldErrors("fileName")).andReturn(true);
		FieldError fieldError = EasyMock.createMock(FieldError.class);
		EasyMock.expect(fieldError.getCode()).andReturn("abc");
		EasyMock.expect(errors.getFieldError("fileName")).andReturn(fieldError);
		EasyMock.replay(refereeServiceMock, documentValidatorMock, fieldError, errors);

		ModelAndView modelAndView = controller.submitReference(referee, multipartFileMock);
		ApplicationPageModel model = (ApplicationPageModel) modelAndView.getModel().get("model");
		assertEquals("private/referees/upload_references", modelAndView.getViewName());
		assertEquals("abc", model.getUploadErrorCode());
		assertEquals(referee, model.getReferee());

		EasyMock.verify(refereeServiceMock, documentValidatorMock);

	}

	@Test
	public void shouldAddErrorMessageAndNotSaveIfNeitherDocumentOrCommentProvided() throws IOException {
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		Referee referee = new RefereeBuilder().id(1).application(form).reference(new Reference()).activationCode("1234").toReferee();
		MultipartFile multipartFileMock = EasyMock.createMock(MultipartFile.class);
		EasyMock.expect(multipartFileMock.getOriginalFilename()).andReturn("");
		EasyMock.replay(multipartFileMock, refereeServiceMock);
		ModelAndView modelAndView = controller.submitReference(referee, multipartFileMock);
		ApplicationPageModel model = (ApplicationPageModel) modelAndView.getModel().get("model");
		assertEquals("reference.missing", model.getGlobalErrorCodes().get(0));

		EasyMock.verify(refereeServiceMock);

	}
	
	@Test
	public void shouldSetCommentToNullIfEmptyString() throws IOException {
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		Referee referee = new RefereeBuilder().id(1).application(form).reference(new Reference()).activationCode("1234").toReferee();
		MultipartFile multipartFileMock = EasyMock.createMock(MultipartFile.class);
		EasyMock.expect(multipartFileMock.getOriginalFilename()).andReturn("");
		EasyMock.replay(multipartFileMock, refereeServiceMock);
		ModelAndView modelAndView = controller.submitReference(referee, multipartFileMock);
		ApplicationPageModel model = (ApplicationPageModel) modelAndView.getModel().get("model");
		assertEquals("reference.missing", model.getGlobalErrorCodes().get(0));
		assertNull(referee.getReference().getComment());
		EasyMock.verify(refereeServiceMock);

	}
	



	@Before
	public void setup() {
		errors = EasyMock.createMock(BindingResult.class);
		refereeServiceMock = EasyMock.createMock(RefereeService.class);
		documentValidatorMock = EasyMock.createMock(DocumentValidator.class);
		document = new DocumentBuilder().id(1).toDocument();
		

		controller = new ReferencesController(refereeServiceMock,  documentValidatorMock) {

			@Override
			BindingResult newErrors(Document document) {
				return errors;
			}

			@Override
			Document newDocument() {
				return document;
			}

		};

		currentUser = new RegisteredUserBuilder().id(1).toUser();
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);

		authenticationToken.setDetails(currentUser);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
	}

}
