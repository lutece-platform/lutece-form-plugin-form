--
-- FORM-125 : Add the possibility to include the response file as email attachment for the OutputProcessor Notify Sender
--
ALTER TABLE form_notify_sender_configuration ADD COLUMN send_attachments smallint default 0 NOT NULL;
