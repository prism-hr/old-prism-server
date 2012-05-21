package com.zuehlke.pgadmissions.controllers.workflow.rejection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.ui.ModelMap;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.EventBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectReasonBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.RejectService;

public class RejectApplicationControllerTest {
	private static final String VIEW_RESULT = "private/staff/approver/reject_page";
	private static final String REJECT_EMAIL = "private/pgStudents/mail/rejected_notification";
	private static final String AFTER_REJECT_VIEW = "redirect:/applications";

	private RejectApplicationController controllerUT;

	private ApplicationForm application;
	private ApplicationsService applicationServiceMock;
	private RejectService rejectServiceMock;

	private UsernamePasswordAuthenticationToken authenticationToken;
	private RegisteredUser admin;
	private RegisteredUser approver;
	private RejectReason reason1;
	private RejectReason reason2;
	private Program program;

	@Before
	public void setUp() {
		admin = new RegisteredUserBuilder().id(1).username("admin").role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		setupSecurityContext(admin);
		reason1 = new RejectReasonBuilder().id(10).text("idk").toRejectReason();
		reason2 = new RejectReasonBuilder().id(20).text("idc").toRejectReason();
		approver = new RegisteredUserBuilder().id(2).username("real approver").role(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()).toUser();
		program = new ProgramBuilder().id(100).administrators(admin).approver(approver).toProgram();
		application = new ApplicationFormBuilder().id(10).status(ApplicationFormStatus.VALIDATION)//
				.program(program)//
				.toApplicationForm();

		rejectServiceMock = EasyMock.createMock(RejectService.class);
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);

