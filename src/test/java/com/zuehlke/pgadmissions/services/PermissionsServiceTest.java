package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.OpportunityRequestDAO;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.OpportunityRequestComment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder;
import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestCommentType;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class PermissionsServiceTest {

    @Mock
    @InjectIntoByType
    private AuthenticationService authenticationService;

    @Mock
    @InjectIntoByType
    private OpportunityRequestDAO opportunityRequestDAO;

    @Mock
    @InjectIntoByType
    private ProgramService programsService;

    @TestedObject
    private PermissionsService service;

    @Test
    public void shouldBeAbleToSeeOpportunityRequestsIfSuperadmin() {
        RegisteredUser registeredUser = new RegisteredUserBuilder().role(new RoleBuilder().id(Authority.SUPERADMINISTRATOR).build()).build();

        expect(authenticationService.getCurrentUser()).andReturn(registeredUser);

        replay();
        assertTrue(service.canSeeOpportunityRequests());
        verify();
    }

    @Test
    public void shouldBeAbleToSeeOpportunityRequestsIfRequestAuthor() {
        RegisteredUser registeredUser = new RegisteredUser();

        expect(authenticationService.getCurrentUser()).andReturn(registeredUser);
        expect(opportunityRequestDAO.getOpportunityRequestsForAuthor(registeredUser)).andReturn(Collections.singletonList(new OpportunityRequest()));

        replay();
        assertTrue(service.canSeeOpportunityRequests());
        verify();
    }

    @Test
    public void shouldNotBeAbleToSeeOpportunityRequests() {
        RegisteredUser registeredUser = new RegisteredUser();

        expect(authenticationService.getCurrentUser()).andReturn(registeredUser);
        expect(opportunityRequestDAO.getOpportunityRequestsForAuthor(registeredUser)).andReturn(Collections.<OpportunityRequest> emptyList());

        replay();
        assertFalse(service.canSeeOpportunityRequests());
        verify();
    }

    @Test
    public void shouldRequestAuthorNotBeAbleToApproveOpportunityRequest() {
        RegisteredUser registeredUser = new RegisteredUserBuilder().id(53425345).build();
        OpportunityRequest opportunityRequest = new OpportunityRequestBuilder().author(registeredUser).build();
        OpportunityRequestComment comment = new OpportunityRequestCommentBuilder().commentType(OpportunityRequestCommentType.APPROVE).build();

        expect(authenticationService.getCurrentUser()).andReturn(registeredUser).anyTimes();

        replay();
        assertFalse(service.canPostOpportunityRequestComment(opportunityRequest, comment));
        verify();
    }

    @Test
    public void shouldRequestAuthorBeAbleToReviseOpportunityRequest() {
        RegisteredUser registeredUser = new RegisteredUserBuilder().id(53425345).build();
        OpportunityRequest opportunityRequest = new OpportunityRequestBuilder().author(registeredUser).build();
        OpportunityRequestComment comment = new OpportunityRequestCommentBuilder().commentType(OpportunityRequestCommentType.REVISE).build();

        expect(authenticationService.getCurrentUser()).andReturn(registeredUser).anyTimes();

        replay();
        assertTrue(service.canPostOpportunityRequestComment(opportunityRequest, comment));
        verify();
    }

    @Test
    public void shouldRequestAuthorBeAbleToSeeOpportunityRequest() {
        RegisteredUser registeredUser = new RegisteredUserBuilder().id(53425345).build();
        OpportunityRequest opportunityRequest = new OpportunityRequestBuilder().author(registeredUser).build();

        expect(authenticationService.getCurrentUser()).andReturn(registeredUser);

        replay();
        assertTrue(service.canSeeOpportunityRequest(opportunityRequest));
        verify();
    }

    @Test
    public void shouldBeAbleToManageProjects() {
        RegisteredUser registeredUser = new RegisteredUser();

        expect(authenticationService.getCurrentUser()).andReturn(registeredUser);
        expect(programsService.getProgramsForWhichCanManageProjects(registeredUser)).andReturn(Collections.singletonList(new Program()));

        replay();
        assertTrue(service.canManageProjects());
        verify();
    }

}
