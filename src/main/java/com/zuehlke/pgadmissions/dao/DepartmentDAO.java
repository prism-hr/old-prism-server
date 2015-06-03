package com.zuehlke.pgadmissions.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.department.Department;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.rest.representation.resource.DepartmentRepresentation;

@Repository
public class DepartmentDAO {

    @Inject
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public List<DepartmentRepresentation> getDepartments(Institution institution) {
        return (List<DepartmentRepresentation>) sessionFactory.getCurrentSession().createCriteria(Department.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "id") //
                        .add(Projections.property("title"), "title")) //
                .add(Restrictions.eq("institution", institution)) //
                .setResultTransformer(Transformers.aliasToBean(DepartmentRepresentation.class))
                .list();
    }

}
