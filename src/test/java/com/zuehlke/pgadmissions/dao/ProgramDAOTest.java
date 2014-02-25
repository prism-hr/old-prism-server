package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramClosingDateBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;

public class ProgramDAOTest extends AutomaticRollbackTestCase {

    private QualificationInstitution institution;

    @Override
    public void setup() {
        super.setup();
        institution = new QualificationInstitutionBuilder().code("code").name("a14").domicileCode("AE").enabled(true).build();
        save(institution);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        ProgramDAO programDAO = new ProgramDAO();
        programDAO.getAllEnabledPrograms();
    }

    @Test
    public void shouldGetAllPrograms() {
        ProgramDAO programDAO = new ProgramDAO(sessionFactory);

        int existingNumberOfPrograms = programDAO.getAllEnabledPrograms().size();
        Program program1 = new ProgramBuilder().id(1).code("code1").title("another title").enabled(true).institution(institution).build();
        Program program2 = new ProgramBuilder().id(1).code("code2").title("another title2").enabled(true).institution(institution).build();
        sessionFactory.getCurrentSession().save(program1);
        sessionFactory.getCurrentSession().save(program2);
        flushAndClearSession();
        Assert.assertEquals(existingNumberOfPrograms + 2, programDAO.getAllEnabledPrograms().size());
    }

    @Test
    public void shouldGetProgramById() {
        Program program = new ProgramBuilder().id(1).code("code1").title("another title").institution(institution).build();

        sessionFactory.getCurrentSession().save(program);
        flushAndClearSession();

        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        assertEquals(program.getId(), programDAO.getProgramById(program.getId()).getId());
    }

    @Test
    public void shouldGetProgramByCode() {
        Program program = new ProgramBuilder().id(1).code("code1").title("another title").institution(institution).build();

        sessionFactory.getCurrentSession().save(program);
        flushAndClearSession();

        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        assertEquals(program.getId(), programDAO.getProgramByCode("code1").getId());
    }

    @Test
    public void shouldSaveProgram() {
        Program program = new ProgramBuilder().code("code1").title("another title").institution(institution).build();

        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        programDAO.save(program);
        Assert.assertNotNull(program.getId());
    }

    @Test
    public void shouldGetProgramWithScoringDefinitions() {
        Program program = new ProgramBuilder().code("code1").title("another title").institution(institution).build();
        ScoringDefinition scoringDef1 = new ScoringDefinition();
        scoringDef1.setContent("aaa");
        scoringDef1.setStage(ScoringStage.INTERVIEW);
        ScoringDefinition scoringDef2 = new ScoringDefinition();
        scoringDef2.setContent("bb");
        scoringDef2.setStage(ScoringStage.REVIEW);
        program.getScoringDefinitions().put(ScoringStage.INTERVIEW, scoringDef1);
        program.getScoringDefinitions().put(ScoringStage.REVIEW, scoringDef2);

        sessionFactory.getCurrentSession().save(program);
        flushAndClearSession();

        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        Program loadedProgram = programDAO.getProgramByCode("code1");
        assertEquals(program.getId(), loadedProgram.getId());

        Map<ScoringStage, ScoringDefinition> scoringDefinitions = loadedProgram.getScoringDefinitions();

        assertEquals(2, scoringDefinitions.size());

        ScoringDefinition interviewDefinition1 = scoringDefinitions.get(ScoringStage.INTERVIEW);
        assertEquals("aaa", interviewDefinition1.getContent());
        assertEquals(ScoringStage.INTERVIEW, interviewDefinition1.getStage());

        ScoringDefinition interviewDefinition2 = scoringDefinitions.get(ScoringStage.REVIEW);
        assertEquals("bb", interviewDefinition2.getContent());
        assertEquals(ScoringStage.REVIEW, interviewDefinition2.getStage());
    }

