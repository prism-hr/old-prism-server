package com.zuehlke.pgadmissions.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.dto.DepartmentImportedSubjectAreaDTO;
import com.zuehlke.pgadmissions.dto.resource.ResourceTargetingDTO;
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
                "delete DepartmentImportedSubjectAreas "
                        + "where department = :department") //
                .setParameter("department", department) //
                .executeUpdate();
    }

    public List<DepartmentImportedSubjectAreaDTO> getImportedSubjectAreas(Department department) {
        return (List<DepartmentImportedSubjectAreaDTO>) sessionFactory.getCurrentSession().createCriteria(Department.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id"), "id") //
                        .add(Projections.groupProperty("subjectArea.id"), "subjectArea") //
                        .add(Projections.sum("programSubjectArea.relationStrength"), "programRelationStrength") //
                        .add(Projections.property("institutionSubjectArea.relationStrength"), "institutionRelationStrength")) //
                .createAlias("importedPrograms", "program", JoinType.INNER_JOIN) //
                .createAlias("program.programSubjectAreas", "programSubjectArea", JoinType.INNER_JOIN) //
                .createAlias("program.institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("institution.institutionSubjectAreas", "institutionSubjectArea", JoinType.INNER_JOIN, //
                        Restrictions.eqProperty("programSubjectArea.id", "institutionSubjectArea.id")) //
                .add(Restrictions.eq("id", department.getId())) //
                .setResultTransformer(Transformers.aliasToBean(DepartmentImportedSubjectAreaDTO.class)) //
                .list();

    }

    public List<ResourceTargetingDTO> getDepartments(Advert advert, List<Integer> departments, List<PrismState> activeStates) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Department.class)
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("institution.id"), "institutionId") //
                        .add(Projections.property("institution.name"), "institutionName") //
                        .add(Projections.groupProperty("id"), "departmentId") //
                        .add(Projections.property("name"), "departmentName") //
                        .add(Projections.property("domicile.name"), "addressDomicileName") //
                        .add(Projections.property("address.addressLine1"), "addressLine1") //
                        .add(Projections.property("address.addressLine2"), "addressLine2") //
                        .add(Projections.property("address.addressTown"), "addressTown") //
                        .add(Projections.property("address.addressRegion"), "addressRegion") //
                        .add(Projections.property("address.addressCode"), "addressCode") //
                        .add(Projections.property("address.googleId"), "addressGoogleId") //
                        .add(Projections.property("address.addressCoordinates.latitude"), "addressCoordinateLatitude") //
                        .add(Projections.property("address.addressCoordinates.longitude"), "addressCoordinateLongitude") //
                        .add(Projections.property("advertSelectedResource.id"), "advertSelectedResourceId") //
                        .add(Projections.property("advertSelectedResource.endorsed"), "endorsed")) //
                .createAlias("institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.address", "address", JoinType.INNER_JOIN) //
                .createAlias("address.domicile", "domicile", JoinType.INNER_JOIN) //
                .createAlias("advertSelectedResources", "advertSelectedResource", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.eq("advertSelectedResource.advert", advert));

        if (activeStates != null) {
            criteria.createAlias("resourceStates", "resourceState") //
                    .add(Restrictions.in("resourceState.state.id", activeStates));
        }

        return (List<ResourceTargetingDTO>) criteria.add(Restrictions.in("id", departments)) //
                .setResultTransformer(Transformers.aliasToBean(ResourceTargetingDTO.class))
                .list();
    }

}
