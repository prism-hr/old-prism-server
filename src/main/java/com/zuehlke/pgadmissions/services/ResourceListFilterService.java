package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismFilterSortOrder.DESCENDING;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilter.getPermittedFilters;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterExpression.CONTAIN;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismFilterMatchMode;
import com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilter;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.ResourceListFilter;
import com.zuehlke.pgadmissions.domain.resource.ResourceListFilterConstraint;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Scope;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterConstraintDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;

@Service
@Transactional
public class ResourceListFilterService {

	@Inject
	private EntityService entityService;

	@Inject
	private ScopeService scopeService;

	@Inject
	private StateService stateService;

	public void save(User user, Scope scope, ResourceListFilterDTO filterDTO) throws DeduplicationException {
		ResourceListFilter transientFilter = new ResourceListFilter().withUserAccount(user.getUserAccount()).withScope(scope)
		        .withValueString(filterDTO.getValueString()).withMatchMode(filterDTO.getMatchMode())
		        .withSortOrder(filterDTO.getSortOrder()).withUrgentOnly(filterDTO.getUrgentOnly());
		ResourceListFilter persistentFilter = entityService.createOrUpdate(transientFilter);

		List<ResourceListFilterConstraintDTO> constraints = filterDTO.getConstraints();
		if (constraints != null) {
			for (int i = 0; i < constraints.size(); i++) {
				ResourceListFilterConstraintDTO constraintDTO = filterDTO.getConstraints().get(i);
				PrismResourceListFilter filterProperty = constraintDTO.getFilterProperty();

				ResourceListFilterConstraint transientConstraint = new ResourceListFilterConstraint().withFilter(persistentFilter)
				        .withFilterProperty(filterProperty).withFilterExpression(constraintDTO.getFilterExpression()).withNegated(constraintDTO.getNegated())
				        .withDisplayPosition(i).withValueString(constraintDTO.getValueString()).withValueDateStart(constraintDTO.getValueDateStart())
				        .withValueDateClose(constraintDTO.getValueDateClose()).withValueReserveStatus(constraintDTO.getValueReserveStatus())
				        .withValueDecimalStart(constraintDTO.getValueDecimalStart()).withValueDecimalClose(constraintDTO.getValueDecimalClose());

				if (filterProperty == PrismResourceListFilter.STATE_GROUP_TITLE) {
					transientConstraint.setValueStateGroup(stateService.getStateGroupById(constraintDTO.getValueStateGroup()));
				}

				persistentFilter.getConstraints().add(transientConstraint);
				entityService.save(transientConstraint);
			}
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
				PrismResourceListFilter filterProperty = constraint.getFilterProperty();

				ResourceListFilterConstraintDTO constraintDTO = new ResourceListFilterConstraintDTO().withFilterProperty(filterProperty)
				        .withFilterExpression(constraint.getFilterExpression()).withNegated(constraint.getNegated())
				        .withDisplayPosition(constraint.getDisplayPosition()).withValueString(constraint.getValueString())
				        .withValueStateGroup(constraint.getValueStateGroup() != null ? constraint.getValueStateGroup().getId() : null)
				        .withValueDateStart(constraint.getValueDateStart()).withValueDateClose(constraint.getValueDateClose())
				        .withValueDecimalStart(constraint.getValueDecimalStart()).withValueDecimalClose(constraint.getValueDecimalClose());

				constraints.add(constraintDTO);
			}
			filterDTO.setConstraints(constraints);

			return filterDTO;
		}

		return new ResourceListFilterDTO().withUrgentOnly(false).withSortOrder(DESCENDING)
		        .withConstraints(new ArrayList<ResourceListFilterConstraintDTO>(0));
	}

	public ResourceListFilterDTO saveOrGetByUserAndScope(User user, PrismScope scopeId, ResourceListFilterDTO filterDTO) throws DeduplicationException {
		Scope scope = scopeService.getById(scopeId);
		if (filterDTO == null) {
			return getByUserAndScope(user, scope);
		} else {
			prepare(scope, filterDTO);
			return filterDTO;
		}
	}

	private void prepare(Scope scope, ResourceListFilterDTO filterDTO) {
		String valueString = filterDTO.getValueString();
		List<ResourceListFilterConstraintDTO> constraintDTOs = filterDTO.getConstraints();

		if (!Strings.isNullOrEmpty(valueString) && constraintDTOs == null) {
			List<ResourceListFilterConstraintDTO> constraints = Lists.newLinkedList();
			int displayPosition = 0;
			for (PrismResourceListFilter property : getPermittedFilters(scope.getId())) {
				if (property.getPermittedExpressions().contains(CONTAIN)) {
					ResourceListFilterConstraintDTO constraintDTO = new ResourceListFilterConstraintDTO().withFilterProperty(property)
					        .withFilterExpression(CONTAIN).withNegated(false).withDisplayPosition(displayPosition)
					        .withValueString(valueString);
					constraints.add(constraintDTO);
					displayPosition++;
				}
			}
			filterDTO.setConstraints(constraints);
			filterDTO.withMatchMode(PrismFilterMatchMode.ANY);
		}
	}

}
