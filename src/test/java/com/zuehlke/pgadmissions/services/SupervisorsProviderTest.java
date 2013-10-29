package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.SuggestedSupervisorBuilder;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class SupervisorsProviderTest {

    @Mock
    @InjectIntoByType
    private ApplicationsService applicationServiceMock;

    @Mock
    @InjectIntoByType
    private UserService userServiceMock;

    @TestedObject
    private SupervisorsProvider controller;

    @Test
    public void shouldGetProgrammeSupervisors() {
        final RegisteredUser interUser1 = new RegisteredUserBuilder().id(7).build();
        final RegisteredUser interUser2 = new RegisteredUserBuilder().id(6).build();

        final Program program = new ProgramBuilder().supervisors(interUser1, interUser2).id(6).build();
        final ApplicationForm application = new ApplicationFormBuilder().id(5).program(program).build();

        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("abc")).andReturn(application).anyTimes();

        replay();
        List<RegisteredUser> supervisorsUsers = controller.getProgrammeSupervisors("abc");
        verify();

        assertEquals(2, supervisorsUsers.size());
        assertTrue(supervisorsUsers.containsAll(Arrays.asList(interUser1, interUser2)));
    }

    @Test
    public void shouldGetApplicantNominatedSupervisors() {
        final RegisteredUser interUser1 = new RegisteredUserBuilder().id(1).build();
        final RegisteredUser interUser2 = new RegisteredUserBuilder().id(2).build();
        final RegisteredUser interUser3 = new RegisteredUserBuilder().id(3).build();
        final RegisteredUser interUser4 = new RegisteredUserBuilder().id(4).build();

        String emailOfSupervisor1 = "1@ucl.ac.uk";
        String emailOfSupervisor2 = "2@ucl.ac.uk";
        SuggestedSupervisor applicantNominatedSupervisor1 = new SuggestedSupervisorBuilder().id(1).email(emailOfSupervisor1).build();
        SuggestedSupervisor applicantNominatedSupervisor2 = new SuggestedSupervisorBuilder().id(2).email(emailOfSupervisor2).build();

        ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().suggestedSupervisors(applicantNominatedSupervisor1, applicantNominatedSupervisor2)
                .build();

        final Program program = new ProgramBuilder().id(6).supervisors(interUser1, interUser2, interUser3, interUser4).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).programmeDetails(programmeDetails).build();

        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm).anyTimes();

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(emailOfSupervisor1)).andReturn(interUser1).times(2);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(emailOfSupervisor2)).andReturn(interUser2).times(2);

        replay();
        List<RegisteredUser> nominatedSupervisors = controller.getNominatedSupervisors("5");
        List<RegisteredUser> programmeSupervisors = controller.getProgrammeSupervisors("5");
        verify();

        assertEquals(2, nominatedSupervisors.size());
        assertTrue(nominatedSupervisors.containsAll(Arrays.asList(interUser1, interUser2)));
        assertEquals(2, programmeSupervisors.size());
        assertTrue(programmeSupervisors.containsAll(Arrays.asList(interUser3, interUser4)));
    }

    @Test
    public void shouldRemoveApplicantNominatedSupervisorsFromProgramSupervisors() {
        final RegisteredUser interUser1 = new RegisteredUserBuilder().id(1).build();
        final RegisteredUser interUser2 = new RegisteredUserBuilder().id(2).build();

        String emailOfSupervisor1 = "1@ucl.ac.uk";
        String emailOfSupervisor2 = "2@ucl.ac.uk";
        SuggestedSupervisor applicantNominatedSupervisor1 = new SuggestedSupervisorBuilder().id(1).email(emailOfSupervisor1).build();
        SuggestedSupervisor applicantNominatedSupervisor2 = new SuggestedSupervisorBuilder().id(2).email(emailOfSupervisor2).build();

        ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().suggestedSupervisors(applicantNominatedSupervisor1, applicantNominatedSupervisor2)
                .build();

        final Program program = new ProgramBuilder().id(6).build();
        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).programmeDetails(programmeDetails).build();

        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm).anyTimes();

        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(emailOfSupervisor1)).andReturn(interUser1);
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(emailOfSupervisor2)).andReturn(interUser2);

        replay();
        List<RegisteredUser> nominatedSupervisors = controller.getNominatedSupervisors("5");
        verify();

        assertEquals(2, nominatedSupervisors.size());
        assertTrue(nominatedSupervisors.containsAll(Arrays.asList(interUser1, interUser2)));
    }

    @Test
    public void shouldGetListOfPreviousSupervisorsAndAddReviewersWillingToApprovalRoundWitDefaultSupervisorsAndApplicantSupervisorsRemoved() {
        final RegisteredUser defaultSupervisor = new RegisteredUserBuilder().id(9).build();
        final RegisteredUser interviewerWillingToSuperviseOne = new RegisteredUserBuilder().id(8).build();
        final RegisteredUser interviewerWillingToSuperviseTwo = new RegisteredUserBuilder().id(7).build();
        final RegisteredUser previousSupervisor = new RegisteredUserBuilder().id(6).build();
        InterviewComment interviewOne = new InterviewCommentBuilder().id(1).user(interviewerWillingToSuperviseOne).willingToSupervise(true).build();
        InterviewComment interviewTwo = new InterviewCommentBuilder().id(1).user(defaultSupervisor).willingToSupervise(true).build();
        InterviewComment interviewThree = new InterviewCommentBuilder().id(1).user(interviewerWillingToSuperviseTwo).willingToSupervise(true).build();

        String emailOfSupervisor1 = "1@ucl.ac.uk";
        String emailOfSupervisor2 = "2@ucl.ac.uk";
        SuggestedSupervisor applicantNominatedSupervisor1 = new SuggestedSupervisorBuilder().id(1).email(emailOfSupervisor1).build();
        SuggestedSupervisor applicantNominatedSupervisor2 = new SuggestedSupervisorBuilder().id(2).email(emailOfSupervisor2).build();
        ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().suggestedSupervisors(applicantNominatedSupervisor1, applicantNominatedSupervisor2)
                .build();

        final Program program = new ProgramBuilder().id(6).supervisors(defaultSupervisor).build();

        final ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).program(program).comments(interviewOne, interviewTwo, interviewThree)
                .programmeDetails(programmeDetails).build();
        EasyMock.expect(applicationServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm).anyTimes();

        EasyMock.expect(userServiceMock.getAllPreviousSupervisorsOfProgram(program)).andReturn(
                Arrays.asList(previousSupervisor, defaultSupervisor, interviewerWillingToSuperviseOne));
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(emailOfSupervisor1)).andReturn(defaultSupervisor).anyTimes();
        EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts(emailOfSupervisor2)).andReturn(interviewerWillingToSuperviseOne).anyTimes();

        replay();
        List<RegisteredUser> interviewerUsers = controller.getPreviousSupervisorsAndInterviewersWillingToSupervise("5");
        verify();

        assertEquals(2, interviewerUsers.size());
        assertTrue(interviewerUsers.containsAll(Arrays.asList(previousSupervisor, interviewerWillingToSuperviseTwo)));
    }

}
