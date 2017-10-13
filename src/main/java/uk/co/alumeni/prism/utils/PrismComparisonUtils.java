package uk.co.alumeni.prism.utils;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;

public class PrismComparisonUtils {

    public static int compareRoles(PrismRole role1, PrismRole role2) {
        return compareRoles(role1, role2, false);
    }

    public static int compareRoles(PrismRole role1, PrismRole role2, boolean nullGreater) {
        if (role1 == role2) {
            return 0;
        } else if (role1 == null) {
            return nullGreater ? 1 : -1;
        } else if (role2 == null) {
            return nullGreater ? -1 : 1;
        }
        return role1.compareWith(role2);
    }

}
