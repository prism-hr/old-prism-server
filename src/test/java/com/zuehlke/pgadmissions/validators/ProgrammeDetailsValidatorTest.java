package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.SuggestedSupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.Referrer;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;

public class ProgrammeDetailsValidatorTest {

	private ProgrammeDetailsValidator programmeDetailsValidator;
	private ProgrammeDetails programmeDetail;
	private ProgramInstance programInstance;
	private ProgramInstanceDAO programInstanceDAOMock;
	private Program program;
	private ApplicationForm form;

	@Test
	public void shouldSupportProgrammeDetails() {
		assertTrue(programmeDetailsValidator.supports(ProgrammeDetails.class));
	}

	@Test
	public void shouldRejectIfProgrammeNameIsEmpty() {
		programmeDetail.setProgrammeName(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "programmeName");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(
				Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		programmeDetailsValidator.validate(programmeDetail, mappingResult);
		EasyMock.verify(programInstanceDAOMock);
		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("programmeName").getCode());
		Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
	}

	@Test
	public void shouldRejectIfStudyOptionIsEmpty() {
		programmeDetail.setStudyOption(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "studyOption");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(
				Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		programmeDetailsValidator.validate(programmeDetail, mappingResult);
		EasyMock.verify(programInstanceDAOMock);

		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("studyOption").getCode());
		Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
	}

	@Test
	public void shouldRejectIfStartDateIsEmpty() {
		programmeDetail.setStartDate(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "startDate");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(
				Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		programmeDetailsValidator.validate(programmeDetail, mappingResult);
		EasyMock.verify(programInstanceDAOMock);

		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldErrors("startDate").get(0).getCode());
		Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldErrors("startDate").get(1).getCode());
	}

	@Test
	public void shouldRejectIfStartDateIsFutureDate() {
		programmeDetail.setStartDate(DateUtils.addDays(new Date(), -1));
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "startDate");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(
				Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		programmeDetailsValidator.validate(programmeDetail, mappingResult);
		EasyMock.verify(programInstanceDAOMock);
	
		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("date.field.notfuture", mappingResult.getFieldErrors("startDate").get(0).getCode());
		Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldErrors("startDate").get(1).getCode());
	}

	@Test
	public void shouldRejectIfReferrerIsEmpty() {
		programmeDetail.setReferrer(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "referrer");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(
				Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		programmeDetailsValidator.validate(programmeDetail, mappingResult);
		EasyMock.verify(programInstanceDAOMock);

		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("referrer").getCode());
		Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
	}
	
	@Test
    public void shouldRejectIfStartDateIsNotInRange() {
	    programmeDetail.setStartDate(DateUtils.addYears(new Date(), 5));
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "startDate");
        EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(
                Arrays.asList(programInstance));
        EasyMock.replay(programInstanceDAOMock);
        programmeDetailsValidator.validate(programmeDetail, mappingResult);
        EasyMock.verify(programInstanceDAOMock);

        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
    }

	@Test
	public void shouldRejectIfSuggestedSupervisorFirstNameIsEmpty() {
		programmeDetail.getSuggestedSupervisors().get(0).setFirstname(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "suggestedSupervisors");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(
				Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		programmeDetailsValidator.validate(programmeDetail, mappingResult);
		EasyMock.verify(programInstanceDAOMock);

		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("suggestedSupervisors").getCode());
		Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
	}

	@Test
	public void shouldRejectIfSuggestedSupervisorLastNameIsEmpty() {
		programmeDetail.getSuggestedSupervisors().get(0).setLastname(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "suggestedSupervisors");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(
				Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		programmeDetailsValidator.validate(programmeDetail, mappingResult);
		EasyMock.verify(programInstanceDAOMock);

		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("suggestedSupervisors").getCode());
		Assert.assertEquals("programmeDetails.startDate.invalid", mappingResult.getFieldError("startDate").getCode());
	}

	@Test
	public void shouldRejectIfStudyOptionDoesNotExistInTheProgrammeInstances() {
		programmeDetail.setStudyOption(StudyOption.FULL_TIME_DISTANCE);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "studyOption");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPastAndSortByDeadline(program, programmeDetail.getStudyOption())).andReturn(
				null);
		EasyMock.replay(programInstanceDAOMock);
		programmeDetailsValidator.validate(programmeDetail, mappingResult);
		EasyMock.verify(programInstanceDAOMock);

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

		form.setStatus(ApplicationFormStatus.VALIDATION);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "acceptedTerms");
		programmeDetailsValidator.validate(programmeDetail, mappingResult);

		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("acceptedTerms").getCode());
	}
	@Test
	public void shouldNotRejectIfApplicationsubmittedAndTermsAcceptedIsTrue() {

		programmeDetail.setAcceptedTerms(true);
		form.setStatus(ApplicationFormStatus.VALIDATION);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "acceptedTerms");
		programmeDetailsValidator.validate(programmeDetail, mappingResult);

		Assert.assertEquals(1, mappingResult.getErrorCount());


	}
	@Test
	public void shouldNotRejectIfApplicationUnsubmittedAndTermsAcceptedIsFalse() {

		form.setStatus(ApplicationFormStatus.UNSUBMITTED);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "acceptedTerms");
		programmeDetailsValidator.validate(programmeDetail, mappingResult);

		Assert.assertEquals(1, mappingResult.getErrorCount());
	}

	@Before
	public void setup() throws ParseException {
		Role role = new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole();
		RegisteredUser currentUser = new RegisteredUserBuilder().id(1).role(role).toUser();
		SuggestedSupervisor suggestedSupervisor = new SuggestedSupervisorBuilder().firstname("Mark").lastname("Johnson").email("mark@gmail.com")
				.aware(true).toSuggestedSupervisor();
		program = new ProgramBuilder().id(1).title("Program 1").toProgram();
		programInstance = new ProgramInstanceBuilder().id(1).studyOption(StudyOption.FULL_TIME)
				.applicationDeadline(new SimpleDateFormat("yyyy/MM/dd").parse("2030/08/06")).toProgramInstance();
		program.setInstances(Arrays.asList(programInstance));
		form = new ApplicationFormBuilder().id(2).program(program).applicant(currentUser).status(ApplicationFormStatus.UNSUBMITTED).toApplicationForm();
		programmeDetail = new ProgrammeDetailsBuilder().id(5).suggestedSupervisors(suggestedSupervisor).programmeName("programmeName")
				.referrer(Referrer.OPTION_1).startDate(DateUtils.addDays(new Date(),10)).applicationForm(form)
				.studyOption(StudyOption.FULL_TIME).toProgrammeDetails();
		programInstanceDAOMock = EasyMock.createMock(ProgramInstanceDAO.class);
		programmeDetailsValidator = new ProgrammeDetailsValidator(programInstanceDAOMock);
	}
}
