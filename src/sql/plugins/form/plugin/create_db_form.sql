DROP TABLE IF EXISTS form_verify_by;
DROP TABLE IF EXISTS form_response;
DROP TABLE IF EXISTS form_submit;
DROP TABLE IF EXISTS form_field;
DROP TABLE IF EXISTS form_entry;
DROP TABLE IF EXISTS form_form;
DROP TABLE IF EXISTS form_recap;
DROP TABLE IF EXISTS form_notify_sender_configuration;
DROP TABLE IF EXISTS form_form_processor;
DROP TABLE IF EXISTS form_graph_type;
DROP TABLE IF EXISTS form_export_format;
DROP TABLE IF EXISTS form_entry_type;
DROP TABLE IF EXISTS form_default_message;
DROP TABLE IF EXISTS form_action;
DROP TABLE IF EXISTS form_form_parameter;
DROP TABLE IF EXISTS form_entry_parameter;
DROP TABLE IF EXISTS form_rss_cf;
DROP TABLE IF EXISTS form_category;
DROP TABLE IF EXISTS form_file;
DROP TABLE IF EXISTS form_physical_file;

--
-- Table structure for table form_action
--
CREATE TABLE form_action (
	id_action int default 0 NOT NULL,
	name_key varchar(100) default NULL,
	description_key varchar(100) default NULL,
	action_url varchar(255) default NULL,
	icon_url varchar(255) default NULL,
	action_permission varchar(255) default NULL,
	form_state smallint default NULL,
	PRIMARY KEY (id_action)
);

--
-- Table structure for table form_default_message
--
CREATE TABLE form_default_message (
	welcome_message long varchar,
	unavailability_message long varchar,
	requirement_message long varchar,
	recap_message long varchar,
	libelle_validate_button varchar(255),
	libelle_reset_button varchar(255),
	back_url long varchar
);

--
-- Table structure for table form_entry_type
--
CREATE TABLE form_entry_type (
	id_type int default 0 NOT NULL,
	title varchar(255),
	is_group smallint default NULL,
	is_comment int default NULL,
	is_mylutece_user smallint default NULL,
	class_name varchar(255),
	PRIMARY KEY (id_type)
);

--
-- Table structure for table form_export_format
--
CREATE TABLE form_export_format (
	id_export int default 0 NOT NULL,
	title varchar(255) default NULL,
	description varchar(255) default NULL,
	extension varchar(255) default NULL,
	xsl_file long varbinary,
	PRIMARY KEY (id_export)
);

--
-- Table structure for table form_graph_type
--
CREATE TABLE form_graph_type (
	id_graph_type int default 0 NOT NULL,
	title varchar(255) default NULL,
	class_name varchar(255),
	PRIMARY KEY (id_graph_type)
);

--
-- Table structure for table form_form_processor
--
CREATE TABLE form_form_processor (
	id_form int default 0 NOT NULL,
	key_processor varchar(255) default NULL,
	PRIMARY KEY (id_form,key_processor)
);

--
-- Table structure for table form_notify_sender_configuration
--
CREATE TABLE form_notify_sender_configuration (
	id_form int default 0 NOT NULL,
	id_entry_email_sender int default 0 NOT NULL,
	message long varchar,
	send_attachments smallint default 0 NOT NULL,
	PRIMARY KEY (id_form,id_entry_email_sender)
);

--
-- Table structure for table form_recap
--
CREATE TABLE form_recap (
	id_recap int default 0 NOT NULL,
	back_url long varchar,
	id_graph_type int default NULL,
	recap_message long varchar,
	recap_data smallint default NULL,
	graph smallint default NULL,
	graph_three_dimension smallint default NULL,
	graph_legende smallint default NULL,
	graph_value_legende varchar(255),
	graph_label smallint default NULL,
	PRIMARY KEY (id_recap)
);

CREATE INDEX index_form_recap_graph_type ON form_recap (id_graph_type);

ALTER TABLE form_recap ADD CONSTRAINT fk_form_recap_graph_type FOREIGN KEY (id_graph_type)
	REFERENCES form_graph_type (id_graph_type);

--
-- Table structure for table form_category
--
CREATE TABLE form_category (
	id_category int NOT NULL,
	title varchar(100) NOT NULL,
	color varchar(10),
	PRIMARY KEY (id_category)
);

--
-- Table structure for table form_form
--
CREATE TABLE form_form (
	id_form int default 0 NOT NULL,
	title varchar(255) NOT NULL,
	description long varchar NOT NULL,
	welcome_message long varchar,
	unavailability_message long varchar,
	requirement_message long varchar,
	workgroup varchar(255),
	id_mailing_list int default NULL,
	active_captcha smallint default NULL,
	active_store_adresse smallint default NULL,
	libelle_validate_button varchar(255),
	libelle_reset_button varchar(255),
	date_begin_disponibility date NULL,
	date_end_disponibility date NULL,
	active smallint default NULL,
	auto_publication smallint default NULL,
	date_creation timestamp NULL,
	limit_number_response smallint default NULL,
	id_recap int default NULL,
	active_requirement smallint default NULL,
	information_1 varchar(255) default NULL,
	information_2 varchar(255) default NULL,
	information_3 varchar(255) default NULL,
	information_4 varchar(255) default NULL,
	information_5 varchar(255) default NULL,
	supports_https smallint default 0,
	code_theme varchar(25)default NULL,
	active_mylutece_authentification smallint default NULL,
	id_category int default NULL,
	front_office_title varchar(255) default NULL,
	is_shown_front_office_title smallint default 0,
	PRIMARY KEY (id_form)
);

