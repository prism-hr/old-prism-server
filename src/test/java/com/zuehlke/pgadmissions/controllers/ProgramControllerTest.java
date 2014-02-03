package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.MainPageModel;

public class ProgramControllerTest {
	
	MockHttpServletRequest request;
	ProgramController programController;
	ProgramDAO programDAOMock;
	List<Program> programs;
	
	Map<String, MainPageModel> modelMap;
	ModelAndView modelAndView;
	
	@Before
	public void setUp(){
	    programDAOMock = EasyMock.createMock(ProgramDAO.class);
	    programs = new ArrayList<Program>();
	    programController = new ProgramController(programDAOMock);
	}
	
	@Test
	public void getProgramsViewName() {
	    programController.setEnabled(true);
		assertEquals("private/pgStudents/programs/program_list_page", programController.getProgramsPage().getViewName());
	}
	
	@Test
	public void shouldShowAllPrograms() {
	    programController.setEnabled(true);
		EasyMock.expect(programDAOMock.getAllPrograms()).andReturn(programs);
		EasyMock.replay(programDAOMock);
		
		ModelAndView modelAndView = programController.getProgramsPage();
		MainPageModel model = (MainPageModel) modelAndView.getModel().get("model");
		assertNotNull(model);
		assertSame(programs, model.getPrograms());
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void shouldShowNoPrograms() {
	    programController.setEnabled(false);
        EasyMock.expect(programDAOMock.getAllPrograms()).andReturn(programs);
        EasyMock.replay(programDAOMock);
        programController.getProgramsPage();
	}
}
