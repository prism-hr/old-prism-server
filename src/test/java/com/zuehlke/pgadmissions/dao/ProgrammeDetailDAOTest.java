package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Assert;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.SourcesOfInterestBuilder;

public class ProgrammeDetailDAOTest extends AutomaticRollbackTestCase{
	
    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        ProgrammeDetailDAO programDetailDAO = new ProgrammeDetailDAO();
        programDetailDAO.getProgrammeDetailWithId(1);
    }

    @Test
    public void shouldGetProgrammeDetailById() throws ParseException {
        SourcesOfInterest interest = new SourcesOfInterestBuilder().id(1).name("ZZ").code("ZZ").toSourcesOfInterest();
        ProgrammeDetails programmeDetail = new ProgrammeDetailsBuilder().programmeName("proName")
                .startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2001/02/02")).studyOption("1", "Full-time")
                .sourcesOfInterest(interest).id(1).toProgrammeDetails();
        sessionFactory.getCurrentSession().save(programmeDetail);
        flushAndClearSession();

        ProgrammeDetailDAO programmeDetailDAO = new ProgrammeDetailDAO(sessionFactory);
        assertEquals(programmeDetail, programmeDetailDAO.getProgrammeDetailWithId(programmeDetail.getId()));

    }

    @Test
    public void shouldSaveProgrammeDetail() throws ParseException {
        SourcesOfInterest interest = new SourcesOfInterestBuilder().id(1).name("ZZ").code("ZZ").toSourcesOfInterest();
        ProgrammeDetails programmeDetail = new ProgrammeDetailsBuilder().programmeName("proName")
                .startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2001/02/02")).studyOption("1", "Full-time")
                .sourcesOfInterest(interest).toProgrammeDetails();
        sessionFactory.getCurrentSession().save(programmeDetail);
        flushAndClearSession();

        ProgrammeDetailDAO programmeDetailDAO = new ProgrammeDetailDAO(sessionFactory);
        programmeDetailDAO.save(programmeDetail);
        Assert.assertNotNull(programmeDetail.getId());
    }
}
