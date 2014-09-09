package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.ResourceListFilter;
import com.zuehlke.pgadmissions.domain.ResourceListFilterConstraint;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.FilterProperty;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterConstraintDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;

@Service
@Transactional
public class ResourceListFilterService {

    @Autowired
    private EntityService entityService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ScopeService scopeService;

    @Autowired
    private StateService stateService;

    public <T extends Resource> void save(User user, Scope scope, ResourceListFilterDTO filterDTO) throws DeduplicationException {
        ResourceListFilter transientFilter = new ResourceListFilter().withUserAccount(user.getUserAccount()).withScope(scope)
                .withMatchMode(filterDTO.getMatchMode()).withSortOrder(filterDTO.getSortOrder());
        ResourceListFilter persistentFilter = entityService.createOrUpdate(transientFilter);

        persistentFilter.getConstraints().clear();
        List<ResourceListFilterConstraintDTO> constraints = filterDTO.getConstraints();

        for (ResourceListFilterConstraintDTO constraint : constraints) {
            FilterProperty filterProperty = constraint.getFilterProperty();

            ResourceListFilterConstraint transientConstraint = new ResourceListFilterConstraint().withFilter(persistentFilter)
                    .withFilterProperty(filterProperty).withFilterExpression(constraint.getFilterExpression()).withNegated(constraint.isNegated())
                    .withDisplayPosition(constraint.getDisplayPosition()).withValueString(constraint.getValueString())
                    .withValueDateStart(constraint.getValueDateStart()).withValueDateClose(constraint.getValueDateClose())
                    .withValueDecimalStart(constraint.getValueDecimalStart()).withValueDecimalClose(constraint.getValueDecimalClose());

            if (filterProperty == FilterProperty.STATE_GROUP) {
                transientConstraint.setValueStateGroup(stateService.getStateGroupById(constraint.getValueStateGroup()));
            } else if (filterProperty == FilterProperty.USER_ROLE) {
                for (PrismRole roleId : constraint.getValueRoles()) {
                    transientConstraint.addValueRole(roleService.getById(roleId));
                }
            }

            entityService.save(transientConstraint);
            persistentFilter.getConstraints().add(transientConstraint);
        }

        user.getUserAccount().getFilters().put(scope, persistentFilter);
    }

    public ResourceListFilterDTO getByUserAndScope(User user, Scope scope) {
        HashMap<Scope, ResourceListFilter> filters = user.getUserAccount().getFilters();

        if (filters.containsKey(scope)) {
            ResourceListFilter filter = filters.get(scope);
            ResourceListFilterDTO filterDTO = new ResourceListFilterDTO().withUrgentOnly(filter.isUrgentOnly()).withMatchMode(filter.getMatchMode())
                    .withSortOrder(filter.getSortOrder());

            for (ResourceListFilterConstraint constraint : filter.getConstraints()) {
                FilterProperty filterProperty = constraint.getFilterProperty();

                ResourceListFilterConstraintDTO constraintDTO = new ResourceListFilterConstraintDTO().withFilterProperty(filterProperty)
                        .withFilterExpression(constraint.getFilterExpression()).withNegated(constraint.isNegated())
                        .withDisplayPosition(constraint.getDisplayPosition()).withValueString(constraint.getValueString())
                        .withValueStateGroup(constraint.getValueStateGroup().getId()).withValueDateStart(constraint.getValueDateStart())
                        .withValueDateClose(constraint.getValueDateClose()).withValueDecimalStart(constraint.getValueDecimalStart())
                        .withValueDecimalClose(constraint.getValueDecimalClose());

                if (filterProperty == FilterProperty.USER_ROLE) {
                    for (Role role : constraint.getValueRoles()) {
                        constraintDTO.addValueRole(role.getId());
                    }
                }

                filterDTO.addConstraint(constraintDTO);
            }

            return filterDTO;
        }

        return null;
    }

    public ResourceListFilterDTO saveOrGetByUserAndScope(User user, PrismScope scopeId, ResourceListFilterDTO filterDTO) throws DeduplicationException {
        Scope scope = scopeService.getById(scopeId);
        if (filterDTO == null) {
            return getByUserAndScope(user, scope);
        } else if (filterDTO.isSaveAsDefaultFilter()) {
            save(user, scope, filterDTO);
        }
        return filterDTO;
    }

}
