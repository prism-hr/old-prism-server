/* Opportunity categories for adverts */

CREATE TABLE ADVERT_OPPORTUNITY_CATEGORY (
	id INT(10) UNSIGNED NOT NULL,
	parent_category_id INT(10) UNSIGNED,
	name VARCHAR(250) NOT NULL,
	enabled INT(1) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	INDEX (parent_category_id),
	UNIQUE INDEX (name),
	INDEX (enabled),
	FOREIGN KEY (parent_category_id) REFERENCES ADVERT_OPPORTUNITY_CATEGORY (id)
) ENGINE = INNODB
;

CREATE TABLE ADVERT_CATEGORY (
	advert_id INT(10) UNSIGNED,
	advert_opportunity_category_id INT(10) UNSIGNED,
	PRIMARY KEY (advert_id, advert_opportunity_category_id),
	INDEX (advert_opportunity_category_id),
	FOREIGN KEY (advert_id) REFERENCES ADVERT (id),
	FOREIGN KEY (advert_opportunity_category_id) REFERENCES ADVERT_OPPORTUNITY_CATEGORY (id)
) ENGINE = INNODB
;

/* Shorter field names */

ALTER TABLE ACTION_REDACTION
	CHANGE COLUMN action_redaction_type redaction_type VARCHAR(50) NOT NULL
;

ALTER TABLE STATE_ACTION_ENHANCEMENT
	CHANGE COLUMN action_enhancement_type enhancement_type VARCHAR(50) NOT NULL
;

/* Mistake in workflow */

UPDATE ACTION_REDACTION
SET redaction_type = "ALL_CONTENT"
WHERE action_id = "APPLICATION_PROVIDE_REFERENCE"
;

UPDATE STATE_ACTION_NOTIFICATION
SET notification_template_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION"
WHERE notification_template_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWEE"
;

/* Tidy up */

ALTER TABLE ROLE_TRANSITION
	MODIFY COLUMN transition_role_id VARCHAR(50) NOT NULL AFTER role_transition_type
;
