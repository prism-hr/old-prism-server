package uk.co.alumeni.prism.mapping;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.isTrue;
import static uk.co.alumeni.prism.PrismConstants.RATING_PRECISION;
import static uk.co.alumeni.prism.domain.definitions.PrismConnectionState.ACCEPTED;
import static uk.co.alumeni.prism.domain.definitions.PrismConnectionState.ACCEPTED_PARTIAL;
import static uk.co.alumeni.prism.domain.definitions.PrismConnectionState.PENDING;
import static uk.co.alumeni.prism.domain.definitions.PrismConnectionState.PENDING_PARTIAL;
import static uk.co.alumeni.prism.domain.definitions.PrismConnectionState.REJECTED;
import static uk.co.alumeni.prism.domain.definitions.PrismConnectionState.UNKNOWN;
import static uk.co.alumeni.prism.domain.definitions.PrismDurationUnit.YEAR;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceContext.APPLICANT;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceContext.EMPLOYER;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceContext.UNIVERSITY;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_PENDING;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_PROVIDED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.INSTITUTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROGRAM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROJECT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.getResourceContexts;
import static uk.co.alumeni.prism.utils.PrismCollectionUtils.containsSame;
import static uk.co.alumeni.prism.utils.PrismCollectionUtils.containsSome;
import static uk.co.alumeni.prism.utils.PrismConversionUtils.doubleToBigDecimal;
import static uk.co.alumeni.prism.utils.PrismListUtils.getSummaryRepresentations;
import static uk.co.alumeni.prism.utils.PrismListUtils.processRowDescriptors;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.getProperty;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.setProperty;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

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

