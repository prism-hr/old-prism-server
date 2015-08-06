package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_DISABLED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_WITHDRAWN;

import java.util.Arrays;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyLocation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;

@Repository
@SuppressWarnings("unchecked")
public class ProgramDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public Program getProgramByCode(String code) {
        return (Program) sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .setFetchMode("studyOptions", FetchMode.JOIN) //
                .add(Restrictions.eq("code", code)) //
                .uniqueResult();
    }

    public Program getProgramByImportedCode(Institution institution, String importedCode) {
        return (Program) sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .setFetchMode("studyOptions", FetchMode.JOIN) //
                .add(Restrictions.eq("institution", institution)) //
                .add(Restrictions.eq("importedCode", importedCode)) //
                .uniqueResult();
    }

    public List<ResourceRepresentationSimple> getApprovedPrograms(Integer institutionId) {
        return (List<ResourceRepresentationSimple>) sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id")) //
                        .add(Projections.property("name"))) //
                .createAlias("resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("institution.id", institutionId)) //
                .add(Restrictions.eq("resourceState.state.id", PROGRAM_APPROVED)) //
                .setResultTransformer(Transformers.aliasToBean(ResourceRepresentationSimple.class)) //
                .list();
    }

    public List<ResourceRepresentationSimple> getSimilarPrograms(Integer institutionId, String searchTerm) {
        return (List<ResourceRepresentationSimple>) sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "id") //
                        .add(Projections.property("name"), "name")) //
                .add(Restrictions.eq("institution.id", institutionId)) //
                .add(Restrictions.not( //
                        Restrictions.in("state.id", Arrays.asList(PROGRAM_REJECTED, PROGRAM_WITHDRAWN, PROGRAM_DISABLED_COMPLETED)))) //
                .add(Restrictions.ilike("name", searchTerm, MatchMode.ANYWHERE)) //
                .addOrder(Order.desc("name")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceRepresentationSimple.class)) //
                .list();
    }

    public List<String> getSuggestedDivisions(Program program, String location) {
        return (List<String>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.groupProperty("studyDetail.studyDivision")) //
                .add(Restrictions.eq("program", program)) //
                .add(Restrictions.eq("studyDetail.studyLocation", location)) //
                .add(Subqueries.in(location, DetachedCriteria.forClass(ResourceStudyLocation.class) //
                        .setProjection(Projections.property("studyLocation")) //
                        .add(Restrictions.eq("program", program)))) //
                .list();
    }

    public List<String> getSuggestedStudyAreas(Program program, String location, String division) {
        return (List<String>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.groupProperty("studyDetail.studyArea")) //
                .add(Restrictions.eq("program", program)) //
                .add(Restrictions.eq("studyDetail.studyLocation", location)) //
                .add(Restrictions.eq("studyDetail.studyDivision", division)) //
                .add(Subqueries.in(location, DetachedCriteria.forClass(ResourceStudyLocation.class) //
                        .setProjection(Projections.property("studyLocation")) //
                        .add(Restrictions.eq("program", program)))) //
                .list();
    }

    public List<Integer> getProjects(Integer program) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Project.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.eq("program.id", program)) //
                .list();
    }

    public List<Integer> getApplications(Integer program) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.eq("program.id", program)) //
                .list();
    }

}
