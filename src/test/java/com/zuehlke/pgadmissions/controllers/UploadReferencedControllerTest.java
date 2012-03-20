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
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.pagemodels.PageModel;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.validators.DocumentValidator;

public class UploadReferencedControllerTest {

	UploadReferencesController controller;
	private ApplicationsService applicationsServiceMock;
	private RegisteredUser currentUser;
	private DocumentValidator documentValidatorMock;
	private BindingResult errors;
	private Document document;
	
	
	@Test
	public void shouldNotReturnReferencesPageIfAvtivationCodeIsWrong(){
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		Referee referee = new RefereeBuilder().refereeId(1).application(form).activationCode("1234").toReferee();
		EasyMock.expect(applicationsServiceMock.getRefereeById(1)).andReturn(referee);
		EasyMock.expect(applicationsServiceMock.getApplicationById(1)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);
		ModelAndView modelAndView = controller.getReferencesPage(referee.getId(), "467", 1);
		EasyMock.verify(applicationsServiceMock);
		assertEquals("The link you provided is incorrect please try again", ((ApplicationPageModel) modelAndView.getModel().get("model")).getMessage());
		assertEquals("private/referees/upload_references", modelAndView.getViewName());
	}
	
	@Test
	public void shouldNotReturnReferencesPageIfApplicationIdIsWrong(){
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		Referee referee = new RefereeBuilder().refereeId(1).application(form).activationCode("1234").toReferee();
		EasyMock.expect(applicationsServiceMock.getRefereeById(1)).andReturn(referee);
		EasyMock.expect(applicationsServiceMock.getApplicationById(null)).andReturn(null);
		EasyMock.replay(applicationsServiceMock);
		ModelAndView modelAndView = controller.getReferencesPage(referee.getId(), "1234", null);
		EasyMock.verify(applicationsServiceMock);
		assertEquals("The link you provided is incorrect please try again", ((ApplicationPageModel) modelAndView.getModel().get("model")).getMessage());
		assertEquals("private/referees/upload_references", modelAndView.getViewName());
	}
	
	
//	@Ignore
//	@Test
//	public void shouldCreateDocumentFromFile() throws IOException {
//		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
//		Referee referee = new RefereeBuilder().refereeId(1).comment("i recommend the applicant").application(form).activationCode("1234").toReferee();
//		MultipartFile multipartFileMock = EasyMock.createMock(MultipartFile.class);
//		EasyMock.expect(multipartFileMock.getOriginalFilename()).andReturn("filename");
//		EasyMock.expect(multipartFileMock.getContentType()).andReturn("ContentType");
//		EasyMock.expect(multipartFileMock.getBytes()).andReturn("lala".getBytes());
//		EasyMock.replay(multipartFileMock);
//		EasyMock.expect(applicationsServiceMock.getRefereeById(1)).andReturn(referee);
//		applicationsServiceMock.saveReferee(referee);
//		EasyMock.replay(applicationsServiceMock);
//		controller.submitReference(referee,  multipartFileMock);
//
//		EasyMock.verify(applicationsServiceMock);
//		assertNotNull(referee.getDocument());
//		Document document = referee.getDocument();
//		assertEquals("filename", document.getFileName());
//		assertEquals("ContentType", document.getContentType());
//		assertEquals("lala", new String(document.getContent()));
//		assertEquals("i recommend the applicant", referee.getComment());
//
//	}

	
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
