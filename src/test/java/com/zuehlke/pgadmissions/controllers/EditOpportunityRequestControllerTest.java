package com.zuehlke.pgadmissions.controllers;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.ModelMap;
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
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.OpportunitiesService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.OpportunityRequestValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class EditOpportunityRequestControllerTest {

    @Mock
    @InjectIntoByType
    private OpportunitiesService opportunitiesService;

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
    private ProgramInstanceService programInstanceService;

    @Mock
    @InjectIntoByType
    private DatePropertyEditor datePropertyEditor;
    
    @Mock
    @InjectIntoByType
    private ApplicationContext applicationContext;

    @TestedObject
    private EditOpportunityRequestController controller = new EditOpportunityRequestController();

    @Test
    public void shouldGetEditOpportunityRequestPage() {
        Domicile institutionCountry = new DomicileBuilder().code("PL").build();
        OpportunityRequest opportunityRequest = new OpportunityRequestBuilder().studyDuration(24).institutionCountry(institutionCountry).build();
        ModelMap modelMap = new ModelMap();
        List<QualificationInstitution> institutions = Lists.newArrayList();

        expect(opportunitiesService.getOpportunityRequest(8)).andReturn(opportunityRequest);
        expect(qualificationInstitutionDAO.getEnabledInstitutionsByDomicileCode("PL")).andReturn(institutions);

        replay();
        String result = controller.getEditOpportunityRequestPage(8, modelMap);
        verify();

        assertSame(opportunityRequest, modelMap.get("opportunityRequest"));
        assertSame(institutions, modelMap.get("institutions"));
        assertEquals(2, opportunityRequest.getStudyDurationNumber().intValue());
        assertEquals("YEARS", opportunityRequest.getStudyDurationUnit());
        assertEquals(EditOpportunityRequestController.EDIT_REQUEST_PAGE_VIEW_NAME, result);
    }

    @Test
    public void shouldApproveOpportunityRequest() {
        OpportunityRequest opportunityRequest = new OpportunityRequestBuilder().studyDurationUnit("MONTHS").studyDurationNumber(3).build();
        BindingResult bindingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");

        opportunitiesService.approveOpportunityRequest(8, opportunityRequest);

        replay();
        String result = controller.approveOpportunityRequest(8, opportunityRequest, bindingResult, null);
        verify();

        assertEquals(3, opportunityRequest.getStudyDuration().intValue());
        assertEquals("redirect:/requests", result);
    }

    @Test
    public void shouldNotApproveOpportunityRequestWhenBindingErrors() {
        Domicile institutionCountry = new DomicileBuilder().code("PL").build();
        OpportunityRequest opportunityRequest = new OpportunityRequestBuilder().institutionCountry(institutionCountry).build();
        RegisteredUser author = new RegisteredUser();
        ModelMap modelMap = new ModelMap();
        BindingResult bindingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");
        bindingResult.reject("error");
        List<QualificationInstitution> institutions = Lists.newArrayList();

        expect(qualificationInstitutionDAO.getEnabledInstitutionsByDomicileCode("PL")).andReturn(institutions);
        expect(opportunitiesService.getOpportunityRequest(8)).andReturn(new OpportunityRequestBuilder().author(author).build());

        replay();
        String result = controller.approveOpportunityRequest(8, opportunityRequest, bindingResult, modelMap);
        verify();

        assertSame(opportunityRequest, modelMap.get("opportunityRequest"));
        assertSame(institutions, modelMap.get("institutions"));
        assertEquals(EditOpportunityRequestController.EDIT_REQUEST_PAGE_VIEW_NAME, result);
        assertSame(author, opportunityRequest.getAuthor());
    }

    @Test
    public void shouldRejectOpportunityRequest() {
        opportunitiesService.rejectOpportunityRequest(8, "Because I said so");

        replay();
        Map<String, Object> result = controller.rejectOpportunityRequest(8, "Because I said so");
        verify();

        assertEquals(Collections.singletonMap("success", true), result);
    }
    @Test
    public void shouldNotRejectOpportunityRequestIfValidatioErrors() {
        expect(applicationContext.getMessage(eq("text.field.maxcharacters"), isA(Object[].class), isA(Locale.class))).andReturn("mess");
        
        replay();
        Map<String, Object> result = controller.rejectOpportunityRequest(8, RandomStringUtils.random(2001));
        verify();
        
        assertEquals(Collections.singletonMap("rejectionReason", "mess"), result);
    }

    @Test
    public void shouldReturnAllEnabledDomiciles() {
        List<Domicile> domicileList = Lists.newArrayList();
        EasyMock.expect(domicileService.getAllEnabledDomicilesExceptAlternateValues()).andReturn(domicileList);

        replay();
        List<Domicile> returnedList = controller.getAllEnabledDomiciles();
        verify();

        assertSame(domicileList, returnedList);
    }

    @Test
    public void shouldGetDistinctStudyOptions() {
        List<StudyOption> studyOptions = Lists.newArrayList();
        EasyMock.expect(programInstanceService.getDistinctStudyOptions()).andReturn(studyOptions);

        replay();
        List<StudyOption> returnedList = controller.getDistinctStudyOptions();
        verify();

        assertSame(studyOptions, returnedList);
    }

    @Test
    public void shouldRegisterPropertyEditors() {
        WebDataBinder dataBinder = EasyMockUnitils.createMock(WebDataBinder.class);
        dataBinder.setValidator(opportunityRequestValidator);
        dataBinder.registerCustomEditor(Domicile.class, domicilePropertyEditor);
        dataBinder.registerCustomEditor(Date.class, datePropertyEditor);

        replay();
        controller.registerPropertyEditors(dataBinder);
        verify();
    }

}
