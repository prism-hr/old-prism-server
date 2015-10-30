package com.zuehlke.pgadmissions.utils;

import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.invokeMethod;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class PrismEnumUtils {

    @SuppressWarnings("unchecked")
    public static <T extends Enum<?>> List<T> values(Class<T> enumClass, PrismScope scope, String... extensions) {
        String scopeReference = scope.name();
        return stream(extensions).map(ex -> (T) invokeMethod(enumClass, "valueOf", scopeReference + "_" + ex)).collect(toList());
    }

}
