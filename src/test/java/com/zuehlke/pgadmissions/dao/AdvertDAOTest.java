package com.zuehlke.pgadmissions.dao;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.ResearchOpportunitiesFeed;
import com.zuehlke.pgadmissions.domain.builders.ResearchOpportunitiesFeedBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.FeedFormat;
import com.zuehlke.pgadmissions.domain.enums.OpportunityListType;
import com.zuehlke.pgadmissions.dto.AdvertDTO;

public class AdvertDAOTest extends AutomaticRollbackTestCase {

    private AdvertDAO advertDAO;
    
    @Override
    public void setup() {
        super.setup();
        advertDAO = new AdvertDAO(sessionFactory);
    }
    
    @Test
    public void shouldGetAllActiveAdverts() {
        Integer activeAdvertCount = sessionFactory.getCurrentSession().createCriteria(Advert.class)
                .add(Restrictions.eq("enabled", true))
                .add(Restrictions.eq("active", true)).list().size();
        List<AdvertDTO> loadedAdverts = advertDAO.getAdvertFeed(null, null, null);
        assertThat(loadedAdverts.size(), equalTo(activeAdvertCount));
    }
    
    @Test
    public void shouldGetProgramAdvert() {
        Program program = testObjectProvider.getEnabledProgram();
        String programId = program.getId().toString(); 
        List<AdvertDTO> loadedAdverts = advertDAO.getAdvertFeed(OpportunityListType.CURRENTOPPORTUNITYBYADVERTID, programId, null);
        assertThat(loadedAdverts.size(), equalTo(1));
        assertThat(loadedAdverts.get(0).getId(), equalTo(program.getId()));
    }
    
    @Test
    public void shouldGetProjectAdvert() {
        Project project = testObjectProvider.getEnabledProject();
        String projectId = project.getId().toString(); 
        List<AdvertDTO> loadedAdverts = advertDAO.getAdvertFeed(OpportunityListType.CURRENTOPPORTUNITYBYADVERTID, projectId, null);
        assertThat(loadedAdverts.size(), equalTo(1));
        assertThat(loadedAdverts.get(0).getId(), equalTo(project.getId()));
    }
    
    @Test
    public void shouldGetProgramApplicationAdvert() {
        ApplicationForm applicationForm = testObjectProvider.getEnabledProgramApplication();
        String applicationFormId = applicationForm.getId().toString(); 
        List<AdvertDTO> loadedAdverts = advertDAO.getAdvertFeed(OpportunityListType.CURRENTOPPORTUNITYBYAPPLICATIONFORMID, applicationFormId, null);
        assertThat(loadedAdverts.size(), equalTo(1));
        assertThat(loadedAdverts.get(0).getId(), equalTo(applicationForm.getProgram().getId()));
    }
    
