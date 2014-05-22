package com.zuehlke.pgadmissions.dao;

import junit.framework.Assert;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ProgramExport;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.ProgramExportFormat;

public class ProgramExportDAOTest extends AutomaticRollbackTestCase {

    private ProgramExportDAO dao;

    @Override
    public void setup() {
        super.setup();
        dao = new ProgramExportDAO(sessionFactory);
    }

    @Test
    public void shouldPersistAResearchOpportunitiesFeed() {
        User user = testObjectProvider.getEnabledUserInRole(Authority.SYSTEM_ADMINISTRATOR);
        ProgramExport feed = new ProgramExport().withFormat(ProgramExportFormat.LARGE).withPrograms(testObjectProvider.getEnabledProgram())
                .withTitle("Hello Feed2").withUser(user);
        dao.save(feed);
        sessionFactory.getCurrentSession().refresh(user);
        ProgramExport feedFromDb = dao.getById(feed.getId());
        Assert.assertNotNull(feedFromDb);
        for (ProgramExport gotFeed : dao.getAllFeedsForUser(user)) {
            Assert.assertEquals(gotFeed.getId(), feed.getId());
        }
    }

    @Test
    public void shouldReturnWhetherATitleIsUniqueForAUser() {
        User user = testObjectProvider.getEnabledUserInRole(Authority.SYSTEM_ADMINISTRATOR);
        ProgramExport feed = new ProgramExport().withFormat(ProgramExportFormat.LARGE).withPrograms(testObjectProvider.getEnabledProgram())
                .withTitle("Hello Feed2").withUser(user);
        dao.save(feed);
        sessionFactory.getCurrentSession().refresh(user);
        Assert.assertFalse(dao.isUniqueFeedTitleForUser("Hello Feed2", user));
    }

    @Test
    public void shouldReturnAllFeedsForAUser() {
        User user = testObjectProvider.getEnabledUserInRole(Authority.SYSTEM_ADMINISTRATOR);
        ProgramExport feed = new ProgramExport().withFormat(ProgramExportFormat.LARGE).withPrograms(testObjectProvider.getEnabledProgram())
                .withTitle("Hello Feed2").withUser(user);
        dao.save(feed);
        for (ProgramExport gotFeed : dao.getAllFeedsForUser(user)) {
            Assert.assertEquals(gotFeed.getId(), feed.getId());
        }

    }
    
}
