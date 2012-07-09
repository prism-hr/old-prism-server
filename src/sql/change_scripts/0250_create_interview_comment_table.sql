CREATE TABLE INTERVIEW_COMMENT (
  id INTEGER UNSIGNED NOT NULL,
  willing_to_supervise VARCHAR(30),
  suitable_candidate VARCHAR(30),
  decline VARCHAR(30),
  comment_type VARCHAR(50),
  admins_notified VARCHAR(10),
  interviewer_id INTEGER UNSIGNED,
  CONSTRAINT interviewer_inter_comment_fk FOREIGN KEY (interviewer_id) REFERENCES INTERVIEWER(id),
  CONSTRAINT interview_comment_fk FOREIGN KEY (id) REFERENCES COMMENT(id)
)
ENGINE = InnoDB;