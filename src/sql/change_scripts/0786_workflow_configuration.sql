ALTER TABLE COMMENT
	DROP COLUMN application_suitable_for_institution,
	DROP COLUMN application_suitable_for_opportunity,
	DROP COLUMN application_desire_to_interview,
	DROP COLUMN application_desire_to_recruit
;

DELETE
FROM DISPLAY_PROPERTY
;

DELETE
FROM IMPORTED_ENTITY_FEED
where imported_entity_type = "RESIDENCE_STATE"
;

DELETE
FROM IMPORTED_ENTITY
WHERE imported_entity_type = "RESIDENCE_STATE"
;
