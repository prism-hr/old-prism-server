package uk.co.alumeni.prism.event;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.context.ApplicationEvent;

import uk.co.alumeni.prism.rest.dto.resource.ResourceDTO;

public class UserActivityUpdateEvent extends ApplicationEvent {

    private static final long serialVersionUID = 2524341835748173001L;

    private ResourceDTO resource;

    private List<Integer> users;

    private Integer currentUser;

    private DateTime baseline;

    public UserActivityUpdateEvent(Object source, List<Integer> users, Integer currentUser, DateTime baseline) {
        this(source, null, users, currentUser, baseline);
    }

    public UserActivityUpdateEvent(Object source, ResourceDTO resource, Integer currentUser, DateTime baseline) {
        this(source, resource, null, currentUser, baseline);
    }

    public UserActivityUpdateEvent(Object source, ResourceDTO resource, List<Integer> users, Integer currentUser, DateTime baseline) {
        super(source);
        this.resource = resource;
        this.users = users;
        this.currentUser = currentUser;
        this.baseline = baseline;
    }

    public ResourceDTO getResource() {
        return resource;
    }

    public void setResource(ResourceDTO resource) {
        this.resource = resource;
    }

    public List<Integer> getUsers() {
        return users;
    }

    public void setUsers(List<Integer> users) {
        this.users = users;
    }

    public Integer getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Integer currentUser) {
        this.currentUser = currentUser;
    }

    public DateTime getBaseline() {
        return baseline;
    }

    public void setBaseline(DateTime baseline) {
        this.baseline = baseline;
    }

}
