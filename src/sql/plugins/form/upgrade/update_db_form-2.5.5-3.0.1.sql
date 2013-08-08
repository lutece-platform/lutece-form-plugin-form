-- LUTECE-1493 : Bootstrap CSS Framework integration 
-- update actions icons

REPLACE INTO form_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (1,'form.action.modify.name','form.action.modify.description','jsp/admin/plugins/form/ModifyForm.jsp','icon-edit','MODIFY',0);
REPLACE INTO form_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (2,'form.action.modify.name','form.action.modify.description','jsp/admin/plugins/form/ModifyForm.jsp','icon-edit','MODIFY',1);
REPLACE INTO form_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (3,'form.action.viewRecap.name','form.action.viewRecap.description','jsp/admin/plugins/form/ModifyRecap.jsp','icon-list','MODIFY',0);
REPLACE INTO form_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (4,'form.action.viewRecap.name','form.action.viewRecap.description','jsp/admin/plugins/form/ModifyRecap.jsp','icon-list','MODIFY',1);
REPLACE INTO form_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (5,'form.action.modifyMessage.name','form.action.modifyMessage.description','jsp/admin/plugins/form/ModifyMessage.jsp','icon-comment','MODIFY',0);
REPLACE INTO form_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (6,'form.action.modifyMessage.name','form.action.modifyMessage.description','jsp/admin/plugins/form/ModifyMessage.jsp','icon-comment','MODIFY',1);
REPLACE INTO form_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (7,'form.action.viewResult.name','form.action.viewResult.description','jsp/admin/plugins/form/Result.jsp','icon-tasks','VIEW_RESULT',0);
REPLACE INTO form_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (8,'form.action.viewResult.name','form.action.viewResult.description','jsp/admin/plugins/form/Result.jsp','icon-tasks','VIEW_RESULT',1);
REPLACE INTO form_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (9,'form.action.test.name','form.action.test.description','jsp/admin/plugins/form/TestForm.jsp','icon-cog','TEST',0);
REPLACE INTO form_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (10,'form.action.test.name','form.action.test.description','jsp/admin/plugins/form/TestForm.jsp','icon-cog','TEST',1);
REPLACE INTO form_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (11,'form.action.disable.name','form.action.disable.description','jsp/admin/plugins/form/ConfirmDisableForm.jsp','icon-remove','CHANGE_STATE',1);
REPLACE INTO form_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (12,'form.action.enable.name','form.action.enable.description','jsp/admin/plugins/form/DoEnableForm.jsp','icon-ok','CHANGE_STATE',0);
REPLACE INTO form_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (13,'form.action.copy.name','form.action.copy.description','jsp/admin/plugins/form/DoCopyForm.jsp','icon-plus-sign','COPY',0);
REPLACE INTO form_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (14,'form.action.copy.name','form.action.copy.description','jsp/admin/plugins/form/DoCopyForm.jsp','icon-plus-sign','COPY',1);
REPLACE INTO form_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (15,'form.action.manageOutputProcessor.name','form.action.manageOutputProcessor.description','jsp/admin/plugins/form/ManageOutputProcessor.jsp','icon-wrench','MANAGE_OUTPUT_PROCESSOR',1);
REPLACE INTO form_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (16,'form.action.manageOutputProcessor.name','form.action.manageOutputProcessor.description','jsp/admin/plugins/form/ManageOutputProcessor.jsp','icon-wrench','MANAGE_OUTPUT_PROCESSOR',0);
REPLACE INTO form_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (17,'form.action.delete.name','form.action.delete.description','jsp/admin/plugins/form/ConfirmRemoveForm.jsp','icon-trash icon-white','DELETE',0);
REPLACE INTO form_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (18,'form.action.disable.name','form.action.disableAuto.description','jsp/admin/plugins/form/ConfirmDisableAutoForm.jsp','icon-ok','CHANGE_STATE_AUTO_PUBLICATION',1);
REPLACE INTO form_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (19,'form.action.enable.name','form.action.enableAuto.description','jsp/admin/plugins/form/DoEnableAutoForm.jsp','icon-ok','CHANGE_STATE_AUTO_PUBLICATION',0);
REPLACE INTO form_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (20,'form.action.manageValidator.name','form.action.manageValidator.description','jsp/admin/plugins/form/ManageValidator.jsp','icon-check','MANAGE_VALIDATOR',1);
REPLACE INTO form_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (21,'form.action.manageValidator.name','form.action.manageValidator.description','jsp/admin/plugins/form/ManageValidator.jsp','icon-check','MANAGE_VALIDATOR',0);

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
