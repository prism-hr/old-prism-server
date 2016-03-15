package uk.co.alumeni.prism.domain.activity;

import org.joda.time.DateTime;

public interface ActivityEditable extends Activity {

    public DateTime getUpdatedTimestamp();
    
    public void setUpdatedTimestamp(DateTime updatedTimestamp);
    
    public DateTime getActivityCachedTimestamp();
    
    public void setActivityCachedTimestamp(DateTime activityCachedTimestamp);
    
}
