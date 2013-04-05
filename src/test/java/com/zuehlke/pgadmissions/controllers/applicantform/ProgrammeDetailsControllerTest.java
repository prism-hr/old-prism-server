package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.SourcesOfInterestBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SourcesOfInterestPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SuggestedSupervisorJSONPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ProgrammeDetailsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ProgrammeDetailsValidator;

public class ProgrammeDetailsControllerTest {
    private RegisteredUser currentUser;
    private DatePropertyEditor datePropertyEditorMock;
    private ApplicationsService applicationsServiceMock;
    private ProgrammeDetailsValidator programmeDetailsValidatorMock;
    private ProgrammeDetailsService programmeDetailsServiceMock;
    private ProgrammeDetailsController controller;
    private ApplicationFormPropertyEditor applicationFormPropertyEditorMock;
    private SourcesOfInterestPropertyEditor sourcesOfInterestPropertyEditorMock;

    private SuggestedSupervisorJSONPropertyEditor supervisorJSONPropertyEditorMock;
    private UserService userServiceMock;

    @Test(expected = CannotUpdateApplicationException.class)
    public void shouldThrowExceptionIfApplicationFormNotModifiableOnPost() {
        ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().id(1)
                .applicationForm(new ApplicationFormBuilder().id(5).status(ApplicationFormStatus.APPROVED).build()).build();
        BindingResult errors = EasyMock.createMock(BindingResult.class);

        Model model = new ExtendedModelMap();
        model.addAttribute("user", currentUser);

        EasyMock.replay(programmeDetailsServiceMock, errors);
        controller.editProgrammeDetails(programmeDetails, errors, model);
        EasyMock.verify(programmeDetailsServiceMock);

    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourenotFoundExceptionOnSubmitIfCurrentUserNotApplicant() {
        currentUser.getRoles().clear();

        Model model = new ExtendedModelMap();
        model.addAttribute("user", currentUser);

        controller.editProgrammeDetails(null, null, model);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourenotFoundExceptionOnGetIfCurrentUserNotApplicant() {
        currentUser.getRoles().clear();
        controller.getProgrammeDetailsView();
    }

    @Test
    public void shouldReturnProgrammeDetailsView() {
        assertEquals("/private/pgStudents/form/components/programme_details", controller.getProgrammeDetailsView());
    }

    @Test
    public void shouldReturnAvaialbeStudyOptionLevels() {
        final String applicationNumber = "1";
        Program program = new ProgramBuilder().id(7).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicationNumber(applicationNumber).program(program).build();
        controller = new ProgrammeDetailsController(applicationsServiceMock, applicationFormPropertyEditorMock, datePropertyEditorMock,
                supervisorJSONPropertyEditorMock, programmeDetailsValidatorMock, programmeDetailsServiceMock, userServiceMock,
                sourcesOfInterestPropertyEditorMock) {

            @Override
            public ApplicationForm getApplicationForm(String id) {
                if (applicationNumber.equals(id)) {
                    return applicationForm;
                }
                return null;
            }

        };

        StudyOption option1 = new StudyOption("1", "Full-time");
        StudyOption option2 = new StudyOption("31", "Part-time");

        List<StudyOption> optionsList = Arrays.asList(option1, option2);

        EasyMock.expect(programmeDetailsServiceMock.getAvailableStudyOptions(program)).andReturn(optionsList);
        EasyMock.replay(programmeDetailsServiceMock);
        List<StudyOption> studyOptions = controller.getStudyOptions(applicationNumber);
        assertSame(studyOptions, optionsList);
    }

    @Test
    public void shouldReturnAllSourcesOfInterest() {
        SourcesOfInterest sourcesOfInterest = new SourcesOfInterestBuilder().id(1).code("ZZ").name("ZZ").build();
        EasyMock.expect(programmeDetailsServiceMock.getAllEnabledSourcesOfInterest()).andReturn(Collections.singletonList(sourcesOfInterest));
        EasyMock.replay(programmeDetailsServiceMock);
        assertEquals(controller.getSourcesOfInterests().get(0), sourcesOfInterest);
    }

    @Test
    public void shouldReturnApplicationForm() {
        currentUser = EasyMock.createMock(RegisteredUser.class);

        EasyMock.reset(userServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.replay(userServiceMock);
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
        EasyMock.replay(applicationsServiceMock, currentUser);
        ApplicationForm returnedApplicationForm = controller.getApplicationForm("1");
        assertEquals(applicationForm, returnedApplicationForm);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNoFoundExceptionIfApplicationFormDoesNotExist() {
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(null);
        EasyMock.replay(applicationsServiceMock);
        controller.getApplicationForm("1");
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionIfUserCAnnotSeeApplFormOnGet() {
        currentUser = EasyMock.createMock(RegisteredUser.class);
        EasyMock.reset(userServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.replay(userServiceMock);
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).build();
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
        EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(false);
        EasyMock.replay(applicationsServiceMock, currentUser);
        controller.getApplicationForm("1");

    }

    @Test
    public void shouldBindPropertyEditors() {
        WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
        binderMock.setValidator(programmeDetailsValidatorMock);
        binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
        binderMock.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditorMock);
        binderMock.registerCustomEditor(SuggestedSupervisor.class, supervisorJSONPropertyEditorMock);
        binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
        binderMock.registerCustomEditor(SourcesOfInterest.class, sourcesOfInterestPropertyEditorMock);
        EasyMock.replay(binderMock);
        controller.registerPropertyEditors(binderMock);
        EasyMock.verify(binderMock);
    }

    @Test
    public void shouldGetProgrammeDetailsFromApplicationForm() {

        ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().id(1).build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).build();
        applicationForm.setProgrammeDetails(programmeDetails);
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        currentUser = EasyMock.createMock(RegisteredUser.class);
        EasyMock.reset(userServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.replay(userServiceMock);
        EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
        EasyMock.replay(applicationsServiceMock, currentUser);

        ProgrammeDetails returnedProgrammeDetails = controller.getProgrammeDetails("5");
        assertEquals(programmeDetails, returnedProgrammeDetails);
    }

    @Test
    public void shouldReturnNewProgrammeDetailsIfApplicationFormHasNoProgrammeDetails() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).build();
        EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
        currentUser = EasyMock.createMock(RegisteredUser.class);
        EasyMock.reset(userServiceMock);
        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.replay(userServiceMock);
        EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
        EasyMock.replay(applicationsServiceMock, currentUser);
        ProgrammeDetails returnedProgrammeDetails = controller.getProgrammeDetails("5");
        assertNull(returnedProgrammeDetails.getId());
    }

    @Test
    public void shouldReturnMessage() {
        assertEquals("bob", controller.getMessage("bob"));

    }

    @Test
    public void shouldSaveProgrammeDetailsAndApplicationAndRedirectIfNoErrors() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).applicationNumber("ABC").build();
        ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().id(1).applicationForm(applicationForm).build();
        BindingResult errors = EasyMock.createMock(BindingResult.class);
        Model model = new ExtendedModelMap();
        model.addAttribute("user", currentUser);

        EasyMock.expect(errors.hasErrors()).andReturn(false);
        EasyMock.expect(programmeDetailsServiceMock.getStudyOptionCodeForProgram(null, null)).andReturn("1");
        programmeDetailsServiceMock.save(programmeDetails);
        applicationsServiceMock.save(applicationForm);

        EasyMock.replay(programmeDetailsServiceMock, applicationsServiceMock, errors);
        String view = controller.editProgrammeDetails(programmeDetails, errors, model);
        EasyMock.verify(programmeDetailsServiceMock, applicationsServiceMock);

        assertEquals("redirect:/update/getProgrammeDetails?applicationId=ABC", view);
        assertEquals(DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE), DateUtils.truncate(applicationForm.getLastUpdated(), Calendar.DATE));
    }

    @Test
    public void shouldNotSaveAndReturnToViewIfErrors() {
        ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().id(1).applicationForm(new ApplicationFormBuilder().id(5).build()).build();
        BindingResult errors = EasyMock.createMock(BindingResult.class);
        Model model = new ExtendedModelMap();
        model.addAttribute("user", currentUser);

        EasyMock.expect(errors.hasErrors()).andReturn(true);

        EasyMock.replay(programmeDetailsServiceMock, errors);
        String view = controller.editProgrammeDetails(programmeDetails, errors, model);
        EasyMock.verify(programmeDetailsServiceMock);

        assertEquals("/private/pgStudents/form/components/programme_details", view);
    }

    @Before
    public void setUp() {
        applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
        programmeDetailsServiceMock = EasyMock.createMock(ProgrammeDetailsService.class);
        applicationFormPropertyEditorMock = EasyMock.createMock(ApplicationFormPropertyEditor.class);
        datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);
        supervisorJSONPropertyEditorMock = EasyMock.createMock(SuggestedSupervisorJSONPropertyEditor.class);
        programmeDetailsValidatorMock = EasyMock.createMock(ProgrammeDetailsValidator.class);
        programmeDetailsServiceMock = EasyMock.createMock(ProgrammeDetailsService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        sourcesOfInterestPropertyEditorMock = EasyMock.createMock(SourcesOfInterestPropertyEditor.class);

        controller = new ProgrammeDetailsController(applicationsServiceMock, applicationFormPropertyEditorMock, datePropertyEditorMock,
                supervisorJSONPropertyEditorMock, programmeDetailsValidatorMock, programmeDetailsServiceMock, userServiceMock,
                sourcesOfInterestPropertyEditorMock);

        currentUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPLICANT).build()).build();

        EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
        EasyMock.replay(userServiceMock);
    }
}
