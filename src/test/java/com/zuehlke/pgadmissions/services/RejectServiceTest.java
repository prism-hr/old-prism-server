package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
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
import com.zuehlke.pgadmissions.domain.Rejection;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectReasonBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.StateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.mail.MailSendingService;

public class RejectServiceTest {

	private RejectService rejectService;

	private RejectReasonDAO rejectDaoMock;
	
	private ApplicationFormDAO applicationDaoMock;

	private ApplicationForm application;
	
	private RejectReason reason1;
	
	private RejectReason reason2;
	
	private RegisteredUser admin;
	
	private RegisteredUser approver;

	private EventFactory eventFactoryMock;

	private MailSendingService mailServiceMock;
	
	private PorticoQueueService porticoQueueService;
	
	@Before
	public void setUp() {
		applicationDaoMock = EasyMock.createMock(ApplicationFormDAO.class);		
		rejectDaoMock = EasyMock.createMock(RejectReasonDAO.class);
		eventFactoryMock = EasyMock.createMock(EventFactory.class);
		porticoQueueService = createMock(PorticoQueueService.class);
		mailServiceMock = createMock(MailSendingService.class);

		admin = new RegisteredUserBuilder().id(324).username("admin").role(new RoleBuilder().authorityEnum(Authority.ADMINISTRATOR).build()).build();
		approver = new RegisteredUserBuilder().id(22414).username("real approver").role(new RoleBuilder().authorityEnum(Authority.APPROVER).build()).build();
		Program program = new ProgramBuilder().id(10023).administrators(admin).approver(approver).build();
		application = new ApplicationFormBuilder().id(200).program(program).status(ApplicationFormStatus.VALIDATION).build();

		reason1 = new RejectReasonBuilder().id(1).text("idk").build();
		reason2 = new RejectReasonBuilder().id(2).text("idc").build();
		
		rejectService = new RejectService(applicationDaoMock, rejectDaoMock, eventFactoryMock, porticoQueueService, mailServiceMock);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIAEIfRejectionIsNull() {
		rejectService.moveApplicationToReject(application, approver, null);
	}


	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIAEIfApproverIsNull() {
		rejectService.moveApplicationToReject(application, null, new Rejection());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIAEIfWrongApprover() {
		RegisteredUser wrongApprover = new RegisteredUserBuilder().id(3423).username("wrong approver").role(new RoleBuilder().authorityEnum(Authority.APPROVER).build()).build();
		rejectService.moveApplicationToReject(application, wrongApprover, new Rejection());
	}

	@Test
	public void shouldMoveToReject() {
		Rejection rejection = new RejectionBuilder().id(1).build();
		
		applicationDaoMock.save(application);
		EasyMock.expectLastCall();
		
		StateChangeEvent event = new StateChangeEventBuilder().id(1).build();
		EasyMock.expect(eventFactoryMock.createEvent(ApplicationFormStatus.REJECTED)).andReturn(event);
		
		EasyMock.replay(applicationDaoMock, eventFactoryMock);

		rejectService.moveApplicationToReject(application, approver, rejection);

		EasyMock.verify(applicationDaoMock);
		Assert.assertEquals(ApplicationFormStatus.REJECTED, application.getStatus());
		Assert.assertEquals(approver, application.getApprover());
		Assert.assertEquals(rejection,application.getRejection());
		assertEquals(1, application.getEvents().size());
		assertEquals(event, application.getEvents().get(0));
	}

	@Test
	public void shouldMoveToRejectAsAdministrator() {
		Rejection rejection = new RejectionBuilder().id(1).build();
		applicationDaoMock.save(application);
		EasyMock.expectLastCall();
		EasyMock.replay(applicationDaoMock);

		rejectService.moveApplicationToReject(application, admin,rejection);

		EasyMock.verify(applicationDaoMock);
		Assert.assertEquals(ApplicationFormStatus.REJECTED, application.getStatus());
		Assert.assertEquals(rejection,application.getRejection());
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
	
	@Test
	public void shouldGetRejectReasonById(){
		RejectReason rejectReason = new RejectReasonBuilder().id(1).build();
		EasyMock.expect(rejectDaoMock.getRejectReasonById(1)).andReturn(rejectReason);
		EasyMock.replay(rejectDaoMock);
		
		Assert.assertEquals(rejectReason,rejectService.getRejectReasonById(1));
	}
}
