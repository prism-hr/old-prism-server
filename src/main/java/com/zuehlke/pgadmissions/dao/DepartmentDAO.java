package com.zuehlke.pgadmissions.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.resource.department.Department;

@Repository
@SuppressWarnings("unchecked")
public class DepartmentDAO {

    @Inject
    private SessionFactory sessionFactory;

    public List<Integer> getDepartments(Integer institution) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Department.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.eq("institution.id", institution)) //
                .list();
    }

}
