package com.zuehlke.pgadmissions.mapping;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.address.Address;
import com.zuehlke.pgadmissions.domain.address.AddressCoordinates;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.advert.AdvertCategories;
import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.advert.AdvertFinancialDetail;
import com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.*;
import com.zuehlke.pgadmissions.dto.AdvertDTO;
import com.zuehlke.pgadmissions.dto.AdvertTargetDTO;
import com.zuehlke.pgadmissions.dto.EntityOpportunityCategoryDTO;
import com.zuehlke.pgadmissions.dto.ResourceActivityDTO;
import com.zuehlke.pgadmissions.rest.dto.AddressDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunitiesQueryDTO;
import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.address.AddressCoordinatesRepresentation;
import com.zuehlke.pgadmissions.rest.representation.address.AddressRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.*;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceConditionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceOpportunityRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;
import com.zuehlke.pgadmissions.services.AdvertService;
import org.joda.time.LocalDate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit.YEAR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.*;
import static com.zuehlke.pgadmissions.utils.PrismListUtils.getSummaryRepresentations;
import static com.zuehlke.pgadmissions.utils.PrismListUtils.processRowDescriptors;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.getProperty;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.setProperty;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
@Transactional
public class AdvertMapper {

    @Inject
    private AdvertService advertService;

    @Inject
    private AddressMapper addressMapper;

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private UserMapper userMapper;

    public AdvertRepresentationSimple getAdvertRepresentationSimple(Advert advert) {
        return getAdvertRepresentation(advert, AdvertRepresentationSimple.class);
    }

    // FIXME - prioritise the users network
    public AdvertListRepresentation getAdvertExtendedRepresentations(OpportunitiesQueryDTO query) {
        PrismScope filterScope = query.getContextScope();
        PrismScope[] filterScopes = filterScope != null ? new PrismScope[] { filterScope } : query.getContext().getFilterScopes();

        Set<Integer> advertIds = Sets.newHashSet();
        Map<String, Integer> summaries = Maps.newHashMap();
        Set<EntityOpportunityCategoryDTO> adverts = advertService.getVisibleAdverts(query, filterScopes);
        processRowDescriptors(adverts, advertIds, null, summaries);

        PrismScope[] parentScopes = new PrismScope[] { PROJECT, PROGRAM, DEPARTMENT, INSTITUTION };

        HashMultimap<PrismScope, Integer> resources = HashMultimap.create();
        Map<Integer, AdvertRepresentationExtended> index = Maps.newLinkedHashMap();
        advertService.getAdvertList(query, advertIds).forEach(advert -> {
            PrismScope scope = advert.getScope();
            for (PrismScope advertScope : parentScopes) {
                if (advertScope.ordinal() <= scope.ordinal()) {
                    ResourceActivityDTO enclosingResourceDTO = advert.getEnclosingResource(advertScope);
                    if (enclosingResourceDTO != null) {
                        resources.put(advertScope, enclosingResourceDTO.getId());
                    }
                }
            }
            index.put(advert.getAdvertId(), getAdvertRepresentationExtended(advert));
        });

        HashMultimap<Integer, PrismStudyOption> studyOptionIndex = HashMultimap.create();
        for (PrismScope opportunityScope : new PrismScope[] { PROJECT, PROGRAM }) {
            Set<Integer> scopedResources = resources.get(opportunityScope);
            if (isNotEmpty(scopedResources)) {
                HashMultimap<Integer, PrismStudyOption> studyOptions = advertService.getAdvertStudyOptions(opportunityScope, scopedResources);
                studyOptions.keySet().forEach(advert -> {
                    Set<PrismStudyOption> advertStudyOptions = studyOptions.get(advert);
                    if (!(isEmpty(advertStudyOptions) || studyOptionIndex.containsKey(advert))) {
                        studyOptionIndex.putAll(advert, advertStudyOptions);
                    }
                });
            }
        }

        HashMultimap<Integer, ResourceConditionRepresentation> actionConditionIndex = HashMultimap.create();
        for (PrismScope parentScope : parentScopes) {
            Set<Integer> scopedResources = resources.get(parentScope);
            if (isNotEmpty(scopedResources)) {
                HashMultimap<Integer, ResourceConditionRepresentation> resourceConditions = advertService.getAdvertActionConditions(parentScope, scopedResources);
                resourceConditions.keySet().forEach(advert -> {
                    Set<ResourceConditionRepresentation> advertResourceConditions = resourceConditions.get(advert);
                    if (!(isEmpty(advertResourceConditions) || actionConditionIndex.containsKey(advert))) {
                        actionConditionIndex.putAll(advert, advertResourceConditions);
                    }
                });
            }
        }

        List<AdvertRepresentationExtended> representations = Lists.newLinkedList();
        index.keySet().forEach(advert -> {
            AdvertRepresentationExtended representation = index.get(advert);
            representation.setStudyOptions(newArrayList(studyOptionIndex.get(advert)));
            representation.setConditions(newArrayList(actionConditionIndex.get(advert)));
            representations.add(representation);
        });

        return new AdvertListRepresentation().withRows(representations).withSummaries(getSummaryRepresentations(summaries));
    }

