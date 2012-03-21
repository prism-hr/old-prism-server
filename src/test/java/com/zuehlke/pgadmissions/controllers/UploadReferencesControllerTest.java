package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.*;

import java.io.IOException;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.domain.enums.FundingType;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.RefereeAlreadyUploadedReference;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.pagemodels.PageModel;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.validators.DocumentValidator;

public class UploadReferencesControllerTest {

	UploadReferencesController controller;
	private ApplicationsService applicationsServiceMock;
	private RegisteredUser currentUser;
	private DocumentValidator documentValidatorMock;
	private BindingResult errors;
	private Document document;
	
	@Test
	public void shouldReturnReferencesPageIfLinkIsCorrect(){
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		Referee referee = new RefereeBuilder().refereeId(1).application(form).activationCode("1234").toReferee();
		EasyMock.replay(applicationsServiceMock);
		ModelAndView modelAndView = controller.getReferencesPage(referee, "1234");
		EasyMock.verify(applicationsServiceMock);
		assertNull(((ApplicationPageModel) modelAndView.getModel().get("model")).getMessage());
		assertEquals(form, ((ApplicationPageModel) modelAndView.getModel().get("model")).getApplicationForm());
		assertEquals(referee, ((ApplicationPageModel) modelAndView.getModel().get("model")).getReferee());
		assertEquals("private/referees/upload_references", modelAndView.getViewName());
	}
	
	@Test
	public void shouldNotReturnReferencesPageIfAvtivationCodeIsWrong(){
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		Referee referee = new RefereeBuilder().refereeId(1).application(form).activationCode("1234").toReferee();
		EasyMock.replay(applicationsServiceMock);
		ModelAndView modelAndView = controller.getReferencesPage(referee, "467");
		EasyMock.verify(applicationsServiceMock);
		assertEquals("The link you provided is incorrect please try again", ((ApplicationPageModel) modelAndView.getModel().get("model")).getMessage());
		assertEquals("private/referees/upload_references", modelAndView.getViewName());
	}
	
	@Test
	public void shouldNotReturnReferencesPageIfApplicationIdIsWrong(){
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		Referee referee = new RefereeBuilder().refereeId(1).application(null).activationCode("1234").toReferee();
		EasyMock.replay(applicationsServiceMock);
		ModelAndView modelAndView = controller.getReferencesPage(referee, "1234");
		EasyMock.verify(applicationsServiceMock);
		assertEquals("The link you provided is incorrect please try again", ((ApplicationPageModel) modelAndView.getModel().get("model")).getMessage());
		assertEquals("private/referees/upload_references", modelAndView.getViewName());
	}
	
	@Test
	public void shouldCreateDocumentFromFile() throws IOException {
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		Referee referee = new RefereeBuilder().refereeId(1).comment("i recommend the applicant").application(form).activationCode("1234").toReferee();
		MultipartFile multipartFileMock = EasyMock.createMock(MultipartFile.class);
		EasyMock.expect(multipartFileMock.getOriginalFilename()).andReturn("filename");
		EasyMock.expect(multipartFileMock.getContentType()).andReturn("ContentType");
		EasyMock.expect(multipartFileMock.getBytes()).andReturn("lala".getBytes());
		EasyMock.replay(multipartFileMock);
		applicationsServiceMock.saveReferee(referee);
		EasyMock.replay(applicationsServiceMock);
		controller.submitReference(referee,  multipartFileMock);

		EasyMock.verify(applicationsServiceMock);
		assertNotNull(referee.getDocument());
		Document document = referee.getDocument();
		assertEquals("filename", document.getFileName());
		assertEquals("ContentType", document.getContentType());
		assertEquals("lala", new String(document.getContent()));
		assertEquals("i recommend the applicant", referee.getComment());
	}
	
	@Test(expected = RefereeAlreadyUploadedReference.class)
	public void shouldThrowExceptionWhenRefereealreadyUploadedAReference() throws IOException{
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		Referee referee = new RefereeBuilder().refereeId(1).comment("i recommend the applicant").document(new Document()).application(form).activationCode("1234").toReferee();
		MultipartFile multipartFileMock = EasyMock.createMock(MultipartFile.class);
		controller.submitReference(referee,  multipartFileMock);
	}

	
	@Before
	public void setup() {
		errors = EasyMock.createMock(BindingResult.class);
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		documentValidatorMock = EasyMock.createMock(DocumentValidator.class);
		document = new DocumentBuilder().id(1).toDocument();
		controller = new UploadReferencesController(applicationsServiceMock, documentValidatorMock);

		currentUser = new RegisteredUserBuilder().id(1).toUser();
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);

		authenticationToken.setDetails(currentUser);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
	}

	
}
