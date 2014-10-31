ALTER TABLE USER
	ADD COLUMN latest_creation_scope_id VARCHAR(50) AFTER last_notified_date_application,
	ADD INDEX (latest_creation_scope_id),
	ADD FOREIGN KEY (latest_creation_scope_id) REFERENCES SCOPE (id)
;

UPDATE USER INNER JOIN INSTITUTION
	ON USER.id = INSTITUTION.user_id
SET USER.latest_creation_scope_id = "INSTITUTION"
WHERE USER.latest_creation_scope_id IS NULL
;

UPDATE USER INNER JOIN PROGRAM
	ON USER.id = PROGRAM.user_id
SET USER.latest_creation_scope_id = "PROGRAM"
WHERE USER.latest_creation_scope_id IS NULL
;

UPDATE USER INNER JOIN PROJECT
	ON USER.id = PROJECT.user_id
SET USER.latest_creation_scope_id = "PROJECT"
WHERE USER.latest_creation_scope_id IS NULL
;

UPDATE USER INNER JOIN APPLICATION
	ON USER.id = APPLICATION.user_id
SET USER.latest_creation_scope_id = "APPLICATION"
WHERE USER.latest_creation_scope_id IS NULL
;
