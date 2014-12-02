ALTER TABLE DISPLAY_PROPERTY_CONFIGURATION
	DROP FOREIGN KEY display_property_configuration_ibfk_1,
	CHANGE COLUMN display_property_id display_property_definition_id VARCHAR(100) NOT NULL,
	ADD FOREIGN KEY (display_property_definition_id) REFERENCES DISPLAY_PROPERTY_DEFINITION (id)
;

RENAME TABLE NOTIFICATION_TEMPLATE TO NOTIFICATION_DEFINITION
;

ALTER TABLE NOTIFICATION_DEFINITION
	CHANGE COLUMN reminder_notification_template_id reminder_definition_id VARCHAR(100)
;