    public AdvertRepresentationExtended getAdvertRepresentationExtended(Advert advert) {
        AdvertRepresentationExtended representation = getAdvertRepresentation(advert, AdvertRepresentationExtended.class);

        ResourceParent resource = advert.getResource();
        representation.setUser(userMapper.getUserRepresentationSimple(resource.getUser()));

        Institution institution = resource.getInstitution();
        representation.setInstitution(resourceMapper.getResourceRepresentationSimple(institution));

        Department department = resource.getDepartment();
        if (department != null) {
            representation.setDepartment(resourceMapper.getResourceRepresentationSimple(department));
        }

        Program program = resource.getProgram();
        if (program != null) {
            representation.setProgram(resourceMapper.getResourceOpportunityRepresentationSimple(program));
        }

        Project project = resource.getProject();
        if (project != null) {
            representation.setProject(resourceMapper.getResourceOpportunityRepresentationSimple(project));
        }

        if (ResourceOpportunity.class.isAssignableFrom(resource.getClass())) {
            representation.setOpportunityType(((ResourceOpportunity) resource).getOpportunityType().getId());

            String opportunityCategories = resource.getOpportunityCategories();
            if (opportunityCategories != null) {
                representation
                        .setOpportunityCategories(asList(opportunityCategories.split("\\|")).stream().map(oc -> PrismOpportunityCategory.valueOf(oc)).collect(Collectors.toList()));
            }

            representation.setStudyOptions(((ResourceOpportunity) resource).getResourceStudyOptions().stream().map(rso -> rso.getStudyOption()).collect(toList()));
        }

        representation.setConditions(resourceMapper.getResourceConditionRepresentations(resource));
        representation.setName(advert.getName());

        return representation;
    }

