package com.zuehlke.pgadmissions.controllers;

import java.io.IOException;
import java.util.Date;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.ui.ModelMap;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.FundingType;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.dto.Funding;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.pagemodels.PageModel;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.validators.DocumentValidator;


public class FundingControllerTest {

	private ApplicationsService applicationsServiceMock;
	private FundingController fundingController;
	private RegisteredUser student;
	private DatePropertyEditor datePropertyEditorMock;
	private DocumentValidator documentValidatorMock;

	@Test
	public void shouldSaveNewFunding() throws IOException {

		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock);

		Funding funding = new Funding();
		funding.setFundingType(FundingType.SCHOLARSHIP);
		funding.setFundingDescription("my description");
		funding.setFundingValue("2000");
		funding.setFundingAwardDate(new Date());
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		ModelAndView modelAndView = fundingController.addFunding(funding, 2, null, mappingResult, new ModelMap());
		Assert.assertEquals("redirect:/application", modelAndView.getViewName());
		Assert.assertEquals(FundingType.SCHOLARSHIP, ((PageModel) modelAndView.getModel().get("model")).getApplicationForm()
				.getFundings().get(0).getType());
		Assert.assertNull(modelAndView.getModel().get("add"));
	}

	@Test
	public void shouldAddMessageIdFundingAddParameterProvided() throws IOException {

		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		applicationsServiceMock.save(form);
		EasyMock.replay(applicationsServiceMock);

		Funding funding = new Funding();
		funding.setFundingType(FundingType.SCHOLARSHIP);
		funding.setFundingDescription("my description");
		funding.setFundingValue("2000");
		funding.setFundingAwardDate(new Date());
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		ModelAndView modelAndView = fundingController.addFunding(funding, 2, "add", mappingResult, new ModelMap());
		Assert.assertEquals("redirect:/application", modelAndView.getViewName());
		Assert.assertEquals(FundingType.SCHOLARSHIP, ((PageModel) modelAndView.getModel().get("model")).getApplicationForm()
				.getFundings().get(0).getType());
		Assert.assertEquals("add", modelAndView.getModel().get("add"));
	}

	@Test
	public void shouldNotSaveNewFunding() throws IOException {
		ApplicationForm form = new ApplicationFormBuilder().id(2).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);
		Funding funding = new Funding();
		funding.setFundingType(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		ModelAndView modelAndView = fundingController.addFunding(funding, 2, null, mappingResult, new ModelMap());
		Assert.assertEquals("redirect:/application", modelAndView.getViewName());
	}

	@Test(expected = CannotUpdateApplicationException.class)
	public void shouldNotSaveNewFundingWhenAplicationIsSubmitted() throws IOException {

		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.SUBMITTED)
				.toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationById(2)).andReturn(form);
		EasyMock.replay(applicationsServiceMock);

		Funding funding = new Funding();
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(funding, "funding");
		fundingController.addFunding(funding, 2,null, mappingResult, new ModelMap());
	}
	
	@Test
	public void shouldBindPropertyEditors() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
		EasyMock.replay(binderMock);
		fundingController.registerPropertyEditors(binderMock);
		EasyMock.verify(binderMock);
	}
	
	@Before
	public void setUp(){
		
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);
		documentValidatorMock = EasyMock.createMock(DocumentValidator.class);
		fundingController = new FundingController(applicationsServiceMock, datePropertyEditorMock, documentValidatorMock);
		
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		student = new RegisteredUserBuilder().id(1).username("mark").email("mark@gmail.com").firstName("mark")
		.lastName("ham").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		authenticationToken.setDetails(student);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		}
	

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

}
