package com.zuehlke.pgadmissions.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    private final RoleDAO roleDAO;

    public AdvertDAO() {
        this(null, null);
    }

    @Autowired
    public AdvertDAO(SessionFactory sessionFactory, RoleDAO roleDAO) {
        this.sessionFactory = sessionFactory;
        this.roleDAO = roleDAO;
    }

    public void save(Advert advert) {
        sessionFactory.getCurrentSession().saveOrUpdate(advert);
    }

    public Program getProgram(Advert advert) {
        return (Program) sessionFactory.getCurrentSession().createCriteria(Program.class).add(Restrictions.eq("advert", advert)).uniqueResult();
    }

    public Project getProject(Advert advert) {
        return (Project) sessionFactory.getCurrentSession().createCriteria(Project.class).add(Restrictions.eq("advert", advert)).uniqueResult();
    }

    public Advert getAdvertById(int advertId) {
        return (Advert) sessionFactory.getCurrentSession().get(Advert.class, advertId);
    }

    @SuppressWarnings("unchecked")
    public List<Advert> getActiveAdverts() {
        return sessionFactory.getCurrentSession().createCriteria(Advert.class).add(Restrictions.eq("active", true)).list();
    }

    @SuppressWarnings("unchecked")
    public List<Advert> getRecommendedAdverts(RegisteredUser user) {
        HashSet<Advert> adverts = new HashSet<Advert>((List<Advert>) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .setProjection(Projections.groupProperty("program2.advert"))
                .createAlias("applicationForm", "application", JoinType.INNER_JOIN)
                .createAlias("application.program", "program", JoinType.INNER_JOIN)
                .createAlias("program.applications", "application2", JoinType.INNER_JOIN)
                .createAlias("application2.applicationFormUserRoles", "applicationFormUserRole2", JoinType.INNER_JOIN)
                .createAlias("applicationFormUserRole2.applicationForm", "application3", JoinType.INNER_JOIN)
                .createAlias("application3.program", "program2", JoinType.INNER_JOIN)
                .createAlias("program2.advert", "advert", JoinType.INNER_JOIN)
                .add(Restrictions.eq("user", user))
                .add(Restrictions.eq("role", roleDAO.getRoleByAuthority(Authority.APPLICANT)))
                .add(Restrictions.neProperty("user", "applicationFormUserRole2.user"))
                .add(Restrictions.in("applicationFormUserRole2.role", Arrays.asList(Authority.APPLICANT, Authority.INTERVIEWER, Authority.REVIEWER,
                        Authority.SUGGESTEDSUPERVISOR, Authority.STATEADMINISTRATOR, Authority.SUGGESTEDSUPERVISOR, Authority.SUPERVISOR)))
                .add(Restrictions.eq("program.enabled", false)) 
                .add(Restrictions.eq("advert.active", true)).list());
        adverts.addAll((List<Advert>) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .setProjection(Projections.groupProperty("program2.advert"))
                .createAlias("applicationForm", "application", JoinType.INNER_JOIN)
                .createAlias("application.program", "program", JoinType.INNER_JOIN)
                .createAlias("program.applications", "application2", JoinType.INNER_JOIN)
                .createAlias("application2.applicationFormUserRoles", "applicationFormUserRole2", JoinType.INNER_JOIN)
                .createAlias("applicationFormUserRole2.applicationForm", "application3", JoinType.INNER_JOIN)
                .createAlias("application3.program", "program2", JoinType.INNER_JOIN)
                .createAlias("program2.advert", "advert", JoinType.INNER_JOIN)
                .add(Restrictions.eq("user", user))
                .add(Restrictions.eq("role", roleDAO.getRoleByAuthority(Authority.APPLICANT)))
                .add(Restrictions.neProperty("user", "applicationFormUserRole2.user"))
                .add(Restrictions.in("applicationFormUserRole2.role", Arrays.asList(Authority.APPLICANT, Authority.INTERVIEWER, Authority.REVIEWER,
                        Authority.SUGGESTEDSUPERVISOR, Authority.STATEADMINISTRATOR, Authority.SUGGESTEDSUPERVISOR, Authority.SUPERVISOR)))
                .add(Restrictions.eq("program.enabled", false)) 
                .add(Restrictions.eq("advert.active", true)).list());
        adverts.addAll((List<Advert>) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .setProjection(Projections.groupProperty("program2.advert"))
                .createAlias("applicationForm", "application", JoinType.INNER_JOIN)
                .createAlias("application.project", "project", JoinType.INNER_JOIN)
                .createAlias("project.applications", "application2", JoinType.INNER_JOIN)
                .createAlias("application2.applicationFormUserRoles", "applicationFormUserRole2", JoinType.INNER_JOIN)
                .createAlias("applicationFormUserRole2.applicationForm", "application3", JoinType.INNER_JOIN)
                .createAlias("application3.program", "program2", JoinType.INNER_JOIN)
                .createAlias("program2.advert", "advert", JoinType.INNER_JOIN)
                .add(Restrictions.eq("user", user))
                .add(Restrictions.eq("role", roleDAO.getRoleByAuthority(Authority.APPLICANT)))
                .add(Restrictions.neProperty("user", "applicationFormUserRole2.user"))
                .add(Restrictions.in("applicationFormUserRole2.role", Arrays.asList(Authority.APPLICANT, Authority.INTERVIEWER, Authority.REVIEWER,
                        Authority.SUGGESTEDSUPERVISOR, Authority.STATEADMINISTRATOR, Authority.SUGGESTEDSUPERVISOR, Authority.SUPERVISOR)))
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
                .add(Restrictions.eq("user", user))
                .add(Restrictions.eq("role", roleDAO.getRoleByAuthority(Authority.APPLICANT)))
                .add(Restrictions.neProperty("user", "applicationFormUserRole2.user"))
                .add(Restrictions.in("applicationFormUserRole2.role", Arrays.asList(Authority.APPLICANT, Authority.INTERVIEWER, Authority.REVIEWER,
                        Authority.SUGGESTEDSUPERVISOR, Authority.STATEADMINISTRATOR, Authority.SUGGESTEDSUPERVISOR, Authority.SUPERVISOR)))
                .add(Restrictions.eq("project.disabled", false)) 
                .add(Restrictions.eq("advert.active", true)).list());
        List<Advert> deduplicatedAdverts = new ArrayList<Advert>(adverts);
        Collections.shuffle(deduplicatedAdverts);
        return deduplicatedAdverts;
    }

    public void delete(Advert advert) {
        if (advert == null || advert.getId() == null) {
            return;
        }
        sessionFactory.getCurrentSession().delete(advert);
    }

}