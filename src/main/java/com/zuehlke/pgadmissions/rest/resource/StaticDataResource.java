package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.rest.domain.workflow.RoleRepresentation;
import com.zuehlke.pgadmissions.rest.domain.workflow.StateActionRepresentation;
import com.zuehlke.pgadmissions.rest.domain.workflow.StateRepresentation;
import com.zuehlke.pgadmissions.services.EntityService;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/static")
public class StaticDataResource {

    @Autowired
    private EntityService entityService;

    @Autowired
    private DozerBeanMapper dozerBeanMapper;

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping(method = RequestMethod.GET)
    public Map<String, Object> getStaticData() {
        Map<String, Object> staticData = Maps.newHashMap();

        List<StateAction> stateActions = entityService.getAll(StateAction.class);
        List<StateActionRepresentation> stateActionRepresentations = Lists.newArrayListWithExpectedSize(stateActions.size());
        for (StateAction stateAction : stateActions) {
            StateActionRepresentation actionRepresentation = dozerBeanMapper.map(stateAction, StateActionRepresentation.class);
            actionRepresentation.setDisplayName(applicationContext.getMessage("action." + actionRepresentation.getAction(), null, LocaleContextHolder.getLocale()));
            stateActionRepresentations.add(actionRepresentation);
        }
        staticData.put("stateActions", stateActionRepresentations);

        List<State> states = entityService.getAll(State.class);
        List<StateRepresentation> stateRepresentations = Lists.newArrayListWithExpectedSize(states.size());
        for (State state : states) {
            StateRepresentation stateRepresentation = dozerBeanMapper.map(state, StateRepresentation.class);
            stateRepresentation.setDisplayValue(applicationContext.getMessage("state." + state.getParentState().getId().toString(), null, LocaleContextHolder.getLocale()));
            stateRepresentations.add(stateRepresentation);
        }
        staticData.put("states", stateRepresentations);

        List<Role> roles = entityService.getAll(Role.class);
        List<RoleRepresentation> roleRepresentationsRepresentations = Lists.newArrayListWithExpectedSize(roles.size());
        for (Role role : roles) {
            RoleRepresentation roleRepresentation = dozerBeanMapper.map(role, RoleRepresentation.class);
            roleRepresentation.setDisplayValue(applicationContext.getMessage("role." + role.getId().toString(), null, LocaleContextHolder.getLocale()));
            roleRepresentationsRepresentations.add(roleRepresentation);
        }
        staticData.put("roles", roleRepresentationsRepresentations);

        // Display names for enum classes
        for (Class enumClass : new Class[]{Gender.class}) {
            List<EnumDefinition> definitions = Lists.newArrayListWithExpectedSize(enumClass.getEnumConstants().length);
            for (java.lang.Object key : enumClass.getEnumConstants()) {
                String message = applicationContext.getMessage(enumClass.getSimpleName().toLowerCase() + "." + key, null, LocaleContextHolder.getLocale());
                definitions.add(new EnumDefinition(key.toString(), message));
            }
            staticData.put(enumClass.getSimpleName().toLowerCase() + "s", definitions);
        }

        return staticData;
    }

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
