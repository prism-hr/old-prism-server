CREATE TABLE user_feedback (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  role_category VARCHAR(50) NOT NULL,
  system_id INT(10) UNSIGNED NULL DEFAULT NULL,
  institution_id INT(10) UNSIGNED NULL DEFAULT NULL,
  program_id INT(10) UNSIGNED NULL DEFAULT NULL,
  project_id INT(10) UNSIGNED NULL DEFAULT NULL,
  application_id INT(10) UNSIGNED NULL DEFAULT NULL,
  user_id INT(10) UNSIGNED NULL DEFAULT NULL,
  action_id VARCHAR(100) NOT NULL,
  declined_response INT(1) UNSIGNED NOT NULL,
  rating INT(1) UNSIGNED NULL DEFAULT NULL,
  content MEDIUMTEXT NULL,
  feature_request MEDIUMTEXT NULL,
  recommended INT(1) NULL DEFAULT NULL,
  created_timestamp DATETIME NOT NULL,
  sequence_identifier VARCHAR(23) NULL DEFAULT NULL,
  PRIMARY KEY (id),
  INDEX user_id (user_id),
  INDEX declined_response (declined_response, sequence_identifier),
  INDEX system_id (system_id, sequence_identifier),
  INDEX institution_id (institution_id, sequence_identifier),
  INDEX program_id (program_id, sequence_identifier),
  INDEX project_id (project_id, sequence_identifier),
  INDEX application_id (application_id, sequence_identifier),
  INDEX action_id (action_id),
  CONSTRAINT user_feedback_ibfk_1 FOREIGN KEY (user_id) REFERENCES user (id),
  CONSTRAINT user_feedback_ibfk_2 FOREIGN KEY (institution_id) REFERENCES institution (id),
  CONSTRAINT user_feedback_ibfk_3 FOREIGN KEY (system_id) REFERENCES system (id),
  CONSTRAINT user_feedback_ibfk_4 FOREIGN KEY (program_id) REFERENCES program (id),
  CONSTRAINT user_feedback_ibfk_5 FOREIGN KEY (project_id) REFERENCES project (id),
  CONSTRAINT user_feedback_ibfk_6 FOREIGN KEY (application_id) REFERENCES application (id),
  CONSTRAINT user_feedback_ibfk_7 FOREIGN KEY (action_id) REFERENCES action (id)
)
  COLLATE='utf8_general_ci'
  ENGINE=InnoDB
  AUTO_INCREMENT=1
;
