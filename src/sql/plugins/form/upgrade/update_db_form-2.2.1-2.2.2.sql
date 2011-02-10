ALTER TABLE form_form ADD date_begin_disponibility date NULL;
ALTER TABLE form_form ADD auto_publication smallint default NULL;
ALTER TABLE form_form ADD libelle_reset_button varchar(255);
UPDATE form_form SET auto_publication = 0 WHERE date_begin_disponibility IS NULL AND date_end_disponibility IS NULL;
UPDATE form_form SET auto_publication = 1 WHERE date_begin_disponibility IS NOT NULL OR date_end_disponibility IS NOT NULL;
UPDATE form_form SET libelle_reset_button = 'Réinitialiser';

ALTER TABLE form_entry ADD confirm_field smallint default NULL;
ALTER TABLE form_entry ADD confirm_field_title long varchar;
ALTER TABLE form_entry ADD field_unique smallint default NULL;

DELETE FROM form_action;

INSERT INTO form_action (id_action,name_key,description_key,action_url,icon_url,action_permission,form_state) VALUES 
(1,'form.action.modify.name','form.action.modify.description','jsp/admin/plugins/form/ModifyForm.jsp','images/admin/skin/plugins/form/actions/modify.png','MODIFY',0);
INSERT INTO form_action (id_action,name_key,description_key,action_url,icon_url,action_permission,form_state) VALUES 
(2,'form.action.modify.name','form.action.modify.description','jsp/admin/plugins/form/ModifyForm.jsp','images/admin/skin/plugins/form/actions/modify.png','MODIFY',1);
INSERT INTO form_action (id_action,name_key,description_key,action_url,icon_url,action_permission,form_state) VALUES 
(3,'form.action.viewRecap.name','form.action.viewRecap.description','jsp/admin/plugins/form/ModifyRecap.jsp','images/admin/skin/plugins/form/actions/recap.png','MODIFY',0);
INSERT INTO form_action (id_action,name_key,description_key,action_url,icon_url,action_permission,form_state) VALUES 
(4,'form.action.viewRecap.name','form.action.viewRecap.description','jsp/admin/plugins/form/ModifyRecap.jsp','images/admin/skin/plugins/form/actions/recap.png','MODIFY',1);
INSERT INTO form_action (id_action,name_key,description_key,action_url,icon_url,action_permission,form_state) VALUES 
(5,'form.action.modifyMessage.name','form.action.modifyMessage.description','jsp/admin/plugins/form/ModifyMessage.jsp','images/admin/skin/plugins/form/actions/message.png','MODIFY',0);
INSERT INTO form_action (id_action,name_key,description_key,action_url,icon_url,action_permission,form_state) VALUES 
(6,'form.action.modifyMessage.name','form.action.modifyMessage.description','jsp/admin/plugins/form/ModifyMessage.jsp','images/admin/skin/plugins/form/actions/message.png','MODIFY',1);
INSERT INTO form_action (id_action,name_key,description_key,action_url,icon_url,action_permission,form_state) VALUES 
(7,'form.action.viewResult.name','form.action.viewResult.description','jsp/admin/plugins/form/Result.jsp','images/admin/skin/plugins/form/actions/response.png','VIEW_RESULT',0);
INSERT INTO form_action (id_action,name_key,description_key,action_url,icon_url,action_permission,form_state) VALUES 
(8,'form.action.viewResult.name','form.action.viewResult.description','jsp/admin/plugins/form/Result.jsp','images/admin/skin/plugins/form/actions/response.png','VIEW_RESULT',1);
INSERT INTO form_action (id_action,name_key,description_key,action_url,icon_url,action_permission,form_state) VALUES 
(9,'form.action.test.name','form.action.test.description','jsp/admin/plugins/form/TestForm.jsp','images/admin/skin/plugins/form/actions/test.png','TEST',0);
INSERT INTO form_action (id_action,name_key,description_key,action_url,icon_url,action_permission,form_state) VALUES 
(10,'form.action.test.name','form.action.test.description','jsp/admin/plugins/form/TestForm.jsp','images/admin/skin/plugins/form/actions/test.png','TEST',1);
INSERT INTO form_action (id_action,name_key,description_key,action_url,icon_url,action_permission,form_state) VALUES 
(11,'form.action.disable.name','form.action.disable.description','jsp/admin/plugins/form/ConfirmDisableForm.jsp','images/admin/skin/plugins/form/actions/disable.png','CHANGE_STATE',1);
INSERT INTO form_action (id_action,name_key,description_key,action_url,icon_url,action_permission,form_state) VALUES 
(12,'form.action.enable.name','form.action.enable.description','jsp/admin/plugins/form/DoEnableForm.jsp','images/admin/skin/plugins/form/actions/enable.png','CHANGE_STATE',0);
INSERT INTO form_action (id_action,name_key,description_key,action_url,icon_url,action_permission,form_state) VALUES 
(13,'form.action.copy.name','form.action.copy.description','jsp/admin/plugins/form/DoCopyForm.jsp','images/admin/skin/plugins/form/actions/editcopy.png','COPY',0);
INSERT INTO form_action (id_action,name_key,description_key,action_url,icon_url,action_permission,form_state) VALUES 
(14,'form.action.copy.name','form.action.copy.description','jsp/admin/plugins/form/DoCopyForm.jsp','images/admin/skin/plugins/form/actions/editcopy.png','COPY',1);
INSERT INTO form_action (id_action,name_key,description_key,action_url,icon_url,action_permission,form_state) VALUES 
(15,'form.action.manageOutputProcessor.name','form.action.manageOutputProcessor.description','jsp/admin/plugins/form/ManageOutputProcessor.jsp','images/admin/skin/plugins/form/actions/processor.png','MANAGE_OUTPUT_PROCESSOR',1);	
INSERT INTO form_action (id_action,name_key,description_key,action_url,icon_url,action_permission,form_state) VALUES 
(16,'form.action.manageOutputProcessor.name','form.action.manageOutputProcessor.description','jsp/admin/plugins/form/ManageOutputProcessor.jsp','images/admin/skin/plugins/form/actions/processor.png','MANAGE_OUTPUT_PROCESSOR',0);
INSERT INTO form_action (id_action,name_key,description_key,action_url,icon_url,action_permission,form_state) VALUES 
(17,'form.action.delete.name','form.action.delete.description','jsp/admin/plugins/form/ConfirmRemoveForm.jsp','images/admin/skin/plugins/form/actions/delete.png','DELETE',0);
INSERT INTO form_action (id_action,name_key,description_key,action_url,icon_url,action_permission,form_state) VALUES 
(18,'form.action.disable.name','form.action.disableAuto.description','jsp/admin/plugins/form/ConfirmDisableAutoForm.jsp','images/admin/skin/plugins/form/actions/pause_auto_publication.png','CHANGE_STATE_AUTO_PUBLICATION',1);
INSERT INTO form_action (id_action,name_key,description_key,action_url,icon_url,action_permission,form_state) VALUES 
(19,'form.action.enable.name','form.action.enableAuto.description','jsp/admin/plugins/form/DoEnableAutoForm.jsp','images/admin/skin/plugins/form/actions/enable_auto_publication.png','CHANGE_STATE_AUTO_PUBLICATION',0);

-- Need to update roles with CHANGE_STATE_AUTO_PUBLICATION ?

ALTER TABLE form_default_message ADD libelle_reset_button varchar(255);
UPDATE form_default_message SET libelle_reset_button = '';

ALTER TABLE form_form ADD COLUMN information_1 VARCHAR(255) DEFAULT null;
ALTER TABLE form_form ADD COLUMN information_2 VARCHAR(255) DEFAULT null;
ALTER TABLE form_form ADD COLUMN information_3 VARCHAR(255) DEFAULT null;
ALTER TABLE form_form ADD COLUMN information_4 VARCHAR(255) DEFAULT null;
ALTER TABLE form_form ADD COLUMN information_5 VARCHAR(255) DEFAULT null;

ALTER TABLE form_field ADD COLUMN no_display_title smallint default NULL;