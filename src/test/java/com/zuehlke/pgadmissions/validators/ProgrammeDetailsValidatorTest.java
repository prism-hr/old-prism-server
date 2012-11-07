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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.SourcesOfInterestBuilder;
import com.zuehlke.pgadmissions.domain.builders.SuggestedSupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testContext.xml")
public class ProgrammeDetailsValidatorTest {

    @Autowired
	private ProgrammeDetailsValidator programmeDetailsValidator;
    
	private ProgrammeDetails programmeDetail;
	
	private ProgramInstance programInstance;
	
	@Autowired
	private ProgramInstanceDAO programInstanceDAOMock;
	
	private Program program;
	
	private ApplicationForm form;

	@Test
	public void shouldSupportProgrammeDetails() {
		assertTrue(programmeDetailsValidator.supports(ProgrammeDetails.class));
	}

	@Test
	@DirtiesContext
	public void shouldRejectIfProgrammeNameIsEmpty() {
		programmeDetail.setProgrammeName(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "programmeName");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		programmeDetailsValidator.validate(programmeDetail, mappingResult);
		EasyMock.verify(programInstanceDAOMock);
		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("programmeName").getCode());
		Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
	}

	@Test
	@DirtiesContext
	public void shouldRejectIfStudyOptionIsEmpty() {
		programmeDetail.setStudyOption(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "studyOption");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		programmeDetailsValidator.validate(programmeDetail, mappingResult);
		EasyMock.verify(programInstanceDAOMock);

		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("studyOption").getCode());
		Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
	}

	@Test
	@DirtiesContext
	public void shouldRejectIfStartDateIsEmpty() {
		programmeDetail.setStartDate(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "startDate");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		programmeDetailsValidator.validate(programmeDetail, mappingResult);
		EasyMock.verify(programInstanceDAOMock);

		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldErrors("startDate").get(0).getCode());
	}

	@Test
	@DirtiesContext
	public void shouldRejectIfStartDateIsFutureDate() {
		programmeDetail.setStartDate(DateUtils.addDays(new Date(), -1));
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "startDate");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		programmeDetailsValidator.validate(programmeDetail, mappingResult);
		EasyMock.verify(programInstanceDAOMock);
	
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldErrors("startDate").get(0).getCode());
	}

	@Test
	@DirtiesContext
	public void shouldRejectIfSourcesOfInterestIsEmpty() {
		programmeDetail.setSourcesOfInterest(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "sourcesOfInterest");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		programmeDetailsValidator.validate(programmeDetail, mappingResult);
		EasyMock.verify(programInstanceDAOMock);

		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("sourcesOfInterest").getCode());
		Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
	}
	
	@Test
	@DirtiesContext
	public void shouldRejectIfSourcesOfInterestFreeTextIsEmpty() {
	    SourcesOfInterest sourcesOfInterest = new SourcesOfInterestBuilder().id(1).code("OTHER").name("Other").enabled(true).toSourcesOfInterest();
	    programmeDetail.setSourcesOfInterest(sourcesOfInterest);
	    DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "sourcesOfInterest");
	    EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(Arrays.asList(programInstance));
	    EasyMock.replay(programInstanceDAOMock);
	    programmeDetailsValidator.validate(programmeDetail, mappingResult);
	    EasyMock.verify(programInstanceDAOMock);

	    Assert.assertEquals(2, mappingResult.getErrorCount());
	    Assert.assertEquals("text.field.empty", mappingResult.getFieldError("sourcesOfInterestText").getCode());
	    Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
	}
	
	@Test
	@DirtiesContext
    public void shouldRejectIfStartDateIsNotInRange() {
	    programmeDetail.setStartDate(DateUtils.addYears(new Date(), 5));
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "startDate");
        EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(Arrays.asList(programInstance));
        EasyMock.replay(programInstanceDAOMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programInstanceDAOMock);

        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
    }

	@Test
	@DirtiesContext
	public void shouldRejectIfSuggestedSupervisorFirstNameIsEmpty() {
		programmeDetail.getSuggestedSupervisors().get(0).setFirstname(null);
		BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(programmeDetail, "suggestedSupervisors");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		programmeDetailsValidator.validate(programmeDetail, mappingResult);
		EasyMock.verify(programInstanceDAOMock);

		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("suggestedSupervisors").getCode());
		Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
	}
	
    @Test
    @DirtiesContext
    public void shouldRejectIfSuggestedSupervisorFirstNameContainsInvalidCharacter() {
        String chineseName = StringEscapeUtils.unescapeJava("\\u5b9d\\u8912\\u82de\\n");
        programmeDetail.getSuggestedSupervisors().get(0).setFirstname(chineseName);
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(programmeDetail, "suggestedSupervisors");
        EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(Arrays.asList(programInstance));
        EasyMock.replay(programInstanceDAOMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programInstanceDAOMock);

        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("You must enter ASCII compliant characters.", mappingResult.getFieldError("suggestedSupervisors[0].firstname").getDefaultMessage());
        Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
    }	

    @Test
    @DirtiesContext
    public void shouldRejectIfSuggestedSupervisorFirstNameIsLongerThan30() {
        programmeDetail.getSuggestedSupervisors().get(0).setFirstname("PaulinePaulinePaulinePaulinePaulinePaulinePauline");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(programmeDetail, "suggestedSupervisors");
        EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(Arrays.asList(programInstance));
        EasyMock.replay(programInstanceDAOMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programInstanceDAOMock);

        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 30 characters are allowed.", mappingResult.getFieldError("suggestedSupervisors[0].firstname").getDefaultMessage());
        Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
    }    

	@Test
	@DirtiesContext
	public void shouldRejectIfSuggestedSupervisorLastNameIsEmpty() {
		programmeDetail.getSuggestedSupervisors().get(0).setLastname(null);
		BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(programmeDetail, "suggestedSupervisors");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		programmeDetailsValidator.validate(programmeDetail, mappingResult);
		EasyMock.verify(programInstanceDAOMock);

		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("suggestedSupervisors").getCode());
		Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
	}
	
    @Test
    @DirtiesContext
    public void shouldRejectIfSuggestedSupervisorLastNameContainsInvalidCharacter() {
        String chineseName = StringEscapeUtils.unescapeJava("\\u5b9d\\u8912\\u82de\\n");
        programmeDetail.getSuggestedSupervisors().get(0).setLastname(chineseName);
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(programmeDetail, "suggestedSupervisors");
        EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(Arrays.asList(programInstance));
        EasyMock.replay(programInstanceDAOMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programInstanceDAOMock);

        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("You must enter ASCII compliant characters.", mappingResult.getFieldError("suggestedSupervisors[0].lastname").getDefaultMessage());
        Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
    }   

    @Test
    @DirtiesContext
    public void shouldRejectIfSuggestedSupervisorLastNameIsLongerThan40() {
        programmeDetail.getSuggestedSupervisors().get(0).setLastname("PaulinePaulinePaulinePaulinePaulinePaulinePaulinePaulinePaulinePauline");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(programmeDetail, "suggestedSupervisors");
        EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(Arrays.asList(programInstance));
        EasyMock.replay(programInstanceDAOMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programInstanceDAOMock);

        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 40 characters are allowed.", mappingResult.getFieldError("suggestedSupervisors[0].lastname").getDefaultMessage());
        Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
    }
    
    @Test
    @DirtiesContext
    public void shouldRejectIfSuggestedSupervisorEmailIsEmpty() {
        programmeDetail.getSuggestedSupervisors().get(0).setEmail(null);
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(programmeDetail, "suggestedSupervisors");
        EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(Arrays.asList(programInstance));
        EasyMock.replay(programInstanceDAOMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programInstanceDAOMock);

        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("text.field.empty", mappingResult.getFieldError("suggestedSupervisors").getCode());
        Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
    }
    
    @Test
    @DirtiesContext
    public void shouldRejectIfSuggestedSupervisorEmailContainsInvalidCharacter() {
        programmeDetail.getSuggestedSupervisors().get(0).setEmail("paul@never.com!");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(programmeDetail, "suggestedSupervisors");
        EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(Arrays.asList(programInstance));
        EasyMock.replay(programInstanceDAOMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programInstanceDAOMock);

        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("You must enter a valid email address.", mappingResult.getFieldError("suggestedSupervisors[0].email").getDefaultMessage());
        Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
    }   

    @Test
    @DirtiesContext
    public void shouldRejectIfSuggestedSupervisorEmailIsLongerThan255() {
        programmeDetail.getSuggestedSupervisors().get(0).setEmail("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890@a.com");
        BeanPropertyBindingResult mappingResult = new BeanPropertyBindingResult(programmeDetail, "suggestedSupervisors");
        EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(Arrays.asList(programInstance));
        EasyMock.replay(programInstanceDAOMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programInstanceDAOMock);

        Assert.assertEquals(2, mappingResult.getErrorCount());
        Assert.assertEquals("A maximum of 255 characters are allowed.", mappingResult.getFieldError("suggestedSupervisors[0].email").getDefaultMessage());
        Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
    }    

	@Test
	@DirtiesContext
	public void shouldRejectIfStudyOptionDoesNotExistInTheProgrammeInstances() {
		programmeDetail.setStudyOption("Part-time");
		programmeDetail.setStudyOptionCode("31");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "studyOption");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(null);
		EasyMock.replay(programInstanceDAOMock);
		programmeDetailsValidator.validate(programmeDetail, mappingResult);
		EasyMock.verify(programInstanceDAOMock);

		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("programmeDetails.studyOption.invalid", mappingResult.getFieldError("studyOption").getCode());
	}

	@Test
	@DirtiesContext
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
	@DirtiesContext
	public void shouldRejectIfApplicationSubmittedAndTermsAcceptedIsFalse() {
		form.setStatus(ApplicationFormStatus.VALIDATION);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "acceptedTerms");
		programmeDetailsValidator.validate(programmeDetail, mappingResult);

		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("acceptedTerms").getCode());
	}
	
	@Test
	@DirtiesContext
	public void shouldNotRejectIfApplicationsubmittedAndTermsAcceptedIsTrue() {
		programmeDetail.setAcceptedTerms(true);
		form.setStatus(ApplicationFormStatus.VALIDATION);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "acceptedTerms");
		programmeDetailsValidator.validate(programmeDetail, mappingResult);

		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	@DirtiesContext
	public void shouldNotRejectIfApplicationUnsubmittedAndTermsAcceptedIsFalse() {
		form.setStatus(ApplicationFormStatus.UNSUBMITTED);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "acceptedTerms");
		programmeDetailsValidator.validate(programmeDetail, mappingResult);

		Assert.assertEquals(1, mappingResult.getErrorCount());
	}

	@Before
	public void setup() throws ParseException {
	    SourcesOfInterest interest = new SourcesOfInterestBuilder().id(1).name("ZZ").code("ZZ").toSourcesOfInterest();
		Role role = new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole();
		RegisteredUser currentUser = new RegisteredUserBuilder().id(1).role(role).toUser();
		SuggestedSupervisor suggestedSupervisor = new SuggestedSupervisorBuilder()
		    .firstname("Mark")
		    .lastname("Johnson")
		    .email("mark@gmail.com")
		    .aware(true)
		    .toSuggestedSupervisor();
        program = new ProgramBuilder().id(1).title("Program 1").enabled(true).toProgram();
		programInstance = new ProgramInstanceBuilder()
		    .id(1)
		    .studyOption("1", "Full-time")
		    .applicationStartDate(new SimpleDateFormat("yyyy/MM/dd").parse("2025/08/06"))
		    .applicationDeadline(new SimpleDateFormat("yyyy/MM/dd").parse("2030/08/06"))
		    .enabled(true)
		    .toProgramInstance();
		program.setInstances(Arrays.asList(programInstance));
		form = new ApplicationFormBuilder()
		    .id(2)
		    .program(program)
		    .applicant(currentUser)
		    .status(ApplicationFormStatus.UNSUBMITTED)
		    .toApplicationForm();
		programmeDetail = new ProgrammeDetailsBuilder()
		    .id(5)
		    .suggestedSupervisors(suggestedSupervisor)
		    .programmeName("programmeName")
		    .sourcesOfInterest(interest)
		    .startDate(DateUtils.addDays(new Date(),10)).applicationForm(form)
		    .studyOption("1", "Full-time").toProgrammeDetails();
	}
}
