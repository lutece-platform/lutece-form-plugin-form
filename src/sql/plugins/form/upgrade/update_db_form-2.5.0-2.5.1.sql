--
-- FORM-152 : Add a daemon that will export the form responses in a physical file
--
INSERT INTO form_form_parameter (parameter_key, parameter_value) VALUES ('id_export_format_daemon', '3');
INSERT INTO form_form_parameter (parameter_key, parameter_value) VALUES ('export_daemon_type', 'full');
