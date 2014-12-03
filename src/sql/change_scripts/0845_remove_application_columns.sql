ALTER TABLE APPLICATION
	DROP FOREIGN KEY application_ibfk_8,
	DROP INDEX confirmed_supervisor_user_id,
	DROP COLUMN confirmed_primary_supervisor_id,
	DROP FOREIGN KEY application_ibfk_9,
	DROP INDEX confirmed_secondary_supervisor_id,
	DROP COLUMN confirmed_secondary_supervisor_id
;
