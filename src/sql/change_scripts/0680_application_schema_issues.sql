/* Location filtering for organisations and opportunities */

CREATE TABLE INSTITUTION_DOMICILE_REGION (
	id VARCHAR(10) NOT NULL,
	institution_domicile_id VARCHAR(10) NOT NULL,
	parent_region_id VARCHAR(10),
	region_type VARCHAR(250) NOT NULL,
	name VARCHAR(250) NOT NULL,
	other_name TEXT,
	enabled INT(1) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (institution_domicile_id, parent_region_id, region_type, name),
	INDEX (parent_region_id),
	INDEX (region_type),
	INDEX (name),
	INDEX (enabled),
	FOREIGN KEY (institution_domicile_id) REFERENCES INSTITUTION_DOMICILE (id),
	FOREIGN KEY (parent_region_id) REFERENCES INSTITUTION_DOMICILE_REGION (id)
) ENGINE = INNODB
;

CREATE TABLE INSTITUTION_ADDRESS LIKE ADDRESS
;

ALTER TABLE INSTITUTION_ADDRESS
	ADD COLUMN institution_id INT(10) UNSIGNED NOT NULL AFTER id,
	ADD INDEX (institution_id),
	ADD FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id),
	CHANGE COLUMN domicile_id institution_domicile_id VARCHAR(10) NOT NULL,
	ADD FOREIGN KEY (institution_domicile_id) REFERENCES INSTITUTION_DOMICILE (id),
	ADD COLUMN institution_domicile_region_id VARCHAR(10) AFTER institution_domicile_id,
	ADD INDEX (institution_domicile_region_id),
	ADD FOREIGN KEY (institution_domicile_region_id) REFERENCES INSTITUTION_DOMICILE_REGION (id)
;

ALTER TABLE INSTITUTION
	DROP INDEX domicile_id,
	DROP INDEX institution_domicile_id,
	ADD UNIQUE INDEX (institution_domicile_id, name),
	ADD COLUMN institution_address_id INT(10) UNSIGNED,
	ADD INDEX (institution_address_id),
	ADD FOREIGN KEY (institution_address_id) REFERENCES INSTITUTION_ADDRESS (id)
;

ALTER TABLE ADVERT
	ADD COLUMN institution_address_id INT(10) UNSIGNED,
	ADD INDEX (institution_address_id),
	ADD FOREIGN KEY (institution_address_id) REFERENCES INSTITUTION_ADDRESS (id),
	DROP COLUMN funding
;

INSERT INTO INSTITUTION_DOMICILE_REGION (id, institution_domicile_id, parent_region_id, region_type, name, enabled)
VALUES ("GB-ENG", "GB", "GB-ENG", "Country", "England", 1),
	("GB-LDN", "GB", "GB-ENG", "City Corporation", "London, City of", 1)
;

INSERT INTO INSTITUTION_ADDRESS (institution_id, institution_domicile_id, institution_domicile_region_id, address_line_1, address_town, address_code)
VALUES (5243, "GB", "GB-LDN", "Gower Street",  "London", "WC1E 6BT")
;

UPDATE ADVERT
SET institution_address_id = 1
;

UPDATE INSTITUTION
SET institution_address_id = 1
;

ALTER TABLE ADVERT
	MODIFY COLUMN institution_address_id INT(10) UNSIGNED NOT NULL
;

ALTER TABLE ADVERT
	ADD COLUMN fee_interval VARCHAR(10) AFTER month_study_duration,
	ADD COLUMN fee_value DECIMAL(10,2) AFTER fee_interval,
	ADD COLUMN fee_annualised DECIMAL (10,2) AFTER fee_value,
	ADD COLUMN pay_interval VARCHAR(10) AFTER fee_annualised,
	ADD COLUMN pay_value DECIMAL(10,2) AFTER pay_interval,
	ADD COLUMN pay_annualised DECIMAL (10,2) AFTER pay_value 
;

ALTER TABLE COMMENT_CUSTOM_QUESTION_VERSION
	ADD COLUMN created_timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
;

ALTER TABLE COMMENT_CUSTOM_QUESTION_VERSION
	MODIFY COLUMN created_timestamp DATETIME NOT NULL
;

/* Problem in workflow definition */

SET FOREIGN_KEY_CHECKS = 0
;

UPDATE STATE_TRANSITION_EVALUATION
SET id = "APPLICATION_EVALUATED_OUTCOME"
WHERE id = "APPLICATION_STATE_COMPLETED_OUTCOME"
;

