CREATE TABLE INTERVIEW_STATE_CHANGE_EVENT (
  id INTEGER UNSIGNED NOT NULL,
  interview_id INTEGER UNSIGNED,
  CONSTRAINT interview_event_interview_fk FOREIGN KEY (interview_id) REFERENCES INTERVIEW(id),
  CONSTRAINT interview_event_fk FOREIGN KEY (id) REFERENCES EVENT(id)
)
ENGINE = InnoDB;