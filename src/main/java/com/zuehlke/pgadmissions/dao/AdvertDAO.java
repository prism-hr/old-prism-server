package com.zuehlke.pgadmissions.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.ResearchOpportunitiesFeed;
import com.zuehlke.pgadmissions.domain.enums.OpportunityListType;
import com.zuehlke.pgadmissions.dto.AdvertDTO;

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
    public List<AdvertDTO> getAdvertFeed(OpportunityListType feedKey, String feedKeyValue, Integer selectedAdvertId) {
        if (feedKey == OpportunityListType.RECOMMENDEDOPPORTUNTIIES) {
            return (List<AdvertDTO>) sessionFactory.getCurrentSession().createSQLQuery("CALL SELECT_RECOMMENDED_ADVERT(?, ?);")
                    .addEntity(AdvertDTO.class)
                    .setInteger(0, Integer.parseInt(feedKeyValue))
                    .setBigDecimal(1, new BigDecimal(0.01));
        } else {
            Criteria programQuery = null;
            Criteria projectQuery = null;
            Criterion restriction = null;
            
            Session session = sessionFactory.getCurrentSession();
            ProjectionList programProjections = Projections.projectionList();
            ProjectionList projectProjections = Projections.projectionList();
            
            if (feedKey == null 
                    || feedKey == OpportunityListType.CURRENTOPPORTUNITYBYADVERTID 
                    || feedKey == OpportunityListType.CURRENTOPPORTUNITYBYPROGRAMCODE) {
                programQuery = session.createCriteria(Program.class, "program");
                projectQuery = session.createCriteria(Program.class, "program");
                if (feedKey != null) {
                    programProjections.add(Projections.property("active"), "selected");
                    projectProjections.add(Projections.property("active"), "selected");
                    if (feedKey == OpportunityListType.CURRENTOPPORTUNITYBYADVERTID) {
                        programQuery.add(Restrictions.eq("id", Integer.parseInt(feedKeyValue)));
                        projectQuery.add(Restrictions.eq("project.id", Integer.parseInt(feedKeyValue)));
                    } else {
                        projectQuery.add(Restrictions.eq("code", feedKeyValue));
                    }
                }
            } else if (feedKey == OpportunityListType.OPPORTUNITIESBYFEEDID) {
                restriction = Restrictions.eq("id", Integer.parseInt(feedKeyValue));
                programQuery = session.createCriteria(ResearchOpportunitiesFeed.class)
                        .createAlias("programs", "program", JoinType.INNER_JOIN)
                        .add(restriction);
                projectQuery = session.createCriteria(ResearchOpportunitiesFeed.class)
                        .createAlias("programs", "program", JoinType.INNER_JOIN)
                        .add(restriction);
            } else if (feedKey == OpportunityListType.OPPORTUNITIESBYUSERUPI) {
                restriction = Restrictions.eq("registeredUser.upi", feedKeyValue);
                programQuery = session.createCriteria(ApplicationFormUserRole.class)
                        .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
                        .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                        .add(restriction);
                projectQuery = session.createCriteria(ApplicationFormUserRole.class)
                        .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
                        .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                        .add(restriction);
            } else if (feedKey == OpportunityListType.OPPORTUNITIESBYUSERUSERNAME) {
                restriction = Restrictions.eq("registeredUser.username", feedKeyValue);
                programQuery = session.createCriteria(ApplicationFormUserRole.class)
                        .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
                        .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                        .add(restriction);
                projectQuery = session.createCriteria(ApplicationFormUserRole.class)
                        .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
                        .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                        .add(restriction);
            }
            
            DateTime baseline = new DateTime(new Date());
            DateTime cleanBaseline = new DateTime(baseline.getYear(), baseline.getMonthOfYear(), baseline.getDayOfMonth(), 0, 0, 0);
            Date baselineDate = cleanBaseline.toDate();
            
            List<AdvertDTO> advertDTOs = (List<AdvertDTO>) programQuery
                    .setProjection(programProjections.add(Projections.groupProperty("program.id"), "id")
                            .add(Projections.property("program.title"), "title")
                            .add(Projections.property("program.description"), "description")
                            .add(Projections.property("program.studyDuration"), "studyDuration")
                            .add(Projections.property("program.funding"), "funding")
                            .add(Projections.property("program.code"), "programCode")
                            .add(Projections.min("closingDate.closingDate"), "closingDate")
                            .add(Projections.property("contactUser.firstName"), "primarySupervisorFirstName")
                            .add(Projections.property("contactUser.lastName"), "primarySupervisorLastName")
                            .add(Projections.property("contactUser.email"), "primarySupervisorEmail"))
                            .createAlias("program.contactUser", "contactUser", JoinType.INNER_JOIN)
                    .createAlias("program.closingDates", "closingDate", JoinType.LEFT_OUTER_JOIN)
                    .add(Restrictions.eq("program.enabled", true))
                    .add(Restrictions.eq("program.active", true))
                    .add(Restrictions.disjunction()
                            .add(Restrictions.isNull("closingDate.id"))
                            .add(Restrictions.ge("closingDate.closingDate", baselineDate)))
                    .add(Restrictions.ne("program.id", selectedAdvertId))
                    .setResultTransformer(Transformers.aliasToBean(AdvertDTO.class)).list();
            
            if (feedKey != OpportunityListType.CURRENTOPPORTUNITYBYPROGRAMCODE 
                    && !(feedKey == OpportunityListType.CURRENTOPPORTUNITYBYADVERTID
                            && advertDTOs.size() == 1)) {        
                advertDTOs.addAll((List<AdvertDTO>) projectQuery
                        .setProjection(projectProjections.add(Projections.property("project.title"), "title")
                                .add(Projections.property("project.description"), "description")
                                .add(Projections.property("project.studyDuration"), "studyDuration")
                                .add(Projections.property("project.funding"), "funding")
                                .add(Projections.property("program.code"), "programCode")
                                .add(Projections.min("closingDate.closingDate"), "closingDate")
                                .add(Projections.property("primarySupervisor.firstName"), "primarySupervisorFirstName")
                                .add(Projections.property("primarySupervisor.lastName"), "primarySupervisorLastName")
                                .add(Projections.property("primarySupervisor.email"), "primarySupervisorEmail")
                                .add(Projections.property("project.id"), "projectId")
                                .add(Projections.property("secondarySupervisor.firstName"), "secondarySupervisorFirstName")
                                .add(Projections.property("secondarySupervisor.lastName"), "secondarySupervisorLastName"))
                        .createAlias("program.projects", "project", JoinType.INNER_JOIN)
                        .createAlias("project.primarySupervisor", "primarySupervisor", JoinType.INNER_JOIN)
                        .createAlias("project.secondarySupervisor", "secondarySupervisor", JoinType.INNER_JOIN)
                        .createAlias("program.closingDates", "closingDate", JoinType.LEFT_OUTER_JOIN)
                        .add(Restrictions.eq("project.enabled", true))
                        .add(Restrictions.eq("project.active", true))
                        .add(Restrictions.disjunction()
                                .add(Restrictions.isNull("closingDate.id"))
                                .add(Restrictions.ge("closingDate.closingDate", baselineDate)))
                        .add(Restrictions.ne("project.id", selectedAdvertId))
                        .setResultTransformer(Transformers.aliasToBean(AdvertDTO.class)).list());
            }
                
            return advertDTOs;
        }
    }

}
