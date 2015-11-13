package com.zuehlke.pgadmissions.services;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Activity;

@Service
@Transactional
public class ActivityService {

    public <T extends Activity> void setSequenceIdentifier(T activity, DateTime baseline) {
        activity.setSequenceIdentifier(Long.toString(baseline.getMillis()) + String.format("%010d", activity.getId()));
    }

}
