package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationTransfer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class WithdrawServiceTest {

    @Mock
    @InjectIntoByType
    private ApplicationService applicationServiceMock;

    @Mock
    @InjectIntoByType
    private ExportQueueService porticoQueueServiceMock;

    @Mock
    @InjectIntoByType
    private ActionService actionService;

    @TestedObject
    private WithdrawService service;

    @Test
    public void shouldWithdrawApplication() {
        Application applicationForm = new ApplicationFormBuilder().id(1).status(new State().withId(PrismState.APPLICATION_REVIEW)).build();

        applicationServiceMock.saveUpdate(applicationForm);

        replay();
        service.withdrawApplication(applicationForm);
        verify();

        assertEquals(PrismState.APPLICATION_WITHDRAWN, applicationForm.getState());
    }

    @Test
    public void shouldWithdrawUnsubmittedApplication() {
        Application applicationForm = new ApplicationFormBuilder().id(1).status(new State().withId(PrismState.APPLICATION_UNSUBMITTED)).build();

        applicationServiceMock.saveUpdate(applicationForm);

        replay();
        service.withdrawApplication(applicationForm);
        verify();

        assertEquals(PrismState.APPLICATION_WITHDRAWN, applicationForm.getState());
    }

    @Test
    public void shouldSendFormToPortico() {
        Program program = new Program();
        Application form = new ApplicationFormBuilder().id(1).program(program).submittedDate(new DateTime()).status(new State().withId(PrismState.APPLICATION_VALIDATION)).build();
        expect(porticoQueueServiceMock.createOrReturnExistingApplicationFormTransfer(form)).andReturn(new ApplicationTransfer());
        replay();
        service.sendToPortico(form);
        verify();
    }

}