		controllerUT = new RejectApplicationController(applicationServiceMock, rejectServiceMock);
	}

	private void setupSecurityContext(RegisteredUser user) {
		authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		authenticationToken.setDetails(user);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void getRejectionPage() {
		Assert.assertEquals(VIEW_RESULT, controllerUT.getRejectPage());
	}

	// -------------------------------------------------------------------
	// ----------- check for application states:
	@Test(expected = CannotUpdateApplicationException.class)
	public void throwCUAEIfApplicationIsUnsubmitted() {
		EasyMock.expect(applicationServiceMock.getApplicationById(10)).andReturn(application);
		EasyMock.replay(applicationServiceMock);

		application.setStatus(ApplicationFormStatus.UNSUBMITTED);
		controllerUT.getApplicationForm(10);
	}

	@Test(expected = CannotUpdateApplicationException.class)
	public void throwCUAEIfApplicationIsWithdrawn() {
		EasyMock.expect(applicationServiceMock.getApplicationById(10)).andReturn(application);
		EasyMock.replay(applicationServiceMock);

		application.setStatus(ApplicationFormStatus.WITHDRAWN);
		controllerUT.getApplicationForm(10);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void throwRNFEIfApplicationDoesntExist() {
		EasyMock.expect(applicationServiceMock.getApplicationById(10)).andReturn(null);
		EasyMock.replay(applicationServiceMock);
		controllerUT.getApplicationForm(10);
	}

	@Test
	public void returnApplicationIfApplicationHasValidState() {
		// setup of application status is VALIDATION
		EasyMock.expect(applicationServiceMock.getApplicationById(10)).andReturn(application);
		EasyMock.replay(applicationServiceMock);

		ApplicationForm applicationForm = controllerUT.getApplicationForm(10);
		Assert.assertNotNull(applicationForm);
		Assert.assertEquals(application, applicationForm);
		EasyMock.verify(applicationServiceMock);
	}

	@Test
	public void returnApplicationIfApplicationIsInReviewState() {
		application.setStatus(ApplicationFormStatus.REVIEW);
		returnApplicationIfApplicationHasValidState();
	}

	@Test
	public void returnApplicationIfApplicationIsInApprovalState() {
		application.setStatus(ApplicationFormStatus.APPROVAL);
		returnApplicationIfApplicationHasValidState();
	}

	@Test
	public void returnApplicationIfApplicationIsInInterviewState() {
		application.setStatus(ApplicationFormStatus.INTERVIEW);
		returnApplicationIfApplicationHasValidState();
	}

	// -------------------------------------------------------------------
	// ----------- check for user roles:
	@Test(expected = ResourceNotFoundException.class)
	public void throwRNFEIfUserIsApplicant() {
		EasyMock.expect(applicationServiceMock.getApplicationById(10)).andReturn(application);
		EasyMock.replay(applicationServiceMock);
		RegisteredUser applicant = new RegisteredUserBuilder().id(2023).username("applicant").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		authenticationToken.setDetails(applicant);

		controllerUT.getApplicationForm(10);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void throwRNFEIfUserIsNotApproverOfApplication() {
		EasyMock.expect(applicationServiceMock.getApplicationById(10)).andReturn(application);
		EasyMock.replay(applicationServiceMock);
		RegisteredUser wrongApprover = new RegisteredUserBuilder().id(656).username("wrongApprover").role(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()).toUser();
		authenticationToken.setDetails(wrongApprover);

		controllerUT.getApplicationForm(10);
	}

	@Test
	public void returnApplicationIfUserIsApprover() {
		authenticationToken.setDetails(approver);
		EasyMock.expect(applicationServiceMock.getApplicationById(10)).andReturn(application);
		EasyMock.replay(applicationServiceMock);

		ApplicationForm applicationForm = controllerUT.getApplicationForm(10);
		Assert.assertNotNull(applicationForm);
		Assert.assertEquals(application, applicationForm);
		EasyMock.verify(applicationServiceMock);
	}

	// -----------------------------------------
	// ------ Retrieve all available reasons:
	@Test
	public void getAvailbalbeReasons() {
		List<RejectReason> values = new ArrayList<RejectReason>();
		values.add(reason1);
		values.add(reason2);
		EasyMock.expect(rejectServiceMock.getAllRejectionReasons()).andReturn(values);
		EasyMock.replay(rejectServiceMock);

		List<RejectReason> allReasons = controllerUT.getAvailableReasons();

		EasyMock.verify(rejectServiceMock);
		Assert.assertNotNull(allReasons);
		Assert.assertTrue(allReasons.containsAll(values));
	}

	// -------------------------------------------
	// ------- move application to reject:

	@Test
	public void moveToRejectWithOneReason() {
		authenticationToken.setDetails(approver);

		List<RejectReason> reasons = Arrays.asList(new RejectReason[] { reason1, reason2 });
		rejectServiceMock.moveApplicationToReject(application, approver, reasons);
		EasyMock.expectLastCall();

		RejectReason reason3 = new RejectReasonBuilder().id(30).text("bla").toRejectReason();
		List<RejectReason> allReasons = Arrays.asList(new RejectReason[] { reason1, reason2, reason3 });
		EasyMock.expect(rejectServiceMock.getAllRejectionReasons()).andReturn(allReasons);
		EasyMock.replay(rejectServiceMock);

		String nextView = controllerUT.moveApplicationToReject(application, new Integer[] { reason1.getId(), reason2.getId() });

		EasyMock.verify(rejectServiceMock);
		Assert.assertEquals(AFTER_REJECT_VIEW, nextView);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void moveToReviewThrowRNFEWhenInvalidUser() {
		RegisteredUser applicant = new RegisteredUserBuilder().id(156).username("appl")//
				.role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole())//
				.toUser();
		authenticationToken.setDetails(applicant);
		controllerUT.moveApplicationToReject(application, new Integer[] { reason1.getId() });
	}

	@Test(expected = IllegalArgumentException.class)
	public void moveToReviewThrowIAEwhenNullReasonIds() {
		controllerUT.moveApplicationToReject(application, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void moveToReviewThrowIAEwhenEmptyReasonIds() {
		controllerUT.moveApplicationToReject(application, new Integer[] {});
	}

	// -------------------------------------------
	// ------- retrieving email text:
	@Test(expected = IllegalArgumentException.class)
	public void getEmailTextThrowIAEwhenNullReasonIds() {
		controllerUT.getRejectionText(application, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getEmailTextThrowIAEwhenEmptyReasonIds() {
		controllerUT.getRejectionText(application, new Integer[] {}, null);
	}

	@Test
	public void getRejectionText() {
		RejectReason reason3 = new RejectReasonBuilder().id(30).text("bla").toRejectReason();
		List<RejectReason> allReasons = Arrays.asList(new RejectReason[] { reason1, reason2, reason3 });
		EasyMock.expect(rejectServiceMock.getAllRejectionReasons()).andReturn(allReasons);
		EasyMock.replay(rejectServiceMock);

		ModelMap modelMap = new ModelMap();
		String nextView = controllerUT.getRejectionText(application, new Integer[] { reason1.getId(), reason2.getId() }, modelMap);

		EasyMock.verify(rejectServiceMock);
		Assert.assertEquals(REJECT_EMAIL, nextView);
		Assert.assertEquals(application, modelMap.get("application"));
		@SuppressWarnings("unchecked")
		Collection<RejectReason> providedReasons = (Collection<RejectReason>) modelMap.get("reasons");
		Assert.assertEquals(2, providedReasons.size());
		Assert.assertTrue(providedReasons.contains(reason1));
		Assert.assertTrue(providedReasons.contains(reason2));

		Assert.assertNotNull(modelMap.get("host"));
		Assert.assertNotNull(modelMap.get("adminsEmails"));
	}
	
	
}
