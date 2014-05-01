package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramFeedBuilder;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class WithdrawServiceTest {

    @Mock
    @InjectIntoByType
    private ApplicationFormService applicationServiceMock;

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
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(new State().withId(PrismState.APPLICATION_REVIEW)).build();

        applicationServiceMock.save(applicationForm);
        actionService.deleteApplicationActions(applicationForm);

        replay();
        service.withdrawApplication(applicationForm);
        verify();

        assertEquals(PrismState.APPLICATION_WITHDRAWN, applicationForm.getState());
    }

    @Test
    public void shouldWithdrawUnsubmittedApplication() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(new State().withId(PrismState.APPLICATION_UNSUBMITTED)).build();

        applicationServiceMock.save(applicationForm);
        actionService.deleteApplicationActions(applicationForm);

        replay();
        service.withdrawApplication(applicationForm);
        verify();

        assertEquals(PrismState.APPLICATION_WITHDRAWN, applicationForm.getState());
    }

    @Test
    public void shouldSendFormToPortico() {
        Program program = new ProgramBuilder().programFeed(new ProgramFeedBuilder().feedUrl("test").build()).build();
        ApplicationForm form = new ApplicationFormBuilder().id(1).advert(program).submittedDate(new Date()).status(new State().withId(PrismState.APPLICATION_VALIDATION)).build();
        expect(porticoQueueServiceMock.createOrReturnExistingApplicationFormTransfer(form)).andReturn(new ApplicationFormTransfer());
        replay();
        service.sendToPortico(form);
        verify();
    }

}
