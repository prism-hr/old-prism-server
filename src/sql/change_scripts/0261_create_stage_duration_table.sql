CREATE TABLE STAGE_DURATION (
  stage VARCHAR(100) NOT NULL, 	
  duration INTEGER UNSIGNED, 	
  unit VARCHAR(50),
  PRIMARY KEY (stage)
)
ENGINE = InnoDB;