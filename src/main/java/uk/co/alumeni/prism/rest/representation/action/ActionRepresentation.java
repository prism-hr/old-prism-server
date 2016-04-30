package uk.co.alumeni.prism.rest.representation.action;

import static org.apache.commons.lang3.ObjectUtils.compare;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCategory;

import com.google.common.base.Objects;

public class ActionRepresentation implements Comparable<ActionRepresentation> {

    private PrismAction id;

    private PrismActionCategory category;

    private String redirectLink;

    private Boolean declinable;

    public PrismAction getId() {
        return id;
    }

    public void setId(PrismAction id) {
        this.id = id;
    }

    public PrismActionCategory getCategory() {
        return category;
    }

    public void setCategory(PrismActionCategory category) {
        this.category = category;
    }

    public String getRedirectLink() {
        return redirectLink;
    }

    public void setRedirectLink(String redirectLink) {
        this.redirectLink = redirectLink;
    }

    public Boolean getDeclinable() {
        return declinable;
    }

    public void setDeclinable(Boolean declinable) {
        this.declinable = declinable;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        ActionRepresentation other = (ActionRepresentation) object;
        return Objects.equal(getId(), other.getId());
    }

    @Override
    public int compareTo(ActionRepresentation other) {
        PrismAction otherAction = other.getId();
        int compare = compare(id.getScope(), otherAction.getScope());
        return compare == 0 ? compare(id.name(), otherAction.name()) : compare;
    }

}
