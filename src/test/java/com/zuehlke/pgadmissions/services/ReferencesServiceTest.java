package com.zuehlke.pgadmissions.services;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.ProgrammeDetail;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.AddressBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.EmploymentPositionBuilder;
import com.zuehlke.pgadmissions.domain.builders.FundingBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.dto.RegistrationDTO;
import com.zuehlke.pgadmissions.utils.MimeMessagePreparatorFactory;


public class ReferencesServiceTest {
	
	private ApplicationsService applicationsServiceMock;
	private ReferencesService referencesService;
	private JavaMailSender javaMailSenderMock;
	private MimeMessagePreparatorFactory mimeMessagePreparatorFactoryMock;

	@Before
	public void setUp(){
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		javaMailSenderMock = EasyMock.createMock(JavaMailSender.class);
		mimeMessagePreparatorFactoryMock = EasyMock.createMock(MimeMessagePreparatorFactory.class);
		
		referencesService = new ReferencesService(mimeMessagePreparatorFactoryMock, javaMailSenderMock, applicationsServiceMock);

	}

	@Test
	public void shouldSaveApplicationFormAndSendEmailsToRefereesAdminsAndApplicant() throws UnsupportedEncodingException{
		Address address1 = new AddressBuilder().id(1).toAddress();
		Address address2 = new AddressBuilder().id(2).toAddress();
		EmploymentPosition position = new EmploymentPositionBuilder().id(1).toEmploymentPosition();
		Referee referee1 = new RefereeBuilder().refereeId(1).toReferee();
		Referee referee2 = new RefereeBuilder().refereeId(2).toReferee();
		Qualification qualification = new QualificationBuilder().id(1).toQualification();
		com.zuehlke.pgadmissions.domain.Funding funding = new FundingBuilder().toFunding();
		
		RegisteredUser currentUser = new RegisteredUserBuilder().id(1).toUser();
		ApplicationForm form = new ApplicationFormBuilder().referees(referee1, referee2).qualification(qualification).id(2).employmentPosition(position).fundings(funding).
				applicant(currentUser).addresses(address1, address2).toApplicationForm();
		ProgrammeDetail programmeDetails = new ProgrammeDetail();
		programmeDetails.setId(1);
		form.setProgrammeDetails(programmeDetails);
		applicationsServiceMock.save(form);
		
		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("email@test.com", "bob bobson");
		EasyMock.expect(
				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress), EasyMock.eq("Referee Notification"),EasyMock.eq("private/referees/mail/referee_notification_email.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock);

		javaMailSenderMock.send(preparatorMock);
		EasyMock.expectLastCall().andThrow(new RuntimeException("AARrrgggg"));
		EasyMock.replay(applicationsServiceMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);
	
		
		referencesService.saveApplicationFormAndSendMailNotifications(form);
		EasyMock.verify(applicationsServiceMock);
	}
	
	@Test
	public void shouldNotSendEmailIfSaveFails() throws UnsupportedEncodingException {
		applicationsServiceMock.save(null);
		EasyMock.expectLastCall().andThrow(new RuntimeException("aaaaaaaaaaargh"));

		EasyMock.replay(applicationsServiceMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);
		try {
			referencesService.saveApplicationFormAndSendMailNotifications(null);
		} catch (RuntimeException e) {
			// expected...ignore
		}

		EasyMock.verify(applicationsServiceMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);
	}
	
	@Test
	public void shouldNotThrowExceptionIfEmailSendingFails() throws UnsupportedEncodingException {
		Address address1 = new AddressBuilder().id(1).toAddress();
		Address address2 = new AddressBuilder().id(2).toAddress();
		EmploymentPosition position = new EmploymentPositionBuilder().id(1).toEmploymentPosition();
		Referee referee1 = new RefereeBuilder().refereeId(1).toReferee();
		Referee referee2 = new RefereeBuilder().refereeId(2).toReferee();
		Qualification qualification = new QualificationBuilder().id(1).toQualification();
		com.zuehlke.pgadmissions.domain.Funding funding = new FundingBuilder().toFunding();
		
		RegisteredUser currentUser = new RegisteredUserBuilder().id(1).toUser();
		ApplicationForm form = new ApplicationFormBuilder().referees(referee1, referee2).qualification(qualification).id(2).employmentPosition(position).fundings(funding).
				applicant(currentUser).addresses(address1, address2).toApplicationForm();
		ProgrammeDetail programmeDetails = new ProgrammeDetail();
		programmeDetails.setId(1);
		form.setProgrammeDetails(programmeDetails);
		applicationsServiceMock.save(form);

		MimeMessagePreparator preparatorMock = EasyMock.createMock(MimeMessagePreparator.class);
		InternetAddress toAddress = new InternetAddress("email@test.com", "bob bobson");
//		EasyMock.expect(
//				mimeMessagePreparatorFactoryMock.getMimeMessagePreparator(EasyMock.eq(toAddress), EasyMock.eq("Referee Notification"),EasyMock.eq("private/referees/mail/referee_notification_email.ftl"), EasyMock.isA(Map.class))).andReturn(preparatorMock);

		javaMailSenderMock.send(preparatorMock);
		EasyMock.expectLastCall().andThrow(new RuntimeException("AARrrgggg"));
		EasyMock.replay(applicationsServiceMock, mimeMessagePreparatorFactoryMock, javaMailSenderMock);
		referencesService.saveApplicationFormAndSendMailNotifications(form);

		EasyMock.verify(applicationsServiceMock, mimeMessagePreparatorFactoryMock);

	}

	
	

}
