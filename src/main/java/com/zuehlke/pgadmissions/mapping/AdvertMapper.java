package com.zuehlke.pgadmissions.mapping;

import static com.google.common.collect.Lists.newArrayList;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit.YEAR;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType.valueOf;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.utils.PrismListUtils.getSummaryRepresentations;
import static com.zuehlke.pgadmissions.utils.PrismListUtils.processRowDescriptors;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.address.AddressAdvert;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.advert.AdvertAttribute;
import com.zuehlke.pgadmissions.domain.advert.AdvertCategories;
import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.advert.AdvertCompetence;
import com.zuehlke.pgadmissions.domain.advert.AdvertFinancialDetail;
import com.zuehlke.pgadmissions.domain.advert.AdvertSubjectArea;
import com.zuehlke.pgadmissions.domain.advert.AdvertTargets;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.location.AddressCoordinates;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.dto.AdvertDTO;
import com.zuehlke.pgadmissions.dto.AdvertRecommendationDTO;
import com.zuehlke.pgadmissions.dto.EntityOpportunityCategoryDTO;
import com.zuehlke.pgadmissions.dto.resource.ResourceStandardDTO;
import com.zuehlke.pgadmissions.rest.dto.AddressAdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunitiesQueryDTO;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedAdvertDomicileDTO;
import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.address.AddressAdvertRepresentation;
import com.zuehlke.pgadmissions.rest.representation.address.AddressCoordinatesRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertCategoriesRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertClosingDateRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertCompetenceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertFinancialDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertFinancialDetailsRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertListRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertSubjectAreaRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertTargetsRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceConditionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.ResourceRepresentationTarget;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;

