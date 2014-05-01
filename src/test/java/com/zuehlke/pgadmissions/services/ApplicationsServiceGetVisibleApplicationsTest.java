package com.zuehlke.pgadmissions.services;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.unitils.inject.util.InjectionUtils;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormListDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramDetails;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationsFilterBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationsFilteringBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.SourcesOfInterestBuilder;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SearchPredicate;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;

public class ApplicationsServiceGetVisibleApplicationsTest extends AutomaticRollbackTestCase {

    private static final int APPLICATION_BLOCK_SIZE = ApplicationFormService.APPLICATION_BLOCK_SIZE;

    private User applicant;

    private User superUser;

    private ApplicationFormListDAO applicationFormListDAO;

    private ApplicationFormDAO applicationFormDAO;

    private Program program;

    private ApplicationFormService applicationsService;

    private RoleDAO roleDAO;

    private UserDAO userDAO;

    private Institution institution;

    @Before
    public void prepare() {
        userDAO = new UserDAO(sessionFactory, null, null);

        applicationFormListDAO = new ApplicationFormListDAO(sessionFactory, userDAO);

        applicationFormDAO = new ApplicationFormDAO(sessionFactory);

        applicationsService = new ApplicationFormService();
        InjectionUtils.injectInto(applicationFormListDAO, applicationsService, "applicationFormListDAO");
        InjectionUtils.injectInto(applicationFormDAO, applicationsService, "applicationFormDAO");

        roleDAO = new RoleDAO(sessionFactory);

        applicant = new UserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
        // .withRole(roleDAO.getById(Authority.APPLICANT))
                .userAccount(new UserAccount().withEnabled(true).withApplicationListLastAccessTimestamp(DateUtils.addHours(new Date(), 1))).build();

        superUser = new UserBuilder().firstName("John").lastName("Doe").email("email@test.com")
        // .withRole(roleDAO.getById(Authority.SUPERADMINISTRATOR))
                .userAccount(new UserAccount().withEnabled(true).withApplicationListLastAccessTimestamp(DateUtils.addHours(new Date(), 1))).build();

        sessionFactory.getCurrentSession().flush();

        institution = testObjectProvider.getInstitution();
        program = new ProgramBuilder().contactUser(superUser).code("doesntexist").title("another title").institution(institution).build();

        save(applicant, superUser, institution, program);

        flushAndClearSession();
    }

