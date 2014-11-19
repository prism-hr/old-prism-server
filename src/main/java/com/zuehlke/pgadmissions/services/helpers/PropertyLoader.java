package com.zuehlke.pgadmissions.services.helpers;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import java.util.Arrays;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.services.DisplayPropertyService;

@Component
@Scope(SCOPE_PROTOTYPE)
public class PropertyLoader {

    private Resource resource;

    private PrismLocale locale;

    private PrismProgramType programType;

    private final HashMap<PrismDisplayProperty, String> properties = Maps.newHashMap();

    @Autowired
    private DisplayPropertyService displayPropertyService;

    public String load(PrismDisplayProperty property) {
        String value = properties.get(property);
        if (value == null) {
            PrismDisplayPropertyCategory category = property.getDisplayCategory();
            properties.putAll(displayPropertyService.getDisplayProperties(resource, property.getScope(), category, locale, programType));
            value = properties.get(property);
        }
        return value;
    }

    public String load(PrismDisplayProperty trueIndex, PrismDisplayProperty falseIndex, boolean evaluation) {
        return evaluation ? load(trueIndex) : load(falseIndex);
    }

    public PropertyLoader localize(Resource resource, User user) {
        PrismScope resourceScope = resource.getResourceScope();
        if (Arrays.asList(PROGRAM, PROJECT, APPLICATION).contains(resourceScope)) {
            Program program = resource.getProgram();
            this.resource = program;
            this.programType = program.getProgramType().getPrismProgramType();
        } else {
            this.resource = resource;
            this.programType = null;
        }
        this.locale = resourceScope == SYSTEM ? user.getLocale() : resource.getLocale();
        return this;
    }

}
