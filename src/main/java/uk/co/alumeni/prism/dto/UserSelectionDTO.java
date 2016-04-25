package uk.co.alumeni.prism.dto;

import org.joda.time.DateTime;

import uk.co.alumeni.prism.rest.UserDescriptor;

public class UserSelectionDTO extends UserDescriptor {

    private Integer id;
    
    private String firstName;
    
    private String lastName;
    
    private String email;

    private DateTime eventTimestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public DateTime getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(DateTime eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public UserSelectionDTO withId(Integer id) {
        this.id = id;
        return this;
    }
    
    public UserSelectionDTO withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }
    
    public UserSelectionDTO withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserSelectionDTO withEmail(String email) {
        this.email = email;
        return this;
    }
    
    public UserSelectionDTO withEventTimestamp(DateTime eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
        return this;
    }

}
