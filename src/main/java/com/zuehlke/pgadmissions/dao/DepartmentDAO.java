package com.zuehlke.pgadmissions.dao;

import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.DEPARTMENT_APPROVED;

@Repository
public class DepartmentDAO {

    @Inject
    private SessionFactory sessionFactory;

    public List<ResourceRepresentationSimple> getDepartments(Institution institution) {
        return (List<ResourceRepresentationSimple>) sessionFactory.getCurrentSession().createCriteria(Department.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "id") //
                        .add(Projections.property("title"), "title")) //
                .add(Restrictions.eq("institution", institution)) //
                .setResultTransformer(Transformers.aliasToBean(ResourceRepresentationSimple.class))
                .list();
    }

    public List<Department> getDepartments(String searchTerm) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Department.class);
        if (searchTerm != null) {
            criteria.add(Restrictions.ilike("title", searchTerm, MatchMode.ANYWHERE));
        }
        return criteria
                .add(Restrictions.eq("state.id", DEPARTMENT_APPROVED))
                .setMaxResults(10)
                .list();
    }

}
