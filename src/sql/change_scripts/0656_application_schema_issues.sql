DELETE ROLE_TRANSITION_TYPE.*
FROM ROLE_TRANSITION_TYPE LEFT JOIN ROLE_TRANSITION
	ON ROLE_TRANSITION_TYPE.id = ROLE_TRANSITION.role_transition_type_id
WHERE ROLE_TRANSITION.role_transition_type_id IS NULL
;

CREATE TABLE APPLICATION_SUPERVISOR (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	application_program_detail_id INT(10) UNSIGNED NOT NULL,
	user_id INT(10) UNSIGNED NOT NULL,
	aware_of_application INT(1) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (application_program_detail_id, user_id),
	INDEX (user_id),
	FOREIGN KEY (application_program_detail_id) REFERENCES APPLICATION_PROGRAM_DETAIL (id),
	FOREIGN KEY (user_id) REFERENCES USER (id)
) ENGINE = INNODB
	SELECT NULL AS id, APPLICATION_PROGRAM_DETAIL.id AS application_program_detail_id, 
		SUGGESTED_SUPERVISOR.user_id AS user_id, 
		SUGGESTED_SUPERVISOR.aware AS aware_of_application
	FROM APPLICATION_PROGRAM_DETAIL INNER JOIN SUGGESTED_SUPERVISOR
		ON APPLICATION_PROGRAM_DETAIL.id = SUGGESTED_SUPERVISOR.programme_detail_id
	GROUP BY APPLICATION_PROGRAM_DETAIL.id, SUGGESTED_SUPERVISOR.user_id
;

DROP TABLE SUGGESTED_SUPERVISOR
;

ALTER TABLE INSTITUTION
	ADD COLUMN domicile_id INT(10) UNSIGNED AFTER id
;

UPDATE IGNORE INSTITUTION
SET domicile_code = "842"
WHERE domicile_code = "RU"
;

UPDATE APPLICATION_QUALIFICATION INNER JOIN INSTITUTION
	ON APPLICATION_QUALIFICATION.institution_id = INSTITUTION.id
INNER JOIN INSTITUTION AS ALTERNATE_INSTITUTION
	ON INSTITUTION.name = ALTERNATE_INSTITUTION.name
	AND INSTITUTION.domicile_code = "RU"
	AND ALTERNATE_INSTITUTION.domicile_code = 842
SET APPLICATION_QUALIFICATION.institution_id = ALTERNATE_INSTITUTION.id
;

DELETE FROM INSTITUTION
WHERE domicile_code = "RU"
;

UPDATE INSTITUTION INNER JOIN DOMICILE
	ON INSTITUTION.domicile_code = DOMICILE.code
SET INSTITUTION.domicile_id = DOMICILE.id
WHERE DOMICILE.enabled = 1
;

ALTER TABLE INSTITUTION
	MODIFY COLUMN domicile_id INT(10) UNSIGNED NOT NULL,
	ADD UNIQUE INDEX (domicile_id, name),
	ADD FOREIGN KEY (domicile_id) REFERENCES DOMICILE (id),
	DROP INDEX domicile_code,
	DROP COLUMN domicile_code
;
