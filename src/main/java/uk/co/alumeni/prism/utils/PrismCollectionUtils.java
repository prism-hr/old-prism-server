package uk.co.alumeni.prism.utils;

import java.util.Collection;

public class PrismCollectionUtils {

    public static boolean containsSame(Collection<?> collection, Collection<?> otherCollection) {
        int collectionSize = collection == null ? 0 : collection.size();
        int otherCollectionSize = otherCollection == null ? 0 : otherCollection.size();
        return collectionSize > 0 && otherCollectionSize > 0 && collectionSize == otherCollectionSize && collection.containsAll(otherCollection);
    }

}
