--/* Fix program instance */
--
--DELETE PROGRAM_INSTANCE.* 
--FROM PROGRAM_INSTANCE INNER JOIN (
--	SELECT MAX(id) AS id,
--		program_id AS program_id
--	FROM PROGRAM_INSTANCE
--	GROUP BY program_id, academic_year, program_study_option_id
--	HAVING COUNT(id) > 1) AS DUPLICATE_PROGRAM_INSTANCE
--	ON PROGRAM_INSTANCE.program_id = DUPLICATE_PROGRAM_INSTANCE.program_id
--	AND PROGRAM_INSTANCE.id != DUPLICATE_PROGRAM_INSTANCE.id
--;
--
--ALTER TABLE PROGRAM_INSTANCE
--	ADD UNIQUE INDEX (program_id, academic_year, program_study_option_id),
--	DROP INDEX program_instance_prog_fk
--;
--
--/* Add missing columns to transient resource entities */
--
--ALTER TABLE ADVERT
--	ADD COLUMN created_timestamp DATETIME AFTER advert_type
--;
--
--UPDATE ADVERT INNER JOIN PROJECT
--	ON ADVERT.id = PROJECT.id
--SET ADVERT.created_timestamp = "2014-01-02 09:00:00"
--;
--
--UPDATE ADVERT INNER JOIN PROGRAM
--	ON ADVERT.id = PROGRAM.id
--INNER JOIN (
--	SELECT PROGRAM.id AS program_id,
--		MIN(PROGRAM_INSTANCE.start_date) AS created_timestamp
--	FROM PROGRAM INNER JOIN PROGRAM_INSTANCE
--		ON PROGRAM.id = PROGRAM_INSTANCE.program_id
--	GROUP BY PROGRAM.id) AS PROGRAM_HISTORY
--	ON PROGRAM.id = PROGRAM_HISTORY.program_id
--SET ADVERT.created_timestamp = PROGRAM_HISTORY.created_timestamp
--;
--
--ALTER TABLE ADVERT
--	MODIFY COLUMN created_timestamp DATETIME NOT NULL,
--	DROP COLUMN advert_type
--;

/* Remove processing order from state transition */

/* Make transition action not null */

/* Create default action assignment */

/* Remove ALL_COMPLETED action and replace with evaluation */

/* Tidy up code and created timestamp columns on application, program, user */