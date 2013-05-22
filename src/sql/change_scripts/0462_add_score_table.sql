CREATE TABLE SCORE (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  comment_id INTEGER UNSIGNED,
  score_position INTEGER UNSIGNED,
  question_type VARCHAR(15) NOT NULL,
  question VARCHAR(200) NOT NULL,
  text_response VARCHAR(200),
  date_response DATE DEFAULT NULL,
  second_date_response DATE DEFAULT NULL,
  rating_response INTEGER UNSIGNED,

  CONSTRAINT comment_fk FOREIGN KEY (comment_id) REFERENCES COMMENT(id),
  PRIMARY KEY(id)
) ENGINE = InnoDB
;
