package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.OpportunityRequestDAO;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestStatus;
import com.zuehlke.pgadmissions.mail.MailSendingService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class OpportunitiesServiceTest {

    @Mock
    @InjectIntoByType
    private RegistrationService registrationService;

    @Mock
    @InjectIntoByType
    private OpportunityRequestDAO opportunityRequestDAO;

    @Mock
    @InjectIntoByType
    private ProgramsService programsService;

    @Mock
    @InjectIntoByType
    private ProgramInstanceService programInstanceService;

    @Mock
    @InjectIntoByType
    private MailSendingService mailSendingService;

    @TestedObject
    private OpportunitiesService service = new OpportunitiesService();

    @Test
    public void shouldCreateNewOpportunityRequestAndAuthor() {
        RegisteredUser author = new RegisteredUser();
        OpportunityRequest opportunityRequest = new OpportunityRequestBuilder().author(author).build();

        expect(registrationService.updateOrSaveUser(author, null)).andReturn(null);
        opportunityRequestDAO.save(opportunityRequest);

        replay();
        service.createOpportunityRequestAndAuthor(opportunityRequest);
        verify();

        assertNotNull(opportunityRequest.getCreatedDate());
        assertEquals(OpportunityRequestStatus.NEW, opportunityRequest.getStatus());
    }

    @Test
    public void shouldGetOpportunityRequests() {
        List<OpportunityRequest> requests = Lists.newArrayList();

        expect(opportunityRequestDAO.getOpportunityRequests()).andReturn(requests);

        replay();
        List<OpportunityRequest> returned = service.getOpportunityRequests();
        verify();

        assertSame(requests, returned);
    }

    @Test
    public void shouldGetOpportunityRequest() {
        OpportunityRequest request = new OpportunityRequest();

        expect(opportunityRequestDAO.findById(8)).andReturn(request);

        replay();
        OpportunityRequest returned = service.getOpportunityRequest(8);
        verify();

        assertSame(request, returned);
    }

    @Test
    public void shouldApproveOpportunityRequest() {
        OpportunityRequest request = new OpportunityRequest();
        Domicile country = new Domicile();
        OpportunityRequest newOpportunityRequest = OpportunityRequestBuilder.aOpportunityRequest(null, country).otherInstitution("jakis uniwerek").build();
        Program program = new Program();
        ProgramInstance programInstance1 = new ProgramInstance();
        ProgramInstance programInstance2 = new ProgramInstance();

        expect(opportunityRequestDAO.findById(8)).andReturn(request);
        expect(programsService.createNewCustomProgram(request)).andReturn(program);
        expect(programInstanceService.createRemoveProgramInstances(program, Lists.newArrayList("B+++++", "F+++++"), 2014)).andReturn(
                Lists.newArrayList(programInstance1, programInstance2));

        replay();
        service.approveOpportunityRequest(8, newOpportunityRequest);
        verify();

        assertThat(program.getInstances(), containsInAnyOrder(programInstance1, programInstance2));
        assertEquals(OpportunityRequestStatus.APPROVED, request.getStatus());
        assertSame(newOpportunityRequest.getInstitutionCountry(), request.getInstitutionCountry());
        assertEquals(newOpportunityRequest.getInstitutionCode(), request.getInstitutionCode());
        assertEquals(newOpportunityRequest.getOtherInstitution(), request.getOtherInstitution());
        assertEquals(newOpportunityRequest.getProgramTitle(), request.getProgramTitle());
        assertEquals(newOpportunityRequest.getProgramDescription(), request.getProgramDescription());
        assertEquals(newOpportunityRequest.getStudyDuration(), request.getStudyDuration());
        assertEquals(newOpportunityRequest.getAtasRequired(), request.getAtasRequired());
        assertEquals(newOpportunityRequest.getAdvertisingDeadlineYear(), request.getAdvertisingDeadlineYear());
        assertEquals(newOpportunityRequest.getStudyOptions(), request.getStudyOptions());
    }

    @Test
    public void shouldRejectOpportunityRequest() {
        OpportunityRequest request = new OpportunityRequest();
        expect(opportunityRequestDAO.findById(8)).andReturn(request);
        mailSendingService.sendOpportunityRequestRejectionConfirmation(request);

        replay();
        service.rejectOpportunityRequest(8, "because");
        verify();

        assertEquals(OpportunityRequestStatus.REJECTED, request.getStatus());
        assertEquals("because", request.getRejectionReason());
    }

}
