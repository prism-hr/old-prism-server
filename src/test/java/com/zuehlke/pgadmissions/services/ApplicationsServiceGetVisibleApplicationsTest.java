package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormListDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormUserRoleDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormUserRoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationsFilterBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationsFilteringBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.SourcesOfInterestBuilder;
import com.zuehlke.pgadmissions.domain.builders.SuggestedSupervisorBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SearchPredicate;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;

public class ApplicationsServiceGetVisibleApplicationsTest extends AutomaticRollbackTestCase {

    private static final int APPLICATION_BLOCK_SIZE = ApplicationsService.APPLICATION_BLOCK_SIZE;

    private RegisteredUser applicant;

    private RegisteredUser superUser;

    private ApplicationFormListDAO applicationFormListDAO;

    private ApplicationFormDAO applicationFormDAO;

    private Program program;

    private ApplicationsService applicationsService;

    private RoleDAO roleDAO;

	private ApplicationFormUserRoleDAO applicationFormUserRoleDAO;
	
	private ApplicationFormUserRoleService applicationFormUserRoleService;

    @Before
    public void prepare() {
    	
        applicationFormListDAO = new ApplicationFormListDAO(sessionFactory);

        applicationFormDAO = new ApplicationFormDAO(sessionFactory);
        
        applicationFormUserRoleDAO = new ApplicationFormUserRoleDAO(sessionFactory);
        
        applicationFormUserRoleService = new ApplicationFormUserRoleService();

        applicationsService = new ApplicationsService(applicationFormDAO, applicationFormListDAO, null, null, null, applicationFormUserRoleDAO, applicationFormUserRoleService);

        roleDAO = new RoleDAO(sessionFactory);

        applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false)
                .enabled(true).build();

