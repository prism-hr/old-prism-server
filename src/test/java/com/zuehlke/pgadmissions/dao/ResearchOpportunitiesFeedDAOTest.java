package com.zuehlke.pgadmissions.dao;

import junit.framework.Assert;

import org.junit.Test;

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

    private ResearchOpportunitiesFeedDAO dao;

    @Override
    public void setup() {
        super.setup();
        dao = new ResearchOpportunitiesFeedDAO(sessionFactory);
    }

    @Test
    public void shouldPersistAResearchOpportunitiesFeed() {
        RegisteredUser user = new RegisteredUserBuilder().email("fooBarZ@fooBarZ.com").username("fooBarZ@fooBarZ.com").build();
        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").domicileCode("AE").enabled(true).build();
        Program program = new ProgramBuilder().code("XXXXXXXXXXX").title("Program1").institution(institution).build();
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().feedFormat(FeedFormat.LARGE).programs(program).title("Hello Feed").user(user)
                .build();

        save(user, institution, program);
        flushAndClearSession();

        dao.save(feed);
        flushAndClearSession();

        sessionFactory.getCurrentSession().refresh(user);

        ResearchOpportunitiesFeed feedFromDb = dao.getById(feed.getId());
        Assert.assertNotNull(feedFromDb);
        
        for (ResearchOpportunitiesFeed gotFeed : dao.getAllFeedsForUser(user)) {
            Assert.assertEquals(gotFeed.getId(), feed.getId());
        }
    }

    @Test
    public void shouldReturnWhetherATitleIsUniqueForAUser() {
        RegisteredUser user = new RegisteredUserBuilder().email("fooBarZ@fooBarZ.com").username("fooBarZ@fooBarZ.com").build();
        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").domicileCode("AE").enabled(true).build();
        Program program = new ProgramBuilder().code("XXXXXXXXXXX").title("Program1").institution(institution).build();
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().feedFormat(FeedFormat.LARGE).programs(program).title("Hello Feed2").user(user)
                .build();

        save(user, institution, program);
        flushAndClearSession();

        dao.save(feed);
        flushAndClearSession();

        sessionFactory.getCurrentSession().refresh(user);

        Assert.assertFalse(dao.isUniqueFeedTitleForUser("Hello Feed2", user));
    }

    @Test
    public void shouldReturnAllFeedsForAUser() {
        RegisteredUser user = new RegisteredUserBuilder().email("fooBarZ@fooBarZ.com").username("fooBarZ@fooBarZ.com").build();
        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").domicileCode("AE").enabled(true).build();
        Program program = new ProgramBuilder().code("XXXXXXXXXXX").title("Program1").institution(institution).build();
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().feedFormat(FeedFormat.LARGE).programs(program).title("Hello Feed3").user(user)
                .build();

        save(user, institution, program);
        flushAndClearSession();

        dao.save(feed);
        flushAndClearSession();

        for (ResearchOpportunitiesFeed gotFeed : dao.getAllFeedsForUser(user)) {
            Assert.assertEquals(gotFeed.getId(), feed.getId());
        }
        
    }
}
