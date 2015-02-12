ALTER TABLE institution
  DROP COLUMN linkedin_uri,
  DROP COLUMN facebook_uri,
  DROP COLUMN twitter_uri
;

ALTER TABLE user
  DROP COLUMN linkedin_uri,
  DROP COLUMN twitter_uri
;
