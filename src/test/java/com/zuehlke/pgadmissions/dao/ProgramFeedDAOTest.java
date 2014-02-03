package com.zuehlke.pgadmissions.dao;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ProgramFeed;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.builders.ProgramFeedBuilder;

public class ProgramFeedDAOTest extends AutomaticRollbackTestCase {

    @Test
    public void shouldGetAllProgramFeeds() {
        BigInteger existingNumberOfProgramFeeds = (BigInteger) sessionFactory.getCurrentSession().createSQLQuery("select count(*) from PROGRAM_FEED").uniqueResult();

        QualificationInstitution institution = (QualificationInstitution) sessionFactory.getCurrentSession().createCriteria(QualificationInstitution.class).setMaxResults(1).uniqueResult();
        
        ProgramFeed programFeed1 = new ProgramFeedBuilder().feedUrl("url").institution(institution).build();
        ProgramFeed programFeed2 = new ProgramFeedBuilder().feedUrl("url2").institution(institution).build();
        
        sessionFactory.getCurrentSession().save(programFeed1);
        sessionFactory.getCurrentSession().save(programFeed2);
        flushAndClearSession();
        
        ProgramFeedDAO programFeedDAO = new ProgramFeedDAO(sessionFactory);
        Assert.assertEquals(existingNumberOfProgramFeeds.intValue() + 2, programFeedDAO.getAllProgramFeeds().size());
    }

}
