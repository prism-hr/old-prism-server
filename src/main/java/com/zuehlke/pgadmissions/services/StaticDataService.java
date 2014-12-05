package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.utils.WordUtils.pluralize;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.zuehlke.pgadmissions.rest.representation.workflow.WorkflowDefinitionRepresentation;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.TimeZoneUtils;

@Service
@Transactional
public class StaticDataService {

    @Value("${integration.google.api.key}")
    private String googleApiKey;

    private ToIdFunction toIdFunction = new ToIdFunction();

    @Autowired
    private EntityService entityService;

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private CustomizationService customizationService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private UserService userService;

    @Autowired
    private Mapper mapper;

    @Autowired
    private ApplicationContext applicationContext;
    
    public Map<String, Object> getActions() {
        Map<String, Object> staticData = Maps.newHashMap();
        
        List<Action> actions = entityService.list(Action.class);
        List<ActionRepresentation> actionRepresentations = Lists.newArrayListWithExpectedSize(actions.size());
        for (Action action : actions) {
            PrismActionCustomQuestionDefinition customQuestionDefinitionId = action.getActionCustomQuestionDefinition() != null ? action
                    .getActionCustomQuestionDefinition().getId() : null;
            actionRepresentations.add(new ActionRepresentation(action.getId(), action.getActionCategory(), customQuestionDefinitionId));
        }
        
        staticData.put("actions", actionRepresentations);
        return staticData;
    }

    public Map<String, Object> getStates() {
        Map<String, Object> staticData = Maps.newHashMap();
        
        List<State> states = entityService.list(State.class);
        List<StateRepresentation> stateRepresentations = Lists.newArrayListWithExpectedSize(states.size());
        for (State state : states) {
            stateRepresentations.add(new StateRepresentation(state.getId(), state.getStateGroup().getId()));
        }
        
        staticData.put("states", stateRepresentations);
        return staticData;
    }
    
    public Map<String, Object> getStateGroups() {
        Map<String, Object> staticData = Maps.newHashMap();
        List<StateGroup> stateGroups = entityService.list(StateGroup.class);
        staticData.put("stateGroups", Lists.newArrayList(Iterables.transform(stateGroups, toIdFunction)));
        return staticData;
    }
    
    public Map<String, Object> getRoles() {
        Map<String, Object> staticData = Maps.newHashMap();
        List<Role> roles = entityService.list(Role.class);
        staticData.put("roles", Lists.newArrayList(Iterables.transform(roles, toIdFunction)));
        return staticData;
    }

    public Map<String, Object> getInstitutionDomiciles() {
        Map<String, Object> staticData = Maps.newHashMap();
        
        List<InstitutionDomicile> institutionDomiciles = institutionService.getDomiciles();
        List<InstitutionDomicileRepresentation> institutionDomicileRepresentations = Lists.newArrayListWithExpectedSize(institutionDomiciles.size());
        for (InstitutionDomicile institutionDomicile : institutionDomiciles) {
            institutionDomicileRepresentations.add(mapper.map(institutionDomicile, InstitutionDomicileRepresentation.class));
        }
        
        staticData.put("institutionDomiciles", institutionDomicileRepresentations);
        return staticData;
    }

    public Map<String, Object> getSimpleProperties() {
        Map<String, Object> staticData = Maps.newHashMap();
        
        for (Class<?> enumClass : new Class[] { PrismProgramType.class, PrismStudyOption.class, PrismYesNoUnsureResponse.class, PrismDurationUnit.class,
                PrismAdvertDomain.class, PrismAdvertFunction.class, PrismAdvertIndustry.class }) {
            String simpleName = enumClass.getSimpleName().replaceFirst("Prism", "");
            simpleName = WordUtils.uncapitalize(simpleName);
            staticData.put(pluralize(simpleName), enumClass.getEnumConstants());
        }

        staticData.put("timeZones", TimeZoneUtils.getInstance().getTimeZoneDefinitions());
        staticData.put("currencies", institutionService.listAvailableCurrencies());
        staticData.put("locales", PrismLocale.values());
        staticData.put("googleApiKey", googleApiKey);
        
        return staticData;
    }

    public Map<String, Object> getFilterProperties() {
        Map<String, Object> staticData = Maps.newHashMap();
        
        List<FilterRepresentation> filters = Lists.newArrayListWithCapacity(ResourceListFilterProperty.values().length);
        for (ResourceListFilterProperty filterProperty : ResourceListFilterProperty.values()) {
            filters.add(new FilterRepresentation(filterProperty, filterProperty.getPermittedExpressions(), filterProperty.getPropertyType(), filterProperty
                    .getPermittedScopes()));
        }
        
        staticData.put("filters", filters);
        return staticData;
    }

    public Map<String, Object> getConfigurations() {
        Map<String, Object> staticData = Maps.newHashMap();
        
        Map<String, Object> configurations = Maps.newHashMap();
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
            configurations.put(name, scopeConfigurations);
        }

        staticData.put("workflowConfigurations", configurations);
        return staticData;
    }

    public Map<String, Object> getProgramCategories() {
        Map<String, Object> staticData = Maps.newHashMap();
        PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localize(systemService.getSystem(), userService.getCurrentUser());

        List<ProgramCategoryRepresentation> programCategories = Lists.newArrayListWithCapacity(PrismProgramCategory.values().length);
        for (PrismProgramCategory prismProgramCategory : PrismProgramCategory.values()) {
            ProgramCategoryRepresentation category = new ProgramCategoryRepresentation();
            category.setId(prismProgramCategory);
            category.setDisplayName(loader.load(prismProgramCategory.getDisplayProperty()));
            category.setHasFee(prismProgramCategory.isHasFee());
            category.setHasPay(prismProgramCategory.isHasFee());
            category.setProgramTypes(Lists.<ProgramTypeRepresentation> newLinkedList());
            for (PrismProgramType prismProgramType : PrismProgramType.values()) {
                if (prismProgramType.getProgramCategory() == prismProgramCategory) {
                    ProgramTypeRepresentation programType = new ProgramTypeRepresentation();
                    programType.setId(prismProgramType);
                    programType.setDisplayName(loader.load(prismProgramType.getDisplayProperty()));
                    category.getProgramTypes().add(programType);
                }
            }
            programCategories.add(category);
        }
        
        staticData.put("programCategories", programCategories);
        return staticData;
    }


    @SuppressWarnings("unchecked")
    public Map<String, Object> getImportedData(Integer institutionId) {
        Map<String, Object> staticData = Maps.newHashMap();

        Institution institution = entityService.getById(Institution.class, institutionId);

        for (Class<? extends ImportedEntity> entityClass : new Class[] { ReferralSource.class, Title.class, Ethnicity.class, Disability.class, Gender.class,
                Country.class, Domicile.class, ReferralSource.class, Language.class, QualificationType.class, FundingSource.class, RejectionReason.class,
                ResidenceState.class }) {
            String simpleName = entityClass.getSimpleName();
            simpleName = WordUtils.uncapitalize(simpleName);
            List<? extends ImportedEntity> entities = importedEntityService.getEnabledImportedEntities(institution, entityClass);
            List<ImportedEntityRepresentation> entityRepresentations = Lists.newArrayListWithCapacity(entities.size());
            for (Object entity : entities) {
                entityRepresentations.add(mapper.map(entity, ImportedEntityRepresentation.class));
            }
            staticData.put(pluralize(simpleName), entityRepresentations);
        }

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

    public List<ImportedInstitutionRepresentation> getImportedInstitutions(Integer domicileId) {
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
