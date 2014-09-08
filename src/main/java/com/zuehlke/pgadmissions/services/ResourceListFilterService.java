package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
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
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.AbstractFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.DateFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.DecimalFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.StateGroupFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.StringFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.UserRoleFilterDTO;

@Service
@Transactional
@SuppressWarnings("unchecked")
public class ResourceListFilterService {

    @Autowired
    private EntityService entityService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ScopeService scopeService;

    @Autowired
    private StateService stateService;

    public <T extends Resource> void save(User user, Class<T> resourceClass, ResourceListFilterDTO filterDTO) throws DeduplicationException {
        Scope filterScope = scopeService.getById(PrismScope.getResourceScope(resourceClass));

        ResourceListFilter transientFilter = new ResourceListFilter().withUserAccount(user.getUserAccount()).withScope(filterScope)
                .withMatchMode(filterDTO.getMatchMode()).withSortOrder(filterDTO.getSortOrder());
        ResourceListFilter persistentFilter = entityService.createOrUpdate(transientFilter);

        persistentFilter.getConstraints().clear();
        HashMap<String, Object> filters = filterDTO.getFilterConstraints();

        for (String propertyName : filters.keySet()) {
            for (AbstractFilterDTO abstractConstraintDTO : (List<AbstractFilterDTO>) filters.get(propertyName)) {
                FilterProperty filterProperty = FilterProperty.getByPropertyName(propertyName);
                ResourceListFilterConstraint transientConstraint = new ResourceListFilterConstraint().withFilter(persistentFilter)
                        .withFilterProperty(filterProperty).withNegated(abstractConstraintDTO.isNegated());

                Class<? extends AbstractFilterDTO> constraintClass = abstractConstraintDTO.getClass();

                if (constraintClass.equals(StringFilterDTO.class)) {
                    saveStringFilterConstraint(transientConstraint, abstractConstraintDTO);
                } else if (constraintClass.equals(StateGroupFilterDTO.class)) {
                    saveStateGroupFilterConstraint(transientConstraint, abstractConstraintDTO);
                } else if (constraintClass.equals(DateFilterDTO.class)) {
                    saveDateFilterConstraint(transientConstraint, abstractConstraintDTO);
                } else if (constraintClass.equals(DecimalFilterDTO.class)) {
                    saveDecimalFilterConstraint(transientConstraint, abstractConstraintDTO);
                } else {
                    saveUserRoleFilterConstraint(transientConstraint, abstractConstraintDTO);
                }

                entityService.save(transientConstraint);
                persistentFilter.getConstraints().add(transientConstraint);
            }
        }

        user.getUserAccount().getFilters().put(filterScope, persistentFilter);
    }

    public ResourceListFilterDTO getByUserAndScope(User user, Scope scope) {
        ResourceListFilter filter = user.getUserAccount().getFilters().get(scope);
        
        if (filter == null) {
            return null;
        }
        
        ResourceListFilterDTO filterDTO = new ResourceListFilterDTO().withUrgentOnly(filter.isUrgentOnly()).withMatchMode(filter.getMatchMode())
                .withSortOrder(filter.getSortOrder());
        
        for (ResourceListFilterConstraint constraint : filter.getConstraints()) {
            FilterProperty filterProperty = constraint.getFilterProperty();
            
            Class<? extends AbstractFilterDTO> constraintClass = filterProperty.getFilterClass();

            if (constraintClass.equals(StringFilterDTO.class)) {
                filterDTO.addStringFilter(filterProperty, constraint.getFilterTermString(), constraint.isNegated());
            } else if (constraintClass.equals(StateGroupFilterDTO.class)) {
                filterDTO.addStateGroupFilter(filterProperty, constraint.getFilterTermStateGroup().getId(), constraint.isNegated());
            } else if (constraintClass.equals(DateFilterDTO.class)) {
                filterDTO.addDateFilter(filterProperty, constraint.getFilterTermDateStart(), constraint.getFilterTermDateClose(), constraint.isNegated());
            } else if (constraintClass.equals(DecimalFilterDTO.class)) {
                filterDTO.addDecimalFilter(filterProperty, constraint.getFilterTermDecimalStart(), constraint.getFilterTermDecimalClose(), constraint.isNegated());
            } else {
                filterDTO.addUserRoleFilter(filterProperty, getFilterConstraintRoles(constraint), constraint.isNegated());
            }
        }
        
        return filterDTO;
    }

    private void saveStringFilterConstraint(ResourceListFilterConstraint transientConstraint, AbstractFilterDTO abstractConstraintDTO) {
        StringFilterDTO concreteConstraintDTO = (StringFilterDTO) abstractConstraintDTO;
        transientConstraint.setFilterTermString(concreteConstraintDTO.getString());
    }

    private void saveStateGroupFilterConstraint(ResourceListFilterConstraint transientConstraint, AbstractFilterDTO abstractConstraintDTO) {
        StateGroupFilterDTO concreteConstraintDTO = (StateGroupFilterDTO) abstractConstraintDTO;
        transientConstraint.setFilterTermStateGroup(stateService.getStateGroupById(concreteConstraintDTO.getStateGroup()));
    }

    private void saveDateFilterConstraint(ResourceListFilterConstraint transientConstraint, AbstractFilterDTO abstractConstraintDTO) {
        DateFilterDTO concreteConstraintDTO = (DateFilterDTO) abstractConstraintDTO;
        transientConstraint.setFilterTermDateStart(concreteConstraintDTO.getRangeStart());
        transientConstraint.setFilterTermDateClose(concreteConstraintDTO.getRangeClose());
    }

    private void saveDecimalFilterConstraint(ResourceListFilterConstraint transientConstraint, AbstractFilterDTO abstractConstraintDTO) {
        DecimalFilterDTO concreteConstraintDTO = (DecimalFilterDTO) abstractConstraintDTO;
        transientConstraint.setFilterTermDecimalStart(concreteConstraintDTO.getRangeStart());
        transientConstraint.setFilterTermDecimalClose(concreteConstraintDTO.getRangeClose());
    }

    private void saveUserRoleFilterConstraint(ResourceListFilterConstraint transientConstraint, AbstractFilterDTO abstractConstraintDTO) {
        UserRoleFilterDTO concreteConstraintDTO = (UserRoleFilterDTO) abstractConstraintDTO;
        transientConstraint.setFilterTermString(concreteConstraintDTO.getString());
        for (PrismRole roleId : concreteConstraintDTO.getRoles()) {
            transientConstraint.getRoles().add(roleService.getById(roleId));
        }
    }
    
    private List<PrismRole> getFilterConstraintRoles(ResourceListFilterConstraint constraint) {
        List<PrismRole> roles = Lists.newArrayList();
        for (Role role : constraint.getRoles()) {
            roles.add(role.getId());
        }
        return roles;
    }

}
