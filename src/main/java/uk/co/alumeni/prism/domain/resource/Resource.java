package uk.co.alumeni.prism.domain.resource;

import static com.google.common.collect.Maps.newLinkedHashMap;
import static uk.co.alumeni.prism.PrismConstants.HYPHEN;
import static uk.co.alumeni.prism.PrismConstants.SPACE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.SYSTEM;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.getProperty;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import uk.co.alumeni.prism.domain.Activity;
import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserRole;
import uk.co.alumeni.prism.domain.workflow.State;
import uk.co.alumeni.prism.domain.workflow.StateActionPending;

import com.google.common.base.Joiner;

public abstract class Resource implements Activity, UniqueEntity {

    @Override
    public abstract Integer getId();

    @Override
    public abstract void setId(Integer id);

    public abstract User getUser();

    public abstract void setUser(User user);

    public abstract String getCode();

    public abstract void setCode(String code);

    public abstract uk.co.alumeni.prism.domain.resource.System getSystem();

    public abstract void setSystem(uk.co.alumeni.prism.domain.resource.System system);

    public abstract Institution getInstitution();

    public abstract void setInstitution(Institution institution);

    public abstract Department getDepartment();

    public abstract void setDepartment(Department department);

    public abstract Program getProgram();

    public abstract void setProgram(Program program);

    public abstract Project getProject();

    public abstract void setProject(Project project);

    public abstract Advert getAdvert();

    public abstract void setAdvert(Advert advert);

    public abstract String getOpportunityCategories();

    public abstract void setOpportunityCategories(String opportunityCategories);

    public abstract Application getApplication();

    public abstract Boolean getShared();

    public abstract void setShared(Boolean shared);

    public abstract State getState();

    public abstract void setState(State state);

    public abstract State getPreviousState();

    public abstract void setPreviousState(State previousState);

    public abstract LocalDate getDueDate();

    public abstract void setDueDate(LocalDate dueDate);

    public abstract DateTime getCreatedTimestamp();

    public abstract void setCreatedTimestamp(DateTime createdTimestamp);

    public abstract DateTime getUpdatedTimestamp();

    public abstract void setUpdatedTimestamp(DateTime updatedTimestamp);

    @Override
    public abstract String getSequenceIdentifier();

    @Override
    public abstract void setSequenceIdentifier(String sequenceIdentifier);

    public abstract Set<ResourceState> getResourceStates();

    public abstract Set<ResourcePreviousState> getResourcePreviousStates();

    public abstract Set<ResourceCondition> getResourceConditions();

    public abstract Set<Comment> getComments();

    public abstract Set<UserRole> getUserRoles();

    public abstract Set<StateActionPending> getStateActionPendings();

    public void addComment(Comment comment) {
        getComments().add(comment);
    }

    public void addResourceState(ResourceState resourceState) {
        getResourceStates().add(resourceState);
    }

    public void addResourcePreviousState(ResourcePreviousState resourcePreviousState) {
        getResourcePreviousStates().add(resourcePreviousState);
    }

    @SuppressWarnings("unchecked")
    public <T extends Resource> T getParentResource() {
        switch (getResourceScope()) {
        case SYSTEM:
            return (T) this;
        case INSTITUTION:
            return (T) getSystem();
        case DEPARTMENT:
            return (T) getInstitution();
        case PROGRAM:
            return (T) ObjectUtils.firstNonNull(getDepartment(), getInstitution());
        case PROJECT:
            return (T) ObjectUtils.firstNonNull(getProgram(), getDepartment(), getInstitution());
        case APPLICATION:
            return (T) ObjectUtils.firstNonNull(getProject(), getProgram(), getDepartment(), getInstitution());
        default:
            throw new UnsupportedOperationException();
        }
    }

    public void setParentResource(Resource parentResource) {
        if (parentResource.getId() != null) {
            setProject(parentResource.getProject());
            setProgram(parentResource.getProgram());
            setDepartment(parentResource.getDepartment());
            setInstitution(parentResource.getInstitution());
            setSystem(parentResource.getSystem());
        }
    }

    public ResourceParent getResourceParent() {
        return ObjectUtils.firstNonNull(getDepartment(), getInstitution());
    }

    public PrismScope getResourceScope() {
        return PrismScope.valueOf(getClass().getSimpleName().toUpperCase());
    }

    public Resource getEnclosingResource(PrismScope resourceScope) {
        return (Resource) getProperty(this, resourceScope.getLowerCamelName());
    }

    public Map<PrismScope, Resource> getEnclosingResources() {
        Map<PrismScope, Resource> enclosingResources = newLinkedHashMap();
        for (PrismScope enclosingScope : getResourceScope().getEnclosingScopes()) {
            Resource enclosingResource = getEnclosingResource(enclosingScope);
            if (enclosingResource != null) {
                enclosingResources.put(enclosingScope, enclosingResource);
            }
        }
        return enclosingResources;
    }

    public boolean sameAs(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final Resource other = (Resource) object;
        Integer id = getId();
        Integer otherId = other.getId();
        return id != null && otherId != null && id.equals(otherId);
    }

    public String getDisplayName() {
        PrismScope resourceScope = getResourceScope();
        if (resourceScope.equals(SYSTEM)) {
            return getSystem().getName();
        } else if (resourceScope.equals(PrismScope.APPLICATION)) {
            Application application = getApplication();
            return application.getParentResource().getDisplayName() + SPACE + HYPHEN + SPACE + application.getUser().getFullName();
        }

        return Joiner.on(SPACE + HYPHEN + SPACE).skipNulls()
                .join(getResourceName(getInstitution()), getResourceName(getDepartment()), getResourceName(getProgram()),
                        getResourceName(getProject()));
    }

    @Override
    public String toString() {
        return getResourceScope().name() + "#" + getId();
    }

    private String getResourceName(ResourceParent resource) {
        return resource == null ? null : resource.getName();
    }

}
