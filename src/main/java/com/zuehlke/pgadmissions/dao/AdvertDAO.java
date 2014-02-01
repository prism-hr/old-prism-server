package com.zuehlke.pgadmissions.dao;

import java.util.Collections;
import java.util.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

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
import com.zuehlke.pgadmissions.domain.enums.Authority;

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
    
    /**
     * Returns a list of other recommended opportunities for a given applicant as follows:
     * - Query 1: programs related to the seed application through associations between users, applications and programs.
     * - Query 2: projects related to the seed application through associations between users, applications and programs.
     * - Query 3: projects related to the seed application through associations between users, applications and projects.
     * @param applicant
     * @return List<Advert>
     * @author Alastair Knowles
     */
    @SuppressWarnings("unchecked")
    public List<Advert> getRecommendedAdverts(RegisteredUser applicant) {
        Authority[] authoritiesToConsider = {Authority.APPLICANT, Authority.INTERVIEWER, Authority.REVIEWER,
                Authority.SUGGESTEDSUPERVISOR, Authority.STATEADMINISTRATOR, Authority.SUGGESTEDSUPERVISOR, Authority.SUPERVISOR};
        
        HashSet<Advert> adverts = new HashSet<Advert>((List<Advert>) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .setProjection(Projections.groupProperty("program2.advert"))
                .createAlias("applicationForm", "application", JoinType.INNER_JOIN)
                .createAlias("application.program", "program", JoinType.INNER_JOIN)
                .createAlias("program.applications", "application2", JoinType.INNER_JOIN)
                .createAlias("application2.applicationFormUserRoles", "applicationFormUserRole2", JoinType.INNER_JOIN)
                .createAlias("applicationFormUserRole2.applicationForm", "application3", JoinType.INNER_JOIN)
                .createAlias("application3.program", "program2", JoinType.INNER_JOIN)
                .createAlias("program2.advert", "advert", JoinType.INNER_JOIN)
                .add(Restrictions.eq("application.applicant", applicant))
                .add(Restrictions.in("applicationFormUserRole2.role.id", authoritiesToConsider))
                .add(Restrictions.neProperty("application.applicant", "application3.applicant"))
                .add(Restrictions.eq("program2.enabled", true)) 
                .add(Restrictions.eq("advert.active", true)).list());
        
        adverts.addAll((List<Advert>) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .setProjection(Projections.groupProperty("project.advert"))
                .createAlias("applicationForm", "application", JoinType.INNER_JOIN)
                .createAlias("application.program", "program", JoinType.INNER_JOIN)
                .createAlias("program.applications", "application2", JoinType.INNER_JOIN)
                .createAlias("application2.applicationFormUserRoles", "applicationFormUserRole2", JoinType.INNER_JOIN)
                .createAlias("applicationFormUserRole2.applicationForm", "application3", JoinType.INNER_JOIN)
                .createAlias("application3.program", "program2", JoinType.INNER_JOIN)
                .createAlias("program2.projects", "project", JoinType.INNER_JOIN)
                .createAlias("project.advert", "advert", JoinType.INNER_JOIN)
                .createAlias("project.applications", "application4", JoinType.INNER_JOIN)
                .add(Restrictions.eq("application.applicant", applicant))
                .add(Restrictions.in("applicationFormUserRole2.role.id", authoritiesToConsider))
                .add(Restrictions.neProperty("application.applicant", "application4.applicant"))
                .add(Restrictions.eq("project.disabled", false)) 
                .add(Restrictions.eq("advert.active", true)).list());
        
        adverts.addAll((List<Advert>) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .setProjection(Projections.groupProperty("project2.advert"))
                .createAlias("applicationForm", "application", JoinType.INNER_JOIN)
                .createAlias("application.project", "project", JoinType.INNER_JOIN)
                .createAlias("project.applications", "application2", JoinType.INNER_JOIN)
                .createAlias("application2.applicationFormUserRoles", "applicationFormUserRole2", JoinType.INNER_JOIN)
                .createAlias("applicationFormUserRole2.applicationForm", "application3", JoinType.INNER_JOIN)
                .createAlias("application3.project", "project2", JoinType.INNER_JOIN)
                .createAlias("project2.advert", "advert", JoinType.INNER_JOIN)
                .add(Restrictions.eq("application.applicant", applicant))
                .add(Restrictions.in("applicationFormUserRole2.role.id", authoritiesToConsider))
                .add(Restrictions.neProperty("application.applicant", "application3.applicant"))
                .add(Restrictions.eq("project2.disabled", false)) 
                .add(Restrictions.eq("advert.active", true)).list());
        
        List<Advert> deduplicatedAdverts = new ArrayList<Advert>(adverts);
        
        Collections.sort(deduplicatedAdverts, new Comparator<Advert>() {
            @Override
            public int compare(Advert advert1, Advert advert2) {
                Date date1 = advert1.getLastEditedTimestamp();
                Date date2 = advert2.getLastEditedTimestamp();
                if (date1.after(date2))
                    return 1;
                if (date2.after(date1))
                    return -1;
                return 0;
            }
        });
        
        return deduplicatedAdverts;
    }

    public void delete(Advert advert) {
        if (advert == null || advert.getId() == null) {
            return;
        }
        sessionFactory.getCurrentSession().delete(advert);
    }
    
}
