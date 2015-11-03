package com.zuehlke.pgadmissions.utils;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class PrismEnumUtils {

    public static <T extends Enum<T>> List<T> values(Class<T> enumClass, PrismScope scope, String... extensions) {
        String scopeReference = scope.name();
        return stream(extensions).map(ex -> (T) Enum.valueOf(enumClass, scopeReference + "_" + ex)).collect(toList());
    }

}
