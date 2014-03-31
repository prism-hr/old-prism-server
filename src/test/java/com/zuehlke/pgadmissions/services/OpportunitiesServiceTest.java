package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.OpportunityRequestDAO;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.OpportunityRequestComment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder;
import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestCommentType;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestStatus;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestType;
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
    private ProgramService programsService;

    @Mock
    @InjectIntoByType
    private ProgramInstanceService programInstanceService;

    @Mock
    @InjectIntoByType
    private MailSendingService mailSendingService;

    @Mock
    @InjectIntoByType
    private UserService userService;

    @Mock
    @InjectIntoByType
    private ApplicationContext applicationContext;

    @TestedObject
    private OpportunitiesService service = new OpportunitiesService();

    @Test
    public void shouldCreateNewOpportunityRequest() {
        RegisteredUser author = new RegisteredUser();
        OpportunityRequest opportunityRequest = new OpportunityRequestBuilder().author(author).programTitle("dupa").build();

        expect(registrationService.updateOrSaveUser(author, null)).andReturn(null);
        opportunityRequestDAO.save(opportunityRequest);

        replay();
        service.createOpportunityRequest(opportunityRequest, true);
        verify();

        assertNotNull(opportunityRequest.getCreatedDate());
        assertEquals(OpportunityRequestStatus.NEW, opportunityRequest.getStatus());
        assertEquals(OpportunityRequestType.CREATE, opportunityRequest.getType());
        assertEquals("dupa", opportunityRequest.getProgramTitle());
        assertFalse(opportunityRequest.getAtasRequired());
    }

    @Test
    public void shouldCreateOpportunityChangeRequest() {
        Program program = new ProgramBuilder().title("tytul").atasRequired(true).build();
        RegisteredUser author = new RegisteredUser();
        OpportunityRequest opportunityRequest = new OpportunityRequestBuilder().author(author).sourceProgram(program).build();

        expect(opportunityRequestDAO.findByProgramAndStatus(null, OpportunityRequestStatus.NEW)).andReturn(Collections.<OpportunityRequest> emptyList());
        opportunityRequestDAO.save(opportunityRequest);
        expect(programsService.merge(program)).andReturn(program);

        replay();
        service.createOpportunityRequest(opportunityRequest, false);
        verify();

        assertNotNull(opportunityRequest.getCreatedDate());
        assertEquals(OpportunityRequestStatus.NEW, opportunityRequest.getStatus());
        assertEquals(OpportunityRequestType.CHANGE, opportunityRequest.getType());
        assertEquals("tytul", opportunityRequest.getProgramTitle());
        assertTrue(opportunityRequest.getAtasRequired());
        assertTrue(program.getLocked());
    }

    @Test
    public void shouldlistOpportunityRequests() {
        List<OpportunityRequest> requests = Lists.newArrayList();
        RegisteredUser user = new RegisteredUser();

        expect(opportunityRequestDAO.listOpportunityRequests(user)).andReturn(requests);

        replay();
        List<OpportunityRequest> returned = service.listOpportunityRequests(user);
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
    public void shouldRespondToOpportunityRequest() {
        RegisteredUser author = new RegisteredUser();
        RegisteredUser currentUser = new RegisteredUser();
        Program program = new ProgramBuilder().locked(true).build();
        Program savedProgram = new Program();
        OpportunityRequest request = new OpportunityRequestBuilder().id(666).author(author).sourceProgram(program).build();
        Domicile country = new Domicile();
        OpportunityRequest newOpportunityRequest = OpportunityRequestBuilder.aOpportunityRequest(null, country).otherInstitution("jakis uniwerek").build();
        OpportunityRequestComment comment = new OpportunityRequestCommentBuilder().content("ok").commentType(OpportunityRequestCommentType.APPROVE).build();
        OpportunitiesService thisBeanMock = EasyMockUnitils.createMock(OpportunitiesService.class);

        expect(applicationContext.getBean(OpportunitiesService.class)).andReturn(thisBeanMock);
        expect(thisBeanMock.getAllRelatedOpportunityRequests(request)).andReturn(Lists.newArrayList(request, new OpportunityRequest()));
        expect(opportunityRequestDAO.findById(8)).andReturn(request);
        expect(userService.getCurrentUser()).andReturn(currentUser);
        expect(programsService.merge(program)).andReturn(program);
        expect(programsService.saveProgramOpportunity(request)).andReturn(savedProgram);
        mailSendingService.sendOpportunityRequestOutcome(comment);

        replay();
        service.respondToOpportunityRequest(8, newOpportunityRequest, comment);
        verify();

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
        assertFalse(program.getLocked());
        assertSame(savedProgram, request.getSourceProgram());

        OpportunityRequestComment returncomment = Iterables.getOnlyElement(request.getComments());
        assertSame(comment, returncomment);
        assertSame(currentUser, returncomment.getAuthor());
        assertEquals("ok", returncomment.getContent());
        assertEquals(OpportunityRequestCommentType.APPROVE, returncomment.getCommentType());

    }

    @Test(expected = RuntimeException.class)
    public void shouldPreventOpportunityRequestFromBeingChangeWhenAlreadyApproved() {
        OpportunityRequest request = new OpportunityRequestBuilder().status(OpportunityRequestStatus.APPROVED).build();

        expect(applicationContext.getBean(OpportunitiesService.class)).andReturn(null);
        expect(opportunityRequestDAO.findById(8)).andReturn(request);

        replay();
        service.respondToOpportunityRequest(8, request, null);
    }

    @Test(expected = RuntimeException.class)
    public void shouldPreventDeprecatedOpportunityRequestFromBeingChange() {
        OpportunityRequest request = new OpportunityRequestBuilder().id(8).status(OpportunityRequestStatus.NEW).build();
        OpportunityRequestComment comment = new OpportunityRequestCommentBuilder().content("ok").commentType(OpportunityRequestCommentType.REJECT).build();
        OpportunitiesService thisBeanMock = EasyMockUnitils.createMock(OpportunitiesService.class);
        RegisteredUser currentUser = new RegisteredUser();

        expect(applicationContext.getBean(OpportunitiesService.class)).andReturn(thisBeanMock);
        expect(thisBeanMock.getAllRelatedOpportunityRequests(request)).andReturn(Lists.newArrayList(new OpportunityRequestBuilder().id(99).build(), request));
        expect(opportunityRequestDAO.findById(8)).andReturn(request);
        expect(userService.getCurrentUser()).andReturn(currentUser).anyTimes();

        replay();
        service.respondToOpportunityRequest(8, request, comment);
    }

}
