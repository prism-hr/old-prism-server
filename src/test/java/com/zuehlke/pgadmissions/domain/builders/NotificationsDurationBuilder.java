package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.NotificationsDuration;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

public class NotificationsDurationBuilder {

    private Integer id;

    private Integer duration;

    private DurationUnitEnum unit;

    public NotificationsDurationBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public NotificationsDurationBuilder duration(Integer duration) {
        this.duration = duration;
        return this;
    }

    public NotificationsDurationBuilder unit(DurationUnitEnum unit) {
        this.unit = unit;
        return this;
    }

    public NotificationsDuration build() {
        NotificationsDuration notificationsDuration = new NotificationsDuration();
        notificationsDuration.setId(id);
        notificationsDuration.setDuration(duration);
        notificationsDuration.setUnit(unit);
        return notificationsDuration;
    }
}
