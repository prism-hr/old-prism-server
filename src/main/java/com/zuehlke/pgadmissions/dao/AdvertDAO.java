package com.zuehlke.pgadmissions.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ResearchOpportunitiesFeed;

@Repository
public class AdvertDAO {

    private final SessionFactory sessionFactory;

    public AdvertDAO() {
        this(null);
    }

    @Autowired
    public AdvertDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Advert advert) {
        sessionFactory.getCurrentSession().saveOrUpdate(advert);
    }

    public Program getProgram(Advert advert) {
        return (Program) sessionFactory.getCurrentSession().createCriteria(Program.class)
                .add(Restrictions.eq("advert", advert)).uniqueResult();
    }

    public Project getProject(Advert advert) {
        return (Project) sessionFactory.getCurrentSession().createCriteria(Project.class)
                .add(Restrictions.eq("advert", advert)).uniqueResult();
    }

    public Advert getAdvertById(int advertId) {
        return (Advert) sessionFactory.getCurrentSession().get(Advert.class, advertId);
    }

    @SuppressWarnings("unchecked")
    public List<Advert> getActiveAdverts() {
        return sessionFactory.getCurrentSession().createCriteria(Advert.class)
                .add(Restrictions.eq("active", true)).list();
    }

    @SuppressWarnings("unchecked")
    public List<Advert> getRecommendedAdverts(RegisteredUser applicant) {
        return (List<Advert>) sessionFactory.getCurrentSession()
                .createSQLQuery("CALL SELECT_RECOMMENDED_ADVERT(?, ?);")
                .addEntity(Advert.class)
                .setInteger(0, applicant.getId())
                .setBigDecimal(1, new BigDecimal(0.01)).list();
    }
    
    @SuppressWarnings("unchecked")
    public List<Advert> getAdvertsByFeedId(Integer feedId) {
        Session session = sessionFactory.getCurrentSession();
        List<Advert> adverts = (List<Advert>) session.createCriteria(ResearchOpportunitiesFeed.class)
                .setProjection(Projections.property("program.advert"))
                .createAlias("programs", "program", JoinType.INNER_JOIN)
                .createAlias("program.advert", "advert", JoinType.INNER_JOIN)
                .add(Restrictions.eq("id", feedId))
                .add(Restrictions.eq("program.enabled", true))
                .add(Restrictions.eq("advert.active", true)).list();
        adverts.addAll((List<Advert>) session.createCriteria(ResearchOpportunitiesFeed.class)
                .setProjection(Projections.property("project.advert"))
                .createAlias("programs", "program", JoinType.INNER_JOIN)
                .createAlias("programs.projects", "project", JoinType.INNER_JOIN)
                .createAlias("project.advert", "advert", JoinType.INNER_JOIN)
                .add(Restrictions.eq("id", feedId))
                .add(Restrictions.eq("program.enabled", true))
                .add(Restrictions.eq("project.disabled", false))
                .add(Restrictions.eq("advert.active", true)).list());
        return adverts;
    }
    
    @SuppressWarnings("unchecked")
    public List<Advert> getAdvertsByUserUPI(String userUPI) {
        Session session = sessionFactory.getCurrentSession();
        List<Advert> adverts = (List<Advert>) session.createCriteria(ApplicationFormUserRole.class)
                .setProjection(Projections.groupProperty("program.advert"))
                .createAlias("user", "registeredUser", JoinType.INNER_JOIN)
                .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
                .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                .createAlias("program.advert", "advert", JoinType.INNER_JOIN)
                .add(Restrictions.eq("registeredUser.upi", userUPI))
                .add(Restrictions.eq("program.enabled", true))
                .add(Restrictions.eq("advert.active", true)).list();
        adverts.addAll((List<Advert>) session.createCriteria(ApplicationFormUserRole.class)
                .setProjection(Projections.groupProperty("project.advert"))
                .createAlias("user", "registeredUser", JoinType.INNER_JOIN)
                .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
                .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                .createAlias("program.projects", "project", JoinType.INNER_JOIN)
                .createAlias("project.advert", "advert", JoinType.INNER_JOIN)
                .add(Restrictions.eq("registeredUser.upi", userUPI))
                .add(Restrictions.eq("program.enabled", true))
                .add(Restrictions.eq("project.disabled", false))
                .add(Restrictions.eq("advert.active", true)).list());
        return adverts;
    }
    
    @SuppressWarnings("unchecked")
    public List<Advert> getAdvertsByUserUsername(String username) {
        Session session = sessionFactory.getCurrentSession();
        List<Advert> adverts = (List<Advert>) session.createCriteria(ApplicationFormUserRole.class)
                .setProjection(Projections.groupProperty("program.advert"))
                .createAlias("user", "registeredUser", JoinType.INNER_JOIN)
                .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
                .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                .createAlias("program.advert", "advert", JoinType.INNER_JOIN)
                .add(Restrictions.eq("registeredUser.username", username))
                .add(Restrictions.eq("program.enabled", true))
                .add(Restrictions.eq("advert.active", true)).list();
        adverts.addAll((List<Advert>) session.createCriteria(ApplicationFormUserRole.class)
                .setProjection(Projections.groupProperty("project.advert"))
                .createAlias("user", "registeredUser", JoinType.INNER_JOIN)
                .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
                .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                .createAlias("program.projects", "project", JoinType.INNER_JOIN)
                .createAlias("project.advert", "advert", JoinType.INNER_JOIN)
                .add(Restrictions.eq("registeredUser.username", username))
                .add(Restrictions.eq("program.enabled", true))
                .add(Restrictions.eq("project.disabled", false))
                .add(Restrictions.eq("advert.active", true)).list());
        return adverts;
    }
    
    public Advert getProgramAdvertByProgramCode(String code) {
        return (Advert) sessionFactory.getCurrentSession().createCriteria(Program.class)
                .setProjection(Projections.property("advert"))
                .add(Restrictions.eq("code", code)).uniqueResult();
    }
    
    public Advert getProjectAdvertByProjectId(Integer projectId) {
        return (Advert) sessionFactory.getCurrentSession().createCriteria(Project.class)
                .setProjection(Projections.property("advert"))
                .add(Restrictions.eq("id", projectId)).uniqueResult();
    }
    
}
