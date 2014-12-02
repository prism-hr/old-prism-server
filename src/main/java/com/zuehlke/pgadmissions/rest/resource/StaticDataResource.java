package com.zuehlke.pgadmissions.rest.resource;

import static com.zuehlke.pgadmissions.utils.WordUtils.pluralize;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertDomain;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.PrismYesNoUnsureResponse;
import com.zuehlke.pgadmissions.domain.definitions.ResourceListFilterProperty;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCustomQuestionDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.imported.Country;
import com.zuehlke.pgadmissions.domain.imported.Disability;
import com.zuehlke.pgadmissions.domain.imported.Domicile;
import com.zuehlke.pgadmissions.domain.imported.Ethnicity;
import com.zuehlke.pgadmissions.domain.imported.FundingSource;
import com.zuehlke.pgadmissions.domain.imported.Gender;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedLanguageQualificationType;
import com.zuehlke.pgadmissions.domain.imported.Language;
import com.zuehlke.pgadmissions.domain.imported.QualificationType;
import com.zuehlke.pgadmissions.domain.imported.ReferralSource;
import com.zuehlke.pgadmissions.domain.imported.RejectionReason;
import com.zuehlke.pgadmissions.domain.imported.ResidenceState;
import com.zuehlke.pgadmissions.domain.imported.Title;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.institution.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.domain.workflow.StateAction;
import com.zuehlke.pgadmissions.domain.workflow.StateGroup;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;
import com.zuehlke.pgadmissions.rest.representation.InstitutionDomicileRepresentation;
import com.zuehlke.pgadmissions.rest.representation.StateRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.ProgramCategoryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.configuration.ProgramTypeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.FilterRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ImportedEntityRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ImportedInstitutionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.LanguageQualificationTypeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.ActionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.StateActionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.WorkflowDefinitionRepresentation;
import com.zuehlke.pgadmissions.services.CustomizationService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.utils.TimeZoneUtils;

@RestController
@RequestMapping("/api/static")
public class StaticDataResource {

    @Autowired
    private EntityService entityService;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private CustomizationService customizationService;

    @Autowired
    private Mapper mapper;

    @Value("${integration.google.api.key}")
    private String googleApiKey;

    private ToIdFunction toIdFunction = new ToIdFunction();

    @Cacheable("staticData")
    @RequestMapping(method = RequestMethod.GET)
    public Map<String, Object> getStaticData() {
        Map<String, Object> staticData = Maps.newHashMap();

        List<StateAction> stateActions = entityService.list(StateAction.class);
        List<StateActionRepresentation> stateActionRepresentations = Lists.newArrayListWithExpectedSize(stateActions.size());
        for (StateAction stateAction : stateActions) {
            stateActionRepresentations.add(new StateActionRepresentation(stateAction.getState().getId(), stateAction.getAction().getId(), stateAction
                    .getRaisesUrgentFlag()));
        }
        staticData.put("stateActions", stateActionRepresentations);

        List<Action> actions = entityService.list(Action.class);
        List<ActionRepresentation> actionRepresentations = Lists.newArrayListWithExpectedSize(actions.size());
        for (Action action : actions) {
            PrismActionCustomQuestionDefinition customQuestionDefinitionId = action.getActionCustomQuestionDefinition() != null ? action.getActionCustomQuestionDefinition().getId() : null;
            actionRepresentations.add(new ActionRepresentation(action.getId(), action.getActionCategory(), customQuestionDefinitionId));
        }
        staticData.put("actions", actionRepresentations);

        List<State> states = entityService.list(State.class);
        List<StateRepresentation> stateRepresentations = Lists.newArrayListWithExpectedSize(actions.size());
        for (State state : states) {
            stateRepresentations.add(new StateRepresentation(state.getId(), state.getStateGroup().getId()));
        }
        staticData.put("states", stateRepresentations);

        List<StateGroup> stateGroups = entityService.list(StateGroup.class);
        staticData.put("stateGroups", Lists.newArrayList(Iterables.transform(stateGroups, toIdFunction)));

        List<Role> roles = entityService.list(Role.class);
        staticData.put("roles", Lists.newArrayList(Iterables.transform(roles, toIdFunction)));

        List<InstitutionDomicile> institutionDomiciles = institutionService.getDomiciles();
        List<InstitutionDomicileRepresentation> institutionDomicileRepresentations = Lists.newArrayListWithExpectedSize(institutionDomiciles.size());
        for (InstitutionDomicile institutionDomicile : institutionDomiciles) {
            institutionDomicileRepresentations.add(mapper.map(institutionDomicile, InstitutionDomicileRepresentation.class));
        }
        staticData.put("institutionDomiciles", institutionDomicileRepresentations);

        List<String> currencies = institutionService.listAvailableCurrencies();
        staticData.put("currencies", currencies);

        for (Class<?> enumClass : new Class[]{PrismProgramType.class, PrismStudyOption.class, PrismYesNoUnsureResponse.class, PrismDurationUnit.class,
                PrismAdvertDomain.class, PrismAdvertFunction.class, PrismAdvertIndustry.class}) {
            String simpleName = enumClass.getSimpleName().replaceFirst("Prism", "");
            simpleName = WordUtils.uncapitalize(simpleName);
            staticData.put(pluralize(simpleName), enumClass.getEnumConstants());
        }

        staticData.put("timeZones", TimeZoneUtils.getInstance().getTimeZoneDefinitions());

        List<FilterRepresentation> filters = Lists.newArrayListWithCapacity(ResourceListFilterProperty.values().length);
        for (ResourceListFilterProperty filterProperty : ResourceListFilterProperty.values()) {
            filters.add(new FilterRepresentation(filterProperty, filterProperty.getPermittedExpressions(), filterProperty.getPropertyType(), filterProperty
                    .getPermittedScopes()));
        }
        staticData.put("filters", filters);

        Map<String, Object> workflowConfigurations = Maps.newHashMap();
        for (PrismConfiguration prismConfiguration : PrismConfiguration.values()) {
            String name = pluralize(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, prismConfiguration.name()));
            Map<PrismScope, List<WorkflowDefinitionRepresentation>> scopeConfigurations = Maps.newHashMap();
            for (PrismScope prismScope : PrismScope.values()) {
                List<? extends WorkflowDefinition> definitions = customizationService.getDefinitions(prismConfiguration, prismScope);
                List<WorkflowDefinitionRepresentation> parameters = Lists.newArrayList();
                for (WorkflowDefinition definition : definitions) {
                    parameters.add(mapper.map(definition, prismConfiguration.getDefinitionRepresentationClass()));
                }
                if (!parameters.isEmpty()) {
                    scopeConfigurations.put(prismScope, parameters);
                }
            }
            workflowConfigurations.put(name, scopeConfigurations);
        }

