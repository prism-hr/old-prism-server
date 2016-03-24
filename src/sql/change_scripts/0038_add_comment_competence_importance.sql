ALTER TABLE comment_competence
ADD COLUMN importance TINYINT UNSIGNED
AFTER competence_id
;

UPDATE comment_competence
SET importance = 2
;

ALTER TABLE comment_competence
MODIFY COLUMN importance TINYINT UNSIGNED NOT NULL
;
