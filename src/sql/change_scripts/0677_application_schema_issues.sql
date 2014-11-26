/* Mistakes in workflow configuration */

UPDATE STATE_DURATION
SET day_duration = 28
WHERE day_duration = 25
;

ALTER TABLE SYSTEM
	MODIFY COLUMN state_id VARCHAR(50)
;

SET FOREIGN_KEY_CHECKS = 0
;

UPDATE NOTIFICATION_TEMPLATE
SET id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION"
WHERE id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGMENTS_NOTIFICATION"
;

UPDATE NOTIFICATION_CONFIGURATION
SET notification_template_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION"
WHERE notification_template_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGMENTS_NOTIFICATION"
;

UPDATE NOTIFICATION_TEMPLATE_VERSION
SET notification_template_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION"
WHERE notification_template_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGMENTS_NOTIFICATION"
;

UPDATE STATE_ACTION
SET notification_template_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION"
WHERE notification_template_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGMENTS_NOTIFICATION"
;

UPDATE STATE_ACTION_NOTIFICATION
SET notification_template_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION"
WHERE notification_template_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGMENTS_NOTIFICATION"
;

UPDATE USER_NOTIFICATION_INDIVIDUAL
SET notification_template_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION"
WHERE notification_template_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGMENTS_NOTIFICATION"
;

UPDATE USER_NOTIFICATION_SYNDICATED
SET notification_template_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION"
WHERE notification_template_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGMENTS_NOTIFICATION"
;

SET FOREIGN_KEY_CHECKS = 1
;
