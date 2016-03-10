package uk.co.alumeni.prism.utils;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.Collection;

public class PrismCollectionUtils {

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
