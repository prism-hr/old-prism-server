CREATE TABLE SUPERVISION_CONFIRMATION_COMMENT (
  id INTEGER UNSIGNED NOT NULL,
  supervisor_id INTEGER UNSIGNED,
  project_title varchar(100),
  project_abstract varchar(2000),
  recommended_start_date date,
  recommended_conditions_available tinyint(1),
  recommended_conditions varchar(1000),
  comment_type VARCHAR(50),
  
  CONSTRAINT supervisor_inter_comment_fk FOREIGN KEY (supervisor_id) REFERENCES SUPERVISOR(id),
  CONSTRAINT supervision_confirmation_comment_fk FOREIGN KEY (id) REFERENCES COMMENT(id)
)
ENGINE = InnoDB;