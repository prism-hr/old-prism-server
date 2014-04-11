package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
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

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramDetails;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.SourcesOfInterestBuilder;
import com.zuehlke.pgadmissions.domain.builders.SuggestedSupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.AdvertState;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
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

    private ApplicationForm form;

    @Test
    public void shouldSupportProgrammeDetails() {
        assertTrue(programmeDetailsValidator.supports(ProgramDetails.class));
    }

    @Test
    public void shouldRejectIfSupervisorsHaveTheSameEmailAddress() {
        SuggestedSupervisor suggestedSupervisor = new SuggestedSupervisorBuilder().firstname("Mark").lastname("Johnson").email("mark@gmail.com").aware(true)
                .build();
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
        programmeDetail.setStartDate(DateUtils.addDays(new Date(), -1));
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
        SourcesOfInterest sourcesOfInterest = new SourcesOfInterestBuilder().id(1).code("OTHER").name("Other").enabled(true).build();
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
        programmeDetail.setStartDate(DateUtils.addYears(new Date(), 5));
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
        programmeDetail.getSuggestedSupervisors().get(0).setFirstname(null);
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
        programmeDetail.getSuggestedSupervisors().get(0).setFirstname(chineseName);
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
        programmeDetail.getSuggestedSupervisors().get(0).setFirstname("PaulinePaulinePaulinePaulinePaulinePaulinePauline");
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
        programmeDetail.getSuggestedSupervisors().get(0).setLastname(null);
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
        programmeDetail.getSuggestedSupervisors().get(0).setLastname(chineseName);
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
        programmeDetail.getSuggestedSupervisors().get(0).setLastname("PaulinePaulinePaulinePaulinePaulinePaulinePaulinePaulinePaulinePauline");
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
        programmeDetail.getSuggestedSupervisors().get(0).setEmail(null);
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
        programmeDetail.getSuggestedSupervisors().get(0).setEmail("paul.@never.com");
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
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_WEEK, -1);
        Date yesterday = calendar.getTime();
        programInstance.setApplicationDeadline(yesterday);
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "studyOption");
        programmeDetailsValidator.validate(programmeDetail, mappingResult);

        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("programmeDetails.studyOption.invalid", mappingResult.getFieldError("studyOption").getCode());
    }

    @Test
    public void shouldRejectIfApplicationSubmittedAndTermsAcceptedIsFalse() {
        State validationState = new State();
        validationState.setId(ApplicationFormStatus.VALIDATION);
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
        validationState.setId(ApplicationFormStatus.VALIDATION);
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
        SourcesOfInterest interest = new SourcesOfInterestBuilder().id(1).name("ZZ").code("ZZ").build();
        Role role = new RoleBuilder().id(Authority.APPLICATION_APPLICANT).build();
        User currentUser = new UserBuilder().id(1).build();
        SuggestedSupervisor suggestedSupervisor = new SuggestedSupervisorBuilder().firstname("Mark").lastname("Johnson").email("mark@gmail.com").aware(true)
                .build();
        program = new ProgramBuilder().id(1).title("Program 1").state(AdvertState.PROGRAM_APPROVED).build();
        programInstance = new ProgramInstanceBuilder().id(1).studyOption("1", "Full-time")
                .applicationStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2025/08/06"))
                .applicationDeadline(new SimpleDateFormat("yyyy/MM/dd").parse("2030/08/06")).enabled(true).build();
        form = new ApplicationFormBuilder().id(2).advert(program).applicant(currentUser).status(new State().withId(ApplicationFormStatus.UNSUBMITTED)).build();
        programmeDetail = new ProgrammeDetailsBuilder().id(5).suggestedSupervisors(suggestedSupervisor).sourcesOfInterest(interest)
                .startDate(DateUtils.addDays(new Date(), 10)).applicationForm(form).studyOption(new StudyOption("1", "Full-time")).build();

        programServiceMock = EasyMock.createMock(ProgramService.class);

        programmeDetailsValidator = new ProgramDetailsValidator();
        InjectionUtils.injectInto(programServiceMock, programmeDetailsValidator, "programService");
        programmeDetailsValidator.setValidator((javax.validation.Validator) validator);
    }
}