    @Test
    public void shouldGetProgramOfWhichPreviousReviewer() {
//        Program program = new ProgramBuilder().code("code1").title("another title").institution(institution).build();
//        RegisteredUser applicant = new RegisteredUserBuilder().username("applicant").build();
        RegisteredUser user = new RegisteredUserBuilder().username("aaa").build();
//        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(applicant).build();
//        ReviewRound reviewRound = new ReviewRoundBuilder().application(applicationForm).build();
//        Reviewer reviewer = new ReviewerBuilder().user(user).reviewRound(reviewRound).build();
//
//        save(program, applicant, user, applicationForm, reviewRound, reviewer);
//        flushAndClearSession();

        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        List<Program> programs = programDAO.getProgramsOfWhichPreviousReviewer(user);

        Assert.assertEquals(1, programs.size());
//        Assert.assertEquals(program.getId(), programs.iterator().next().getId());
    }

    @Test
    public void shouldGetProgramOfWhichPreviousInterviewer() {
//        Program program = new ProgramBuilder().code("code1").title("another title").institution(institution).build();
//        RegisteredUser applicant = new RegisteredUserBuilder().username("applicant").build();
        RegisteredUser user = new RegisteredUserBuilder().username("aaa").build();
//        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(applicant).build();
//        Interview interview = new InterviewBuilder().application(applicationForm).build();
//        Interviewer interviewer = new InterviewerBuilder().user(user).interview(interview).build();
//
//        save(program, applicant, user, applicationForm, interview, interviewer);
//        flushAndClearSession();

        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        List<Program> programs = programDAO.getProgramsOfWhichPreviousInterviewer(user);

        Assert.assertEquals(1, programs.size());
//        Assert.assertEquals(program.getId(), programs.iterator().next().getId());
    }

    @Test
    public void shouldGetProgramOfWhichPreviousSupervisor() {
//        Program program = new ProgramBuilder().code("code1").title("another title").institution(institution).build();
//        RegisteredUser applicant = new RegisteredUserBuilder().username("applicant").build();
        RegisteredUser user = new RegisteredUserBuilder().username("aaa").build();
//        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(applicant).build();
//        Supervisor supervisor = new SupervisorBuilder().user(user).isPrimary(true).build();
//        ApprovalRound approvalRound = new ApprovalRoundBuilder().application(applicationForm).supervisors(supervisor).build();
//
//        save(program, applicant, user, applicationForm, approvalRound, supervisor);
//        flushAndClearSession();

        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        List<Program> programs = programDAO.getProgramsOfWhichPreviousSupervisor(user);

        Assert.assertEquals(1, programs.size());
//        Assert.assertEquals(program.getId(), programs.iterator().next().getId());
    }

    @Test
    public void shouldReturnNextClosingDateForProgram() {
        DateTime closingDates = new DateTime();
        ProgramClosingDate badge1 = new ProgramClosingDateBuilder().closingDate(closingDates.minusMonths(1).toDate()).build();
        ProgramClosingDate badge2 = new ProgramClosingDateBuilder().closingDate(closingDates.plusMonths(1).toDate()).build();
        ProgramClosingDate badge3 = new ProgramClosingDateBuilder().closingDate(closingDates.plusMonths(2).toDate()).build();
        Program program = new ProgramBuilder().code("code2").institution(institution).closingDates(badge1, badge2, badge3).build();
        badge1.setProgram(program);
        badge2.setProgram(program);
        badge3.setProgram(program);

        save(program, badge1, badge2, badge3);
        flushAndClearSession();

        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        Date result = programDAO.getNextClosingDate(program);

        Assert.assertNotNull(result);

        Assert.assertEquals(0, result.compareTo(badge2.getClosingDate()));
    }

    @Test
    public void shouldGetLastCustomProgram() {
        Program program1 = ProgramBuilder.aProgram(institution).title("pdao1").code(institution.getCode() + "_00006").build();
        Program program2 = ProgramBuilder.aProgram(institution).title("pdao2").code(institution.getCode() + "_00008").build();
        Program program3 = ProgramBuilder.aProgram(institution).title("pdao3").code(institution.getCode() + "_00007").build();

        save(program1, program2, program3);

        flushAndClearSession();

        ProgramDAO programDAO = new ProgramDAO(sessionFactory);

        Program returned = programDAO.getLastCustomProgram(institution);

        assertEquals(program2.getCode(), returned.getCode());
    }

