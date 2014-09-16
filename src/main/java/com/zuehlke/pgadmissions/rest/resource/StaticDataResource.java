package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.definitions.FilterProperty;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.YesNoUnsureResponse;
import com.zuehlke.pgadmissions.rest.representation.StateRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ImportedEntityRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ImportedInstitutionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.LanguageQualificationTypeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.ActionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.FilterRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.StateActionRepresentation;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.utils.TimeZoneList;
import org.apache.commons.lang.WordUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.zuehlke.pgadmissions.utils.WordUtils.pluralize;

@RestController
@RequestMapping("/api/static")
public class StaticDataResource {

    @Autowired
    private EntityService entityService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private Mapper dozerBeanMapper;

    @Autowired
    private ApplicationContext applicationContext;

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

        List<InstitutionDomicile> institutionDomiciles = entityService.listByProperty(InstitutionDomicile.class, "enabled", true);
        staticData.put("institutionDomiciles", institutionDomiciles);

        List<String> currencies = institutionService.listAvailableCurrencies();
        staticData.put("currencies", currencies);

        // Display names for enum classes
        for (Class<?> enumClass : new Class[]{PrismProgramType.class, YesNoUnsureResponse.class, PrismStudyOption.class}) {
            List<EnumDefinition> definitions = Lists.newArrayListWithExpectedSize(enumClass.getEnumConstants().length);
            String simpleName = enumClass.getSimpleName();
            if (simpleName.startsWith("Prism")) {
                simpleName = simpleName.replaceFirst("Prism", "");
            }
            simpleName = WordUtils.uncapitalize(simpleName);
            for (java.lang.Object key : enumClass.getEnumConstants()) {
                String message = applicationContext.getMessage(simpleName + "." + key, null, LocaleContextHolder.getLocale());
                definitions.add(new EnumDefinition(key.toString(), message));
            }
            staticData.put(pluralize(simpleName), definitions);
        }

        staticData.put("timeZones", TimeZoneList.getInstance().getTimeZoneDefinitions());

        List<FilterRepresentation> filters = Lists.newArrayListWithCapacity(FilterProperty.values().length);
        for (FilterProperty filterProperty : FilterProperty.values()) {
            filters.add(new FilterRepresentation(filterProperty, filterProperty.getPermittedExpressions(), filterProperty.getPropertyType(), filterProperty.getPermittedScopes()));
        }
        staticData.put("filters", filters);

        return staticData;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET, params = "institutionId")
    public Map<String, Object> getStaticData(@RequestParam Integer institutionId) {
        Map<String, Object> staticData = Maps.newHashMap();

        Institution institution = entityService.getById(Institution.class, institutionId);

        // Display names for imported entities
        for (Class<Object> importedEntityType : new Class[]{ReferralSource.class, Title.class, Ethnicity.class, Disability.class,
                Gender.class, Country.class, Domicile.class, ReferralSource.class, Language.class, QualificationType.class, FundingSource.class,
                RejectionReason.class, ResidenceState.class}) {
            String simpleName = importedEntityType.getSimpleName();
            simpleName = WordUtils.uncapitalize(simpleName);
            List<Object> entities = entityService.listByProperties(importedEntityType, ImmutableMap.of("institution", institution, "enabled", true));
            List<ImportedEntityRepresentation> entityRepresentations = Lists.newArrayListWithCapacity(entities.size());
            for (Object studyOption : entities) {
                entityRepresentations.add(dozerBeanMapper.map(studyOption, ImportedEntityRepresentation.class));
            }
            staticData.put(pluralize(simpleName), entityRepresentations);
        }

        // Display names and min/max values for language qualification types
        List<ImportedLanguageQualificationType> languageQualificationTypes = entityService.listByProperty(ImportedLanguageQualificationType.class, "institution", institution);
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
        List<ImportedInstitution> institutions = entityService.listByProperties(ImportedInstitution.class, ImmutableMap.of("domicile", domicile, "enabled", true));

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

    private static class ToIdFunction implements Function<WorkflowResource, Object> {
        @Override
        public Object apply(WorkflowResource input) {
            return input.getId();
        }
    }

}
