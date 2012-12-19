package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormListDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;

public class ApplicationsServiceGetVisibleApplicationsTest extends AutomaticRollbackTestCase {

    private static final int APPLICATION_BLOCK_SIZE = ApplicationsService.APPLICATION_BLOCK_SIZE;
    
    private RegisteredUser user;
    
    private RegisteredUser superUser;
    
    private ApplicationFormListDAO applicationFormListDAO;
    
    private ApplicationFormDAO applicationFormDAO;
    
    private Program program;
    
    private ApplicationsService applicationsService;
    
    private RoleDAO roleDAO;
    
    @Before
    public void prepare() {
        applicationFormListDAO = new ApplicationFormListDAO(sessionFactory);
        
        applicationFormDAO = new ApplicationFormDAO(sessionFactory);

        applicationsService = new ApplicationsService(applicationFormDAO, applicationFormListDAO);
        
        roleDAO = new RoleDAO(sessionFactory);
        
        user = new RegisteredUserBuilder()
            .firstName("Jane")
            .lastName("Doe")
            .email("email@test.com")
            .username("username")
            .password("password")
            .role(roleDAO.getRoleByAuthority(Authority.APPLICANT))
            .accountNonExpired(false)
            .accountNonLocked(false)
            .credentialsNonExpired(false).enabled(true).toUser();
        
        superUser = new RegisteredUserBuilder()
            .firstName("John")
            .lastName("Doe")
            .email("email@test.com")
            .username("superUserUsername")
            .password("password")
            .role(roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR))
            .accountNonExpired(false)
            .accountNonLocked(false)
            .credentialsNonExpired(false).enabled(true).toUser();

        program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();

        save(user, superUser, program);

