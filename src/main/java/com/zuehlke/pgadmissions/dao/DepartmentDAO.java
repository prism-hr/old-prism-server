package com.zuehlke.pgadmissions.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;

@Repository
public class DepartmentDAO {

    @Inject
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public List<ResourceRepresentationSimple> getDepartments(Institution institution) {
        return (List<ResourceRepresentationSimple>) sessionFactory.getCurrentSession().createCriteria(Department.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "id") //
                        .add(Projections.property("title"), "title")) //
                .add(Restrictions.eq("institution", institution)) //
                .setResultTransformer(Transformers.aliasToBean(ResourceRepresentationSimple.class))
                .list();
    }

}
