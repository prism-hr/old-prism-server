package com.zuehlke.pgadmissions.controllers;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
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
import com.zuehlke.pgadmissions.dao.InstitutionDAO;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.OpportunityRequestComment;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestStatus;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ProgramTypePropertyEditor;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.OpportunitiesService;
import com.zuehlke.pgadmissions.services.PermissionsService;
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
    private ImportedEntityService importedEntityService;

    @Mock
    @InjectIntoByType
    private InstitutionDAO qualificationInstitutionDAO;

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
    private ProgramTypePropertyEditor programTypePropertyEditor;

    @Mock
    @InjectIntoByType
    private PermissionsService permissionsService;

    @Mock
    @InjectIntoByType
    private ApplicationContext applicationContext;

    @TestedObject
    private EditOpportunityRequestController controller = new EditOpportunityRequestController();

    @Test
    public void shouldGetEditOpportunityRequestPage() {
        Domicile domicile = new Domicile();
        OpportunityRequest opportunityRequest = new OpportunityRequestBuilder().institutionCountry(domicile).build();
        List<OpportunityRequest> requests = Lists.newArrayList();
        ModelMap modelMap = new ModelMap();
        List<Institution> institutions = Lists.newArrayList();

        expect(permissionsService.canSeeOpportunityRequest(opportunityRequest)).andReturn(true);
        expect(opportunitiesService.getOpportunityRequest(8)).andReturn(opportunityRequest);
        expect(opportunitiesService.getAllRelatedOpportunityRequests(opportunityRequest)).andReturn(requests);
        expect(qualificationInstitutionDAO.getByDomicile(domicile)).andReturn(institutions);

        replay();
        String result = controller.getEditOpportunityRequestPage(8, modelMap);
        verify();

        assertSame(opportunityRequest, modelMap.get("opportunityRequest"));
        assertSame(requests, modelMap.get("opportunityRequests"));
        assertSame(institutions, modelMap.get("institutions"));
        assertThat((OpportunityRequestComment) modelMap.get("comment"), Matchers.isA(OpportunityRequestComment.class));
        assertEquals(EditOpportunityRequestController.EDIT_REQUEST_PAGE_VIEW_NAME, result);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldNotGetEditOpportunityRequestPageIfNotEnoughPermissions() {
        OpportunityRequest opportunityRequest = new OpportunityRequest();

        expect(permissionsService.canSeeOpportunityRequest(opportunityRequest)).andReturn(false);
        expect(opportunitiesService.getOpportunityRequest(8)).andReturn(opportunityRequest);

        replay();
        controller.getEditOpportunityRequestPage(8, null);
    }

    @Test
    public void shouldRespondToOpportunityRequest() {
        OpportunityRequest existingOpportunityRequest = new OpportunityRequestBuilder().acceptingApplications(true).build();
        OpportunityRequest opportunityRequest = new OpportunityRequestBuilder().studyDurationUnit("MONTHS").studyDurationNumber(3).acceptingApplications(false)
                .build();
        OpportunityRequestComment comment = new OpportunityRequestComment();
        BindingResult requestBindingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");
        BindingResult commentBindingResult = new DirectFieldBindingResult(comment, "comment");

        expect(permissionsService.canPostOpportunityRequestComment(existingOpportunityRequest, comment)).andReturn(true);
        expect(opportunitiesService.getOpportunityRequest(8)).andReturn(existingOpportunityRequest);
        opportunitiesService.respondToOpportunityRequest(8, opportunityRequest, comment);

        replay();
        RedirectView result = (RedirectView) controller.respondToOpportunityRequest(8, opportunityRequest, requestBindingResult, comment, commentBindingResult,
                null);
        verify();

        assertEquals(3, opportunityRequest.getStudyDuration().intValue());
        assertEquals("/requests", result.getUrl());
        assertFalse(result.isExposePathVariables());
        assertTrue(opportunityRequest.getAcceptingApplications());
    }

    @Test
    public void shouldRespondToOpportunityRequestWhenBindingErrors() {
        User author = new User();
        Date createdDate = new Date();
        Domicile domicile = new DomicileBuilder().code("PL").build();
        OpportunityRequest opportunityRequest = new OpportunityRequestBuilder().institutionCountry(domicile).build();
        OpportunityRequest existingRequest = new OpportunityRequestBuilder().author(author).createdDate(createdDate).status(OpportunityRequestStatus.REJECTED)
                .build();
        OpportunityRequestComment comment = new OpportunityRequestComment();
        ModelMap modelMap = new ModelMap();
        BindingResult requestBindingResult = new DirectFieldBindingResult(opportunityRequest, "opportunityRequest");
        requestBindingResult.reject("error");
        BindingResult commentBindingResult = new DirectFieldBindingResult(comment, "comment");
        List<Institution> institutions = Lists.newArrayList();
        List<OpportunityRequest> requests = Lists.newArrayList();

        expect(permissionsService.canPostOpportunityRequestComment(existingRequest, comment)).andReturn(true);
        expect(qualificationInstitutionDAO.getByDomicile(domicile)).andReturn(institutions);
        expect(opportunitiesService.getOpportunityRequest(8)).andReturn(existingRequest);
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

    @Test(expected = ResourceNotFoundException.class)
    public void shouldNotRespondToOpportunityRequestIfNotEnoughPermissions() {
        OpportunityRequest opportunityRequest = new OpportunityRequest();
        OpportunityRequestComment comment = new OpportunityRequestComment();

        expect(permissionsService.canPostOpportunityRequestComment(opportunityRequest, comment)).andReturn(false);
        expect(opportunitiesService.getOpportunityRequest(8)).andReturn(opportunityRequest);

        replay();
        controller.respondToOpportunityRequest(8, null, null, comment, null, null);
    }

    @Test
    public void shouldReturnAllEnabledDomiciles() {
        List<Domicile> domicileList = Lists.newArrayList();
        EasyMock.expect(importedEntityService.getAllDomiciles()).andReturn(domicileList);

        replay();
        List<Domicile> returnedList = controller.getAllDomiciles();
        verify();

        assertSame(domicileList, returnedList);
    }

    @Test
    public void shouldGetDistinctStudyOptions() {
        List<StudyOption> studyOptions = Lists.newArrayList();
        EasyMock.expect(programInstanceService.getAvailableStudyOptions()).andReturn(studyOptions);

        replay();
        List<StudyOption> returnedList = controller.getDistinctStudyOptions();
        verify();

        assertSame(studyOptions, returnedList);
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
