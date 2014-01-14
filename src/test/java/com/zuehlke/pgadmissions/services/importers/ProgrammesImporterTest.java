package com.zuehlke.pgadmissions.services.importers;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertSame;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.ProgramFeedDAO;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramFeed;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramFeedBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;

public class ProgrammesImporterTest {

    private ProgrammesImporter importer;

    private ApplicationContext applicationContext;
    
    private ProgramDAO programDAO;

    private ProgramInstanceDAO programInstanceDAO;

    private ProgramFeedDAO programFeedDAO;

    private ImportService importService;

    @Before
    public void setUp() throws MalformedURLException, JAXBException {
        applicationContext = EasyMock.createMock(ApplicationContext.class);
        programDAO = EasyMock.createMock(ProgramDAO.class);
        programInstanceDAO = EasyMock.createMock(ProgramInstanceDAO.class);
        programFeedDAO = EasyMock.createMock(ProgramFeedDAO.class);
        importService = EasyMock.createMock(ImportService.class);
        importer = new ProgrammesImporter(applicationContext, programInstanceDAO, programDAO, programFeedDAO, importService, "user", "password");
    }
    
    @Test
    public void shouldImportPrograms() throws Exception{
        IProgrammesImporter importerMock = EasyMock.createMock(IProgrammesImporter.class);
        
        ProgramFeed programFeed1 = new ProgramFeed();
        ProgramFeed programFeed2 = new ProgramFeed();
        
        expect(applicationContext.getBean(IProgrammesImporter.class)).andReturn(importerMock);
        expect(importerMock.getProgramFeeds()).andReturn(Lists.newArrayList(programFeed1, programFeed2));
        importerMock.importData(programFeed1);
        importerMock.importData(programFeed2);
        
        replay(applicationContext, importerMock);
        importer.importData();
        verify(applicationContext, importerMock);
    }
    
    @Test
    public void shouldGetProgramFeeds(){
        List<ProgramFeed> feeds = Collections.emptyList();
        expect(programFeedDAO.getAllProgramFeeds()).andReturn(feeds);
        
        replay(programFeedDAO);
        List<ProgramFeed> returnedFeeds = importer.getProgramFeeds();
        verify(programFeedDAO);
        assertSame(feeds, returnedFeeds);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldImportProgramsForProgramFeed() throws Exception {
        List<ProgramInstance> currentData = new ArrayList<ProgramInstance>();
        URL xmlFileLocation = new File("src/test/resources/reference_data/programme_feed_atas.xml").toURI().toURL();
        ProgramFeed programFeed = new ProgramFeedBuilder().feedUrl(xmlFileLocation.toExternalForm()).build();
        Program p1 = new ProgramBuilder().programFeed(programFeed).code("DDNBENSING09").title("A").atasRequired(false).id(1).build();
        Program p2 = new ProgramBuilder().programFeed(programFeed).code("DDNCIVSUSR09").title("B").atasRequired(true).id(2).build();
        Date deadline = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        Date startDate = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        currentData.add(new ProgramInstanceBuilder().id(1).academicYear("1").identifier("0001").program(p1).applicationDeadline(deadline)
                .applicationStartDate(startDate).studyOption("1", "option").enabled(true).build());
        currentData.add(new ProgramInstanceBuilder().id(2).academicYear("1").identifier("0002").program(p2).applicationDeadline(deadline)
                .applicationStartDate(startDate).studyOption("2", "option").enabled(true).build());


        EasyMock.expect(programInstanceDAO.getAllProgramInstances(programFeed)).andReturn(currentData);

        List<ProgramInstance> changes = currentData.subList(0, 1);
        EasyMock.expect(importService.merge(EasyMock.same(currentData), EasyMock.anyObject(List.class))).andReturn(changes);
        programInstanceDAO.save(EasyMock.same(changes.get(0)));

        EasyMock.expect(programDAO.getProgramByCode("DDNBENSING09")).andReturn(p1);
        EasyMock.expect(programDAO.getProgramByCode("DDNCIVSUSR09")).andReturn(p2);

        programDAO.save(p1);
        programDAO.save(p2);

        EasyMock.replay(programInstanceDAO, programDAO, importService);

        importer.importData(programFeed);

        EasyMock.verify(programInstanceDAO, programDAO, importService);

        assertSame(programFeed, p2.getProgramFeed());
        Assert.assertTrue("The ATAS flag should have been updated to be true", p1.getAtasRequired());
        Assert.assertFalse("The ATAS flag should have been updated to be false", p2.getAtasRequired());
    }
}
