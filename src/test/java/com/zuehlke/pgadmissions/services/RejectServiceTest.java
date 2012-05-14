package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.RejectReasonDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectReasonBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class RejectServiceTest {

	private RejectService rejectService;

	private RejectReasonDAO rejectDaoMock;
	private ApplicationFormDAO applicationDaoMock;

	private ApplicationForm application;
	private RejectReason reason1;
	private RejectReason reason2;
	private RegisteredUser admin;
	private RegisteredUser approver;

	@Before
	public void setUp() {
		applicationDaoMock = EasyMock.createMock(ApplicationFormDAO.class);
		rejectDaoMock = EasyMock.createMock(RejectReasonDAO.class);

		admin = new RegisteredUserBuilder().id(324).username("admin").role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).toRole()).toUser();
		approver = new RegisteredUserBuilder().id(22414).username("real approver").role(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()).toUser();
		Program program = new ProgramBuilder().id(10023).administrators(admin).approver(approver).toProgram();
		application = new ApplicationFormBuilder().id(200).program(program).status(ApplicationFormStatus.VALIDATION).toApplicationForm();

		reason1 = new RejectReasonBuilder().id(1).text("idk").toRejectReason();
		reason2 = new RejectReasonBuilder().id(2).text("idc").toRejectReason();
		rejectService = new RejectService(applicationDaoMock, rejectDaoMock);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIAEIfNullReasons() {
		rejectService.moveApplicationToReject(application, approver, (Collection<RejectReason>) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIAEIfEmptyReasons() {
		rejectService.moveApplicationToReject(application, approver, new ArrayList<RejectReason>());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIAEIfAReasonIsNull() {
		rejectService.moveApplicationToReject(application, approver, Arrays.asList(new RejectReason[] { null }));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIAEIfApproverIsNull() {
		rejectService.moveApplicationToReject(application, null, Arrays.asList(new RejectReason[] { reason1 }));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIAEIfWrongApprover() {
		RegisteredUser wrongApprover = new RegisteredUserBuilder().id(3423).username("wrong approver").role(new RoleBuilder().authorityEnum(Authority.APPROVER).toRole()).toUser();
		rejectService.moveApplicationToReject(application, wrongApprover, Arrays.asList(new RejectReason[] { reason1 }));
	}

	@Test
	public void shouldMoveToReject() {
		applicationDaoMock.save(application);
		EasyMock.expectLastCall();
		EasyMock.replay(applicationDaoMock);

		rejectService.moveApplicationToReject(application, approver, Arrays.asList(new RejectReason[] { reason1, reason2 }));

		EasyMock.verify(applicationDaoMock);
		Assert.assertEquals(ApplicationFormStatus.REJECTED, application.getStatus());
		List<RejectReason> rejectReasons = application.getRejectReasons();
		Assert.assertTrue(rejectReasons.contains(reason1));
		Assert.assertTrue(rejectReasons.contains(reason2));
		Assert.assertEquals(approver, application.getApprover());
	}

	@Test
	public void shouldMoveToRejectAsAdministrator() {
		applicationDaoMock.save(application);
		EasyMock.expectLastCall();
		EasyMock.replay(applicationDaoMock);

		rejectService.moveApplicationToReject(application, admin, Arrays.asList(new RejectReason[] { reason1, reason2 }));

		EasyMock.verify(applicationDaoMock);
		Assert.assertEquals(ApplicationFormStatus.REJECTED, application.getStatus());
		List<RejectReason> rejectReasons = application.getRejectReasons();
		Assert.assertTrue(rejectReasons.contains(reason1));
		Assert.assertTrue(rejectReasons.contains(reason2));
		Assert.assertEquals(admin, application.getApprover());
	}

	@Test
	public void loadAllRejections() {
		List<RejectReason> values = new ArrayList<RejectReason>();
		values.add(reason1);
		values.add(reason2);
		EasyMock.expect(rejectDaoMock.getAllReasons()).andReturn(values);
		EasyMock.replay(rejectDaoMock);

		List<RejectReason> reasons = rejectService.getAllRejectionReasons();

		EasyMock.verify(rejectDaoMock);
		Assert.assertNotNull(reasons);
		Assert.assertEquals(2, reasons.size());
		Assert.assertEquals("idc", reasons.get(1).getText());
	}
}
