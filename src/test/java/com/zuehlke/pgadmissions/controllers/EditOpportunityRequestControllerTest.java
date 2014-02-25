package com.zuehlke.pgadmissions.controllers;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.util.Date;
import java.util.List;

import org.easymock.EasyMock;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.view.RedirectView;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.OpportunityRequestComment;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestStatus;
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
        OpportunityRequest opportunityRequest = new OpportunityRequestBuilder().institutionCountry(institutionCountry).build();
        List<OpportunityRequest> requests = Lists.newArrayList();
        ModelMap modelMap = new ModelMap();
        List<QualificationInstitution> institutions = Lists.newArrayList();

        expect(opportunitiesService.getOpportunityRequest(8)).andReturn(opportunityRequest);
        expect(opportunitiesService.getAllRelatedOpportunityRequests(opportunityRequest)).andReturn(requests);
        expect(qualificationInstitutionDAO.getEnabledInstitutionsByDomicileCode("PL")).andReturn(institutions);

        replay();
        String result = controller.getEditOpportunityRequestPage(8, modelMap);
        verify();

        assertSame(opportunityRequest, modelMap.get("opportunityRequest"));
        assertSame(requests, modelMap.get("opportunityRequests"));
        assertSame(institutions, modelMap.get("institutions"));
        assertThat((OpportunityRequestComment) modelMap.get("comment"), Matchers.isA(OpportunityRequestComment.class));
        assertEquals(EditOpportunityRequestController.EDIT_REQUEST_PAGE_VIEW_NAME, result);
    }

    @Test
    public void shouldRespondToOpportunityRequest() {
        OpportunityRequest opportunityRequest = new OpportunityRequestBuilder().studyDurationUnit("MONTHS").studyDurationNumber(3).build();
        OpportunityRequestComment comment = new OpportunityRequestComment();
        BindingResult requestBindingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");
        BindingResult commentBindingResult = new DirectFieldBindingResult(comment, "comment");

        opportunitiesService.respondToOpportunityRequest(8, opportunityRequest, comment);

        replay();
        RedirectView result = (RedirectView) controller.respondToOpportunityRequest(8, opportunityRequest, requestBindingResult, comment, commentBindingResult,
                null);
        verify();

        assertEquals(3, opportunityRequest.getStudyDuration().intValue());
        assertEquals("/requests", result.getUrl());
        assertFalse(result.isExposePathVariables());
    }

    @Test
    public void shouldRespondToOpportunityRequestWhenBindingErrors() {
        Domicile institutionCountry = new DomicileBuilder().code("PL").build();
        OpportunityRequest opportunityRequest = new OpportunityRequestBuilder().institutionCountry(institutionCountry).build();
        OpportunityRequestComment comment = new OpportunityRequestComment();
        RegisteredUser author = new RegisteredUser();
        ModelMap modelMap = new ModelMap();
        BindingResult requestBindingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");
        requestBindingResult.reject("error");
        BindingResult commentBindingResult = new DirectFieldBindingResult(comment, "comment");
        List<QualificationInstitution> institutions = Lists.newArrayList();
        Date createdDate = new Date();
        List<OpportunityRequest> requests = Lists.newArrayList();

        expect(qualificationInstitutionDAO.getEnabledInstitutionsByDomicileCode("PL")).andReturn(institutions);
        expect(opportunitiesService.getOpportunityRequest(8)).andReturn(
                new OpportunityRequestBuilder().author(author).createdDate(createdDate).status(OpportunityRequestStatus.REJECTED).build());
        expect(opportunitiesService.getAllRelatedOpportunityRequests(opportunityRequest)).andReturn(requests);

        replay();
        String result = (String) controller.respondToOpportunityRequest(8, opportunityRequest, requestBindingResult, comment, commentBindingResult, modelMap);
        verify();

        assertSame(opportunityRequest, modelMap.get("opportunityRequest"));
        assertSame(requests, modelMap.get("opportunityRequests"));
        assertSame(institutions, modelMap.get("institutions"));
        assertEquals(EditOpportunityRequestController.EDIT_REQUEST_PAGE_VIEW_NAME, result);
        assertSame(author, opportunityRequest.getAuthor());
        assertSame(createdDate, opportunityRequest.getCreatedDate());
        assertEquals(OpportunityRequestStatus.REJECTED, opportunityRequest.getStatus());
        
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
    public void shouldRegisterOportunityRequestPropertyEditors() {
        WebDataBinder dataBinder = EasyMockUnitils.createMock(WebDataBinder.class);
        dataBinder.setValidator(opportunityRequestValidator);
        dataBinder.registerCustomEditor(Domicile.class, domicilePropertyEditor);
        dataBinder.registerCustomEditor(Date.class, datePropertyEditor);
        dataBinder.registerCustomEditor(eq(String.class), isA(StringTrimmerEditor.class));

        replay();
        controller.registerOpportunityRequestPropertyEditors(dataBinder);
        verify();
    }

    @Test
    public void shouldRegisterCommentPropertyEditors() {
        WebDataBinder dataBinder = EasyMockUnitils.createMock(WebDataBinder.class);
        dataBinder.registerCustomEditor(eq(String.class), isA(StringTrimmerEditor.class));

        replay();
        controller.registerCommentPropertyEditors(dataBinder);
        verify();
    }

}