    public AdvertRepresentationExtended getAdvertRepresentationExtended(AdvertDTO advert) {
        AdvertRepresentationExtended representation = new AdvertRepresentationExtended();

        representation.setId(advert.getId());
        representation.setUser(new UserRepresentationSimple().withFirstName(advert.getUserFirstName()).withLastName(advert.getUserLastName())
                .withAccountProfileUrl(advert.getUserAccountProfileUrl()).withAccountImageUrl(advert.getUserAccountImageUrl()));

        for (PrismScope scope : new PrismScope[] { PROJECT, PROGRAM, DEPARTMENT, INSTITUTION }) {
            ResourceRepresentationSimple resource = getAdvertResourceRepresentation(advert, scope);
            if (resource != null) {
                setProperty(representation, scope.getLowerCamelName(), resource);
            }
        }

        representation.setOpportunityType(advert.getOpportunityType());
        representation.setName(advert.getName());
        representation.setSummary(advert.getSummary());
        representation.setDescription(advert.getDescription());
        representation.setGloballyVisible(advert.getGloballyVisible());
        representation.setHomepage(advert.getHomepage());
        representation.setApplyHomepage(advert.getApplyHomepage());
        representation.setTelephone(advert.getTelephone());

        representation.setAddress(new AddressRepresentation().withAddressLine1(advert.getAddressLine1()).withAddressLine2(advert.getAddressLine2())
                .withAddressTown(advert.getAddressTown()).withAddressRegion(advert.getAddressRegion()).withAddressCode(advert.getAddressCode())
                .withDomicile(advert.getAddressDomicileId()).withGoogleId(advert.getAddressGoogleId())
                .withCoordinates(new AddressCoordinatesRepresentation().withLatitude(advert.getAddressCoordinateLatitude()).withLongitude(advert.getAddressCoordinateLongitude())));

        String payCurrency = advert.getPayCurrency();
        if (payCurrency != null) {
            PrismDurationUnit payInterval = advert.getPayInterval();
            AdvertFinancialDetailRepresentation payRepresentation = new AdvertFinancialDetailRepresentation().withCurrency(payCurrency).withInterval(payInterval);

            boolean byYear = payInterval.equals(YEAR);
            payRepresentation.setMinimum(byYear ? advert.getPayYearMinimum() : advert.getPayMonthMinimum());
            payRepresentation.setMaximum(byYear ? advert.getPayYearMaximum() : advert.getPayMonthMaximum());
            representation.setPay(payRepresentation);
        }

        representation.setClosingDate(advert.getClosingDate() != null ? new AdvertClosingDateRepresentation().withClosingDate(advert.getClosingDate()) : null);
        representation.setSequenceIdentifier(advert.getSequenceIdentifier());
        return representation;
    }

    public <T extends AdvertRepresentationSimple> T getAdvertRepresentation(Advert advert, Class<T> returnType) {
        T representation = BeanUtils.instantiate(returnType);

        representation.setId(advert.getId());
        representation.setSummary(advert.getSummary());
        representation.setDescription(advert.getDescription());
        representation.setGloballyVisible(advert.getGloballyVisible());

        Integer backgroundImageId = advertService.getBackgroundImage(advert);
        representation.setBackgroundImage(backgroundImageId != null ? new DocumentRepresentation().withId(backgroundImageId) : null);
        representation.setHomepage(advert.getHomepage());
        representation.setApplyHomepage(advert.getApplyHomepage());

        representation.setTelephone(advert.getTelephone());
        representation.setAddress(getAdvertAddressRepresentation(advert));
        representation.setPay(getAdvertFinancialDetailRepresentation(advert));

        representation.setClosingDate(getAdvertClosingDateRepresentation(advert));
        representation.setClosingDates(getAdvertClosingDateRepresentations(advert));

        representation.setCategories(getAdvertCategoriesRepresentation(advert));
        representation.setConnections(getAdvertTargetRepresentations(advert));
        representation.setCompetences(getAdvertCompetenceRepresentations(advert));
        representation.setSequenceIdentifier(advert.getSequenceIdentifier());

        return representation;
    }

    public AddressDTO getAddressDTO(Address address) {
        AddressDTO addressDTO = addressMapper.transform(address, AddressDTO.class);
        Domicile domicile = address.getDomicile();
        addressDTO.setDomicile(domicile == null ? null : domicile.getId());
        addressDTO.setGoogleId(address.getGoogleId());
        return addressDTO;
    }

    public AddressRepresentation getAdvertAddressRepresentation(Advert advert) {
        Address address = advert.getAddress();
        if (address != null) {
            AddressRepresentation representation = addressMapper.transform(address, AddressRepresentation.class);

            Domicile domicile = address.getDomicile();
            representation.setDomicile(domicile == null ? null : domicile.getId());
            representation.setGoogleId(address.getGoogleId());

            AddressCoordinates addressCoordinates = address.getAddressCoordinates();
            if (addressCoordinates != null) {
                representation.setCoordinates(new AddressCoordinatesRepresentation().withLatitude(addressCoordinates.getLatitude()).withLongitude(
                        addressCoordinates.getLongitude()));
            }

            return representation;
        }

        return null;
    }

