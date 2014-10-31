ALTER TABLE SCOPE
	DROP FOREIGN KEY scope_ibfk_1,
	DROP COLUMN fallback_action_id
;

ALTER TABLE ACTION
	ADD COLUMN fallback_action_id VARCHAR(100) AFTER action_category,
	ADD INDEX (fallback_action_id),
	ADD FOREIGN KEY (fallback_action_id) REFERENCES ACTION (id)
;
