package com.zuehlke.pgadmissions.mapping;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.zuehlke.pgadmissions.PrismConstants.RATING_PRECISION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismConnectionState.ACCEPTED;
import static com.zuehlke.pgadmissions.domain.definitions.PrismConnectionState.PENDING;
import static com.zuehlke.pgadmissions.domain.definitions.PrismConnectionState.REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.PrismConnectionState.UNKNOWN;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit.YEAR;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceContext.EMPLOYER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceContext.UNIVERSITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_PENDING;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_PROVIDED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.getResourceContexts;
import static com.zuehlke.pgadmissions.utils.PrismCollectionUtils.containsSame;
import static com.zuehlke.pgadmissions.utils.PrismConversionUtils.doubleToBigDecimal;
import static com.zuehlke.pgadmissions.utils.PrismListUtils.getSummaryRepresentations;
import static com.zuehlke.pgadmissions.utils.PrismListUtils.processRowDescriptors;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.getProperty;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.setProperty;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.isTrue;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.address.Address;
import com.zuehlke.pgadmissions.domain.address.AddressCoordinates;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.advert.AdvertCategories;
import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.advert.AdvertFinancialDetail;
import com.zuehlke.pgadmissions.domain.advert.AdvertFunction;
import com.zuehlke.pgadmissions.domain.advert.AdvertIndustry;
import com.zuehlke.pgadmissions.domain.advert.AdvertTarget;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;
import com.zuehlke.pgadmissions.domain.definitions.PrismConnectionState;
import com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismResourceContext;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.AdvertApplicationSummaryDTO;
import com.zuehlke.pgadmissions.dto.AdvertCategoryDTO;
import com.zuehlke.pgadmissions.dto.AdvertDTO;
import com.zuehlke.pgadmissions.dto.AdvertTargetDTO;
import com.zuehlke.pgadmissions.dto.AdvertUserDTO;
import com.zuehlke.pgadmissions.dto.EntityOpportunityFilterDTO;
import com.zuehlke.pgadmissions.dto.ResourceFlatToNestedDTO;
import com.zuehlke.pgadmissions.rest.dto.AddressDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunitiesQueryDTO;
import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.address.AddressCoordinatesRepresentation;
import com.zuehlke.pgadmissions.rest.representation.address.AddressRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertCategoriesRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertClosingDateRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertCompetenceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertFinancialDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertListRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertTargetRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertTargetRepresentation.AdvertTargetConnectionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceOpportunityRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationConnection;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.UserService;

@Service
@Transactional
public class AdvertMapper {

    @Inject
    private ActionService actionService;

    @Inject
    private AdvertService advertService;

    @Inject
    private UserService userService;

    @Inject
    private AddressMapper addressMapper;

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private DocumentMapper documentMapper;

    @Inject
    private UserMapper userMapper;

    public AdvertRepresentationSimple getAdvertRepresentationSimple(Advert advert) {
        return getAdvertRepresentation(advert, AdvertRepresentationSimple.class);
    }

