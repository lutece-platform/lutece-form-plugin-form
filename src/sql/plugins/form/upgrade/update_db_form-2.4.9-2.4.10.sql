--
-- FORM-145 : Add the possibility to upload multiple files
--
INSERT INTO form_entry_parameter (parameter_key, parameter_value) VALUES ('file_max_size', '2097152');
INSERT INTO form_entry_parameter (parameter_key, parameter_value) VALUES ('max_files', '1');