CREATE INDEX index_form_form_recap ON form_form (id_recap);

ALTER TABLE form_form ADD CONSTRAINT fk_form_form_recap FOREIGN KEY (id_recap)
	REFERENCES form_recap (id_recap);
ALTER TABLE form_form ADD CONSTRAINT fk_form_form_category FOREIGN KEY (id_category)
	REFERENCES form_category (id_category);

--
-- Table structure for table form_entry
--
CREATE TABLE form_entry (
	id_entry int default 0 NOT NULL,
	id_form int default 0 NOT NULL,
	id_type int default 0 NOT NULL,
	id_parent int default NULL,
	title long varchar,
	help_message long varchar,
	comment long varchar,
	mandatory smallint default NULL,
	fields_in_line smallint default NULL,
	pos int default NULL,
	id_field_depend int default NULL,
	confirm_field smallint default NULL,
	confirm_field_title long varchar,
	field_unique smallint default NULL,
	map_provider varchar(45) default NULL,
	css_class varchar(255) default NULL,
	pos_conditional int default 0,
	PRIMARY KEY (id_entry)
);

CREATE INDEX index_form_entry_form ON form_entry (id_form);
CREATE INDEX index_form_entry_parent ON form_entry (id_parent);

ALTER TABLE form_entry ADD CONSTRAINT fk_form_entry_form FOREIGN KEY (id_form)
	REFERENCES form_form (id_form);
ALTER TABLE form_entry ADD CONSTRAINT fk_form_entry_type FOREIGN KEY (id_type)
	REFERENCES form_entry_type (id_type);

--
-- Table structure for table form_field
--
CREATE TABLE form_field (
	id_field int default 0 NOT NULL,
	id_entry int default 0 NOT NULL,
	title varchar(255),
	value long varchar,
	height int default NULL,
	width int default NULL,
	default_value smallint default NULL,
	max_size_enter int default NULL,
	pos int default NULL,
	value_type_date date NULL,
	no_display_title smallint default NULL,
	PRIMARY KEY (id_field)
);

CREATE INDEX index_form_field_entry ON form_field (id_entry);

ALTER TABLE form_field ADD CONSTRAINT fk_form_field_entry FOREIGN KEY (id_entry)
	REFERENCES form_entry (id_entry);

--
-- Table structure for table form_submit
--
CREATE TABLE form_submit (
	id_form_submit int default 0 NOT NULL,
	date_response timestamp NULL,
	day_date_response smallint default NULL,
	week_date_response smallint default NULL,
	month_date_response smallint default NULL,
	year_date_response smallint default NULL,
	ip varchar(100),
	id_form int default NULL,
	PRIMARY KEY (id_form_submit)
);

CREATE INDEX index_form_submit_form ON form_submit (id_form);

ALTER TABLE form_submit ADD CONSTRAINT fk_form_submit_form FOREIGN KEY (id_form)
	REFERENCES form_form (id_form);

--
-- Table structure for table form_response
--
CREATE TABLE form_response (
	id_response int default 0 NOT NULL,
	id_form_submit int default NULL,
	response_value long VARCHAR DEFAULT NULL,
	id_entry int default NULL,
	id_field int default NULL,
	id_file int default NULL,
	PRIMARY KEY (id_response)
);

CREATE INDEX index_form_response_entry ON form_response (id_entry);
CREATE INDEX index_form_response_submit ON form_response (id_form_submit);

ALTER TABLE form_response ADD CONSTRAINT fk_form_response_entry FOREIGN KEY (id_entry)
	REFERENCES form_entry (id_entry);
ALTER TABLE form_response ADD CONSTRAINT fk_form_response_submit FOREIGN KEY (id_form_submit)
	REFERENCES form_submit (id_form_submit);

--
-- Table structure for table form_verify_by
--
CREATE TABLE form_verify_by (
	id_field int default 0 NOT NULL,
	id_expression int default 0 NOT NULL,
	PRIMARY KEY (id_field,id_expression)
);

CREATE INDEX index_form_verify_by_field ON form_verify_by (id_field);

ALTER TABLE form_verify_by ADD CONSTRAINT fk_form_verify_by_field FOREIGN KEY (id_field)
	REFERENCES form_field (id_field);

--
-- Table structure for table form_form_parameter
--
CREATE TABLE form_form_parameter (
	parameter_key varchar(100) NOT NULL,
	parameter_value varchar(100),
	PRIMARY KEY (parameter_key)
);

--
-- Table structure for table form_entry_parameter
--
CREATE TABLE form_entry_parameter (
	parameter_key varchar(100) NOT NULL,
	parameter_value varchar(100),
	PRIMARY KEY (parameter_key)
);

--
-- Table structure for table form_rss_cf
--
CREATE TABLE form_rss_cf (
	id_rss int default 0 NOT NULL,
	id_form int default 0 NOT NULL,
	is_submit_rss smallint default 0 NOT NULL,	
	id_form_submit int default 0 NOT NULL,
	PRIMARY KEY (id_rss)
);

--
-- Table structure for table form_file
--
CREATE TABLE form_file (
  id_file INT DEFAULT 0 NOT NULL,
  title LONG VARCHAR DEFAULT NULL,
  id_physical_file INT DEFAULT NULL,
  file_size  INT DEFAULT NULL,
  mime_type VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id_file)
);

--
-- Table structure for table form_physical_file
--
CREATE TABLE form_physical_file (
  id_physical_file INT DEFAULT 0 NOT NULL,
  file_value LONG VARBINARY,  
  PRIMARY KEY (id_physical_file)
);
