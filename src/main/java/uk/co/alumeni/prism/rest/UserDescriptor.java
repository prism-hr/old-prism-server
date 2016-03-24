package uk.co.alumeni.prism.rest;

import static com.google.common.base.Objects.equal;
import static org.apache.commons.lang3.ObjectUtils.compare;

import com.google.common.base.Objects;

public abstract class UserDescriptor implements Comparable<UserDescriptor> {

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract String getFirstName();

    public abstract void setFirstName(String firstName);

    public abstract String getLastName();

    public abstract void setLastName(String lastName);

    public abstract String getEmail();

    public abstract void setEmail(String email);
    
    @Override
    public int hashCode() {
        return Objects.hashCode(getUserIdentity());
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        UserDescriptor other = (UserDescriptor) object;
        return equal(getUserIdentity(), other.getUserIdentity());
    }

    @Override
    public int compareTo(UserDescriptor other) {
        int compare = compare(getFirstName(), other.getFirstName());
        return compare == 0 ? compare(getLastName(), other.getLastName()) : compare;
    }
    
    private String getUserIdentity() {
        Integer id = getId();
        return id == null ? getEmail() : id.toString();
    }

}
