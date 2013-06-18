package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;

public class ProgramDAOTest extends AutomaticRollbackTestCase {

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        ProgramDAO programDAO = new ProgramDAO();
        programDAO.getAllPrograms();
    }

    @Test
    public void shouldGetAllPrograms() {
        BigInteger existingNumberOfPrograms = (BigInteger) sessionFactory.getCurrentSession().createSQLQuery("select count(*) from PROGRAM").uniqueResult();
        Program program1 = new ProgramBuilder().id(1).code("code1").title("another title").build();
        Program program2 = new ProgramBuilder().id(1).code("code2").title("another title").build();
        sessionFactory.getCurrentSession().save(program1);
        sessionFactory.getCurrentSession().save(program2);
        flushAndClearSession();
        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        Assert.assertEquals(existingNumberOfPrograms.intValue() + 2, programDAO.getAllPrograms().size());
    }

    @Test
    public void shouldGetProgramById() {
        Program program = new ProgramBuilder().id(1).code("code1").title("another title").build();

        sessionFactory.getCurrentSession().save(program);
        flushAndClearSession();

        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        assertEquals(program.getId(), programDAO.getProgramById(program.getId()).getId());
    }

    @Test
    public void shouldGetProgramByCode() {
        Program program = new ProgramBuilder().id(1).code("code1").title("another title").build();

        sessionFactory.getCurrentSession().save(program);
        flushAndClearSession();

        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        assertEquals(program.getId(), programDAO.getProgramByCode("code1").getId());
    }

    @Test
    public void shouldSaveProgram() {
        Program program = new ProgramBuilder().code("code1").title("another title").build();

        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        programDAO.save(program);
        Assert.assertNotNull(program.getId());
    }

    @Test
    public void shouldGetProgramWithScoringDefinitions() {
        Program program = new ProgramBuilder().code("code1").title("another title").build();
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
        Program program = new ProgramBuilder().code("code1").title("another title").build();
        RegisteredUser applicant = new RegisteredUserBuilder().username("applicant").build();
        RegisteredUser user = new RegisteredUserBuilder().username("aaa").build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(applicant).build();
        ReviewRound reviewRound = new ReviewRoundBuilder().application(applicationForm).build();
        Reviewer reviewer = new ReviewerBuilder().user(user).reviewRound(reviewRound).build();

        save(program, applicant, user, applicationForm, reviewRound, reviewer);
        flushAndClearSession();

        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        List<Program> programs = programDAO.getProgramsOfWhichPreviousReviewer(user);

        Assert.assertEquals(1, programs.size());
        Assert.assertEquals(program.getId(), programs.iterator().next().getId());
    }

    @Test
    public void shouldGetProgramOfWhichPreviousInterviewer() {
        Program program = new ProgramBuilder().code("code1").title("another title").build();
        RegisteredUser applicant = new RegisteredUserBuilder().username("applicant").build();
        RegisteredUser user = new RegisteredUserBuilder().username("aaa").build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(applicant).build();
        Interview interview = new InterviewBuilder().application(applicationForm).build();
        Interviewer interviewer = new InterviewerBuilder().user(user).interview(interview).build();

        save(program, applicant, user, applicationForm, interview, interviewer);
        flushAndClearSession();

        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        List<Program> programs = programDAO.getProgramsOfWhichPreviousInterviewer(user);

        Assert.assertEquals(1, programs.size());
        Assert.assertEquals(program.getId(), programs.iterator().next().getId());
    }

    @Test
    public void shouldGetProgramOfWhichPreviousSupervisor() {
        Program program = new ProgramBuilder().code("code1").title("another title").build();
        RegisteredUser applicant = new RegisteredUserBuilder().username("applicant").build();
        RegisteredUser user = new RegisteredUserBuilder().username("aaa").build();
        ApplicationForm applicationForm = new ApplicationFormBuilder().program(program).applicant(applicant).build();
        Supervisor supervisor = new SupervisorBuilder().user(user).isPrimary(true).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().application(applicationForm).supervisors(supervisor).build();

        save(program, applicant, user, applicationForm, approvalRound, supervisor);
        flushAndClearSession();

        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        List<Program> programs = programDAO.getProgramsOfWhichPreviousSupervisor(user);

        Assert.assertEquals(1, programs.size());
        Assert.assertEquals(program.getId(), programs.iterator().next().getId());
    }

}
