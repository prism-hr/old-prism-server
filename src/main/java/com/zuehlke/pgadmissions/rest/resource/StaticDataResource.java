package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.definitions.Gender;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.rest.representation.application.ImportedEntityRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.ActionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.RoleRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.StateActionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.workflow.StateRepresentation;
import com.zuehlke.pgadmissions.services.EntityService;
import org.apache.commons.lang.WordUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.zuehlke.pgadmissions.utils.WordUtils.pluralize;

@RestController
@RequestMapping("/api/static")
public class StaticDataResource {

    @Autowired
    private EntityService entityService;

    @Autowired
    private Mapper dozerBeanMapper;

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping(method = RequestMethod.GET)
    public Map<String, Object> getStaticData() {
        Map<String, Object> staticData = Maps.newHashMap();

        List<StateAction> stateActions = entityService.list(StateAction.class);
        List<StateActionRepresentation> stateActionRepresentations = Lists.newArrayListWithExpectedSize(stateActions.size());
        for (StateAction stateAction : stateActions) {
            StateActionRepresentation actionRepresentation = dozerBeanMapper.map(stateAction, StateActionRepresentation.class);
            stateActionRepresentations.add(actionRepresentation);
        }
        staticData.put("stateActions", stateActionRepresentations);

        List<Action> actions = entityService.list(Action.class);
        List<ActionRepresentation> actionRepresentations = Lists.newArrayListWithExpectedSize(actions.size());
        for (Action action : actions) {
            ActionRepresentation actionRepresentation = dozerBeanMapper.map(action, ActionRepresentation.class);
            actionRepresentation.setDisplayName(applicationContext.getMessage("action." + actionRepresentation.getId().toString(), null, LocaleContextHolder.getLocale()));
            actionRepresentations.add(actionRepresentation);
        }
        staticData.put("actions", actionRepresentations);

        List<State> states = entityService.list(State.class);
        List<StateRepresentation> stateRepresentations = Lists.newArrayListWithExpectedSize(states.size());
        for (State state : states) {
            StateRepresentation stateRepresentation = dozerBeanMapper.map(state, StateRepresentation.class);
            stateRepresentation.setDisplayName(applicationContext.getMessage("state." + state.getParentState().getId().toString(), null, LocaleContextHolder.getLocale()));
            stateRepresentations.add(stateRepresentation);
        }
        staticData.put("states", stateRepresentations);

        List<Role> roles = entityService.list(Role.class);
        List<RoleRepresentation> roleRepresentationsRepresentations = Lists.newArrayListWithExpectedSize(roles.size());
        for (Role role : roles) {
            RoleRepresentation roleRepresentation = dozerBeanMapper.map(role, RoleRepresentation.class);
            roleRepresentation.setDisplayName(applicationContext.getMessage("role." + role.getId().toString(), null, LocaleContextHolder.getLocale()));
            roleRepresentationsRepresentations.add(roleRepresentation);
        }
        staticData.put("roles", roleRepresentationsRepresentations);

        List<InstitutionDomicile> institutionDomiciles = entityService.list(InstitutionDomicile.class);
        staticData.put("institutionDomiciles", institutionDomiciles);


        for (Class<Object> importedEntityType : new Class[]{StudyOption.class, ReferralSource.class, Title.class, Country.class, ReferralSource.class, Language.class}) {
            String simpleName = importedEntityType.getSimpleName();
            simpleName = WordUtils.uncapitalize(simpleName);
            List<Object> entities = entityService.list(importedEntityType);
            List<ImportedEntityRepresentation> entityRepresentations = Lists.newArrayListWithCapacity(entities.size());
            for (Object studyOption : entities) {
                entityRepresentations.add(dozerBeanMapper.map(studyOption, ImportedEntityRepresentation.class));
            }
            staticData.put(pluralize(simpleName), entityRepresentations);
        }

        // Display names for enum classes
        for (Class<?> enumClass : new Class[]{Gender.class, PrismProgramType.class}) {
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

        return staticData;
    }

    @SuppressWarnings("unused")
    private class EnumDefinition {

        private String key;

        private String displayName;

        private EnumDefinition(String key, String displayName) {
            this.key = key;
            this.displayName = displayName;
        }

        public String getKey() {
            return key;
        }

        public String getDisplayName() {
            return displayName;
        }

    }
}
