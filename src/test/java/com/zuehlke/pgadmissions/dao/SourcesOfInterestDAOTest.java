package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.List;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.builders.SourcesOfInterestBuilder;

public class SourcesOfInterestDAOTest extends AutomaticRollbackTestCase {

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        SourcesOfInterestDAO sourcesOfInterestDAO = new SourcesOfInterestDAO();
        SourcesOfInterest sourcesOfInterest = new SourcesOfInterestBuilder().id(1).name("ZZZZZZ").code("ZZ").enabled(true).build();
        sourcesOfInterestDAO.getSourcesOfInterestById(sourcesOfInterest.getId());
    }

    @Test
    public void shouldGetAllSourcesOfInterestInNameOrder() {
        BigInteger numberOfEthnicities = (BigInteger) sessionFactory.getCurrentSession().createSQLQuery("select count(*) from SOURCES_OF_INTEREST").uniqueResult();
        SourcesOfInterest sourcesOfInterest1 = new SourcesOfInterestBuilder().name("ZZZZZZ").code("ZZ").enabled(true).build();
        SourcesOfInterest sourcesOfInterest2 = new SourcesOfInterestBuilder().name("AAAAAAAA").code("AA").enabled(true).build();
        save(sourcesOfInterest1, sourcesOfInterest2);
        flushAndClearSession();
        SourcesOfInterestDAO sourcesOfInterestDAO = new SourcesOfInterestDAO(sessionFactory);
        List<SourcesOfInterest> allSourcesOfInterest = sourcesOfInterestDAO.getAllSourcesOfInterest();
        
        assertEquals(numberOfEthnicities.intValue() + 2, allSourcesOfInterest.size());
        assertEquals("AAAAAAAA", allSourcesOfInterest.get(0).getName());
        assertEquals("ZZZZZZ", allSourcesOfInterest.get(numberOfEthnicities.intValue() + 1).getName());
    }

    @Test
    public void shouldGetSourcesOfInterestById() {
        SourcesOfInterest sourcesOfInterest1 = new SourcesOfInterestBuilder().name("ZZZZZZ").code("ZZ").enabled(true).build();
        SourcesOfInterest sourcesOfInterest2 = new SourcesOfInterestBuilder().name("mmmmmm").code("mm").enabled(true).build();

        save(sourcesOfInterest1, sourcesOfInterest2);
        flushAndClearSession();
        Integer id = sourcesOfInterest1.getId();
        SourcesOfInterestDAO sourcesOfInterestDAO = new SourcesOfInterestDAO(sessionFactory);
        SourcesOfInterest reloadedSourcesOfInterest = sourcesOfInterestDAO.getSourcesOfInterestById(id);
        assertEquals(sourcesOfInterest1.getId(), reloadedSourcesOfInterest.getId());
    }
    
    @Test
    public void shouldGetAllEnabledSourcesOfInterest() {
        BigInteger numberOfSourcesOfInterest = (BigInteger) sessionFactory.getCurrentSession().createSQLQuery("select count(*) from SOURCES_OF_INTEREST WHERE enabled = true").uniqueResult();
        SourcesOfInterest sourcesOfInterest1 = new SourcesOfInterestBuilder().name("ZZZZZZ").code("ZZ").enabled(false).build();
        SourcesOfInterest sourcesOfInterest2 = new SourcesOfInterestBuilder().name("AAAAAAAA").code("AA").enabled(true).build();
        save(sourcesOfInterest1, sourcesOfInterest2);
        flushAndClearSession();
        SourcesOfInterestDAO sourcesOfInterestDAO = new SourcesOfInterestDAO(sessionFactory);
        List<SourcesOfInterest> allEnabledSourcesOfInterest = sourcesOfInterestDAO.getAllEnabledSourcesOfInterest();
        assertEquals(numberOfSourcesOfInterest.intValue() + 1, allEnabledSourcesOfInterest.size());
        for (SourcesOfInterest sInterest : allEnabledSourcesOfInterest) {
            assertTrue("SourcesOfInterest is disabled but has been loaded anyway.", sInterest.getEnabled());
        }
    }
}