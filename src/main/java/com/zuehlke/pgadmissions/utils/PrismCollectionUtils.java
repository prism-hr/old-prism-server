package com.zuehlke.pgadmissions.utils;

import java.util.Collection;

public class PrismCollectionUtils {

    public static <T> boolean equals(Collection<T> collection, Collection<T> otherCollection) {
        int collectionSize = collection.size();
        int otherCollectionSize = otherCollection.size();
        return collectionSize > 0 && otherCollectionSize > 0 && collectionSize == otherCollectionSize && collection.containsAll(otherCollection);
    }

}
