CREATE TABLE REVIEW_EVALUATION_COMMENT (
  id INTEGER UNSIGNED NOT NULL,
  review_round_id INTEGER UNSIGNED,
  CONSTRAINT review_evaluation_comment_fk FOREIGN KEY (id) REFERENCES COMMENT(id),
  CONSTRAINT review_eval_com_rev_round_fk FOREIGN KEY (review_round_id) REFERENCES REVIEW_ROUND(id)
)
ENGINE = InnoDB;