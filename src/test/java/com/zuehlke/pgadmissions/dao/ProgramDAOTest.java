package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.builders.AdvertClosingDateBuilder;
import com.zuehlke.pgadmissions.domain.builders.TestData;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;

public class ProgramDAOTest extends AutomaticRollbackTestCase {

    private ProgramDAO programDAO;
    private Institution institution;
    private Project project;
    private ProgramType programType;
    private User user;
    private State state;

    @Override
    public void setup() {
        super.setup();
        programDAO = new ProgramDAO(sessionFactory);

        institution = testObjectProvider.getInstitution();
        project = testObjectProvider.getEnabledProject();
        programType = testObjectProvider.getProgramType();
        user = testObjectProvider.getEnabledUserInRole(Authority.PROGRAM_ADMINISTRATOR);
        state = testObjectProvider.getState(PrismState.PROGRAM_APPROVED);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGetAllPrograms() {
        List<Program> programs = (List<Program>) sessionFactory.getCurrentSession().createCriteria(Program.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).add(Restrictions.eq("state.id", PrismState.PROGRAM_APPROVED)).addOrder(Order.asc("title"))
                .list();
        Program program1 = TestData.aProgram(programType, institution, user, state).withCode("AAA").withTitle("p1");
        Program program2 = TestData.aProgram(programType, institution, user, state).withCode("BBB").withTitle("p2");
        sessionFactory.getCurrentSession().save(program1);
        sessionFactory.getCurrentSession().save(program2);
        flushAndClearSession();
        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        Assert.assertEquals(programs.size() + 2, programDAO.getAllEnabledPrograms().size());
    }

    @Test
    public void shouldGetProgramById() {
        Program program = TestData.aProgram(programType, institution, user, state);
        sessionFactory.getCurrentSession().save(program);
        flushAndClearSession();
        assertEquals(program.getId(), programDAO.getById(program.getId()).getId());
    }

    @Test
    public void shouldGetProgramByCode() {
        Program program = TestData.aProgram(programType, institution, user, state).withCode("code1");
        sessionFactory.getCurrentSession().save(program);
        flushAndClearSession();
        assertEquals(program.getId(), programDAO.getProgramByCode("code1").getId());
    }

    @Test
    public void shouldSaveProgram() {
        Program program = TestData.aProgram(programType, institution, user, state);
        programDAO.save(program);
        Assert.assertNotNull(program.getId());
    }

    @Test
    public void shouldGetProgramWithScoringDefinitions() {
        Program program = TestData.aProgram(programType, institution, user, state).withCode("code1");

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
    public void shouldReturnNextClosingDateForProgram() {
        LocalDate closingDates = new LocalDate();
        AdvertClosingDate badge1 = new AdvertClosingDateBuilder().closingDate(closingDates.minusMonths(1)).build();
        AdvertClosingDate badge2 = new AdvertClosingDateBuilder().closingDate(closingDates.plusMonths(1)).build();
        AdvertClosingDate badge3 = new AdvertClosingDateBuilder().closingDate(closingDates.plusMonths(2)).build();
        Program program = TestData.aProgram(programType, institution, user, state).withClosingDates(badge1, badge2, badge3);
        badge1.setAdvert(program);
        badge2.setAdvert(program);
        badge3.setAdvert(program);
        save(program);
        flushAndClearSession();

        LocalDate result = programDAO.getNextClosingDate(program);
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.compareTo(badge2.getClosingDate()));
    }

    @Test
    public void shouldGetLastCustomProgram() {
        Program program1 = TestData.aProgram(programType, institution, user, state).withInstitution(institution).withTitle("A");
        Program program2 = TestData.aProgram(programType, institution, user, state).withInstitution(institution).withTitle("B");
        Program program3 = TestData.aProgram(programType, institution, user, state).withInstitution(institution).withTitle("C");
        save(program1, program2, program3);
        flushAndClearSession();
        Program returned = programDAO.getLastCustomProgram(institution);
        assertEquals(program2.getCode(), returned.getCode());
    }

    @Test
    public void shouldGetClosingDateById() {
        AdvertClosingDate putClosingDate = new AdvertClosingDateBuilder().closingDate(new LocalDate()).build();
        Program program = TestData.aProgram(programType, institution, user, state).withClosingDates(putClosingDate);
        sessionFactory.getCurrentSession().save(program);
        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        AdvertClosingDate gotClosingDate = programDAO.getClosingDateById(putClosingDate.getId());
        assertEquals(putClosingDate, gotClosingDate);
    }

    @Test
    public void shouldGetClosingDateByDate() {
        LocalDate closingDate = new LocalDate();
        AdvertClosingDate putClosingDate = new AdvertClosingDateBuilder().closingDate(closingDate).build();
        Program program = TestData.aProgram(programType, institution, user, state).withClosingDates(putClosingDate);
        sessionFactory.getCurrentSession().save(program);
        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        AdvertClosingDate gotClosingDate = programDAO.getClosingDateByDate(program, closingDate);
        assertEquals(putClosingDate, gotClosingDate);
    }

    @Test
    public void shouldUpdateClosingDate() {
        DateTime dateToday = new DateTime(new Date());
        LocalDate truncatedDateToday = new LocalDate(dateToday.getYear(), dateToday.getMonthOfYear(), dateToday.getDayOfMonth());
        LocalDate truncatedDateTomorrow = truncatedDateToday.plusDays(1);
        AdvertClosingDate putClosingDate = new AdvertClosingDateBuilder().closingDate(truncatedDateToday).build();
        Program program = TestData.aProgram(programType, institution, user, state).withClosingDates(putClosingDate);
        sessionFactory.getCurrentSession().save(program);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();
        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        AdvertClosingDate gotClosingDate = programDAO.getClosingDateById(putClosingDate.getId());
        gotClosingDate.setClosingDate(truncatedDateTomorrow);
        programDAO.updateClosingDate(gotClosingDate);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();
        assertEquals(gotClosingDate.getClosingDate(), truncatedDateTomorrow);
    }

    @Test
    public void shouldDeleteClosingDate() {
        AdvertClosingDate putClosingDate = new AdvertClosingDateBuilder().closingDate(new LocalDate()).build();
        Integer putClosingDateId = putClosingDate.getId();
        Program program = TestData.aProgram(programType, institution, user, state).withClosingDates(putClosingDate);
        sessionFactory.getCurrentSession().save(program);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();
        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        programDAO.deleteClosingDate(putClosingDate);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();
        AdvertClosingDate gotClosingDate = programDAO.getClosingDateById(putClosingDateId);
        assertEquals(gotClosingDate, null);
    }

    @Test
    public void shouldSaveProjectAndAdvert() {
        programDAO.save(project);
        flushAndClearSession();
        assertNotNull(project.getId());
    }

    @Test
    public void shouldLoadProjectAndAdvert() {
        Project loadedProject = (Project) programDAO.getById(project.getId());
        assertNotNull(loadedProject);
        assertEquals(loadedProject.getId(), project.getId());
    }

    @Test
    public void shouldGetEnabledProjectsForProgram() {
        programDAO.save(project);
        flushAndClearSession();

        List<Project> projects = programDAO.getProjectsForProgram(project.getProgram());
        assertTrue(projects.size() >= 1);
        assertProjectIncluded(true, projects, project);
    }

    @Test
    public void shouldNotGetDisabledProjectsForProgram() {
        project.setState(new State().withId(PrismState.PROJECT_APPROVED));
        programDAO.save(project);
        flushAndClearSession();

        List<Project> projects = programDAO.getProjectsForProgram(project.getProgram());
        assertProjectIncluded(false, projects, project);
    }

    @Test
    public void shouldGetProjectsForProgramAndUser() {
        programDAO.save(project);
        flushAndClearSession();

        List<Project> projects = programDAO.getProjectsForProgramOfWhichAuthor(project.getProgram(), project.getUser());
        assertProjectIncluded(true, projects, project);
    }

    @Test
    public void shouldGetProgramAcceptingApplicationsByCode() {
        Program program = testObjectProvider.getEnabledProgram();
        Program loadedProgram = programDAO.getProgamAcceptingApplicationsByCode(program.getCode());
        assertEquals(loadedProgram.getId(), program.getId());
    }

    @Test
    public void shouldGetProgramAcceptingApplicationsById() {
        Program program = testObjectProvider.getEnabledProgram();
        Advert loadedAdvert = programDAO.getAcceptingApplicationsById(program.getId());
        assertEquals(loadedAdvert.getProgram().getId(), program.getId());
    }

    @Test
    public void shouldGetProjectAcceptingApplicationsById() {
        Advert loadedProject = programDAO.getAcceptingApplicationsById(project.getId());
        assertEquals(loadedProject.getProject().getId(), project.getId());
    }

    private void assertProjectIncluded(final boolean expected, List<Project> projects, Project expectedProject) {
        assertTrue(projects.size() >= (expected ? 1 : 0));
        boolean projectIncluded = false;
        for (Project loadedProject : projects) {
            assertTrue(loadedProject.isEnabled());
            if (expectedProject.getId().equals(loadedProject.getId())) {
                projectIncluded = true;
            }
        }
        assertEquals(expected, projectIncluded);
    }

}