    public AdvertListRepresentation getAdvertExtendedRepresentations(OpportunitiesQueryDTO query) {
        PrismScope filterScope = query.getContextScope();
        PrismScope[] filterScopes = filterScope != null ? new PrismScope[] { filterScope } : query.getContext().getFilterScopes();

        User user = userService.getCurrentUser();
        Set<Integer> advertIds = Sets.newHashSet();
        Map<String, Integer> summaries = Maps.newHashMap();
        Set<EntityOpportunityFilterDTO> adverts = advertService.getVisibleAdverts(user, query, filterScopes);
        processRowDescriptors(adverts, advertIds, summaries, query.getOpportunityTypes());

        PrismScope[] parentScopes = new PrismScope[] { PROJECT, PROGRAM, DEPARTMENT, INSTITUTION };

        HashMultimap<PrismScope, Integer> resources = HashMultimap.create();
        Map<Integer, AdvertRepresentationExtended> index = Maps.newLinkedHashMap();
        advertService.getAdvertList(query, advertIds).forEach(advert -> {
            PrismScope scope = advert.getScope();
            for (PrismScope advertScope : parentScopes) {
                if (advertScope.ordinal() <= scope.ordinal()) {
                    ResourceFlatToNestedDTO enclosingResourceDTO = advert.getEnclosingResource(advertScope);
                    if (enclosingResourceDTO != null) {
                        resources.put(advertScope, enclosingResourceDTO.getId());
                    }
                }
            }
            index.put(advert.getAdvertId(), getAdvertRepresentationExtended(advert));
        });

        LinkedHashMultimap<Integer, PrismStudyOption> studyOptionIndex = LinkedHashMultimap.create();
        LinkedHashMultimap<Integer, PrismActionCondition> actionConditionIndex = LinkedHashMultimap.create();
        LinkedHashMultimap<Integer, PrismAdvertIndustry> industryIndex = LinkedHashMultimap.create();
        LinkedHashMultimap<Integer, PrismAdvertFunction> functionIndex = LinkedHashMultimap.create();
        for (PrismScope parentScope : parentScopes) {
            Set<Integer> scopedResources = resources.get(parentScope);
            if (isNotEmpty(scopedResources)) {
                LinkedHashMultimap<Integer, PrismActionCondition> actionConditions = advertService.getAdvertActionConditions(parentScope, scopedResources);
                actionConditions.keySet().forEach(advert -> {
                    Set<PrismActionCondition> advertPartnerActions = actionConditions.get(advert);
                    if (!(isEmpty(advertPartnerActions) || actionConditionIndex.containsKey(advert))) {
                        actionConditionIndex.putAll(advert, advertPartnerActions);
                    }
                });
                LinkedHashMultimap<Integer, PrismAdvertIndustry> industries = advertService.getAdvertIndustries(parentScope, scopedResources);
                industries.keySet().forEach(advert -> {
                    Set<PrismAdvertIndustry> advertIndustries = industries.get(advert);
                    if (!(isEmpty(advertIndustries) || industryIndex.containsKey(advert))) {
                        industryIndex.putAll(advert, advertIndustries);
                    }
                });
                LinkedHashMultimap<Integer, PrismAdvertFunction> functions = advertService.getAdvertFunctions(parentScope, scopedResources);
                functions.keySet().forEach(advert -> {
                    Set<PrismAdvertFunction> advertFunctions = functions.get(advert);
                    if (!(isEmpty(advertFunctions) || functionIndex.containsKey(advert))) {
                        functionIndex.putAll(advert, advertFunctions);
                    }
                });
                if (asList(PROJECT, PROGRAM).contains(parentScope)) {
                    LinkedHashMultimap<Integer, PrismStudyOption> studyOptions = advertService.getAdvertStudyOptions(parentScope, scopedResources);
                    studyOptions.keySet().forEach(advert -> {
                        Set<PrismStudyOption> advertStudyOptions = studyOptions.get(advert);
                        if (!(isEmpty(advertStudyOptions) || studyOptionIndex.containsKey(advert))) {
                            studyOptionIndex.putAll(advert, advertStudyOptions);
                        }
                    });
                }
            }
        }

        Map<Integer, AdvertRepresentationExtended> representations = Maps.newLinkedHashMap();
        index.keySet().forEach(advert -> {
            AdvertRepresentationExtended representation = index.get(advert);
            representation.setExternalConditions(newLinkedList(actionConditionIndex.get(advert)));
            representation.setStudyOptions(newLinkedList(studyOptionIndex.get(advert)));

            Set<PrismAdvertIndustry> industries = industryIndex.get(advert);
            Set<PrismAdvertFunction> functions = functionIndex.get(advert);
            if (isNotEmpty(industries) || isNotEmpty(functions)) {
                representation.setCategories(new AdvertCategoriesRepresentation().withIndustries(newLinkedList(industries)).withFunctions(newLinkedList(functions)));
            }

            representations.put(advert, representation);
        });

        setAdvertCallToActionStates(user, advertIds, representations);
        return new AdvertListRepresentation().withRows(newLinkedList(representations.values())).withSummaries(getSummaryRepresentations(summaries));
    }

