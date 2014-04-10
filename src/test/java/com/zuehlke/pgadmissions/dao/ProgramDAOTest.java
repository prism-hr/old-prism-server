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
import org.junit.Assert;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramClosingDateBuilder;
import com.zuehlke.pgadmissions.domain.enums.AdvertState;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;

public class ProgramDAOTest extends AutomaticRollbackTestCase {

    private ProgramDAO programDAO;
    private Institution institution;
    private Project project;

    @Override
    public void setup() {
        super.setup();
        programDAO = new ProgramDAO(sessionFactory);
        institution = testObjectProvider.getEnabledInstitution();
        flushAndClearSession();
        project = testObjectProvider.getEnabledProject();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGetAllPrograms() {
        List<Program> programs = (List<Program>) sessionFactory.getCurrentSession().createCriteria(Program.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .add(Restrictions.eq("state", AdvertState.PROGRAM_APPROVED))
                .addOrder(Order.asc("title")).list();
        Program program1 = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).id(1).code("code1").title("another title").institution(institution).build();
        Program program2 = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).id(1).code("code2").title("another title").institution(institution).build();
        sessionFactory.getCurrentSession().save(program1);
        sessionFactory.getCurrentSession().save(program2);
        flushAndClearSession();
        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        Assert.assertEquals(programs.size() + 2, programDAO.getAllEnabledPrograms().size());
    }

    @Test
    public void shouldGetProgramById() {
        Program program = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).id(1).code("code1").title("another title").institution(institution).build();
        sessionFactory.getCurrentSession().save(program);
        flushAndClearSession();
        assertEquals(program.getId(), programDAO.getById(program.getId()).getId());
    }

    @Test
    public void shouldGetProgramByCode() {
        Program program = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).id(1).code("code1").title("another title").institution(institution).build();
        sessionFactory.getCurrentSession().save(program);
        flushAndClearSession();
        assertEquals(program.getId(), programDAO.getProgramByCode("code1").getId());
    }

    @Test
    public void shouldSaveProgram() {
        Program program = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).code("code1").title("another title").institution(institution).build();
        programDAO.save(program);
        Assert.assertNotNull(program.getId());
    }

    @Test
    public void shouldGetProgramWithScoringDefinitions() {
        Program program = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).code("code1").title("another title").institution(institution).build();
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
        DateTime closingDates = new DateTime();
        ProgramClosingDate badge1 = new ProgramClosingDateBuilder().closingDate(closingDates.minusMonths(1).toDate()).build();
        ProgramClosingDate badge2 = new ProgramClosingDateBuilder().closingDate(closingDates.plusMonths(1).toDate()).build();
        ProgramClosingDate badge3 = new ProgramClosingDateBuilder().closingDate(closingDates.plusMonths(2).toDate()).build();
        Program program = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).code("code2").institution(institution).closingDates(badge1, badge2, badge3).build();
        badge1.setProgram(program);
        badge2.setProgram(program);
        badge3.setProgram(program);
        save(program, badge1, badge2, badge3);
        flushAndClearSession();
        Date result = programDAO.getNextClosingDate(program);
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.compareTo(badge2.getClosingDate()));
    }

    @Test
    public void shouldGetLastCustomProgram() {
        Program program1 = ProgramBuilder.aProgram(institution).contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).code(institution.getCode() + "_00006").build();
        Program program2 = ProgramBuilder.aProgram(institution).contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).code(institution.getCode() + "_00008").build();
        Program program3 = ProgramBuilder.aProgram(institution).contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).code(institution.getCode() + "_00007").build();
        save(program1, program2, program3);
        flushAndClearSession();
        Program returned = programDAO.getLastCustomProgram(institution);
        assertEquals(program2.getCode(), returned.getCode());
    }

    @Test
    public void shouldGetClosingDateById() {
        ProgramClosingDate putClosingDate = new ProgramClosingDateBuilder().closingDate(new Date()).build();
        Program program = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).code("code").institution(institution).closingDates(putClosingDate).build();
        sessionFactory.getCurrentSession().save(program);
        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        ProgramClosingDate gotClosingDate = programDAO.getClosingDateById(putClosingDate.getId());
        assertEquals(putClosingDate, gotClosingDate);
    }

    @Test
    public void shouldGetClosingDateByDate() {
        Date closingDate = new Date();
        ProgramClosingDate putClosingDate = new ProgramClosingDateBuilder().closingDate(closingDate).build();
        Program program = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).code("code").institution(institution).closingDates(putClosingDate).build();
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
        Program program = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).code("code").institution(institution).closingDates(putClosingDate).build();
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
        Program program = new ProgramBuilder().contactUser(testObjectProvider.getEnabledUserInRole(Authority.SUPERADMINISTRATOR)).code("code").institution(institution).closingDates(putClosingDate).build();
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
        project.setState(AdvertState.PROJECT_DISABLED);
        programDAO.save(project);
        flushAndClearSession();

        List<Project> projects = programDAO.getProjectsForProgram(project.getProgram());
        assertProjectIncluded(false, projects, project);
    }

    @Test
    public void shouldGetProjectsForProgramAndAuthor() {
        programDAO.save(project);
        flushAndClearSession();

        List<Project> projects = programDAO.getProjectsForProgramOfWhichAuthor(project.getProgram(), project.getContactUser());
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
