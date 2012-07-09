CREATE TABLE REFERENCE_COMMENT (
  id INTEGER UNSIGNED NOT NULL,
  suitable_for_UCL tinyint(1),
  suitable_for_Programme tinyint(1),
  updated_time_stamp TIMESTAMP NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  comment_type VARCHAR(50),
  referee_id INTEGER UNSIGNED,
  CONSTRAINT refereee_refe_co_fk FOREIGN KEY (referee_id) REFERENCES APPLICATION_FORM_REFEREE(id),
  CONSTRAINT reference_comment_fk FOREIGN KEY (id) REFERENCES COMMENT(id)
)
ENGINE = InnoDB;