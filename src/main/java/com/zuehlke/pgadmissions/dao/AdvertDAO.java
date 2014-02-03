package com.zuehlke.pgadmissions.dao;

import java.util.Arrays;
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
import com.zuehlke.pgadmissions.domain.ApplicationForm;
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

    @SuppressWarnings("unchecked")
    public List<Advert> getRecommendedAdverts(RegisteredUser applicant) {
        List<Integer> applicantPrograms = (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
                .setProjection(Projections.groupProperty("program.id"))
                .createAlias("program", "program", JoinType.INNER_JOIN)
                .add(Restrictions.eq("applicant", applicant)).list();
        applicantPrograms.add(0);
        
        List<Integer> applicantProjects = (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
                .setProjection(Projections.groupProperty("project.id"))
                .createAlias("project", "project", JoinType.INNER_JOIN)
                .add(Restrictions.eq("applicant", applicant)).list();
        applicantProjects.add(0);

        List<Integer> applicantPeers = (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
                .setProjection(Projections.groupProperty("registeredUser.id"))
                .createAlias("applicant", "registeredUser", JoinType.INNER_JOIN)
                .createAlias("program", "program", JoinType.INNER_JOIN)
                .add(Restrictions.in("program.id", applicantPrograms))
                .add(Restrictions.ne("applicant", applicant)).list();
        applicantPeers.add(0);
        
        List<Integer> applicantRecruiters = (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
                .setProjection(Projections.groupProperty("registeredUser.id"))
                .createAlias("applicationFormUserRoles", "applicationFormUserRole", JoinType.INNER_JOIN)
                .createAlias("applicationFormUserRole.user", "registeredUser", JoinType.INNER_JOIN)
                .createAlias("applicationFormUserRole.role", "role", JoinType.INNER_JOIN)
                .add(Restrictions.eq("applicant", applicant))
                .add(Restrictions.ne("applicationFormUserRole.user", applicant))
                .add(Restrictions.in("role.id", Arrays.asList(Authority.APPROVER, Authority.INTERVIEWER, Authority.PROJECTADMINISTRATOR, 
                        Authority.REVIEWER, Authority.SUGGESTEDSUPERVISOR, Authority.STATEADMINISTRATOR, Authority.SUPERVISOR))).list();
        applicantRecruiters.add(0);
        
        List<Integer> recommendedPrograms = (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .setProjection(Projections.groupProperty("program.id"))
                .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
                .createAlias("applicationForm.applicant", "applicant", JoinType.INNER_JOIN)
                .createAlias("user", "recruiter", JoinType.INNER_JOIN)
                .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                .createAlias("program.advert", "advert", JoinType.INNER_JOIN)
                .add(Restrictions.disjunction()
                        .add(Restrictions.in("applicant.id", applicantPeers))
                        .add(Restrictions.in("recruiter.id", applicantRecruiters)))
                .add(Restrictions.not(
                        Restrictions.in("program.id", applicantPrograms)))
                .add(Restrictions.eq("program.enabled", true)).list();
                
        HashSet<Advert> deduplicateRecommendedAdverts = new HashSet<Advert>((List<Advert>)sessionFactory.getCurrentSession().createCriteria(Program.class)
                .setProjection(Projections.property("advert"))
                .createAlias("advert", "advert", JoinType.INNER_JOIN)
                .add(Restrictions.in("id", recommendedPrograms))
                .add(Restrictions.eq("advert.active", true)).list());
                
        deduplicateRecommendedAdverts.addAll((List<Advert>)sessionFactory.getCurrentSession().createCriteria(Project.class)
                .setProjection(Projections.property("advert"))
                .createAlias("program", "program", JoinType.INNER_JOIN)
                .createAlias("advert", "advert", JoinType.INNER_JOIN)
                .add(Restrictions.in("program.id", recommendedPrograms))
                .add(Restrictions.eq("disabled", false))
                .add(Restrictions.eq("advert.active", true)).list());
        
        List<Advert> recommendedAdverts = new ArrayList<Advert>(deduplicateRecommendedAdverts);
        
        Collections.sort(recommendedAdverts, new Comparator<Advert>() {
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
        
        return recommendedAdverts;
    }

    public void delete(Advert advert) {
        if (advert == null || advert.getId() == null) {
            return;
        }
        sessionFactory.getCurrentSession().delete(advert);
    }
    
}
