package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;

public class ApplicationFormValidatorTest {

	private ApplicationFormValidator validator;
	private ApplicationForm applicationForm;
	private ProgramInstanceDAO programInstanceDAOMock;
	private ProgramInstance programInstance;
	private ProgrammeDetails programmeDetails;

	@Test
	public void shouldSupportAppForm() {
		assertTrue(validator.supports(ApplicationForm.class));
	}

	@Test
	public void shouldRejectIfProgrammeDetailsSectionMissing() {
		applicationForm.setProgrammeDetails(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("user.programmeDetails.incomplete", mappingResult.getFieldError("programmeDetails").getCode());

	}
	@Test
	public void shouldRejectIfProgrammeDetailsSectionNotSaved() {
		applicationForm.setProgrammeDetails(new ProgrammeDetails());
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(2, mappingResult.getErrorCount());
		Assert.assertEquals("user.programmeDetails.incomplete", mappingResult.getFieldError("programmeDetails").getCode());
	}
	@Test
	public void shouldRejectIfPersonalDetailsSectionMissing() {
		applicationForm.setPersonalDetails(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(programmeDetails.getStudyOption())).andReturn(Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.personalDetails.incomplete", mappingResult.getFieldError("personalDetails").getCode());

	}
	@Test
	public void shouldRejectIfPersonalDetailsSectionNotSaved() {
		applicationForm.setPersonalDetails(new PersonalDetails());
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(programmeDetails.getStudyOption())).andReturn(Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.personalDetails.incomplete", mappingResult.getFieldError("personalDetails").getCode());

	}

	@Test
	public void shouldRejectIfCurrentAddressIsMissing() {
		applicationForm.setCurrentAddress(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(programmeDetails.getStudyOption())).andReturn(Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.addresses.notempty", mappingResult.getFieldError("currentAddress").getCode());

	}

	@Test
	public void shouldRejectIfContactAddressIsMissing() {
		applicationForm.setContactAddress(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(programmeDetails.getStudyOption())).andReturn(Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.addresses.notempty", mappingResult.getFieldError("contactAddress").getCode());

	}
	
	@Test
	public void shouldRejectIfFewerThanThreeReferees() {
		applicationForm.getReferees().remove(2);		
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(programmeDetails.getStudyOption())).andReturn(Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("user.referees.notvalid", mappingResult.getFieldError("referees").getCode());

	}
	
	@Test
	public void shouldRejectIfPersonalStatementNotProvided() {
		applicationForm.setPersonalStatement(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(programmeDetails.getStudyOption())).andReturn(Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("documents.section.invalid", mappingResult.getFieldError("personalStatement").getCode());
	}
	
	@Test
	public void shouldRejectIfTooMuchAddtionalInfo() {
		StringBuilder addnInfo = new StringBuilder();
		for (int i = 0; i <=5000; i++) {
			addnInfo.append("a");
		}
		
		applicationForm.setAdditionalInformation(addnInfo.toString());
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "applicationForm");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(programmeDetails.getStudyOption())).andReturn(Arrays.asList(programInstance));
		EasyMock.replay(programInstanceDAOMock);
		validator.validate(applicationForm, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfStudyOptionDoesNotExistInTheProgrammeInstances(){
		ProgrammeDetails programmeDetail = applicationForm.getProgrammeDetails();
		programmeDetail.setStudyOption(StudyOption.FULL_TIME_DISTANCE);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationForm, "programmeDetails");
		EasyMock.expect(programInstanceDAOMock.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(programmeDetail.getStudyOption())).andReturn(null);
		EasyMock.replay(programInstanceDAOMock);
		validator.validate(applicationForm, mappingResult);
		EasyMock.verify(programInstanceDAOMock);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("programmeDetails.studyOption.invalid", mappingResult.getFieldError("programmeDetails").getCode());

	}
	@Before
	public void setup() throws ParseException {
		programInstanceDAOMock = EasyMock.createMock(ProgramInstanceDAO.class);
		validator = new ApplicationFormValidator(programInstanceDAOMock);
		Program program = new ProgramBuilder().id(1).title("Program 1").toProgram();
		programInstance = new ProgramInstanceBuilder().id(1).studyOption(StudyOption.FULL_TIME).applicationDeadline(new SimpleDateFormat("yyyy/MM/dd").parse("2030/08/06")).toProgramInstance();
		program.setInstances(Arrays.asList(programInstance));
		programmeDetails = new ProgrammeDetailsBuilder().studyOption(StudyOption.FULL_TIME).id(2).toProgrammeDetails();
		applicationForm = new ApplicationFormBuilder().program(program).programmeDetails(programmeDetails).personalDetails(new PersonalDetailsBuilder().id(1).toPersonalDetails())
				.currentAddress(new Address()).contactAddress(new Address()).referees(new Referee(), new Referee(), new Referee()).personalStatement(new Document()).toApplicationForm();
	}

}