import jersey.repackaged.com.google.common.base.Objects;
import uk.co.alumeni.prism.domain.Domicile;
import uk.co.alumeni.prism.domain.Theme;
import uk.co.alumeni.prism.domain.address.Address;
import uk.co.alumeni.prism.domain.address.AddressCoordinates;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.advert.AdvertCategories;
import uk.co.alumeni.prism.domain.advert.AdvertFinancialDetail;
import uk.co.alumeni.prism.domain.advert.AdvertFunction;
import uk.co.alumeni.prism.domain.advert.AdvertIndustry;
import uk.co.alumeni.prism.domain.advert.AdvertTarget;
import uk.co.alumeni.prism.domain.definitions.PrismAdvertFunction;
import uk.co.alumeni.prism.domain.definitions.PrismAdvertIndustry;
import uk.co.alumeni.prism.domain.definitions.PrismConnectionState;
import uk.co.alumeni.prism.domain.definitions.PrismDurationUnit;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;
import uk.co.alumeni.prism.domain.definitions.PrismResourceContext;
import uk.co.alumeni.prism.domain.definitions.PrismStudyOption;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.Department;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.resource.Program;
import uk.co.alumeni.prism.domain.resource.Project;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceOpportunity;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.dto.AdvertApplicationSummaryDTO;
import uk.co.alumeni.prism.dto.AdvertCategoryDTO;
import uk.co.alumeni.prism.dto.AdvertDTO;
import uk.co.alumeni.prism.dto.AdvertTargetDTO;
import uk.co.alumeni.prism.dto.AdvertUserDTO;
import uk.co.alumeni.prism.dto.EntityOpportunityFilterDTO;
import uk.co.alumeni.prism.dto.ResourceFlatToNestedDTO;
import uk.co.alumeni.prism.rest.dto.AddressDTO;
import uk.co.alumeni.prism.rest.dto.OpportunitiesQueryDTO;
import uk.co.alumeni.prism.rest.representation.DocumentRepresentation;
import uk.co.alumeni.prism.rest.representation.address.AddressCoordinatesRepresentation;
import uk.co.alumeni.prism.rest.representation.address.AddressRepresentation;
import uk.co.alumeni.prism.rest.representation.advert.AdvertCategoriesRepresentation;
import uk.co.alumeni.prism.rest.representation.advert.AdvertCompetenceRepresentation;
import uk.co.alumeni.prism.rest.representation.advert.AdvertFinancialDetailRepresentation;
import uk.co.alumeni.prism.rest.representation.advert.AdvertListRepresentation;
import uk.co.alumeni.prism.rest.representation.advert.AdvertRepresentationExtended;
import uk.co.alumeni.prism.rest.representation.advert.AdvertRepresentationSimple;
import uk.co.alumeni.prism.rest.representation.advert.AdvertTargetRepresentation;
import uk.co.alumeni.prism.rest.representation.advert.AdvertTargetRepresentation.AdvertTargetConnectionRepresentation;
import uk.co.alumeni.prism.rest.representation.advert.AdvertThemeRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceOpportunityRepresentationSimple;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationConnection;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationRelation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationSimple;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;
import uk.co.alumeni.prism.services.ActionService;
import uk.co.alumeni.prism.services.AdvertService;
import uk.co.alumeni.prism.services.UserService;

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
        return getAdvertExtendedRepresentations(userService.getCurrentUser(), query);
    }

    public AdvertListRepresentation getAdvertExtendedRepresentations(Integer user) {
        return getAdvertExtendedRepresentations(userService.getById(user), new OpportunitiesQueryDTO().withContext(APPLICANT).withRecommendation(true));
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
                    representation.setOpportunityCategories(asList(opportunityCategories.split("\\|")).stream().map(PrismOpportunityCategory::valueOf).collect(toList()));
                }

                setTargetOpportunityTypes(representation, advert.getTargetOpportunityTypes());
                representation.setStudyOptions(((ResourceOpportunity) resource).getResourceStudyOptions().stream().map(rso -> rso.getStudyOption()).collect(toList()));
            }

            representation.setTargets(getAdvertTargetConnectionRepresentations(advertService.getAdvertTargets(advert)));
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

        representation.setId(advert.getAdvertId());
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

        representation.setClosingDate(advert.getClosingDate());
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
        representation.setClosingDate(advert.getClosingDate());

        representation.setCategories(getAdvertCategoriesRepresentation(advert));
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

    public List<AdvertTargetConnectionRepresentation> getAdvertTargetConnectionRepresentations(List<AdvertTargetDTO> advertTargets) {
        Set<AdvertTargetConnectionRepresentation> representations = Sets.newTreeSet();
        getAdvertTargetRepresentations(advertTargets).forEach(advertTarget -> {
            representations.addAll(advertTarget.getConnections());
        });
        return newLinkedList(representations);
    }

    public AdvertCategoriesRepresentation getAdvertCategoriesRepresentation(Advert advert) {
        AdvertCategories categories = advertService.getAdvertCategories(advert);
        if (categories != null) {
            List<PrismAdvertIndustry> industries = categories.getIndustries().stream().map(AdvertIndustry::getIndustry).collect(toList());
            List<PrismAdvertFunction> functions = categories.getFunctions().stream().map(AdvertFunction::getFunction).collect(toList());

            List<AdvertThemeRepresentation> themes = Lists.newLinkedList();
            categories.getThemes().forEach(advertTheme -> {
                Theme theme = advertTheme.getTheme();
                themes.add(new AdvertThemeRepresentation().withId(theme.getId()).withName(theme.getName()));
            });

            List<ResourceRepresentationRelation> locations = categories.getLocations().stream()
                    .map(al -> resourceMapper.getResourceRepresentationRelation(al.getLocationAdvert().getResource())).collect(Collectors.toList());

            return new AdvertCategoriesRepresentation().withIndustries(industries).withFunctions(functions).withThemes(themes).withLocations(locations);
        }
        return null;
    }

    public List<AdvertCompetenceRepresentation> getAdvertCompetenceRepresentations(Advert advert) {
        return advert.getCompetences().stream()
                .map(competence -> new AdvertCompetenceRepresentation().withCompetenceId(competence.getCompetence().getId())
                        .withName(competence.getCompetence().getName())
                        .withDescription(Objects.firstNonNull(competence.getDescription(), competence.getCompetence().getDescription()))
                        .withImportance(competence.getImportance()))
                .collect(toList());
    }

    private AdvertListRepresentation getAdvertExtendedRepresentations(User user, OpportunitiesQueryDTO query) {
        PrismScope filterScope = query.getContextScope();
        PrismScope[] filterScopes = filterScope != null ? new PrismScope[] { filterScope } : query.getContext().getFilterScopes();

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

    private AdvertTargetConnectionRepresentation getAdvertTargetConnectionRepresentation(AdvertTargetDTO advertTarget) {
        boolean canManage = isTrue(advertTarget.getCanManage());
        boolean severed = isTrue(advertTarget.getThisAdvertSevered()) || isTrue(advertTarget.getOtherAdvertSevered());
        AdvertTargetConnectionRepresentation connectionRepresentation = new AdvertTargetConnectionRepresentation().withAdvertTargetId(advertTarget.getId())
                .withResource(resourceMapper.getResourceRepresentationConnection(advertTarget.getOtherInstitutionId(), advertTarget.getOtherInstitutionName(),
                        advertTarget.getOtherInstitutionLogoImageId(), advertTarget.getOtherDepartmentId(), advertTarget.getOtherDepartmentName(),
                        advertTarget.getOtherBackgroundId()))
                .withCanManage(canManage).withSevered(severed).withSelected(isTrue(advertTarget.getSelected()));

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
        } else if (severed) {
            connectionRepresentation.setConnectState(canManage ? REJECTED : UNKNOWN);
        }

        return connectionRepresentation;
    }

    private AdvertFinancialDetailRepresentation getAdvertFinancialDetailRepresentation(Advert advert) {
        AdvertFinancialDetail pay = advert.getPay();
        if (pay != null) {
            return new AdvertFinancialDetailRepresentation().withCurrency(pay.getCurrency()).withInterval(pay.getInterval()).withMinimum(pay.getMinimum())
                    .withMaximum(pay.getMaximum());
        }
        return null;
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
