CREATE TABLE form_response_submit (
	id_response int default 0 NOT NULL,
	id_form_submit int default 0 NOT NULL,
	PRIMARY KEY (id_response,id_form_submit)
);

ALTER TABLE form_entry DROP CONSTRAINT fk_form_entry_form;

INSERT INTO form_response_submit (id_response, id_form_submit) SELECT id_response, id_form_submit FROM form_response;

ALTER TABLE form_response DROP COLUMN id_form_submit;


ALTER TABLE form_entry CHANGE id_form id_resource int NOT NULL;
ALTER TABLE form_entry ADD COLUMN resource_type VARCHAR(255) NOT NULL;
UPDATE form_entry SET resource_type = 'FORM_FORM_TYPE';

ALTER TABLE form_entry_type ADD COLUMN plugin varchar(255) NOT NULL;
