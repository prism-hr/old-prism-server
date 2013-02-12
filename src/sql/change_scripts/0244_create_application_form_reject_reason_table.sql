CREATE TABLE APPLICATION_FORM_REJECT_REASON (
  application_id INTEGER UNSIGNED NOT NULL,
  reason_id INTEGER UNSIGNED NOT NULL,
  CONSTRAINT reject_application_fk FOREIGN KEY (application_id) REFERENCES APPLICATION_FORM(id),
  CONSTRAINT reject_reason_fk FOREIGN KEY (reason_id) REFERENCES REJECT_REASON(id)
)
ENGINE = InnoDB;

--//@UNDO

DROP TABLE IF EXISTS APPLICATION_FORM_REJECT_REASON;