        flushAndClearSession();
    }
    
    @Test
    public void shouldGetListOfVisibleApplicationsFromDAOAndSetDefaultValues() {
        ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
        ApplicationFormListDAO applicationFormDAOMock = EasyMock.createMock(ApplicationFormListDAO.class);
        ApplicationsService applicationsService = new ApplicationsService(null, applicationFormDAOMock);
        RegisteredUser user = new RegisteredUserBuilder().id(1).username("bob").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
        EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user, null, null, SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, APPLICATION_BLOCK_SIZE)).andReturn(Arrays.asList(form));
        EasyMock.replay(applicationFormDAOMock);
        List<ApplicationForm> visibleApplications = applicationsService.getAllVisibleAndMatchedApplications(user, null, null, null, null, 1);
        EasyMock.verify(applicationFormDAOMock);
        Assert.assertTrue(visibleApplications.contains(form));
        Assert.assertEquals(1, visibleApplications.size());
    }
    
    @Test
    public void shouldGetApplicationsOrderedByCreationDateThenSubmissionDateFirst() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");

        ApplicationForm applicationFormOne = new ApplicationFormBuilder()
            .program(program)
            .applicant(user)
            .status(ApplicationFormStatus.VALIDATION)
            .submittedDate(new Date())
            .appDate(format.parse("01 01 2012"))
            .toApplicationForm();
        
        ApplicationForm applicationFormTwo = new ApplicationFormBuilder()
            .program(program)
            .applicant(user)
            .status(ApplicationFormStatus.UNSUBMITTED)
            .appDate(format.parse("01 01 2012"))
            .submittedDate(format.parse("01 04 2012"))
            .toApplicationForm();
        
        ApplicationForm applicationFormThree = new ApplicationFormBuilder()
            .program(program)
            .applicant(user)
            .status(ApplicationFormStatus.UNSUBMITTED)
            .appDate(format.parse("01 02 2012"))    // this is insertable=false. We can not properly test this?
            .toApplicationForm();
        
        ApplicationForm applicationFormFour = new ApplicationFormBuilder()
            .program(program)
            .applicant(user)
            .status(ApplicationFormStatus.UNSUBMITTED)
            .appDate(format.parse("01 02 2012"))    // this is insertable=false. We can not properly test this?
            .submittedDate(format.parse("01 03 2012"))
            .toApplicationForm();
                
        save(applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour);
        
        flushAndClearSession();
        
        List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, null, null, SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1);
        
        assertEquals(applicationFormOne.getId(), applications.get(0).getId());
        assertEquals(applicationFormTwo.getId(), applications.get(1).getId());
        assertEquals(applicationFormFour.getId(), applications.get(2).getId());
        assertEquals(applicationFormThree.getId(), applications.get(3).getId());
    }
    
    @Test
    public void shouldGetAllApplicationsContainingBiologyInTheirNumber() throws ParseException {
        ApplicationForm applicationFormOne = new ApplicationFormBuilder()
            .applicationNumber("ABC")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .applicant(user)
            .status(ApplicationFormStatus.APPROVAL)
            .toApplicationForm();
        
        ApplicationForm applicationFormTwo = new ApplicationFormBuilder()
            .applicationNumber("App_Biology")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .applicant(user)
            .status(ApplicationFormStatus.APPROVAL)
            .toApplicationForm();
        
        ApplicationForm applicationFormThree = new ApplicationFormBuilder()
            .applicationNumber("ABCD")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .applicant(user)
            .status(ApplicationFormStatus.APPROVAL)
            .toApplicationForm();
        
        ApplicationForm applicationFormFour = new ApplicationFormBuilder()
            .applicationNumber("BIOLOGY1")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .applicant(user)
            .status(ApplicationFormStatus.APPROVAL)
            .toApplicationForm();
        
        save(applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour);
        
        flushAndClearSession();
        
        List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, SearchCategory.APPLICATION_NUMBER, "BiOlOgY", null, null, 1);

        assertEquals(2, applications.size());
        assertTrue(listContainsId(applicationFormFour, applications));
        assertTrue(listContainsId(applicationFormTwo, applications));
    }
    
    @Test
    public void shouldGetApplicationBelongingToProgramWithCodeScienceAndOtherTitle() throws ParseException {
        Program programOne = new ProgramBuilder().code("Program_ZZZZZ_1").title("empty").toProgram();
        
        ApplicationForm applicationFormOne = new ApplicationFormBuilder()
            .applicationNumber("ABC1")
            .program(programOne)
            .applicant(user)
            .status(ApplicationFormStatus.APPROVAL)
            .toApplicationForm();
        
        ApplicationForm applicationFormTwo = new ApplicationFormBuilder()
            .applicationNumber("ABC2")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .applicant(user)
            .status(ApplicationFormStatus.APPROVAL)
            .toApplicationForm();

        save(programOne, applicationFormOne, applicationFormTwo);
        
        flushAndClearSession();
        
        List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, SearchCategory.PROGRAMME_NAME, "zzZZz", null, null, 1);

        assertEquals(1, applications.size());
        assertEquals(applicationFormOne.getId(), applications.get(0).getId());
    }
    
    @Test
    public void shouldGetApplicationBelongingToProgramWithTitleScienceAndOtherCode() throws ParseException {
        Program programOne = new ProgramBuilder().code("empty").title("Program_ZZZZZ_1").toProgram();
        ApplicationForm applicationFormOne = new ApplicationFormBuilder()
            .applicationNumber("ABC")
            .program(programOne)
            .applicant(user)
            .status(ApplicationFormStatus.APPROVAL)
            .toApplicationForm();
        
        ApplicationForm applicationFormTwo = new ApplicationFormBuilder()
            .applicationNumber("ABC2")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .applicant(user)
            .status(ApplicationFormStatus.APPROVAL)
            .toApplicationForm();
 
        save(programOne, applicationFormOne, applicationFormTwo);
        
        flushAndClearSession();
        
        List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, SearchCategory.PROGRAMME_NAME, "zzZZz", null, null, 1);

        assertEquals(1, applications.size());
        assertEquals(applicationFormOne.getId(), applications.get(0).getId());
    }
    
    @Test
    public void shouldNotReturnAppIfTermNotInProgrameCodeOrTitle() {
        Program programOne = new ProgramBuilder().code("empty").title("empty").toProgram();
        
        ApplicationForm applicationFormOne = new ApplicationFormBuilder()
            .applicationNumber("ABC")
            .program(programOne)
            .applicant(user)
            .status(ApplicationFormStatus.APPROVAL)
            .toApplicationForm();

        save(programOne, applicationFormOne);
        flushAndClearSession();

        List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, SearchCategory.PROGRAMME_NAME, "zzZZz", null, null, 1);

        assertEquals(0, applications.size());
    }
    
    @Test
    public void shouldGetApplicationBelongingToApplicantMatchingFirstNameFred() {
        Program programOne = new ProgramBuilder().code("Program_Science_1").title("empty").toProgram();
        
        RegisteredUser applicant = new RegisteredUserBuilder()
            .firstName("FredzzZZZZZerick")
            .lastName("Doe")
            .email("email@test.com")
            .username("freddy")
            .password("password")
            .accountNonExpired(false)
            .accountNonLocked(false)
            .credentialsNonExpired(false)
            .enabled(true)
            .toUser();
        
        ApplicationForm applicationFormOne = new ApplicationFormBuilder()
            .applicationNumber("ABC")
            .program(programOne)
            .applicant(applicant)
            .status(ApplicationFormStatus.APPROVAL)
            .toApplicationForm();

        save(programOne, applicant, applicationFormOne);
        
        flushAndClearSession();
        
        List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(superUser, SearchCategory.APPLICANT_NAME, "zzZZz", null, null, 1);

        assertEquals(1, applications.size());
        assertEquals(applicationFormOne.getId(), applications.get(0).getId());
    }
    
    @Test
    public void shouldGetApplicationBelongingToApplicantMatchingLastName() {
        Program programOne = new ProgramBuilder().code("Program_Science_1").title("empty").toProgram();
        
        RegisteredUser applicant = new RegisteredUserBuilder()
            .firstName("Frederick")
            .lastName("FredzzZZZZZerick")
            .email("email@test.com")
            .username("freddy")
            .password("password")
            .accountNonExpired(false)
            .accountNonLocked(false)
            .credentialsNonExpired(false)
            .enabled(true)
            .toUser();
        
        ApplicationForm applicationFormOne = new ApplicationFormBuilder()
            .applicationNumber("ABC")
            .program(programOne)
            .applicant(applicant)
            .status(ApplicationFormStatus.APPROVAL)
            .toApplicationForm();
        
        save(programOne, applicant, applicationFormOne);
        
        flushAndClearSession();

        List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(superUser, SearchCategory.APPLICANT_NAME, "zzZZz", null, null, 1);

        assertEquals(1, applications.size());
        assertEquals(applicationFormOne.getId(), applications.get(0).getId());
    }
    
    @Test
    public void shouldNotReturnAppIfTermNotInApplicantNameFirstOrLastName() {
        Program programOne = new ProgramBuilder().code("empty").title("empty").toProgram();
        
        RegisteredUser applicant = new RegisteredUserBuilder()
            .firstName("Frederick")
            .lastName("unique")
            .email("email@test.com")
            .username("freddy")
            .password("password")
            .accountNonExpired(false)
            .accountNonLocked(false)
            .credentialsNonExpired(false)
            .enabled(true)
            .toUser();
        
        ApplicationForm applicationFormOne = new ApplicationFormBuilder()
            .applicationNumber("ABC")
            .program(programOne)
            .applicant(applicant)
            .status(ApplicationFormStatus.APPROVAL)
            .toApplicationForm();

        save(programOne, applicant, applicationFormOne);
        
        flushAndClearSession();

        List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(superUser, SearchCategory.APPLICANT_NAME, "empty", null, null, 1);

        assertEquals(0, applications.size());
    }
    
    @Test
    public void shouldGetAllApplicationsInValidationStage() throws ParseException {
        Program program = new ProgramBuilder().code("empty").title("empty").toProgram();
        
        ApplicationForm applicationFormOne = new ApplicationFormBuilder()
            .status(ApplicationFormStatus.VALIDATION)
            .applicationNumber("ABC")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .applicant(user)
            .toApplicationForm();
        
        ApplicationForm applicationFormTwo = new ApplicationFormBuilder()
            .applicationNumber("App_Biology")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .applicant(user)
            .toApplicationForm();
        
        ApplicationForm applicationFormThree = new ApplicationFormBuilder()
            .status(ApplicationFormStatus.VALIDATION)
            .applicationNumber("ABCD")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .applicant(user)
            .toApplicationForm();
        
        ApplicationForm applicationFormFour = new ApplicationFormBuilder()
            .applicationNumber("BIOLOGY1")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .applicant(user)
            .toApplicationForm();

        save(program, applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour);
        
        flushAndClearSession();

        List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, SearchCategory.APPLICATION_STATUS, "validati", null, null, 1);
        
        assertEquals(2, applications.size());
        assertTrue(listContainsId(applicationFormOne, applications));
        assertTrue(listContainsId(applicationFormThree, applications));
    }
    
    @Test
    public void shouldGetAllApplicationsInApprovalStage() throws ParseException {
        Program program = new ProgramBuilder().code("empty").title("empty").toProgram();
        
        ApplicationForm applicationFormOne = new ApplicationFormBuilder()
            .status(ApplicationFormStatus.APPROVAL)
            .applicationNumber("ABC")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .applicant(user)
            .toApplicationForm();
        
        ApplicationForm applicationFormTwo = new ApplicationFormBuilder()
            .applicationNumber("App_Biology")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .applicant(user)
            .toApplicationForm();
        
        ApplicationForm applicationFormThree = new ApplicationFormBuilder()
            .status(ApplicationFormStatus.VALIDATION)
            .applicationNumber("ABCD")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .applicant(user)
            .toApplicationForm();
        
        ApplicationForm applicationFormFour = new ApplicationFormBuilder()
            .applicationNumber("BIOLOGY1")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .applicant(user)
            .toApplicationForm();

        save(program, applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour);
        
        flushAndClearSession();

        List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, SearchCategory.APPLICATION_STATUS, "approv", null, null, 1);
        
        assertEquals(1, applications.size());
        assertEquals(applicationFormOne.getId(), applications.get(0).getId());
    }
    
    @Test
    public void shouldGetAllApplicationsInApprovedStage() throws ParseException {
        Program program = new ProgramBuilder().code("empty").title("empty").toProgram();
        
        ApplicationForm applicationFormOne = new ApplicationFormBuilder()
            .status(ApplicationFormStatus.APPROVED)
            .applicationNumber("ABC")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .applicant(user)
            .toApplicationForm();
        
        ApplicationForm applicationFormTwo = new ApplicationFormBuilder()
            .applicationNumber("App_Biology")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .applicant(user)
            .toApplicationForm();
        
        ApplicationForm applicationFormThree = new ApplicationFormBuilder()
            .status(ApplicationFormStatus.VALIDATION)
            .applicationNumber("ABCD")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .applicant(user)
            .toApplicationForm();
        
        ApplicationForm applicationFormFour = new ApplicationFormBuilder()
            .applicationNumber("BIOLOGY1")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .applicant(user)
            .toApplicationForm();

        save(program, applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour);
        
        flushAndClearSession();

        List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, SearchCategory.APPLICATION_STATUS, "approved", null, null, 1);
        
        assertEquals(1, applications.size());
        assertEquals(applicationFormOne.getId(), applications.get(0).getId());
    }
    
    @Test
    public void shouldNotReturnAppIfNoStatusMatching() throws ParseException {
        Program program = new ProgramBuilder().code("empty").title("empty").toProgram();
        
        ApplicationForm applicationFormOne = new ApplicationFormBuilder()
            .status(ApplicationFormStatus.APPROVED)
            .applicationNumber("ABC")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .applicant(user)
            .toApplicationForm();
        
        ApplicationForm applicationFormTwo = new ApplicationFormBuilder()
            .applicationNumber("App_Biology")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .applicant(user)
            .toApplicationForm();
        
        ApplicationForm applicationFormThree = new ApplicationFormBuilder()
            .status(ApplicationFormStatus.VALIDATION)
            .applicationNumber("ABCD")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .applicant(user)
            .toApplicationForm();
        
        ApplicationForm applicationFormFour = new ApplicationFormBuilder()
            .applicationNumber("BIOLOGY1")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .applicant(user)
            .toApplicationForm();

        save(program, applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour);
        
        flushAndClearSession();

        List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, SearchCategory.APPLICATION_STATUS, "foobar", null, null, 1);
        
        assertEquals(0, applications.size());
    }
    
    @Test
    public void shouldSearchAndSort() throws ParseException {
        Program program = new ProgramBuilder().code("empty").title("empty").toProgram();
        
        ApplicationForm applicationFormOne = new ApplicationFormBuilder()
            .status(ApplicationFormStatus.INTERVIEW)
            .applicationNumber("zzzFooBarzzz")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .applicant(user)
            .toApplicationForm();
        
        ApplicationForm applicationFormTwo = new ApplicationFormBuilder()
            .applicationNumber("App_Biology")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/04"))
            .applicant(user)
            .toApplicationForm();
        
        ApplicationForm applicationFormThree = new ApplicationFormBuilder()
            .status(ApplicationFormStatus.VALIDATION)
            .applicationNumber("zzzFooBarzzz1")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/05"))
            .applicant(user)
            .toApplicationForm();
        
        ApplicationForm applicationFormFour = new ApplicationFormBuilder()
            .applicationNumber("BIOLOGY1")
            .program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/06"))
            .applicant(user)
            .toApplicationForm();

        save(program, applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour);
        
        flushAndClearSession();
        
        List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, 
                SearchCategory.APPLICATION_NUMBER, "zzzFooBarz", 
                SortCategory.APPLICATION_STATUS, 
                SortOrder.ASCENDING, 
                1);
        
        assertEquals(2, applications.size());
        assertEquals(applicationFormOne.getId(), applications.get(0).getId());
        assertEquals(applicationFormThree.getId(), applications.get(1).getId());
    }
    
    @Test
    public void shouldSortApplicationInNaturalSortOrder() throws ParseException {
        Program program = new ProgramBuilder().code("empty").title("empty").toProgram();
        
        ApplicationForm applicationFormOne = new ApplicationFormBuilder()
            .status(ApplicationFormStatus.APPROVED)
            .applicationNumber("ABC")
            .program(program)
            .applicant(user)
            .toApplicationForm();
        
        ApplicationForm applicationFormTwo = new ApplicationFormBuilder()
            .applicationNumber("App_Biology")
            .program(program)
            .submittedDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/03"))
            .applicant(user)
            .toApplicationForm();
        
        ApplicationForm applicationFormThree = new ApplicationFormBuilder()
            .status(ApplicationFormStatus.APPROVED)
            .applicationNumber("ABCD")
            .program(program).submittedDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04"))
            .applicant(user)
            .toApplicationForm();
        
        ApplicationForm applicationFormFour = new ApplicationFormBuilder()
            .applicationNumber("BIOLOGY1")
            .program(program)
            .submittedDate(new SimpleDateFormat("yyyy/MM/dd").parse("2013/03/03"))
            .applicant(user)
            .toApplicationForm();

        save(program, applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour);
        
        flushAndClearSession();
        
        List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, null, null, null, null, 1);

        Assert.assertEquals(applicationFormOne.getId(), applications.get(0).getId());
        Assert.assertEquals(applicationFormTwo.getId(), applications.get(1).getId());
        Assert.assertEquals(applicationFormThree.getId(), applications.get(2).getId());
        Assert.assertEquals(applicationFormFour.getId(), applications.get(3).getId());
    }
    
    @Test
    public void shouldSortApplicationWithApplName() throws ParseException {
        RegisteredUser applicant1 = new RegisteredUserBuilder().firstName("AAAA").lastName("BBBB").username("1").role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).toUser();
        RegisteredUser applicant2 = new RegisteredUserBuilder().firstName("AAAA").lastName("CCCC").username("2").role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).toUser();
        RegisteredUser applicant3 = new RegisteredUserBuilder().firstName("BBBB").lastName("AAAA").username("3").role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).toUser();
        RegisteredUser applicant4 = new RegisteredUserBuilder().firstName("CCCC").lastName("AAAA").username("4").role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).toUser();
        Program program = new ProgramBuilder().code("empty").title("empty").toProgram();
        
        ApplicationForm applicationFormOne = new ApplicationFormBuilder()
            .applicant(applicant1)
            .status(ApplicationFormStatus.APPROVED)
            .applicationNumber("ABCDE1")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .toApplicationForm();
        
        ApplicationForm applicationFormTwo = new ApplicationFormBuilder()
            .applicant(applicant2)
            .status(ApplicationFormStatus.APPROVED)
            .applicationNumber("ABCDE2")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/03"))
            .toApplicationForm();
        
        ApplicationForm applicationFormThree = new ApplicationFormBuilder()
            .applicant(applicant3)
            .status(ApplicationFormStatus.APPROVED)
            .applicationNumber("ABCDE3")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04"))
            .toApplicationForm();
        
        ApplicationForm applicationFormFour = new ApplicationFormBuilder()
            .applicant(applicant4)
            .applicationNumber("ABCDE4")
            .status(ApplicationFormStatus.APPROVED)
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2013/03/03"))
            .toApplicationForm();

        save(applicant1, applicant2, applicant3, applicant4, program, applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour);
        
        flushAndClearSession();
        
        List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(superUser, SearchCategory.APPLICATION_NUMBER, "ABCD", SortCategory.APPLICANT_NAME, SortOrder.ASCENDING, 1);
        
        Assert.assertEquals(4, applications.size());
        Assert.assertEquals(applicationFormOne.getId(), applications.get(0).getId());
        Assert.assertEquals(applicationFormTwo.getId(), applications.get(1).getId());
        Assert.assertEquals(applicationFormThree.getId(), applications.get(2).getId());
        Assert.assertEquals(applicationFormFour.getId(), applications.get(3).getId());
    }
    
    @Test
    public void shouldSortApplicationWithApplStatus() throws ParseException {
        RegisteredUser applicant1 = new RegisteredUserBuilder().firstName("AAAA").lastName("BBBB").username("1").role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).toUser();
        RegisteredUser applicant2 = new RegisteredUserBuilder().firstName("AAAA").lastName("CCCC").username("2").role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).toUser();
        RegisteredUser applicant3 = new RegisteredUserBuilder().firstName("BBBB").lastName("AAAA").username("3").role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).toUser();
        RegisteredUser applicant4 = new RegisteredUserBuilder().firstName("CCCC").lastName("AAAA").username("4").role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).toUser();
        Program program = new ProgramBuilder().code("empty").title("empty").toProgram();
        
        ApplicationForm applicationFormOne = new ApplicationFormBuilder()
            .applicant(applicant1)
            .status(ApplicationFormStatus.APPROVED)
            .applicationNumber("ABCDE1")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .toApplicationForm();
        
        ApplicationForm applicationFormTwo = new ApplicationFormBuilder()
            .applicant(applicant2)
            .status(ApplicationFormStatus.INTERVIEW)
            .applicationNumber("ABCDE2")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/03"))
            .toApplicationForm();
        
        ApplicationForm applicationFormThree = new ApplicationFormBuilder()
            .applicant(applicant3)
            .status(ApplicationFormStatus.REVIEW)
            .applicationNumber("ABCDE3")
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04"))
            .toApplicationForm();
        
        ApplicationForm applicationFormFour = new ApplicationFormBuilder()
            .applicant(applicant4)
            .applicationNumber("ABCDE4")
            .status(ApplicationFormStatus.WITHDRAWN)
            .program(program)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2013/03/03"))
            .toApplicationForm();

        save(applicant1, applicant2, applicant3, applicant4, program, applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour);
        
        flushAndClearSession();
        
        List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(superUser, SearchCategory.APPLICATION_NUMBER, "ABCD", SortCategory.APPLICATION_STATUS, SortOrder.ASCENDING, 1);
        
        Assert.assertEquals(4, applications.size());
        Assert.assertEquals(applicationFormOne.getId(), applications.get(0).getId());
        Assert.assertEquals(applicationFormTwo.getId(), applications.get(1).getId());
        Assert.assertEquals(applicationFormThree.getId(), applications.get(2).getId());
        Assert.assertEquals(applicationFormFour.getId(), applications.get(3).getId());
    }    

    @Test
    public void shouldSortApplicationWithProgramName() throws ParseException {
        RegisteredUser applicant1 = new RegisteredUserBuilder().firstName("AAAA").lastName("BBBB").username("1").role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).toUser();
        RegisteredUser applicant2 = new RegisteredUserBuilder().firstName("AAAA").lastName("CCCC").username("2").role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).toUser();
        RegisteredUser applicant3 = new RegisteredUserBuilder().firstName("BBBB").lastName("AAAA").username("3").role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).toUser();
        RegisteredUser applicant4 = new RegisteredUserBuilder().firstName("CCCC").lastName("AAAA").username("4").role(roleDAO.getRoleByAuthority(Authority.APPLICANT)).toUser();
      
        Program program1 = new ProgramBuilder().code("empty1").title("AAA").toProgram();
        Program program2 = new ProgramBuilder().code("empty2").title("CCC").toProgram();
        Program program3 = new ProgramBuilder().code("empty3").title("BBB").toProgram();
        Program program4 = new ProgramBuilder().code("empty4").title("DDD").toProgram();
        
        ApplicationForm applicationFormOne = new ApplicationFormBuilder()
            .applicant(applicant1)
            .status(ApplicationFormStatus.APPROVED)
            .applicationNumber("ABCDE1")
            .program(program1)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
            .toApplicationForm();
    
        ApplicationForm applicationFormTwo = new ApplicationFormBuilder()
            .applicant(applicant2)
            .status(ApplicationFormStatus.INTERVIEW)
            .applicationNumber("ABCDE2")
            .program(program2)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/03"))
            .toApplicationForm();
        
        ApplicationForm applicationFormThree = new ApplicationFormBuilder()
            .applicant(applicant3)
            .status(ApplicationFormStatus.REVIEW)
            .applicationNumber("ABCDE3")
            .program(program3)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04"))
            .toApplicationForm();
        
        ApplicationForm applicationFormFour = new ApplicationFormBuilder()
            .applicant(applicant4)
            .applicationNumber("ABCDE4")
            .status(ApplicationFormStatus.WITHDRAWN)
            .program(program4)
            .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2013/03/03"))
            .toApplicationForm();
        
        save(applicant1, applicant2, applicant3, applicant4, applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour);
        save(program1, program2, program3, program4);
        flushAndClearSession();
        
        List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(superUser, SearchCategory.APPLICATION_NUMBER, "ABCD", SortCategory.PROGRAMME_NAME, SortOrder.DESCENDING, 1);

        Assert.assertEquals(applicationFormFour.getId(), applications.get(0).getId());
        Assert.assertEquals(applicationFormTwo.getId(), applications.get(1).getId());
        Assert.assertEquals(applicationFormThree.getId(), applications.get(2).getId());
        Assert.assertEquals(applicationFormOne.getId(), applications.get(3).getId());
    }
    
    @Test
    public void shouldLimitApplicationList() throws ParseException {
        List<ApplicationForm> returnedAppls = new ArrayList<ApplicationForm>();
        for (int i = 0; i < 70; i++) {
            ApplicationForm form = new ApplicationFormBuilder()
                .status(ApplicationFormStatus.APPROVED)
                .applicationNumber("ABCDEFG" + i)
                .program(program)
                .applicant(user)
                .toApplicationForm();
            returnedAppls.add(form);
        }
        Collections.shuffle(returnedAppls);
        
        save(returnedAppls);
        
        List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(superUser, SearchCategory.APPLICATION_NUMBER, "ABCDEFG", SortCategory.PROGRAMME_NAME, SortOrder.DESCENDING, 1);

        Assert.assertEquals(APPLICATION_BLOCK_SIZE, applications.size());
    }
    
    private boolean listContainsId(ApplicationForm form, List<ApplicationForm> aplicationForms) {
        for (ApplicationForm entry : aplicationForms) {
            if (form.getId().equals(entry.getId())) {
                return true;
            }
        }
        return false;
    }
}