        superUser = new RegisteredUserBuilder().firstName("John").lastName("Doe").email("email@test.com").username("superUserUsername").password("password")
                .role(roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR)).accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false)
                .enabled(true).build();

        program = new ProgramBuilder().code("doesntexist").title("another title").build();

        save(applicant, superUser, program);

        flushAndClearSession();
    }

    @Test
    public void shouldGetListOfVisibleApplicationsFromDAOAndSetDefaultValues() {
        ApplicationForm form = new ApplicationFormBuilder().id(1).build();
        ApplicationDescriptor applicationDescriptor = new ApplicationDescriptor();
        applicationDescriptor.setApplicationFormId(form.getId());
        
        ApplicationFormListDAO applicationFormDAOMock = EasyMock.createMock(ApplicationFormListDAO.class);
        ApplicationsService applicationsService = new ApplicationsService(null, applicationFormDAOMock, null, null, null, applicationFormUserRoleDAO, applicationFormUserRoleService);
        RegisteredUser user = new RegisteredUserBuilder().id(1).username("bob").role(new RoleBuilder().id(Authority.APPLICANT).build()).build();
        ApplicationsFiltering filtering = newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1);
        EasyMock.expect(applicationFormDAOMock.getVisibleApplicationsForList(user, filtering, APPLICATION_BLOCK_SIZE)).andReturn(Arrays.asList(applicationDescriptor));
        EasyMock.replay(applicationFormDAOMock);
        List<ApplicationDescriptor> visibleApplications = applicationsService.getAllVisibleAndMatchedApplicationsForList(user, filtering);
        EasyMock.verify(applicationFormDAOMock);
        Assert.assertTrue(visibleApplications.contains(form));
        Assert.assertEquals(1, visibleApplications.size());
    }

    @Test
    public void shouldGetApplicationsOrderedByCreationDateThenSubmissionDateFirst() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).applicant(applicant).status(ApplicationFormStatus.VALIDATION)
                .submittedDate(new Date()).appDate(format.parse("01 01 2012")).build();
        ApplicationDescriptor applicationDescriptorOne = new ApplicationDescriptor();
        applicationDescriptorOne.setApplicationFormId(applicationFormOne.getId());

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(program).applicant(applicant).status(ApplicationFormStatus.UNSUBMITTED)
                .appDate(format.parse("01 01 2012")).submittedDate(format.parse("01 04 2012")).build();
        ApplicationDescriptor applicationDescriptorTwo = new ApplicationDescriptor();
        applicationDescriptorTwo.setApplicationFormId(applicationFormTwo.getId());

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().program(program).applicant(applicant).status(ApplicationFormStatus.UNSUBMITTED)
                .appDate(format.parse("01 02 2012")) // this is insertable=false. We can not properly test this?
                .build();
        ApplicationDescriptor applicationDescriptorThree = new ApplicationDescriptor();
        applicationDescriptorThree.setApplicationFormId(applicationFormThree.getId());

        ApplicationForm applicationFormFour = new ApplicationFormBuilder().program(program).applicant(applicant).status(ApplicationFormStatus.UNSUBMITTED)
                .appDate(format.parse("01 02 2012")) // this is insertable=false. We can not properly test this?
                .submittedDate(format.parse("01 03 2012")).build();
        ApplicationDescriptor applicationDescriptorFour = new ApplicationDescriptor();
        applicationDescriptorFour.setApplicationFormId(applicationFormFour.getId());

        Role role = roleDAO.getRoleByAuthority(Authority.APPLICANT);
        ApplicationFormUserRole applicationFormUserRole1 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormOne).role(role).user(applicant)
                .build();
        ApplicationFormUserRole applicationFormUserRole2 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormTwo).role(role).user(applicant)
                .build();
        ApplicationFormUserRole applicationFormUserRole3 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormThree).role(role)
                .user(applicant).build();
        ApplicationFormUserRole applicationFormUserRole4 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormFour).role(role).user(applicant)
                .build();

        save(applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour, applicationFormUserRole1, applicationFormUserRole2,
                applicationFormUserRole3, applicationFormUserRole4);

        flushAndClearSession();

        List<ApplicationDescriptor> applications = applicationsService.getAllVisibleAndMatchedApplicationsForList(applicant,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1));

        assertEquals(applicationFormOne.getId(), applications.get(0).getApplicationFormId());
        assertEquals(applicationFormTwo.getId(), applications.get(1).getApplicationFormId());
        assertEquals(applicationFormFour.getId(), applications.get(2).getApplicationFormId());
        assertEquals(applicationFormThree.getId(), applications.get(3).getApplicationFormId());
    }

    @Test
    public void shouldGetAllApplicationsContainingBiologyInTheirNumber() throws ParseException {

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").program(program)
                .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).status(ApplicationFormStatus.APPROVAL).build();
        ApplicationDescriptor applicationDescriptorOne = new ApplicationDescriptor();
        applicationDescriptorOne.setApplicationFormId(applicationFormOne.getId());

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicationNumber("App_Biology").program(program)
                .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).status(ApplicationFormStatus.APPROVAL).build();
        ApplicationDescriptor applicationDescriptorTwo = new ApplicationDescriptor();
        applicationDescriptorTwo.setApplicationFormId(applicationFormTwo.getId());

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().applicationNumber("ABCD").program(program)
                .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).status(ApplicationFormStatus.APPROVAL).build();
        ApplicationDescriptor applicationDescriptorThree = new ApplicationDescriptor();
        applicationDescriptorThree.setApplicationFormId(applicationFormThree.getId());
        
        ApplicationForm applicationFormFour = new ApplicationFormBuilder().applicationNumber("BIOLOGY1").program(program)
                .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).status(ApplicationFormStatus.APPROVAL).build();
        ApplicationDescriptor applicationDescriptorFour = new ApplicationDescriptor();
        applicationDescriptorFour.setApplicationFormId(applicationFormFour.getId());
        
        Role role = roleDAO.getRoleByAuthority(Authority.APPLICANT);
        ApplicationFormUserRole applicationFormUserRole1 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormOne).role(role).user(applicant)
                .build();
        ApplicationFormUserRole applicationFormUserRole2 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormTwo).role(role).user(applicant)
                .build();
        ApplicationFormUserRole applicationFormUserRole3 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormThree).role(role)
                .user(applicant).build();
        ApplicationFormUserRole applicationFormUserRole4 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormFour).role(role).user(applicant)
                .build();

        save(applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour, applicationFormUserRole1, applicationFormUserRole2,
                applicationFormUserRole3, applicationFormUserRole4);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("BiOlOgY").build();
        List<ApplicationDescriptor> applications = applicationsService.getAllVisibleAndMatchedApplicationsForList(applicant,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, filter));

        assertEquals(2, applications.size());
        assertTrue(listContainsId(applicationDescriptorFour, applications));
        assertTrue(listContainsId(applicationDescriptorTwo, applications));
    }

    @Test
    public void shouldGetApplicationBelongingToProgramWithCodeScienceAndOtherTitle() throws ParseException {
        Program programOne = new ProgramBuilder().code("Program_ZZZZZ_1").title("empty").build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC1").program(programOne).applicant(applicant)
                .status(ApplicationFormStatus.APPROVAL).build();

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicationNumber("ABC2").program(program)
                .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).status(ApplicationFormStatus.APPROVAL).build();

        Role role = roleDAO.getRoleByAuthority(Authority.APPLICANT);
        ApplicationFormUserRole applicationFormUserRole = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormOne).role(role).user(applicant)
                .build();

        save(programOne, applicationFormOne, applicationFormTwo, applicationFormUserRole);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.PROGRAMME_NAME).searchTerm("zzZZz").build();
        List<ApplicationDescriptor> applications = applicationsService.getAllVisibleAndMatchedApplicationsForList(applicant,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, filter));

        assertEquals(1, applications.size());
        assertEquals(applicationFormOne.getId(), applications.get(0).getApplicationFormId());
    }

    @Test
    public void shouldGetApplicationBelongingToProgramWithTitleScienceAndOtherCode() throws ParseException {
        Program programOne = new ProgramBuilder().code("empty").title("Program_ZZZZZ_1").build();
        ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").program(programOne).applicant(applicant)
                .status(ApplicationFormStatus.APPROVAL).build();

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicationNumber("ABC2").program(program)
                .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).status(ApplicationFormStatus.APPROVAL).build();

        Role role = roleDAO.getRoleByAuthority(Authority.APPLICANT);
        ApplicationFormUserRole applicationFormUserRole = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormOne).role(role).user(applicant)
                .build();

        save(programOne, applicationFormOne, applicationFormTwo, applicationFormUserRole);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.PROGRAMME_NAME).searchTerm("zzZZz").build();
        List<ApplicationDescriptor> applications = applicationsService.getAllVisibleAndMatchedApplicationsForList(applicant,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, filter));

        assertEquals(1, applications.size());
        assertEquals(applicationFormOne.getId(), applications.get(0).getApplicationFormId());
    }

    @Test
    public void shouldNotReturnAppIfTermNotInProgrameCodeOrTitle() {
        Program programOne = new ProgramBuilder().code("empty").title("empty").build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").program(programOne).applicant(applicant)
                .status(ApplicationFormStatus.APPROVAL).build();

        Role role = roleDAO.getRoleByAuthority(Authority.APPLICANT);
        ApplicationFormUserRole applicationFormUserRole = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormOne).role(role).user(applicant)
                .build();

        save(programOne, applicationFormOne, applicationFormUserRole);
        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.PROGRAMME_NAME).searchTerm("zzZZz").build();
        List<ApplicationDescriptor> applications = applicationsService.getAllVisibleAndMatchedApplicationsForList(applicant,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, filter));

        assertEquals(0, applications.size());
    }

    @Test
    public void shouldGetApplicationBelongingToApplicantMatchingFirstNameFred() {
        Program programOne = new ProgramBuilder().code("Program_Science_1").title("empty").build();

        RegisteredUser applicant = new RegisteredUserBuilder().firstName("FredzzZZZZZerick").lastName("Doe").email("email@test.com").username("freddy")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").program(programOne).applicant(applicant)
                .status(ApplicationFormStatus.APPROVAL).build();

        Role role = roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR);
        ApplicationFormUserRole applicationFormUserRole = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormOne).role(role).user(superUser)
                .build();

        save(programOne, applicant, applicationFormOne, applicationFormUserRole);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICANT_NAME).searchTerm("zzZZz").build();
        List<ApplicationDescriptor> applications = applicationsService.getAllVisibleAndMatchedApplicationsForList(superUser,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, filter));

        assertEquals(1, applications.size());
        assertEquals(applicationFormOne.getId(), applications.get(0).getApplicationFormId());
    }

    @Test
    public void shouldGetApplicationBelongingToApplicantMatchingLastName() {
        Program programOne = new ProgramBuilder().code("Program_Science_1").title("empty").build();

        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Frederick").lastName("FredzzZZZZZerick").email("email@test.com").username("freddy")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").program(programOne).applicant(applicant)
                .status(ApplicationFormStatus.APPROVAL).build();

        Role role = roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR);
        ApplicationFormUserRole applicationFormUserRole = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormOne).role(role).user(superUser)
                .build();

        save(programOne, applicant, applicationFormOne, applicationFormUserRole);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICANT_NAME).searchTerm("zzZZz").build();
        List<ApplicationDescriptor> applications = applicationsService.getAllVisibleAndMatchedApplicationsForList(superUser,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, filter));

        assertEquals(1, applications.size());
        assertEquals(applicationFormOne.getId(), applications.get(0).getApplicationFormId());
    }

    @Test
    public void shouldNotReturnAppIfTermNotInApplicantNameFirstOrLastName() {
        Program programOne = new ProgramBuilder().code("empty").title("empty").build();

        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Frederick").lastName("unique").email("email@test.com").username("freddy")
                .password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").program(programOne).applicant(applicant)
                .status(ApplicationFormStatus.APPROVAL).build();

        Role role = roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR);
        ApplicationFormUserRole applicationFormUserRole = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormOne).role(role).user(superUser)
                .build();

        save(programOne, applicant, applicationFormOne, applicationFormUserRole);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICANT_NAME).searchTerm("zzZZz").build();
        List<ApplicationDescriptor> applications = applicationsService.getAllVisibleAndMatchedApplicationsForList(superUser,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, filter));

        assertEquals(0, applications.size());
    }

    @Test
    public void shouldGetAllApplicationsInValidationStage() throws ParseException {
        Program program = new ProgramBuilder().code("empty").title("empty").build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).applicationNumber("ABC").program(program)
                .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();
        ApplicationDescriptor applicationDescriptorOne = new ApplicationDescriptor();
        applicationDescriptorOne.setApplicationFormId(applicationFormOne.getId());

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicationNumber("App_Biology").program(program)
                .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).applicationNumber("ABCD").program(program)
                .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();
        ApplicationDescriptor applicationDescriptorThree = new ApplicationDescriptor();
        applicationDescriptorThree.setApplicationFormId(applicationFormThree.getId());

        ApplicationForm applicationFormFour = new ApplicationFormBuilder().applicationNumber("BIOLOGY1").program(program)
                .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();

        Role role = roleDAO.getRoleByAuthority(Authority.APPLICANT);
        ApplicationFormUserRole applicationFormUserRole1 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormOne).role(role).user(applicant)
                .build();
        ApplicationFormUserRole applicationFormUserRole2 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormTwo).role(role).user(applicant)
                .build();
        ApplicationFormUserRole applicationFormUserRole3 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormThree).role(role)
                .user(applicant).build();
        ApplicationFormUserRole applicationFormUserRole4 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormFour).role(role).user(applicant)
                .build();

        save(program, applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour, applicationFormUserRole1, applicationFormUserRole2,
                applicationFormUserRole3, applicationFormUserRole4);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_STATUS).searchTerm("validati").build();
        List<ApplicationDescriptor> applications = applicationsService.getAllVisibleAndMatchedApplicationsForList(applicant,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, filter));

        assertEquals(2, applications.size());
        assertTrue(listContainsId(applicationDescriptorOne, applications));
        assertTrue(listContainsId(applicationDescriptorThree, applications));
    }

    @Test
    public void shouldGetAllApplicationsInApprovalStage() throws ParseException {
        Program program = new ProgramBuilder().code("empty").title("empty").build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVAL).applicationNumber("ABC").program(program)
                .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();
        ApplicationDescriptor applicationDescriptorOne = new ApplicationDescriptor();
        applicationDescriptorOne.setApplicationFormId(applicationFormOne.getId());
        
        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicationNumber("App_Biology").program(program)
                .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).applicationNumber("ABCD").program(program)
                .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();

        ApplicationForm applicationFormFour = new ApplicationFormBuilder().applicationNumber("BIOLOGY1").program(program)
                .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();

        Role role = roleDAO.getRoleByAuthority(Authority.APPLICANT);
        ApplicationFormUserRole applicationFormUserRole1 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormOne).role(role).user(applicant)
                .build();
        ApplicationFormUserRole applicationFormUserRole2 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormTwo).role(role).user(applicant)
                .build();
        ApplicationFormUserRole applicationFormUserRole3 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormThree).role(role)
                .user(applicant).build();
        ApplicationFormUserRole applicationFormUserRole4 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormFour).role(role).user(applicant)
                .build();

        save(program, applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour, applicationFormUserRole1, applicationFormUserRole2,
                applicationFormUserRole3, applicationFormUserRole4);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_STATUS).searchTerm("approv").build();
        List<ApplicationDescriptor> applications = applicationsService.getAllVisibleAndMatchedApplicationsForList(applicant,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, filter));

        assertEquals(1, applications.size());
        assertEquals(applicationFormOne.getId(), applications.get(0).getApplicationFormId());
    }

    @Test
    public void shouldGetAllApplicationsInApprovedStage() throws ParseException {
        Program program = new ProgramBuilder().code("empty").title("empty").build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).applicationNumber("ABC").program(program)
                .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();
        ApplicationDescriptor applicationDescriptorOne = new ApplicationDescriptor();
        applicationDescriptorOne.setApplicationFormId(applicationFormOne.getId());

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicationNumber("App_Biology").program(program)
                .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).applicationNumber("ABCD").program(program)
                .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();

        ApplicationForm applicationFormFour = new ApplicationFormBuilder().applicationNumber("BIOLOGY1").program(program)
                .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();

        Role role = roleDAO.getRoleByAuthority(Authority.APPLICANT);
        ApplicationFormUserRole applicationFormUserRole1 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormOne).role(role).user(applicant)
                .build();
        ApplicationFormUserRole applicationFormUserRole2 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormTwo).role(role).user(applicant)
                .build();
        ApplicationFormUserRole applicationFormUserRole3 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormThree).role(role)
                .user(applicant).build();
        ApplicationFormUserRole applicationFormUserRole4 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormFour).role(role).user(applicant)
                .build();

        save(program, applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour, applicationFormUserRole1, applicationFormUserRole2,
                applicationFormUserRole3, applicationFormUserRole4);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_STATUS).searchTerm("Offer").build();
        List<ApplicationDescriptor> applications = applicationsService.getAllVisibleAndMatchedApplicationsForList(applicant,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, filter));

        assertEquals(1, applications.size());
        assertEquals(applicationFormOne.getId(), applications.get(0).getApplicationFormId());
    }

    @Test
    public void shouldNotReturnAppIfNoStatusMatching() throws ParseException {
        Program program = new ProgramBuilder().code("empty").title("empty").build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).applicationNumber("ABC").program(program)
                .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicationNumber("App_Biology").program(program)
                .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).applicationNumber("ABCD").program(program)
                .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();

        ApplicationForm applicationFormFour = new ApplicationFormBuilder().applicationNumber("BIOLOGY1").program(program)
                .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();

        Role role = roleDAO.getRoleByAuthority(Authority.APPLICANT);
        ApplicationFormUserRole applicationFormUserRole1 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormOne).role(role).user(applicant)
                .build();
        ApplicationFormUserRole applicationFormUserRole2 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormTwo).role(role).user(applicant)
                .build();
        ApplicationFormUserRole applicationFormUserRole3 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormThree).role(role)
                .user(applicant).build();
        ApplicationFormUserRole applicationFormUserRole4 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormFour).role(role).user(applicant)
                .build();

        save(program, applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour, applicationFormUserRole1, applicationFormUserRole2,
                applicationFormUserRole3, applicationFormUserRole4);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("foobar").build();
        List<ApplicationDescriptor> applications = applicationsService.getAllVisibleAndMatchedApplicationsForList(applicant,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, filter));

        assertEquals(0, applications.size());
    }

    @Test
    public void shouldSearchAndSort() throws ParseException {
        Program program = new ProgramBuilder().code("empty").title("empty").build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().status(ApplicationFormStatus.INTERVIEW).applicationNumber("zzzFooBarzzz")
                .program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicationNumber("App_Biology").program(program)
                .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/04")).applicant(applicant).build();

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).applicationNumber("zzzFooBarzzz1")
                .program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/05")).applicant(applicant).build();

        ApplicationForm applicationFormFour = new ApplicationFormBuilder().applicationNumber("BIOLOGY1").program(program)
                .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/06")).applicant(applicant).build();

        Role applicantRole = roleDAO.getRoleByAuthority(Authority.APPLICANT);
        ApplicationFormUserRole applicationFormUserRole1 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormOne).role(applicantRole)
                .user(applicant).build();
        ApplicationFormUserRole applicationFormUserRole2 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormTwo).role(applicantRole)
                .user(applicant).build();
        ApplicationFormUserRole applicationFormUserRole3 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormThree).role(applicantRole)
                .user(applicant).build();
        ApplicationFormUserRole applicationFormUserRole4 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormFour).role(applicantRole)
                .user(applicant).build();

        save(program, applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour, applicationFormUserRole1, applicationFormUserRole2,
                applicationFormUserRole3, applicationFormUserRole4);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("zzzFooBarz").build();
        List<ApplicationDescriptor> applications = applicationsService.getAllVisibleAndMatchedApplicationsForList(applicant,
                newFiltering(SortCategory.APPLICATION_STATUS, SortOrder.ASCENDING, 1, filter));

        assertEquals(2, applications.size());
        assertEquals(applicationFormOne.getId(), applications.get(0).getApplicationFormId());
        assertEquals(applicationFormThree.getId(), applications.get(1).getApplicationFormId());
    }

    @Test
    public void shouldSortApplicationInNaturalSortOrder() throws ParseException {
        Program program = new ProgramBuilder().code("empty").title("empty").build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).applicationNumber("ABC").program(program)
                .applicant(applicant).build();

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicationNumber("App_Biology").program(program)
                .submittedDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/03")).applicant(applicant).build();

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).applicationNumber("ABCD").program(program)
                .submittedDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).applicant(applicant).build();

        ApplicationForm applicationFormFour = new ApplicationFormBuilder().applicationNumber("BIOLOGY1").program(program)
                .submittedDate(new SimpleDateFormat("yyyy/MM/dd").parse("2013/03/03")).applicant(applicant).build();

        Role applicantRole = roleDAO.getRoleByAuthority(Authority.APPLICANT);
        ApplicationFormUserRole applicationFormUserRole1 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormOne).role(applicantRole)
                .user(applicant).build();
        ApplicationFormUserRole applicationFormUserRole2 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormTwo).role(applicantRole)
                .user(applicant).build();
        ApplicationFormUserRole applicationFormUserRole3 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormThree).role(applicantRole)
                .user(applicant).build();
        ApplicationFormUserRole applicationFormUserRole4 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormFour).role(applicantRole)
                .user(applicant).build();

        save(program, applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour, applicationFormUserRole1, applicationFormUserRole2,
                applicationFormUserRole3, applicationFormUserRole4);

        flushAndClearSession();

        List<ApplicationDescriptor> applications = applicationsService.getAllVisibleAndMatchedApplicationsForList(applicant,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1));

        Assert.assertEquals(applicationFormOne.getId(), applications.get(0).getApplicationFormId());
        Assert.assertEquals(applicationFormTwo.getId(), applications.get(1).getApplicationFormId());
        Assert.assertEquals(applicationFormThree.getId(), applications.get(2).getApplicationFormId());
        Assert.assertEquals(applicationFormFour.getId(), applications.get(3).getApplicationFormId());
    }

    @Test
    public void shouldSortApplicationWithApplName() throws ParseException {
        RegisteredUser applicant1 = new RegisteredUserBuilder().firstName("AAAA").lastName("BBBB").username("1")
                .role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).build();
        RegisteredUser applicant2 = new RegisteredUserBuilder().firstName("AAAA").lastName("CCCC").username("2")
                .role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).build();
        RegisteredUser applicant3 = new RegisteredUserBuilder().firstName("BBBB").lastName("AAAA").username("3")
                .role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).build();
        RegisteredUser applicant4 = new RegisteredUserBuilder().firstName("CCCC").lastName("AAAA").username("4")
                .role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).build();
        Program program = new ProgramBuilder().code("empty").title("empty").build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicant(applicant1).status(ApplicationFormStatus.APPROVED)
                .applicationNumber("ABCDE1").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).build();

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicant(applicant2).status(ApplicationFormStatus.APPROVED)
                .applicationNumber("ABCDE2").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/03")).build();

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().applicant(applicant3).status(ApplicationFormStatus.APPROVED)
                .applicationNumber("ABCDE3").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).build();

        ApplicationForm applicationFormFour = new ApplicationFormBuilder().applicant(applicant4).applicationNumber("ABCDE4")
                .status(ApplicationFormStatus.APPROVED).program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2013/03/03")).build();

        Role role = roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR);
        ApplicationFormUserRole applicationFormUserRole1 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormOne).role(role).user(superUser)
                .build();
        ApplicationFormUserRole applicationFormUserRole2 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormTwo).role(role).user(superUser)
                .build();
        ApplicationFormUserRole applicationFormUserRole3 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormThree).role(role)
                .user(superUser).build();
        ApplicationFormUserRole applicationFormUserRole4 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormFour).role(role).user(superUser)
                .build();

        save(applicant1, applicant2, applicant3, applicant4, program, applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour,
                applicationFormUserRole1, applicationFormUserRole2, applicationFormUserRole3, applicationFormUserRole4);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("ABCD").build();
        List<ApplicationDescriptor> applications = applicationsService.getAllVisibleAndMatchedApplicationsForList(superUser,
                newFiltering(SortCategory.APPLICANT_NAME, SortOrder.ASCENDING, 1, filter));

        Assert.assertEquals(4, applications.size());
        Assert.assertEquals("The users should be ordered by lastname first.", applicationFormThree.getId(), applications.get(0).getApplicationFormId());
        Assert.assertEquals("The users should be ordered by lastname first.", applicationFormFour.getId(), applications.get(1).getApplicationFormId());
        Assert.assertEquals("The users should be ordered by lastname first.", applicationFormOne.getId(), applications.get(2).getApplicationFormId());
        Assert.assertEquals("The users should be ordered by lastname first.", applicationFormTwo.getId(), applications.get(3).getApplicationFormId());
    }

    @Test
    public void shouldSortApplicationWithApplStatus() throws ParseException {
        RegisteredUser applicant1 = new RegisteredUserBuilder().firstName("AAAA").lastName("BBBB").username("1")
                .role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).build();
        RegisteredUser applicant2 = new RegisteredUserBuilder().firstName("AAAA").lastName("CCCC").username("2")
                .role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).build();
        RegisteredUser applicant3 = new RegisteredUserBuilder().firstName("BBBB").lastName("AAAA").username("3")
                .role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).build();
        RegisteredUser applicant4 = new RegisteredUserBuilder().firstName("CCCC").lastName("AAAA").username("4")
                .role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).build();
        Program program = new ProgramBuilder().code("empty").title("empty").build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicant(applicant1).status(ApplicationFormStatus.APPROVED)
                .applicationNumber("ABCDE1").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).build();

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicant(applicant2).status(ApplicationFormStatus.INTERVIEW)
                .applicationNumber("ABCDE2").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/03")).build();

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().applicant(applicant3).status(ApplicationFormStatus.REVIEW)
                .applicationNumber("ABCDE3").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).build();

        ApplicationForm applicationFormFour = new ApplicationFormBuilder().applicant(applicant4).applicationNumber("ABCDE4")
                .status(ApplicationFormStatus.WITHDRAWN).program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2013/03/03")).build();

        Role superadministratorRole = roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR);
        ApplicationFormUserRole applicationFormUserRole1 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormOne)
                .role(superadministratorRole).user(superUser).build();
        ApplicationFormUserRole applicationFormUserRole2 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormTwo)
                .role(superadministratorRole).user(superUser).build();
        ApplicationFormUserRole applicationFormUserRole3 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormThree)
                .role(superadministratorRole).user(superUser).build();
        ApplicationFormUserRole applicationFormUserRole4 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormFour)
                .role(superadministratorRole).user(superUser).build();

        save(applicant1, applicant2, applicant3, applicant4, program, applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour,
                applicationFormUserRole1, applicationFormUserRole2, applicationFormUserRole3, applicationFormUserRole4);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("ABCD").build();
        List<ApplicationDescriptor> applications = applicationsService.getAllVisibleAndMatchedApplicationsForList(superUser,
                newFiltering(SortCategory.APPLICATION_STATUS, SortOrder.ASCENDING, 1, filter));

        Assert.assertEquals(4, applications.size());
        Assert.assertEquals(applicationFormOne.getId(), applications.get(0).getApplicationFormId());
        Assert.assertEquals(applicationFormTwo.getId(), applications.get(1).getApplicationFormId());
        Assert.assertEquals(applicationFormThree.getId(), applications.get(2).getApplicationFormId());
        Assert.assertEquals(applicationFormFour.getId(), applications.get(3).getApplicationFormId());
    }

    @Test
    public void shouldSortApplicationWithProgramName() throws ParseException {
        RegisteredUser applicant1 = new RegisteredUserBuilder().firstName("AAAA").lastName("BBBB").username("1")
                .role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).build();
        RegisteredUser applicant2 = new RegisteredUserBuilder().firstName("AAAA").lastName("CCCC").username("2")
                .role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).build();
        RegisteredUser applicant3 = new RegisteredUserBuilder().firstName("BBBB").lastName("AAAA").username("3")
                .role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).build();
        RegisteredUser applicant4 = new RegisteredUserBuilder().firstName("CCCC").lastName("AAAA").username("4")
                .role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).build();

        Program program1 = new ProgramBuilder().code("empty1").title("AAA").build();
        Program program2 = new ProgramBuilder().code("empty2").title("CCC").build();
        Program program3 = new ProgramBuilder().code("empty3").title("BBB").build();
        Program program4 = new ProgramBuilder().code("empty4").title("DDD").build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicant(applicant1).status(ApplicationFormStatus.APPROVED)
                .applicationNumber("ABCDE1").program(program1).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).build();

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicant(applicant2).status(ApplicationFormStatus.INTERVIEW)
                .applicationNumber("ABCDE2").program(program2).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/03")).build();

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().applicant(applicant3).status(ApplicationFormStatus.REVIEW)
                .applicationNumber("ABCDE3").program(program3).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).build();

        ApplicationForm applicationFormFour = new ApplicationFormBuilder().applicant(applicant4).applicationNumber("ABCDE4")
                .status(ApplicationFormStatus.WITHDRAWN).program(program4).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2013/03/03")).build();

        Role superadministratorRole = roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR);
        ApplicationFormUserRole applicationFormUserRole1 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormOne)
                .role(superadministratorRole).user(superUser).build();
        ApplicationFormUserRole applicationFormUserRole2 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormTwo)
                .role(superadministratorRole).user(superUser).build();
        ApplicationFormUserRole applicationFormUserRole3 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormThree)
                .role(superadministratorRole).user(superUser).build();
        ApplicationFormUserRole applicationFormUserRole4 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormFour)
                .role(superadministratorRole).user(superUser).build();

        save(applicant1, applicant2, applicant3, applicant4, applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour,
                applicationFormUserRole1, applicationFormUserRole2, applicationFormUserRole3, applicationFormUserRole4);
        save(program1, program2, program3, program4);
        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("ABCD").build();
        List<ApplicationDescriptor> applications = applicationsService.getAllVisibleAndMatchedApplicationsForList(superUser,
                newFiltering(SortCategory.PROGRAMME_NAME, SortOrder.DESCENDING, 1, filter));

        Assert.assertEquals(applicationFormFour.getId(), applications.get(0).getApplicationFormId());
        Assert.assertEquals(applicationFormTwo.getId(), applications.get(1).getApplicationFormId());
        Assert.assertEquals(applicationFormThree.getId(), applications.get(2).getApplicationFormId());
        Assert.assertEquals(applicationFormOne.getId(), applications.get(3).getApplicationFormId());
        
    }

    @Test
    public void shouldLimitApplicationList() throws ParseException {
        Role role = roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR);

        List<ApplicationFormUserRole> applicationFormUserRoles = Lists.newArrayList();
        List<ApplicationForm> returnedAppls = Lists.newArrayList();
        for (int i = 0; i < 70; i++) {
            ApplicationForm form = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVED).applicationNumber("ABCDEFG" + i).program(program)
                    .applicant(applicant).build();

            ApplicationFormUserRole applicationFormUserRole = new ApplicationFormUserRoleBuilder().applicationForm(form).role(role).user(superUser).build();
            applicationFormUserRoles.add(applicationFormUserRole);
            returnedAppls.add(form);
        }
        Collections.shuffle(returnedAppls);

        save(returnedAppls);
        save(applicationFormUserRoles);

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("ABCDEFG").build();
        List<ApplicationDescriptor> applications = applicationsService.getAllVisibleAndMatchedApplicationsForList(superUser,
                newFiltering(SortCategory.PROGRAMME_NAME, SortOrder.DESCENDING, 1, filter));

        Assert.assertEquals(APPLICATION_BLOCK_SIZE, applications.size());
    }

    @Test
    public void shouldReturnApplicationWithSupervisorInProgrammeDetails() {
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("AAAA").lastName("BBBB").username("1")
                .role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).build();
        SuggestedSupervisor supervisor = new SuggestedSupervisorBuilder().aware(true).email("threepwood@monkeyisland.com").firstname("Guybrush")
                .lastname("Threepwood").build();
        SourcesOfInterest sourcesOfInterest = new SourcesOfInterestBuilder().name("foo").code("foo").build();
        ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().programmeName("Test").studyOption("Half").startDate(new Date()).projectName("Test")
                .sourcesOfInterest(sourcesOfInterest).suggestedSupervisors(supervisor).build();
        ApplicationForm formWithSupervisor = new ApplicationFormBuilder().status(ApplicationFormStatus.REVIEW).applicant(applicant)
                .programmeDetails(programmeDetails).program(program).build();

        Role superadministratorRole = roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR);
        ApplicationFormUserRole applicationFormUserRole1 = new ApplicationFormUserRoleBuilder().applicationForm(formWithSupervisor)
                .role(superadministratorRole).user(superUser).build();

        save(applicant, supervisor, sourcesOfInterest, programmeDetails, formWithSupervisor, applicationFormUserRole1);
        programmeDetails.setApplication(formWithSupervisor);
        save(programmeDetails);

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.SUPERVISOR).searchTerm("Threepwood").build();

        List<ApplicationDescriptor> applications = applicationsService.getAllVisibleAndMatchedApplicationsForList(superUser,
                newFiltering(SortCategory.PROGRAMME_NAME, SortOrder.DESCENDING, 1, filter));
        assertEquals(1, applications.size());
        assertEquals(supervisor.getLastname(), applicationFormDAO.get(applications.get(0).getApplicationFormId()).getProgrammeDetails().getSuggestedSupervisors().get(0).getLastname());
    }

    @Test
    public void shouldReturnApplicationWithSupervisorInOneOfTheApprovalRounds() {
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("AAAA").lastName("BBBB").username("AASW")
                .role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).build();
        RegisteredUser supervisorUser1 = new RegisteredUserBuilder().firstName("Guybrush").lastName("Threepwood").username("AASWW")
                .role(roleDAO.getRoleByAuthority(Authority.SUPERVISOR)).build();
        Supervisor supervisor1 = new SupervisorBuilder().isPrimary(true).user(supervisorUser1).build();
        RegisteredUser supervisorUser2 = new RegisteredUserBuilder().firstName("Herman").lastName("Toothrot").username("AASSSCXXSW")
                .role(roleDAO.getRoleByAuthority(Authority.SUPERVISOR)).build();
        Supervisor supervisor2 = new SupervisorBuilder().isPrimary(false).user(supervisorUser1).build();
        ApprovalRound approvalRound1 = new ApprovalRoundBuilder().createdDate(new Date()).supervisors(supervisor1).build();
        ApprovalRound approvalRound2 = new ApprovalRoundBuilder().createdDate(new Date()).supervisors(supervisor2).build();
        ApplicationForm formWithSupervisor = new ApplicationFormBuilder().status(ApplicationFormStatus.REVIEW).applicant(applicant).program(program)
                .approvalRounds(approvalRound1, approvalRound2).build();

        Role superadministratorRole = roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR);
        ApplicationFormUserRole applicationFormUserRole1 = new ApplicationFormUserRoleBuilder().applicationForm(formWithSupervisor)
                .role(superadministratorRole).user(superUser).build();

        save(applicant, supervisorUser1, supervisor1, supervisorUser2, supervisor2, approvalRound1, approvalRound2, formWithSupervisor,
                applicationFormUserRole1);

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.SUPERVISOR).searchTerm("Threepwood").build();

        List<ApplicationDescriptor> applications = applicationsService.getAllVisibleAndMatchedApplicationsForList(superUser,
                newFiltering(SortCategory.PROGRAMME_NAME, SortOrder.DESCENDING, 1, filter));
        assertEquals(1, applications.size());
        assertEquals(supervisor1.getUser().getLastName(), applicationFormDAO.get(applications.get(0).getApplicationFormId()).getApprovalRounds().get(0).getSupervisors().get(0).getUser().getLastName());
    }

    @Test
    public void shouldReturnApplicationsBasedOnTheirClosingDate() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
        ApplicationForm applicationFormOne = new ApplicationFormBuilder().program(program).applicant(applicant).status(ApplicationFormStatus.VALIDATION)
                .submittedDate(new Date()).appDate(format.parse("01 01 2012")).batchDeadline(format.parse("01 01 2050")).build();

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().program(program).applicant(applicant).status(ApplicationFormStatus.UNSUBMITTED)
                .appDate(format.parse("01 01 2012")).batchDeadline(format.parse("01 01 2050")).submittedDate(format.parse("01 04 2012")).build();

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().program(program).applicant(applicant).batchDeadline(format.parse("01 01 2050"))
                .status(ApplicationFormStatus.UNSUBMITTED).appDate(format.parse("01 02 2012")).build();

        ApplicationForm applicationFormFour = new ApplicationFormBuilder().program(program).applicant(applicant).status(ApplicationFormStatus.UNSUBMITTED)
                .appDate(format.parse("01 02 2012")).batchDeadline(format.parse("01 01 2050")).submittedDate(format.parse("01 03 2012")).build();

        Role role = roleDAO.getRoleByAuthority(Authority.APPLICANT);
        ApplicationFormUserRole applicationFormUserRole1 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormOne).role(role).user(applicant)
                .build();
        ApplicationFormUserRole applicationFormUserRole2 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormTwo).role(role).user(applicant)
                .build();
        ApplicationFormUserRole applicationFormUserRole3 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormThree).role(role)
                .user(applicant).build();
        ApplicationFormUserRole applicationFormUserRole4 = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormFour).role(role).user(applicant)
                .build();

        save(applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour, applicationFormUserRole1, applicationFormUserRole2,
                applicationFormUserRole3, applicationFormUserRole4);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.CLOSING_DATE).searchPredicate(SearchPredicate.ON_DATE)
                .searchTerm("01 Jan 2050").build();

        List<ApplicationDescriptor> applications = applicationsService.getAllVisibleAndMatchedApplicationsForList(applicant,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, filter));

        assertEquals(applicationFormOne.getId(), applications.get(0).getApplicationFormId());
        assertEquals(applicationFormTwo.getId(), applications.get(1).getApplicationFormId());
        assertEquals(applicationFormFour.getId(), applications.get(2).getApplicationFormId());
        assertEquals(applicationFormThree.getId(), applications.get(3).getApplicationFormId());
    }

    @Test
    public void shouldReturnApplicationWithProjectTitle() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
        ApplicationForm applicationFormOne = new ApplicationFormBuilder().status(ApplicationFormStatus.REVIEW).program(program).projectTitle("another title")
                .applicant(applicant).submittedDate(new Date()).appDate(format.parse("01 01 2012")).dueDate(format.parse("01 01 2050")).build();

        Role role = roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR);
        ApplicationFormUserRole applicationFormUserRole = new ApplicationFormUserRoleBuilder().applicationForm(applicationFormOne).role(role).user(superUser)
                .build();

        save(applicationFormOne, applicationFormUserRole);
        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.PROJECT_TITLE).searchTerm("another title").build();
        List<ApplicationDescriptor> applications = applicationsService.getAllVisibleAndMatchedApplicationsForList(superUser,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, filter));
        assertEquals(1, applications.size());

    }

    private ApplicationsFiltering newFiltering(SortCategory sortCategory, SortOrder sortOrder, int blockCount, ApplicationsFilter... filters) {
        return new ApplicationsFilteringBuilder().sortCategory(sortCategory).order(sortOrder).blockCount(blockCount).filters(filters).build();
    }

    private boolean listContainsId(ApplicationDescriptor descriptor, List<ApplicationDescriptor> applications) {
        for (ApplicationDescriptor entry : applications) {
            if (descriptor.getApplicationFormId().equals(entry.getApplicationFormId())) {
                return true;
            }
        }
        return false;
    }
}