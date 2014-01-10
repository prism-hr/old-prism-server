package com.zuehlke.pgadmissions.services.importers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;

public class ProgrammesImporterTest {
    
    private ProgrammesImporter importer;
    
    private ProgramDAO programDAO;
    
    private ProgramInstanceDAO programInstanceDAO;
    
    private ImportService importService;
    
    @Before
    public void setUp() throws MalformedURLException, JAXBException {
        programDAO = EasyMock.createMock(ProgramDAO.class);
        programInstanceDAO = EasyMock.createMock(ProgramInstanceDAO.class);
        importService = EasyMock.createMock(ImportService.class);
        URL xmlFileLocation = new File("src/test/resources/reference_data/programme_feed_atas.xml").toURI().toURL();
        importer = new ProgrammesImporter(programInstanceDAO, programDAO, importService, xmlFileLocation, "user", "password");
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testImportData() throws XMLDataImportException {
        List<ProgramInstance> currentData = new ArrayList<ProgramInstance>();
        ProgramBuilder pb = new ProgramBuilder();
        Program p1 = pb.code("DDNBENSING09").title("A").atasRequired(false).id(1).build();
        Program p2 = pb.code("DDNCIVSUSR09").title("B").atasRequired(true).id(2).build();
        Date deadline = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        Date startDate = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        currentData.add(new ProgramInstanceBuilder().id(1).academicYear("1").identifier("0001").program(p1).applicationDeadline(deadline).applicationStartDate(startDate).studyOption("1", "option").enabled(true).build());
        currentData.add(new ProgramInstanceBuilder().id(2).academicYear("1").identifier("0002").program(p2).applicationDeadline(deadline).applicationStartDate(startDate).studyOption("2", "option").enabled(true).build());
        
        ProgramInstance testProgramInstance = new ProgramInstance();
        
        EasyMock.expect(programInstanceDAO.getAllProgramInstances()).andReturn(currentData);
        EasyMock.expect(programInstanceDAO.getById(192)).andReturn(testProgramInstance);
        
        List<ProgramInstance> changes = currentData.subList(0, 1);
        EasyMock.expect(importService.merge(EasyMock.same(currentData), EasyMock.anyObject(List.class))).andReturn(changes);
        programInstanceDAO.save(EasyMock.same(changes.get(0)));

        EasyMock.expect(programDAO.getProgramByCode("DDNBENSING09")).andReturn(p1);
        EasyMock.expect(programDAO.getProgramByCode("DDNCIVSUSR09")).andReturn(p2);
        
        programDAO.save(p1);
        programDAO.save(p2);
        
        EasyMock.replay(programInstanceDAO, programDAO, importService);
        
        importer.importData();

        EasyMock.verify(programInstanceDAO, programDAO, importService);
        
        Assert.assertTrue("The ATAS flag should have been updated to be true", p1.getAtasRequired());
        Assert.assertFalse("The ATAS flag should have been updated to be false", p2.getAtasRequired());
    }
}
