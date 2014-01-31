package com.zuehlke.pgadmissions.dao;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ResearchOpportunitiesFeed;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ResearchOpportunitiesFeedBuilder;
import com.zuehlke.pgadmissions.domain.enums.FeedFormat;

public class ResearchOpportunitiesFeedDAOTest extends AutomaticRollbackTestCase {

    private Logger log = LoggerFactory.getLogger(ResearchOpportunitiesFeedDAOTest.class);

    private ResearchOpportunitiesFeedDAO dao;

    @Override
    public void setup() {
        super.setup();
        dao = new ResearchOpportunitiesFeedDAO(sessionFactory);
    }

    @Test
    public void shouldPersistAResearchOpportunitiesFeed() {
        RegisteredUser user = new RegisteredUserBuilder().email("fooBarZ1@fooBarZ.com").username("fooBarZ1@fooBarZ.com").build();
        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").countryCode("AE").enabled(true).build();
        Program program = new ProgramBuilder().code("XXXXXXXXXXX").title("Program1").institution(institution).build();
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().feedFormat(FeedFormat.LARGE).programs(program).title("Hello Feed").user(user)
                .build();

        save(user, institution, program);
        flushAndClearSession();

        dao.save(feed);
        flushAndClearSession();

        sessionFactory.getCurrentSession().refresh(user);

        log.trace("UserId:" + user.getId());
        log.trace("ProgramId:" + program.getId());
        log.trace("ResearchOpportunitiesFeedId:" + feed.getId());

        ResearchOpportunitiesFeed feedFromDb = dao.getById(feed.getId());
        Assert.assertNotNull(feedFromDb);
        Assert.assertEquals(feed.getTitle(), feedFromDb.getTitle());
        Assert.assertEquals(1, user.getResearchOpportunitiesFeeds().size());
        Assert.assertEquals(feed.getTitle(), user.getResearchOpportunitiesFeeds().get(0).getTitle());
    }

    @Test
    public void shouldReturnWhetherATitleIsUniqueForAUser() {
        RegisteredUser user = new RegisteredUserBuilder().email("fooBarZ2@fooBarZ.com").username("fooBarZ2@fooBarZ.com").build();
        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").countryCode("AE").enabled(true).build();
        Program program = new ProgramBuilder().code("XXXXXXXXXXX").title("Program1").institution(institution).build();
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().feedFormat(FeedFormat.LARGE).programs(program).title("Hello Feed").user(user)
                .build();

        save(user, institution, program);
        flushAndClearSession();

        dao.save(feed);
        flushAndClearSession();

        sessionFactory.getCurrentSession().refresh(user);

        Assert.assertFalse(dao.isUniqueFeedTitleForUser("Hello Feed", user));
    }

    @Test
    public void shouldReturnAllFeedsForAUser() {
        RegisteredUser user = new RegisteredUserBuilder().email("fooBarZ2@fooBarZ.com").username("fooBarZ2@fooBarZ.com").build();
        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").countryCode("AE").enabled(true).build();
        Program program = new ProgramBuilder().code("XXXXXXXXXXX").title("Program1").institution(institution).build();
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().feedFormat(FeedFormat.LARGE).programs(program).title("Hello Feed").user(user)
                .build();

        save(user, institution, program);
        flushAndClearSession();

        dao.save(feed);
        flushAndClearSession();

        List<ResearchOpportunitiesFeed> allFeedsForUser = dao.getAllFeedsForUser(user);

        Assert.assertEquals(1, allFeedsForUser.size());
        Assert.assertEquals(feed.getId(), allFeedsForUser.get(0).getId());
    }
}
