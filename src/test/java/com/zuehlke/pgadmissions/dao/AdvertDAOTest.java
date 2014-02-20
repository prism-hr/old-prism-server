package com.zuehlke.pgadmissions.dao;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.AdvertBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.OpportunityListType;
import com.zuehlke.pgadmissions.domain.enums.ProgramTypeId;
import com.zuehlke.pgadmissions.dto.AdvertDTO;

public class AdvertDAOTest extends AutomaticRollbackTestCase {

    private AdvertDAO advertDAO;
    private ProgramDAO programDAO;
    private QualificationInstitution institution;
    
    @Override
    public void setup() {
        super.setup();
        advertDAO = new AdvertDAO(sessionFactory);
        programDAO = new ProgramDAO(sessionFactory);
        institution = (QualificationInstitution) sessionFactory.getCurrentSession().get(QualificationInstitution.class, 3800);
        save(institution);
    }

    @Test
    public void shouldGetActiveAdverts() {
        ProgramType testProgramType = programDAO.getProgramTypeById(ProgramTypeId.RESEARCH_DEGREE);  
        RegisteredUser advertContact = new RegisteredUserBuilder().username("advertcontact").build();
        
        Program programWithInactiveProgramAdvert = new ProgramBuilder().code("inactive").institution(institution).programType(testProgramType)
                .advert(new AdvertBuilder().title("inactive program").description("inactive program").studyDuration(9).active(false).enabled(true)
                .contactUser(advertContact).build()).build();
        
        Program programWithActiveProgramAdvert = new ProgramBuilder().code("active").institution(institution).programType(testProgramType)
                .advert(new AdvertBuilder().title("active program").description("active program").studyDuration(66).active(true).enabled(true)
                .contactUser(advertContact).build()).build();

        save(programWithInactiveProgramAdvert, programWithActiveProgramAdvert);
        flushAndClearSession();

        List<AdvertDTO> activeAdverts = advertDAO.getAdvertFeed(null, null, 0);
        assertThat(activeAdverts.size(), greaterThanOrEqualTo(1));
        assertTrue(advertInList(programWithActiveProgramAdvert, activeAdverts));
    }

    /**
     * Test uses a real user with real applications as the test seed for the recommender algorithm
     * The test will fail
     */
    @Test
    public void shouldGetRecommendedAdverts() {
        String testUserId = new Integer(4157).toString();
        List<AdvertDTO> gotAdverts = advertDAO.getAdvertFeed(OpportunityListType.RECOMMENDEDOPPORTUNTIIESBYAPPLICANTID, testUserId, null);
        assertTrue(gotAdverts.size() > 0);
        String testUserWithNoApplicationsId = new Integer(15).toString();
        gotAdverts = advertDAO.getAdvertFeed(OpportunityListType.RECOMMENDEDOPPORTUNTIIESBYAPPLICANTID, testUserWithNoApplicationsId, null);
        assertTrue(gotAdverts.size() == 0);
    }

    private boolean advertInList(Program program, List<AdvertDTO> loadedAdverts) {
        for (AdvertDTO loadedAdvert : loadedAdverts) {
            if (loadedAdvert.getId().equals(program.getId())) {
                assertThat(loadedAdvert.getId(), equalTo(program.getId()));
                assertThat(loadedAdvert.getDescription(), equalTo(program.getDescription()));
                assertThat(loadedAdvert.getStudyDuration(), equalTo(program.getStudyDuration()));
                assertThat(loadedAdvert.getId(), equalTo(program.getId()));
                return true;
            }
        }
        return false;
    }

}
