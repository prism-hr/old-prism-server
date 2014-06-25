package com.zuehlke.pgadmissions.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramExport;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.enums.OpportunityListType;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.dto.AdvertDTO;

@Repository
@SuppressWarnings("unchecked")
public class AdvertDAO {
    
    private static final BigDecimal RECOMMENDED_ADVERT_FEED_THRESHOLD = new BigDecimal(0.15);
    
    @Autowired
    private SessionFactory sessionFactory;

    public List<AdvertDTO> getAdvertFeed(OpportunityListType feedKey, String feedKeyValue, Integer selectedAdvertId) {
        if (selectedAdvertId == null) {
            selectedAdvertId = 0;
        }
        
        Session session = sessionFactory.getCurrentSession();
        Criteria programQuery = null;
        Criteria projectQuery = null;
        ProjectionList programProjectionList = Projections.projectionList();
        ProjectionList projectProjectionList = Projections.projectionList();
        
        if (feedKey == null) {
            programQuery = getProgramAdvertCriteria(session);
            projectQuery = getProjectAdvertCriteria(session);
        } else if (BooleanUtils.isTrue(OpportunityListType.isCurrentOpportunityListType(feedKey))) {
            programProjectionList.add(Projections.property("program.active"), "selected");
            projectProjectionList.add(Projections.property("project.active"), "selected");
            
            if (feedKey == OpportunityListType.CURRENTOPPORTUNITYBYADVERTID) {
                programQuery = getProgramAdvertByAdvertIdCriteria(session, feedKeyValue);
                projectQuery = getProjectAdvertByAdvertIdCriteria(session, feedKeyValue);
            } else if (feedKey == OpportunityListType.CURRENTOPPORTUNITYBYAPPLICATIONFORMID) {
                programQuery = getProgramAdvertByApplicationFormIdCriteria(session, feedKeyValue);
                projectQuery = getProjectAdvertByApplicationFormIdCriteria(session, feedKeyValue);
            }
            
        } else if (feedKey == OpportunityListType.OPPORTUNITIESBYFEEDID) {
            programQuery = getProgramAdvertByFeedIdCriteria(session, feedKeyValue);
            projectQuery = getProjectAdvertByFeedIdCriteria(session, feedKeyValue);
        } else if (feedKey == OpportunityListType.OPPORTUNITIESBYUSERUPI) {
            programQuery = getProgramAdvertByUserUpiCriteria(session, feedKeyValue);
            projectQuery = getProjectAdvertByUserUpiCriteria(session, feedKeyValue);
        } else if (feedKey == OpportunityListType.OPPORTUNITIESBYUSERUSERNAME) {
            programQuery = getProgramAdvertByUserUsernameCriteria(session, feedKeyValue);
            projectQuery = getProjectAdvertByUserUsernameCriteria(session, feedKeyValue);
        } else if (feedKey == OpportunityListType.RECOMMENDEDOPPORTUNTIIESBYAPPLICANTID) {
//            return getRecommendedAdvertFeed(feedKeyValue);
        } 
        
        DateTime baseline = new DateTime(new Date());
        DateTime cleanBaseline = new DateTime(baseline.getYear(), baseline.getMonthOfYear(), baseline.getDayOfMonth(), 0, 0, 0);
        Date baselineDate = cleanBaseline.toDate();
        
        List<AdvertDTO> advertDTOs = getProgramAdvertDTOs(programQuery, programProjectionList, baselineDate, selectedAdvertId);
        
        if (!(BooleanUtils.isTrue(OpportunityListType.isSingletonOpportunityListType(feedKey)) && advertDTOs.size() == 1)) {        
            advertDTOs.addAll(getProjectAdvertDTOs(projectQuery, projectProjectionList, baselineDate, selectedAdvertId));
        }
            
        return advertDTOs;
    }
    
    private Criteria getProgramAdvertCriteria(Session session) {
        return session.createCriteria(Program.class, "program");
    }
    
    private Criteria getProjectAdvertCriteria(Session session) {
        return getProgramAdvertCriteria(session)
                .createAlias("program.projects", "project", JoinType.INNER_JOIN);
    }
    
    private Criteria getProgramAdvertByAdvertIdCriteria(Session session, String feedKeyValue) {
        return getProgramAdvertCriteria(session)
                .add(Restrictions.eq("id", Integer.parseInt(feedKeyValue)));
    }
    
    private Criteria getProjectAdvertByAdvertIdCriteria(Session session, String feedKeyValue) {
        return getProjectAdvertCriteria(session)
                .add(Restrictions.eq("project.id", Integer.parseInt(feedKeyValue)));
    }
    
    private Criteria getProgramAdvertByApplicationFormIdCriteria(Session session, String feedKeyValue) {
        return session.createCriteria(Application.class, "applicationForm")
                .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                .add(Restrictions.eq("applicationForm.id", Integer.parseInt(feedKeyValue)));
    }
    
    private Criteria getProjectAdvertByApplicationFormIdCriteria(Session session, String feedKeyValue) {
        return getProgramAdvertByApplicationFormIdCriteria(session, feedKeyValue)
                .createAlias("applicationForm.project", "project");
    }
    
