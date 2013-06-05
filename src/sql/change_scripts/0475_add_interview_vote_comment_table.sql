CREATE TABLE IF NOT EXISTS INTERVIEW_VOTE_COMMENT (
  id INTEGER UNSIGNED NOT NULL,
  comment_type VARCHAR(50) NOT NULL,

  CONSTRAINT interview_vote_comment_fk FOREIGN KEY (id) REFERENCES COMMENT(id)
)
ENGINE = InnoDB;