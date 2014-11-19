ALTER TABLE PROGRAM_EXPORT
	DROP FOREIGN KEY program_export_ibfk_1,
	CHANGE COLUMN program_export_format_id program_export_format VARCHAR(50) NOT NULL
;

ALTER TABLE PROGRAM
	DROP FOREIGN KEY program_ibfk_1,
	CHANGE COLUMN program_type_id program_type VARCHAR(50) NOT NULL AFTER institution_id
;

DROP TABLE PROGRAM_EXPORT_FORMAT
;

DROP TABLE PROGRAM_TYPE_STUDY_DURATION
;

DROP TABLE PROGRAM_TYPE
;

ALTER TABLE ADVERT
	MODIFY COLUMN description TEXT,
	CHANGE COLUMN study_duration month_study_duration INT(4) UNSIGNED,
	MODIFY COLUMN funding TEXT
;

UPDATE ADVERT
SET description = CONCAT(description, funding)
;

INSERT INTO IMPORTED_ENTITY (institution_id, imported_entity_type, code, name, enabled)
	SELECT 5243, "STUDY_OPTION", id, display_name, 1
	FROM PROGRAM_STUDY_OPTION
;

ALTER TABLE PROGRAM_INSTANCE
	MODIFY COLUMN program_id INT(10) UNSIGNED NOT NULL AFTER id,
	ADD COLUMN study_option_id INT(10) UNSIGNED AFTER program_id,
	ADD INDEX (study_option_id),
	ADD FOREIGN KEY (study_option_id) REFERENCES IMPORTED_ENTITY (id)
;

ALTER TABLE APPLICATION_PROGRAM_DETAIL
	ADD COLUMN study_option_id INT(10) UNSIGNED AFTER id,
	ADD INDEX (study_option_id),
	ADD FOREIGN KEY (study_option_id) REFERENCES IMPORTED_ENTITY (id)
;

UPDATE PROGRAM_INSTANCE INNER JOIN IMPORTED_ENTITY
	ON PROGRAM_INSTANCE.program_study_option_id = IMPORTED_ENTITY.code
	AND IMPORTED_ENTITY.imported_entity_type = "STUDY_OPTION"
SET PROGRAM_INSTANCE.study_option_id = IMPORTED_ENTITY.id
;

UPDATE APPLICATION_PROGRAM_DETAIL INNER JOIN IMPORTED_ENTITY
	ON APPLICATION_PROGRAM_DETAIL.program_study_option_id = IMPORTED_ENTITY.code
	AND IMPORTED_ENTITY.imported_entity_type = "STUDY_OPTION"
SET APPLICATION_PROGRAM_DETAIL.study_option_id = IMPORTED_ENTITY.id
;

ALTER TABLE PROGRAM_INSTANCE
	DROP INDEX program_id,
	ADD UNIQUE INDEX (program_id, academic_year, study_option_id),
	DROP FOREIGN KEY program_instance_ibfk_1,
	DROP COLUMN program_study_option_id
;

ALTER TABLE APPLICATION_PROGRAM_DETAIL
	DROP FOREIGN KEY application_program_detail_ibfk_1,
	DROP COLUMN program_study_option_id
;

DROP TABLE PROGRAM_STUDY_OPTION
;

ALTER TABLE ROLE_TRANSITION
	DROP FOREIGN KEY role_transition_ibfk_3,
	CHANGE COLUMN role_transition_type_id role_transition_type VARCHAR(50) NOT NULL
;

DROP TABLE ROLE_TRANSITION_TYPE
;

ALTER TABLE USER_INSTITUTION_IDENTITY
	DROP FOREIGN KEY user_institution_identity_ibfk_3,
	CHANGE COLUMN user_identity_type_id user_identity_type VARCHAR(50) NOT NULL
;

DROP TABLE USER_IDENTITY_TYPE
;

ALTER TABLE STATE_TRANSITION_EVALUATION
	ADD COLUMN method_name VARCHAR(100) AFTER id
;

UPDATE STATE_TRANSITION_EVALUATION
SET method_name = "getApplicationCompletedOutcome"
WHERE id = "APPLICATION_COMPLETED_OUTCOME"
;

