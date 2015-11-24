package uk.co.alumeni.prism.utils;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;

import java.util.List;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public class PrismEnumUtils {

    public static <T extends Enum<T>> List<T> values(Class<T> enumClass, PrismScope scope, String... extensions) {
        String scopeReference = scope.name();
        return stream(extensions).map(ex -> Enum.valueOf(enumClass, scopeReference + "_" + ex)).collect(toList());
    }

}
