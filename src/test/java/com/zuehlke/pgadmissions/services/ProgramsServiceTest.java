package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.AdvertDAO;
import com.zuehlke.pgadmissions.dao.BadgeDAO;
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
    
    private BadgeDAO badgeDAOMock;

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
    
    @Test
    public void shouldReturnClosingDatesMap() {
        Program program1 = new ProgramBuilder().code("p1").id(1).build();
        Program program2 = new ProgramBuilder().code("p2").id(2).build();
        expect(programDAOMock.getAllPrograms()).andReturn(Arrays.asList(program1, program2));
        
        Capture<Date> dateCaptor = new Capture<Date>();
        expect(badgeDAOMock.getNextClosingDateForProgram(eq(program1), EasyMock.capture(dateCaptor)))
            .andReturn(new DateTime(2013, 2, 15, 00, 15).toDate());
        expect(badgeDAOMock.getNextClosingDateForProgram(eq(program2), EasyMock.capture(dateCaptor)))
            .andReturn(new DateTime(2013, 2, 13, 13, 15).toDate());
        
        replay(programDAOMock, badgeDAOMock);
        Map<String, String> result = programsService.getDefaultClosingDates();
        verify(programDAOMock, badgeDAOMock);
        
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("15 Feb 2013", result.get("p1"));
        Assert.assertEquals("13 Feb 2013", result.get("p2"));
    }

    @Before
    public void setUp() {
        programDAOMock = EasyMock.createMock(ProgramDAO.class);
        advertDAOMock = EasyMock.createMock(AdvertDAO.class);
        projectDAOMock = EasyMock.createMock(ProjectDAO.class);
        badgeDAOMock = EasyMock.createMock(BadgeDAO.class);
        programsService = new ProgramsService(programDAOMock, advertDAOMock, projectDAOMock, badgeDAOMock);
    }
}
