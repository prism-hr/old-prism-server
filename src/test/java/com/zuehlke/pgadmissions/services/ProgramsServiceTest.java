package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;

public class ProgramsServiceTest {

    private ProgramDAO programDAOMock;
    
    private AdvertDAO advertDAOMock;

    private ProjectDAO projectDAOMock;
    
    private ProgramsService programsService;

    @Test
    public void shouldGetAllPrograms() {
        Program programOne = EasyMock.createMock(Program.class);
        Program programTwo = EasyMock.createMock(Program.class);
        EasyMock.expect(programDAOMock.getAllPrograms()).andReturn(Arrays.asList(programOne, programTwo));
        EasyMock.replay(programOne, programTwo, programDAOMock);

        List<Program> allPrograms = programsService.getAllPrograms();
        assertEquals(2, allPrograms.size());
        assertTrue(allPrograms.contains(programOne));
        assertTrue(allPrograms.contains(programTwo));
    }

    @Test
    public void shouldGetProgramById() {
        Program program = EasyMock.createMock(Program.class);
        program.setId(2);
        EasyMock.expect(programDAOMock.getProgramById(2)).andReturn(program);
        EasyMock.replay(program, programDAOMock);
        assertEquals(program, programsService.getProgramById(2));
    }

    @Test
    public void shouldGetProgramByCode() {
        Program program = EasyMock.createMock(Program.class);
        EasyMock.expect(programDAOMock.getProgramByCode("code")).andReturn(program);
        EasyMock.replay(program, programDAOMock);
        assertEquals(program, programsService.getProgramByCode("code"));
    }

    @Test
    public void shouldDelegateSaveToDAO() {
        Program program = EasyMock.createMock(Program.class);
        programDAOMock.save(program);
        EasyMock.replay(programDAOMock);
        programsService.save(program);
        EasyMock.verify(programDAOMock);
    }

    @Test
    public void shouldApplyScoringDefinition() {
        Program program = new ProgramBuilder().build();

        EasyMock.expect(programDAOMock.getProgramByCode("any_code")).andReturn(program);

        EasyMock.replay(programDAOMock);
        programsService.applyScoringDefinition("any_code", ScoringStage.REFERENCE, "Siala baba mak");
        EasyMock.verify(programDAOMock);

        ScoringDefinition definition = program.getScoringDefinitions().get(ScoringStage.REFERENCE);
        assertNotNull(definition);
        assertEquals("Siala baba mak", definition.getContent());
    }

    @Before
    public void setUp() {
        programDAOMock = EasyMock.createMock(ProgramDAO.class);
        advertDAOMock = EasyMock.createMock(AdvertDAO.class);
        projectDAOMock = EasyMock.createMock(ProjectDAO.class);
        programsService = new ProgramsService(null, programDAOMock, advertDAOMock, projectDAOMock);
    }
}
