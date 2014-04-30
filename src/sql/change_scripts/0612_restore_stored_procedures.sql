DROP PROCEDURE IF EXISTS SP_SELECT_RECOMMENDED_ADVERTS
;

CREATE PROCEDURE SP_SELECT_RECOMMENDED_ADVERTS (
	IN in_registered_user_id INT, 
	IN in_ranking_threshold DECIMAL(3,2))
BEGIN

	DECLARE ranking_threshold INT(10) UNSIGNED;
	DECLARE baseline_date DATE;
	
	SET baseline_date = CURRENT_DATE();

	CREATE TEMPORARY TABLE RECOMMENDED_ADVERT (
		id INT(10) UNSIGNED NOT NULL,
		title VARCHAR(255) NOT NULL,
		description VARCHAR(3000) NOT NULL,
		study_duration INT(4) NOT NULL,
		funding VARCHAR(2000) NOT NULL,
		program_code VARCHAR(50) NOT NULL,
		closing_date DATE,
		primary_supervisor_first_name VARCHAR(30) NOT NULL,
		primary_supervisor_last_name VARCHAR(40) NOT NULL,
		primary_supervisor_email VARCHAR(255) NOT NULL,
		advert_type VARCHAR(10) NOT NULL,
		secondary_supervisor_first_name VARCHAR(30),
		secondary_supervisor_last_name VARCHAR(40),
		ranking DECIMAL(8,2) NOT NULL DEFAULT 0.00,
		last_edited_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
		PRIMARY KEY (id),
		INDEX (last_edited_timestamp),
		INDEX (ranking)) ENGINE = MEMORY
		SELECT ADVERT.id AS id, ADVERT.title AS title, ADVERT.description AS description, ADVERT.study_duration AS study_duration,
			ADVERT.funding AS funding, PROGRAM.code AS program_code, PROGRAM_CLOSING_DATES.closing_date AS closing_date,
			REGISTERED_USER2.firstname AS primary_supervisor_first_name, REGISTERED_USER2.lastname AS primary_supervisor_last_name,
			REGISTERED_USER2.email AS primary_supervisor_email, "PROGRAM" AS advert_type, NULL AS secondary_supervisor_first_name, 
			NULL AS secondary_supervisor_last_name, ROUND(SQRT(COUNT(DISTINCT(APPLICATION_FORM2.id))), 2) AS ranking, 
			ADVERT.last_edited_timestamp AS last_edited_timestamp
		FROM APPLICATION_FORM INNER JOIN APPLICATION_FORM_USER_ROLE
			ON APPLICATION_FORM.id = APPLICATION_FORM_USER_ROLE.application_form_id
		INNER JOIN REGISTERED_USER
			ON APPLICATION_FORM_USER_ROLE.registered_user_id = REGISTERED_USER.id
		INNER JOIN APPLICATION_FORM_USER_ROLE AS APPLICATION_FORM_USER_ROLE2
			ON REGISTERED_USER.id = APPLICATION_FORM_USER_ROLE2.registered_user_id
		INNER JOIN APPLICATION_FORM AS APPLICATION_FORM2
			ON APPLICATION_FORM_USER_ROLE2.application_form_id = APPLICATION_FORM2.id
		INNER JOIN PROGRAM
			ON APPLICATION_FORM2.program_id = PROGRAM.id
		INNER JOIN ADVERT
			ON PROGRAM.id = ADVERT.id
		INNER JOIN REGISTERED_USER AS REGISTERED_USER2
			ON ADVERT.registered_user_id = REGISTERED_USER2.id
		LEFT JOIN PROGRAM_CLOSING_DATES
			ON PROGRAM.id = PROGRAM_CLOSING_DATES.program_id
		WHERE APPLICATION_FORM.applicant_id = in_registered_user_id
			AND APPLICATION_FORM_USER_ROLE.application_role_id IN ("APPROVER", "INTERVIEWER",
				"PROJECTADMINISTRATOR", "REVIEWER", "SUGGESTEDSUPERVISOR", "STATEADMINISTRATOR", "SUPERVISOR")
			AND REGISTERED_USER.enabled = 1
			AND APPLICATION_FORM.program_id != PROGRAM.id
			AND ADVERT.enabled = 1
			AND ADVERT.active = 1
			AND (PROGRAM_CLOSING_DATES.id IS NULL
				OR PROGRAM_CLOSING_DATES.closing_date >= baseline_date)
			AND APPLICATION_FORM2.status NOT IN ("UNSUBMITTED", "WITHDRAWN")
		GROUP BY ADVERT.id;

	INSERT INTO RECOMMENDED_ADVERT
		SELECT *
		FROM (
			SELECT ADVERT.id AS id, ADVERT.title AS title, ADVERT.description AS description, ADVERT.study_duration AS study_duration,
				ADVERT.funding AS funding, PROGRAM.code AS program_code, PROGRAM_CLOSING_DATES.closing_date AS closing_date,
				REGISTERED_USER.firstname AS primary_supervisor_first_name, REGISTERED_USER.lastname AS primary_supervisor_last_name,
				REGISTERED_USER.email AS primary_supervisor_email, "PROGRAM" AS advert_type, NULL AS secondary_supervisor_first_name, 
				NULL AS secondary_supervisor_last_name, ROUND(SQRT(COUNT(DISTINCT(APPLICATION_FORM3.id))), 2) AS ranking, 
				ADVERT.last_edited_timestamp AS last_edited_timestamp
			FROM APPLICATION_FORM INNER JOIN APPLICATION_FORM AS APPLICATION_FORM2
				ON APPLICATION_FORM.program_id = APPLICATION_FORM2.program_id
			INNER JOIN APPLICATION_FORM AS APPLICATION_FORM3
				ON APPLICATION_FORM2.applicant_id = APPLICATION_FORM3.applicant_id
			INNER JOIN PROGRAM
				ON APPLICATION_FORM3.program_id = PROGRAM.id
			INNER JOIN ADVERT
				ON PROGRAM.id = ADVERT.id
			INNER JOIN REGISTERED_USER
				ON ADVERT.registered_user_id = REGISTERED_USER.id
			LEFT JOIN PROGRAM_CLOSING_DATES
				ON PROGRAM.id = PROGRAM_CLOSING_DATES.program_id
			WHERE APPLICATION_FORM.applicant_id = in_registered_user_id
				AND APPLICATION_FORM.program_id != PROGRAM.id
				AND ADVERT.enabled = 1
				AND ADVERT.active = 1
				AND (PROGRAM_CLOSING_DATES.id IS NULL
					OR PROGRAM_CLOSING_DATES.closing_date >= baseline_date)
				AND APPLICATION_FORM3.status NOT IN ("UNSUBMITTED", "WITHDRAWN")
			GROUP BY ADVERT.id) AS RECOMMENDED_ADVERT_SECONDARY
		ON DUPLICATE KEY UPDATE RECOMMENDED_ADVERT.ranking = RECOMMENDED_ADVERT.ranking + RECOMMENDED_ADVERT_SECONDARY.ranking;

	INSERT INTO RECOMMENDED_ADVERT
		SELECT ADVERT.id AS id, ADVERT.title AS title, ADVERT.description AS description, ADVERT.study_duration AS study_duration,
			ADVERT.funding AS funding, PROGRAM.code AS program_code, PROGRAM_CLOSING_DATES.closing_date AS closing_date,
			REGISTERED_USER2.firstname AS primary_supervisor_first_name, REGISTERED_USER2.lastname AS primary_supervisor_last_name,
			REGISTERED_USER2.email AS primary_supervisor_email, "PROJECT" AS advert_type,
			REGISTERED_USER3.firstname AS secondary_supervisor_first_name,
			REGISTERED_USER3.lastname AS secondary_supervisor_last_name, 
			ROUND(SQRT(COUNT(DISTINCT(APPLICATION_FORM2.id))), 2) AS ranking, ADVERT.last_edited_timestamp AS last_edited_timestamp
		FROM APPLICATION_FORM INNER JOIN APPLICATION_FORM_USER_ROLE
			ON APPLICATION_FORM.id = APPLICATION_FORM_USER_ROLE.application_form_id
		INNER JOIN REGISTERED_USER
			ON APPLICATION_FORM_USER_ROLE.registered_user_id = REGISTERED_USER.id
		INNER JOIN APPLICATION_FORM_USER_ROLE AS APPLICATION_FORM_USER_ROLE2
			ON APPLICATION_FORM_USER_ROLE.registered_user_id = APPLICATION_FORM_USER_ROLE2.registered_user_id
		INNER JOIN APPLICATION_FORM AS APPLICATION_FORM2
			ON APPLICATION_FORM_USER_ROLE2.application_form_id = APPLICATION_FORM2.id
		INNER JOIN PROGRAM
			ON APPLICATION_FORM2.program_id = PROGRAM.id
		INNER JOIN PROJECT
			ON APPLICATION_FORM2.project_id = PROJECT.id
		INNER JOIN ADVERT
			ON PROJECT.id = ADVERT.id
		INNER JOIN REGISTERED_USER AS REGISTERED_USER2
			ON PROJECT.primary_supervisor_id = REGISTERED_USER2.id
		LEFT JOIN REGISTERED_USER AS REGISTERED_USER3
			ON PROJECT.secondary_supervisor_id = REGISTERED_USER3.id
		LEFT JOIN PROGRAM_CLOSING_DATES
			ON PROGRAM.id = PROGRAM_CLOSING_DATES.program_id
		WHERE APPLICATION_FORM.applicant_id = in_registered_user_id
			AND APPLICATION_FORM_USER_ROLE.application_role_id IN ("APPROVER", "INTERVIEWER",
				"PROJECTADMINISTRATOR", "REVIEWER", "SUGGESTEDSUPERVISOR", "STATEADMINISTRATOR", "SUPERVISOR")
			AND REGISTERED_USER.enabled = 1
			AND (APPLICATION_FORM.project_id is NULL
				OR APPLICATION_FORM.project_id != PROJECT.id)
			AND ADVERT.enabled = 1
			AND ADVERT.active = 1
			AND (PROGRAM_CLOSING_DATES.id IS NULL
				OR PROGRAM_CLOSING_DATES.closing_date >= baseline_date)
			AND APPLICATION_FORM2.status NOT IN ("UNSUBMITTED", "WITHDRAWN")
		GROUP BY ADVERT.id;

	INSERT INTO RECOMMENDED_ADVERT
		SELECT *
		FROM (
			SELECT ADVERT.id AS id, ADVERT.title AS title, ADVERT.description AS description, ADVERT.study_duration AS study_duration,
				ADVERT.funding AS funding, PROGRAM.code AS program_code, PROGRAM_CLOSING_DATES.closing_date AS closing_date,
				REGISTERED_USER.firstname AS primary_supervisor_first_name, REGISTERED_USER.lastname AS primary_supervisor_last_name,
				REGISTERED_USER.email AS primary_supervisor_email, "PROJECT" AS advert_type,
				REGISTERED_USER2.firstname AS secondary_supervisor_first_name,
				REGISTERED_USER2.lastname AS secondary_supervisor_last_name, 
				ROUND(SQRT(COUNT(DISTINCT(APPLICATION_FORM3.id))), 2) AS ranking, ADVERT.last_edited_timestamp AS last_edited_timestamp
			FROM APPLICATION_FORM INNER JOIN APPLICATION_FORM AS APPLICATION_FORM2
				ON APPLICATION_FORM.program_id = APPLICATION_FORM2.program_id
			INNER JOIN APPLICATION_FORM AS APPLICATION_FORM3
				ON APPLICATION_FORM2.applicant_id = APPLICATION_FORM3.applicant_id
			INNER JOIN PROGRAM
				ON APPLICATION_FORM3.program_id = PROGRAM.id
			INNER JOIN PROJECT
				ON APPLICATION_FORM3.project_id = PROJECT.id
			INNER JOIN ADVERT
				ON PROJECT.id = ADVERT.id
			INNER JOIN REGISTERED_USER
				ON PROJECT.primary_supervisor_id = REGISTERED_USER.id
			LEFT JOIN REGISTERED_USER AS REGISTERED_USER2
				ON PROJECT.secondary_supervisor_id = REGISTERED_USER2.id
			LEFT JOIN PROGRAM_CLOSING_DATES
				ON PROGRAM.id = PROGRAM_CLOSING_DATES.program_id
			WHERE APPLICATION_FORM.applicant_id = in_registered_user_id
				AND (APPLICATION_FORM.project_id is NULL
					OR APPLICATION_FORM.project_id != PROJECT.id)
				AND ADVERT.enabled = 1
				AND ADVERT.active = 1
				AND (PROGRAM_CLOSING_DATES.id IS NULL
					OR PROGRAM_CLOSING_DATES.closing_date >= baseline_date)
				AND APPLICATION_FORM3.status NOT IN ("UNSUBMITTED", "WITHDRAWN")
			GROUP BY ADVERT.id) AS RECOMMENDED_ADVERT_SECONDARY
		ON DUPLICATE KEY UPDATE RECOMMENDED_ADVERT.ranking = RECOMMENDED_ADVERT.ranking + RECOMMENDED_ADVERT_SECONDARY.ranking;

	SET ranking_threshold = (
			SELECT ROUND(MAX(RECOMMENDED_ADVERT.ranking) * in_ranking_threshold)
			FROM RECOMMENDED_ADVERT);

	SELECT RECOMMENDED_ADVERT.id AS id, RECOMMENDED_ADVERT.title AS title, RECOMMENDED_ADVERT.description AS description,
		RECOMMENDED_ADVERT.study_duration AS studyDuration, RECOMMENDED_ADVERT.funding AS funding,
		RECOMMENDED_ADVERT.program_code AS programCode, RECOMMENDED_ADVERT.closing_date AS closingDate,
		RECOMMENDED_ADVERT.primary_supervisor_first_name AS primarySupervisorFirstName,
		RECOMMENDED_ADVERT.primary_supervisor_last_name AS primarySupervisorLastName,
		RECOMMENDED_ADVERT.primary_supervisor_email AS primarySupervisorEmail, RECOMMENDED_ADVERT.advert_type AS advertType,
		RECOMMENDED_ADVERT.secondary_supervisor_first_name AS secondarySupervisorFirstName,
		RECOMMENDED_ADVERT.secondary_supervisor_last_name AS secondarySupervisorLastName,
		RECOMMENDED_ADVERT.ranking
	FROM RECOMMENDED_ADVERT
	WHERE RECOMMENDED_ADVERT.ranking >= ranking_threshold
	ORDER BY RECOMMENDED_ADVERT.ranking DESC,
		RECOMMENDED_ADVERT.last_edited_timestamp;

	DROP TABLE RECOMMENDED_ADVERT;

END
;
