package com.zuehlke.pgadmissions.controllers;

import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.controllers.workflow.approval.OfferRecommendationController;
import com.zuehlke.pgadmissions.propertyeditors.CommentAssignedUserPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.OfferRecommendationService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.WorkflowService;
import com.zuehlke.pgadmissions.validators.OfferRecommendedCommentValidator;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class OfferRecommendationControllerTest {

    
    @Mock
    @InjectIntoByType
    private UserService userService;

    @Mock
    @InjectIntoByType
    private ApplicationFormService applicationsService;

    @Mock
    @InjectIntoByType
    private WorkflowService applicationFormUserRoleService;

    @Mock
    @InjectIntoByType
    private ActionService actionService;

    @Mock
    @InjectIntoByType
    private OfferRecommendationService offerRecommendedService;

    @Mock
    @InjectIntoByType
    private OfferRecommendedCommentValidator offerRecommendedCommentValidator;

    @Mock
    @InjectIntoByType
    private DatePropertyEditor datePropertyEditor;

    @Mock
    @InjectIntoByType
    private ProgramInstanceService programInstanceService;

    @Mock
    @InjectIntoByType
    private CommentAssignedUserPropertyEditor supervisorPropertyEditor;

    @TestedObject
    private OfferRecommendationController controller;
    
//    @Test
//    public void shouldGetOfferRecommendationPage() {
//        ApplicationForm applicationForm = new ApplicationForm();
//        RegisteredUser user = new RegisteredUser();
//        ModelMap modelMap = new ModelMap();
//        modelMap.put("applicationForm", applicationForm);
//        modelMap.put("user", user);
//
//        actionsProviderMock.validateAction(applicationForm, user, ApplicationFormAction.CONFIRM_OFFER_RECOMMENDATION);
//
//        EasyMock.replay(actionsProviderMock);
//        String res = controller.getOfferRecommendationPage(modelMap);
//        EasyMock.verify(actionsProviderMock);
//
//        assertEquals("private/staff/approver/offer_recommendation_page", res);
//    }
//
//    @Test(expected = MissingApplicationFormException.class)
//    public void shouldThrowExceptionWhenApptlicationIsNull() {
//        EasyMock.expect(applicationsServiceMock.getByApplicationNumber("app1")).andReturn(null);
//
//        EasyMock.replay(applicationsServiceMock);
//        controller.getApplicationForm("app1");
//        EasyMock.verify(applicationsServiceMock);
//    }
//
//    @Test
//    public void shouldSubmitOfferRecommendationData() {
//        RegisteredUser user = new RegisteredUser();
//        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").build();
//
//        OfferRecommendedComment comment = new OfferRecommendedComment();
//        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
//
//        ModelMap modelMap = new ModelMap();
//        modelMap.put("applicationForm", applicationForm);
//        modelMap.put("user", user);
//
//        actionsProviderMock.validateAction(applicationForm, user, ApplicationFormAction.CONFIRM_OFFER_RECOMMENDATION);
//        EasyMock.expect(offerRecommendationServiceMock.moveToApproved(applicationForm, comment)).andReturn(true);
//        offerRecommendationServiceMock.sendToPortico(applicationForm);
//
//        EasyMock.replay(offerRecommendationServiceMock, actionsProviderMock);
//        String res = controller.recommendOffer(comment, result, modelMap);
//        EasyMock.verify(offerRecommendationServiceMock, actionsProviderMock);
//
//        assertEquals("redirect:/applications", res);
//        assertEquals("move.approved", modelMap.get("messageCode"));
//        assertEquals("app1", modelMap.get("application"));
//    }
//
//    @Test
//    public void shouldRejectOfferRecommendationDataIfErrors() {
//        RegisteredUser user = new RegisteredUser();
//        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").build();
//        OfferRecommendedComment comment = new OfferRecommendedComment();
//
//        BindingResult errors = new BeanPropertyBindingResult(comment, "offerRecommendedComment");
//        errors.reject("error");
//
//        ModelMap modelMap = new ModelMap();
//        modelMap.put("applicationForm", applicationForm);
//        modelMap.put("user", user);
//
//        String result = controller.recommendOffer(comment, errors, modelMap);
//        Assert.assertEquals("private/staff/approver/offer_recommendation_page", result);
//    }
//
//    @Test
//    public void shouldRedirectToRejectApplicationPageIfCannotBeApproved() {
//        RegisteredUser user = new RegisteredUser();
//        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").build();
//
//        OfferRecommendedComment comment = new OfferRecommendedComment();
//        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
//
//        ModelMap modelMap = new ModelMap();
//        modelMap.put("applicationForm", applicationForm);
//        modelMap.put("user", user);
//
//        actionsProviderMock.validateAction(applicationForm, user, ApplicationFormAction.CONFIRM_OFFER_RECOMMENDATION);
//        EasyMock.expect(offerRecommendationServiceMock.moveToApproved(applicationForm, comment)).andReturn(false);
//
//        EasyMock.replay(offerRecommendationServiceMock, actionsProviderMock);
//        String res = controller.recommendOffer(comment, result, modelMap);
//        EasyMock.verify(offerRecommendationServiceMock, actionsProviderMock);
//
//        Assert.assertEquals("redirect:/rejectApplication?applicationId=app1&rejectionId=7", res);
//    }
//
//    @Test
//    public void shouldRegisterPropertyEditors() {
//        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
//
//        binderMock.setValidator(offerRecommendedCommentValidatorMock);
//        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
//        binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
//        binderMock.registerCustomEditor(Supervisor.class, supervisorPropertyEditorMock);
//
//        EasyMock.replay(binderMock);
//        controller.registerPropertyEditors(binderMock);
//        EasyMock.verify(binderMock);
//    }

}