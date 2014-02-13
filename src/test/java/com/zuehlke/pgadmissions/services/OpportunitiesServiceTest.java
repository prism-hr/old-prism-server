package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.Matchers.contains;
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

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.OpportunityRequestDAO;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.OpportunityRequestComment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder;
import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestCommentType;
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
    private RoleService roleService;

    @Mock
    @InjectIntoByType
    private ProgramsService programsService;

    @Mock
    @InjectIntoByType
    private ProgramInstanceService programInstanceService;

    @Mock
    @InjectIntoByType
    private MailSendingService mailSendingService;

    @Mock
    @InjectIntoByType
    private UserService userService;

    @TestedObject
    private OpportunitiesService service = new OpportunitiesService();

    @Test
    public void shouldCreateNewOpportunityRequestAndAuthor() {
        RegisteredUser author = new RegisteredUser();
        OpportunityRequest opportunityRequest = new OpportunityRequestBuilder().author(author).build();

        expect(registrationService.updateOrSaveUser(author, null)).andReturn(null);
        opportunityRequestDAO.save(opportunityRequest);

        replay();
        service.createNewOpportunityRequestAndAuthor(opportunityRequest);
        verify();

        assertNotNull(opportunityRequest.getCreatedDate());
        assertEquals(OpportunityRequestStatus.NEW, opportunityRequest.getStatus());
    }

    @Test
    public void shouldGetOpportunityRequests() {
        List<OpportunityRequest> requests = Lists.newArrayList();

        expect(opportunityRequestDAO.getInitialOpportunityRequests()).andReturn(requests);

        replay();
        List<OpportunityRequest> returned = service.getInitialOpportunityRequests();
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
        Role administratorRole = new RoleBuilder().id(Authority.ADMINISTRATOR).build();
        RegisteredUser author = new RegisteredUser();
        RegisteredUser currentUser = new RegisteredUser();
        OpportunityRequest request = new OpportunityRequestBuilder().author(author).build();
        Domicile country = new Domicile();
        OpportunityRequest newOpportunityRequest = OpportunityRequestBuilder.aOpportunityRequest(null, country).otherInstitution("jakis uniwerek").build();
        QualificationInstitution institution = new QualificationInstitution();
        Program program = new ProgramBuilder().institution(institution).build();
        OpportunityRequestComment comment = new OpportunityRequestCommentBuilder().content("ok").commentType(OpportunityRequestCommentType.APPROVE).build();

        expect(opportunityRequestDAO.findById(8)).andReturn(request);
        expect(programsService.saveProgramOpportunity(request)).andReturn(program);
        expect(roleService.getRoleByAuthority(Authority.ADMINISTRATOR)).andReturn(administratorRole);
        expect(userService.getCurrentUser()).andReturn(currentUser);

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
        assertSame(program, request.getSourceProgram());

        OpportunityRequestComment returncomment = Iterables.getOnlyElement(request.getComments());
        assertSame(currentUser, returncomment.getAuthor());
        assertEquals("ok", returncomment.getContent());
        assertEquals(OpportunityRequestCommentType.APPROVE, returncomment.getCommentType());

        assertThat(author.getInstitutions(), contains(institution));
        assertThat(author.getProgramsOfWhichAdministrator(), contains(program));
        assertThat(author.getRoles(), contains(administratorRole));
    }

    @Test(expected = RuntimeException.class)
    public void shouldPreventOpportunityRequestFromBeingChangeWhenAlreadyApproved() {
        OpportunityRequest request = new OpportunityRequestBuilder().status(OpportunityRequestStatus.APPROVED).build();

        expect(opportunityRequestDAO.findById(8)).andReturn(request);

        replay();
        service.respondToOpportunityRequest(8, request, null);
    }

}
