package com.zuehlke.pgadmissions.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
    public List<AdvertDTO> getActiveAdverts(Integer selectedAdvertId) {        
        return sessionFactory.getCurrentSession().createCriteria(Advert.class)
                .add(Restrictions.eq("active", true))
                .add(Restrictions.ne("id", selectedAdvertId))
                .setResultTransformer(Transformers.aliasToBean(AdvertDTO.class)).list();
    }

    @SuppressWarnings("unchecked")
    public List<AdvertDTO> getRecommendedAdvertDTOs(Integer applicantId) {
        return (List<AdvertDTO>) sessionFactory.getCurrentSession()
                .createSQLQuery("CALL SELECT_RECOMMENDED_ADVERT(?, ?);")
                .addEntity(AdvertDTO.class)
                .setInteger(0, applicantId)
                .setBigDecimal(1, new BigDecimal(0.01)).list();
    }
    
    @SuppressWarnings("unchecked")
    public List<AdvertDTO> getAdvertDTOsByFeedId(Integer feedId, Integer selectedAdvertId) {
        Date baselineDate = getBaselineDate(new Date());
        Session session = sessionFactory.getCurrentSession();
        
        List<AdvertDTO> adverts = (List<AdvertDTO>) session.createCriteria(ResearchOpportunitiesFeed.class)
                .setProjection(Projections.projectionList()
                        .add(Projections.groupProperty("advert.id"), "id")
                        .add(Projections.groupProperty("advert.title"), "title")
                        .add(Projections.groupProperty("advert.description"), "description")
                        .add(Projections.groupProperty("advert.studyDuration"), "studyDuration")
                        .add(Projections.groupProperty("advert.funding"), "funding")
                        .add(Projections.property("program.code"), "programCode")
                        .add(Projections.min("closingDate.date"), "programClosingDate")
                        .add(Projections.groupProperty("program.admistrators"), "primarySupervisor"))
                .createAlias("programs", "program", JoinType.INNER_JOIN)
                .createAlias("program.advert", "advert", JoinType.INNER_JOIN)
                .createAlias("program.closingDates", "closingDate", JoinType.LEFT_OUTER_JOIN)
                .createAlias("program.administrators", "registeredUser", JoinType.LEFT_OUTER_JOIN)
                .add(Restrictions.eq("id", feedId))
                .add(Restrictions.eq("program.enabled", true))
                .add(Restrictions.eq("advert.active", true))
                .add(Restrictions.ge("programClosingDate.closingDate", baselineDate))
                .add(Restrictions.ne("id", selectedAdvertId))
                .setResultTransformer(Transformers.aliasToBean(AdvertDTO.class)).list();
        
        adverts.addAll((List<AdvertDTO>) session.createCriteria(ResearchOpportunitiesFeed.class)
                .setProjection(Projections.projectionList()
                        .add(Projections.groupProperty("advert.id"), "id")
                        .add(Projections.groupProperty("advert.title"), "title")
                        .add(Projections.groupProperty("advert.description"), "description")
                        .add(Projections.groupProperty("advert.studyDuration"), "studyDuration")
                        .add(Projections.groupProperty("advert.funding"), "funding")
                        .add(Projections.property("program.code"), "programCode")
                        .add(Projections.min("closingDate.date"), "programClosingDate")
                        .add(Projections.groupProperty("project.primarySupervisor"), "primarySupervisor")
                        .add(Projections.property("project.id"), "projectId")
                        .add(Projections.groupProperty("project.secondarySupervisor"), "secondarySupervisor"))
                .createAlias("programs", "program", JoinType.INNER_JOIN)
                .createAlias("programs.projects", "project", JoinType.INNER_JOIN)
                .createAlias("project.advert", "advert", JoinType.INNER_JOIN)
                .createAlias("program.closingDates", "closingDate", JoinType.LEFT_OUTER_JOIN)
                .add(Restrictions.eq("id", feedId))
                .add(Restrictions.eq("program.enabled", true))
                .add(Restrictions.eq("project.disabled", false))
                .add(Restrictions.eq("advert.active", true))
                .add(Restrictions.ge("programClosingDate.closingDate", baselineDate))
                .add(Restrictions.ne("id", selectedAdvertId))
                .setResultTransformer(Transformers.aliasToBean(AdvertDTO.class)).list());
        
        return adverts;
    }
    
    @SuppressWarnings("unchecked")
    public List<AdvertDTO> getAdvertDTOsByUserUPI(String userUPI, Integer selectedAdvertId) {
        Date baselineDate = getBaselineDate(new Date());
        Session session = sessionFactory.getCurrentSession();
        
        List<AdvertDTO> adverts = (List<AdvertDTO>) session.createCriteria(ApplicationFormUserRole.class)
                .setProjection(Projections.projectionList()
                        .add(Projections.groupProperty("advert.id"), "id")
                        .add(Projections.groupProperty("advert.title"), "title")
                        .add(Projections.groupProperty("advert.description"), "description")
                        .add(Projections.groupProperty("advert.studyDuration"), "studyDuration")
                        .add(Projections.groupProperty("advert.funding"), "funding")
                        .add(Projections.property("program.code"), "programCode")
                        .add(Projections.min("closingDate.date"), "programClosingDate")
                        .add(Projections.groupProperty("project.primarySupervisor"), "primarySupervisor")
                        .add(Projections.groupProperty("project.secondarySupervisor"), "secondarySupervisor"))
                .createAlias("user", "registeredUser", JoinType.INNER_JOIN)
                .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
                .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                .createAlias("program.advert", "advert", JoinType.INNER_JOIN)
                .add(Restrictions.eq("registeredUser.upi", userUPI))
                .add(Restrictions.eq("program.enabled", true))
                .add(Restrictions.eq("advert.active", true))
                .add(Restrictions.ge("programClosingDate.closingDate", baselineDate))
                .add(Restrictions.ne("id", selectedAdvertId))
                .setResultTransformer(Transformers.aliasToBean(AdvertDTO.class)).list();
        
        adverts.addAll((List<AdvertDTO>) session.createCriteria(ApplicationFormUserRole.class)
                .setProjection(Projections.projectionList()
                        .add(Projections.groupProperty("advert.id"), "id")
                        .add(Projections.groupProperty("advert.title"), "title")
                        .add(Projections.groupProperty("advert.description"), "description")
                        .add(Projections.groupProperty("advert.studyDuration"), "studyDuration")
                        .add(Projections.groupProperty("advert.funding"), "funding")
                        .add(Projections.property("program.code"), "programCode")
                        .add(Projections.min("closingDate.date"), "programClosingDate")
                        .add(Projections.groupProperty("project.primarySupervisor"), "primarySupervisor")
                        .add(Projections.property("project.id"), "projectId")
                        .add(Projections.groupProperty("project.secondarySupervisor"), "secondarySupervisor"))
                .createAlias("user", "registeredUser", JoinType.INNER_JOIN)
                .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
                .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                .createAlias("program.projects", "project", JoinType.INNER_JOIN)
                .createAlias("project.advert", "advert", JoinType.INNER_JOIN)
                .add(Restrictions.eq("registeredUser.upi", userUPI))
                .add(Restrictions.eq("program.enabled", true))
                .add(Restrictions.eq("project.disabled", false))
                .add(Restrictions.eq("advert.active", true))
                .add(Restrictions.ge("programClosingDate.closingDate", baselineDate))
                .add(Restrictions.ne("id", selectedAdvertId))
                .setResultTransformer(Transformers.aliasToBean(AdvertDTO.class)).list());
        
        return adverts;
    }
    
    @SuppressWarnings("unchecked")
    public List<AdvertDTO> getAdvertDTOsByUserUsername(String username, Integer selectedAdvertId) {
        Date baselineDate = getBaselineDate(new Date());
        Session session = sessionFactory.getCurrentSession();
        
        List<AdvertDTO> adverts = (List<AdvertDTO>) session.createCriteria(ApplicationFormUserRole.class)
                .setProjection(Projections.projectionList()
                        .add(Projections.groupProperty("advert.id"), "id")
                        .add(Projections.groupProperty("advert.title"), "title")
                        .add(Projections.groupProperty("advert.description"), "description")
                        .add(Projections.groupProperty("advert.studyDuration"), "studyDuration")
                        .add(Projections.groupProperty("advert.funding"), "funding")
                        .add(Projections.property("program.code"), "programCode")
                        .add(Projections.min("closingDate.date"), "programClosingDate")
                        .add(Projections.groupProperty("project.primarySupervisor"), "primarySupervisor")
                        .add(Projections.groupProperty("project.secondarySupervisor"), "secondarySupervisor"))
                .createAlias("user", "registeredUser", JoinType.INNER_JOIN)
                .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
                .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                .createAlias("program.advert", "advert", JoinType.INNER_JOIN)
                .add(Restrictions.eq("registeredUser.username", username))
                .add(Restrictions.eq("program.enabled", true))
                .add(Restrictions.eq("advert.active", true))
                .add(Restrictions.ge("programClosingDate.closingDate", baselineDate))
                .add(Restrictions.ne("id", selectedAdvertId))
                .setResultTransformer(Transformers.aliasToBean(AdvertDTO.class)).list();
        
        adverts.addAll((List<AdvertDTO>) session.createCriteria(ApplicationFormUserRole.class)
                .setProjection(Projections.projectionList()
                        .add(Projections.groupProperty("advert.id"), "id")
                        .add(Projections.groupProperty("advert.title"), "title")
                        .add(Projections.groupProperty("advert.description"), "description")
                        .add(Projections.groupProperty("advert.studyDuration"), "studyDuration")
                        .add(Projections.groupProperty("advert.funding"), "funding")
                        .add(Projections.property("program.code"), "programCode")
                        .add(Projections.min("closingDate.date"), "programClosingDate")
                        .add(Projections.groupProperty("project.primarySupervisor"), "primarySupervisor")
                        .add(Projections.property("project.id"), "projectId")
                        .add(Projections.groupProperty("project.secondarySupervisor"), "secondarySupervisor"))
                .createAlias("user", "registeredUser", JoinType.INNER_JOIN)
                .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
                .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                .createAlias("program.projects", "project", JoinType.INNER_JOIN)
                .createAlias("project.advert", "advert", JoinType.INNER_JOIN)
                .add(Restrictions.eq("registeredUser.username", username))
                .add(Restrictions.eq("program.enabled", true))
                .add(Restrictions.eq("project.disabled", false))
                .add(Restrictions.eq("advert.active", true))
                .add(Restrictions.ge("programClosingDate.closingDate", baselineDate))
                .add(Restrictions.ne("id", selectedAdvertId))
                .setResultTransformer(Transformers.aliasToBean(AdvertDTO.class)).list());
        
        return adverts;
    }
    
    public AdvertDTO getAdvertDTOByAdvertId(String advertId) {
        AdvertDTO advertDTO = (AdvertDTO) sessionFactory.getCurrentSession().createCriteria(Advert.class)
                .add(Restrictions.eq("id", Integer.parseInt(advertId)))
                .setResultTransformer(Transformers.aliasToBean(AdvertDTO.class)).uniqueResult();
        advertDTO.setSelected(true);
        return advertDTO;
    }
    
    public AdvertDTO getAdvertDTOByProgramCode(String code) {
        return (AdvertDTO) sessionFactory.getCurrentSession().createCriteria(Program.class)
                .setProjection(Projections.property("advert"))
                .add(Restrictions.eq("code", code))
                .setResultTransformer(Transformers.aliasToBean(AdvertDTO.class)).uniqueResult();
    }
    
    public AdvertDTO getAdvertDTOByProjectId(Integer projectId) {
        return (AdvertDTO) sessionFactory.getCurrentSession().createCriteria(Project.class)
                .setProjection(Projections.property("advert"))
                .add(Restrictions.eq("id", projectId))
                .setResultTransformer(Transformers.aliasToBean(AdvertDTO.class)).uniqueResult();
    }
    
    private Date getBaselineDate(Date seedDate) {
        DateTime baseline = new DateTime(seedDate);
        DateTime cleanBaseline = new DateTime(baseline.getYear(), baseline.getMonthOfYear(), baseline.getDayOfMonth(), 0, 0, 0);
        return cleanBaseline.toDate();
    }
    
}
