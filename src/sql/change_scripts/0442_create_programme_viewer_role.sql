CREATE TABLE PROGRAM_VIEWER_LINK (
  viewer_id int(10) UNSIGNED NOT NULL,
  program_id int(10) UNSIGNED NOT NULL,
  KEY user_program_user_fk (viewer_id),
  KEY user_program_program_fk (program_id),
  CONSTRAINT viewer_program_program_fk FOREIGN KEY (program_id) REFERENCES PROGRAM (id),
  CONSTRAINT viewer_program_user_fk FOREIGN KEY (viewer_id) REFERENCES REGISTERED_USER (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
;

INSERT INTO APPLICATION_ROLE (authority) VALUES ('VIEWER')
;
