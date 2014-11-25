ALTER TABLE NOTIFICATION_CONFIGURATION
	DROP FOREIGN KEY notification_configuration_ibfk_4,
	CHANGE COLUMN notification_template_id notification_definition_id VARCHAR(100) NOT NULL,
	ADD FOREIGN KEY (notification_definition_id) REFERENCES NOTIFICATION_DEFINITION (id)
;

ALTER TABLE STATE_ACTION
	DROP FOREIGN KEY state_action_ibfk_3,
	CHANGE COLUMN notification_template_id notification_definition_id VARCHAR(100),
	ADD FOREIGN KEY (notification_definition_id) REFERENCES NOTIFICATION_DEFINITION (id)
;
