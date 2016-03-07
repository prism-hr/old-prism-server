package uk.co.alumeni.prism.services;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.ArrayUtils.contains;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static uk.co.alumeni.prism.dao.WorkflowDAO.advertScopes;
import static uk.co.alumeni.prism.domain.definitions.PrismFilterMatchMode.ANY;
import static uk.co.alumeni.prism.domain.definitions.PrismFilterSortOrder.DESCENDING;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceListConstraint.STATE_GROUP_NAME;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceListConstraint.getPermittedFilters;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceListFilterExpression.CONTAIN;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.APPLICATION;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.domain.Theme;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.advert.AdvertCategories;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.definitions.PrismResourceListConstraint;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceListFilter;
import uk.co.alumeni.prism.domain.resource.ResourceListFilterConstraint;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Scope;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.dto.ResourceIdentityDTO;
import uk.co.alumeni.prism.exceptions.DeduplicationException;
import uk.co.alumeni.prism.rest.dto.resource.ResourceListFilterConstraintDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceListFilterDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceListFilterTagDTO;

import com.google.common.collect.Lists;

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
                PrismResourceListConstraint filterProperty = constraintDTO.getFilterProperty();

                ResourceListFilterConstraint transientConstraint = new ResourceListFilterConstraint().withFilter(persistentFilter)
                        .withFilterProperty(filterProperty).withFilterExpression(constraintDTO.getFilterExpression()).withNegated(constraintDTO.getNegated())
                        .withDisplayPosition(i).withValueString(constraintDTO.getValueString()).withValueDateStart(constraintDTO.getValueDateStart())
                        .withValueDateClose(constraintDTO.getValueDateClose()).withValueDecimalStart(constraintDTO.getValueDecimalStart())
                        .withValueDecimalClose(constraintDTO.getValueDecimalClose());

                if (filterProperty == STATE_GROUP_NAME) {
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
                PrismResourceListConstraint filterProperty = constraint.getFilterProperty();

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
                .withConstraints(Collections.emptyList());
    }

    public ResourceListFilterDTO getReplicableActionFilter(Resource resource, StateTransition stateTransition, List<PrismAction> actions) {
        return getReplicableActionFilter(resource, stateTransition, actions, false);
    }

    public ResourceListFilterDTO getReplicableActionFilter(Resource resource, StateTransition stateTransition, List<PrismAction> actions, boolean includeTags) {
        List<ResourceListFilterTagDTO> themeDTOs = newArrayList();
        List<ResourceListFilterTagDTO> secondaryThemeDTOs = newArrayList();
        List<ResourceListFilterTagDTO> locationDTOs = newArrayList();
        List<ResourceListFilterTagDTO> secondaryLocationDTOs = newArrayList();

        if (includeTags) {
            PrismScope resourceScope = resource.getResourceScope();
            if (resourceScope.equals(APPLICATION)) {
                Application application = (Application) resource;
                application.getThemes().forEach(applicationTheme -> {
                    Theme tag = applicationTheme.getTag();
                    ResourceListFilterTagDTO themeDTO = new ResourceListFilterTagDTO(tag.getId(), tag.getName());
                    if (isTrue(applicationTheme.getPreference())) {
                        themeDTOs.add(themeDTO);
                    } else {
                        secondaryThemeDTOs.add(themeDTO);
                    }
                });

                application.getLocations().forEach(applicationLocation -> {
                    Advert tag = applicationLocation.getTag();
                    ResourceListFilterTagDTO locationDTO = new ResourceListFilterTagDTO(tag.getId(), tag.toString());
                    if (isTrue(applicationLocation.getPreference())) {
                        locationDTOs.add(locationDTO);
                    } else {
                        secondaryLocationDTOs.add(locationDTO);
                    }
                });
            } else if (contains(advertScopes, resourceScope)) {
                AdvertCategories categories = resource.getAdvert().getCategories();
                if (categories != null) {
                    categories.getThemes().forEach(advertTheme -> {
                        Theme tag = advertTheme.getTheme();
                        themeDTOs.add(new ResourceListFilterTagDTO(tag.getId(), tag.getName()));
                    });

                    categories.getLocations().forEach(advertLocation -> {
                        Advert tag = advertLocation.getLocationAdvert();
                        locationDTOs.add(new ResourceListFilterTagDTO(tag.getId(), tag.toString()));
                    });
                }
            }
        }

        Resource parentResource = resource.getParentResource();
        Class<? extends Resource> parentResourceClass = parentResource.getClass();
        ResourceIdentityDTO parentResourceDTO = new ResourceIdentityDTO().withId(parentResource.getId()).withScope(parentResource.getResourceScope());
        if (ResourceParent.class.isAssignableFrom(parentResourceClass)) {
            parentResourceDTO.setName(((ResourceParent) parentResource).getAdvert().getName());
            if (parentResourceClass.equals(Institution.class)) {
                Document logoImage = ((Institution) parentResource).getLogoImage();
                if (logoImage != null) {
                    parentResourceDTO.setLogoImageId(logoImage.getId());
                }
            }
        }

        return new ResourceListFilterDTO().withParentResource(parentResourceDTO).withActionIds(actions).withThemes(themeDTOs)
                .withThemesApplied(stateTransition.getReplicableSequenceFilterTheme()).withSecondaryThemes(secondaryThemeDTOs)
                .withSecondaryThemesApplied(stateTransition.getReplicableSequenceFilterSecondaryTheme())
                .withLocations(locationDTOs).withLocationsApplied(stateTransition.getReplicableSequenceFilterLocation())
                .withSecondaryLocations(secondaryLocationDTOs)
                .withSecondaryLocationsApplied(stateTransition.getReplicableSequenceFilterSecondaryLocation());
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

        if (!isNullOrEmpty(valueString) && constraintDTOs == null) {
            List<ResourceListFilterConstraintDTO> constraints = Lists.newLinkedList();
            int displayPosition = 0;
            for (PrismResourceListConstraint property : getPermittedFilters(scope.getId())) {
                if (property.getPermittedExpressions().contains(CONTAIN)) {
                    ResourceListFilterConstraintDTO constraintDTO = new ResourceListFilterConstraintDTO().withFilterProperty(property)
                            .withFilterExpression(CONTAIN).withNegated(false).withDisplayPosition(displayPosition)
                            .withValueString(valueString);
                    constraints.add(constraintDTO);
                    displayPosition++;
                }
            }
            filterDTO.setConstraints(constraints);
            filterDTO.withMatchMode(ANY);
        }
    }

}