    private Criteria getProgramAdvertByFeedIdCriteria(Session session, String feedKeyValue) {
        return session.createCriteria(ProgramExport.class)
                .createAlias("programs", "program", JoinType.INNER_JOIN)
                .add(Restrictions.eq("id", Integer.parseInt(feedKeyValue)));
    }
    
    private Criteria getProjectAdvertByFeedIdCriteria(Session session, String feedKeyValue) {
        return getProgramAdvertByFeedIdCriteria(session, feedKeyValue)
                .createAlias("program.projects", "project", JoinType.INNER_JOIN);
    }
    
    private Criteria getProgramAdvertByUserUpiCriteria(Session session, String feedKeyValue) {
        return getAdvertByUserAttributeCriteria(session)
                .add(Restrictions.eq("registeredUser.upi", feedKeyValue)); 
    }
    
    private Criteria getProjectAdvertByUserUpiCriteria(Session session, String feedKeyValue) {
        return getProgramAdvertByUserUpiCriteria(session, feedKeyValue)
                .createAlias("program.projects", "project", JoinType.INNER_JOIN);
    }
    
    private Criteria getProgramAdvertByUserUsernameCriteria(Session session, String feedKeyValue) {
        return getAdvertByUserAttributeCriteria(session)
                .add(Restrictions.eq("registeredUser.username", feedKeyValue));      
    }
    
    private Criteria getProjectAdvertByUserUsernameCriteria(Session session, String feedKeyValue) {
        return getProgramAdvertByUserUsernameCriteria(session, feedKeyValue)
                .createAlias("program.projects", "project", JoinType.INNER_JOIN);
    }
    
    private Criteria getAdvertByUserAttributeCriteria (Session session) {
        return session.createCriteria(UserRole.class)
                .createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
                .createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
                .createAlias("user", "registeredUser", JoinType.INNER_JOIN);
    }
    
    private List<AdvertDTO> getProgramAdvertDTOs(Criteria programQuery, ProjectionList programProjectionList, Date baselineDate, Integer selectedAdvertId) {
        return (List<AdvertDTO>) programQuery
                .setProjection(programProjectionList.add(Projections.groupProperty("program.id"), "id")
                        .add(Projections.property("program.title"), "title")
                        .add(Projections.property("program.description"), "description")
                        .add(Projections.property("program.studyDuration"), "studyDuration")
                        .add(Projections.property("program.funding"), "funding")
                        .add(Projections.property("program.code"), "programCode")
                        .add(Projections.min("closingDate.closingDate"), "closingDate")
                        .add(Projections.property("user.firstName"), "primarySupervisorFirstName")
                        .add(Projections.property("user.lastName"), "primarySupervisorLastName")
                        .add(Projections.property("user.email"), "primarySupervisorEmail")
                        .add(Projections.property("program.advertType"), "advertType"))
                .createAlias("program.user", "user", JoinType.INNER_JOIN)
                .createAlias("program.closingDates", "closingDate", JoinType.LEFT_OUTER_JOIN)
                .add(Restrictions.eq("program.state.id", PrismState.PROGRAM_APPROVED))
                .add(Restrictions.disjunction()
                        .add(Restrictions.isNull("closingDate.id"))
                        .add(Restrictions.ge("closingDate.closingDate", baselineDate)))
                .add(Restrictions.ne("program.id", selectedAdvertId))
                .setResultTransformer(Transformers.aliasToBean(AdvertDTO.class)).list();
    }
    
    private List<AdvertDTO> getProjectAdvertDTOs(Criteria projectQuery, ProjectionList projectProjectionList, Date baselineDate, Integer selectedAdvertId) {
        return (List<AdvertDTO>) projectQuery
                .setProjection(projectProjectionList.add(Projections.groupProperty("project.id"), "id")
                        .add(Projections.property("project.title"), "title")
                        .add(Projections.property("project.description"), "description")
                        .add(Projections.property("project.studyDuration"), "studyDuration")
                        .add(Projections.property("project.funding"), "funding")
                        .add(Projections.property("program.code"), "programCode")
                        .add(Projections.property("project.closingDate"), "closingDate")
                        .add(Projections.property("primarySupervisor.firstName"), "primarySupervisorFirstName")
                        .add(Projections.property("primarySupervisor.lastName"), "primarySupervisorLastName")
                        .add(Projections.property("primarySupervisor.email"), "primarySupervisorEmail")
                        .add(Projections.property("project.advertType"), "advertType")
                        .add(Projections.property("secondarySupervisor.firstName"), "secondarySupervisorFirstName")
                        .add(Projections.property("secondarySupervisor.lastName"), "secondarySupervisorLastName"))
                .createAlias("project.primarySupervisor", "primarySupervisor", JoinType.INNER_JOIN)
                .createAlias("project.secondarySupervisor", "secondarySupervisor", JoinType.LEFT_OUTER_JOIN)
                .add(Restrictions.eq("project.state.id", PrismState.PROJECT_APPROVED))
                .add(Restrictions.ne("project.id", selectedAdvertId))
                .setResultTransformer(Transformers.aliasToBean(AdvertDTO.class)).list();
    }

}
