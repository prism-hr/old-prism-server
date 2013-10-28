package com.zuehlke.pgadmissions.controllers;

import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.CONFIRM_OFFER_RECOMMENDATION;
import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Date;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.components.ApplicationDescriptorProvider;
import com.zuehlke.pgadmissions.controllers.workflow.approval.OfferRecommendationController;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.OfferRecommendedComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SupervisorPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
import com.zuehlke.pgadmissions.services.SupervisorsProvider;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.OfferRecommendedCommentValidator;

public class OfferRecommendationControllerTest {

    private OfferRecommendationController controller;

    private ApplicationsService applicationsServiceMock;

    private UserService userServiceMock;

    private ApplicationFormAccessService accessServiceMock;

    private ActionsProvider actionsProviderMock;

    private ApplicationDescriptorProvider applicationDescriptorProviderMock;

    private ApprovalService approvalServiceMock;

    private OfferRecommendedCommentValidator offerRecommendedCommentValidatorMock;

    private DatePropertyEditor datePropertyEditorMock;

    private ProgramInstanceService programInstanceServiceMock;

    private SupervisorsProvider supervisorsProviderMock;

    private SupervisorPropertyEditor supervisorPropertyEditorMock;

    @Test
    public void shouldGetOfferRecommendationPage() {
        ApplicationForm applicationForm = new ApplicationForm();
        RegisteredUser user = new RegisteredUser();
        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);
        modelMap.put("user", user);

        actionsProviderMock.validateAction(applicationForm, user, CONFIRM_OFFER_RECOMMENDATION);

        EasyMock.replay(actionsProviderMock);
        String res = controller.getOfferRecommendationPage(modelMap);
        EasyMock.verify(actionsProviderMock);

        assertEquals("private/staff/approver/offer_recommendation_page", res);
    }

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowExceptionWhenApptlicationIsNull() {
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("app1")).andReturn(null);

        EasyMock.replay(applicationsServiceMock);
        controller.getApplicationForm("app1");
        EasyMock.verify(applicationsServiceMock);
    }

    @Test
    public void shouldSubmitOfferRecommendationData() {
        RegisteredUser user = new RegisteredUser();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").build();

        OfferRecommendedComment comment = new OfferRecommendedComment();
        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");

        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);
        modelMap.put("user", user);

        actionsProviderMock.validateAction(applicationForm, user, CONFIRM_OFFER_RECOMMENDATION);
        EasyMock.expect(approvalServiceMock.moveToApproved(applicationForm, comment)).andReturn(true);
        approvalServiceMock.sendToPortico(applicationForm);

        EasyMock.replay(approvalServiceMock, actionsProviderMock);
        String res = controller.recommendOffer(comment, result, modelMap);
        EasyMock.verify(approvalServiceMock, actionsProviderMock);

        assertEquals("redirect:/applications", res);
        assertEquals("move.approved", modelMap.get("messageCode"));
        assertEquals("app1", modelMap.get("application"));
    }

    @Test
    public void shouldRejectOfferRecommendationDataIfErrors() {
        RegisteredUser user = new RegisteredUser();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").build();
        OfferRecommendedComment comment = new OfferRecommendedComment();

        BindingResult errors = new BeanPropertyBindingResult(comment, "offerRecommendedComment");
        errors.reject("error");

        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);
        modelMap.put("user", user);

        String result = controller.recommendOffer(comment, errors, modelMap);
        Assert.assertEquals("private/staff/approver/offer_recommendation_page", result);
    }

    @Test
    public void shouldRedirectToRejectApplicationPageIfCannotBeApproved() {
        RegisteredUser user = new RegisteredUser();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").build();

        OfferRecommendedComment comment = new OfferRecommendedComment();
        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");

        ModelMap modelMap = new ModelMap();
        modelMap.put("applicationForm", applicationForm);
        modelMap.put("user", user);

        actionsProviderMock.validateAction(applicationForm, user, CONFIRM_OFFER_RECOMMENDATION);
        EasyMock.expect(approvalServiceMock.moveToApproved(applicationForm, comment)).andReturn(false);

        EasyMock.replay(approvalServiceMock, actionsProviderMock);
        String res = controller.recommendOffer(comment, result, modelMap);
        EasyMock.verify(approvalServiceMock, actionsProviderMock);

        Assert.assertEquals("redirect:/rejectApplication?applicationId=app1&rejectionId=7", res);
    }

    @Test
    public void shouldRegisterPropertyEditors() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);

        binderMock.setValidator(offerRecommendedCommentValidatorMock);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
        binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
        binderMock.registerCustomEditor(Supervisor.class, supervisorPropertyEditorMock);
        
        EasyMock.replay(binderMock);
        controller.registerPropertyEditors(binderMock);
        EasyMock.verify(binderMock);
    }

    @Before
    public void prepare() {
        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        actionsProviderMock = EasyMock.createMock(ActionsProvider.class);
        accessServiceMock = EasyMock.createMock(ApplicationFormAccessService.class);
        applicationDescriptorProviderMock = EasyMock.createMock(ApplicationDescriptorProvider.class);
        approvalServiceMock = EasyMock.createMock(ApprovalService.class);
        offerRecommendedCommentValidatorMock = EasyMock.createMock(OfferRecommendedCommentValidator.class);
        datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);
        programInstanceServiceMock = EasyMock.createMock(ProgramInstanceService.class);
        supervisorsProviderMock = EasyMock.createMock(SupervisorsProvider.class);
        supervisorPropertyEditorMock = EasyMock.createMock(SupervisorPropertyEditor.class);
        controller = new OfferRecommendationController(applicationsServiceMock, userServiceMock, actionsProviderMock, accessServiceMock,
                applicationDescriptorProviderMock, approvalServiceMock, offerRecommendedCommentValidatorMock, datePropertyEditorMock,
                programInstanceServiceMock, supervisorsProviderMock, supervisorPropertyEditorMock);
    }
}