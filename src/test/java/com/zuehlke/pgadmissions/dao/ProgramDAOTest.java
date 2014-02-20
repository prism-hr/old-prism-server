package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramClosingDateBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;

public class ProgramDAOTest extends AutomaticRollbackTestCase {

    private QualificationInstitution institution;

    @Override
    public void setup() {
        super.setup();
        institution = new QualificationInstitutionBuilder().code("code").name("a").domicileCode("AE").enabled(true).build();
        save(institution);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        ProgramDAO programDAO = new ProgramDAO();
        programDAO.getAllPrograms();
    }

    @Test
    public void shouldGetAllPrograms() {
        BigInteger existingNumberOfPrograms = (BigInteger) sessionFactory.getCurrentSession().createSQLQuery("select count(*) from PROGRAM").uniqueResult();
        Program program1 = new ProgramBuilder().id(1).code("code1").title("another title").institution(institution).build();
        Program program2 = new ProgramBuilder().id(1).code("code2").title("another title").institution(institution).build();
        sessionFactory.getCurrentSession().save(program1);
        sessionFactory.getCurrentSession().save(program2);
        flushAndClearSession();
        ProgramDAO programDAO = new ProgramDAO(sessionFactory);
        Assert.assertEquals(existingNumberOfPrograms.intValue() + 2, programDAO.getAllPrograms().size());
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
        Program program1 = ProgramBuilder.aProgram(institution).code(institution.getCode() + "_00006").build();
        Program program2 = ProgramBuilder.aProgram(institution).code(institution.getCode() + "_00008").build();
        Program program3 = ProgramBuilder.aProgram(institution).code(institution.getCode() + "_00007").build();

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
    
}
