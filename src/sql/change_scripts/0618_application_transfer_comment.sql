CREATE TABLE application_transfer_comment (
	id INT(10) UNSIGNED NOT NULL,
	comment_type VARCHAR(50) DEFAULT "APPLICATION_TRANSFER_COMMENT",
	succeeded INT(1) UNSIGNED NOT NULL,
	application_form_transfer_error_id BIGINT,
	PRIMARY KEY (id),
	INDEX (application_form_transfer_error_id),
	FOREIGN KEY (id) REFERENCES comment (id),
	FOREIGN KEY (application_form_transfer_error_id) REFERENCES application_form_transfer_error (id)
) ENGINE = INNODB
;

INSERT INTO COMMENT (application_form_id, comment, user_id, created_timestamp)
	SELECT application_id, "APPLICATION_FORM_TRANSFER", 15, transfer_end_timepoint
	FROM application_form_transfer
	WHERE status = "COMPLETED"
;

INSERT INTO APPLICATION_TRANSFER_COMMENT (id, succeeded)
	SELECT id, 1
	FROM COMMENT
	where comment = "APPLICATION_FORM_TRANSFER"
;

UPDATE COMMENT
SET comment = NULL
WHERE comment = "APPLICATION_FORM_TRANSFER"
;

INSERT INTO COMMENT (application_form_id, comment, user_id, created_timestamp)
	SELECT application_form_transfer.application_id, CONCAT("APPLICATION_FORM_TRANSFER_", application_form_transfer_error.id), 
		15, application_form_transfer_error.handling_time
	FROM application_form_transfer INNER JOIN application_form_transfer_error
		ON application_form_transfer.id = application_form_transfer_error.transfer_id
;

INSERT INTO APPLICATION_TRANSFER_COMMENT (id, succeeded, application_form_transfer_error_id)
	SELECT id, 0, CAST(REPLACE(comment, "APPLICATION_FORM_TRANSFER_", "") AS UNSIGNED)
	FROM COMMENT
	WHERE comment LIKE "APPLICATION_FORM_TRANSFER%"
;

UPDATE COMMENT
SET comment = NULL
WHERE comment LIKE "APPLICATION_FORM_TRANSFER%"
;
