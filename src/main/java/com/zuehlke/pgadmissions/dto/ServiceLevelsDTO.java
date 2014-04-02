package com.zuehlke.pgadmissions.dto;

import java.util.List;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.NotificationsDuration;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.State;

public class ServiceLevelsDTO {

    private List<State> stagesDuration = Lists.newArrayList();

    private List<ReminderInterval> reminderIntervals = Lists.newArrayList();

    private NotificationsDuration notificationsDuration;

    public List<State> getStagesDuration() {
        return stagesDuration;
    }

    public List<ReminderInterval> getReminderIntervals() {
        return reminderIntervals;
    }

    public NotificationsDuration getNotificationsDuration() {
        return notificationsDuration;
    }

    public void setReminderIntervals(List<ReminderInterval> reminderIntervals) {
        this.reminderIntervals.clear();
        this.reminderIntervals.addAll(Collections2.filter(reminderIntervals, Predicates.notNull()));
    }

    public void setStagesDuration(List<State> stagesDuration) {
        this.stagesDuration.clear();
        this.stagesDuration.addAll(Collections2.filter(stagesDuration, Predicates.notNull()));
    }

    public void setNotificationsDuration(NotificationsDuration notificationsDuration) {
        this.notificationsDuration = notificationsDuration;
    }

}