    @Test
    public void shouldGetClosingDateById() {
        ProgramClosingDate putClosingDate = new ProgramClosingDateBuilder().closingDate(new Date()).build();
        Program program = new ProgramBuilder().code("code").institution(institution).closingDates(putClosingDate).build();
        sessionFactory.getCurrentSession().save(program);
        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        ProgramClosingDate gotClosingDate = programDAO.getClosingDateById(putClosingDate.getId());
        assertEquals(putClosingDate, gotClosingDate);
    }

    @Test
    public void shouldGetClosingDateByDate() {
        Date closingDate = new Date();
        ProgramClosingDate putClosingDate = new ProgramClosingDateBuilder().closingDate(closingDate).build();
        Program program = new ProgramBuilder().code("code").institution(institution).closingDates(putClosingDate).build();
        sessionFactory.getCurrentSession().save(program);
        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        ProgramClosingDate gotClosingDate = programDAO.getClosingDateByDate(program, closingDate);
        assertEquals(putClosingDate, gotClosingDate);
    }

    @Test
    public void shouldUpdateClosingDate() {
        DateTime dateToday = new DateTime(new Date());
        DateTime truncatedDateToday = new DateTime(dateToday.getYear(), dateToday.getMonthOfYear(), dateToday.getDayOfMonth(), 0, 0, 0);
        DateTime truncatedDateTomorrow = truncatedDateToday.plusDays(1);
        Date testDateOne = truncatedDateToday.toDate();
        Date testDateTwo = truncatedDateTomorrow.toDate();
        ProgramClosingDate putClosingDate = new ProgramClosingDateBuilder().closingDate(testDateOne).build();
        Program program = new ProgramBuilder().code("code").institution(institution).closingDates(putClosingDate).build();
        sessionFactory.getCurrentSession().save(program);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();
        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        ProgramClosingDate gotClosingDate = programDAO.getClosingDateById(putClosingDate.getId());
        gotClosingDate.setClosingDate(testDateTwo);
        programDAO.updateClosingDate(gotClosingDate);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();
        assertEquals(gotClosingDate.getClosingDate(), testDateTwo);
    }

    @Test
    public void shouldDeleteClosingDate() {
        ProgramClosingDate putClosingDate = new ProgramClosingDateBuilder().closingDate(new Date()).build();
        Integer putClosingDateId = putClosingDate.getId();
        Program program = new ProgramBuilder().code("code").institution(institution).closingDates(putClosingDate).build();
        sessionFactory.getCurrentSession().save(program);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();
        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        programDAO.deleteClosingDate(putClosingDate);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();
        ProgramClosingDate gotClosingDate = programDAO.getClosingDateById(putClosingDateId);
        assertEquals(gotClosingDate, null);
    }

    @Test
    public void shouldGetFirstEnabledAdministrator() {
        Program program = new ProgramBuilder().code("code").institution(institution).build();
        sessionFactory.getCurrentSession().save(program);
        RegisteredUser user1 = new RegisteredUserBuilder().username("testuser1").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false)
                .enabled(false).programsOfWhichAdministrator(program).build();
        sessionFactory.getCurrentSession().save(user1);
        RegisteredUser user2 = new RegisteredUserBuilder().username("testuser2").accountNonExpired(true).accountNonLocked(true).credentialsNonExpired(true)
                .enabled(true).programsOfWhichAdministrator(program).build();
        sessionFactory.getCurrentSession().save(user2);
        RegisteredUser user3 = new RegisteredUserBuilder().username("testuser3").accountNonExpired(true).accountNonLocked(true).credentialsNonExpired(true)
                .enabled(true).programsOfWhichAdministrator(program).build();
        sessionFactory.getCurrentSession().save(user3);
        program.setAdministrators(Arrays.asList(user1, user2, user3));
        sessionFactory.getCurrentSession().update(program);
        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        RegisteredUser gotUser = programDAO.getFirstAdministratorForProgram(program);
        assertEquals(gotUser, user2);
    }

}
