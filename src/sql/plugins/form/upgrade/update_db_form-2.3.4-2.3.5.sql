ALTER TABLE form_form ADD COLUMN code_theme varchar(25)default NULL;

-- geolocation
ALTER TABLE form_entry ADD COLUMN map_provider varchar(45) default NULL;
INSERT INTO form_entry_type (id_type,title,is_group,is_comment,class_name) VALUES
(11,'Géolocalisation',false,false,'fr.paris.lutece.plugins.form.business.EntryTypeGeolocation');
