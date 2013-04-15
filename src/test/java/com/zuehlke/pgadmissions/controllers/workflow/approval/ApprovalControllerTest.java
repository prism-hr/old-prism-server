package com.zuehlke.pgadmissions.controllers.workflow.approval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.bind.support.SimpleSessionStatus;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RequestRestartComment;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RequestRestartCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;
import com.zuehlke.pgadmissions.dto.SendToPorticoDataDTO;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.CountryPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SendToPorticoDataDTOEditor;
import com.zuehlke.pgadmissions.propertyeditors.SupervisorPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.CountryService;
import com.zuehlke.pgadmissions.services.QualificationService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ApprovalRoundValidator;
import com.zuehlke.pgadmissions.validators.GenericCommentValidator;
import com.zuehlke.pgadmissions.validators.RefereesAdminEditDTOValidator;
import com.zuehlke.pgadmissions.validators.SendToPorticoDataDTOValidator;

public class ApprovalControllerTest {
    
    private ApplicationsService applicationServiceMock;
    private UserService userServiceMock;
    private RegisteredUser currentUserMock;
    private ApprovalController controller;
    private ApprovalRoundValidator approvalRoundValidatorMock;
    private SupervisorPropertyEditor supervisorPropertyEditorMock;
    private ApprovalService approvalServiceMock;
    private BindingResult bindingResultMock;
    private DocumentPropertyEditor documentPropertyEditorMock;
    private GenericCommentValidator commentValidatorMock;
    private RefereesAdminEditDTOValidator refereesAdminEditDTOValidatorMock;
    private QualificationService qualificationServiceMock;
    private RefereeService refereeServiceMock;
    private EncryptionHelper encryptionHelperMock;
    private SendToPorticoDataDTOEditor sendToPorticoDataDTOEditorMock;
    private SendToPorticoDataDTOValidator sendToPorticoDataDTOValidatorMock;
    private DatePropertyEditor datePropertyEditorMock;
    private CountryService countryServiceMock;
    private CountryPropertyEditor countryPropertyEditorMock;

    @Test
    public void shouldGetApprovalPage() {
        Supervisor supervisorOne = new SupervisorBuilder().id(1).build();
        Supervisor suprvisorTwo = new SupervisorBuilder().id(2).build();

        Date startDate = new Date();
        ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().startDate(startDate).studyOption("1", "full").build();
        ProgramInstance instance = new ProgramInstanceBuilder().applicationStartDate(startDate).applicationDeadline(DateUtils.addDays(startDate, 1))
                .enabled(true).studyOption("1", "full").build();
        Program program = new ProgramBuilder().id(1).instances(instance).enabled(true).build();

        final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").programmeDetails(programmeDetails).program(program)
                .latestApprovalRound(new ApprovalRoundBuilder().supervisors(supervisorOne, suprvisorTwo).build()).build();

        controller = new ApprovalController(applicationServiceMock, userServiceMock, approvalServiceMock, approvalRoundValidatorMock,
                supervisorPropertyEditorMock, documentPropertyEditorMock, commentValidatorMock, refereesAdminEditDTOValidatorMock, qualificationServiceMock,
                refereeServiceMock, encryptionHelperMock, sendToPorticoDataDTOEditorMock, sendToPorticoDataDTOValidatorMock, datePropertyEditorMock,
                countryServiceMock, countryPropertyEditorMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                if (applicationId.equals("bob")) {
                    return application;
                }
                return null;
            }
        };
        Model model = new ExtendedModelMap();

        Assert.assertEquals("/private/staff/supervisors/approval_details", controller.getMoveToApprovalPage("bob", model));