    @Test
    public void shouldGetProjectApplicationAdvert() {
        ApplicationForm applicationForm = testObjectProvider.getEnabledProjectApplication();
        String applicationFormId = applicationForm.getId().toString();
        List<AdvertDTO> loadedAdverts = advertDAO.getAdvertFeed(OpportunityListType.CURRENTOPPORTUNITYBYAPPLICATIONFORMID, applicationFormId, null);
        assertThat(loadedAdverts.size(), equalTo(2));
        assertThat(loadedAdverts.get(0).getId(), equalTo(applicationForm.getProgram().getId()));
        assertThat(loadedAdverts.get(1).getId(), equalTo(applicationForm.getProject().getId()));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldGetAdvertsByUserUsername() {
        User testUser = testObjectProvider.getEnabledUserInRole(Authority.SYSTEM_ADMINISTRATOR);
        String testUserUsername = testUser.getUsername();
        
        List<Integer> advertIds = (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserRole.class)
                .setProjection(Projections.groupProperty("program.id"))
                .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
                .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                .createAlias("user", "registeredUser", JoinType.INNER_JOIN)
                .add(Restrictions.eq("registeredUser.username", testUserUsername))
                .add(Restrictions.eq("program.enabled", true))
                .add(Restrictions.eq("program.active", true)).list();
        
        advertIds.addAll(sessionFactory.getCurrentSession().createCriteria(UserRole.class)
                .setProjection(Projections.groupProperty("project.id"))
                .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
                .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                .createAlias("program.projects", "project", JoinType.INNER_JOIN)
                .createAlias("user", "registeredUser", JoinType.INNER_JOIN)
                .add(Restrictions.eq("registeredUser.username", testUserUsername))
                .add(Restrictions.eq("project.enabled", true))
                .add(Restrictions.eq("project.active", true)).list());
        
        List<AdvertDTO> loadedAdverts = advertDAO.getAdvertFeed(OpportunityListType.OPPORTUNITIESBYUSERUSERNAME, testUserUsername, null);
        
        Integer correctlyLoadedAdvertCount = 0;
        for (AdvertDTO loadedAdvert : loadedAdverts) {
            if (advertIds.contains(loadedAdvert.getId())) {
                correctlyLoadedAdvertCount ++;
            }
        }
        
        assertThat(loadedAdverts.size(), equalTo(correctlyLoadedAdvertCount));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldGetAdvertsByUserUpi() {
        String testUPI = "testUPI";
        User testUser = testObjectProvider.getEnabledUserInRole(Authority.SYSTEM_ADMINISTRATOR);
//        testUser.setUpi(testUPI);
        save(testUser);
        
        List<Integer> advertIds = (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserRole.class)
                .setProjection(Projections.groupProperty("program.id"))
                .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
                .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                .createAlias("user", "registeredUser", JoinType.INNER_JOIN)
                .add(Restrictions.eq("registeredUser.upi", testUPI))
                .add(Restrictions.eq("program.enabled", true))
                .add(Restrictions.eq("program.active", true)).list();
        
        advertIds.addAll(sessionFactory.getCurrentSession().createCriteria(UserRole.class)
                .setProjection(Projections.groupProperty("project.id"))
                .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
                .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                .createAlias("program.projects", "project", JoinType.INNER_JOIN)
                .createAlias("user", "registeredUser", JoinType.INNER_JOIN)
                .add(Restrictions.eq("registeredUser.upi", testUPI))
                .add(Restrictions.eq("project.enabled", true))
                .add(Restrictions.eq("project.active", true)).list());
        
        List<AdvertDTO> loadedAdverts = advertDAO.getAdvertFeed(OpportunityListType.OPPORTUNITIESBYUSERUSERNAME, "ts", null);
        
        Integer correctlyLoadedAdvertCount = 0;
        for (AdvertDTO loadedAdvert : loadedAdverts) {
            if (advertIds.contains(loadedAdvert.getId())) {
                correctlyLoadedAdvertCount ++;
            }
        }
        
        assertThat(loadedAdverts.size(), equalTo(correctlyLoadedAdvertCount));
    }
    
    @Test
    public void shouldGetAdvertsByFeedId() {
        Program program = testObjectProvider.getEnabledProgram();
        Program otherProgram = testObjectProvider.getAlternativeEnabledProgram(program);
        
        ResearchOpportunitiesFeed feed = new ResearchOpportunitiesFeedBuilder().feedFormat(FeedFormat.LARGE).programs(program, otherProgram)
                .title("feed").user(testObjectProvider.getEnabledUserInRole(Authority.SYSTEM_ADMINISTRATOR)).build();
        save(feed);
        
        List<AdvertDTO> loadedAdverts = advertDAO.getAdvertFeed(OpportunityListType.OPPORTUNITIESBYFEEDID, feed.getId().toString(), null);

        Integer correctlyLoadedAdvertCount = 0;
        for (AdvertDTO loadedAdvert : loadedAdverts) {
            if (program.getId() == loadedAdvert.getId() || otherProgram.getId() == loadedAdvert.getId()) {
                correctlyLoadedAdvertCount ++;
            } else {
                for (Project project : program.getProjects()) {
                    if (project.getId() == loadedAdvert.getId()) {
                        correctlyLoadedAdvertCount ++;
                        continue;
                    }
                }
                for (Project project : otherProgram.getProjects()) {
                    if (project.getId() == loadedAdvert.getId()) {
                        correctlyLoadedAdvertCount ++;
                        continue;
                    }
                }
            }
        }
        
        assertThat(loadedAdverts.size(), equalTo(correctlyLoadedAdvertCount));
    }

    @Test
    public void shouldHighlightSelectedAdvert() {
        Program program = testObjectProvider.getEnabledProgram();
        List<AdvertDTO> loadedAdverts = advertDAO.getAdvertFeed(OpportunityListType.CURRENTOPPORTUNITYBYADVERTID, program.getId().toString(), null);
        assertThat(loadedAdverts.get(0).getId(), equalTo(program.getId()));
        assertTrue(loadedAdverts.get(0).getSelected());
        
        loadedAdverts = advertDAO.getAdvertFeed(null, null, program.getId());
        
        Boolean excludesSelectedAdvert = true;
        for (AdvertDTO loadedAdvert : loadedAdverts) {
            if (loadedAdvert.getId() == program.getId()) {
                excludesSelectedAdvert = false;
                break;
            }
        }
        
        assertTrue(excludesSelectedAdvert);
    }

    @Test
    public void shouldGetRecommendedAdverts() {
        String testUserId = new Integer(testObjectProvider.getEnabledUserInRole(Authority.APPLICATION_CREATOR).getId()).toString();
        List<AdvertDTO> gotAdverts = advertDAO.getAdvertFeed(OpportunityListType.RECOMMENDEDOPPORTUNTIIESBYAPPLICANTID, testUserId, null);
        assertTrue(gotAdverts.size() > 0);
    }

}
