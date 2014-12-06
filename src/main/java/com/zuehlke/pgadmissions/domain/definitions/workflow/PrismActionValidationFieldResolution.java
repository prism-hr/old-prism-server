package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.Collections;
import java.util.Map;

public class PrismActionValidationFieldResolution {

    private PrismActionValidationFieldRestriction restriction;

    private Map<String, Object> arguments;

    public PrismActionValidationFieldResolution(PrismActionValidationFieldRestriction restriction) {
        this(restriction, Collections.<String, Object>emptyMap());
    }

    public PrismActionValidationFieldResolution(PrismActionValidationFieldRestriction restriction, String argumentKey, Object argumentValue) {
        this(restriction, Collections.singletonMap(argumentKey, argumentValue));
    }

    public PrismActionValidationFieldResolution(PrismActionValidationFieldRestriction restriction, Map<String, Object> arguments) {
        this.restriction = restriction;
        this.arguments = arguments;
    }

    public PrismActionValidationFieldRestriction getRestriction() {
        return restriction;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }
}
