package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.rest.domain.StateActionRepresentation;
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
            actionRepresentation.setDisplayValue(applicationContext.getMessage("action." + actionRepresentation.getAction(), null, LocaleContextHolder.getLocale()));
            stateActionRepresentations.add(actionRepresentation);
        }
        staticData.put("stateActions", stateActionRepresentations);

        return staticData;
    }

}