        ApprovalRound approvalRound = (ApprovalRound) model.asMap().get("approvalRound");
        assertNull(approvalRound.getId());
        assertEquals(2, approvalRound.getSupervisors().size());
        assertTrue(approvalRound.getSupervisors().containsAll(Arrays.asList(supervisorOne, suprvisorTwo)));
    }

    @Test
    public void shouldGetSupervisorsSection() {
        Assert.assertEquals("/private/staff/supervisors/supervisors_section", controller.getSupervisorSection());
    }

    // @Test
    // public void shouldGetRequestApprovalPage() {
    // Assert.assertEquals("/private/staff/approver/request_restart_approve_page",
    // controller.getRequestRestartPage());
    // }

    @Test
    public void shouldGetProgrammeSupervisors() {
        final RegisteredUser interUser1 = new RegisteredUserBuilder().id(7).build();
        final RegisteredUser interUser2 = new RegisteredUserBuilder().id(6).build();

        final Program program = new ProgramBuilder().supervisors(interUser1, interUser2).id(6).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();
        controller = new ApprovalController(applicationServiceMock, userServiceMock, approvalServiceMock, approvalRoundValidatorMock,
                supervisorPropertyEditorMock, documentPropertyEditorMock, commentValidatorMock, refereesAdminEditDTOValidatorMock, qualificationServiceMock,
                refereeServiceMock, encryptionHelperMock, sendToPorticoDataDTOEditorMock, sendToPorticoDataDTOValidatorMock, datePropertyEditorMock,
                countryServiceMock, countryPropertyEditorMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                if (applicationId.equals("5")) {
                    return applicationForm;
                }
                return null;
            }

        };

        List<RegisteredUser> supervisorsUsers = controller.getProgrammeSupervisors("5");
        assertEquals(2, supervisorsUsers.size());
        assertTrue(supervisorsUsers.containsAll(Arrays.asList(interUser1, interUser2)));
    }

    @Test
    public void shouldGetListOfPreviousSupervisorsAndAddReviewersWillingToApprovalRoundWitDefaultSupervisorsRemoved() {
        EasyMock.reset(userServiceMock);
        final RegisteredUser defaultSupervisor = new RegisteredUserBuilder().id(9).build();
        final RegisteredUser interviewerWillingToSuperviseOne = new RegisteredUserBuilder().id(8).build();
        final RegisteredUser interviewerWillingToSuperviseTwo = new RegisteredUserBuilder().id(7).build();
        final RegisteredUser previousSupervisor = new RegisteredUserBuilder().id(6).build();
        InterviewComment interviewOne = new InterviewCommentBuilder().id(1).user(interviewerWillingToSuperviseOne).willingToSupervise(true).build();
        InterviewComment interviewTwo = new InterviewCommentBuilder().id(1).user(defaultSupervisor).willingToSupervise(true).build();
        InterviewComment interviewThree = new InterviewCommentBuilder().id(1).user(interviewerWillingToSuperviseTwo).willingToSupervise(true).build();

        final Program program = new ProgramBuilder().id(6).supervisors(defaultSupervisor).build();

        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).comments(interviewOne, interviewTwo, interviewThree)
                .build();
        controller = new ApprovalController(applicationServiceMock, userServiceMock, approvalServiceMock, approvalRoundValidatorMock,
                supervisorPropertyEditorMock, documentPropertyEditorMock, commentValidatorMock, refereesAdminEditDTOValidatorMock, qualificationServiceMock,
                refereeServiceMock, encryptionHelperMock, sendToPorticoDataDTOEditorMock, sendToPorticoDataDTOValidatorMock, datePropertyEditorMock,
                countryServiceMock, countryPropertyEditorMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                if (applicationId.equals("5")) {
                    return applicationForm;
                }
                return null;
            }

        };

        EasyMock.expect(userServiceMock.getAllPreviousSupervisorsOfProgram(program)).andReturn(
                Arrays.asList(previousSupervisor, defaultSupervisor, interviewerWillingToSuperviseOne));
        EasyMock.replay(userServiceMock);
        List<RegisteredUser> interviewerUsers = controller.getPreviousSupervisorsAndInterviewersWillingToSupervise("5");
        assertEquals(3, interviewerUsers.size());
        assertTrue(interviewerUsers.containsAll(Arrays.asList(previousSupervisor, interviewerWillingToSuperviseOne, interviewerWillingToSuperviseTwo)));
    }

    @Test
    public void shouldReturnNewApprovalRoundWithExistingRoundsSupervisorsIfAny() {
        Supervisor supervisorOne = new SupervisorBuilder().id(1).build();
        Supervisor suprvisorTwo = new SupervisorBuilder().id(2).build();

        Date startDate = new Date();
        ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().startDate(startDate).studyOption("1", "full").build();
        ProgramInstance instance = new ProgramInstanceBuilder().applicationStartDate(startDate).applicationDeadline(DateUtils.addDays(startDate, 1))
                .enabled(true).studyOption("1", "full").build();
        Program program = new ProgramBuilder().id(1).instances(instance).enabled(true).build();

        final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").program(program).programmeDetails(programmeDetails)
                .latestApprovalRound(new ApprovalRoundBuilder().supervisors(supervisorOne, suprvisorTwo).build()).build();

        controller = new ApprovalController(applicationServiceMock, userServiceMock, approvalServiceMock, approvalRoundValidatorMock,
                supervisorPropertyEditorMock, documentPropertyEditorMock, commentValidatorMock, refereesAdminEditDTOValidatorMock, qualificationServiceMock,
                refereeServiceMock, encryptionHelperMock, sendToPorticoDataDTOEditorMock, sendToPorticoDataDTOValidatorMock, datePropertyEditorMock,
                countryServiceMock, countryPropertyEditorMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                if (applicationId.equals("bob")) {
                    return application;
                }
                return null;
            }
        };
        ApprovalRound returnedApprovalRound = controller.getApprovalRound("bob");
        assertNull(returnedApprovalRound.getId());
        assertEquals(2, returnedApprovalRound.getSupervisors().size());
        assertTrue(returnedApprovalRound.getSupervisors().containsAll(Arrays.asList(supervisorOne, suprvisorTwo)));
        assertEquals(startDate, returnedApprovalRound.getRecommendedStartDate());
    }

    @Test
    public void shouldReturnApprovalRoundWithWillingToApprovalRoundWithSupervisorsOfPreviousApprovalRoundRemoved() {
        RegisteredUser userOne = new RegisteredUserBuilder().id(1).build();
        InterviewComment interviewOne = new InterviewCommentBuilder().id(1).user(userOne).willingToSupervise(true).build();
        RegisteredUser userTwo = new RegisteredUserBuilder().id(2).build();
        InterviewComment interviewTwo = new InterviewCommentBuilder().id(2).user(userTwo).willingToSupervise(true).build();
        RegisteredUser userThree = new RegisteredUserBuilder().id(3).build();
        InterviewComment interviewThree = new InterviewCommentBuilder().id(3).user(userThree).willingToSupervise(true).build();
        Supervisor interviewerOne = new SupervisorBuilder().id(1).user(userOne).build();
        Supervisor interviewerTwo = new SupervisorBuilder().id(2).user(userTwo).build();

        Date startDate = new Date();
        ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().startDate(startDate).studyOption("1", "full").build();
        ProgramInstance instance = new ProgramInstanceBuilder().applicationStartDate(startDate).applicationDeadline(DateUtils.addDays(startDate, 1))
                .enabled(true).studyOption("1", "full").build();
        Program program = new ProgramBuilder().id(1).instances(instance).enabled(true).build();

        final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").programmeDetails(programmeDetails).program(program)
                .comments(interviewOne, interviewTwo, interviewThree)
                .latestApprovalRound(new ApprovalRoundBuilder().supervisors(interviewerOne, interviewerTwo).build()).build();

        controller = new ApprovalController(applicationServiceMock, userServiceMock, approvalServiceMock, approvalRoundValidatorMock,
                supervisorPropertyEditorMock, documentPropertyEditorMock, commentValidatorMock, refereesAdminEditDTOValidatorMock, qualificationServiceMock,
                refereeServiceMock, encryptionHelperMock, sendToPorticoDataDTOEditorMock, sendToPorticoDataDTOValidatorMock, datePropertyEditorMock,
                countryServiceMock, countryPropertyEditorMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                if (applicationId.equals("bob")) {
                    return application;
                }
                return null;
            }

        };
        ApprovalRound returnedApprovalRound = controller.getApprovalRound("bob");
        assertNull(returnedApprovalRound.getId());
        assertEquals(3, returnedApprovalRound.getSupervisors().size());
        assertTrue(returnedApprovalRound.getSupervisors().containsAll(Arrays.asList(interviewerOne, interviewerTwo)));
        assertNull(returnedApprovalRound.getSupervisors().get(2).getId());
        assertEquals(userThree, returnedApprovalRound.getSupervisors().get(2).getUser());
    }

    @Test
    public void shouldReturnNewApprovalRoundWithEmtpySupervisorsIfNoLatestApprovalRound() {

        Date startDate = new Date();

        ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().startDate(startDate).studyOption("1", "full").build();
        ProgramInstance instance = new ProgramInstanceBuilder().applicationStartDate(startDate).applicationDeadline(DateUtils.addDays(startDate, 1))
                .enabled(true).studyOption("1", "full").build();
        Program program = new ProgramBuilder().id(1).instances(instance).enabled(true).build();

        final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").programmeDetails(programmeDetails).program(program)
                .build();

        controller = new ApprovalController(applicationServiceMock, userServiceMock, approvalServiceMock, approvalRoundValidatorMock,
                supervisorPropertyEditorMock, documentPropertyEditorMock, commentValidatorMock, refereesAdminEditDTOValidatorMock, qualificationServiceMock,
                refereeServiceMock, encryptionHelperMock, sendToPorticoDataDTOEditorMock, sendToPorticoDataDTOValidatorMock, datePropertyEditorMock,
                countryServiceMock, countryPropertyEditorMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                if (applicationId.equals("bob")) {
                    return application;
                }
                return null;
            }

        };
        ApprovalRound returnedApprovalRound = controller.getApprovalRound("bob");
        assertNull(returnedApprovalRound.getId());
        assertTrue(returnedApprovalRound.getSupervisors().isEmpty());
        assertEquals(startDate, returnedApprovalRound.getRecommendedStartDate());

    }

    @Test
    public void shouldGetApplicationFromIdForAdmin() {
        Program program = new ProgramBuilder().id(6).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();

        EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(true);
        EasyMock.expect(currentUserMock.canSee(applicationForm)).andReturn(true);
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationServiceMock, currentUserMock);

        ApplicationForm returnedForm = controller.getApplicationForm("5");
        assertEquals(applicationForm, returnedForm);

    }

    @Test
    public void shouldGetApplicationFromIdForApprover() {
        Program program = new ProgramBuilder().id(6).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();

        EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
        EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(true);
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationServiceMock, currentUserMock);

        ApplicationForm returnedForm = controller.getApplicationForm("5");
        assertEquals(applicationForm, returnedForm);

    }

    @Test(expected = MissingApplicationFormException.class)
    public void shouldThrowResourceNotFoundExceptionIfApplicatioDoesNotExist() {
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(null);
        EasyMock.replay(applicationServiceMock);

        controller.getApplicationForm("5");
    }

    @Test(expected = InsufficientApplicationFormPrivilegesException.class)
    public void shouldThrowExceptionIfUserNotAdminOrApproverOfApplicationProgram() {

        Program program = new ProgramBuilder().id(6).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).build();

        EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
        EasyMock.expect(currentUserMock.isInRoleInProgram(Authority.APPROVER, program)).andReturn(false);

        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        EasyMock.replay(applicationServiceMock, currentUserMock);

        controller.getApplicationForm("5");
    }

    @Test
    public void shouldGetCurrentUserAsUser() {
        assertEquals(currentUserMock, controller.getUser());
    }

    @Test
    public void shouldAssignSupervisorsAndMoveToApproval() {

        final ApprovalRound approvalRound = new ApprovalRoundBuilder().id(4).build();
        final ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("abc").build();
        SessionStatus sessionStatus = new SimpleSessionStatus();
        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");

        approvalServiceMock.moveApplicationToApproval(application, approvalRound);
        EasyMock.expectLastCall().once();

        EasyMock.replay(approvalServiceMock);
        String view = controller.assignSupervisors(application, approvalRound, result, sessionStatus);
        EasyMock.verify(approvalServiceMock);

        assertEquals("/private/common/ajax_OK", view);
        assertTrue(sessionStatus.isComplete());
    }

    @Test
    public void shouldNotSaveApprovalRoundAndReturnToApprovalPageIfHasErrors() {
        BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().application(applicationForm).build();
        SessionStatus sessionStatus = new SimpleSessionStatus();

        EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
        EasyMock.replay(errorsMock);
        assertEquals("/private/staff/supervisors/supervisors_section", controller.assignSupervisors(applicationForm, approvalRound, errorsMock, sessionStatus));
        EasyMock.verify(errorsMock);

        assertFalse(sessionStatus.isComplete());
    }

    @Test
    public void shouldAddApprovalRoundValidatorAndSupervisorPropertyEditor() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(approvalRoundValidatorMock);
        binderMock.registerCustomEditor(Supervisor.class, supervisorPropertyEditorMock);
        binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));

        EasyMock.replay(binderMock);
        controller.registerValidatorAndPropertyEditorForApprovalRound(binderMock);
        EasyMock.verify(binderMock);
    }

    @Test
    public void shouldGetRequestRequestRestartCommentWithApplicationAndCurrentUser() {
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        controller = new ApprovalController(applicationServiceMock, userServiceMock, approvalServiceMock, approvalRoundValidatorMock,
                supervisorPropertyEditorMock, documentPropertyEditorMock, commentValidatorMock, refereesAdminEditDTOValidatorMock, qualificationServiceMock,
                refereeServiceMock, encryptionHelperMock, sendToPorticoDataDTOEditorMock, sendToPorticoDataDTOValidatorMock, datePropertyEditorMock,
                countryServiceMock, countryPropertyEditorMock) {
            @Override
            public ApplicationForm getApplicationForm(String applicationId) {
                if ("5".equals(applicationId)) {
                    return applicationForm;
                }
                return null;
            }
        };
        RequestRestartComment comment = controller.getRequestRestartComment("5");
        assertEquals(applicationForm, comment.getApplication());
        assertEquals(currentUserMock, comment.getUser());
    }

    @Test
    public void shouldAddCommentValidatorAndDocumentPropertyEditor() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(commentValidatorMock);
        binderMock.registerCustomEditor(Document.class, documentPropertyEditorMock);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));

        EasyMock.replay(binderMock);
        controller.registerValidatorAndPropertyEditorForComment(binderMock);
        EasyMock.verify(binderMock);
    }

    @Test
    public void shouldRegisterSendToPorticoDataBinder() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(sendToPorticoDataDTOValidatorMock);
        binderMock.registerCustomEditor(List.class, sendToPorticoDataDTOEditorMock);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));

        EasyMock.replay(binderMock);
        controller.registerSendToPorticoDataBinder(binderMock);
        EasyMock.verify(binderMock);
    }

    @Test
    public void shouldRequestRestartOfApproval() {
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(121).applicationNumber("LALALA").build();

        RequestRestartComment comment = new RequestRestartCommentBuilder().id(9).comment("request restart").build();

        approvalServiceMock.requestApprovalRestart(applicationForm, currentUserMock, comment);
        EasyMock.replay(approvalServiceMock);
        assertEquals("redirect:/applications?messageCode=request.approval.restart&application=LALALA",
                controller.requestRestart(applicationForm, comment, bindingResultMock));
        EasyMock.verify(approvalServiceMock);

    }

    @Test
    public void shouldReturnToReequestRestartPageIfErrors() {
        EasyMock.reset(bindingResultMock);
        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(true);
        EasyMock.replay(bindingResultMock);

        ApplicationForm applicationForm = new ApplicationFormBuilder().id(121).applicationNumber("LALALA").build();
        RequestRestartComment comment = new RequestRestartCommentBuilder().id(9).comment("request restart").build();

        EasyMock.replay(approvalServiceMock);
        assertEquals("/private/staff/approver/request_restart_approve_page", controller.requestRestart(applicationForm, comment, bindingResultMock));
        EasyMock.verify(approvalServiceMock);

    }

    @Test
    public void shouldApplySendToPorticoDataAndMoveToApproval() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(121).applicationNumber("LALALA").build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(4).build();

        SendToPorticoDataDTO sendToPorticoData = new SendToPorticoDataDTO();
        sendToPorticoData.setQualificationsSendToPortico(Arrays.asList(new Integer[] { 1, 2 }));
        sendToPorticoData.setRefereesSendToPortico(Arrays.asList(new Integer[] { 11, 12 }));

        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");

        qualificationServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoData.getQualificationsSendToPortico());
        EasyMock.expectLastCall().once();

        refereeServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoData.getRefereesSendToPortico());
        EasyMock.expectLastCall().once();

        EasyMock.replay(qualificationServiceMock, refereeServiceMock);

        String returnValue = controller.applySendToPorticoData(applicationForm, approvalRound, sendToPorticoData, result);
        assertEquals("/private/staff/supervisors/supervisors_section", returnValue);

        EasyMock.verify(qualificationServiceMock, refereeServiceMock);
    }

    @Test
    public void shouldApplySendToPorticoDataWithMissingQualificationsAndExplanation() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(121).applicationNumber("LALALA").build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().id(4).build();

        SendToPorticoDataDTO sendToPorticoData = new SendToPorticoDataDTO();
        sendToPorticoData.setQualificationsSendToPortico(Collections.<Integer> emptyList());
        sendToPorticoData.setRefereesSendToPortico(Arrays.asList(new Integer[] { 11, 12 }));
        sendToPorticoData.setEmptyQualificationsExplanation("explanation");

        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");

        qualificationServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoData.getQualificationsSendToPortico());
        EasyMock.expectLastCall().once();

        refereeServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoData.getRefereesSendToPortico());
        EasyMock.expectLastCall().once();

        EasyMock.replay(qualificationServiceMock, refereeServiceMock);

        String returnValue = controller.applySendToPorticoData(applicationForm, approvalRound, sendToPorticoData, result);
        assertEquals("/private/staff/supervisors/supervisors_section", returnValue);
        assertEquals("explanation", approvalRound.getMissingQualificationExplanation());

        EasyMock.verify(qualificationServiceMock, refereeServiceMock);
    }

    @Test
    public void shouldApplySendToPorticoDataAndNotToMoveToApprovalIfThereAreErrors() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(121).applicationNumber("LALALA").build();
        ApprovalRound interview = new ApprovalRoundBuilder().id(4).build();

        SendToPorticoDataDTO sendToPorticoData = new SendToPorticoDataDTO();
        sendToPorticoData.setQualificationsSendToPortico(Arrays.asList(new Integer[] { 1, 2 }));
        sendToPorticoData.setRefereesSendToPortico(Arrays.asList(new Integer[] { 11, 12 }));

        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
        result.reject("error"); // does not matter if error

        qualificationServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoData.getQualificationsSendToPortico());
        EasyMock.expectLastCall().once();

        refereeServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoData.getRefereesSendToPortico());
        EasyMock.expectLastCall().once();

        EasyMock.replay(qualificationServiceMock, refereeServiceMock, approvalServiceMock);

        String returnValue = controller.applySendToPorticoData(applicationForm, interview, sendToPorticoData, result);
        assertEquals("/private/staff/supervisors/portico_validation_section", returnValue);

        EasyMock.verify(qualificationServiceMock, refereeServiceMock, approvalServiceMock);
    }

    @Test
    public void shouldSubmitQualificationData() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(121).applicationNumber("LALALA").build();

        SendToPorticoDataDTO sendToPorticoData = new SendToPorticoDataDTO();
        sendToPorticoData.setQualificationsSendToPortico(Arrays.asList(new Integer[] { 1, 2 }));

        BindingResult porticoResult = new MapBindingResult(Collections.emptyMap(), "");
        porticoResult.reject("error"); // does not matter if error

        qualificationServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoData.getQualificationsSendToPortico());
        EasyMock.expectLastCall().once();

        EasyMock.replay(qualificationServiceMock);

        String returnValue = controller.submitQualificationsData(applicationForm, sendToPorticoData, porticoResult);
        assertEquals("/private/staff/supervisors/components/qualification_portico_validation", returnValue);

        EasyMock.verify(qualificationServiceMock);
    }

    @Test
    public void shouldSaveSendToPorticoReferencesAndSaveNewReference() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(121).applicationNumber("LALALA").build();

        SendToPorticoDataDTO sendToPorticoData = new SendToPorticoDataDTO();
        List<Integer> refereesSendToPortico = new ArrayList<Integer>();
        refereesSendToPortico.add(11);
        sendToPorticoData.setRefereesSendToPortico(refereesSendToPortico);

        BindingResult porticoResult = new MapBindingResult(Collections.emptyMap(), "");
        porticoResult.reject("error"); // does not matter if error

        Document document = new DocumentBuilder().build();
        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
        refereesAdminEditDTO.setComment("comment text");
        refereesAdminEditDTO.setEditedRefereeId("refereeId");
        refereesAdminEditDTO.setReferenceDocument(document);
        refereesAdminEditDTO.setSuitableForProgramme(true);
        refereesAdminEditDTO.setSuitableForUCL(false);

        Referee referee = new RefereeBuilder().application(applicationForm).id(12).build();

        BindingResult referenceResult = new MapBindingResult(Collections.emptyMap(), "");
        Model model = new ExtendedModelMap();

        ReferenceComment referenceComment = new ReferenceCommentBuilder().referee(referee).build();

        refereeServiceMock.selectForSendingToPortico(applicationForm, sendToPorticoData.getRefereesSendToPortico());
        EasyMock.expectLastCall().once();

        EasyMock.expect(encryptionHelperMock.decryptToInteger("refereeId")).andReturn(12);
        EasyMock.expect(encryptionHelperMock.encrypt(12)).andReturn("refereeId");
        EasyMock.expect(refereeServiceMock.getRefereeById(12)).andReturn(referee);

        EasyMock.expect(refereeServiceMock.postCommentOnBehalfOfReferee(applicationForm, refereesAdminEditDTO)).andReturn(referenceComment);
        refereeServiceMock.refresh(referee);
        EasyMock.expectLastCall();

        sendToPorticoDataDTOValidatorMock.validate(sendToPorticoData, porticoResult);
        EasyMock.expectLastCall();

        EasyMock.replay(refereeServiceMock, encryptionHelperMock, sendToPorticoDataDTOValidatorMock);

        String returnValue = controller.submitRefereesData(applicationForm, sendToPorticoData, porticoResult, refereesAdminEditDTO, referenceResult, null,
                model);
        assertEquals("/private/staff/supervisors/components/reference_portico_validation", returnValue);
        assertEquals(Arrays.asList(11, 12), refereesSendToPortico);

        EasyMock.verify(refereeServiceMock, encryptionHelperMock, sendToPorticoDataDTOValidatorMock);
    }

    @Test
    public void shouldAddNewReferenceWithoutSavingSendToPorticoReferences() {
        Role adminRole = new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build();
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).role(adminRole).firstName("bob").lastName("bobson").email("email@test.com").build();
        Program program = new ProgramBuilder().title("some title").administrators(admin1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().applicationNumber("app1").program(program).status(ApplicationFormStatus.INTERVIEW)
                .build();
        SendToPorticoDataDTO sendToPorticoDataDTO = new SendToPorticoDataDTO();
        BindingResult porticoResult = new MapBindingResult(Collections.emptyMap(), "");

        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();
        refereesAdminEditDTO.setComment("comment text");
        refereesAdminEditDTO.setEditedRefereeId("refereeId");
        refereesAdminEditDTO.setSuitableForProgramme(true);
        refereesAdminEditDTO.setSuitableForUCL(false);

        BindingResult result = new MapBindingResult(Collections.emptyMap(), "");
        Model model = new ExtendedModelMap();

        Referee referee = new RefereeBuilder().application(applicationForm).id(12).build();
        ReferenceComment referenceComment = new ReferenceCommentBuilder().referee(referee).build();

        EasyMock.expect(encryptionHelperMock.decryptToInteger("refereeId")).andReturn(12);
        EasyMock.expect(encryptionHelperMock.encrypt(12)).andReturn("refereeId");
        EasyMock.expect(refereeServiceMock.getRefereeById(12)).andReturn(referee);
        refereesAdminEditDTOValidatorMock.validate(refereesAdminEditDTO, result);
        EasyMock.expectLastCall();
        EasyMock.expect(refereeServiceMock.postCommentOnBehalfOfReferee(applicationForm, refereesAdminEditDTO)).andReturn(referenceComment);
        refereeServiceMock.refresh(referee);
        EasyMock.expectLastCall();

        EasyMock.replay(encryptionHelperMock, refereeServiceMock, refereesAdminEditDTOValidatorMock);
        String viewName = controller.submitRefereesData(applicationForm, sendToPorticoDataDTO, porticoResult, refereesAdminEditDTO, result, true, model);
        assertEquals("/private/staff/supervisors/components/reference_portico_validation", viewName);

        EasyMock.verify(encryptionHelperMock, refereeServiceMock, refereesAdminEditDTOValidatorMock);
    }

    @Test
    public void shouldRegisterPropertyEditors() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);

        binderMock.setValidator(refereesAdminEditDTOValidatorMock);
        binderMock.registerCustomEditor(Document.class, documentPropertyEditorMock);
        binderMock.registerCustomEditor(Country.class, countryPropertyEditorMock);
        binderMock.registerCustomEditor((Class<?>) EasyMock.isNull(), EasyMock.eq("comment"), EasyMock.isA(StringTrimmerEditor.class));
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
        binderMock.registerCustomEditor(EasyMock.eq(String[].class), EasyMock.anyObject(StringArrayPropertyEditor.class));

        EasyMock.replay(binderMock);
        controller.registerPropertyEditors(binderMock);

        EasyMock.verify(binderMock);
    }

    @Before
    public void setUp() {
        applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        currentUserMock = EasyMock.createMock(RegisteredUser.class);
        approvalRoundValidatorMock = EasyMock.createMock(ApprovalRoundValidator.class);
        supervisorPropertyEditorMock = EasyMock.createMock(SupervisorPropertyEditor.class);
        approvalServiceMock = EasyMock.createMock(ApprovalService.class);
        documentPropertyEditorMock = EasyMock.createMock(DocumentPropertyEditor.class);
        commentValidatorMock = EasyMock.createMock(GenericCommentValidator.class);
        refereesAdminEditDTOValidatorMock = EasyMock.createMock(RefereesAdminEditDTOValidator.class);
        qualificationServiceMock = EasyMock.createMock(QualificationService.class);
        refereeServiceMock = EasyMock.createMock(RefereeService.class);
        encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
        sendToPorticoDataDTOEditorMock = EasyMock.createMock(SendToPorticoDataDTOEditor.class);
        sendToPorticoDataDTOValidatorMock = EasyMock.createMock(SendToPorticoDataDTOValidator.class);
        datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);
        countryServiceMock = EasyMock.createMock(CountryService.class);
        countryPropertyEditorMock = EasyMock.createMock(CountryPropertyEditor.class);

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
        EasyMock.replay(userServiceMock);

        bindingResultMock = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(bindingResultMock.hasErrors()).andReturn(false);
        EasyMock.replay(bindingResultMock);

        controller = new ApprovalController(applicationServiceMock, userServiceMock, approvalServiceMock, approvalRoundValidatorMock,
                supervisorPropertyEditorMock, documentPropertyEditorMock, commentValidatorMock, refereesAdminEditDTOValidatorMock, qualificationServiceMock,
                refereeServiceMock, encryptionHelperMock, sendToPorticoDataDTOEditorMock, sendToPorticoDataDTOValidatorMock, datePropertyEditorMock,
                countryServiceMock, countryPropertyEditorMock);

    }
}
