package com.zuehlke.pgadmissions.dao;

import junit.framework.Assert;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.ResearchOpportunitiesFeed;
import com.zuehlke.pgadmissions.domain.builders.ResearchOpportunitiesFeedBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.FeedFormat;

public class ResearchOpportunitiesFeedDAOTest extends AutomaticRollbackTestCase {

    private ResearchOpportunitiesFeedDAO dao;

    @Override
    public void setup() {
        super.setup();
        dao = new ResearchOpportunitiesFeedDAO(sessionFactory);
    }

    @Test
    public void shouldPersistAResearchOpportunitiesFeed() {
        User user = testObjectProvider.getEnabledUserInRole(Authority.SYSTEM_ADMINISTRATOR);
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().feedFormat(FeedFormat.LARGE).programs(testObjectProvider.getEnabledProgram())
                .title("Hello Feed").user(user).build();
        dao.save(feed);
        sessionFactory.getCurrentSession().refresh(user);
        ResearchOpportunitiesFeed feedFromDb = dao.getById(feed.getId());
        Assert.assertNotNull(feedFromDb);
        for (ResearchOpportunitiesFeed gotFeed : dao.getAllFeedsForUser(user)) {
            Assert.assertEquals(gotFeed.getId(), feed.getId());
        }
    }

    @Test
    public void shouldReturnWhetherATitleIsUniqueForAUser() {
        User user = testObjectProvider.getEnabledUserInRole(Authority.SYSTEM_ADMINISTRATOR);
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().feedFormat(FeedFormat.LARGE).programs(testObjectProvider.getEnabledProgram())
                .title("Hello Feed2").user(user).build();
        dao.save(feed);
        sessionFactory.getCurrentSession().refresh(user);
        Assert.assertFalse(dao.isUniqueFeedTitleForUser("Hello Feed2", user));
    }

    @Test
    public void shouldReturnAllFeedsForAUser() {
        User user = testObjectProvider.getEnabledUserInRole(Authority.SYSTEM_ADMINISTRATOR);
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().feedFormat(FeedFormat.LARGE).programs(testObjectProvider.getEnabledProgram())
                .title("Hello Feed3").user(user).build();
        dao.save(feed);
        for (ResearchOpportunitiesFeed gotFeed : dao.getAllFeedsForUser(user)) {
            Assert.assertEquals(gotFeed.getId(), feed.getId());
        }

    }
    
}
