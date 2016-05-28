package uk.co.alumeni.prism.mapping;

import com.google.common.collect.*;
import jersey.repackaged.com.google.common.base.Objects;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import uk.co.alumeni.prism.domain.Domicile;
import uk.co.alumeni.prism.domain.Theme;
import uk.co.alumeni.prism.domain.address.Address;
import uk.co.alumeni.prism.domain.advert.*;
import uk.co.alumeni.prism.domain.definitions.*;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.*;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.dto.*;
import uk.co.alumeni.prism.rest.dto.AddressDTO;
import uk.co.alumeni.prism.rest.dto.OpportunityQueryDTO;
import uk.co.alumeni.prism.rest.representation.DocumentRepresentation;
import uk.co.alumeni.prism.rest.representation.action.ActionRepresentationResource;
import uk.co.alumeni.prism.rest.representation.address.AddressCoordinatesRepresentation;
import uk.co.alumeni.prism.rest.representation.address.AddressRepresentation;
import uk.co.alumeni.prism.rest.representation.advert.*;
import uk.co.alumeni.prism.rest.representation.advert.AdvertTargetRepresentation.AdvertTargetConnectionRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.*;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;
import uk.co.alumeni.prism.services.ActionService;
import uk.co.alumeni.prism.services.AdvertService;
import uk.co.alumeni.prism.services.UserService;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newTreeSet;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.isTrue;
import static org.springframework.beans.BeanUtils.instantiate;
import static uk.co.alumeni.prism.PrismConstants.RATING_PRECISION;
import static uk.co.alumeni.prism.dao.WorkflowDAO.advertScopes;
import static uk.co.alumeni.prism.domain.definitions.PrismConnectionState.*;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceContext.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_VIEW_EDIT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_PENDING;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_PROVIDED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeCategory.OPPORTUNITY;
import static uk.co.alumeni.prism.utils.PrismConversionUtils.doubleToBigDecimal;
import static uk.co.alumeni.prism.utils.PrismIterableUtils.containsSame;
import static uk.co.alumeni.prism.utils.PrismIterableUtils.containsSome;
import static uk.co.alumeni.prism.utils.PrismListUtils.getSummaryRepresentations;
import static uk.co.alumeni.prism.utils.PrismListUtils.processRowDescriptors;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.setProperty;
import static uk.co.alumeni.prism.utils.PrismStringUtils.endsWith;

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

    public AdvertRepresentationSimple getAdvertRepresentationSimple(Advert advert, User currentUser) {
        return getAdvertRepresentation(advert, AdvertRepresentationSimple.class, currentUser);
    }

    public AdvertListRepresentation getAdvertExtendedRepresentations(OpportunityQueryDTO query) {
        return getAdvertListRepresentation(userService.getCurrentUser(), query);
    }

    public AdvertListRepresentation getAdvertExtendedRepresentations(Integer user) {
        return getAdvertListRepresentation(userService.getById(user), new OpportunityQueryDTO().withContext(APPLICANT).withRecommendation(true));
    }

    public AdvertRepresentationExtended getAdvertRepresentationExtended(Advert advert) {
        User currentUser = userService.getCurrentUser();
        UserAdvertDTO userAdvertDTO = advertService.getUserAdverts(currentUser, advert.getResource().getResourceScope());
        if (advertService.checkAdvertVisible(advert, userAdvertDTO)) {
            AdvertRepresentationExtended representation = getAdvertRepresentation(advert, AdvertRepresentationExtended.class, currentUser);

            ResourceParent resource = advert.getResource();
            representation.setUser(userMapper.getUserRepresentationSimple(resource.getUser(), userService.getCurrentUser()));

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
                representation.setStudyOptions(((ResourceOpportunity) resource).getResourceStudyOptions().stream()
                        .map(ResourceStudyOption::getStudyOption).collect(toList()));
            }

            setOpportunityCategories(representation, resource.getOpportunityCategories());

            Boolean recommended = userAdvertDTO.getVisibleDirect().contains(advert.getId());
            representation.setRecommended(recommended);

            representation.setName(advert.getName());

            AdvertApplicationSummaryDTO applicationSummary = advertService.getAdvertApplicationSummary(advert);
            Long applicationCount = applicationSummary.getApplicationCount();
            representation.setApplicationCount(applicationCount == null ? null : applicationCount.intValue());

            Long applicationRatingCount = applicationSummary.getApplicationRatingCount();
            representation.setApplicationRatingCount(applicationRatingCount == null ? null : applicationRatingCount.intValue());
            representation.setApplicationRatingAverage(doubleToBigDecimal(applicationSummary.getApplicationRatingAverage(), RATING_PRECISION));

            representation.setTargets(getAdvertTargetConnectionRepresentations(advertService.getAdvertTargets(advert), currentUser));

            Map<Integer, AdvertRepresentationExtended> representations = ImmutableMap.of(advert.getId(), representation);
            setAdvertCallToActionStates(currentUser, representations);

            representation.setSequenceIdentifier((BooleanUtils.toBoolean(recommended) ? 1 : 0) + advert.getSequenceIdentifier());
            return representation;
        }

        return null;
    }

    public AdvertRepresentationExtended getAdvertRepresentationExtended(AdvertDTO advert) {
        AdvertRepresentationExtended representation = new AdvertRepresentationExtended();

        representation.setId(advert.getAdvertId());
        representation.setUser(new UserRepresentationSimple().withFirstName(advert.getUserFirstName()).withLastName(advert.getUserLastName())
                .withAccountProfileUrl(advert.getUserAccountProfileUrl()).withAccountImageUrl(advert.getUserAccountImageUrl()));

        for (PrismScope scope : new PrismScope[]{PROJECT, PROGRAM, DEPARTMENT, INSTITUTION}) {
            ResourceRepresentationSimple resource = getAdvertResourceRepresentation(advert, scope);
            if (resource != null) {
                setProperty(representation, scope.getLowerCamelName(), resource);
            }
        }

        representation.setOpportunityType(advert.getOpportunityType());
        setOpportunityCategories(representation, advert.getOpportunityCategories());
        representation.setRecommended(advert.getRecommended());

        representation.setName(advert.getName());
        representation.setSummary(advert.getSummary());
        representation.setDescription(advert.getDescription());
        representation.setGloballyVisible(advert.getGloballyVisible());
        representation.setPublished(advert.getPublished());
        representation.setHomepage(advert.getHomepage());
        representation.setApplyHomepage(advert.getApplyHomepage());
        representation.setTelephone(advert.getTelephone());

        representation.setAddress(new AddressRepresentation()
                .withAddressLine1(advert.getAddressLine1())
                .withAddressLine2(advert.getAddressLine2())
                .withAddressTown(advert.getAddressTown())
                .withAddressRegion(advert.getAddressRegion())
                .withAddressCode(advert.getAddressCode())
                .withDomicile(advert.getAddressDomicileId())
                .withGoogleId(advert.getAddressGoogleId())
                .withCoordinates(new AddressCoordinatesRepresentation().withLatitude(advert.getAddressCoordinateLatitude()) //
                        .withLongitude(advert.getAddressCoordinateLongitude())));

        ResourceRepresentationSimple resource = representation.getResource();
        if (ResourceOpportunityRepresentationSimple.class.isAssignableFrom(resource.getClass())) {
            ResourceOpportunityRepresentationSimple resourceOpportunity = (ResourceOpportunityRepresentationSimple) resource;
            resourceOpportunity.setDurationMinimum(advert.getDurationMinimum());
            resourceOpportunity.setDurationMaximum(advert.getDurationMaximum());
        }

        AdvertFinancialDetailRepresentation financialDetailRepresentation = new AdvertFinancialDetailRepresentation()
                .withInterval(advert.getPayInterval()).withHoursWeekMinimum(advert.getPayHoursWeekMinimum())
                .withHoursWeekMaximum(advert.getPayHoursWeekMaximum()).withPaymentOption(advert.getPayOption()).withCurrency(advert.getPayCurrency())
                .withMinimum(advert.getPayMinimum()).withMaximum(advert.getPayMaximum());

        representation.setFinancialDetail(financialDetailRepresentation);
        setAdvertFinancialDetailBenefitsRepresentation(advert.getPayBenefit(), advert.getPayBenefitDescription(), financialDetailRepresentation);

        Long applicationCount = advert.getApplicationCount();
        representation.setApplicationCount(applicationCount == null ? null : applicationCount.intValue());

        Long applicationRatingCount = advert.getApplicationRatingCount();
        representation.setApplicationRatingCount(applicationRatingCount == null ? null : applicationRatingCount.intValue());
        representation.setApplicationRatingAverage(doubleToBigDecimal(advert.getApplicationRatingAverage(), RATING_PRECISION));

        representation.setClosingDate(advert.getClosingDate());
        representation.setSequenceIdentifier(advert.getSequenceIdentifier());
        return representation;
    }

    public <T extends AdvertRepresentationSimple> T getAdvertRepresentation(Advert advert, Class<T> returnType, User currentUser) {
        T representation = instantiate(returnType);

        representation.setId(advert.getId());
        representation.setSummary(advert.getSummary());
        representation.setDescription(advert.getDescription());
        representation.setGloballyVisible(advert.getGloballyVisible());
        representation.setPublished(advert.getPublished());

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

        representation.setFinancialDetail(getAdvertFinancialDetailRepresentation(advert));
        representation.setClosingDate(advert.getClosingDate());

        representation.setCategories(getAdvertCategoriesRepresentation(advert, currentUser));
        representation.setCompetences(getAdvertCompetenceRepresentations(advert));
        representation.setExternalConditions(actionService.getExternalConditions(advert.getResource()));

        return representation;
    }

    public AddressDTO getAddressDTO(Address address) {
        AddressDTO addressDTO = addressMapper.transform(address, AddressDTO.class);
        Domicile domicile = address.getDomicile();
        addressDTO.setDomicile(domicile == null ? null : domicile.getId());
        addressDTO.setGoogleId(address.getGoogleId());
        return addressDTO;
    }

    public List<AdvertTargetRepresentation> getAdvertTargetRepresentations(List<AdvertTargetDTO> advertTargets, User user) {
        Map<ResourceRepresentationConnection, AdvertTargetRepresentation> representationIndex = Maps.newHashMap();
        TreeMultimap<AdvertTargetRepresentation, AdvertTargetConnectionRepresentation> filteredRepresentations = TreeMultimap.create();

        Set<Integer> otherAdvertIds = advertTargets.stream().filter(at -> at.getOtherUserId() == null).map(AdvertTargetDTO::getOtherAdvertId).collect(toSet());
        Map<Integer, AdvertUserDTO> advertUsers = advertService.getAdvertUsers(otherAdvertIds);

        for (AdvertTargetDTO advertTarget : advertTargets) {
            ResourceRepresentationConnection thisResourceRepresentation = resourceMapper.getResourceRepresentationConnection(
                    advertTarget.getThisInstitutionId(),
                    advertTarget.getThisInstitutionName(), advertTarget.getThisLogoImageId(), advertTarget.getThisDepartmentId(),
                    advertTarget.getThisDepartmentName());

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

            AdvertTargetConnectionRepresentation targetRepresentation = getAdvertTargetConnectionRepresentation(advertTarget, user);
            filteredRepresentations.put(representation, targetRepresentation);
        }

        List<AdvertTargetRepresentation> representations = Lists.newLinkedList();
        filteredRepresentations.keySet().forEach(representation -> {
            representation.setConnections(newLinkedList(filteredRepresentations.get(representation)));
            representations.add(representation);
        });

        return representations;
    }

    public List<AdvertTargetConnectionRepresentation> getAdvertTargetConnectionRepresentations(List<AdvertTargetDTO> advertTargets, User currentUser) {
        Set<AdvertTargetConnectionRepresentation> representations = Sets.newTreeSet();
        getAdvertTargetRepresentations(advertTargets, currentUser).forEach(advertTarget -> representations.addAll(advertTarget.getConnections()));
        return newLinkedList(representations);
    }

    public AdvertCategoriesRepresentation getAdvertCategoriesRepresentation(Advert advert, User currentUser) {
        AdvertCategories categories = advertService.getAdvertCategories(advert);
        if (categories != null) {
            List<PrismAdvertIndustry> industries = categories.getIndustries().stream().map(AdvertIndustry::getIndustry).collect(toList());
            List<PrismAdvertFunction> functions = categories.getFunctions().stream().map(AdvertFunction::getFunction).collect(toList());
            List<AdvertThemeRepresentation> themes = getAdvertThemeRepresentations(categories);
            List<ResourceRepresentationLocationRelation> locations = getAdvertLocationRepresentations(advert, categories, currentUser);

            Resource resource = advert.getResource();
            List<String> displayThemes = newLinkedList();
            List<String> displayLocations = newLinkedList();
            if (ResourceOpportunity.class.isAssignableFrom(resource.getClass())) {
                Integer advertId = advert.getId();
                PrismScope resourceScope = resource.getResourceScope();
                List<Integer> resourceIds = newArrayList(resource.getId());

                Set<String> advertDisplayThemes = advertService.getAdvertThemes(resourceScope, resourceIds).get(advertId);
                if (isNotEmpty(advertDisplayThemes)) {
                    displayThemes.addAll(advertDisplayThemes);
                }

                Set<String> advertDisplayLocations = advertService.getAdvertLocations(resourceScope, resourceIds).get(advertId);
                if (isNotEmpty(advertDisplayLocations)) {
                    displayLocations.addAll(advertDisplayLocations);
                }
            }

            return new AdvertCategoriesRepresentation().withIndustries(industries).withFunctions(functions).withThemes(themes)
                    .withLocations(locations).withThemesDisplay(displayThemes).withLocationsDisplay(displayLocations);
        }
        return null;
    }

    public List<ResourceRepresentationLocationRelation> getAdvertLocationRepresentations(Advert advert, AdvertCategories categories, User currentUser) {
        Set<ResourceRepresentationLocationRelation> locations = newTreeSet();
        getAdvertLocationRepresentations(categories, true, currentUser).stream().forEach(location -> locations.add(location));
        getAdvertLocationRepresentations(advertService.getPossibleAdvertLocations(advert), false, currentUser)
                .stream().forEach(location -> locations.add(location));
        return newLinkedList(locations);
    }

    public List<AdvertThemeRepresentation> getAdvertThemeRepresentations(AdvertCategories categories) {
        Set<AdvertTheme> advertThemes = categories.getThemes();
        List<AdvertThemeRepresentation> advertThemeRepresentations = null;
        if (isNotEmpty(advertThemes)) {
            advertThemeRepresentations = Lists.newLinkedList();
            for (AdvertTheme advertTheme : advertThemes) {
                Theme theme = advertTheme.getTheme();
                advertThemeRepresentations.add(new AdvertThemeRepresentation().withId(theme.getId()).withName(theme.getName()));
            }
        }
        return advertThemeRepresentations;
    }

    public List<AdvertCompetenceRepresentation> getAdvertCompetenceRepresentations(Advert advert) {
        return advert.getCompetences().stream()
                .map(competence -> new AdvertCompetenceRepresentation().withCompetenceId(competence.getCompetence().getId())
                        .withName(competence.getCompetence().getName())
                        .withDescription(Objects.firstNonNull(competence.getDescription(), competence.getCompetence().getDescription()))
                        .withImportance(competence.getImportance()))
                .collect(toList());
    }

    public List<AdvertIndustrySummaryRepresentation> getAdvertIndustrySummaryRepresentations(OpportunityQueryDTO query, String searchTerm) {
        return advertService.getAdvertIndustrySummaries(query, searchTerm).stream()
                .map(advertIndustrySummary -> new AdvertIndustrySummaryRepresentation().withIndustry(advertIndustrySummary.getIndustry())
                        .withAdvertCount(advertIndustrySummary.getAdvertCount())).collect(toList());
    }

    public List<AdvertFunctionSummaryRepresentation> getAdvertFunctionSummaryRepresentations(OpportunityQueryDTO query, String searchTerm) {
        return advertService.getAdvertFunctionSummaries(query, searchTerm).stream()
                .map(advertFunctionSummary -> new AdvertFunctionSummaryRepresentation().withFunction(advertFunctionSummary.getFunction())
                        .withAdvertCount(advertFunctionSummary.getAdvertCount())).collect(toList());
    }

    public List<AdvertThemeSummaryRepresentation> getAdvertThemeSummaryRepresentations(OpportunityQueryDTO query, String searchTerm) {
        return advertService.getAdvertThemeSummaries(query, searchTerm).stream()
                .map(advertThemeSummary -> new AdvertThemeSummaryRepresentation().withId(advertThemeSummary.getId())
                        .withName(advertThemeSummary.getName()).withAdvertCount(advertThemeSummary.getAdvertCount())).collect(toList());
    }

    public List<AdvertLocationSummaryRepresentation> getAdvertLocationSummaryRepresentations(OpportunityQueryDTO query, String searchTerm) {
        List<AdvertLocationSummaryDTO> advertlocationSummaries = advertService.getAdvertLocationSummaries(query, searchTerm);
        if (advertlocationSummaries.size() > 0) {
            Set<AdvertLocationSummaryRepresentation> representations = newTreeSet();
            TreeMultimap<Integer, AdvertLocationSummaryRepresentation> representationIndex = TreeMultimap.create();
            advertlocationSummaries.forEach(summaryDTO -> {
                Integer parentId = summaryDTO.getParentId();
                AdvertLocationSummaryRepresentation representation = new AdvertLocationSummaryRepresentation().withId(summaryDTO.getId())
                        .withName(summaryDTO.getName()).withAdvertCount(summaryDTO.getAdvertCount());
                if (parentId == null) {
                    representations.add(representation);
                } else {
                    representationIndex.put(parentId, representation);
                }
            });

            mapAdvertLocationAddressPartRepresentations(representations, representationIndex);
            return newLinkedList(representations);
        }

        return newArrayList();
    }

    public List<AdvertInstitutionSummaryRepresentation> getAdvertInstitutionSummaryRepresentations(OpportunityQueryDTO query, String searchTerm) {
        return advertService.getAdvertInstitutionSummaries(query, searchTerm).stream()
                .map(advertInstitutionSummary -> new AdvertInstitutionSummaryRepresentation().withInstitution(
                        new ResourceRepresentationIdentity().withScope(INSTITUTION).withId(advertInstitutionSummary.getId())
                                .withName(advertInstitutionSummary.getName())
                                .withLogoImage(documentMapper.getDocumentRepresentation(advertInstitutionSummary.getLogoImageId())))
                        .withAdvertCount(advertInstitutionSummary.getAdvertCount())).collect(toList());
    }

    public AddressRepresentation getAdvertAddressRepresentation(Advert advert) {
        Address address = advert.getAddress();
        return address == null ? null : addressMapper.getAddressRepresentation(address);
    }

    private void mapAdvertLocationAddressPartRepresentations(Set<AdvertLocationSummaryRepresentation> representations,
                                                             TreeMultimap<Integer, AdvertLocationSummaryRepresentation> representationIndex) {
        Set<AdvertLocationSummaryRepresentation> newRepresentations = newHashSet();
        if (representations.size() > 0) {
            representations.forEach(representation -> {
                Integer representationId = representation.getId();
                Set<AdvertLocationSummaryRepresentation> subRepresentations = representationIndex.get(representationId);

                subRepresentations.forEach(subRepresentation -> {
                    newRepresentations.add(subRepresentation);
                });

                representation.setChildLocations(newLinkedList(subRepresentations));
            });

            mapAdvertLocationAddressPartRepresentations(newRepresentations, representationIndex);
        }
    }

    private AdvertListRepresentation getAdvertListRepresentation(User user, OpportunityQueryDTO query) {
        PrismScope filterScope = query.getContextScope();
        PrismScope[] filterScopes = filterScope != null ? new PrismScope[]{filterScope} : query.getContext().getFilterScopes();

        Map<String, Integer> summaries = Maps.newHashMap();
        VisibleAdvertDTO visibleAdvertDTO = advertService.getVisibleAdverts(user, query, filterScopes);
        Set<AdvertOpportunityCategoryDTO> adverts = visibleAdvertDTO.getVisible();
        processRowDescriptors(adverts, summaries, query.getOpportunityTypes());

        HashMultimap<PrismScope, Integer> resources = HashMultimap.create();
        Map<Integer, AdvertRepresentationExtended> representations = newLinkedHashMap();
        advertService.getAdvertList(query, adverts).forEach(advert -> {
            PrismScope scope = advert.getScope();
            for (PrismScope advertScope : advertScopes) {
                if (advertScope.ordinal() <= scope.ordinal()) {
                    ResourceFlatToNestedDTO enclosingResourceDTO = advert.getEnclosingResource(advertScope);
                    if (enclosingResourceDTO != null) {
                        resources.put(advertScope, enclosingResourceDTO.getId());
                    }
                }
            }
            representations.put(advert.getAdvertId(), getAdvertRepresentationExtended(advert));
        });

        LinkedHashMultimap<Integer, PrismActionCondition> actionConditionIndex = LinkedHashMultimap.create();
        LinkedHashMultimap<Integer, PrismAdvertIndustry> industryIndex = LinkedHashMultimap.create();
        LinkedHashMultimap<Integer, PrismAdvertFunction> functionIndex = LinkedHashMultimap.create();
        LinkedHashMultimap<Integer, String> themeIndex = LinkedHashMultimap.create();
        LinkedHashMultimap<Integer, String> locationIndex = LinkedHashMultimap.create();
        LinkedHashMultimap<Integer, PrismStudyOption> studyOptionIndex = LinkedHashMultimap.create();
        for (PrismScope advertScope : advertScopes) {
            Set<Integer> scopedResources = resources.get(advertScope);
            if (isNotEmpty(scopedResources)) {
                LinkedHashMultimap<Integer, PrismActionCondition> actionConditions = advertService.getAdvertActionConditions(advertScope, scopedResources);
                actionConditions.keySet().forEach(advert -> {
                    Set<PrismActionCondition> advertPartnerActions = actionConditions.get(advert);
                    if (!(isEmpty(advertPartnerActions) || actionConditionIndex.containsKey(advert))) {
                        actionConditionIndex.putAll(advert, advertPartnerActions);
                    }
                });
                LinkedHashMultimap<Integer, PrismAdvertIndustry> industries = advertService.getAdvertIndustries(advertScope, scopedResources);
                industries.keySet().forEach(advert -> {
                    Set<PrismAdvertIndustry> advertIndustries = industries.get(advert);
                    if (!(isEmpty(advertIndustries) || industryIndex.containsKey(advert))) {
                        industryIndex.putAll(advert, advertIndustries);
                    }
                });
                LinkedHashMultimap<Integer, PrismAdvertFunction> functions = advertService.getAdvertFunctions(advertScope, scopedResources);
                functions.keySet().forEach(advert -> {
                    Set<PrismAdvertFunction> advertFunctions = functions.get(advert);
                    if (!(isEmpty(advertFunctions) || functionIndex.containsKey(advert))) {
                        functionIndex.putAll(advert, advertFunctions);
                    }
                });
                if (asList(PROJECT, PROGRAM).contains(advertScope)) {
                    LinkedHashMultimap<Integer, String> themes = advertService.getAdvertThemes(advertScope, scopedResources);
                    themes.keySet().forEach(advert -> {
                        Set<String> advertThemes = themes.get(advert);
                        if (!(isEmpty(advertThemes) || themeIndex.containsKey(advert))) {
                            themeIndex.putAll(advert, advertThemes);
                        }
                    });
                    LinkedHashMultimap<Integer, String> locations = advertService.getAdvertLocations(advertScope, scopedResources);
                    locations.keySet().forEach(advert -> {
                        Set<String> advertLocations = locations.get(advert);
                        if (!(isEmpty(advertLocations) || locationIndex.containsKey(advert))) {
                            locationIndex.putAll(advert, advertLocations);
                        }
                    });
                    LinkedHashMultimap<Integer, PrismStudyOption> studyOptions = advertService.getAdvertStudyOptions(advertScope, scopedResources);
                    studyOptions.keySet().forEach(advert -> {
                        Set<PrismStudyOption> advertStudyOptions = studyOptions.get(advert);
                        if (!(isEmpty(advertStudyOptions) || studyOptionIndex.containsKey(advert))) {
                            studyOptionIndex.putAll(advert, advertStudyOptions);
                        }
                    });
                }
            }
        }

        representations.keySet().forEach(advert -> {
            AdvertRepresentationExtended representation = representations.get(advert);
            representation.setExternalConditions(newLinkedList(actionConditionIndex.get(advert)));

            Set<PrismAdvertIndustry> industries = industryIndex.get(advert);
            Set<PrismAdvertFunction> functions = functionIndex.get(advert);
            Set<String> themes = themeIndex.get(advert);
            Set<String> locations = locationIndex.get(advert);

            if (isNotEmpty(industries) || isNotEmpty(functions) || isNotEmpty(themes) || isNotEmpty(locations)) {
                representation.setCategories(new AdvertCategoriesRepresentation().withIndustries(newLinkedList(industries))
                        .withFunctions(newLinkedList(functions)).withThemesDisplay(newLinkedList(themes))
                        .withLocationsDisplay(newLinkedList(locations)));
            }

            representation.setStudyOptions(newLinkedList(studyOptionIndex.get(advert)));
        });

        setAdvertCallToActionStates(user, representations);

        int invisibleCount = 0;
        Map<Integer, ResourceRepresentationOccurrence> invisibleIndex = newHashMap();
        for (AdvertOpportunityCategoryDTO invisibleAdvert : visibleAdvertDTO.getInvisible()) {
            Integer institutionId = invisibleAdvert.getInstitutionId();
            ResourceRepresentationOccurrence invisibleIndexEntry = invisibleIndex.get(institutionId);
            if (invisibleIndexEntry == null) {
                invisibleIndexEntry = resourceMapper.getResourceRepresentationOccurrence(INSTITUTION, invisibleAdvert);
                invisibleIndexEntry.setOccurrenceCount(1);
                invisibleIndex.put(institutionId, invisibleIndexEntry);
            } else {
                invisibleIndexEntry.setOccurrenceCount(invisibleIndexEntry.getOccurrenceCount() + 1);
            }

            invisibleCount++;
        }

        return new AdvertListRepresentation().withRows(newLinkedList(representations.values())).withInvisibleAdvertCount(invisibleCount)
                .withInvisibleAdvertInstitutions(newTreeSet(invisibleIndex.values())).withSummaries(getSummaryRepresentations(summaries))
                .withFilterValues(getFilterValues(query));
    }

    private Map<String, List<? extends AdvertCategorySummaryRepresentation>> getFilterValues(OpportunityQueryDTO query) {
        return ImmutableMap.of("industries", getAdvertIndustrySummaryRepresentations(query, null),
                "functions", getAdvertFunctionSummaryRepresentations(query, null),
                "themes", getAdvertThemeSummaryRepresentations(query, null),
                "locations", getAdvertLocationSummaryRepresentations(query, null),
                "institutions", getAdvertInstitutionSummaryRepresentations(query, null));
    }

    private AdvertTargetConnectionRepresentation getAdvertTargetConnectionRepresentation(AdvertTargetDTO advertTarget, User user) {
        boolean canManage = isTrue(advertTarget.getCanManage());
        boolean severed = isTrue(advertTarget.getThisAdvertSevered()) || isTrue(advertTarget.getOtherAdvertSevered());
        AdvertTargetConnectionRepresentation connectionRepresentation = new AdvertTargetConnectionRepresentation().withAdvertTargetId(advertTarget.getId())
                .withResource(resourceMapper.getResourceRepresentationConnection(advertTarget.getOtherInstitutionId(), advertTarget.getOtherInstitutionName(),
                        advertTarget.getOtherLogoImageId(), advertTarget.getOtherDepartmentId(), advertTarget.getOtherDepartmentName(),
                        advertTarget.getOtherBackgroundId()))
                .withCanManage(canManage).withSevered(severed).withSelected(isTrue(advertTarget.getSelected()));

        Integer otherUserId = advertTarget.getOtherUserId();
        if (otherUserId != null) {
            connectionRepresentation.setUser(new UserRepresentationSimple().withId(advertTarget.getOtherUserId())
                    .withFirstName(advertTarget.getOtherUserFirstName())
                    .withLastName(advertTarget.getOtherUserLastName())
                    .withEmail(userService.getSecuredUserEmailAddress(advertTarget.getOtherUserEmail(), user))
                    .withAccountProfileUrl(advertTarget.getOtherUserLinkedinProfileUrl()).withAccountImageUrl(advertTarget.getOtherUserLinkedinImageUrl())
                    .withPortraitImage(documentMapper.getDocumentRepresentation(advertTarget.getOtherUserPortraitImageId())));
        }

        PrismPartnershipState partnershipState = advertTarget.getPartnershipState();
        if (partnershipState.equals(ENDORSEMENT_PENDING)) {
            connectionRepresentation.setConnectState(PENDING);
        } else if (partnershipState.equals(ENDORSEMENT_PROVIDED)) {
            connectionRepresentation.setConnectState(ACCEPTED);
        } else if (severed) {
            connectionRepresentation.setConnectState(canManage ? REJECTED : UNKNOWN);
        }

        return connectionRepresentation;
    }

    private AdvertFinancialDetailRepresentation getAdvertFinancialDetailRepresentation(Advert advert) {
        AdvertFinancialDetail pay = advert.getPay();
        if (pay != null) {
            AdvertFinancialDetailRepresentation representation = new AdvertFinancialDetailRepresentation().withInterval(pay.getInterval())
                    .withHoursWeekMinimum(pay.getHoursWeekMinimum()).withHoursWeekMaximum(pay.getHoursWeekMaximum()).withPaymentOption(pay.getOption())
                    .withCurrency(pay.getCurrency()).withMinimum(pay.getMinimum()).withMaximum(pay.getMaximum());

            setAdvertFinancialDetailBenefitsRepresentation(pay.getBenefit(), pay.getBenefitDescription(), representation);
            return representation;
        }

        return null;
    }

    private void setAdvertFinancialDetailBenefitsRepresentation(String benefit, String benefitDescription,
                                                                AdvertFinancialDetailRepresentation financialDetailRepresentation) {
        if (benefit != null) {
            financialDetailRepresentation.setBenefits(stream(benefit.split("\\|")).map(PrismAdvertBenefit::valueOf).collect(toList()));
            financialDetailRepresentation.setBenefitsDescription(benefitDescription);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends ResourceRepresentationSimple> T getAdvertResourceRepresentation(AdvertDTO advert, PrismScope scope) {
        ResourceFlatToNestedDTO resource = advert.getEnclosingResource(scope);
        if (resource != null) {
            Class<?> representationClass = scope.getScopeCategory().equals(OPPORTUNITY) ? ResourceOpportunityRepresentationSimple.class
                    : ResourceRepresentationSimple.class;
            T resourceRepresentation = (T) BeanUtils.instantiate(representationClass);

            resourceRepresentation.setScope(scope);
            resourceRepresentation.setId(resource.getId());
            resourceRepresentation.setName(resource.getName());

            if (scope.equals(INSTITUTION)) {
                resourceRepresentation.setLogoImage(documentMapper.getDocumentRepresentation(advert.getLogoImageId()));
            }

            return resourceRepresentation;
        }
        return null;
    }

    private void setOpportunityCategories(AdvertRepresentationExtended representation, String opportunityCategories) {
        if (opportunityCategories != null) {
            representation.setOpportunityCategories(asList(opportunityCategories.split("\\|")).stream().map(PrismOpportunityCategory::valueOf)
                    .collect(toList()));
        }
    }

    private void setAdvertCallToActionStates(User user, Map<Integer, AdvertRepresentationExtended> representations) {
        Set<Integer> advertIds = representations.keySet();
        if (isNotEmpty(advertIds)) {
            HashMultimap<String, AdvertCategoryDTO> advertUserRoles = HashMultimap.create();
            advertService.getAdvertsForWhichUserHasRoles(user).stream().forEach(advert -> {
                String roleName = advert.getRole().name();
                if (endsWith(roleName, "ADMINISTRATOR", "APPROVER", "VIEWER")) {
                    advertUserRoles.put("staff", advert);
                } else if (endsWith(roleName, "VIEWER_UNVERIFIED", "VIEWER_REJECTED")) {
                    advertUserRoles.put("staffPending", advert);
                } else if (endsWith(roleName, "STUDENT")) {
                    advertUserRoles.put("student", advert);
                } else if (endsWith(roleName, "STUDENT_UNVERIFIED", "STUDENT_REJECTED")) {
                    advertUserRoles.put("studentPending", advert);
                }
            });

            setAdvertApplyActions(user, representations);
            setAdvertJoinStates(user, advertUserRoles, representations);
            setAdvertConnectStates(user, advertUserRoles.get("staff"), representations);
        }
    }

    private void setAdvertApplyActions(User user, Map<Integer, AdvertRepresentationExtended> representations) {
        List<AdvertApplicationDTO> advertsApplyingFor = advertService.getAdvertsUserApplyingFor(user, representations.keySet());
        Map<Integer, Integer> advertsMap = advertsApplyingFor.stream().collect(
                Collectors.toMap(AdvertApplicationDTO::getAdvertId, AdvertApplicationDTO::getApplicationId));
        if (advertsApplyingFor.size() > 0) {
            representations.values().stream().forEach(representation -> {
                PrismScope resourceScope = representation.getResource().getScope();
                if (resourceScope.getScopeCategory().equals(OPPORTUNITY)) {
                    Integer applicationId = advertsMap.get(representation.getId());
                    PrismAction action;
                    ResourceRepresentationIdentity resourceRepresentation;
                    if (applicationId != null) {
                        action = APPLICATION_VIEW_EDIT;
                        resourceRepresentation = new ResourceRepresentationIdentity().withId(applicationId).withScope(APPLICATION);
                    } else {
                        action = PrismAction.valueOf(resourceScope.name() + "_CREATE_APPLICATION");
                        resourceRepresentation = new ResourceRepresentationIdentity().withId(representation.getId()).withScope(
                                PrismScope.valueOf(resourceScope.name()));
                    }
                    ActionRepresentationResource actionRepresentation = new ActionRepresentationResource()
                            .withId(action).withCategory(action.getActionCategory()).withResource(resourceRepresentation);
                    representation.setAction(actionRepresentation);
                }
            });
        }
    }

    private void setAdvertJoinStates(User user, HashMultimap<String, AdvertCategoryDTO> advertUserRoles,
                                     Map<Integer, AdvertRepresentationExtended> representations) {
        List<Integer> advertsAsStaff = advertUserRoles.get("staff").stream().map(advert -> advert.getAdvert()).collect(toList());
        List<Integer> advertsAsStaffPending = advertUserRoles.get("staffPending").stream().map(advert -> advert.getAdvert()).collect(toList());
        List<Integer> advertsAsStudent = advertUserRoles.get("student").stream().map(advert -> advert.getAdvert()).collect(toList());
        List<Integer> advertsAsStudentPending = advertUserRoles.get("studentPending").stream().map(advert -> advert.getAdvert()).collect(toList());

        representations.keySet().forEach(representation -> {
            representations.get(representation).setJoinStateStaff(getAdvertJoinState(representation, advertsAsStaff, advertsAsStaffPending));
            representations.get(representation).setJoinStateStudent(getAdvertJoinState(representation, advertsAsStudent, advertsAsStudentPending));
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

    private void setAdvertConnectStates(User user, Collection<AdvertCategoryDTO> advertsAsStaff, Map<Integer, AdvertRepresentationExtended> representations) {
        List<AdvertTarget> targets = advertService.getAdvertTargetsForAdverts(representations.keySet());

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
                Set<PrismResourceContext> ownerContexts = getResourceContexts(advertAsStaff.getOpportunityCategories());
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
                if (containsSame(pendingFor, advertAsStaffIds)) {
                    representations.get(advert).setConnectState(PENDING);
                } else if (containsSome(pendingFor, advertAsStaffIds)) {
                    representations.get(advert).setConnectState(PENDING_PARTIAL);
                } else if (containsSame(acceptedFor, advertAsStaffIds)) {
                    representations.get(advert).setConnectState(ACCEPTED);
                } else if (containsSome(acceptedFor, advertAsStaffIds)) {
                    representations.get(advert).setConnectState(ACCEPTED_PARTIAL);
                } else {
                    representations.get(advert).setConnectState(UNKNOWN);
                }
            } else {
                representations.get(advert).setConnectState(UNKNOWN);
            }

        });
    }

    private Set<PrismResourceContext> getAdvertResourceContexts(Advert advert) {
        return getResourceContexts(advert.getOpportunityCategories());
    }

    private boolean matchAdvertResourceContexts(Set<PrismResourceContext> ownerContexts, Set<PrismResourceContext> targetContexts) {
        return (ownerContexts.contains(UNIVERSITY) && targetContexts.contains(EMPLOYER))
                || (ownerContexts.contains(EMPLOYER) && targetContexts.contains(UNIVERSITY));
    }

    private void setAdvertConnectState(HashMultimap<Integer, Integer> pendingForIndex, HashMultimap<Integer, Integer> acceptedForIndex, AdvertTarget target,
                                       Integer ownerAdvert, Integer targetAdvert) {
        if (target.getPartnershipState().equals(ENDORSEMENT_PROVIDED)) {
            acceptedForIndex.put(targetAdvert, ownerAdvert);
        } else {
            pendingForIndex.put(targetAdvert, ownerAdvert);
        }
    }

    private List<ResourceRepresentationLocationRelation> getAdvertLocationRepresentations(AdvertCategories categories, boolean selected, User currentUser) {
        return getAdvertLocationRepresentations(categories.getLocations().stream()
                .map(location -> location.getLocationAdvert()).collect(toList()), selected, currentUser);
    }

    private List<ResourceRepresentationLocationRelation> getAdvertLocationRepresentations(Collection<Advert> locationAdverts, boolean selected,
                                                                                          User currentUser) {
        return locationAdverts.stream().map(locationAdvert -> resourceMapper.getResourceRepresentationRelationLocation(locationAdvert.getResource(),
                locationAdvert.getAddress(), selected, currentUser)).collect(toList());
    }

}