UPDATE STATE_TRANSITION_EVALUATION
SET method_name = "getApplicationConfirmSupervisionOutcome"
WHERE id = "APPLICATION_CONFIRM_SUPERVISION_OUTCOME"
;

UPDATE STATE_TRANSITION_EVALUATION
SET method_name = "getApplicationEligibilityAssessedOutcome"
WHERE id = "APPLICATION_ELIGIBILITY_ASSESSED_OUTCOME"
;

UPDATE STATE_TRANSITION_EVALUATION
SET method_name = "getApplicationExportedOutcome"
WHERE id = "APPLICATION_EXPORTED_OUTCOME"
;

UPDATE STATE_TRANSITION_EVALUATION
SET method_name = "getApplicationInterviewAvailabilityOutcome"
WHERE id = "APPLICATION_INTERVIEW_AVAILABILITY_OUTCOME"
;

UPDATE STATE_TRANSITION_EVALUATION
SET method_name = "getApplicationInterviewFeedbackOutcome"
WHERE id = "APPLICATION_INTERVIEW_FEEDBACK_OUTCOME"
;

UPDATE STATE_TRANSITION_EVALUATION
SET method_name = "getApplicationInterviewScheduledOutcome"
WHERE id = "APPLICATION_INTERVIEW_SCHEDULED_OUTCOME"
;

UPDATE STATE_TRANSITION_EVALUATION
SET method_name = "getApplicationProcessingCompletedOutcome"
WHERE id = "APPLICATION_PROCESSING_COMPLETED_OUTCOME"
;

UPDATE STATE_TRANSITION_EVALUATION
SET method_name = "getApplicationRecruitmentOutcome"
WHERE id = "APPLICATION_RECRUITMENT_OUTCOME"
;

UPDATE STATE_TRANSITION_EVALUATION
SET method_name = "getApplicationReviewOutcome"
WHERE id = "APPLICATION_REVIEW_OUTCOME"
;

SET FOREIGN_KEY_CHECKS = 0
;

UPDATE STATE_TRANSITION_EVALUATION
SET id = "APPLICATION_STATE_COMPLETED_OUTCOME"
WHERE id = "APPLICATION_STAGE_COMPLETED_OUTCOME"
;

UPDATE STATE_TRANSITION
SET state_transition_evaluation_id = "APPLICATION_STATE_COMPLETED_OUTCOME"
WHERE id = "APPLICATION_STAGE_COMPLETED_OUTCOME"
;

SET FOREIGN_KEY_CHECKS = 1
;

UPDATE STATE_TRANSITION_EVALUATION
SET method_name = "getApplicationStateCompletedOutcome"
WHERE id = "APPLICATION_STATE_COMPLETED_OUTCOME"
;

UPDATE STATE_TRANSITION_EVALUATION
SET method_name = "getProgramApprovedOutcome"
WHERE id = "PROGRAM_APPROVED_OUTCOME"
;

UPDATE STATE_TRANSITION_EVALUATION
SET method_name = "getProgramConfiguredOutcome"
WHERE id = "PROGRAM_CONFIGURED_OUTCOME"
;

UPDATE STATE_TRANSITION_EVALUATION
SET method_name = "getProgramCreationOutcome"
WHERE id = "PROGRAM_CREATED_OUTCOME"
;

UPDATE STATE_TRANSITION_EVALUATION
SET method_name = "getProgramExpiredOutcome"
WHERE id = "PROGRAM_EXPIRED_OUTCOME"
;

UPDATE STATE_TRANSITION_EVALUATION
SET method_name = "getProgramReactivatedOutcome"
WHERE id = "PROGRAM_REACTIVATED_OUTCOME"
;

UPDATE STATE_TRANSITION_EVALUATION
SET method_name = "getProjectConfiguredOutcome"
WHERE id = "PROJECT_CONFIGURED_OUTCOME"
;

UPDATE STATE_TRANSITION_EVALUATION
SET method_name = "getProjectReactivatedOutcome"
WHERE id = "PROJECT_REACTIVATED_OUTCOME"
;
