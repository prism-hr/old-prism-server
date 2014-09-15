package com.zuehlke.pgadmissions.services;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.definitions.FilterExpression;
import com.zuehlke.pgadmissions.domain.definitions.FilterMatchMode;
import com.zuehlke.pgadmissions.domain.definitions.FilterProperty;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterConstraintDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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

    public void save(User user, Scope scope, ResourceListFilterDTO filterDTO) throws DeduplicationException {
        ResourceListFilter transientFilter = new ResourceListFilter().withUserAccount(user.getUserAccount()).withScope(scope)
                .withMatchMode(filterDTO.getMatchMode()).withSortOrder(filterDTO.getSortOrder()).withUrgentOnly(filterDTO.getUrgentOnly());
        ResourceListFilter persistentFilter = entityService.createOrUpdate(transientFilter);

        for (int i = 0; i < filterDTO.getConstraints().size(); i++) {
            ResourceListFilterConstraintDTO constraintDTO = filterDTO.getConstraints().get(i);
            FilterProperty filterProperty = constraintDTO.getFilterProperty();

            ResourceListFilterConstraint transientConstraint = new ResourceListFilterConstraint().withFilter(persistentFilter)
                    .withFilterProperty(filterProperty).withFilterExpression(constraintDTO.getFilterExpression()).withNegated(constraintDTO.getNegated())
                    .withDisplayPosition(i).withValueString(constraintDTO.getValueString())
                    .withValueDateStart(constraintDTO.getValueDateStart()).withValueDateClose(constraintDTO.getValueDateClose())
                    .withValueDecimalStart(constraintDTO.getValueDecimalStart()).withValueDecimalClose(constraintDTO.getValueDecimalClose());

            if (filterProperty == FilterProperty.STATE_GROUP) {
                transientConstraint.setValueStateGroup(stateService.getStateGroupById(constraintDTO.getValueStateGroup()));
            } else if (filterProperty == FilterProperty.USER_ROLE) {
                for (PrismRole roleId : constraintDTO.getValueRoles()) {
                    transientConstraint.addValueRole(roleService.getById(roleId));
                }
            }

            persistentFilter.getConstraints().add(transientConstraint);
            entityService.save(transientConstraint);
        }

        user.getUserAccount().getFilters().put(scope, persistentFilter);
    }

    public ResourceListFilterDTO getByUserAndScope(User user, Scope scope) {
        Map<Scope, ResourceListFilter> filters = user.getUserAccount().getFilters();

        if (filters.containsKey(scope)) {
            ResourceListFilter filter = filters.get(scope);
            ResourceListFilterDTO filterDTO = new ResourceListFilterDTO().withUrgentOnly(filter.isUrgentOnly()).withMatchMode(filter.getMatchMode())
                    .withSortOrder(filter.getSortOrder()).withValueString(filter.getValueString());

            List<ResourceListFilterConstraintDTO> constraints = Lists.newArrayListWithCapacity(filter.getConstraints().size());
            for (ResourceListFilterConstraint constraint : filter.getConstraints()) {
                FilterProperty filterProperty = constraint.getFilterProperty();

                ResourceListFilterConstraintDTO constraintDTO = new ResourceListFilterConstraintDTO().withFilterProperty(filterProperty)
                        .withFilterExpression(constraint.getFilterExpression()).withNegated(constraint.isNegated())
                        .withDisplayPosition(constraint.getDisplayPosition()).withValueString(constraint.getValueString())
                        .withValueStateGroup(constraint.getValueStateGroup() != null ? constraint.getValueStateGroup().getId() : null)
                        .withValueDateStart(constraint.getValueDateStart()).withValueDateClose(constraint.getValueDateClose())
                        .withValueDecimalStart(constraint.getValueDecimalStart()).withValueDecimalClose(constraint.getValueDecimalClose());

                if (filterProperty == FilterProperty.USER_ROLE) {
                    for (Role role : constraint.getValueRoles()) {
                        constraintDTO.addValueRole(role.getId());
                    }
                }

                constraints.add(constraintDTO);
            }
            filterDTO.setConstraints(constraints);

            return filterDTO;
        }

        return null;
    }

    public ResourceListFilterDTO saveOrGetByUserAndScope(User user, PrismScope scopeId, ResourceListFilterDTO filterDTO) throws DeduplicationException {
        Scope scope = scopeService.getById(scopeId);
        if (filterDTO == null) {
            return getByUserAndScope(user, scope);
        } else {
            prepare(scope, filterDTO);
        }
        return filterDTO;
    }

    private void prepare(Scope scope, ResourceListFilterDTO filterDTO) {
        String valueString = filterDTO.getValueString();
        List<ResourceListFilterConstraintDTO> constraintDTOs = filterDTO.getConstraints();

        if (!Strings.isNullOrEmpty(valueString) && constraintDTOs == null) {
            List<ResourceListFilterConstraintDTO> constraints = Lists.newLinkedList();
            for (FilterProperty property : FilterProperty.getPermittedFilterProperties(scope.getId())) {
                int displayPosition = 0;
                if (property.getPermittedExpressions().contains(FilterExpression.CONTAIN)) {
                    ResourceListFilterConstraintDTO constraintDTO = new ResourceListFilterConstraintDTO().withFilterProperty(property)
                            .withFilterExpression(FilterExpression.CONTAIN).withNegated(false).withDisplayPosition(displayPosition)
                            .withValueString(valueString);
                    constraints.add(constraintDTO);
                    displayPosition++;
                }
            }
            filterDTO.setConstraints(constraints);
            filterDTO.withMatchMode(FilterMatchMode.ANY);
        }
    }

}