        List<ProgramCategoryRepresentation> programCategories = Lists.newArrayListWithCapacity(PrismProgramCategory.values().length);
        for (PrismProgramCategory prismProgramCategory : PrismProgramCategory.values()) {
            ProgramCategoryRepresentation category = new ProgramCategoryRepresentation();
            category.setId(prismProgramCategory);
            category.setDisplayName(prismProgramCategory.name());
            category.setProgramTypes(Lists.<ProgramTypeRepresentation>newLinkedList());
            for (PrismProgramType prismProgramType : PrismProgramType.values()) {
                if (prismProgramType.getProgramCategory() == prismProgramCategory) {
                    ProgramTypeRepresentation programType = new ProgramTypeRepresentation();
                    programType.setId(prismProgramType);
                    programType.setDisplayName(prismProgramType.name());
                    category.getProgramTypes().add(programType);
                }
            }
            programCategories.add(category);
        }
        staticData.put("programCategories", programCategories);

        staticData.put("workflowConfigurations", workflowConfigurations);
        staticData.put("locales", PrismLocale.values());
        staticData.put("googleApiKey", googleApiKey);

        return staticData;
    }

    @Cacheable("institutionStaticData")
    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET, params = "institutionId")
    public Map<String, Object> getStaticData(@RequestParam Integer institutionId) {
        Map<String, Object> staticData = Maps.newHashMap();

        Institution institution = entityService.getById(Institution.class, institutionId);

        // Display names for imported entities
        for (Class<? extends ImportedEntity> entityClass : new Class[]{ReferralSource.class, Title.class, Ethnicity.class, Disability.class, Gender.class,
                Country.class, Domicile.class, ReferralSource.class, Language.class, QualificationType.class, FundingSource.class, RejectionReason.class,
                ResidenceState.class}) {
            String simpleName = entityClass.getSimpleName();
            simpleName = WordUtils.uncapitalize(simpleName);
            List<? extends ImportedEntity> entities = importedEntityService.getEnabledImportedEntities(institution, entityClass);
            List<ImportedEntityRepresentation> entityRepresentations = Lists.newArrayListWithCapacity(entities.size());
            for (Object entity : entities) {
                entityRepresentations.add(mapper.map(entity, ImportedEntityRepresentation.class));
            }
            staticData.put(pluralize(simpleName), entityRepresentations);
        }

        // Display names and min/max values for language qualification types
        List<ImportedLanguageQualificationType> languageQualificationTypes = importedEntityService.getEnabledImportedEntities(institution,
                ImportedLanguageQualificationType.class);
        List<LanguageQualificationTypeRepresentation> languageQualificationTypeRepresentations = Lists.newArrayListWithCapacity(languageQualificationTypes
                .size());
        for (ImportedLanguageQualificationType languageQualificationType : languageQualificationTypes) {
            languageQualificationTypeRepresentations.add(mapper.map(languageQualificationType, LanguageQualificationTypeRepresentation.class));
        }
        staticData.put("languageQualificationTypes", languageQualificationTypeRepresentations);

        staticData.put("institution", mapper.map(institution, InstitutionRepresentation.class));
        return staticData;
    }

    @SuppressWarnings("unused")
    @RequestMapping(method = RequestMethod.GET, value = "/domiciles/{domicileId}/importedInstitutions")
    public List<ImportedInstitutionRepresentation> getImportedInstitutions(@PathVariable Integer domicileId) {
        Map<String, Object> staticData = Maps.newHashMap();

        Domicile domicile = entityService.getById(Domicile.class, domicileId);
        List<ImportedInstitution> institutions = importedEntityService.getEnabledImportedInstitutions(domicile);

        List<ImportedInstitutionRepresentation> institutionRepresentations = Lists.newArrayListWithCapacity(institutions.size());
        for (ImportedInstitution institution : institutions) {
            institutionRepresentations.add(mapper.map(institution, ImportedInstitutionRepresentation.class));
        }

        return institutionRepresentations;
    }

    @SuppressWarnings("unused")
    private class EnumDefinition {

        private String id;

        private String name;

        private EnumDefinition(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    private static class ToIdFunction implements Function<WorkflowDefinition, Object> {
        @Override
        public Object apply(WorkflowDefinition input) {
            return input.getId();
        }
    }

}
