package com.zuehlke.pgadmissions.dao;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVED;

@Repository
public class ProgramDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
    public List<Integer> getProjects(Integer program) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Project.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.eq("program.id", program)) //
                .list();
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getApplications(Integer program) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.eq("program.id", program)) //
                .list();
    }

}
