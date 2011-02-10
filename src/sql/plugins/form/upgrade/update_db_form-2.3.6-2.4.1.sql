--
-- Update table form_entry_type 
--
ALTER TABLE form_form ADD COLUMN active_mylutece_authentification smallint default NULL;

--
-- Update table form_entry_type 
--
ALTER TABLE form_entry_type ADD COLUMN is_mylutece_user smallint default NULL AFTER is_comment;

--
-- Dumping data in table form_entry_type
--
INSERT INTO form_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name) VALUES
(13,'Utilisateur MyLutece',false,false,true,'fr.paris.lutece.plugins.form.business.EntryTypeMyLuteceUser');

--
-- Update table form_entry_type
--
UPDATE form_entry_type SET is_mylutece_user = false WHERE id_type = 1;
UPDATE form_entry_type SET is_mylutece_user = false WHERE id_type = 2;
UPDATE form_entry_type SET is_mylutece_user = false WHERE id_type = 3;
UPDATE form_entry_type SET is_mylutece_user = false WHERE id_type = 4;
UPDATE form_entry_type SET is_mylutece_user = false WHERE id_type = 5;
UPDATE form_entry_type SET is_mylutece_user = false WHERE id_type = 6;
UPDATE form_entry_type SET is_mylutece_user = false WHERE id_type = 7;
UPDATE form_entry_type SET is_mylutece_user = false WHERE id_type = 8;
UPDATE form_entry_type SET is_mylutece_user = false WHERE id_type = 9;
UPDATE form_entry_type SET is_mylutece_user = false WHERE id_type = 10;
UPDATE form_entry_type SET is_mylutece_user = false WHERE id_type = 11;
UPDATE form_entry_type SET is_mylutece_user = false WHERE id_type = 12;
