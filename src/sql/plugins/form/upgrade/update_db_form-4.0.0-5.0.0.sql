-- Bootstrap 3 CSS Framework integration 
-- update actions icons

REPLACE INTO form_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES
	(1, 'form.action.modify.name', 'form.action.modify.description', 'jsp/admin/plugins/form/ModifyForm.jsp', 'glyphicon glyphicon-edit', 'MODIFY', 0),
	(2, 'form.action.modify.name', 'form.action.modify.description', 'jsp/admin/plugins/form/ModifyForm.jsp', 'glyphicon glyphicon-edit', 'MODIFY', 1),
	(3, 'form.action.viewRecap.name', 'form.action.viewRecap.description', 'jsp/admin/plugins/form/ModifyRecap.jsp', 'glyphicon glyphicon-list', 'MODIFY', 0),
	(4, 'form.action.viewRecap.name', 'form.action.viewRecap.description', 'jsp/admin/plugins/form/ModifyRecap.jsp', 'glyphicon glyphicon-list', 'MODIFY', 1),
	(5, 'form.action.modifyMessage.name', 'form.action.modifyMessage.description', 'jsp/admin/plugins/form/ModifyMessage.jsp', 'glyphicon glyphicon-comment', 'MODIFY', 0),
	(6, 'form.action.modifyMessage.name', 'form.action.modifyMessage.description', 'jsp/admin/plugins/form/ModifyMessage.jsp', 'glyphicon glyphicon-comment', 'MODIFY', 1),
	(7, 'form.action.viewResult.name', 'form.action.viewResult.description', 'jsp/admin/plugins/form/Result.jsp', 'glyphicon glyphicon-tasks', 'VIEW_RESULT', 0),
	(8, 'form.action.viewResult.name', 'form.action.viewResult.description', 'jsp/admin/plugins/form/Result.jsp', 'glyphicon glyphicon-tasks', 'VIEW_RESULT', 1),
	(9, 'form.action.test.name', 'form.action.test.description', 'jsp/admin/plugins/form/TestForm.jsp', 'glyphicon glyphicon-cog', 'TEST', 0),
	(10, 'form.action.test.name', 'form.action.test.description', 'jsp/admin/plugins/form/TestForm.jsp', 'glyphicon glyphicon-cog', 'TEST', 1),
	(11, 'form.action.disable.name', 'form.action.disable.description', 'jsp/admin/plugins/form/ConfirmDisableForm.jsp', 'glyphicon glyphicon-remove', 'CHANGE_STATE', 1),
	(12, 'form.action.enable.name', 'form.action.enable.description', 'jsp/admin/plugins/form/DoEnableForm.jsp', 'glyphicon glyphicon-ok', 'CHANGE_STATE', 0),
	(13, 'form.action.copy.name', 'form.action.copy.description', 'jsp/admin/plugins/form/DoCopyForm.jsp', 'glyphicon glyphicon-move', 'COPY', 0),
	(14, 'form.action.copy.name', 'form.action.copy.description', 'jsp/admin/plugins/form/DoCopyForm.jsp', 'glyphicon glyphicon-move', 'COPY', 1),
	(15, 'form.action.manageOutputProcessor.name', 'form.action.manageOutputProcessor.description', 'jsp/admin/plugins/form/ManageOutputProcessor.jsp', 'glyphicon glyphicon-wrench', 'MANAGE_OUTPUT_PROCESSOR', 1),
	(16, 'form.action.manageOuhelpdesk_faqtputProcessor.name', 'form.action.manageOutputProcessor.description', 'jsp/admin/plugins/form/ManageOutputProcessor.jsp', 'glyphicon glyphicon-wrench', 'MANAGE_OUTPUT_PROCESSOR', 0),
	(17, 'form.action.delete.name', 'form.action.delete.description', 'jsp/admin/plugins/form/ConfirmRemoveForm.jsp', 'glyphicon glyphicon-trash', 'DELETE', 0),
	(18, 'form.action.disable.name', 'form.action.disableAuto.description', 'jsp/admin/plugins/form/ConfirmDisableAutoForm.jsp', 'glyphicon glyphicon-ok', 'CHANGE_STATE_AUTO_PUBLICATION', 1),
	(19, 'form.action.enable.name', 'form.action.enableAuto.description', 'jsp/admin/plugins/form/DoEnableAutoForm.jsp', 'glyphicon glyphicon-ok', 'CHANGE_STATE_AUTO_PUBLICATION', 0),
	(20, 'form.action.manageValidator.name', 'form.action.manageValidator.description', 'jsp/admin/plugins/form/ManageValidator.jsp', 'glyphicon glyphicon-wrench', 'MANAGE_VALIDATOR', 1),
	(21, 'form.action.manageValidator.name', 'form.action.manageValidator.description', 'jsp/admin/plugins/form/ManageValidator.jsp', 'glyphicon glyphicon-wrench', 'MANAGE_VALIDATOR', 0);
