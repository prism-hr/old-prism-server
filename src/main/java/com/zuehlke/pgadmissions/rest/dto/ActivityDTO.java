package com.zuehlke.pgadmissions.rest.dto;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;

public class ActivityDTO {

    private UserDTO author;
    
    private String title;
    
    private String description;
    
    private DateTime timestamp;

    public UserDTO getAuthor() {
        return author;
    }

    public void setAuthor(UserDTO author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }
    
}
