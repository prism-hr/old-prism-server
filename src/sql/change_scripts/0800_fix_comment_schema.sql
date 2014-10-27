ALTER TABLE COMMENT_APPOINTMENT_PREFERENCE
	ADD COLUMN preference_datetime DATETIME AFTER comment_id,
	DROP INDEX comment_id,
	ADD UNIQUE INDEX (comment_id, preference_datetime)
;

UPDATE IGNORE COMMENT_APPOINTMENT_PREFERENCE INNER JOIN COMMENT_APPOINTMENT_TIMESLOT
	ON COMMENT_APPOINTMENT_PREFERENCE.comment_appointment_timeslot_id = COMMENT_APPOINTMENT_TIMESLOT.id
SET COMMENT_APPOINTMENT_PREFERENCE.preference_datetime = COMMENT_APPOINTMENT_TIMESLOT.timeslot_datetime
;

DELETE
FROM COMMENT_APPOINTMENT_PREFERENCE
WHERE preference_datetime IS NULL

DELETE COMMENT_APPOINTMENT_TIMESLOT.*
FROM COMMENT_APPOINTMENT_TIMESLOT LEFT JOIN COMMENT_APPOINTMENT_PREFERENCE
	ON COMMENT_APPOINTMENT_TIMESLOT.id = COMMENT_APPOINTMENT_PREFERENCE.comment_appointment_timeslot_id
WHERE COMMENT_APPOINTMENT_PREFERENCE.id IS NULL
;

ALTER TABLE COMMENT_APPOINTMENT_PREFERENCE
	DROP INDEX comment_id_2,
	DROP FOREIGN KEY comment_appointment_preference_ibfk_2,
	DROP COLUMN comment_appointment_timeslot_id
;
