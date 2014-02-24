package com.zuehlke.pgadmissions.dao;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.AdvertBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;

public class AdvertDAOTest extends AutomaticRollbackTestCase {

    private AdvertDAO advertDAO;
    private ReminderIntervalDAO reminderIntervalDAO;
    private NotificationsDurationDAO notificationsDurationDAO;
    private UserDAO userDAO;
    private QualificationInstitution institution;
    
    @Override
    public void setup() {
        super.setup();
        advertDAO = new AdvertDAO(sessionFactory);
        reminderIntervalDAO = new ReminderIntervalDAO(sessionFactory);
        notificationsDurationDAO = new NotificationsDurationDAO(sessionFactory);
        userDAO = new UserDAO(sessionFactory, reminderIntervalDAO, notificationsDurationDAO);
        institution = (QualificationInstitution) sessionFactory.getCurrentSession().get(QualificationInstitution.class, 3800);
        save(institution);
    }

    @Test
    public void shouldGetActiveAdverts() {
        Program programWithInactiveProgramAdvert = new ProgramBuilder().code("inactive").title("another title").institution(institution).build();
        Advert inactiveProgramAdvert = new AdvertBuilder().description("inactive program").studyDuration(9).active(false).build();

        Program programWithActiveProgramAdvert = new ProgramBuilder().code("program").title("another title2").institution(institution).build();
        Advert programAdvert = new AdvertBuilder().description("program").studyDuration(66).build();

        save(programWithInactiveProgramAdvert, programWithActiveProgramAdvert);
        save(inactiveProgramAdvert, programAdvert);
        flushAndClearSession();

        List<Advert> activeAdverts = advertDAO.getActiveAdverts();
        assertThat(activeAdverts.size(), greaterThanOrEqualTo(1));
        assertTrue(advertInList(programAdvert, activeAdverts));
    }

    /**
     * Test uses a real user with real applications as the test seed for the recommender algorithm
     * The test will fail
     */
    @Test
    public void shouldGetRecommendedAdverts() {
        RegisteredUser testUser = userDAO.get(4157);
        List<Advert> gotAdverts = advertDAO.getRecommendedAdverts(testUser);
        assertTrue(gotAdverts.size() > 0);
        RegisteredUser testUserWithNoApplications = userDAO.get(15);
        gotAdverts = advertDAO.getRecommendedAdverts(testUserWithNoApplications);
        assertTrue(gotAdverts.size() == 0);
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
