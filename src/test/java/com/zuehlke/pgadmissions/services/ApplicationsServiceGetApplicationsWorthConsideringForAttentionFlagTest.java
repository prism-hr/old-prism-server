package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormListDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationsFilteringBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationsPreFilter;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;

public class ApplicationsServiceGetApplicationsWorthConsideringForAttentionFlagTest extends AutomaticRollbackTestCase {

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
        applicationsService = new ApplicationsService(applicationFormDAO, applicationFormListDAO, null);
        roleDAO = new RoleDAO(sessionFactory);
        user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").role(roleDAO.getRoleByAuthority(Authority.APPLICANT))
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();
        superUser = new RegisteredUserBuilder().firstName("John").lastName("Doe").email("email@test.com")
                .username("superUserUsername").password("password")
                .role(roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR)).accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(true).build();
        program = new ProgramBuilder().code("doesntexist").administrators(superUser).title("another title").build();
        superUser.getProgramsOfWhichAdministrator().add(program);
        superUser.getProgramsOfWhichApprover().add(program);
        save(user, superUser, program);
        flushAndClearSession();
    }

    @Test
    public void shouldReturnApplicationsAUserIsInterestedIn() throws ParseException {
        ApprovalRound approvalRound = new ApprovalRoundBuilder().build();
        ApplicationForm applicationFormOne = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVAL)
                .applicationNumber("ABC").program(program)
                .appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
                .latestApprovalRound(approvalRound).pendingApprovalRestart(true).applicant(user)
                .build();

        ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicationNumber("App_Biology")
                .program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(user)
                .build();

        ApplicationForm applicationFormThree = new ApplicationFormBuilder().status(ApplicationFormStatus.APPROVAL)
                .applicationNumber("ABCD").program(program).latestApprovalRound(approvalRound)
                .pendingApprovalRestart(true).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03"))
                .applicant(user).build();

        ApplicationForm applicationFormFour = new ApplicationFormBuilder().applicationNumber("BIOLOGY1")
                .program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(user)
                .build();

        save(approvalRound, applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour);

        flushAndClearSession();

        List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(superUser,
                newFiltering(SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1));

        assertEquals(2, applications.size());
        assertTrue(listContainsId(applicationFormOne, applications));
        assertTrue(listContainsId(applicationFormThree, applications));
    }
    
    private ApplicationsFiltering newFiltering(SortCategory sortCategory, SortOrder sortOrder, int blockCount, ApplicationsFilter... filters) {
        return new ApplicationsFilteringBuilder().sortCategory(sortCategory).order(sortOrder).blockCount(blockCount)
                .filters(filters).preFilter(ApplicationsPreFilter.URGENT).build();
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
