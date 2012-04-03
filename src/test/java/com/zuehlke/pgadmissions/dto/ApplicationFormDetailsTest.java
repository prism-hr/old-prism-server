package com.zuehlke.pgadmissions.dto;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Nationality;
import com.zuehlke.pgadmissions.domain.PersonalDetail;
import com.zuehlke.pgadmissions.domain.ProgrammeDetail;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.AwareStatus;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.Referrer;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.validators.ApplicationFormValidator;


public class ApplicationFormDetailsTest {

	private ApplicationFormValidator validator;
	private ApplicationFormDetails appFormDetails;
	private PersonalDetail personalDetails;
	private ProgrammeDetail programmeDetails;

	@Test
	public void shouldSupportAppFormDetails() {
		assertTrue(validator.supports(ApplicationFormDetails.class));
	}
	
	@Test
	public void shouldAcceptAppFormDetails() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(appFormDetails, "applicationFormDetails");
		validator.validate(appFormDetails, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfNoAddresses() {
		appFormDetails.setNumberOfAddresses(0);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(appFormDetails, "applicationFormDetails");
		validator.validate(appFormDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfNoContactAddresses() {
		appFormDetails.setNumberOfContactAddresses(0);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(appFormDetails, "applicationFormDetails");
		validator.validate(appFormDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfMoreThanOneContactAddress() {
		appFormDetails.setNumberOfContactAddresses(4);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(appFormDetails, "applicationFormDetails");
		validator.validate(appFormDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfNoReferees() {
		appFormDetails.setNumberOfReferees(0);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(appFormDetails, "applicationFormDetails");
		validator.validate(appFormDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfNoProgrammeDetails() {
		appFormDetails.setProgrammeDetails(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(appFormDetails, "applicationFormDetails");
		validator.validate(appFormDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfIncompleteProgrammeDetails() {
		programmeDetails.setProgrammeName(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(appFormDetails, "applicationFormDetails");
		validator.validate(appFormDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfNoPersonalDetails() {
		appFormDetails.setPersonalDetails(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(appFormDetails, "applicationFormDetails");
		validator.validate(appFormDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfIncompletePersonalDetails() {
		personalDetails.setEmail(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(appFormDetails, "applicationFormDetails");
		validator.validate(appFormDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfIncompleteUploadedDocuments() {
		appFormDetails.setSupportingDocuments(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(appFormDetails, "applicationFormDetails");
		validator.validate(appFormDetails, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
	}
	
	@Before
	public void setup() throws ParseException{
		validator = new ApplicationFormValidator();
		
		appFormDetails = new ApplicationFormDetails();
		appFormDetails.setNumberOfAddresses(1);
		appFormDetails.setNumberOfContactAddresses(1);
		appFormDetails.setNumberOfReferees(2);
		List<Document> supportingDocuments = new ArrayList<Document>();
		Document resume = new Document();
		resume.setType(DocumentType.CV);
		supportingDocuments.add(resume);
		
		Document personalStatement = new Document();
		personalStatement.setType(DocumentType.PERSONAL_STATEMENT);
		supportingDocuments.add(personalStatement);
		appFormDetails.setSupportingDocuments(supportingDocuments);
		
		Nationality nationality = new Nationality();
		personalDetails = new PersonalDetailsBuilder().candiateNationalities(nationality).maternalGuardianNationalities(nationality).paternalGuardianNationalities(nationality).applicationForm(new ApplicationFormBuilder().id(2).toApplicationForm()).country(new CountryBuilder().toCountry()).dateOfBirth(new Date()).email("email@test.com").firstName("bob")
		.gender(Gender.PREFER_NOT_TO_SAY).lastName("smith").residenceCountry(new CountryBuilder().toCountry()).toPersonalDetails();
		appFormDetails.setPersonalDetails(personalDetails);
		
		Role role = new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole();
		RegisteredUser currentUser = new RegisteredUserBuilder().id(1).role(role).toUser();
		Supervisor supervisor = new SupervisorBuilder().firstname("Mark").lastname("Johnson").email("mark@gmail.com").awareSupervisor(AwareStatus.YES).primarySupervisor(CheckedStatus.YES).toSupervisor();
		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.UNSUBMITTED).applicant(currentUser).toApplicationForm();
		programmeDetails = new ProgrammeDetailsBuilder().id(5).supervisors(supervisor).programmeName("programmeName").referrer(Referrer.OPTION_1).startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/08/06")).applicationForm(form).studyOption(StudyOption.FULL_TIME).toProgrammeDetails();
		appFormDetails.setProgrammeDetails(programmeDetails);
	}
}
