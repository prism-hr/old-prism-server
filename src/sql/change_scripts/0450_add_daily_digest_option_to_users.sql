ALTER TABLE REGISTERED_USER ADD COLUMN digest_notification_type VARCHAR(45) NOT NULL DEFAULT 'NONE' AFTER email_notification_type
;

ALTER TABLE SUPERVISOR ADD COLUMN confirmed_supervision_date DATETIME NULL DEFAULT NULL  AFTER confirmed_supervision
;
