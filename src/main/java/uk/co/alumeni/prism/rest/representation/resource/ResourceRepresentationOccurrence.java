package uk.co.alumeni.prism.rest.representation.resource;

import static org.apache.commons.lang3.ObjectUtils.compare;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.rest.representation.DocumentRepresentation;

public class ResourceRepresentationOccurrence extends ResourceRepresentationIdentity {

    private Integer occurrenceCount;

    public Integer getOccurrenceCount() {
        return occurrenceCount;
    }

    public void setOccurrenceCount(Integer occurrenceCount) {
        this.occurrenceCount = occurrenceCount;
    }

    public ResourceRepresentationOccurrence withScope(PrismScope scope) {
        setScope(scope);
        return this;
    }

    public ResourceRepresentationOccurrence withId(Integer id) {
        setId(id);
        return this;
    }

    public ResourceRepresentationOccurrence withName(String name) {
        setName(name);
        return this;
    }

    public ResourceRepresentationOccurrence withLogoImage(DocumentRepresentation logoImage) {
        setLogoImage(logoImage);
        return this;
    }

    @Override
    public int compareTo(ResourceRepresentationIdentity other) {
        int compare = 0;
        if (ResourceRepresentationOccurrence.class.isAssignableFrom(other.getClass())) {
            compare = compare(((ResourceRepresentationOccurrence) other).getOccurrenceCount(), occurrenceCount);
        }
        return compare == 0 ? super.compareTo(other) : compare;
    }

}
