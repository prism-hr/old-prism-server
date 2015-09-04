package com.zuehlke.pgadmissions.dao;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.dto.DepartmentImportedSubjectAreaDTO;
import com.zuehlke.pgadmissions.dto.resource.ResourceTargetRelevanceDTO;

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
                        .add(Projections.groupProperty("id"), "department") //
                        .add(Projections.groupProperty("programSubjectArea.subjectArea.id"), "subjectArea") //
                        .add(Projections.sum("programSubjectArea.relationStrength"), "programRelationStrength") //
                        .add(Projections.property("institutionSubjectArea.relationStrength"), "institutionRelationStrength")) //
                .createAlias("importedPrograms", "program", JoinType.INNER_JOIN) //
                .createAlias("program.programSubjectAreas", "programSubjectArea", JoinType.INNER_JOIN) //
                .createAlias("program.institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("institution.institutionSubjectAreas", "institutionSubjectArea", JoinType.INNER_JOIN) //
                .add(Restrictions.eqProperty("programSubjectArea.subjectArea.id", "institutionSubjectArea.subjectArea.id"))
                .add(Restrictions.eq("id", department.getId())) //
                .setResultTransformer(Transformers.aliasToBean(DepartmentImportedSubjectAreaDTO.class)) //
                .list();
    }

    public List<Department> getDepartmentsByImportedProgram(ImportedProgram importedProgram) {
        return (List<Department>) sessionFactory.getCurrentSession().createCriteria(Department.class) //
                .createAlias("importedPrograms", "importedProgram") //
                .add(Restrictions.eq("importedProgram.id", importedProgram.getId())) //
                .list();
    }

    public List<ResourceTargetRelevanceDTO> getDepartmentsBySubjectAreas(Integer institution, Collection<Integer> subjectAreas) {
        return (List<ResourceTargetRelevanceDTO>) sessionFactory.getCurrentSession().createCriteria(Department.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id"), "resourceId") //
                        .add(Projections.sum("departmentSubjectArea.relationStrength"), "targetingRelevance")) //
                .createAlias("departmentSubjectAreas", "departmentSubjectArea", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("institution.id", institution)) //
                .add(Restrictions.in("departmentSubjectArea.subjectArea.id", subjectAreas)) //
                .setResultTransformer(Transformers.aliasToBean(ResourceTargetRelevanceDTO.class))
                .list();
    }

}
