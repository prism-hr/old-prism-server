package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;

public class ProgramDAOTest extends AutomaticRollbackTestCase {

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        ProgramDAO programDAO = new ProgramDAO();
        programDAO.getAllPrograms();
    }

    @Test
    public void shouldGetAllPrograms() {
        BigInteger existingNumberOfPrograms = (BigInteger) sessionFactory.getCurrentSession()
                .createSQLQuery("select count(*) from PROGRAM").uniqueResult();
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
    
}
