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
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class WithdrawServiceTest {

    @Mock
    @InjectIntoByType
    private ApplicationsService applicationServiceMock;

    @Mock
    @InjectIntoByType
    private PorticoQueueService porticoQueueServiceMock;

    @Mock
    @InjectIntoByType
    private ApplicationFormUserRoleService applicationFormUserRoleServiceMock;

    @TestedObject
    private WithdrawService service;

    @Test
    public void shouldWithdrawApplication() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.REVIEW).build();

        applicationServiceMock.save(applicationForm);
        applicationFormUserRoleServiceMock.moveToApprovedOrRejectedOrWithdrawn(applicationForm);

        replay();
        service.withdrawApplication(applicationForm);
        verify();

        assertEquals(ApplicationFormStatus.WITHDRAWN, applicationForm.getStatus());
        assertEquals(ApplicationFormStatus.REVIEW, applicationForm.getStatusWhenWithdrawn());
    }

    @Test
    public void shouldWithdrawUnsubmittedApplication() {
        ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.UNSUBMITTED).build();

        applicationServiceMock.save(applicationForm);
        applicationFormUserRoleServiceMock.moveToApprovedOrRejectedOrWithdrawn(applicationForm);

        replay();
        service.withdrawApplication(applicationForm);
        verify();

        assertEquals(ApplicationFormStatus.WITHDRAWN, applicationForm.getStatus());
        assertEquals(ApplicationFormStatus.UNSUBMITTED, applicationForm.getStatusWhenWithdrawn());
    }

    @Test
    public void shouldSendFormToPortico() {
        ApplicationForm form = new ApplicationFormBuilder().id(1).submittedDate(new Date()).status(ApplicationFormStatus.VALIDATION).build();
        expect(porticoQueueServiceMock.createOrReturnExistingApplicationFormTransfer(form)).andReturn(new ApplicationFormTransfer());

        replay();
        service.sendToPortico(form);
        verify();
    }

}
