package com.zuehlke.pgadmissions.dao;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.builders.AdvertBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;

public class AdvertDAOTest extends AutomaticRollbackTestCase {

    private AdvertDAO advertDAO;

    @Before
    public void setUp() {
        advertDAO = new AdvertDAO(sessionFactory);
    }

    @Test
    public void shouldGetActiveAdverts() {
        Program programWithInactiveProgramAdvert = new ProgramBuilder().code("inactive").title("another title").build();
        Advert inactiveProgramAdvert = new AdvertBuilder().description("inactive program").studyDuration(9).active(false).build();

        Program programWithActiveProgramAdvert = new ProgramBuilder().code("program").title("another title").build();
        Advert programAdvert = new AdvertBuilder().description("program").studyDuration(66).build();

        save(programWithInactiveProgramAdvert, programWithActiveProgramAdvert);
        save(inactiveProgramAdvert, programAdvert);
        flushAndClearSession();

        List<Advert> activeAdverts = advertDAO.getActiveProgramAdverts();
        assertThat(activeAdverts.size(), greaterThanOrEqualTo(1));
        assertTrue(advertInList(programAdvert, activeAdverts));
    }

    private boolean advertInList(Advert programAdvert, List<Advert> activeAdverts) {
        for (Advert loadedAdvert : activeAdverts) {
            if (loadedAdvert.getId().equals(programAdvert.getId())) {
                assertThat(loadedAdvert.getId(), equalTo(programAdvert.getId()));
                assertThat(loadedAdvert.getDescription(), equalTo(programAdvert.getDescription()));
                assertThat(loadedAdvert.getStudyDuration(), equalTo(programAdvert.getStudyDuration()));
                assertThat(loadedAdvert.getId(), equalTo(programAdvert.getId()));
                return true;
            }
        }
        return false;
    }

}