    public AdvertRepresentationExtended getAdvertRepresentationExtended(Advert advert) {
        User user = userService.getCurrentUser();

        Set<Integer> userAdverts = advertService.getUserAdverts(user, advert.getResource().getResourceScope());
        if (isTrue(advert.getGloballyVisible()) || (userAdverts != null && userAdverts.contains(advert.getId()))) {
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
                            .setOpportunityCategories(asList(opportunityCategories.split("\\|")).stream().map(PrismOpportunityCategory::valueOf).collect(Collectors.toList()));
                }

                setTargetOpportunityTypes(representation, advert.getTargetOpportunityTypes());
                representation.setStudyOptions(((ResourceOpportunity) resource).getResourceStudyOptions().stream().map(rso -> rso.getStudyOption()).collect(toList()));
            }

            representation.setName(advert.getName());

            AdvertApplicationSummaryDTO applicationSummary = advertService.getAdvertApplicationSummary(advert);
            Long applicationCount = applicationSummary.getApplicationCount();
            representation.setApplicationCount(applicationCount == null ? null : applicationCount.intValue());

            Long applicationRatingCount = applicationSummary.getApplicationRatingCount();
            representation.setApplicationRatingCount(applicationRatingCount == null ? null : applicationRatingCount.intValue());
            representation.setApplicationRatingAverage(doubleToBigDecimal(applicationSummary.getApplicationRatingAverage(), RATING_PRECISION));

            List<Integer> advertIds = newArrayList(advert.getId());
            Map<Integer, AdvertRepresentationExtended> representations = ImmutableMap.of(advert.getId(), representation);

