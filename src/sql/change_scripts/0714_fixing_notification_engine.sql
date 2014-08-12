UPDATE APPLICATION
SET state_id = REPLACE(state_id, "_PENDING_CORRECTION", "_COMPLETED")
WHERE state_id IN ("APPLICATION_APPROVED_PENDING_CORRECTION", 
	"APPLICATION_REJECTED_PENDING_CORRECTION", "APPLICATION_WITHDRAWN_PENDING_CORRECTION")
	AND updated_timestamp <= (CURRENT_DATE() - INTERVAL 2 MONTH)
;

ALTER TABLE USER_NOTIFICATION
	ADD COLUMN system_id INT(10) UNSIGNED AFTER id,
	ADD COLUMN institution_id INT(10) UNSIGNED AFTER system_id,
	ADD COLUMN program_id INT(10) UNSIGNED AFTER institution_id,
	ADD COLUMN project_id INT(10) UNSIGNED AFTER program_id,
	ADD COLUMN application_id INT(10) UNSIGNED AFTER project_id,
	ADD UNIQUE INDEX (system_id, user_role_id, notification_template_id),
	ADD UNIQUE INDEX (institution_id, user_role_id, notification_template_id),
	ADD UNIQUE INDEX (program_id, user_role_id, notification_template_id),
	ADD UNIQUE INDEX (project_id, user_role_id, notification_template_id),
	ADD UNIQUE INDEX (application_id, user_role_id, notification_template_id),
	ADD FOREIGN KEY (system_id) REFERENCES SYSTEM (id),
	ADD FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id),
	ADD FOREIGN KEY (program_id) REFERENCES PROGRAM (id),
	ADD FOREIGN KEY (project_id) REFERENCES PROJECT (id),
	ADD FOREIGN KEY (application_id) REFERENCES APPLICATION (id),
	CHANGE COLUMN created_timestamp created_date DATE,
	ADD INDEX (created_date)
;