import uk.co.alumeni.prism.api.model.imported.response.ImportedAdvertDomicileResponse;

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

    public AdvertListRepresentation getAdvertExtendedRepresentations(OpportunitiesQueryDTO query) {
        PrismScope[] opportunityScopes = new PrismScope[] { PROJECT, PROGRAM };
        PrismScope[] parentScopes = new PrismScope[] { PROJECT, PROGRAM, DEPARTMENT, INSTITUTION };

        Set<Integer> advertIds = Sets.newHashSet();
        Map<String, Integer> summaries = Maps.newHashMap();
        Set<EntityOpportunityCategoryDTO> adverts = advertService.getVisibleAdverts(query, parentScopes);
        processRowDescriptors(adverts, advertIds, summaries);

        HashMultimap<PrismScope, Integer> resources = HashMultimap.create();
        Map<Integer, AdvertRepresentationExtended> index = Maps.newLinkedHashMap();
        advertService.getAdvertList(query, advertIds).forEach(advert -> {
            PrismScope scope = advert.getScope();
            for (PrismScope advertScope : parentScopes) {
                if (advertScope.ordinal() >= scope.ordinal()) {
                    ResourceStandardDTO enclosingResourceDTO = advert.getEnclosingResource(advertScope);
                    if (enclosingResourceDTO != null) {
                        resources.put(advertScope, enclosingResourceDTO.getId());
                    }
                }
            }
            index.put(advert.getAdvertId(), getAdvertRepresentationExtended(advert));
        });

        Map<PrismScope, HashMultimap<Integer, PrismStudyOption>> studyOptionIndex = Maps.newHashMap();
        for (PrismScope opportunityScope : opportunityScopes) {
            studyOptionIndex.put(opportunityScope, advertService.getAdvertStudyOptions(opportunityScope, resources.get(opportunityScope)));
        }

        Map<PrismScope, HashMultimap<Integer, ResourceConditionRepresentation>> actionConditionIndex = Maps.newHashMap();
        for (PrismScope partnerScope : parentScopes) {
            actionConditionIndex.put(partnerScope, advertService.getAdvertActionConditions(partnerScope, resources.get(partnerScope)));
        }

        List<AdvertRepresentationExtended> representations = Lists.newLinkedList();
        index.keySet().forEach(advert -> {
            AdvertRepresentationExtended representation = index.get(advert);

            Set<PrismStudyOption> studyOptions = null;
            for (PrismScope opportunityScope : opportunityScopes) {
                studyOptions = studyOptionIndex.get(opportunityScope).get(advert);
                if (CollectionUtils.isNotEmpty(studyOptions)) {
                    representation.setStudyOptions(newArrayList(studyOptions));
                    break;
                }
            }

            Set<ResourceConditionRepresentation> actionConditions = null;
            for (PrismScope partnerScope : parentScopes) {
                actionConditions = actionConditionIndex.get(partnerScope).get(advert);
                if (CollectionUtils.isNotEmpty(actionConditions)) {
                    representation.setConditions(newArrayList(actionConditions));
                    break;
                }
            }

            representations.add(representation);
        });

        return new AdvertListRepresentation().withRows(representations).withSummaries(getSummaryRepresentations(summaries));
    }

    public AdvertRepresentationExtended getAdvertRepresentationExtended(Advert advert) {
        AdvertRepresentationExtended representation = getAdvertRepresentation(advert, AdvertRepresentationExtended.class);

        ResourceParent resource = advert.getResource();
        representation.setUser(userMapper.getUserRepresentationSimple(resource.getUser()));
        representation.setResource(resourceMapper.getResourceRepresentationSimple(resource));

        Institution institution = resource.getInstitution();
        if (!institution.sameAs(resource)) {
            representation.setInstitution(resourceMapper.getResourceRepresentationSimple(institution));
        }

        Department department = resource.getDepartment();
        if (!(department == null || department.sameAs(resource))) {
            representation.setDepartment(resourceMapper.getResourceRepresentationSimple(resource));
        }

        if (ResourceOpportunity.class.isAssignableFrom(resource.getClass())) {
            representation.setOpportunityType(valueOf(((ResourceOpportunity) resource).getOpportunityType().getName()));
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

        ResourceStandardDTO resource = null;
        for (PrismScope scope : new PrismScope[] { PROJECT, PROGRAM, DEPARTMENT, INSTITUTION }) {
            ResourceStandardDTO thisResource = advert.getEnclosingResource(scope);
            if (thisResource == null) {
                continue;
            } else if (resource == null) {
                ResourceRepresentationSimple resourceRepresentation = getAdvertResourceRepresentation(thisResource);
                representation.setResource(resourceRepresentation);
                resource = thisResource;
            } else {
                PrismReflectionUtils.setProperty(representation, scope.getLowerCamelName(), getAdvertResourceRepresentation(thisResource));
            }
        }

        String opportunityType = advert.getOpportunityType();
        representation.setOpportunityType(opportunityType != null ? PrismOpportunityType.valueOf(opportunityType) : null);

        representation.setName(advert.getName());
        representation.setSummary(advert.getSummary());
        representation.setDescription(advert.getDescription());
        representation.setHomepage(advert.getHomepage());
        representation.setApplyHomepage(advert.getApplyHomepage());
        representation.setTelephone(advert.getTelephone());

        representation.setAddress(new AddressAdvertRepresentation().withAddressLine1(advert.getAddressLine1()).withAddressLine2(advert.getAddressLine2())
                .withAddressTown(advert.getAddressTown()).withAddressRegion(advert.getAddressRegion()).withAddressCode(advert.getAddressCode())
                .withDomicile(new ImportedAdvertDomicileResponse().withId(advert.getAddressDomicileId()).withName(advert.getAddressDomicileName()))
                .withGoogleId(advert.getAddressGoogleId())
                .withCoordinates(new AddressCoordinatesRepresentation().withLatitude(advert.getAddressCoordinateLatitude()).withLongitude(advert.getAddressCoordinateLongitude())));

        String feeCurrency = advert.getFeeCurrency();
        String payCurrency = advert.getPayCurrency();
        if (!(feeCurrency == null && payCurrency == null)) {
            AdvertFinancialDetailsRepresentation financialDetailsRepresentation = new AdvertFinancialDetailsRepresentation();
            if (feeCurrency != null) {
                PrismDurationUnit feeInterval = advert.getFeeInterval();
                AdvertFinancialDetailRepresentation feeRepresentation = new AdvertFinancialDetailRepresentation().withCurrency(feeCurrency).withInterval(feeInterval);

                boolean byYear = feeInterval.equals(YEAR);
                feeRepresentation.setMinimum(byYear ? advert.getFeeYearMinimum() : advert.getFeeMonthMinimum());
                feeRepresentation.setMaximum(byYear ? advert.getFeeYearMaximum() : advert.getFeeMonthMaximum());
                financialDetailsRepresentation.setFee(feeRepresentation);
            }

            if (payCurrency != null) {
                PrismDurationUnit payInterval = advert.getPayInterval();
                AdvertFinancialDetailRepresentation payRepresentation = new AdvertFinancialDetailRepresentation().withCurrency(payCurrency).withInterval(payInterval);

                boolean byYear = payInterval.equals(YEAR);
                payRepresentation.setMinimum(byYear ? advert.getPayYearMinimum() : advert.getPayMonthMinimum());
                payRepresentation.setMaximum(byYear ? advert.getPayYearMaximum() : advert.getPayMonthMaximum());
                financialDetailsRepresentation.setFee(payRepresentation);
            }

            representation.setFinancialDetails(financialDetailsRepresentation);
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

        Integer backgroundImageId = advertService.getBackgroundImage(advert);
        representation.setBackgroundImage(backgroundImageId != null ? new DocumentRepresentation().withId(backgroundImageId) : null);
        representation.setHomepage(advert.getHomepage());
        representation.setApplyHomepage(advert.getApplyHomepage());

        representation.setTelephone(advert.getTelephone());
        representation.setAddress(getAdvertAddressRepresentation(advert));
        representation.setFinancialDetails(getAdvertFinancialDetailsRepresentation(advert));

        representation.setClosingDate(getAdvertClosingDateRepresentation(advert));
        representation.setClosingDates(getAdvertClosingDateRepresentations(advert));

        representation.setCategories(getAdvertCategoriesRepresentation(advert));
        representation.setTargets(getAdvertTargetsRepresentation(advert));

        representation.setSequenceIdentifier(advert.getSequenceIdentifier());
        return representation;
    }

    public AddressAdvertDTO getAddressDTO(AddressAdvert address) {
        AddressAdvertDTO addressDTO = addressMapper.transform(address, AddressAdvertDTO.class);

        addressDTO.setDomicile(new ImportedAdvertDomicileDTO().withId(address.getDomicile().getId()));
        addressDTO.setGoogleId(address.getGoogleId());

        return addressDTO;
    }

    public List<ImportedAdvertDomicileResponse> getAdvertDomicileRepresentations() {
        List<ImportedAdvertDomicile> importedAdvertDomiciles = advertService.getAdvertDomiciles();
        return importedAdvertDomiciles.stream().map(this::getAdvertDomicileRepresentation).collect(Collectors.toList());
    }

    public List<AdvertRepresentationExtended> getRecommendedAdvertRepresentations(Application application) {
        List<AdvertRepresentationExtended> representations = Lists.newLinkedList();
        List<AdvertRecommendationDTO> advertRecommendations = advertService.getRecommendedAdverts(application.getUser());
        for (AdvertRecommendationDTO advertRecommendation : advertRecommendations) {
            representations.add(getAdvertRepresentationExtended(advertRecommendation.getAdvert()));
        }
        return representations;
    }

    public AddressAdvertRepresentation getAdvertAddressRepresentation(Advert advert) {
        AddressAdvert address = advert.getAddress();
        if (address != null) {
            AddressAdvertRepresentation representation = addressMapper.transform(address, AddressAdvertRepresentation.class);

            representation.setDomicile(getAdvertDomicileRepresentation(address.getDomicile()));
            representation.setGoogleId(address.getGoogleId());

            AddressCoordinates addressCoordinates = address.getCoordinates();
            if (addressCoordinates != null) {
                representation.setCoordinates(new AddressCoordinatesRepresentation().withLatitude(addressCoordinates.getLatitude()).withLongitude(
                        addressCoordinates.getLongitude()));
            }

            return representation;
        }

        return null;
    }

    private AdvertFinancialDetailsRepresentation getAdvertFinancialDetailsRepresentation(Advert advert) {
        AdvertFinancialDetail fee = advert.getFee();
        AdvertFinancialDetail pay = advert.getPay();
        if (!(fee == null && pay == null)) {
            return new AdvertFinancialDetailsRepresentation().withFee(getAdvertFinancialDetailRepresentation(fee)).withPay(
                    getAdvertFinancialDetailRepresentation(pay));
        }
        return null;
    }

    private AdvertFinancialDetailRepresentation getAdvertFinancialDetailRepresentation(AdvertFinancialDetail detail) {
        if (detail != null) {
            PrismDurationUnit durationUnit = detail.getInterval();
            AdvertFinancialDetailRepresentation representation = new AdvertFinancialDetailRepresentation().withCurrency(detail.getCurrencySpecified())
                    .withInterval(detail.getInterval());
            if (durationUnit.equals(YEAR)) {
                representation.setMinimum(detail.getYearMinimumSpecified());
                representation.setMaximum(detail.getYearMaximumSpecified());
            } else {
                representation.setMinimum(detail.getMonthMinimumSpecified());
                representation.setMaximum(detail.getMonthMaximumSpecified());
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
        return new AdvertClosingDateRepresentation().withId(closingDate.getId()).withClosingDate(closingDate.getValue());
    }

    private List<AdvertClosingDateRepresentation> getAdvertClosingDateRepresentations(Advert advert) {
        return advert.getClosingDates().stream().map(this::getAdvertClosingDateRepresentation).collect(Collectors.toList());
    }

    private AdvertCategoriesRepresentation getAdvertCategoriesRepresentation(Advert advert) {
        AdvertCategories categories = advertService.getAdvertCategories(advert);
        return categories == null ? null : new AdvertCategoriesRepresentation().withIndustries(getAdvertCategoryRepresentations(categories.getIndustries()))
                .withFunctions(getAdvertCategoryRepresentations(categories.getFunctions()))
                .withThemes(getAdvertCategoryRepresentations(categories.getThemes()));
    }

    private <T extends AdvertAttribute<U>, U> List<U> getAdvertCategoryRepresentations(Set<T> categories) {
        return categories.stream().map(T::getValue).collect(Collectors.toList());
    }

    private AdvertTargetsRepresentation getAdvertTargetsRepresentation(Advert advert) {
        AdvertTargets targets = advertService.getAdvertTargets(advert);
        if (targets != null) {
            return new AdvertTargetsRepresentation().withCompetences(getAdvertCompetenceRepresentations(targets.getCompetences()))
                    .withSubjectAreas(getAdvertSubjectAreaRepresentations(targets.getSubjectAreas()))
                    .withResources(getAdvertResourceRepresentations(advert, false)).withSelectedResources(getAdvertResourceRepresentations(advert, true));
        }
        return null;
    }

    private List<AdvertCompetenceRepresentation> getAdvertCompetenceRepresentations(Collection<AdvertCompetence> competences) {
        return competences.stream().<AdvertCompetenceRepresentation> map(competence -> new AdvertCompetenceRepresentation().withName(competence.getName())
                .withDescription(competence.getValue().getDescription()).withImportance(competence.getImportance())).collect(Collectors.toList());
    }

    private List<AdvertSubjectAreaRepresentation> getAdvertSubjectAreaRepresentations(Collection<AdvertSubjectArea> subjectAreas) {
        return subjectAreas.stream().map(subjectArea -> new AdvertSubjectAreaRepresentation().withId(subjectArea.getValueId()).withName(subjectArea.getName()))
                .collect(Collectors.toList());
    }

    private List<ResourceRepresentationTarget> getAdvertResourceRepresentations(Advert advert, boolean selected) {
        return resourceMapper.getResourceTargetingRepresentations(advert, null, advertService.getAdvertTargetResources(advert, INSTITUTION, selected),
                advertService.getAdvertTargetResources(advert, DEPARTMENT, selected), false);
    }

    private ImportedAdvertDomicileResponse getAdvertDomicileRepresentation(ImportedAdvertDomicile domicile) {
        return new ImportedAdvertDomicileResponse().withId(domicile.getId()).withName(domicile.getName()).withCurrency(domicile.getCurrency());
    }

    private ResourceRepresentationSimple getAdvertResourceRepresentation(ResourceStandardDTO resource) {
        PrismScope resourceScope = resource.getScope();
        ResourceRepresentationSimple resourceRepresentation = new ResourceRepresentationSimple().withScope(resourceScope).withId(resource.getId()).withName(resource.getName());
        if (resourceScope.equals(INSTITUTION)) {
            resourceRepresentation.setLogoImage(new DocumentRepresentation().withId(resource.getLogoImage()));
        }
        return resourceRepresentation;
    }

}
