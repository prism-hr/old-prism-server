package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_COMMENT_INITIALIZED_INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_STARTUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_CREATE_INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.dozer.Mapper;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.InstitutionDAO;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.institution.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.dto.ResourceForWhichUserCanCreateChildDTO;
import com.zuehlke.pgadmissions.dto.ResourceSearchEngineDTO;
import com.zuehlke.pgadmissions.dto.SearchEngineAdvertDTO;
import com.zuehlke.pgadmissions.dto.SitemapEntryDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.iso.jaxb.InstitutionDomiciles;
import com.zuehlke.pgadmissions.rest.dto.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.representation.InstitutionDomicileRepresentation;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Service
@Transactional
public class InstitutionService {

    @Inject
    private InstitutionDAO institutionDAO;

    @Inject
    private AdvertService advertService;

    @Inject
    private EntityService entityService;

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private SystemService systemService;

    @Inject
    private ActionService actionService;

    @Inject
    private RoleService roleService;

    @Inject
    private StateService stateService;

    @Inject
    private UserService userService;

    @Inject
    private Mapper mapper;

    @Inject
    private ApplicationContext applicationContext;

    public Institution getById(Integer id) {
        return entityService.getById(Institution.class, id);
    }

    public List<InstitutionDomicileRepresentation> getInstitutionDomiciles() {
        List<InstitutionDomicileRepresentation> representations = Lists.newLinkedList();
        List<InstitutionDomicile> institutionDomiciles = institutionDAO.getInstitutionDomiciles();
        for (InstitutionDomicile institutionDomicile : institutionDomiciles) {
            representations.add(mapper.map(institutionDomicile, InstitutionDomicileRepresentation.class));
        }
        return representations;
    }

    public List<Institution> getApprovedInstitutionsByCountry(InstitutionDomicile domicile) {
        return institutionDAO.getApprovedInstitutionsByCountry(domicile);
    }

    public Institution getUclInstitution() {
        return institutionDAO.getUclInstitution();
    }

    public Institution createPartner(User user, InstitutionDTO institutionDTO) throws Exception {
        Institution institution = (Institution) applicationContext.getBean(INSTITUTION.getResourceCreator()).create(user, institutionDTO);
        Institution persistentInstitution = entityService.getDuplicateEntity(institution);

        if (persistentInstitution == null) {
            Action action = actionService.getById(SYSTEM_CREATE_INSTITUTION);
            Role creatorRole = roleService.getById(INSTITUTION_ADMINISTRATOR);
            Comment comment = new Comment().withResource(institution).withUser(user).withAction(action).withDeclinedResponse(false)
                    .withCreatedTimestamp(new DateTime()).addAssignedUser(user, creatorRole, CREATE);
            actionService.executeUserAction(institution, action, comment);
        } else {
            institution = persistentInstitution;
        }

        return institution;
    }

    public void update(Integer institutionId, InstitutionDTO institutionDTO) throws Exception {
        Institution institution = entityService.getById(Institution.class, institutionId);

        AdvertDTO advertDTO = institutionDTO.getAdvert();
        Advert advert = institution.getAdvert();
        advertService.updateAdvert(advertDTO, advert);
        institution.setGoogleId(advert.getAddress().getGoogleId());

        institution.setTitle(advert.getTitle());
        institution.setDomicile(advert.getAddress().getDomicile());
        institution.setCurrency(institutionDTO.getCurrency());

        Integer oldBusinessYearStartMonth = institution.getBusinessYearStartMonth();
        Integer newBusinessYearStartMonth = institutionDTO.getBusinessYearStartMonth();
        if (!oldBusinessYearStartMonth.equals(newBusinessYearStartMonth)) {
            changeInstitutionBusinessYear(institution, newBusinessYearStartMonth);
        }

        institution.setMinimumWage(institutionDTO.getMinimumWage());

        LocalDate endDate = institutionDTO.getEndDate();
        if (endDate != null) {
            institution.setEndDate(endDate);
        }

        resourceService.setAttributes(institution, institutionDTO.getAttributes());
    }

    public List<String> listAvailableCurrencies() {
        return institutionDAO.listAvailableCurrencies();
    }

    public void save(Institution institution) {
        entityService.save(institution);
    }

    public void populateDefaultImportedEntityFeeds() throws DeduplicationException {
        for (Institution institution : institutionDAO.getInstitutionsWithoutImportedEntityFeeds()) {
            for (PrismImportedEntity prismImportedEntity : PrismImportedEntity.values()) {
                String defaultLocation = prismImportedEntity.getDefaultLocation();
                if (defaultLocation != null) {
                    importedEntityService.getOrCreateImportedEntityFeed(institution, prismImportedEntity, defaultLocation);
                }
            }
        }
    }

    public void initializeInstitution(Integer institutionId) throws Exception {
        Institution institution = getById(institutionId);
        User user = systemService.getSystem().getUser();
        Action action = actionService.getById(INSTITUTION_STARTUP);
        Comment comment = new Comment().withAction(action)
                .withContent(applicationContext.getBean(PropertyLoader.class).localize(institution).load(SYSTEM_COMMENT_INITIALIZED_INSTITUTION))
                .withDeclinedResponse(false).withUser(user).withCreatedTimestamp(new DateTime());
        actionService.executeAction(institution, action, comment);
    }

    public List<Integer> getInstitutionsToActivate() {
        return institutionDAO.getInstitutionsToActivate();
    }

    public List<Institution> list() {
        return institutionDAO.list();
    }

