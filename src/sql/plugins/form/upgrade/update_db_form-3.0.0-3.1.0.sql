CREATE TABLE form_anonymize_fields (
	id_form int default 0 NOT NULL,
	id_entry int default 0 NOT NULL,
	PRIMARY KEY (id_form,id_entry)
);

ALTER TABLE form_form ADD COLUMN automatic_cleaning SMALLINT default 0;
ALTER TABLE form_form ADD COLUMN cleaning_by_removal SMALLINT default 0;
ALTER TABLE form_form ADD COLUMN nb_days_before_cleaning INT default 0;

ALTER TABLE form_response ADD COLUMN status smallint default 1;

ALTER TABLE form_field ADD COLUMN comment long varchar default null;
ALTER TABLE form_entry ADD COLUMN error_message long varchar default NULL;