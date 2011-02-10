--
-- Table structure for table form_form_parameter
--
DROP TABLE IF EXISTS form_form_parameter;
CREATE TABLE form_form_parameter (
	parameter_key varchar(100) NOT NULL,
	parameter_value varchar(100) NOT NULL,
	PRIMARY KEY (parameter_key)
);


INSERT INTO form_form_parameter (parameter_key, parameter_value) VALUES ('active_captcha', '0');
INSERT INTO form_form_parameter (parameter_key, parameter_value) VALUES ('limit_number_response', '0');
INSERT INTO form_form_parameter (parameter_key, parameter_value) VALUES ('active_store_adresse', '0');
INSERT INTO form_form_parameter (parameter_key, parameter_value) VALUES ('active_mylutece_authentification', '0');
INSERT INTO form_form_parameter (parameter_key, parameter_value) VALUES ('support_https', '0');
INSERT INTO form_form_parameter (parameter_key, parameter_value) VALUES ('active_requirement', '1');
INSERT INTO form_form_parameter (parameter_key, parameter_value) VALUES ('id_theme_list', NULL);

--
-- Table structure for table form_entry_parameter
--
DROP TABLE IF EXISTS form_entry_parameter;
CREATE TABLE form_entry_parameter (
	parameter_key varchar(100) NOT NULL,
	parameter_value varchar(100),
	PRIMARY KEY (parameter_key)
);

INSERT INTO form_entry_parameter (parameter_key, parameter_value) VALUES ('field_in_line', '1');
INSERT INTO form_entry_parameter (parameter_key, parameter_value) VALUES ('mandatory', '0');
INSERT INTO form_entry_parameter (parameter_key, parameter_value) VALUES ('width', '50');
INSERT INTO form_entry_parameter (parameter_key, parameter_value) VALUES ('height', '50');
INSERT INTO form_entry_parameter (parameter_key, parameter_value) VALUES ('max_size_enter', '250');
INSERT INTO form_entry_parameter (parameter_key, parameter_value) VALUES ('width_text', '50');
INSERT INTO form_entry_parameter (parameter_key, parameter_value) VALUES ('width_text_area', '45');
INSERT INTO form_entry_parameter (parameter_key, parameter_value) VALUES ('height_text_area', '5');