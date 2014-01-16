ALTER TABLE REFERENCE 
 ADD COLUMN comment_id INTEGER UNSIGNED,
 ADD COLUMN suitable_for_UCL tinyint(1),
 ADD COLUMN suitable_for_Programme tinyint(1),
 ADD CONSTRAINT reference_comment FOREIGN KEY (comment_id) REFERENCES COMMENT(id);
