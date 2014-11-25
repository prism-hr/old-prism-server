ALTER TABLE INSTITUTION
	CHANGE COLUMN google_identifier google_id VARCHAR(255),
	CHANGE COLUMN linkedin_identifier linkedin_uri VARCHAR(255),
	ADD COLUMN twitter_uri VARCHAR(255) AFTER linkedin_uri,
	ADD COLUMN facebook_id VARCHAR(255) AFTER twitter_uri,
	DROP INDEX linkedin_identifier
;

ALTER TABLE USER
	ADD COLUMN portrait_document_id INT(10) UNSIGNED AFTER email,
	ADD COLUMN linkedin_uri VARCHAR(255) AFTER portrait_document_id,
	ADD COLUMN twitter_uri VARCHAR(255) AFTER linkedin_uri,
	ADD INDEX (portrait_document_id),
	ADD FOREIGN KEY (portrait_document_id) REFERENCES DOCUMENT(id)
;

ALTER TABLE INSTITUTION
	ADD COLUMN summary VARCHAR(5000) AFTER currency,
	ADD COLUMN description TEXT AFTER summary
;

ALTER TABLE ADVERT
	MODIFY COLUMN summary VARCHAR(5000)
;

UPDATE INSTITUTION
SET summary = "UCL (University College London) is London’s leading multidisciplinary university, with 10,000 staff and 27,000 students. Over 150 nationalities are represented among UCL students with overseas students making up nearly a third of the student body. 

UCL is currently ranked fourth in the world by the QS World University Rankings, and our publications in clinical medicine attract more citations from fellow researchers than any other university outside North America. 

Based in the heart of London, UCL was the first university in England to welcome students of any class, race or religion, and the first to welcome women on equal terms with men. This same spirit is manifested today in UCL's pioneering approach to interdisciplinary and frontier research. This has helped the university to develop strong linkages with UK Industry and has contributed to it's considerable success in winning research funding. 

In the most recent UK Government Research Assessment Exercise (RAE) UCL was rated the best research university in London, and third in the UK overall, for the number of its submissions which were considered of ‘world-leading quality’. The RAE confirmed UCL’s multidisciplinary research strength with outstanding results achieved across the subjects: Biomedicine, Science and Engineering, the Built Environment, Laws, Social Sciences and Arts and Humanities. 

See degree programmes: http://www.ucl.ac.uk/prospective-students 
Identify research in your area: http://www.ucl.ac.uk/irissee less"
WHERE id = 5243
;

UPDATE INSTITUTION
SET summary = "TEMPORARY"
WHERE id != 5243
;

ALTER TABLE INSTITUTION
	MODIFY COLUMN summary VARCHAR(5000) NOT NULL
;