UPDATE STATE_TRANSITION
SET state_transition_evaluation_id = "APPLICATION_EVALUATED_OUTCOME"
WHERE state_transition_evaluation_id = "APPLICATION_STATE_COMPLETED_OUTCOME"
;

UPDATE STATE_TRANSITION_EVALUATION
SET id = "APPLICATION_PROCESSED_OUTCOME"
WHERE id = "APPLICATION_PROCESSING_COMPLETED_OUTCOME"
;

UPDATE STATE_TRANSITION
SET state_transition_evaluation_id = "APPLICATION_PROCESSED_OUTCOME"
WHERE state_transition_evaluation_id = "APPLICATION_PROCESSING_COMPLETED_OUTCOME"
;

SET FOREIGN_KEY_CHECKS = 1
;

ALTER TABLE STATE
	ADD COLUMN sequence_order INT(1) UNSIGNED AFTER parent_state_id
;

UPDATE STATE
SET sequence_order = 0
WHERE id LIKE "%_WITHDRAWN"
;

UPDATE STATE
SET sequence_order = 1
WHERE id = "APPLICATION_UNSUBMITTED"
;

UPDATE STATE
SET sequence_order = 2
WHERE id = "APPLICATION_VALIDATION"
;

UPDATE STATE
SET sequence_order = 3
WHERE id = "APPLICATION_REVIEW"
;

UPDATE STATE
SET sequence_order = 4
WHERE id = "APPLICATION_INTERVIEW"
;

UPDATE STATE
SET sequence_order = 5
WHERE id = "APPLICATION_APPROVAL"
;

UPDATE STATE
SET sequence_order = 6
WHERE id = "APPLICATION_APPROVED"
;

UPDATE STATE
SET sequence_order = 7
WHERE id = "APPLICATION_REJECTED"
;

UPDATE STATE
SET sequence_order = 1
WHERE id = "INSTITUTION_APPROVED"
;

UPDATE STATE
SET sequence_order = 1
WHERE id = "PROGRAM_APPROVAL"
;

UPDATE STATE
SET sequence_order = 2
WHERE id = "PROGRAM_APPROVED"
;

UPDATE STATE
SET sequence_order = 3
WHERE id = "PROGRAM_REJECTED"
;

UPDATE STATE
SET sequence_order = 4
WHERE id = "PROGRAM_DISABLED"
;

UPDATE STATE
SET sequence_order = 1
WHERE id = "PROJECT_APPROVED"
;

UPDATE STATE
SET sequence_order = 2
WHERE id = "PROJECT_DISABLED"
;

UPDATE STATE
SET sequence_order = 1
WHERE id = "SYSTEM_APPROVED"
;

ALTER TABLE STATE_TRANSITION
	DROP COLUMN display_order
;

ALTER TABLE STATE_TRANSITION
	DROP FOREIGN KEY state_transition_ibfk_5,
	CHANGE COLUMN state_transition_evaluation_id state_transition_evaluation VARCHAR(50)
;

DROP TABLE STATE_TRANSITION_EVALUATION
;

ALTER TABLE STATE_TRANSITION
	ADD COLUMN enabled INT(1) UNSIGNED NOT NULL DEFAULT 1,
	ADD INDEX (enabled)
;

ALTER TABLE STATE_TRANSITION
	MODIFY COLUMN enabled INT(1) UNSIGNED NOT NULL
;

UPDATE STATE_TRANSITION
SET state_transition_evaluation = "APPLICATION_SUPERVISION_CONFIRMED_OUTCOME"
WHERE state_transition_evaluation = "APPLICATION_CONFIRM_SUPERVISION_OUTCOME"
;

UPDATE STATE_TRANSITION
SET state_transition_evaluation = "APPLICATION_REVIEWED_OUTCOME"
WHERE state_transition_evaluation = "APPLICATION_REVIEW_OUTCOME"
;

UPDATE STATE_TRANSITION
SET state_transition_evaluation = "APPLICATION_RECRUITED_OUTCOME"
WHERE state_transition_evaluation = "APPLICATION_RECRUITMENT_OUTCOME"
;

UPDATE STATE_TRANSITION
SET state_transition_evaluation = "APPLICATION_INTERVIEWED_OUTCOME"
WHERE state_transition_evaluation = "APPLICATION_INTERVIEW_FEEDBACK_OUTCOME"
;

UPDATE STATE_TRANSITION
SET state_transition_evaluation = "APPLICATION_INTERVIEW_RSVPED_OUTCOME"
WHERE state_transition_evaluation = "APPLICATION_INTERVIEW_AVAILABILITY_OUTCOME"
;
