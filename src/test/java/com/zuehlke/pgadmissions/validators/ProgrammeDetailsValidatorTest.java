package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ProgrammeDetail;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.AwareStatus;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.Referrer;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;

public class ProgrammeDetailsValidatorTest {

	private ProgrammeDetailsValidator programmeDetailsValidator;
	private ProgrammeDetail programmeDetail;
	
	@Test
	public void shouldSupportProgrammeDetails() {
		assertTrue(programmeDetailsValidator.supports(ProgrammeDetail.class));
	}
	
	@Test
	public void shouldRejectIfProgrammeNameIsEmpty() {
		programmeDetail.setProgrammeName(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "programmeName");
		programmeDetailsValidator.validate(programmeDetail, mappingResult);
		System.out.println(mappingResult.getAllErrors());
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.programmeName.notempty", mappingResult.getFieldError("programmeName").getCode());
	}
	@Test
	public void shouldRejectIfStudyOptionIsEmpty() {
		programmeDetail.setStudyOption(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "studyOption");
		programmeDetailsValidator.validate(programmeDetail, mappingResult);
		System.out.println(mappingResult.getAllErrors());
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.studyOption.notempty", mappingResult.getFieldError("studyOption").getCode());
	}
	@Test
	public void shouldRejectIfStartDateIsEmpty() {
		programmeDetail.setStartDate(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "startDate");
		programmeDetailsValidator.validate(programmeDetail, mappingResult);
		System.out.println(mappingResult.getAllErrors());
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.programmeStartDate.notempty", mappingResult.getFieldError("startDate").getCode());
	}
	@Test
	public void shouldRejectIfReferrerIsEmpty() {
		programmeDetail.setReferrer(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "referrer");
		programmeDetailsValidator.validate(programmeDetail, mappingResult);
		System.out.println(mappingResult.getAllErrors());
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.programmeReferrer.notempty", mappingResult.getFieldError("referrer").getCode());
	}
	@Test
	public void shouldRejectIfSupervisorFirstNameIsEmpty() {
		programmeDetail.getSupervisors().get(0).setFirstname(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "supervisors");
		programmeDetailsValidator.validate(programmeDetail, mappingResult);
		System.out.println(mappingResult.getAllErrors());
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("programmeDetails.firstname.notempty", mappingResult.getFieldError("supervisors").getCode());
	}
	@Test
	public void shouldRejectIfSupervisorLastNameIsEmpty() {
		programmeDetail.getSupervisors().get(0).setLastname(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "supervisors");
		programmeDetailsValidator.validate(programmeDetail, mappingResult);
		System.out.println(mappingResult.getAllErrors());
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("programmeDetails.lastname.notempty", mappingResult.getFieldError("supervisors").getCode());
	}
	@Test
	public void shouldRejectIfSupervisorEmailIsNonValid() {
		programmeDetail.getSupervisors().get(0).setEmail("");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "supervisors");
		programmeDetailsValidator.validate(programmeDetail, mappingResult);
		System.out.println(mappingResult.getAllErrors());
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("programmeDetails.email.invalid", mappingResult.getFieldError("supervisors").getCode());
	}
	
	@Test
	public void shouldRejectIfResidenceFromDateIsEmpty() {
		programmeDetail.getSupervisors().get(0).setEmail("");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(programmeDetail, "supervisors");
		programmeDetailsValidator.validate(programmeDetail, mappingResult);
		System.out.println(mappingResult.getAllErrors());
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("programmeDetails.email.invalid", mappingResult.getFieldError("supervisors").getCode());
	}
	
	@Before
	public void setup() throws ParseException{
		Role role = new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole();
		RegisteredUser currentUser = new RegisteredUserBuilder().id(1).role(role).toUser();
		Supervisor supervisor = new SupervisorBuilder().firstname("Mark").lastname("Johnson").email("mark@gmail.com").awareSupervisor(AwareStatus.YES).primarySupervisor(CheckedStatus.YES).toSupervisor();
		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.UNSUBMITTED).applicant(currentUser).toApplicationForm();
		
		programmeDetail = new ProgrammeDetailsBuilder().id(5).supervisors(supervisor).programmeName("programmeName").referrer(Referrer.OPTION_1).startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/08/06")).applicationForm(form).studyOption(StudyOption.FULL_TIME).toProgrammeDetails();
		programmeDetailsValidator = new ProgrammeDetailsValidator();
	}
}