            setAdvertCallToActionStates(user, advertIds, representations);
            return representation;
        }

        return null;
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
        setTargetOpportunityTypes(representation, advert.getTargetOpportunityTypes());

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
            representation.setFinancialDetails(payRepresentation);
        }

        Long applicationCount = advert.getApplicationCount();
        representation.setApplicationCount(applicationCount == null ? null : applicationCount.intValue());

        Long applicationRatingCount = advert.getApplicationRatingCount();
        representation.setApplicationRatingCount(applicationRatingCount == null ? null : applicationRatingCount.intValue());
        representation.setApplicationRatingAverage(doubleToBigDecimal(advert.getApplicationRatingAverage(), RATING_PRECISION));

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

        Resource resource = advert.getResource();
        if (resource.getResourceScope().ordinal() > INSTITUTION.ordinal()) {
            representation.setParentAddress(getAdvertAddressRepresentation(resource.getParentResource().getAdvert()));
        }

        representation.setFinancialDetails(getAdvertFinancialDetailRepresentation(advert));

        representation.setClosingDate(getAdvertClosingDateRepresentation(advert));
        representation.setClosingDates(getAdvertClosingDateRepresentations(advert));

        representation.setCategories(getAdvertCategoriesRepresentation(advert));
        representation.setTargets(getAdvertTargetRepresentations(advertService.getAdvertTargets(advert)));
        representation.setCompetences(getAdvertCompetenceRepresentations(advert));
        representation.setExternalConditions(actionService.getExternalConditions(advert.getResource()));

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

    public List<AdvertTargetRepresentation> getAdvertTargetRepresentations(List<AdvertTargetDTO> advertTargets) {
        Map<ResourceRepresentationConnection, AdvertTargetRepresentation> representationIndex = Maps.newHashMap();
        TreeMultimap<AdvertTargetRepresentation, AdvertTargetConnectionRepresentation> filteredRepresentations = TreeMultimap.create();

        Set<Integer> otherAdvertIds = advertTargets.stream().filter(at -> at.getOtherUserId() == null).map(at -> at.getOtherAdvertId()).collect(toSet());
        Map<Integer, AdvertUserDTO> advertUsers = advertService.getAdvertUsers(otherAdvertIds);

        for (AdvertTargetDTO advertTarget : advertTargets) {
            ResourceRepresentationConnection thisResourceRepresentation = resourceMapper.getResourceRepresentationConnection(advertTarget.getThisInstitutionId(),
                    advertTarget.getThisInstitutionName(), advertTarget.getThisInstitutionLogoImageId(), advertTarget.getThisDepartmentId(), advertTarget.getThisDepartmentName());

            AdvertTargetRepresentation representation = representationIndex.get(thisResourceRepresentation);
            if (representation == null) {
                representation = new AdvertTargetRepresentation().withResource(thisResourceRepresentation);
                representationIndex.put(thisResourceRepresentation, representation);
            }

            Integer otherUserId = advertTarget.getOtherUserId();
            if (otherUserId == null) {
                AdvertUserDTO advertUser = advertUsers.get(advertTarget.getOtherAdvertId());
                advertTarget.setOtherUserId(advertUser.getUserId());
                advertTarget.setOtherUserFirstName(advertUser.getUserFirstName());
                advertTarget.setOtherUserLastName(advertUser.getUserLastName());
                advertTarget.setOtherUserEmail(advertUser.getUserEmail());
                advertTarget.setOtherUserLinkedinProfileUrl(advertUser.getUserLinkedinProfileUrl());
                advertTarget.setOtherUserLinkedinImageUrl(advertUser.getUserLinkedinImageUrl());
                advertTarget.setOtherUserPortraitImageId(advertUser.getUserPortraitImageId());
            }

            AdvertTargetConnectionRepresentation targetRepresentation = getAdvertTargetConnectionRepresentation(advertTarget);
            filteredRepresentations.put(representation, targetRepresentation);
        }

        List<AdvertTargetRepresentation> representations = Lists.newLinkedList();
        filteredRepresentations.keySet().forEach(representation -> {
            representation.setConnections(newLinkedList(filteredRepresentations.get(representation)));
            representations.add(representation);
        });

        return representations;
    }

    private AdvertTargetConnectionRepresentation getAdvertTargetConnectionRepresentation(AdvertTargetDTO advertTarget) {
        boolean canManage = isTrue(advertTarget.getCanManage());
        AdvertTargetConnectionRepresentation connectionRepresentation = new AdvertTargetConnectionRepresentation().withAdvertTargetId(advertTarget.getId())
                .withResource(resourceMapper.getResourceRepresentationConnection(advertTarget.getOtherInstitutionId(), advertTarget.getOtherInstitutionName(),
                        advertTarget.getOtherInstitutionLogoImageId(), advertTarget.getOtherDepartmentId(), advertTarget.getOtherDepartmentName(),
                        advertTarget.getOtherBackgroundId()))
                .withCanManage(canManage);

        Integer otherUserId = advertTarget.getOtherUserId();
        if (otherUserId != null) {
            connectionRepresentation.setUser(new UserRepresentationSimple().withId(advertTarget.getOtherUserId()).withFirstName(advertTarget.getOtherUserFirstName())
                    .withLastName(advertTarget.getOtherUserLastName()).withEmail(advertTarget.getOtherUserEmail())
                    .withAccountProfileUrl(advertTarget.getOtherUserLinkedinProfileUrl()).withAccountImageUrl(advertTarget.getOtherUserLinkedinImageUrl())
                    .withPortraitImage(documentMapper.getDocumentRepresentation(advertTarget.getOtherUserPortraitImageId())));
        }

        PrismPartnershipState partnershipState = advertTarget.getPartnershipState();
        if (partnershipState.equals(ENDORSEMENT_PENDING)) {
            connectionRepresentation.setConnectState(PENDING);
        } else if (partnershipState.equals(ENDORSEMENT_PROVIDED)) {
            connectionRepresentation.setConnectState(ACCEPTED);
        } else if (BooleanUtils.isTrue(advertTarget.getSevered())) {
            connectionRepresentation.setConnectState(canManage ? REJECTED : UNKNOWN);
        }

        return connectionRepresentation;
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
        List<PrismAdvertIndustry> industries = categories.getIndustries().stream().map(AdvertIndustry::getIndustry).collect(toList());
        List<PrismAdvertFunction> functions = categories.getFunctions().stream().map(AdvertFunction::getFunction).collect(toList());
        return new AdvertCategoriesRepresentation().withIndustries(industries).withFunctions(functions);
    }

    public List<AdvertCompetenceRepresentation> getAdvertCompetenceRepresentations(Advert advert) {
        return advert.getCompetences().stream()
                .map(competence -> new AdvertCompetenceRepresentation().withCompetenceId(competence.getCompetence().getId())
                        .withName(competence.getCompetence().getName())
                        .withDescription(competence.getCompetence().getDescription()).withImportance(competence.getImportance()))
                .collect(toList());
    }

    @SuppressWarnings("unchecked")
    private <T extends ResourceRepresentationSimple> T getAdvertResourceRepresentation(AdvertDTO advert, PrismScope scope) {
        ResourceFlatToNestedDTO resource = advert.getEnclosingResource(scope);
        if (resource != null) {
            boolean isOpportunity = asList(PROGRAM, PROJECT).contains(scope);
            Class<?> representationClass = isOpportunity ? ResourceOpportunityRepresentationSimple.class : ResourceRepresentationSimple.class;
            T resourceRepresentation = (T) BeanUtils.instantiate(representationClass);

            resourceRepresentation.setScope(scope);
            resourceRepresentation.setId(resource.getId());
            resourceRepresentation.setName(resource.getName());

            if (isOpportunity) {
                String scopeReference = scope.getLowerCamelName();
                ResourceOpportunityRepresentationSimple resourceOpportunityRepresentation = (ResourceOpportunityRepresentationSimple) resourceRepresentation;
                resourceOpportunityRepresentation.setAvailableDate((LocalDate) getProperty(advert, scopeReference + "AvailableDate"));
                resourceOpportunityRepresentation.setDurationMinimum((Integer) getProperty(advert, scopeReference + "DurationMinimum"));
                resourceOpportunityRepresentation.setDurationMaximum((Integer) getProperty(advert, scopeReference + "DurationMaximum"));
            } else if (scope.equals(INSTITUTION)) {
                resourceRepresentation.setLogoImage(documentMapper.getDocumentRepresentation(advert.getLogoImageId()));
            }

            return resourceRepresentation;
        }
        return null;
    }

    private void setTargetOpportunityTypes(AdvertRepresentationExtended representation, String targetOpportunityTypes) {
        if (targetOpportunityTypes != null) {
            representation.setTargetOpportunityTypes(asList(targetOpportunityTypes.split("\\|")).stream().map(PrismOpportunityType::valueOf).collect(toList()));
        }
    }

    private void setAdvertCallToActionStates(User user, Collection<Integer> advertIds, Map<Integer, AdvertRepresentationExtended> representations) {
        if (isNotEmpty(advertIds)) {
            setAdvertJoinStates(user, advertIds, representations);
            setAdvertConnectStates(user, advertIds, representations);
        }
    }

    public void setAdvertJoinStates(User user, Collection<Integer> advertIds, Map<Integer, AdvertRepresentationExtended> representations) {
        List<Integer> advertsAsStaff = advertService.getAdvertsForWhichUserHasRolesStrict(user, new String[] { "ADMINISTRATOR", "APPROVER", "VIEWER" }, advertIds);
        List<Integer> advertsAsStaffPending = advertService.getAdvertsForWhichUserHasRolesStrict(user, new String[] { "VIEWER_UNVERIFIED", "VIEWER_REJECTED" }, advertIds);
        List<Integer> advertsAsStudent = advertService.getAdvertsForWhichUserHasRolesStrict(user, new String[] { "STUDENT" }, advertIds);
        List<Integer> advertsAsStudentPending = advertService.getAdvertsForWhichUserHasRolesStrict(user, new String[] { "STUDENT_UNVERIFIED", "STUDENT_REJECTED" }, advertIds);

        representations.keySet().forEach(advert -> {
            representations.get(advert).setJoinStateStaff(getAdvertJoinState(advert, advertsAsStaff, advertsAsStaffPending));
            representations.get(advert).setJoinStateStudent(getAdvertJoinState(advert, advertsAsStudent, advertsAsStudentPending));
        });
    }

    private PrismConnectionState getAdvertJoinState(Integer advert, List<Integer> advertsUserApprovedFor, List<Integer> advertsUserPendingFor) {
        if (advertsUserApprovedFor.contains(advert)) {
            return ACCEPTED;
        } else if (advertsUserPendingFor.contains(advert)) {
            return PENDING;
        }
        return UNKNOWN;
    }

    private void setAdvertConnectStates(User user, Collection<Integer> advertIds, Map<Integer, AdvertRepresentationExtended> representations) {
        List<AdvertTarget> targets = advertService.getAdvertTargetsForAdverts(advertIds);
        List<AdvertCategoryDTO> advertsAsStaff = advertService.getAdvertsForWhichUserHasRolesStrict(user, new String[] { "ADMINISTRATOR", "APPROVER", "VIEWER" });

        HashMultimap<Integer, Integer> pendingForIndex = HashMultimap.create();
        HashMultimap<Integer, Integer> acceptedForIndex = HashMultimap.create();
        targets.forEach(target -> {
            Advert advert = target.getAdvert();
            Advert targetAdvert = target.getTargetAdvert();

            Integer advertId = advert.getId();
            Integer targetAdvertId = targetAdvert.getId();

            Set<PrismResourceContext> advertContexts = getAdvertResourceContexts(advert);
            Set<PrismResourceContext> targetAdvertContexts = getAdvertResourceContexts(targetAdvert);
            advertsAsStaff.stream().forEach(advertAsStaff -> {
                Integer advertAsStaffId = advertAsStaff.getAdvert();
                Set<PrismResourceContext> ownerContexts = getAdvertResourceContexts(advertAsStaff.getOpportunityCategories());
                if (advertAsStaffId.equals(advertId) && matchAdvertResourceContexts(ownerContexts, targetAdvertContexts)) {
                    setAdvertConnectState(pendingForIndex, acceptedForIndex, target, advertId, targetAdvertId);
                } else if (advertAsStaffId.equals(targetAdvertId) && matchAdvertResourceContexts(ownerContexts, advertContexts)) {
                    setAdvertConnectState(pendingForIndex, acceptedForIndex, target, targetAdvertId, advertId);
                }
            });
        });

        int advertsAsStaffCount = advertsAsStaff.size();
        List<Integer> advertAsStaffIds = advertsAsStaff.stream().map(AdvertCategoryDTO::getAdvert).collect(Collectors.toList());
        representations.keySet().forEach(advert -> {
            Set<Integer> pendingFor = pendingForIndex.get(advert);
            Set<Integer> acceptedFor = acceptedForIndex.get(advert);

            if (advertsAsStaffCount > 0) {
                if (containsSame(acceptedFor, advertAsStaffIds)) {
                    representations.get(advert).setConnectState(ACCEPTED);
                } else if (containsSame(pendingFor, advertAsStaffIds)) {
                    representations.get(advert).setConnectState(PENDING);
                } else {
                    representations.get(advert).setConnectState(UNKNOWN);
                }
            } else {
                representations.get(advert).setConnectState(UNKNOWN);
            }

        });
    }

    private Set<PrismResourceContext> getAdvertResourceContexts(Advert advert) {
        return getAdvertResourceContexts(advert.getOpportunityCategories());
    }

    private Set<PrismResourceContext> getAdvertResourceContexts(String opportunityCategories) {
        return getResourceContexts(opportunityCategories);
    }

    private boolean matchAdvertResourceContexts(Set<PrismResourceContext> ownerContexts, Set<PrismResourceContext> targetContexts) {
        return (ownerContexts.contains(UNIVERSITY) && targetContexts.contains(EMPLOYER)) || (ownerContexts.contains(EMPLOYER) && targetContexts.contains(UNIVERSITY));
    }

    public void setAdvertConnectState(HashMultimap<Integer, Integer> pendingForIndex, HashMultimap<Integer, Integer> acceptedForIndex, AdvertTarget target, Integer ownerAdvert,
            Integer targetAdvert) {
        if (target.getPartnershipState().equals(ENDORSEMENT_PROVIDED)) {
            acceptedForIndex.put(targetAdvert, ownerAdvert);
        } else {
            pendingForIndex.put(targetAdvert, ownerAdvert);
        }
    }

}
