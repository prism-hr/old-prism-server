package uk.co.alumeni.prism.dto;

import org.joda.time.DateTime;

import uk.co.alumeni.prism.domain.user.User;

import com.google.common.base.Objects;

public class UserSelectionDTO {

    private User user;

    private DateTime eventTimestamp;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DateTime getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(DateTime eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public String getIndexName() {
        return user.getFullName();
    }

    public UserSelectionDTO withUser(User user) {
        this.user = user;
        return this;
    }

    public UserSelectionDTO withEventTimestamp(DateTime eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(user.getId());
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final UserSelectionDTO other = (UserSelectionDTO) object;
        return Objects.equal(user.getId(), other.getUser().getId());
    }

}
