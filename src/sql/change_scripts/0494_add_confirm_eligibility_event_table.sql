CREATE TABLE CONFIRM_ELIGIBILITY_EVENT (
  id int(10) unsigned NOT NULL,
  admitter_comment_id int(10) unsigned NOT NULL,
  KEY event_id_fk (id),
  KEY confirm_eligibility_event_fk_idx (admitter_comment_id),
  CONSTRAINT confirm_eligibility_event_fk_idx FOREIGN KEY (admitter_comment_id) REFERENCES ADMITTER_COMMENT (id),
  CONSTRAINT event_id_fk FOREIGN KEY (id) REFERENCES EVENT (id)
)
;