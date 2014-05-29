package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Arrays;

import junit.framework.Assert;

import org.apache.commons.lang.StringEscapeUtils;
import org.easymock.EasyMock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Validator;
import org.unitils.inject.util.InjectionUtils;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramDetails;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.services.ProgramService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class ProgrammeDetailsValidatorTest {

    @Autowired
    private Validator validator;

    private ProgramDetailsValidator programmeDetailsValidator;

    private ProgramDetails programmeDetail;

    private ProgramInstance programInstance;

    private ProgramService programServiceMock;

    private Program program;

    private Application form;

    @Test
    public void shouldSupportProgrammeDetails() {
        assertTrue(programmeDetailsValidator.supports(ProgramDetails.class));
    }

    @Test
    public void shouldRejectIfSupervisorsHaveTheSameEmailAddress() {
        SuggestedSupervisor suggestedSupervisor = new SuggestedSupervisor().withUser(
                new User().withFirstName("Mark").withLastName("Johnson").withEmail("mark@gmail.com")).withAware(true);
        programmeDetail.getSuggestedSupervisors().add(suggestedSupervisor);
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(programmeDetail, "suggestedSupervisors");
        EasyMock.expect(programServiceMock.getActiveProgramInstancesForStudyOption(program, programmeDetail.getStudyOption())).andReturn(
                Arrays.asList(programInstance));
        EasyMock.replay(programServiceMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programServiceMock);

        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("suggestedSupervisors.duplicate.email", mappingResult.getFieldError("suggestedSupervisors").getCode());
    }

    @Test
    public void shouldRejectIfStudyOptionIsEmpty() {
        programmeDetail.setStudyOption(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "studyOption");
        EasyMock.expect(programServiceMock.getActiveProgramInstancesForStudyOption(program, programmeDetail.getStudyOption())).andReturn(
                Arrays.asList(programInstance));
        EasyMock.replay(programServiceMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programServiceMock);

        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("studyOption").getCode());
        Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
    }

    @Test
    public void shouldRejectIfStartDateIsEmpty() {
        programmeDetail.setStartDate(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "startDate");
        EasyMock.expect(programServiceMock.getActiveProgramInstancesForStudyOption(program, programmeDetail.getStudyOption())).andReturn(
                Arrays.asList(programInstance));
        EasyMock.replay(programServiceMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programServiceMock);

        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldErrors("startDate").get(0).getCode());
    }

    @Test
    public void shouldRejectIfStartDateIsFutureDate() {
        programmeDetail.setStartDate(new LocalDate().plusDays(1));
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "startDate");
        EasyMock.expect(programServiceMock.getActiveProgramInstancesForStudyOption(program, programmeDetail.getStudyOption())).andReturn(
                Arrays.asList(programInstance));
        EasyMock.replay(programServiceMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programServiceMock);

        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldErrors("startDate").get(0).getCode());
    }

    @Test
    public void shouldRejectIfSourcesOfInterestIsEmpty() {
        programmeDetail.setSourceOfInterest(null);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "sourcesOfInterest");
        EasyMock.expect(programServiceMock.getActiveProgramInstancesForStudyOption(program, programmeDetail.getStudyOption())).andReturn(
                Arrays.asList(programInstance));
        EasyMock.replay(programServiceMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programServiceMock);

        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("sourcesOfInterest").getCode());
        Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
    }

    @Test
    public void shouldRejectIfSourcesOfInterestFreeTextIsEmpty() {
        SourcesOfInterest sourcesOfInterest = new SourcesOfInterest().withId(1).withCode("OTHER").withName("Other");
        programmeDetail.setSourceOfInterest(sourcesOfInterest);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "sourcesOfInterest");
        EasyMock.expect(programServiceMock.getActiveProgramInstancesForStudyOption(program, programmeDetail.getStudyOption())).andReturn(
                Arrays.asList(programInstance));
        EasyMock.replay(programServiceMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programServiceMock);

        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("sourcesOfInterestText").getCode());
        Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
    }

    @Test
    public void shouldRejectIfStartDateIsNotInRange() {
        programmeDetail.setStartDate(new LocalDate().plusYears(5));
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "startDate");
        EasyMock.expect(programServiceMock.getActiveProgramInstancesForStudyOption(program, programmeDetail.getStudyOption())).andReturn(
                Arrays.asList(programInstance));
        EasyMock.replay(programServiceMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programServiceMock);

        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
    }

    @Test
    public void shouldRejectIfSuggestedSupervisorFirstNameIsEmpty() {
        programmeDetail.getSuggestedSupervisors().get(0).getUser().setFirstName(null);
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(programmeDetail, "suggestedSupervisors");
        EasyMock.expect(programServiceMock.getActiveProgramInstancesForStudyOption(program, programmeDetail.getStudyOption())).andReturn(
                Arrays.asList(programInstance));
        EasyMock.replay(programServiceMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programServiceMock);

        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("suggestedSupervisors").getCode());
        Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
    }

    @Test
    public void shouldRejectIfSuggestedSupervisorFirstNameContainsInvalidCharacter() {
        String chineseName = StringEscapeUtils.unescapeJava("\\u5b9d\\u8912\\u82de\\n");
        programmeDetail.getSuggestedSupervisors().get(0).getUser().setFirstName(chineseName);
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(programmeDetail, "suggestedSupervisors");
        EasyMock.expect(programServiceMock.getActiveProgramInstancesForStudyOption(program, programmeDetail.getStudyOption())).andReturn(
                Arrays.asList(programInstance));
        EasyMock.replay(programServiceMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programServiceMock);

        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("You must enter ASCII compliant characters.", mappingResult.getFieldError("suggestedSupervisors[0].firstname").getDefaultMessage());
        Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
    }

    @Test
    public void shouldRejectIfSuggestedSupervisorFirstNameIsLongerThan30() {
        programmeDetail.getSuggestedSupervisors().get(0).getUser().setFirstName("PaulinePaulinePaulinePaulinePaulinePaulinePauline");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(programmeDetail, "suggestedSupervisors");
        EasyMock.expect(programServiceMock.getActiveProgramInstancesForStudyOption(program, programmeDetail.getStudyOption())).andReturn(
                Arrays.asList(programInstance));
        EasyMock.replay(programServiceMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programServiceMock);

        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 30 characters are allowed.", mappingResult.getFieldError("suggestedSupervisors[0].firstname").getDefaultMessage());
        Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
    }

    @Test
    public void shouldRejectIfSuggestedSupervisorLastNameIsEmpty() {
        programmeDetail.getSuggestedSupervisors().get(0).getUser().setLastName(null);
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(programmeDetail, "suggestedSupervisors");
        EasyMock.expect(programServiceMock.getActiveProgramInstancesForStudyOption(program, programmeDetail.getStudyOption())).andReturn(
                Arrays.asList(programInstance));
        EasyMock.replay(programServiceMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programServiceMock);

        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("suggestedSupervisors").getCode());
        Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
    }

    @Test
    public void shouldRejectIfSuggestedSupervisorLastNameContainsInvalidCharacter() {
        String chineseName = StringEscapeUtils.unescapeJava("\\u5b9d\\u8912\\u82de\\n");
        programmeDetail.getSuggestedSupervisors().get(0).getUser().setLastName(chineseName);
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(programmeDetail, "suggestedSupervisors");
        EasyMock.expect(programServiceMock.getActiveProgramInstancesForStudyOption(program, programmeDetail.getStudyOption())).andReturn(
                Arrays.asList(programInstance));
        EasyMock.replay(programServiceMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programServiceMock);

        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("You must enter ASCII compliant characters.", mappingResult.getFieldError("suggestedSupervisors[0].lastname").getDefaultMessage());
        Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
    }

    @Test
    public void shouldRejectIfSuggestedSupervisorLastNameIsLongerThan40() {
        programmeDetail.getSuggestedSupervisors().get(0).getUser().setLastName("PaulinePaulinePaulinePaulinePaulinePaulinePaulinePaulinePaulinePauline");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(programmeDetail, "suggestedSupervisors");
        EasyMock.expect(programServiceMock.getActiveProgramInstancesForStudyOption(program, programmeDetail.getStudyOption())).andReturn(
                Arrays.asList(programInstance));
        EasyMock.replay(programServiceMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programServiceMock);

        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 40 characters are allowed.", mappingResult.getFieldError("suggestedSupervisors[0].lastname").getDefaultMessage());
        Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
    }

    @Test
    public void shouldRejectIfSuggestedSupervisorEmailIsEmpty() {
        programmeDetail.getSuggestedSupervisors().get(0).getUser().setEmail(null);
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(programmeDetail, "suggestedSupervisors");
        EasyMock.expect(programServiceMock.getActiveProgramInstancesForStudyOption(program, programmeDetail.getStudyOption())).andReturn(
                Arrays.asList(programInstance));
        EasyMock.replay(programServiceMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programServiceMock);

        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("suggestedSupervisors").getCode());
        Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
    }

    @Test
    public void shouldRejectIfSuggestedSupervisorEmailContainsInvalidCharacter() {
        programmeDetail.getSuggestedSupervisors().get(0).getUser().setEmail("paul.@never.com");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(programmeDetail, "suggestedSupervisors");
        EasyMock.expect(programServiceMock.getActiveProgramInstancesForStudyOption(program, programmeDetail.getStudyOption())).andReturn(
                Arrays.asList(programInstance));
        EasyMock.replay(programServiceMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programServiceMock);

        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("You must enter a valid email address.", mappingResult.getFieldError("suggestedSupervisors[0].email").getDefaultMessage());
        Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
    }

    @Test
    public void shouldRejectIfSuggestedSupervisorEmailIsLongerThan255() {
        programmeDetail
                .getSuggestedSupervisors()
                .get(0)
                .getUser()
                .setEmail(
                        "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890@a.com");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(programmeDetail, "suggestedSupervisors");
        EasyMock.expect(programServiceMock.getActiveProgramInstancesForStudyOption(program, programmeDetail.getStudyOption())).andReturn(
                Arrays.asList(programInstance));
        EasyMock.replay(programServiceMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programServiceMock);

        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 255 characters are allowed.", mappingResult.getFieldError("suggestedSupervisors[0].email").getDefaultMessage());
        Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
    }

    @Test
    public void shouldRejectIfStudyOptionDoesNotExistInTheProgrammeInstances() {
        StudyOption studyOption = new StudyOption("Dupa", "Jasia");
        programmeDetail.setStudyOption(studyOption);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "studyOption");
        EasyMock.expect(programServiceMock.getActiveProgramInstancesForStudyOption(program, programmeDetail.getStudyOption())).andReturn(null);
        EasyMock.replay(programServiceMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programServiceMock);

        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("programmeDetails.studyOption.invalid", mappingResult.getFieldError("studyOption").getCode());
    }

    @Test
    public void shouldRejectIfApplicationDateHasPassed() {
        programInstance.setApplicationDeadline(new LocalDate().minusDays(1));
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "studyOption");
        programmeDetailsValidator.validate(programmeDetail, mappingResult);

        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("programmeDetails.studyOption.invalid", mappingResult.getFieldError("studyOption").getCode());
    }

    @Test
    public void shouldRejectIfApplicationSubmittedAndTermsAcceptedIsFalse() {
        State validationState = new State();
        validationState.setId(PrismState.APPLICATION_VALIDATION);
        form.setState(validationState);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "acceptedTerms");
        programmeDetailsValidator.validate(programmeDetail, mappingResult);

        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("acceptedTerms").getCode());
    }

    @Test
    public void shouldNotRejectIfApplicationsubmittedAndTermsAcceptedIsTrue() {
        programmeDetail.setAcceptedTerms(true);
        State validationState = new State();
        validationState.setId(PrismState.APPLICATION_VALIDATION);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "acceptedTerms");
        programmeDetailsValidator.validate(programmeDetail, mappingResult);

        Assert.assertEquals(1, mappingResult.getErrorCount());
    }

    @Test
    public void shouldNotRejectIfApplicationUnsubmittedAndTermsAcceptedIsFalse() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "acceptedTerms");
        programmeDetailsValidator.validate(programmeDetail, mappingResult);

        Assert.assertEquals(1, mappingResult.getErrorCount());
    }

    @Before
    public void setup() throws ParseException {
        SourcesOfInterest interest = new SourcesOfInterest().withId(1).withName("ZZ").withCode("ZZ");
        Role role = new Role().withId(Authority.APPLICATION_CREATOR);
        User currentUser = new User().withId(1);
        SuggestedSupervisor suggestedSupervisor = new SuggestedSupervisor().withUser(
                new User().withFirstName("Mark").withLastName("Johnson").withEmail("mark@gmail.com")).withAware(true);
        program = new Program().withId(1).withTitle("Program 1").withState(new State().withId(PrismState.PROGRAM_APPROVED));
        programInstance = new ProgramInstance().withStudyOption("1", "Full-time").withApplicationStartDate(new LocalDate(2025, 8, 6))
                .withApplicationDeadline(new LocalDate(2030, 8, 6)).withEnabled(true);
        form = new ApplicationFormBuilder().id(2).program(program).applicant(currentUser).status(new State().withId(PrismState.APPLICATION_UNSUBMITTED))
                .build();
        programmeDetail = new ProgrammeDetailsBuilder().id(5).suggestedSupervisors(suggestedSupervisor).sourcesOfInterest(interest)
                .startDate(new LocalDate().plusDays(10)).applicationForm(form).studyOption(new StudyOption("1", "Full-time")).build();

        programServiceMock = EasyMock.createMock(ProgramService.class);

        programmeDetailsValidator = new ProgramDetailsValidator();
        InjectionUtils.injectInto(programServiceMock, programmeDetailsValidator, "programService");
        programmeDetailsValidator.setValidator((javax.validation.Validator) validator);
    }
}
