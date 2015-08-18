package com.zuehlke.pgadmissions.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.dto.DepartmentImportedSubjectAreaDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;

@Repository
@SuppressWarnings("unchecked")
public class DepartmentDAO {

    @Inject
    private SessionFactory sessionFactory;

    public List<ResourceRepresentationSimple> getDepartments(Institution institution) {
        return (List<ResourceRepresentationSimple>) sessionFactory.getCurrentSession().createCriteria(Department.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "id") //
                        .add(Projections.property("name"), "name")) //
                .add(Restrictions.eq("institution", institution)) //
                .setResultTransformer(Transformers.aliasToBean(ResourceRepresentationSimple.class))
                .list();
    }

    public void deleteDepartmentImportedSubjectAreas(Department department) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete DepartmentImportedSubjectArea "
                        + "where department = :department") //
                .setParameter("department", department) //
                .executeUpdate();
    }

    public List<DepartmentImportedSubjectAreaDTO> getImportedSubjectAreas(Department department) {
        return (List<DepartmentImportedSubjectAreaDTO>) sessionFactory.getCurrentSession().createCriteria(Department.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id"), "id") //
                        .add(Projections.groupProperty("programSubjectArea.id"), "subjectArea") //
                        .add(Projections.sum("programSubjectArea.relationStrength"), "programRelationStrength") //
                        .add(Projections.property("institutionSubjectArea.relationStrength"), "institutionRelationStrength")) //
                .createAlias("importedPrograms", "program", JoinType.INNER_JOIN) //
                .createAlias("program.programSubjectAreas", "programSubjectArea", JoinType.INNER_JOIN) //
                .createAlias("program.institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("institution.institutionSubjectAreas", "institutionSubjectArea", JoinType.INNER_JOIN) //
                .add(Restrictions.eqProperty("programSubjectArea.id", "institutionSubjectArea.id"))
                .add(Restrictions.eq("id", department.getId())) //
                .setResultTransformer(Transformers.aliasToBean(DepartmentImportedSubjectAreaDTO.class)) //
                .list();
    }

}