    @Test
    public void shouldGetListOfVisibleApplicationsFromDAOAndSetDefaultValues() {
        ApplicationForm form = new ApplicationFormBuilder().id(1).build();
        ApplicationDescriptor applicationDescriptor = new ApplicationDescriptor();
        applicationDescriptor.setApplicationFormId(form.getId());
        applicationDescriptor.setApplicationFormStatus(PrismState.APPLICATION_REVIEW);

        ApplicationFormListDAO applicationFormListDAOMock = EasyMock.createMock(ApplicationFormListDAO.class);
        InjectionUtils.injectInto(applicationFormListDAOMock, applicationsService, "applicationFormListDAO");

        User user = new UserBuilder().id(1)
        // .withRole(new RoleBuilder().id(Authority.APPLICANT).build())
                .build();
        ApplicationsFiltering filtering = newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1);
        EasyMock.expect(applicationFormListDAOMock.getVisibleApplicationsForList(user, filtering, APPLICATION_BLOCK_SIZE)).andReturn(
                Arrays.asList(applicationDescriptor));
        EasyMock.replay(applicationFormListDAOMock);
        List<ApplicationDescriptor> visibleApplications = applicationsService.getApplicationsForList(user, filtering);
        EasyMock.verify(applicationFormListDAOMock);
        assertThat(visibleApplications, contains(applicationDescriptor));
        Assert.assertEquals(1, visibleApplications.size());
    }

    @Test
    public void shouldGetApplicationsOrderedByCreationDateThenSubmissionDateFirst() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().advert(program).applicant(applicant)
                .status(new State().withId(PrismState.APPLICATION_VALIDATION)).submittedDate(new Date())
                .createdTimestamp(format.parse("01 01 2012")).build();
        ApplicationDescriptor applicationDescriptorOne = new ApplicationDescriptor();
        applicationDescriptorOne.setApplicationFormId(applicationFormOne.getId());
        applicationDescriptorOne.setApplicationFormStatus(PrismState.APPLICATION_VALIDATION);

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().advert(program).applicant(applicant)
                .status(new State().withId(PrismState.APPLICATION_UNSUBMITTED)).createdTimestamp(format.parse("01 01 2012"))
                .submittedDate(format.parse("01 04 2012")).build();
        ApplicationDescriptor applicationDescriptorTwo = new ApplicationDescriptor();
        applicationDescriptorTwo.setApplicationFormId(applicationFormTwo.getId());
        applicationDescriptorTwo.setApplicationFormStatus(PrismState.APPLICATION_UNSUBMITTED);

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().advert(program).applicant(applicant)
                .status(new State().withId(PrismState.APPLICATION_UNSUBMITTED)).createdTimestamp(format.parse("01 02 2012")) // this is
                                                                                                                                        // insertable=false. We
                                                                                                                                        // can
                // not
                // properly test this?
                .build();
        ApplicationDescriptor applicationDescriptorThree = new ApplicationDescriptor();
        applicationDescriptorThree.setApplicationFormId(applicationFormThree.getId());
        applicationDescriptorThree.setApplicationFormStatus(PrismState.APPLICATION_UNSUBMITTED);

        ApplicationForm applicationFormFour = new ApplicationFormBuilder().advert(program).applicant(applicant)
                .status(new State().withId(PrismState.APPLICATION_UNSUBMITTED)).createdTimestamp(format.parse("01 02 2012")) // this is
                                                                                                                                        // insertable=false. We
                                                                                                                                        // can
                // not
                // properly test this?
                .submittedDate(format.parse("01 03 2012")).build();
        ApplicationDescriptor applicationDescriptorFour = new ApplicationDescriptor();
        applicationDescriptorFour.setApplicationFormId(applicationFormFour.getId());
        applicationDescriptorFour.setApplicationFormStatus(PrismState.APPLICATION_UNSUBMITTED);

        Role role = roleDAO.getById(Authority.APPLICATION_CREATOR);
        UserRole applicationFormUserRole1 = new UserRole().withApplication(applicationFormOne).withRole(role).withUser(applicant);
        UserRole applicationFormUserRole2 = new UserRole().withApplication(applicationFormTwo).withRole(role).withUser(applicant);
        UserRole applicationFormUserRole3 = new UserRole().withApplication(applicationFormThree).withRole(role).withUser(applicant);
        UserRole applicationFormUserRole4 = new UserRole().withApplication(applicationFormFour).withRole(role).withUser(applicant);

        save(applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour, applicationFormUserRole1, applicationFormUserRole2,
                applicationFormUserRole3, applicationFormUserRole4);

        flushAndClearSession();

        List<ApplicationDescriptor> applications = applicationsService.getApplicationsForList(applicant,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1));

        assertEquals(applicationFormOne.getId(), applications.get(0).getApplicationFormId());
        assertEquals(applicationFormTwo.getId(), applications.get(1).getApplicationFormId());
        assertEquals(applicationFormFour.getId(), applications.get(2).getApplicationFormId());
        assertEquals(applicationFormThree.getId(), applications.get(3).getApplicationFormId());
    }

    @Test
    public void shouldGetAllApplicationsContainingBiologyInTheirNumber() throws ParseException {

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").advert(program)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant)
                .status(new State().withId(PrismState.APPLICATION_APPROVAL)).build();
        ApplicationDescriptor applicationDescriptorOne = new ApplicationDescriptor();
        applicationDescriptorOne.setApplicationFormId(applicationFormOne.getId());
        applicationDescriptorOne.setApplicationFormStatus(PrismState.APPLICATION_APPROVAL);

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicationNumber("App_Biology").advert(program)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant)
                .status(new State().withId(PrismState.APPLICATION_APPROVAL)).build();
        ApplicationDescriptor applicationDescriptorTwo = new ApplicationDescriptor();
        applicationDescriptorTwo.setApplicationFormId(applicationFormTwo.getId());
        applicationDescriptorTwo.setApplicationFormStatus(PrismState.APPLICATION_APPROVAL);

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().applicationNumber("ABCD").advert(program)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant)
                .status(new State().withId(PrismState.APPLICATION_APPROVAL)).build();
        ApplicationDescriptor applicationDescriptorThree = new ApplicationDescriptor();
        applicationDescriptorThree.setApplicationFormId(applicationFormThree.getId());
        applicationDescriptorThree.setApplicationFormStatus(PrismState.APPLICATION_APPROVAL);

        ApplicationForm applicationFormFour = new ApplicationFormBuilder().applicationNumber("BIOLOGY1").advert(program)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant)
                .status(new State().withId(PrismState.APPLICATION_APPROVAL)).build();
        ApplicationDescriptor applicationDescriptorFour = new ApplicationDescriptor();
        applicationDescriptorFour.setApplicationFormId(applicationFormFour.getId());
        applicationDescriptorFour.setApplicationFormStatus(PrismState.APPLICATION_APPROVAL);

        Role role = roleDAO.getById(Authority.APPLICATION_CREATOR);
        UserRole applicationFormUserRole1 = new UserRole().withApplication(applicationFormOne).withRole(role).withUser(applicant);
        UserRole applicationFormUserRole2 = new UserRole().withApplication(applicationFormTwo).withRole(role).withUser(applicant);
        UserRole applicationFormUserRole3 = new UserRole().withApplication(applicationFormThree).withRole(role).withUser(applicant);
        UserRole applicationFormUserRole4 = new UserRole().withApplication(applicationFormFour).withRole(role).withUser(applicant);

        save(applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour, applicationFormUserRole1, applicationFormUserRole2,
                applicationFormUserRole3, applicationFormUserRole4);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("BiOlOgY").build();
        List<ApplicationDescriptor> applications = applicationsService.getApplicationsForList(applicant,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, filter));

        assertEquals(2, applications.size());

        assertTrue(listContainsId(applicationFormFour, applications));
        assertTrue(listContainsId(applicationFormTwo, applications));
    }

    @Test
    public void shouldGetApplicationBelongingToProgramWithCodeScienceAndOtherTitle() throws ParseException {
        Program programOne = new ProgramBuilder().code("Program_ZZZZZ_1").title("empty").institution(institution)
                .contactUser(testObjectProvider.getEnabledUserInRole(Authority.SYSTEM_ADMINISTRATOR)).build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC1").advert(programOne).applicant(applicant)
                .status(new State().withId(PrismState.APPLICATION_APPROVAL)).build();

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicationNumber("ABC2").advert(program)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant)
                .status(new State().withId(PrismState.APPLICATION_APPROVAL)).build();

        Role role = roleDAO.getById(Authority.APPLICATION_CREATOR);
        UserRole applicationFormUserRole = new UserRole().withApplication(applicationFormOne).withRole(role).withUser(applicant);

        save(programOne, applicationFormOne, applicationFormTwo, applicationFormUserRole);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.PROGRAMME_NAME).searchTerm("zzZZz").build();
        List<ApplicationDescriptor> applications = applicationsService.getApplicationsForList(applicant,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, filter));

        assertEquals(1, applications.size());
        assertEquals(applicationFormOne.getId(), applications.get(0).getApplicationFormId());
    }

    @Test
    public void shouldGetApplicationBelongingToProgramWithTitleScienceAndOtherCode() throws ParseException {
        Program programOne = new ProgramBuilder().code("empty").title("Program_ZZZZZ_1").institution(institution)
                .contactUser(testObjectProvider.getEnabledUserInRole(Authority.SYSTEM_ADMINISTRATOR)).build();
        ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").advert(programOne).applicant(applicant)
                .status(new State().withId(PrismState.APPLICATION_APPROVAL)).build();

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicationNumber("ABC2").advert(program)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant)
                .status(new State().withId(PrismState.APPLICATION_APPROVAL)).build();

        Role role = roleDAO.getById(Authority.APPLICATION_CREATOR);
        UserRole applicationFormUserRole = new UserRole().withApplication(applicationFormOne).withRole(role).withUser(applicant);

        save(programOne, applicationFormOne, applicationFormTwo, applicationFormUserRole);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.PROGRAMME_NAME).searchTerm("zzZZz").build();
        List<ApplicationDescriptor> applications = applicationsService.getApplicationsForList(applicant,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, filter));

        assertEquals(1, applications.size());
        assertEquals(applicationFormOne.getId(), applications.get(0).getApplicationFormId());
    }

    @Test
    public void shouldNotReturnAppIfTermNotInProgrameCodeOrTitle() {

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").applicant(applicant)
                .status(new State().withId(PrismState.APPLICATION_APPROVAL)).advert(testObjectProvider.getEnabledProgram()).build();

        Role role = roleDAO.getById(Authority.APPLICATION_CREATOR);
        UserRole applicationFormUserRole = new UserRole().withApplication(applicationFormOne).withRole(role).withUser(applicant);

        save(applicationFormOne, applicationFormUserRole);
        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.PROGRAMME_NAME).searchTerm("zzZZz").build();
        List<ApplicationDescriptor> applications = applicationsService.getApplicationsForList(applicant,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, filter));

        assertEquals(0, applications.size());
    }

    @Test
    public void shouldGetApplicationBelongingToApplicantMatchingFirstNameFred() {
        User applicant = new UserBuilder().firstName("FredzzZZZZZerick").lastName("Doe").email("email@test.com")
                .userAccount(new UserAccount().withPassword("password").withEnabled(true)).build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").advert(program).applicant(applicant)
                .status(new State().withId(PrismState.APPLICATION_APPROVAL)).build();

        Role role = roleDAO.getById(Authority.SYSTEM_ADMINISTRATOR);
        UserRole applicationFormUserRole = new UserRole().withApplication(applicationFormOne).withRole(role).withUser(superUser);

        save(applicant, applicationFormOne, applicationFormUserRole);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICANT_NAME).searchTerm("zzZZz").build();
        List<ApplicationDescriptor> applications = applicationsService.getApplicationsForList(superUser,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, filter));

        assertEquals(1, applications.size());
        assertEquals(applicationFormOne.getId(), applications.get(0).getApplicationFormId());
    }

    @Test
    public void shouldGetApplicationBelongingToApplicantMatchingLastName() {

        User applicant = new UserBuilder().firstName("Frederick").lastName("FredzzZZZZZerick").email("email@test.com")
                .userAccount(new UserAccount().withEnabled(true)).build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").advert(program).applicant(applicant)
                .status(new State().withId(PrismState.APPLICATION_APPROVAL)).build();

        Role role = roleDAO.getById(Authority.SYSTEM_ADMINISTRATOR);
        UserRole applicationFormUserRole = new UserRole().withApplication(applicationFormOne).withRole(role).withUser(superUser);

        save(applicant, applicationFormOne, applicationFormUserRole);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICANT_NAME).searchTerm("zzZZz").build();
        List<ApplicationDescriptor> applications = applicationsService.getApplicationsForList(superUser,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, filter));

        assertEquals(1, applications.size());
        assertEquals(applicationFormOne.getId(), applications.get(0).getApplicationFormId());
    }

    @Test
    public void shouldNotReturnAppIfTermNotInApplicantNameFirstOrLastName() {

        User applicant = new UserBuilder().firstName("Frederick").lastName("unique").email("email@test.com").userAccount(new UserAccount().withEnabled(true))
                .build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").advert(program).applicant(applicant)
                .status(new State().withId(PrismState.APPLICATION_APPROVAL)).build();

        Role role = roleDAO.getById(Authority.SYSTEM_ADMINISTRATOR);
        UserRole applicationFormUserRole = new UserRole().withApplication(applicationFormOne).withRole(role).withUser(superUser);

        save(applicant, applicationFormOne, applicationFormUserRole);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICANT_NAME).searchTerm("zzZZz").build();
        List<ApplicationDescriptor> applications = applicationsService.getApplicationsForList(superUser,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, filter));

        assertEquals(0, applications.size());
    }

    @Test
    public void shouldGetAllApplicationsInValidationStage() throws ParseException {

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().status(new State().withId(PrismState.APPLICATION_VALIDATION))
                .applicationNumber("ABC").advert(program).createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();
        ApplicationDescriptor applicationDescriptorOne = new ApplicationDescriptor();
        applicationDescriptorOne.setApplicationFormId(applicationFormOne.getId());

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicationNumber("App_Biology").advert(program)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().status(new State().withId(PrismState.APPLICATION_VALIDATION))
                .applicationNumber("ABCD").advert(program).createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant)
                .build();
        ApplicationDescriptor applicationDescriptorThree = new ApplicationDescriptor();
        applicationDescriptorThree.setApplicationFormId(applicationFormThree.getId());

        ApplicationForm applicationFormFour = new ApplicationFormBuilder().applicationNumber("BIOLOGY1").advert(program)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();

        Role role = roleDAO.getById(Authority.APPLICATION_CREATOR);
        UserRole applicationFormUserRole1 = new UserRole().withApplication(applicationFormOne).withRole(role).withUser(applicant);
        UserRole applicationFormUserRole2 = new UserRole().withApplication(applicationFormTwo).withRole(role).withUser(applicant);
        UserRole applicationFormUserRole3 = new UserRole().withApplication(applicationFormThree).withRole(role).withUser(applicant);
        UserRole applicationFormUserRole4 = new UserRole().withApplication(applicationFormFour).withRole(role).withUser(applicant);

        save(applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour, applicationFormUserRole1, applicationFormUserRole2,
                applicationFormUserRole3, applicationFormUserRole4);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_STATUS).searchTerm("validati").build();
        List<ApplicationDescriptor> applications = applicationsService.getApplicationsForList(applicant,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, filter));

        assertEquals(2, applications.size());
        assertTrue(listContainsId(applicationFormOne, applications));
        assertTrue(listContainsId(applicationFormThree, applications));
    }

    @Test
    public void shouldGetAllApplicationsInApprovalStage() throws ParseException {

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().status(new State().withId(PrismState.APPLICATION_APPROVAL))
                .applicationNumber("ABC").advert(program).createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();
        ApplicationDescriptor applicationDescriptorOne = new ApplicationDescriptor();
        applicationDescriptorOne.setApplicationFormId(applicationFormOne.getId());

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicationNumber("App_Biology").advert(program)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().status(new State().withId(PrismState.APPLICATION_VALIDATION))
                .applicationNumber("ABCD").advert(program).createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant)
                .build();

        ApplicationForm applicationFormFour = new ApplicationFormBuilder().applicationNumber("BIOLOGY1").advert(program)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();

        Role role = roleDAO.getById(Authority.APPLICATION_CREATOR);
        UserRole applicationFormUserRole1 = new UserRole().withApplication(applicationFormOne).withRole(role).withUser(applicant);
        UserRole applicationFormUserRole2 = new UserRole().withApplication(applicationFormTwo).withRole(role).withUser(applicant);
        UserRole applicationFormUserRole3 = new UserRole().withApplication(applicationFormThree).withRole(role).withUser(applicant);
        UserRole applicationFormUserRole4 = new UserRole().withApplication(applicationFormFour).withRole(role).withUser(applicant);

        save(applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour, applicationFormUserRole1, applicationFormUserRole2,
                applicationFormUserRole3, applicationFormUserRole4);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_STATUS).searchTerm("approv").build();
        List<ApplicationDescriptor> applications = applicationsService.getApplicationsForList(applicant,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, filter));

        assertEquals(1, applications.size());
        assertEquals(applicationFormOne.getId(), applications.get(0).getApplicationFormId());
    }

    @Test
    public void shouldGetAllApplicationsInApprovedStage() throws ParseException {

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().status(new State().withId(PrismState.APPLICATION_APPROVED))
                .applicationNumber("ABC").advert(program).createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();
        ApplicationDescriptor applicationDescriptorOne = new ApplicationDescriptor();
        applicationDescriptorOne.setApplicationFormId(applicationFormOne.getId());

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicationNumber("App_Biology").advert(program)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().status(new State().withId(PrismState.APPLICATION_VALIDATION))
                .applicationNumber("ABCD").advert(program).createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant)
                .build();

        ApplicationForm applicationFormFour = new ApplicationFormBuilder().applicationNumber("BIOLOGY1").advert(program)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();

        Role role = roleDAO.getById(Authority.APPLICATION_CREATOR);
        UserRole applicationFormUserRole1 = new UserRole().withApplication(applicationFormOne).withRole(role).withUser(applicant);
        UserRole applicationFormUserRole2 = new UserRole().withApplication(applicationFormTwo).withRole(role).withUser(applicant);
        UserRole applicationFormUserRole3 = new UserRole().withApplication(applicationFormThree).withRole(role).withUser(applicant);
        UserRole applicationFormUserRole4 = new UserRole().withApplication(applicationFormFour).withRole(role).withUser(applicant);

        save(applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour, applicationFormUserRole1, applicationFormUserRole2,
                applicationFormUserRole3, applicationFormUserRole4);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_STATUS).searchTerm("Offer").build();
        List<ApplicationDescriptor> applications = applicationsService.getApplicationsForList(applicant,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, filter));

        assertEquals(1, applications.size());
        assertEquals(applicationFormOne.getId(), applications.get(0).getApplicationFormId());
    }

    @Test
    public void shouldNotReturnAppIfNoStatusMatching() throws ParseException {

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().status(new State().withId(PrismState.APPLICATION_APPROVED))
                .applicationNumber("ABC").advert(program).createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicationNumber("App_Biology").advert(program)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().status(new State().withId(PrismState.APPLICATION_VALIDATION))
                .applicationNumber("ABCD").advert(program).createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant)
                .build();

        ApplicationForm applicationFormFour = new ApplicationFormBuilder().applicationNumber("BIOLOGY1").advert(program)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant).build();

        Role role = roleDAO.getById(Authority.APPLICATION_CREATOR);
        UserRole applicationFormUserRole1 = new UserRole().withApplication(applicationFormOne).withRole(role).withUser(applicant);
        UserRole applicationFormUserRole2 = new UserRole().withApplication(applicationFormTwo).withRole(role).withUser(applicant);
        UserRole applicationFormUserRole3 = new UserRole().withApplication(applicationFormThree).withRole(role).withUser(applicant);
        UserRole applicationFormUserRole4 = new UserRole().withApplication(applicationFormFour).withRole(role).withUser(applicant);

        save(applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour, applicationFormUserRole1, applicationFormUserRole2,
                applicationFormUserRole3, applicationFormUserRole4);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("foobar").build();
        List<ApplicationDescriptor> applications = applicationsService.getApplicationsForList(applicant,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1, filter));

        assertEquals(0, applications.size());
    }

    @Test
    public void shouldSearchAndSort() throws ParseException {
        ApplicationForm applicationFormOne = new ApplicationFormBuilder().status(new State().withId(PrismState.APPLICATION_INTERVIEW))
                .applicationNumber("zzzFooBarzzz").advert(program).createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
                .applicant(applicant).build();

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicationNumber("App_Biology").advert(program)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/04")).applicant(applicant).build();

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().status(new State().withId(PrismState.APPLICATION_VALIDATION))
                .applicationNumber("zzzFooBarzzz1").advert(program).createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/05"))
                .applicant(applicant).build();

        ApplicationForm applicationFormFour = new ApplicationFormBuilder().applicationNumber("BIOLOGY1").advert(program)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/06")).applicant(applicant).build();

        Role applicantRole = roleDAO.getById(Authority.APPLICATION_CREATOR);
        UserRole applicationFormUserRole1 = new UserRole().withApplication(applicationFormOne).withRole(applicantRole).withUser(applicant);
        UserRole applicationFormUserRole2 = new UserRole().withApplication(applicationFormTwo).withRole(applicantRole).withUser(applicant);
        UserRole applicationFormUserRole3 = new UserRole().withApplication(applicationFormThree).withRole(applicantRole).withUser(applicant);
        UserRole applicationFormUserRole4 = new UserRole().withApplication(applicationFormFour).withRole(applicantRole).withUser(applicant);

        save(applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour, applicationFormUserRole1, applicationFormUserRole2,
                applicationFormUserRole3, applicationFormUserRole4);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("zzzFooBarz").build();
        List<ApplicationDescriptor> applications = applicationsService.getApplicationsForList(applicant,
                newFiltering(SortCategory.APPLICATION_STATUS, SortOrder.ASCENDING, 1, filter));

        assertEquals(2, applications.size());
        assertEquals(applicationFormOne.getId(), applications.get(0).getApplicationFormId());
        assertEquals(applicationFormThree.getId(), applications.get(1).getApplicationFormId());
    }

    @Test
    public void shouldSortApplicationInNaturalSortOrder() throws ParseException {

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().status(new State().withId(PrismState.APPLICATION_APPROVED))
                .applicationNumber("ABC").advert(program).applicant(applicant).build();

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicationNumber("App_Biology").advert(program)
                .submittedDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/03")).applicant(applicant).build();

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().status(new State().withId(PrismState.APPLICATION_APPROVED))
                .applicationNumber("ABCD").advert(program).submittedDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).applicant(applicant).build();

        ApplicationForm applicationFormFour = new ApplicationFormBuilder().applicationNumber("BIOLOGY1").advert(program)
                .submittedDate(new SimpleDateFormat("yyyy/MM/dd").parse("2013/03/03")).applicant(applicant).build();

        Role applicantRole = roleDAO.getById(Authority.APPLICATION_CREATOR);
        UserRole applicationFormUserRole1 = new UserRole().withApplication(applicationFormOne).withRole(applicantRole).withUser(applicant);
        UserRole applicationFormUserRole2 = new UserRole().withApplication(applicationFormTwo).withRole(applicantRole).withUser(applicant);
        UserRole applicationFormUserRole3 = new UserRole().withApplication(applicationFormThree).withRole(applicantRole).withUser(applicant);
        UserRole applicationFormUserRole4 = new UserRole().withApplication(applicationFormFour).withRole(applicantRole).withUser(applicant);

        save(applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour, applicationFormUserRole1, applicationFormUserRole2,
                applicationFormUserRole3, applicationFormUserRole4);

        flushAndClearSession();

        List<ApplicationDescriptor> applications = applicationsService.getApplicationsForList(applicant,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1));

        Assert.assertEquals(applicationFormOne.getId(), applications.get(0).getApplicationFormId());
        Assert.assertEquals(applicationFormTwo.getId(), applications.get(1).getApplicationFormId());
        Assert.assertEquals(applicationFormThree.getId(), applications.get(2).getApplicationFormId());
        Assert.assertEquals(applicationFormFour.getId(), applications.get(3).getApplicationFormId());
    }

    @Test
    public void shouldSortApplicationWithApplName() throws ParseException {
        User applicant1 = new UserBuilder().firstName("AAAA").lastName("BBBB")
        // .withRole(roleDAO.getById(Authority.APPLICANT))
                .build();
        User applicant2 = new UserBuilder().firstName("AAAA").lastName("CCCC")
        // .withRole(roleDAO.getById(Authority.APPLICANT))
                .build();
        User applicant3 = new UserBuilder().firstName("BBBB").lastName("AAAA")
        // .withRole(roleDAO.getById(Authority.APPLICANT))
                .build();
        User applicant4 = new UserBuilder().firstName("CCCC").lastName("AAAA")
        // .withRole(roleDAO.getById(Authority.APPLICANT))
                .build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicant(applicant1)
                .status(new State().withId(PrismState.APPLICATION_APPROVED)).applicationNumber("ABCDE1").advert(program)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).build();

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicant(applicant2)
                .status(new State().withId(PrismState.APPLICATION_APPROVED)).applicationNumber("ABCDE2").advert(program)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/03")).build();

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().applicant(applicant3)
                .status(new State().withId(PrismState.APPLICATION_APPROVED)).applicationNumber("ABCDE3").advert(program)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).build();

        ApplicationForm applicationFormFour = new ApplicationFormBuilder().applicant(applicant4).applicationNumber("ABCDE4")
                .status(new State().withId(PrismState.APPLICATION_APPROVED)).advert(program)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2013/03/03")).build();

        Role role = roleDAO.getById(Authority.SYSTEM_ADMINISTRATOR);
        UserRole applicationFormUserRole1 = new UserRole().withApplication(applicationFormOne).withRole(role).withUser(superUser);
        UserRole applicationFormUserRole2 = new UserRole().withApplication(applicationFormTwo).withRole(role).withUser(superUser);
        UserRole applicationFormUserRole3 = new UserRole().withApplication(applicationFormThree).withRole(role).withUser(superUser);
        UserRole applicationFormUserRole4 = new UserRole().withApplication(applicationFormFour).withRole(role).withUser(superUser);

        save(applicant1, applicant2, applicant3, applicant4, applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour,
                applicationFormUserRole1, applicationFormUserRole2, applicationFormUserRole3, applicationFormUserRole4);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("ABCD").build();
        List<ApplicationDescriptor> applications = applicationsService.getApplicationsForList(superUser,
                newFiltering(SortCategory.APPLICANT_NAME, SortOrder.ASCENDING, 1, filter));

        Assert.assertEquals(4, applications.size());
        Assert.assertEquals("The users should be ordered by lastname first.", applicationFormThree.getId(), applications.get(0).getApplicationFormId());
        Assert.assertEquals("The users should be ordered by lastname first.", applicationFormFour.getId(), applications.get(1).getApplicationFormId());
        Assert.assertEquals("The users should be ordered by lastname first.", applicationFormOne.getId(), applications.get(2).getApplicationFormId());
        Assert.assertEquals("The users should be ordered by lastname first.", applicationFormTwo.getId(), applications.get(3).getApplicationFormId());
    }

    @Test
    public void shouldSortApplicationWithApplStatus() throws ParseException {
        User applicant1 = new UserBuilder().firstName("AAAA").lastName("BBBB")
        // .withRole(roleDAO.getById(Authority.APPLICANT))
                .build();
        User applicant2 = new UserBuilder().firstName("AAAA").lastName("CCCC")
        // .withRole(roleDAO.getById(Authority.APPLICANT))
                .build();
        User applicant3 = new UserBuilder().firstName("BBBB").lastName("AAAA")
        // .withRole(roleDAO.getById(Authority.APPLICANT))
                .build();
        User applicant4 = new UserBuilder().firstName("CCCC").lastName("AAAA")
        // .withRole(roleDAO.getById(Authority.APPLICANT))
                .build();

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicant(applicant1)
                .status(new State().withId(PrismState.APPLICATION_APPROVED)).applicationNumber("ABCDE1").advert(program)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).build();

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicant(applicant2)
                .status(new State().withId(PrismState.APPLICATION_INTERVIEW)).applicationNumber("ABCDE2").advert(program)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/03")).build();

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().applicant(applicant3)
                .status(new State().withId(PrismState.APPLICATION_REVIEW)).applicationNumber("ABCDE3").advert(program)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).build();

        ApplicationForm applicationFormFour = new ApplicationFormBuilder().applicant(applicant4).applicationNumber("ABCDE4")
                .status(new State().withId(PrismState.APPLICATION_WITHDRAWN)).advert(program)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2013/03/03")).build();

        Role superadministratorRole = roleDAO.getById(Authority.SYSTEM_ADMINISTRATOR);
        UserRole applicationFormUserRole1 = new UserRole().withApplication(applicationFormOne).withRole(superadministratorRole).withUser(superUser);
        UserRole applicationFormUserRole2 = new UserRole().withApplication(applicationFormTwo).withRole(superadministratorRole).withUser(superUser);
        UserRole applicationFormUserRole3 = new UserRole().withApplication(applicationFormThree).withRole(superadministratorRole).withUser(superUser);
        UserRole applicationFormUserRole4 = new UserRole().withApplication(applicationFormFour).withRole(superadministratorRole).withUser(superUser);

        save(applicant1, applicant2, applicant3, applicant4, applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour,
                applicationFormUserRole1, applicationFormUserRole2, applicationFormUserRole3, applicationFormUserRole4);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("ABCD").build();
        List<ApplicationDescriptor> applications = applicationsService.getApplicationsForList(superUser,
                newFiltering(SortCategory.APPLICATION_STATUS, SortOrder.ASCENDING, 1, filter));

        Assert.assertEquals(4, applications.size());
        Assert.assertEquals(applicationFormOne.getId(), applications.get(0).getApplicationFormId());
        Assert.assertEquals(applicationFormTwo.getId(), applications.get(1).getApplicationFormId());
        Assert.assertEquals(applicationFormThree.getId(), applications.get(2).getApplicationFormId());
        Assert.assertEquals(applicationFormFour.getId(), applications.get(3).getApplicationFormId());
    }

    @Test
    public void shouldSortApplicationWithProgramName() throws ParseException {
        User applicant1 = new UserBuilder().firstName("AAAA").lastName("BBBB")
        // .withRole(roleDAO.getById(Authority.APPLICANT))
                .build();
        User applicant2 = new UserBuilder().firstName("AAAA").lastName("CCCC")
        // .withRole(roleDAO.getById(Authority.APPLICANT))
                .build();
        User applicant3 = new UserBuilder().firstName("BBBB").lastName("AAAA")
        // .withRole(roleDAO.getById(Authority.APPLICANT))
                .build();
        User applicant4 = new UserBuilder().firstName("CCCC").lastName("AAAA")
        // .withRole(roleDAO.getById(Authority.APPLICANT))
                .build();

        Program program1 = new ProgramBuilder().code("empty1").title("AAA").institution(institution)
                .contactUser(testObjectProvider.getEnabledUserInRole(Authority.SYSTEM_ADMINISTRATOR)).build();
        Program program2 = new ProgramBuilder().code("empty2").title("CCC").institution(institution)
                .contactUser(testObjectProvider.getEnabledUserInRole(Authority.SYSTEM_ADMINISTRATOR)).build();
        Program program3 = new ProgramBuilder().code("empty3").title("BBB").institution(institution)
                .contactUser(testObjectProvider.getEnabledUserInRole(Authority.SYSTEM_ADMINISTRATOR)).build();
        Program program4 = new ProgramBuilder().code("empty4").title("DDD").institution(institution)
                .contactUser(testObjectProvider.getEnabledUserInRole(Authority.SYSTEM_ADMINISTRATOR)).build();
        save(program1, program2, program3, program4);

        ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicant(applicant1)
                .status(new State().withId(PrismState.APPLICATION_APPROVED)).applicationNumber("ABCDE1").advert(program1)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).build();

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicant(applicant2)
                .status(new State().withId(PrismState.APPLICATION_INTERVIEW)).applicationNumber("ABCDE2").advert(program2)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/03")).build();

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().applicant(applicant3)
                .status(new State().withId(PrismState.APPLICATION_REVIEW)).applicationNumber("ABCDE3").advert(program3)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).build();

        ApplicationForm applicationFormFour = new ApplicationFormBuilder().applicant(applicant4).applicationNumber("ABCDE4")
                .status(new State().withId(PrismState.APPLICATION_WITHDRAWN)).advert(program4)
                .createdTimestamp(new SimpleDateFormat("yyyy/MM/dd").parse("2013/03/03")).build();

        Role superadministratorRole = roleDAO.getById(Authority.SYSTEM_ADMINISTRATOR);
        UserRole applicationFormUserRole1 = new UserRole().withApplication(applicationFormOne).withRole(superadministratorRole).withUser(superUser);
        UserRole applicationFormUserRole2 = new UserRole().withApplication(applicationFormTwo).withRole(superadministratorRole).withUser(superUser);
        UserRole applicationFormUserRole3 = new UserRole().withApplication(applicationFormThree).withRole(superadministratorRole).withUser(superUser);
        UserRole applicationFormUserRole4 = new UserRole().withApplication(applicationFormFour).withRole(superadministratorRole).withUser(superUser);

        save(applicant1, applicant2, applicant3, applicant4, applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour,
                applicationFormUserRole1, applicationFormUserRole2, applicationFormUserRole3, applicationFormUserRole4);
        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("ABCD").build();
        List<ApplicationDescriptor> applications = applicationsService.getApplicationsForList(superUser,
                newFiltering(SortCategory.PROGRAMME_NAME, SortOrder.DESCENDING, 1, filter));

        Assert.assertEquals(applicationFormFour.getId(), applications.get(0).getApplicationFormId());
        Assert.assertEquals(applicationFormTwo.getId(), applications.get(1).getApplicationFormId());
        Assert.assertEquals(applicationFormThree.getId(), applications.get(2).getApplicationFormId());
        Assert.assertEquals(applicationFormOne.getId(), applications.get(3).getApplicationFormId());

    }

    @Test
    public void shouldLimitApplicationList() throws ParseException {
        Role role = roleDAO.getById(Authority.SYSTEM_ADMINISTRATOR);

        List<UserRole> applicationFormUserRoles = Lists.newArrayList();
        List<ApplicationForm> returnedAppls = Lists.newArrayList();
        for (int i = 0; i < 70; i++) {
            ApplicationForm form = new ApplicationFormBuilder().status(new State().withId(PrismState.APPLICATION_APPROVED))
                    .applicationNumber("ABCDEFG" + i).advert(program).applicant(applicant).build();

            UserRole applicationFormUserRole = new UserRole().withApplication(form).withRole(role).withUser(superUser);
            applicationFormUserRoles.add(applicationFormUserRole);
            returnedAppls.add(form);
        }
        Collections.shuffle(returnedAppls);

        save(returnedAppls);
        save(applicationFormUserRoles);

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.APPLICATION_NUMBER).searchTerm("ABCDEFG").build();
        List<ApplicationDescriptor> applications = applicationsService.getApplicationsForList(superUser,
                newFiltering(SortCategory.PROGRAMME_NAME, SortOrder.DESCENDING, 1, filter));

        Assert.assertEquals(APPLICATION_BLOCK_SIZE, applications.size());
    }

    @Test
    public void shouldReturnApplicationWithSupervisorInProgrammeDetails() {
        User applicant = new UserBuilder().firstName("AAAA").lastName("BBBB")
        // .withRole(roleDAO.getById(Authority.APPLICANT))
                .build();
        SuggestedSupervisor supervisor = new SuggestedSupervisor().withAware(true).withUser(
                new User().withEmail("threepwood@monkeyisland.com").withFirstName("Guybrush").withLastName("Threepwood"));
        SourcesOfInterest sourcesOfInterest = new SourcesOfInterestBuilder().name("foo").code("foo").build();
        ProgramDetails programmeDetails = new ProgrammeDetailsBuilder().studyOption(new StudyOption("Half", "Half")).startDate(new Date())
                .sourcesOfInterest(sourcesOfInterest).suggestedSupervisors(supervisor).build();
        ApplicationForm formWithSupervisor = new ApplicationFormBuilder().status(new State().withId(PrismState.APPLICATION_REVIEW))
                .applicant(applicant).programmeDetails(programmeDetails).advert(program).build();

        Role superadministratorRole = roleDAO.getById(Authority.SYSTEM_ADMINISTRATOR);
        UserRole applicationFormUserRole1 = new UserRole().withApplication(formWithSupervisor).withRole(superadministratorRole).withUser(superUser);

        save(applicant, supervisor, sourcesOfInterest, programmeDetails, formWithSupervisor, applicationFormUserRole1);
        programmeDetails.setApplication(formWithSupervisor);
        save(programmeDetails);

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.SUPERVISOR).searchTerm("Threepwood").build();

        List<ApplicationDescriptor> applications = applicationsService.getApplicationsForList(superUser,
                newFiltering(SortCategory.PROGRAMME_NAME, SortOrder.DESCENDING, 1, filter));
        assertEquals(1, applications.size());
        assertEquals(supervisor.getUser().getLastName(), applicationFormDAO.getById(applications.get(0).getApplicationFormId()).getProgramDetails()
                .getSuggestedSupervisors().get(0).getUser().getLastName());
    }

    // TODO fix test (approvalComment instead of approvalRound)
    // @Test
    // public void shouldReturnApplicationWithSupervisorInOneOfTheApprovalRounds() {
    // RegisteredUser applicant = new RegisteredUserBuilder().firstName("AAAA").lastName("BBBB")
    // .withRole(roleDAO.getRoleByAuthority(Authority.APPLICANT)).build();
    // RegisteredUser supervisorUser1 = new RegisteredUserBuilder().firstName("Guybrush").lastName("Threepwood")
    // .withRole(roleDAO.getRoleByAuthority(Authority.SUPERVISOR)).build();
    // Supervisor supervisor1 = new SupervisorBuilder().isPrimary(true).withUser(supervisorUser1).build();
    // RegisteredUser supervisorUser2 = new RegisteredUserBuilder().firstName("Herman").lastName("Toothrot")
    // .withRole(roleDAO.getRoleByAuthority(Authority.SUPERVISOR)).build();
    // Supervisor supervisor2 = new SupervisorBuilder().isPrimary(false).withUser(supervisorUser1).build();
    // ApprovalRound approvalRound1 = new ApprovalRoundBuilder().createdDate(new Date()).supervisors(supervisor1).build();
    // ApprovalRound approvalRound2 = new ApprovalRoundBuilder().createdDate(new Date()).supervisors(supervisor2).build();
    // ApplicationForm formWithSupervisor = new ApplicationFormBuilder().status(new
    // State().withId(ApplicationFormStatus.REVIEW)).applicant(applicant).program(program)
    // .approvalRounds(approvalRound1, approvalRound2).build();
    //
    // Role superadministratorRole = roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR);
    // UserRole applicationFormUserRole1 = new UserRole().withApplication(formWithSupervisor)
    // .withRole(superadministratorRole).withUser(superUser);
    //
    // save(applicant, supervisorUser1, supervisor1, supervisorUser2, supervisor2, approvalRound1, approvalRound2, formWithSupervisor,
    // applicationFormUserRole1);
    //
    // ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.SUPERVISOR).searchTerm("Threepwood").build();
    //
    // List<ApplicationDescriptor> applications = applicationsService.getAllVisibleAndMatchedApplicationsForList(superUser,
    // newFiltering(SortCategory.PROGRAMME_NAME, SortOrder.DESCENDING, 1, filter));
    // assertEquals(1, applications.size());
    // assertEquals(supervisor1.getUser().getLastName(), applicationFormDAO.get(applications.get(0).getApplicationFormId()).getApprovalRounds().get(0)
    // .getSupervisors().get(0).getUser().getLastName());
    // }

    @Test
    public void shouldReturnApplicationsBasedOnTheirClosingDate() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
        ApplicationForm applicationFormOne = new ApplicationFormBuilder().advert(program).applicant(applicant)
                .status(new State().withId(PrismState.APPLICATION_VALIDATION)).submittedDate(new Date())
                .createdTimestamp(format.parse("01 01 2012")).closingDate(format.parse("01 01 2050")).build();

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().advert(program).applicant(applicant)
                .status(new State().withId(PrismState.APPLICATION_UNSUBMITTED)).createdTimestamp(format.parse("01 01 2012"))
                .closingDate(format.parse("01 01 2050")).submittedDate(format.parse("01 04 2012")).build();

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().advert(program).applicant(applicant).closingDate(format.parse("01 01 2050"))
                .status(new State().withId(PrismState.APPLICATION_UNSUBMITTED)).createdTimestamp(format.parse("01 02 2012")).build();

        ApplicationForm applicationFormFour = new ApplicationFormBuilder().advert(program).applicant(applicant)
                .status(new State().withId(PrismState.APPLICATION_UNSUBMITTED)).createdTimestamp(format.parse("01 02 2012"))
                .closingDate(format.parse("01 01 2050")).submittedDate(format.parse("01 03 2012")).build();

        Role role = roleDAO.getById(Authority.APPLICATION_CREATOR);
        UserRole applicationFormUserRole1 = new UserRole().withApplication(applicationFormOne).withRole(role).withUser(applicant);
        UserRole applicationFormUserRole2 = new UserRole().withApplication(applicationFormTwo).withRole(role).withUser(applicant);
        UserRole applicationFormUserRole3 = new UserRole().withApplication(applicationFormThree).withRole(role).withUser(applicant);
        UserRole applicationFormUserRole4 = new UserRole().withApplication(applicationFormFour).withRole(role).withUser(applicant);

        save(applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour, applicationFormUserRole1, applicationFormUserRole2,
                applicationFormUserRole3, applicationFormUserRole4);

        flushAndClearSession();

        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.CLOSING_DATE).searchPredicate(SearchPredicate.ON_DATE)
                .searchTerm("01 Jan 2050").build();

        List<ApplicationDescriptor> applications = applicationsService.getApplicationsForList(applicant,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, filter));

        assertEquals(applicationFormOne.getId(), applications.get(0).getApplicationFormId());
        assertEquals(applicationFormTwo.getId(), applications.get(1).getApplicationFormId());
        assertEquals(applicationFormFour.getId(), applications.get(2).getApplicationFormId());
        assertEquals(applicationFormThree.getId(), applications.get(3).getApplicationFormId());
    }

    @Test
    public void shouldReturnApplicationWithProjectTitle() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
        ApplicationForm applicationFormOne = new ApplicationFormBuilder().status(new State().withId(PrismState.APPLICATION_REVIEW)).advert(program)
                .applicant(applicant).submittedDate(new Date()).createdTimestamp(format.parse("01 01 2012")).dueDate(format.parse("01 01 2050")).build();

        Role role = roleDAO.getById(Authority.SYSTEM_ADMINISTRATOR);
        UserRole applicationFormUserRole = new UserRole().withApplication(applicationFormOne).withRole(role).withUser(superUser);

        save(applicationFormOne, applicationFormUserRole);
        ApplicationsFilter filter = new ApplicationsFilterBuilder().searchCategory(SearchCategory.PROJECT_TITLE).searchTerm("another title").build();
        List<ApplicationDescriptor> applications = applicationsService.getApplicationsForList(superUser,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1, filter));
        assertEquals(1, applications.size());

    }

    private ApplicationsFiltering newFiltering(SortCategory sortCategory, SortOrder sortOrder, int blockCount, ApplicationsFilter... filters) {
        return new ApplicationsFilteringBuilder().sortCategory(sortCategory).order(sortOrder).blockCount(blockCount).filters(filters).build();
    }

    private boolean listContainsId(ApplicationForm application, List<ApplicationDescriptor> applications) {
        for (ApplicationDescriptor entry : applications) {
            if (application.getId().equals(entry.getApplicationFormId())) {
                return true;
            }
        }
        return false;
    }
}