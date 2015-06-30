CREATE TABLE form_response_submit (
	id_response int default 0 NOT NULL,
	id_form_submit int default 0 NOT NULL,
	PRIMARY KEY (id_response,id_form_submit)
);

ALTER TABLE form_entry  DROP FOREIGN KEY fk_form_entry_form;

INSERT INTO form_response_submit (id_response, id_form_submit) SELECT id_response, id_form_submit FROM form_response;

ALTER TABLE form_response DROP COLUMN id_form_submit;


ALTER TABLE form_entry CHANGE id_form id_resource int NOT NULL;
ALTER TABLE form_entry ADD COLUMN resource_type VARCHAR(255) NOT NULL;
UPDATE form_entry SET resource_type = 'FORM_FORM_TYPE';

ALTER TABLE form_entry_type ADD COLUMN plugin varchar(255) NOT NULL;



UPDATE form_entry_type SET class_name = 'form.entryTypeRadioButton' WHERE class_name = 'fr.paris.lutece.plugins.form.business.EntryTypeRadioButton';
UPDATE form_entry_type SET class_name = 'form.entryTypeCheckBox' WHERE class_name = 'fr.paris.lutece.plugins.form.business.EntryTypeCheckBox';
UPDATE form_entry_type SET class_name = 'form.entryTypeComment' WHERE class_name = 'fr.paris.lutece.plugins.form.business.EntryTypeComment';
UPDATE form_entry_type SET class_name = 'form.entryTypeDate' WHERE class_name = 'fr.paris.lutece.plugins.form.business.EntryTypeDate';
UPDATE form_entry_type SET class_name = 'form.entryTypeSelect' WHERE class_name = 'fr.paris.lutece.plugins.form.business.EntryTypeSelect';
UPDATE form_entry_type SET class_name = 'form.entryTypeText' WHERE class_name = 'fr.paris.lutece.plugins.form.business.EntryTypeText';
UPDATE form_entry_type SET class_name = 'form.entryTypeTextArea' WHERE class_name = 'fr.paris.lutece.plugins.form.business.EntryTypeTextArea';
UPDATE form_entry_type SET class_name = 'form.entryTypeFile' WHERE class_name = 'fr.paris.lutece.plugins.form.business.EntryTypeFile';
UPDATE form_entry_type SET class_name = 'form.entryTypeGroup' WHERE class_name = 'fr.paris.lutece.plugins.form.business.EntryTypeGroup';
UPDATE form_entry_type SET class_name = 'form.entryTypeSelectSQL' WHERE class_name = 'fr.paris.lutece.plugins.form.business.EntryTypeSelectSQL';
UPDATE form_entry_type SET class_name = 'form.entryTypeGeolocation' WHERE class_name = 'fr.paris.lutece.plugins.form.business.EntryTypeGeolocation';
UPDATE form_entry_type SET class_name = 'form.entryTypeMyLuteceUser' WHERE class_name = 'fr.paris.lutece.plugins.form.business.EntryTypeMyLuteceUser';
UPDATE form_entry_type SET class_name = 'form.entryTypeMandatoryCheckBox' WHERE class_name = 'fr.paris.lutece.plugins.form.business.EntryTypeMandatoryCheckBox';
UPDATE form_entry_type SET class_name = 'form.entryTypeImage' WHERE class_name = 'fr.paris.lutece.plugins.form.business.EntryTypeImage';
UPDATE form_entry_type SET class_name = 'form.entryTypeNumbering' WHERE class_name = 'fr.paris.lutece.plugins.form.business.EntryTypeNumbering';
UPDATE form_entry_type SET class_name = 'form.entryTypeSession' WHERE class_name = 'fr.paris.lutece.plugins.form.business.EntryTypeSession';

ALTER TABLE form_entry_type RENAME TO genatt_entry_type;
ALTER TABLE form_entry RENAME TO genatt_entry;
ALTER TABLE form_response RENAME TO genatt_response;
ALTER TABLE form_field RENAME TO genatt_field;
ALTER TABLE form_verify_by RENAME TO genatt_verify_by;
