insert into form_action(id_action,name_key,description_key,action_url,icon_url,action_permission,form_state) values (14,'form.action.manageOutputProcessor.name','form.action.manageOutputProcessor.description','jsp/admin/plugins/form/ManageOutputProcessor.jsp','images/admin/skin/plugins/form/actions/processor.png','MANAGE_OUTPUT_PROCESSOR',1),(15,'form.action.manageOutputProcessor.name','form.action.manageOutputProcessor.description','jsp/admin/plugins/form/ManageOutputProcessor.jsp','images/admin/skin/plugins/form/actions/processor.png','MANAGE_OUTPUT_PROCESSOR',0);

CREATE TABLE form_form_processor (
	id_form int default 0 NOT NULL,
	key_processor varchar(255) default NULL,
	PRIMARY KEY (id_form,key_processor)
);

CREATE TABLE form_notify_sender_configuration (
	id_form int default 0 NOT NULL,
	id_entry_email_sender int default 0 NOT NULL,
	message long varchar,
	PRIMARY KEY (id_form,id_entry_email_sender)
);

