package uk.co.alumeni.prism.dto;

import org.joda.time.DateTime;

public class ResourceUpdateDTO {
    
    private Integer id;
    
    private Boolean recentUpdate;
    
    private DateTime updatedTimestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getRecentUpdate() {
        return recentUpdate;
    }

    public void setRecentUpdate(Boolean recentUpdate) {
        this.recentUpdate = recentUpdate;
    }

    public DateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }
    
}
