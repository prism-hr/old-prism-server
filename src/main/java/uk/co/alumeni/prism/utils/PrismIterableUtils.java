package uk.co.alumeni.prism.utils;

import java.util.Collection;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class PrismIterableUtils {

    public static boolean noneNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                return false;
            }
        }
        return true;
    }

    public static boolean containsSome(Collection<?> collection, Collection<?> otherCollection) {
        if (isNotEmpty(collection) && isNotEmpty(otherCollection)) {
            int collectionSize = collection.size();
            int otherCollectionSize = otherCollection.size();
            return collectionSize > 0 && otherCollectionSize > 0 && intersects(collection, otherCollection);
        }
        return false;
    }

    public static boolean containsSame(Collection<?> collection, Collection<?> otherCollection) {
        if (isNotEmpty(collection) && isNotEmpty(otherCollection)) {
            int collectionSize = collection.size();
            int otherCollectionSize = otherCollection.size();
            return collectionSize > 0 && otherCollectionSize > 0 && collectionSize == otherCollectionSize && collection.containsAll(otherCollection);
        }
        return false;
    }

    public static boolean intersects(Collection<?> collection, Collection<?> otherCollection) {
        for (Object item : collection) {
            if (otherCollection.contains(item)) {
                return true;
            }
        }

        for (Object otherItem : otherCollection) {
            if (collection.contains(otherItem)) {
                return true;
            }
        }

        return false;
    }

}
