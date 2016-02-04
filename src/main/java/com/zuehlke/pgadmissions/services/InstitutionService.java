package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_CREATE_INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.dozer.Mapper;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.InstitutionDAO;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.comment.Comment;
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
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.rest.representation.InstitutionDomicileRepresentation;

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
    private ResourceService resourceService;

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

    public List<Integer> getApprovedInstitutions() {
        return institutionDAO.getApprovedInstitutions();
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

    public void update(Institution institution, InstitutionDTO institutionDTO) throws Exception {
        AdvertDTO advertDTO = institutionDTO.getAdvert();
        Advert advert = institution.getAdvert();
        advertService.updateAdvert(institution.getParentResource(), advert, advertDTO);
        institution.setGoogleId(advert.getAddress().getGoogleId());

        institution.setTitle(advert.getTitle());
        institution.setDomicile(advert.getAddress().getDomicile());

        String oldCurrency = institution.getCurrency();
        String newCurrency = institutionDTO.getCurrency();
        if (!oldCurrency.equals(newCurrency)) {
            changeInstitutionCurrency(institution, oldCurrency, newCurrency);
        }

        Integer oldBusinessYearStartMonth = institution.getBusinessYearStartMonth();
        Integer newBusinessYearStartMonth = institutionDTO.getBusinessYearStartMonth();
        if (!oldBusinessYearStartMonth.equals(newBusinessYearStartMonth)) {
            changeInstitutionBusinessYear(institution, newBusinessYearStartMonth);
        }

        institution.setMinimumWage(institutionDTO.getMinimumWage());
        resourceService.setResourceAttributes(institution, institutionDTO.getAttributes());
    }

    public List<String> listAvailableCurrencies() {
        return institutionDAO.listAvailableCurrencies();
    }

    public void save(Institution institution) {
        entityService.save(institution);
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
        List<PrismState> activeInstitutionStates = stateService.getActiveInstitutionStates();
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        return institutionDAO.getSitemapEntries(activeInstitutionStates, activeProgramStates, activeProjectStates);
    }

    public List<ResourceSearchEngineDTO> getActiveInstitutions() {
        List<PrismState> activeInstitutionStates = stateService.getActiveInstitutionStates();
        List<PrismState> activeProgramStates = stateService.getActiveProgramStates();
        List<PrismState> activeProjectStates = stateService.getActiveProjectStates();
        return institutionDAO.getRelatedInstitutions(activeInstitutionStates, activeProgramStates, activeProjectStates);
    }

    public List<Institution> getInstitutions(String searchTerm, String[] googleIds) {
        return institutionDAO.getInstitutions(searchTerm, googleIds);
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
        return businessYearStartMonth == 1 ? businessYear.toString() : (businessYear.toString() + "/" + new Integer(businessYear + 1).toString());
    }

    public List<ResourceForWhichUserCanCreateChildDTO> getInstitutionsForWhichUserCanCreateProgram() {
        List<PrismState> states = stateService.getActiveInstitutionStates();
        boolean userLoggedIn = userService.getCurrentUser() != null;
        return institutionDAO.getInstitutionsForWhichUserCanCreateProgram(states, userLoggedIn);
    }

    public List<ResourceForWhichUserCanCreateChildDTO> getInstitutionsForWhichUserCanCreateProject() {
        Map<Integer, ResourceForWhichUserCanCreateChildDTO> index = Maps.newHashMap();
        Map<String, ResourceForWhichUserCanCreateChildDTO> institutions = Maps.newTreeMap();

        List<PrismState> institutionStates = stateService.getActiveInstitutionStates();
        boolean userLoggedIn = userService.getCurrentUser() != null;

        List<ResourceForWhichUserCanCreateChildDTO> institutionProjectParents = institutionDAO
                .getInstitutionsForWhichUserCanCreateProject(institutionStates, userLoggedIn);
        for (ResourceForWhichUserCanCreateChildDTO institutionProjectParent : institutionProjectParents) {
            ResourceForWhichUserCanCreateChildDTO institution = new ResourceForWhichUserCanCreateChildDTO()
                    .withResource(institutionProjectParent.getResource()).withPartnerMode(institutionProjectParent.getPartnerMode());
            index.put(institutionProjectParent.getResource().getId(), institution);
            institutions.put(institutionProjectParent.getResource().getTitle(), institution);
        }

        List<PrismState> programStates = stateService.getActiveProgramStates();

        List<ResourceForWhichUserCanCreateChildDTO> institutionProgramProjectParents = institutionDAO
                .getInstitutionsWhichHaveProgramsForWhichUserCanCreateProject(programStates, userLoggedIn);
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

    private void changeInstitutionCurrency(Institution institution, String oldCurrency, String newCurrency) throws Exception {
        List<Advert> advertsWithFeesAndPays = advertService.getAdvertsWithFeesAndPays(institution);
        for (Advert advertWithFeesAndPays : advertsWithFeesAndPays) {
            advertService.updateFeesAndPayments(advertWithFeesAndPays, newCurrency);
        }

        List<Advert> advertsWithSponsorship = advertService.getAdvertsWithSponsorship(institution);
        for (Advert advertWithSponsorship : advertsWithSponsorship) {
            advertService.updateSponsorship(advertWithSponsorship, oldCurrency, newCurrency);
        }

        institution.setCurrency(newCurrency);
    }

    private void changeInstitutionBusinessYear(Institution institution, Integer businessYearStartMonth) throws Exception {
        institution.setBusinessYearStartMonth(businessYearStartMonth);
        Integer businessYearEndMonth = businessYearStartMonth == 1 ? 12 : businessYearStartMonth - 1;
        institutionDAO.changeInstitutionBusinessYear(institution.getId(), businessYearEndMonth);
    }

}
