package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ModelMap;

import com.zuehlke.pgadmissions.controllers.prospectus.AdvertsController;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ApplicationFormService;

public class AdvertsControllerTest {

    private AdvertsController controller;
    private AdvertService advertServiceMock;
    private ApplicationFormService applicationsServiceMock;
    private ProgramDAO programDAOMock;

    @Before
    public void setUp() {
        advertServiceMock = EasyMock.createMock(AdvertService.class);
        applicationsServiceMock = EasyMock.createMock(ApplicationFormService.class);
        programDAOMock = EasyMock.createMock(ProgramDAO.class);
        controller = new AdvertsController(advertServiceMock, applicationsServiceMock, programDAOMock);
    }

    @Test
    public void shouldOpenNewTabForStandaloneAdvert() {
        ModelMap modelMap = new ModelMap();
        controller.getStandaloneOpportunities(null, null, modelMap);

        assertEquals(1, modelMap.size());
        assertTrue(modelMap.containsAttribute("shouldOpenNewTab"));

    }

}
