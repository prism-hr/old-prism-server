--run on the server to trigger email notification
--change application id accordingly

UPDATE APPLICATION_FORM SET validation_stage = 'TRUE', validation_due_date='2012-01-17', last_email_reminder_date = '2012-02-01' WHERE id = 42948;