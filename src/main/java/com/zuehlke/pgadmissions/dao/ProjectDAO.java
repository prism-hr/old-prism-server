package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.dto.ResourceSearchEngineDTO;
import com.zuehlke.pgadmissions.dto.SearchEngineAdvertDTO;
import com.zuehlke.pgadmissions.dto.SitemapEntryDTO;

@Repository
@SuppressWarnings("unchecked")
public class ProjectDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public DateTime getLatestUpdatedTimestampSitemap(List<PrismState> states) {
        return (DateTime) sessionFactory.getCurrentSession().createCriteria(Project.class) //
                .setProjection(Projections.property("updatedTimestampSitemap")) //
                .add(Restrictions.in("state.id", states)) //
                .add(Restrictions.isNotEmpty("resourceConditions")) //
                .addOrder(Order.desc("updatedTimestampSitemap")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<SitemapEntryDTO> getSitemapEntries(List<PrismState> states) {
        return (List<SitemapEntryDTO>) sessionFactory.getCurrentSession().createCriteria(Project.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "resourceId") //
                        .add(Projections.property("updatedTimestampSitemap"), "lastModifiedTimestamp")) //
                .add(Restrictions.in("state.id", states)) //
                .add(Restrictions.isNotEmpty("resourceConditions")) //
                .addOrder(Order.desc("updatedTimestampSitemap")) //
                .setMaxResults(50000) //
                .setResultTransformer(Transformers.aliasToBean(SitemapEntryDTO.class)) //
                .list();
    }

    public SearchEngineAdvertDTO getSearchEngineAdvert(Integer projectId, List<PrismState> states) {
        return (SearchEngineAdvertDTO) sessionFactory.getCurrentSession().createCriteria(Project.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "projectId") //
                        .add(Projections.property("advert.name"), "projectTitle") //
                        .add(Projections.property("advert.summary"), "projectSummary") //
                        .add(Projections.property("advert.description"), "projectDescription") //
                        .add(Projections.property("program.id"), "programId") //
                        .add(Projections.property("programAdvert.name"), "programTitle") //
                        .add(Projections.property("programAdvert.summary"), "programSummary") //
                        .add(Projections.property("programAdvert.description"), "programDescription") //
                        .add(Projections.property("institution.id"), "institutionId") //
                        .add(Projections.property("institutionAdvert.name"), "institutionTitle") //
                        .add(Projections.property("institutionAdvert.summary"), "institutionSummary") //
                        .add(Projections.property("institutionAdvert.homepage"), "institutionHomepage")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("program", "program", JoinType.INNER_JOIN) //
                .createAlias("program.advert", "programAdvert", JoinType.INNER_JOIN) //
                .createAlias("institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("institution.advert", "institutionAdvert", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("id", projectId)) //
                .add(Restrictions.in("state.id", states)) //
                .add(Restrictions.isNotEmpty("resourceConditions")) //
                .setResultTransformer(Transformers.aliasToBean(SearchEngineAdvertDTO.class)) //
                .uniqueResult();
    }

    public List<ResourceSearchEngineDTO> getActiveProjectsByProgram(Integer programId, List<PrismState> states) {
        return (List<ResourceSearchEngineDTO>) sessionFactory.getCurrentSession().createCriteria(Project.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "id") //
                        .add(Projections.property("name"), "name")) //
                .add(Restrictions.eq("program.id", programId)) //
                .add(Restrictions.in("state.id", states)) //
                .add(Restrictions.isNotEmpty("resourceConditions")) //
                .addOrder(Order.desc("updatedTimestampSitemap")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceSearchEngineDTO.class)) //
                .list();
    }

    public List<ResourceSearchEngineDTO> getActiveProjectsByInstitution(Integer institutionId, List<PrismState> states) {
        return (List<ResourceSearchEngineDTO>) sessionFactory.getCurrentSession().createCriteria(Project.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "id") //
                        .add(Projections.property("name"), "name")) //
                .add(Restrictions.eq("institution.id", institutionId)) //
                .add(Restrictions.in("state.id", states)) //
                .add(Restrictions.isNotEmpty("resourceConditions")) //
                .addOrder(Order.desc("updatedTimestampSitemap")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceSearchEngineDTO.class)) //
                .list();
    }

}
