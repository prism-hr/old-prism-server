CREATE TABLE IF NOT EXISTS INTERVIEW_SCHEDULE_COMMENT (
  id INTEGER UNSIGNED NOT NULL,
  comment_type VARCHAR(50) NOT NULL,
  further_details VARCHAR(2000),
  further_interviewer_details VARCHAR(2000),
  
  CONSTRAINT interview_schedule_comment_fk FOREIGN KEY (id) REFERENCES COMMENT(id)
)
ENGINE = InnoDB;