    private AdvertFinancialDetailRepresentation getAdvertFinancialDetailRepresentation(Advert advert) {
        AdvertFinancialDetail financialDetail = advert.getPay();
        if (financialDetail != null) {
            PrismDurationUnit durationUnit = financialDetail.getInterval();
            AdvertFinancialDetailRepresentation representation = new AdvertFinancialDetailRepresentation().withCurrency(financialDetail.getCurrencySpecified())
                    .withInterval(financialDetail.getInterval());
            if (durationUnit.equals(YEAR)) {
                representation.setMinimum(financialDetail.getYearMinimumSpecified());
                representation.setMaximum(financialDetail.getYearMaximumSpecified());
            } else {
                representation.setMinimum(financialDetail.getMonthMinimumSpecified());
                representation.setMaximum(financialDetail.getMonthMaximumSpecified());
            }
            return representation;
        }
        return null;
    }

    private AdvertClosingDateRepresentation getAdvertClosingDateRepresentation(Advert advert) {
        AdvertClosingDate closingDate = advert.getClosingDate();
        return closingDate == null ? null : getAdvertClosingDateRepresentation(closingDate);
    }

    private AdvertClosingDateRepresentation getAdvertClosingDateRepresentation(AdvertClosingDate closingDate) {
        return new AdvertClosingDateRepresentation().withId(closingDate.getId()).withClosingDate(closingDate.getClosingDate());
    }

    private List<AdvertClosingDateRepresentation> getAdvertClosingDateRepresentations(Advert advert) {
        return advert.getClosingDates().stream().map(this::getAdvertClosingDateRepresentation).collect(Collectors.toList());
    }

    private AdvertCategoriesRepresentation getAdvertCategoriesRepresentation(Advert advert) {
        AdvertCategories categories = advertService.getAdvertCategories(advert);
        return categories == null ? null : new AdvertCategoriesRepresentation().withIndustries(categories.getIndustries().stream().map(i -> i.getIndustry()).collect(toList()))
                .withFunctions(categories.getFunctions().stream().map(f -> f.getFunction()).collect(toList()));
    }

    // TODO: sorting logic
    private List<AdvertTargetRepresentation> getAdvertTargetRepresentations(Advert advert) {
        List<AdvertTargetDTO> connections = advertService.getAdvertTargets(advert);
        return null;
    }

    private List<AdvertCompetenceRepresentation> getAdvertCompetenceRepresentations(Advert advert) {
        return advert.getCompetences().stream().map(competence -> new AdvertCompetenceRepresentation().withName(competence.getCompetence().getName())
                .withDescription(competence.getCompetence().getDescription()).withImportance(competence.getImportance())).collect(toList());
    }

    @SuppressWarnings("unchecked")
    private <T extends ResourceRepresentationSimple> T getAdvertResourceRepresentation(AdvertDTO advert, PrismScope scope) {
        ResourceActivityDTO resource = advert.getEnclosingResource(scope);
        if (resource != null) {
            boolean isOpportunity = asList(PROGRAM, PROJECT).contains(scope);
            Class<?> representationClass = isOpportunity ? ResourceOpportunityRepresentationSimple.class : ResourceRepresentationSimple.class;
            T resourceRepresentation = (T) BeanUtils.instantiate(representationClass);

            resourceRepresentation.setScope(scope);
            resourceRepresentation.setId(advert.getId());
            resourceRepresentation.setName(advert.getName());

            if (isOpportunity) {
                String scopeReference = scope.getLowerCamelName();
                ResourceOpportunityRepresentationSimple resourceOpportunityRepresentation = (ResourceOpportunityRepresentationSimple) resourceRepresentation;
                resourceOpportunityRepresentation.setAvailableDate((LocalDate) getProperty(advert, scopeReference + "AvailableDate"));
                resourceOpportunityRepresentation.setDurationMinimum((Integer) getProperty(advert, scopeReference + "DurationMinimum"));
                resourceOpportunityRepresentation.setDurationMaximum((Integer) getProperty(advert, scopeReference + "DurationMaximum"));
            } else if (scope.equals(INSTITUTION)) {
                resourceRepresentation.setLogoImage(new DocumentRepresentation().withId(advert.getLogoImageId()));
            }

            return resourceRepresentation;
        }
        return null;
    }

}
