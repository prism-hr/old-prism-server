ALTER TABLE COMMENT
	ADD INDEX (application_id, application_rating)
;

ALTER TABLE APPLICATION
	ADD INDEX (institution_id, rating_count),
	ADD INDEX (institution_id, average_rating),
	ADD INDEX (program_id, rating_count),
	ADD INDEX (program_id, average_rating),
	ADD INDEX (project_id, rating_count),
	ADD INDEX (project_id, average_rating)
;

ALTER TABLE APPLICATION
	ADD INDEX (rating_count),
	ADD INDEX (average_rating)
;
