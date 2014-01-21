package com.zuehlke.pgadmissions.controllers;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.OpportunitiesService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.OpportunityRequestValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class CreateNewOpportunityControllerTest {

	@Mock
	@InjectIntoByType
	private UserService userService;

	@Mock
	@InjectIntoByType
	private DomicileService domicileService;

	@Mock
	@InjectIntoByType
	private QualificationInstitutionDAO qualificationInstitutionDAO;

	@Mock
	@InjectIntoByType
	private DomicilePropertyEditor domicilePropertyEditor;

	@Mock
	@InjectIntoByType
	private OpportunityRequestValidator opportunityRequestValidator;

	@Mock
	@InjectIntoByType
	private OpportunitiesService opportunitiesService;

	@TestedObject
	private CreateNewOpportunityController controller = new CreateNewOpportunityController();

	@Test
	public void shouldReturnAllEnabledCountries() {
		List<Domicile> domicileList = Lists.newArrayList();
		EasyMock.expect(domicileService.getAllEnabledDomicilesExceptAlternateValues()).andReturn(domicileList);

		replay();
		List<Domicile> returnedList = controller.getAllEnabledDomiciles();
		verify();

		assertSame(domicileList, returnedList);
	}

	@Test
	public void shouldCreateEmptyOpportunityRequest() {
		OpportunityRequest opportunityRequest = controller.getOpportunityRequest();
		assertNotNull(opportunityRequest);
		assertNotNull(opportunityRequest.getAuthor());
	}

	@Test
	public void shouldGetEmptyQualificationInstitution() {
		assertTrue(controller.getEmptyQualificationInstitution().isEmpty());
	}

	@Test
	public void shouldRegisterPropertyEditors() {
		WebDataBinder dataBinder = EasyMockUnitils.createMock(WebDataBinder.class);
		dataBinder.setValidator(opportunityRequestValidator);
		dataBinder.registerCustomEditor(Domicile.class, domicilePropertyEditor);

		replay();
		controller.registerPropertyEditors(dataBinder);
		verify();
	}

	@Test
	public void shouldGetNewOpportunityPage() {
		HttpServletRequest request = new MockHttpServletRequest();

		String result = controller.getNewOpportunityPage(request);
		assertEquals(CreateNewOpportunityController.LOGIN_PAGE, result);
		assertTrue((Boolean) request.getAttribute(CreateNewOpportunityController.CLICKED_ON_CREATE_OPPORTUNITY));
	}

	@Test
	public void shouldPostOpportunityRequest() {
		OpportunityRequest opportunityRequest = new OpportunityRequest();
		BindingResult bindingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");
		Model model = new ExtendedModelMap();
		HttpServletRequest request = new MockHttpServletRequest();

		opportunitiesService.createOpportunityRequestAndAuthor(opportunityRequest);

		replay();
		String result = controller.postOpportunityRequest(opportunityRequest, bindingResult, model, request);
		verify();

		assertEquals(CreateNewOpportunityController.REGISTER_COMPLETE_VIEW_NAME, result);
	}

	@Test
	public void shouldNotPostOpportunityRequestIfErrors() {
		Domicile domicile = new DomicileBuilder().code("PL").build();
		OpportunityRequest opportunityRequest = new OpportunityRequestBuilder().institutionCountry(domicile).build();
		BindingResult bindingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");
		bindingResult.reject("error");
		Model model = new ExtendedModelMap();
		HttpServletRequest request = new MockHttpServletRequest();

		ArrayList<QualificationInstitution> institutionsList = Lists.newArrayList();
		expect(qualificationInstitutionDAO.getEnabledInstitutionsByCountryCode("PL")).andReturn(institutionsList);

		replay();
		String result = controller.postOpportunityRequest(opportunityRequest, bindingResult, model, request);
		verify();

		assertEquals(CreateNewOpportunityController.LOGIN_PAGE, result);
		assertSame(institutionsList, model.asMap().get("institutions"));
		assertTrue((Boolean) request.getAttribute(CreateNewOpportunityController.CLICKED_ON_CREATE_OPPORTUNITY));
	}

}
