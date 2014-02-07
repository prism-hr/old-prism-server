package com.zuehlke.pgadmissions.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
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
        if (selectedAdvertId == null) {
            selectedAdvertId = 0;
        }
        
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

            switch (feedKey) {
            case OPPORTUNITIESBYFEEDID:
                restriction = Restrictions.eq("id", Integer.parseInt(feedKeyValue));
                programQuery = session.createCriteria(ResearchOpportunitiesFeed.class)
                        .createAlias("programs", "program", JoinType.INNER_JOIN)
                        .add(restriction);
                projectQuery = session.createCriteria(ResearchOpportunitiesFeed.class)
                        .createAlias("programs", "program", JoinType.INNER_JOIN)
                        .add(restriction);
                break;
            case OPPORTUNITIESBYUSERUPI:
                restriction = Restrictions.eq("registeredUser.upi", feedKeyValue);
                programQuery = session.createCriteria(ApplicationFormUserRole.class)
                        .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
                        .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                        .add(restriction);
                projectQuery = session.createCriteria(ApplicationFormUserRole.class)
                        .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
                        .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                        .add(restriction);
                break;
            case OPPORTUNITIESBYUSERUSERNAME:
                restriction = Restrictions.eq("registeredUser.username", feedKeyValue);
                programQuery = session.createCriteria(ApplicationFormUserRole.class)
                        .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
                        .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                        .add(restriction);
                projectQuery = session.createCriteria(ApplicationFormUserRole.class)
                        .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
                        .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                        .add(restriction);
                break;
            case CURRENTOPPORTUNITY:
                programQuery = session.createCriteria(Program.class);
                projectQuery = session.createCriteria(Project.class);
                break;
            default:
                break;
            }
            
            DateTime baseline = new DateTime(new Date());
            DateTime cleanBaseline = new DateTime(baseline.getYear(), baseline.getMonthOfYear(), baseline.getDayOfMonth(), 0, 0, 0);
            Date baselineDate = cleanBaseline.toDate();
            
            programQuery.createAlias("program.advert", "advert", JoinType.INNER_JOIN)
                    .createAlias("program.closingDates", "closingDate", JoinType.LEFT_OUTER_JOIN)
                    .add(Restrictions.eq("advert.enabled", true)).add(Restrictions.eq("advert.active", true))
                    .add(Restrictions.eq("advert.active", true))
                    .add(Restrictions.ge("programClosingDate.closingDate", baselineDate));
            
            projectQuery.createAlias("program.advert", "advert", JoinType.INNER_JOIN)
                    .createAlias("program.closingDates", "closingDate", JoinType.LEFT_OUTER_JOIN)
                    .createAlias("program.projects", "project", JoinType.INNER_JOIN)
                    .add(Restrictions.eq("advert.enabled", true)).add(Restrictions.eq("advert.active", true))
                    .add(Restrictions.eq("advert.active", true))
                    .add(Restrictions.ge("programClosingDate.closingDate", baselineDate));
            
            List<AdvertDTO> advertDTOs = (List<AdvertDTO>) programQuery
                    .setProjection(Projections.projectionList().add(Projections.groupProperty("advert.id"), "id")
                            .add(Projections.property("advert.title"), "title").add(Projections.property("advert.description"), "description")
                            .add(Projections.property("advert.studyDuration"), "studyDuration").add(Projections.property("advert.funding"), "funding")
                            .add(Projections.property("program.code"), "programCode").add(Projections.min("closingDate.date"), "programClosingDate")
                            .add(Projections.property("advert.ContactUser"), "primarySupervisor"))
                    .setResultTransformer(Transformers.aliasToBean(AdvertDTO.class)).list();
                    
            advertDTOs.addAll((List<AdvertDTO>) projectQuery
                    .setProjection(Projections.projectionList().add(Projections.groupProperty("advert.id"), "id")
                            .add(Projections.property("advert.title"), "title").add(Projections.property("advert.description"), "description")
                            .add(Projections.property("advert.studyDuration"), "studyDuration").add(Projections.property("advert.funding"), "funding")
                            .add(Projections.property("program.code"), "programCode").add(Projections.min("closingDate.date"), "programClosingDate")
                            .add(Projections.property("project.primarySupervisor"), "primarySupervisor").add(Projections.property("project.id"), "projectId")
                            .add(Projections.property("project.secondarySupervisor"), "secondarySupervisor"))
                    .setResultTransformer(Transformers.aliasToBean(AdvertDTO.class)).list());
        }
        
        return null;
    }

    public AdvertDTO getAdvertDTOByAdvertId(String advertId) {
        AdvertDTO advertDTO = (AdvertDTO) sessionFactory.getCurrentSession().createCriteria(Advert.class)
                .add(Restrictions.eq("id", Integer.parseInt(advertId))).setResultTransformer(Transformers.aliasToBean(AdvertDTO.class)).uniqueResult();
        advertDTO.setSelected(true);
        return advertDTO;
    }

    public AdvertDTO getAdvertDTOByProgramCode(String code) {
        return (AdvertDTO) sessionFactory.getCurrentSession().createCriteria(Program.class).setProjection(Projections.property("advert"))
                .add(Restrictions.eq("code", code)).setResultTransformer(Transformers.aliasToBean(AdvertDTO.class)).uniqueResult();
    }

    public AdvertDTO getAdvertDTOByProjectId(Integer projectId) {
        return (AdvertDTO) sessionFactory.getCurrentSession().createCriteria(Project.class).setProjection(Projections.property("advert"))
                .add(Restrictions.eq("id", projectId)).setResultTransformer(Transformers.aliasToBean(AdvertDTO.class)).uniqueResult();
    }

}
