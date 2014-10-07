package com.zuehlke.pgadmissions.rest.resource;

import static com.zuehlke.pgadmissions.utils.WordUtils.pluralize;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.FundingSource;
import com.zuehlke.pgadmissions.domain.Gender;
import com.zuehlke.pgadmissions.domain.ImportedEntity;
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.ImportedLanguageQualificationType;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.QualificationType;
import com.zuehlke.pgadmissions.domain.ReferralSource;
import com.zuehlke.pgadmissions.domain.RejectionReason;
import com.zuehlke.pgadmissions.domain.ResidenceState;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateGroup;
import com.zuehlke.pgadmissions.domain.Title;
import com.zuehlke.pgadmissions.domain.WorkflowDefinition;
import com.zuehlke.pgadmissions.domain.definitions.DurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.FilterProperty;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.YesNoUnsureResponse;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplateProperty;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.representation.StateRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ImportedEntityRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ImportedInstitutionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.LanguageQualificationTypeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.ActionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.FilterRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.StateActionRepresentation;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.services.NotificationService;
import com.zuehlke.pgadmissions.utils.TimeZoneList;

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
    private NotificationService notificationService;

    @Autowired
    private Mapper dozerBeanMapper;

    private ToIdFunction toIdFunction = new ToIdFunction();

    @RequestMapping(method = RequestMethod.GET)
    public Map<String, Object> getStaticData() {
        Map<String, Object> staticData = Maps.newHashMap();

        List<StateAction> stateActions = entityService.list(StateAction.class);
        List<StateActionRepresentation> stateActionRepresentations = Lists.newArrayListWithExpectedSize(stateActions.size());
        for (StateAction stateAction : stateActions) {
            stateActionRepresentations.add(new StateActionRepresentation(stateAction.getState().getId(), stateAction.getAction().getId(), stateAction.getRaisesUrgentFlag()));
        }
        staticData.put("stateActions", stateActionRepresentations);

        List<Action> actions = entityService.list(Action.class);
        List<ActionRepresentation> actionRepresentations = Lists.newArrayListWithExpectedSize(actions.size());
        for (Action action : actions) {
            actionRepresentations.add(new ActionRepresentation(action.getId(), action.getActionCategory()));
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
        staticData.put("institutionDomiciles", institutionDomiciles);

        List<String> currencies = institutionService.listAvailableCurrencies();
        staticData.put("currencies", currencies);

        // Display names for enum classes
        for (Class<?> enumClass : new Class[]{PrismProgramType.class, PrismStudyOption.class, YesNoUnsureResponse.class, DurationUnit.class}) {
            String simpleName = enumClass.getSimpleName();
            if (simpleName.startsWith("Prism")) {
                simpleName = simpleName.replaceFirst("Prism", "");
            }
            simpleName = WordUtils.uncapitalize(simpleName);
            staticData.put(pluralize(simpleName), enumClass.getEnumConstants());
        }

        staticData.put("timeZones", TimeZoneList.getInstance().getTimeZoneDefinitions());

        List<FilterRepresentation> filters = Lists.newArrayListWithCapacity(FilterProperty.values().length);
        for (FilterProperty filterProperty : FilterProperty.values()) {
            filters.add(new FilterRepresentation(filterProperty, filterProperty.getPermittedExpressions(), filterProperty.getPropertyType(), filterProperty.getPermittedScopes()));
        }
        staticData.put("filters", filters);

        Map<PrismScope, List<PrismNotificationTemplate>> templatesMap = Maps.newHashMap();
        for (PrismScope prismScope : PrismScope.values()) {
            List<PrismNotificationTemplate> templates = notificationService.getEditableTemplates(prismScope);
            templatesMap.put(prismScope, templates);
        }
        staticData.put("notificationTemplatesPerScope", templatesMap);

        List<Map<String, Object>> templateDefinitions = Lists.newArrayListWithCapacity(PrismNotificationTemplate.values().length);
        for (PrismNotificationTemplate template : PrismNotificationTemplate.values()) {
            Map<String, Object> definition = Maps.newHashMap();
            definition.put("id", template.name());
            if (template.getReminderTemplate() != null) {
                definition.put("reminderTemplate", template.getReminderTemplate().name());
            }
            List<PrismNotificationTemplatePropertyCategory> categories = Lists.asList(PrismNotificationTemplatePropertyCategory.GLOBAL, template.getPropertyCategories());
            List<PrismNotificationTemplateProperty> properties = Lists.newLinkedList();
            for (PrismNotificationTemplateProperty property : PrismNotificationTemplateProperty.values()) {
                if(categories.contains(property.getCategory())){
                    properties.add(property);
                }
            }
            definition.put("properties", properties);
            templateDefinitions.add(definition);
        }
        staticData.put("notificationTemplates", templateDefinitions);

        return staticData;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET, params = "institutionId")
    public Map<String, Object> getStaticData(@RequestParam Integer institutionId) {
        Map<String, Object> staticData = Maps.newHashMap();

        Institution institution = entityService.getById(Institution.class, institutionId);

        // Display names for imported entities
        for (Class<? extends ImportedEntity> entityClass : new Class[]{ReferralSource.class, Title.class, Ethnicity.class, Disability.class,
                Gender.class, Country.class, Domicile.class, ReferralSource.class, Language.class, QualificationType.class, FundingSource.class,
                RejectionReason.class, ResidenceState.class}) {
            String simpleName = entityClass.getSimpleName();
            simpleName = WordUtils.uncapitalize(simpleName);
            List<? extends ImportedEntity> entities = importedEntityService.getEnabledImportedEntities(institution, entityClass);
            List<ImportedEntityRepresentation> entityRepresentations = Lists.newArrayListWithCapacity(entities.size());
            for (Object entity : entities) {
                entityRepresentations.add(dozerBeanMapper.map(entity, ImportedEntityRepresentation.class));
            }
            staticData.put(pluralize(simpleName), entityRepresentations);
        }

        // Display names and min/max values for language qualification types
        List<ImportedLanguageQualificationType> languageQualificationTypes = importedEntityService.getEnabledImportedEntities(institution, ImportedLanguageQualificationType.class);
        List<LanguageQualificationTypeRepresentation> languageQualificationTypeRepresentations = Lists.newArrayListWithCapacity(languageQualificationTypes
                .size());
        for (ImportedLanguageQualificationType languageQualificationType : languageQualificationTypes) {
            languageQualificationTypeRepresentations.add(dozerBeanMapper.map(languageQualificationType, LanguageQualificationTypeRepresentation.class));
        }
        staticData.put("languageQualificationTypes", languageQualificationTypeRepresentations);

        staticData.put("institution", dozerBeanMapper.map(institution, InstitutionRepresentation.class));
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
            institutionRepresentations.add(dozerBeanMapper.map(institution, ImportedInstitutionRepresentation.class));
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
