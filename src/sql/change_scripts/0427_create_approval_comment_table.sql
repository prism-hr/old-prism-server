CREATE TABLE APPROVAL_COMMENT (
  id INTEGER UNSIGNED NOT NULL,
  project_description_available tinyint(1),
  project_title varchar(100),
  project_abstract varchar(2000),
  recommended_start_date date,
  recommended_conditions_available tinyint(1),
  recommended_conditions varchar(1000),
  comment_type VARCHAR(50)
  
)
ENGINE = InnoDB;