INSERT INTO NOTIFICATION_RECORD(application_form_id, notification_type, notification_date) SELECT APPLICATION_FORM.id, 'VALIDATION_REMINDER', last_email_reminder_date FROM APPLICATION_FORM WHERE last_email_reminder_date is NOT NULL;