    public Institution getActivatedInstitutionByGoogleId(String googleId) {
        return institutionDAO.getActivatedInstitutionByGoogleId(googleId);
    }

    public boolean hasAuthenticatedFeeds(Institution institution) {
        return institutionDAO.getAuthenticatedFeedCount(institution) > 0;
    }

    public DateTime getLatestUpdatedTimestampSitemap(List<PrismState> programStates, List<PrismState> projectStates) {
        return institutionDAO.getLatestUpdatedTimestampSitemap(programStates, projectStates);
    }

    public List<SitemapEntryDTO> getSitemapEntries() {
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        return institutionDAO.getSitemapEntries(activeProgramStates, activeProjectStates);
    }

    public List<ResourceSearchEngineDTO> getActiveInstitutions() {
        List<PrismState> activeInstitutionStates = stateService.getActiveInstitutionStates();
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        return institutionDAO.getRelatedInstitutions(activeInstitutionStates, activeProgramStates, activeProjectStates);
    }

    public List<Institution> getInstitutions(String query, String[] googleIds) {
        return institutionDAO.getInstitutions(query, googleIds);
    }

    public void disableInstitutionDomiciles(List<String> updates) {
        institutionDAO.disableInstitutionDomiciles(updates);
    }

    public String mergeInstitutionDomicile(InstitutionDomiciles.InstitutionDomicile instituitionDomicileDefinition) throws DeduplicationException {
        InstitutionDomicile persistentInstitutionDomicile = entityService.getOrCreate(new InstitutionDomicile()
                .withId(instituitionDomicileDefinition.getIsoCode()).withName(instituitionDomicileDefinition.getName())
                .withCurrency(instituitionDomicileDefinition.getCurrency()).withEnabled(true));
        return persistentInstitutionDomicile.getId();
    }

    public String getBusinessYear(Institution institution, Integer year, Integer month) {
        Integer businessYearStartMonth = institution.getBusinessYearStartMonth();
        Integer businessYear = month < businessYearStartMonth ? (year - 1) : year;
        return month == 1 ? businessYear.toString() : (businessYear.toString() + "/" + new Integer(businessYear + 1).toString());
    }

    public Integer getMonthOfBusinessYear(Institution institution, Integer month) {
        Integer businessYearStartMonth = institution.getBusinessYearStartMonth();
        return month >= businessYearStartMonth ? (month - (businessYearStartMonth - 1)) : (month + (12 - (businessYearStartMonth - 1)));
    }

    public List<ResourceForWhichUserCanCreateChildDTO> getInstitutionsForWhichUserCanCreateProgram() {
        List<PrismState> states = stateService.getActiveInstitutionStates();
        boolean userLoggedIn = userService.getCurrentUser() != null;
        return institutionDAO.getInstitutionsForWhichUserCanCreateProgram(states, userLoggedIn);
    }

    public List<ResourceForWhichUserCanCreateChildDTO> getInstitutionsForWhichUserCanCreateProject() {
        Map<Integer, ResourceForWhichUserCanCreateChildDTO> index = Maps.newHashMap();
        Map<String, ResourceForWhichUserCanCreateChildDTO> institutions = Maps.newTreeMap();

        List<PrismState> states = stateService.getActiveInstitutionStates();
        boolean userLoggedIn = userService.getCurrentUser() != null;

        List<ResourceForWhichUserCanCreateChildDTO> institutionProjectParents = institutionDAO
                .getInstitutionsForWhichUserCanCreateProject(states, userLoggedIn);
        for (ResourceForWhichUserCanCreateChildDTO institutionProjectParent : institutionProjectParents) {
            ResourceForWhichUserCanCreateChildDTO institution = new ResourceForWhichUserCanCreateChildDTO()
                    .withResource(institutionProjectParent.getResource()).withPartnerMode(institutionProjectParent.getPartnerMode());
            index.put(institutionProjectParent.getResource().getId(), institution);
            institutions.put(institutionProjectParent.getResource().getTitle(), institution);
        }

        List<ResourceForWhichUserCanCreateChildDTO> institutionProgramProjectParents = institutionDAO
                .getInstitutionsWhichHaveProgramsForWhichUserCanCreateProject(states, userLoggedIn);
        for (ResourceForWhichUserCanCreateChildDTO institutionProgramProjectParent : institutionProgramProjectParents) {
            ResourceForWhichUserCanCreateChildDTO institution = index.get(institutionProgramProjectParent.getResource().getId());
            if (institution == null) {
                institutions.put(institutionProgramProjectParent.getResource().getTitle(),
                        new ResourceForWhichUserCanCreateChildDTO().withResource(institutionProgramProjectParent.getResource()));
            }
        }

        return Lists.newLinkedList(institutions.values());
    }

    public SearchEngineAdvertDTO getSearchEngineAdvert(Integer institutionId, List<PrismState> activeInstitutionStates, List<PrismState> activeProgramStates,
            List<PrismState> activeProjectStates) {
        return institutionDAO.getSearchEngineAdvert(institutionId, activeInstitutionStates, activeProgramStates, activeProjectStates);
    }

    private void changeInstitutionBusinessYear(Institution institution, Integer businessYearStartMonth) throws Exception {
        institution.setBusinessYearStartMonth(businessYearStartMonth);
        Integer businessYearEndMonth = businessYearStartMonth == 1 ? 12 : businessYearStartMonth - 1;
        institutionDAO.changeInstitutionBusinessYear(institution.getId(), businessYearEndMonth);
    }
}
