package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.RejectReasonDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.Rejection;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectReasonBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectionBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.mail.MailSendingService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class RejectServiceTest {

    @TestedObject
    private RejectService rejectService;

    @Mock
    @InjectIntoByType
    private RejectReasonDAO rejectDaoMock;

    @Mock
    @InjectIntoByType
    private ApplicationFormDAO applicationDaoMock;

    @Mock
    @InjectIntoByType
    private MailSendingService mailServiceMock;

    @Mock
    @InjectIntoByType
    private ExportQueueService porticoQueueService;

    @Mock
    @InjectIntoByType
    private ActionService actionService;

    private ApplicationForm application;

    private RejectReason reason1;

    private RejectReason reason2;

    private RegisteredUser admin;

    private RegisteredUser approver;

    @Before
    public void setUp() {
        admin = new RegisteredUserBuilder().id(324).username("admin").build();
        approver = new RegisteredUserBuilder().id(22414).username("real approver").build();
        Program program = new ProgramBuilder().id(10023).administrators(admin).approver(approver).build();
        application = new ApplicationFormBuilder().id(200).advert(program).status(ApplicationFormStatus.VALIDATION).build();

        reason1 = new RejectReasonBuilder().id(1).text("idk").build();
        reason2 = new RejectReasonBuilder().id(2).text("idc").build();
    }

    @Test
    public void shouldMoveToReject() {
        Rejection rejection = new RejectionBuilder().id(1).build();

        applicationDaoMock.save(application);
        expectLastCall();

        mailServiceMock.sendRejectionConfirmationToApplicant(application);
        actionService.deleteApplicationActions(application);

        replay();
        rejectService.moveApplicationToReject(application, rejection);
        verify();

        assertEquals(ApplicationFormStatus.REJECTED, application.getStatus());
        assertEquals(rejection, application.getRejection());
    }

    @Test
    public void loadAllRejections() {
        List<RejectReason> values = new ArrayList<RejectReason>();
        values.add(reason1);
        values.add(reason2);
        expect(rejectDaoMock.getAllReasons()).andReturn(values);

        replay();
        List<RejectReason> reasons = rejectService.getAllRejectionReasons();
        verify();

        assertNotNull(reasons);
        assertEquals(2, reasons.size());
        assertEquals("idc", reasons.get(1).getText());
    }

    @Test
    public void shouldGetRejectReasonById() {
        RejectReason rejectReason = new RejectReasonBuilder().id(1).build();
        expect(rejectDaoMock.getRejectReasonById(1)).andReturn(rejectReason);

        replay();
        assertEquals(rejectReason, rejectService.getRejectReasonById(1));
        verify();
    }
}
