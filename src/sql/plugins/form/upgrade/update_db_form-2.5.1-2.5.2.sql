--
-- FORM-163 : Add a new EntryTypeNumbering
--
INSERT INTO form_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name) VALUES
(15,'Numérotation',0,0,0,'fr.paris.lutece.plugins.form.business.EntryTypeNumbering');

--
-- FORM-165 : Add the possibility to have one file per form submit when exporting a form in the daemon formExportResponses
--
INSERT INTO form_form_parameter (parameter_key, parameter_value) VALUES ('file_export_daemon_type', 'form');

--
-- FORM-168 : Add the possibility to add CSS classes to the entries
--
ALTER TABLE form_entry ADD COLUMN css_class varchar(255) default NULL;
