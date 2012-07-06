ALTER TABLE REVIEWER 
ADD COLUMN requires_admin_notification VARCHAR(10) DEFAULT 'NO',
ADD COLUMN admins_notified_on DATE;