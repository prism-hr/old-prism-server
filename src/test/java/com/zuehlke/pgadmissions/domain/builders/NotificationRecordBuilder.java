package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;

public class NotificationRecordBuilder {

    private Date notificationDate;
	private NotificationType notificationType;
	private Integer id;

	public NotificationRecordBuilder id(Integer id) {
		this.id = id;
		return this;
	}

	public NotificationRecordBuilder notificationDate(Date notificationDate) {
		this.notificationDate = notificationDate;
		return this;
	}

	public NotificationRecordBuilder notificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
		return this;
	}

	public NotificationRecord build() {
		NotificationRecord notifiationRecord = new NotificationRecord();
		notifiationRecord.setDate(notificationDate);
		notifiationRecord.setNotificationType(notificationType);
		notifiationRecord.setId(id);
		return notifiationRecord;
	}

}
