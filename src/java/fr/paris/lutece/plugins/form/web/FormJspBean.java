/*
 * Copyright (c) 2002-2012, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.form.web;

import fr.paris.lutece.plugins.form.business.Category;
import fr.paris.lutece.plugins.form.business.CategoryHome;
import fr.paris.lutece.plugins.form.business.DefaultMessage;
import fr.paris.lutece.plugins.form.business.DefaultMessageHome;
import fr.paris.lutece.plugins.form.business.Entry;
import fr.paris.lutece.plugins.form.business.EntryFilter;
import fr.paris.lutece.plugins.form.business.EntryHome;
import fr.paris.lutece.plugins.form.business.EntryType;
import fr.paris.lutece.plugins.form.business.EntryTypeHome;
import fr.paris.lutece.plugins.form.business.ExportFormat;
import fr.paris.lutece.plugins.form.business.ExportFormatHome;
import fr.paris.lutece.plugins.form.business.Field;
import fr.paris.lutece.plugins.form.business.FieldHome;
import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormAction;
import fr.paris.lutece.plugins.form.business.FormActionHome;
import fr.paris.lutece.plugins.form.business.FormError;
import fr.paris.lutece.plugins.form.business.FormFilter;
import fr.paris.lutece.plugins.form.business.FormHome;
import fr.paris.lutece.plugins.form.business.FormSubmit;
import fr.paris.lutece.plugins.form.business.FormSubmitHome;
import fr.paris.lutece.plugins.form.business.GraphType;
import fr.paris.lutece.plugins.form.business.GraphTypeHome;
import fr.paris.lutece.plugins.form.business.IEntry;
import fr.paris.lutece.plugins.form.business.Recap;
import fr.paris.lutece.plugins.form.business.RecapHome;
import fr.paris.lutece.plugins.form.business.Response;
import fr.paris.lutece.plugins.form.business.ResponseFilter;
import fr.paris.lutece.plugins.form.business.StatisticFormSubmit;
import fr.paris.lutece.plugins.form.business.outputprocessor.IOutputProcessor;
import fr.paris.lutece.plugins.form.business.portlet.FormPortletHome;
import fr.paris.lutece.plugins.form.service.EntryRemovalListenerService;
import fr.paris.lutece.plugins.form.service.FormRemovalListenerService;
import fr.paris.lutece.plugins.form.service.FormResourceIdService;
import fr.paris.lutece.plugins.form.service.FormService;
import fr.paris.lutece.plugins.form.service.IResponseService;
import fr.paris.lutece.plugins.form.service.OutputProcessorService;
import fr.paris.lutece.plugins.form.service.parameter.EntryParameterService;
import fr.paris.lutece.plugins.form.service.parameter.FormParameterService;
import fr.paris.lutece.plugins.form.service.validator.IValidator;
import fr.paris.lutece.plugins.form.service.validator.ValidatorService;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.style.Theme;
import fr.paris.lutece.portal.business.style.ThemeHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.captcha.CaptchaSecurityService;
import fr.paris.lutece.portal.service.html.XmlTransformerService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppHTTPSService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.UniqueIDGenerator;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.util.xml.XmlUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;

import com.keypoint.PngEncoder;


/**
 * This class provides the user interface to manage form features ( manage, create, modify, remove)
 */
public class FormJspBean extends PluginAdminPageJspBean
{
    public static final String RIGHT_MANAGE_FORM = "FORM_MANAGEMENT";

    // templates
    private static final String TEMPLATE_MANAGE_FORM = "admin/plugins/form/manage_form.html";
    private static final String TEMPLATE_MANAGE_OUTPUT_PROCESSOR = "admin/plugins/form/manage_output_processor.html";
    private static final String TEMPLATE_CREATE_FORM = "admin/plugins/form/create_form.html";
    private static final String TEMPLATE_CREATE_FIELD = "admin/plugins/form/create_field.html";
    private static final String TEMPLATE_MODIFY_FORM = "admin/plugins/form/modify_form.html";
    private static final String TEMPLATE_HTML_TEST_FORM = "admin/plugins/form/test_form.html";
    private static final String TEMPLATE_MODIFY_RECAP = "admin/plugins/form/modify_recap.html";
    private static final String TEMPLATE_MOVE_ENTRY = "admin/plugins/form/move_entry.html";
    private static final String TEMPLATE_MODIFY_FIELD_WITH_CONDITIONAL_QUESTION = "admin/plugins/form/modify_field_with_conditional_question.html";
    private static final String TEMPLATE_MODIFY_FIELD = "admin/plugins/form/modify_field.html";
    private static final String TEMPLATE_RESULT = "admin/plugins/form/result.html";
    private static final String TEMPLATE_MODIFY_MESSAGE = "admin/plugins/form/modify_message.html";
    private static final String TEMPLATE_MANAGE_VALIDATOR = "admin/plugins/form/manage_validator.html";
    private static final String TEMPLATE_MANAGE_ADVANCED_PARAMETERS = "admin/plugins/form/manage_advanced_parameters.html";

    // message
    private static final String MESSAGE_CONFIRM_REMOVE_FORM = "form.message.confirmRemoveForm";
    private static final String MESSAGE_CONFIRM_REMOVE_FORM_WITH_FORM_SUBMIT = "form.message.confirmRemoveFormWithFormSubmit";
    private static final String MESSAGE_CONFIRM_REMOVE_FORM_WITH_VALIDATOR = "form.message.confirmRemoveFormWithValidator";
    private static final String MESSAGE_CANT_REMOVE_FORM_ASSOCIATE_PORTLET = "form.message.cantRemoveFormAssociatePortlet";
    private static final String MESSAGE_CANT_REMOVE_FORM = "form.message.cantRemoveForm";
    private static final String MESSAGE_CANT_REMOVE_ENTRY = "form.message.cantRemoveEntry";
    private static final String MESSAGE_CONFIRM_DISABLE_FORM = "form.message.confirmDisableForm";
    private static final String MESSAGE_CONFIRM_DISABLE_FORM_WITH_PORTLET = "form.message.confirmDisableFormWithPortlet";
    private static final String MESSAGE_CONFIRM_REMOVE_ENTRY = "form.message.confirmRemoveEntry";
    private static final String MESSAGE_CONFIRM_REMOVE_FIELD = "form.message.confirmRemoveField";
    private static final String MESSAGE_CONFIRM_REMOVE_GROUP_WITH_ANY_ENTRY = "form.message.confirmRemoveGroupWithAnyEntry";
    private static final String MESSAGE_CONFIRM_REMOVE_GROUP_WITH_ENTRY = "form.message.confirmRemoveGroupWhithEntry";
    private static final String MESSAGE_MANDATORY_FIELD = "form.message.mandatory.field";
    private static final String MESSAGE_FIELD_VALUE_FIELD = "directory.message.field_value_field";
    private static final String MESSAGE_MANDATORY_QUESTION = "form.message.mandatory.question";
    private static final String MESSAGE_CAPTCHA_ERROR = "form.message.captchaError";
    private static final String MESSAGE_REQUIREMENT_ERROR = "form.message.requirementError";
    private static final String MESSAGE_NO_RESPONSE = "form.message.noResponse";
    private static final String MESSAGE_FORM_ERROR = "form.message.formError";
    private static final String MESSAGE_ILLOGICAL_DATE_BEGIN_DISPONIBILITY = "form.message.illogicalDateBeginDisponibility";
    private static final String MESSAGE_ILLOGICAL_DATE_END_DISPONIBILITY = "form.message.illogicalDateEndDisponibility";
    private static final String MESSAGE_DATE_END_DISPONIBILITY_BEFORE_CURRENT_DATE = "form.message.dateEndDisponibilityBeforeCurrentDate";
    private static final String MESSAGE_DATE_END_DISPONIBILITY_BEFORE_DATE_BEGIN = "form.message.dateEndDisponibilityBeforeDateBegin";
    private static final String MESSAGE_CANT_ENABLE_FORM_DATE_END_DISPONIBILITY_BEFORE_CURRENT_DATE = "form.message.cantEnableFormDateEndDisponibilityBeforeCurrentDate";
    private static final String MESSAGE_SELECT_GROUP = "form.message.selectGroup";
    private static final String MESSAGE_ERROR_DURING_DOWNLOAD_FILE = "form.message.errorDuringDownloadFile";
    private static final String MESSAGE_YOU_ARE_NOT_ALLOWED_TO_DOWLOAD_THIS_FILE = "form.message.youAreNotAllowedToDownloadFile";
    private static final String MESSAGE_ERROR_EXPORT_ENCODING_NOT_SUPPORTED = "form.message.error.export.encoding.not_supported";
    private static final String FIELD_TITLE = "form.createForm.labelTitle";
    private static final String FIELD_DESCRIPTION = "form.createForm.labelDescription";
    private static final String FIELD_LIBELE_VALIDATE_BUTTON = "form.createForm.labelLibelleValidateButton";
    private static final String FIELD_TITLE_FIELD = "form.createField.labelTitle";
    private static final String FIELD_VALUE_FIELD = "directory.create_field.label_value";
    private static final String FIELD_BACK_URL = "form.modifyRecap.labelBackUrl";
    private static final String FIELD_RECAP_MESSAGE = "form.modifyRecap.labelRecapMessage";
    private static final String FIELD_UNAVAILABILITY_MESSAGE = "form.createForm.labelUnavailabilityMessage";
    private static final String FIELD_REQUIREMENT = "form.createForm.labelRequirement";

    // properties
    private static final String PROPERTY_ITEM_PER_PAGE = "form.itemsPerPage";
    private static final String PROPERTY_ALL = "form.manageForm.select.all";
    private static final String PROPERTY_YES = "form.manageForm.select.yes";
    private static final String PROPERTY_NO = "form.manageForm.select.no";
    private static final String PROPERTY_NOTHING = "form.createForm.select.nothing";
    private static final String PROPERTY_MODIFY_FORM_TITLE = "form.modifyForm.title";
    private static final String PROPERTY_COPY_FORM_TITLE = "form.copyForm.title";
    private static final String PROPERTY_COPY_ENTRY_TITLE = "form.copyEntry.title";
    private static final String PROPERTY_CREATE_FORM_TITLE = "form.createForm.title";
    private static final String PROPERTY_CREATE_COMMENT_TITLE = "form.createEntry.titleComment";
    private static final String PROPERTY_CREATE_QUESTION_TITLE = "form.createEntry.titleQuestion";
    private static final String PROPERTY_MODIFY_COMMENT_TITLE = "form.modifyEntry.titleComment";
    private static final String PROPERTY_MODIFY_QUESTION_TITLE = "form.modifyEntry.titleQuestion";
    private static final String PROPERTY_MODIFY_GROUP_TITLE = "form.modifyEntry.titleGroup";
    private static final String PROPERTY_CREATE_FIELD_TITLE = "form.createField.title";
    private static final String PROPERTY_MODIFY_FIELD_TITLE = "form.modifyField.title";
    private static final String PROPERTY_MODIFY_RECAP_TITLE = "form.modifyRecap.title";
    private static final String PROPERTY_RESULT_PAGE_TITLE = "form.result.pageTitle";
    private static final String PROPERTY_LABEL_AXIS_X = "form.result.graph.labelAxisX";
    private static final String PROPERTY_LABEL_AXIS_Y = "form.result.graph.labelAxisY";
    private static final String PROPERTY_NUMBER_RESPONSE_AXIS_X = "graph.numberResponseAxisX";
    private static final String XSL_UNIQUE_PREFIX_ID = UniqueIDGenerator.getNewId(  ) + "form-";
    private static final String PROPERTY_MODIFY_MESSAGE_TITLE = "form.modifyMessage.title";
    private static final String PROPERTY_MANAGE_VALIDATOR_TITLE = "form.manageValidator.title";
    private static final String PROPERTY_MANAGE_OUTPUT_PROCESSOR_TITLE = "form.manageOutputProcessor.title";
    private static final String PROPERTY_NO_GROUP = "form.manageForm.noGroup";

    // Markers
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_USER_WORKGROUP_REF_LIST = "user_workgroup_list";
    private static final String MARK_USER_WORKGROUP_SELECTED = "user_workgroup_selected";
    private static final String MARK_ACTIVE_REF_LIST = "active_list";
    private static final String MARK_ACTIVE_SELECTED = "active_selected";
    private static final String MARK_MAILING_REF_LIST = "mailing_list";
    private static final String MARK_ENTRY_TYPE_REF_LIST = "entry_type_list";
    private static final String MARK_REGULAR_EXPRESSION_LIST_REF_LIST = "regular_expression_list";
    private static final String MARK_ENTRY = "entry";
    private static final String MARK_FIELD = "field";
    private static final String MARK_RECAP = "recap";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_FORM_LIST = "form_list";
    private static final String MARK_FORM = "form";
    private static final String MARK_CATEGORY_LIST = "category_list";
    private static final String MARK_PERMISSION_CREATE_FORM = "permission_create_form";
    private static final String MARK_ENTRY_TYPE_GROUP = "entry_type_group";
    private static final String MARK_ENTRY_LIST = "entry_list";
    private static final String MARK_GROUP_ENTRY_LIST = "entry_group_list";
    private static final String MARK_THEME_REF_LIST = "theme_list";
    private static final String MARK_STR_FORM = "str_form";
    private static final String MARK_LIST = "list";
    private static final String MARK_GRAPH_TYPE_REF_LIST = "graph_type_list";
    private static final String MARK_NUMBER_QUESTION = "number_question";
    private static final String MARK_NUMBER_ITEMS = "number_items";
    private static final String MARK_DEFAULT_MESSAGE = "default_message";
    private static final String MARK_IS_ACTIVE_CAPTCHA = "is_active_captcha";
    private static final String MARK_EXPORT_FORMAT_REF_LIST = "export_format_list";
    private static final String MARK_FIRST_RESPONSE_DATE_FILTER = "fist_response_date_filter";
    private static final String MARK_LAST_RESPONSE_DATE_FILTER = "last_response_date_filter";
    private static final String MARK_FIRST_RESPONSE_DATE = "fist_response_date";
    private static final String MARK_LAST_RESPONSE_DATE = "last_response_date";
    private static final String MARK_NUMBER_RESPONSE = "number_response";
    private static final String MARK_TIMES_UNIT = "times_unit";
    private static final String MARK_PROCESSOR_KEY = "processor_key";
    private static final String MARK_PROCESSOR_CONFIGURATION = "processor_configuration";
    private static final String MARK_PROCESSOR_LIST = "processor_list";
    private static final String MARK_IS_SELECTED = "is_selected";
    private static final String MARK_OPTION_NO_DISPLAY_TITLE = "option_no_display_title";
    private static final String MARK_VALIDATOR_LIST = "validator_list";
    private static final String MARK_DEFAULT_THEME = "default_theme";
    private static final String MARK_IS_ACTIVE_MYLUTECE_AUTHENTIFICATION = "is_active_mylutece_authentification";
    private static final String MARK_PERMISSION_MANAGE_ADVANCED_PARAMETERS = "permission_manage_advanced_parameters";
    private static final String MARK_LIST_PARAM_DEFAULT_VALUES = "list_param_default_values";
    private static final String MARK_DEFAULT_VALUE_WORKGROUP_KEY = "workgroup_key_default_value";
    private static final String MARK_MAP_CHILD = "mapChild";

    // Jsp Definition
    private static final String JSP_DO_DISABLE_FORM = "jsp/admin/plugins/form/DoDisableForm.jsp";
    private static final String JSP_DO_DISABLE_AUTO_FORM = "jsp/admin/plugins/form/DoDisableAutoForm.jsp";
    private static final String JSP_DO_REMOVE_FORM = "jsp/admin/plugins/form/DoRemoveForm.jsp";
    private static final String JSP_DO_REMOVE_FIELD = "jsp/admin/plugins/form/DoRemoveField.jsp";
    private static final String JSP_DO_REMOVE_ENTRY = "jsp/admin/plugins/form/DoRemoveEntry.jsp";
    private static final String JSP_MANAGE_FORM = "jsp/admin/plugins/form/ManageForm.jsp";
    private static final String JSP_MODIFY_FORM = "jsp/admin/plugins/form/ModifyForm.jsp";
    private static final String JSP_MODIFY_ENTRY = "jsp/admin/plugins/form/ModifyEntry.jsp";
    private static final String JSP_MODIFY_FIELD = "jsp/admin/plugins/form/ModifyFieldWithConditionalQuestion.jsp";
    private static final String JSP_TEST_FORM = "jsp/admin/plugins/form/TestForm.jsp";
    private static final String JSP_DO_TEST_FORM = "jsp/admin/plugins/form/DoTestForm.jsp";
    private static final String JSP_MANAGE_OUTPUT_PROCESS_FORM = "jsp/admin/plugins/form/ManageOutputProcessor.jsp";
    private static final String JSP_MANAGE_VALIDATOR_FORM = "jsp/admin/plugins/form/ManageValidator.jsp";
    private static final String JSP_MANAGE_ADVANCED_PARAMETERS = "jsp/admin/plugins/form/ManageAdvancedParameters.jsp";

    // parameters form
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_REQUIREMENT = "requirement";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_DESCRIPTION = "description";
    private static final String PARAMETER_WELCOME_MESSAGE = "welcome_message";
    private static final String PARAMETER_UNAVAILABILITY_MESSAGE = "unavailability_message";
    private static final String PARAMETER_ACTIVE_CAPTCHA = "active_captcha";
    private static final String PARAMETER_ACTIVE_STORE_ADRESSE = "active_store_adresse";
    private static final String PARAMETER_ACTIVE_REQUIREMENT = "active_requirement";
    private static final String PARAMETER_LIMIT_NUMBER_RESPONSE = "limit_number_response";
    private static final String PARAMETER_LIBELLE_VALIDATE_BUTTON = "libelle_validate_button";
    private static final String PARAMETER_LIBELLE_RESET_BUTTON = "libelle_reset_button";
    private static final String PARAMETER_BACK_URL = "back_url";
    private static final String PARAMETER_PUBLICATION_MODE = "publication_mode";
    private static final String PARAMETER_DATE_BEGIN_DISPONIBILITY = "date_begin_disponibility";
    private static final String PARAMETER_DATE_END_DISPONIBILITY = "date_end_disponibility";
    private static final String PARAMETER_ACTIVE = "active";
    private static final String PARAMETER_WORKGROUP = "workgroup";
    private static final String PARAMETER_ID_MAILINIG_LIST = "id_mailing_list";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_ID_ENTRY = "id_entry";
    private static final String PARAMETER_ENTRY_ID = "entry_id";
    private static final String PARAMETER_MOVE_BUTTON = "move";
    private static final String PARAMETER_ID_ENTRY_GROUP = "id_entry_group";
    private static final String PARAMETER_ORDER_ID = "order_id";
    private static final String PARAMETER_ID_CATEGORY = "id_category";
    private static final String PARAMETER_ID_FIELD = "id_field";
    private static final String PARAMETER_ID_EXPRESSION = "id_expression";
    private static final String PARAMETER_ID_RECAP = "id_recap";
    private static final String PARAMETER_RECAP_MESSAGE = "recap_message";
    private static final String PARAMETER_RECAP_DATA = "recap_data";
    private static final String PARAMETER_GRAPH = "graph";
    private static final String PARAMETER_GRAPH_THREE_DIMENSION = "graph_three_dimension";
    private static final String PARAMETER_GRAPH_LABEL_VALUE = "graph_label_value";
    private static final String PARAMETER_ID_GRAPH_TYPE = "id_graph_type";
    private static final String PARAMETER_ID_RESPONSE = "id_response";
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String PARAMETER_APPLY = "apply";
    private static final String PARAMETER_VALUE = "value";
    private static final String PARAMETER_DEFAULT_VALUE = "default_value";
    private static final String PARAMETER_NO_DISPLAY_TITLE = "no_display_title";
    private static final String PARAMETER_SESSION = "session";
    private static final String PARAMETER_ID_EXPORT_FORMAT = "id_export_format";
    private static final String PARAMETER_FIRST_RESPONSE_DATE_FILTER = "fist_response_date_filter";
    private static final String PARAMETER_LAST_RESPONSE_DATE_FILTER = "last_response_date_filter";
    private static final String PARAMETER_TIMES_UNIT = "times_unit";
    private static final String PARAMETER_PROCESSOR_KEY = "processor_key";
    private static final String PARAMETER_IS_SELECTED = "is_selected";
    public static final String PARAMETER_ACTION_REDIRECT = "redirect";
    private static final String PARAMETER_INFORMATION_COMPLEMENTARY_1 = "information_complementary_1";
    private static final String PARAMETER_INFORMATION_COMPLEMENTARY_2 = "information_complementary_2";
    private static final String PARAMETER_INFORMATION_COMPLEMENTARY_3 = "information_complementary_3";
    private static final String PARAMETER_INFORMATION_COMPLEMENTARY_4 = "information_complementary_4";
    private static final String PARAMETER_INFORMATION_COMPLEMENTARY_5 = "information_complementary_5";
    private static final String PARAMETER_SUPPORT_HTTPS = "support_https";
    private static final String PARAMETER_OPTION_NO_DISPLAY_TITLE = "option_no_display_title";
    private static final String PARAMETER_THEME_XPAGE = "id_theme_list";
    private static final String PARAMETER_ACTIVE_MYLUTECE_AUTHENTIFICATION = "active_mylutece_authentification";
    private static final String PARAMETER_LIST = "list";
    private static final String PARAMETER_FRONT_OFFICE_TITLE = "front_office_title";
    private static final String PARAMETER_IS_SHOWN_FRONT_OFFICE_TITLE = "is_shown_front_office_title";

    // other constants
    private static final String EMPTY_STRING = "";
    private static final String JCAPTCHA_PLUGIN = "jcaptcha";
    public static final String PUBLICATION_MODE_AUTO = "1";
    private static final String QUESTION_MARK_STRING = "?";
    private static final String EQUAL_STRING = "=";
    private static final String MYLUTECE_PLUGIN = "mylutece";
    private static final String CONST_ZERO = "0";
    private static final String SQL_FILTER_ENTRY_POS = " ent.pos ";

    // session fields
    private final int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 50 );
    private String _strCurrentPageIndexForm;
    private int _nItemsPerPageForm;
    private String _strCurrentPageIndexEntry;
    private int _nItemsPerPageEntry;
    private String _strCurrentPageIndexConditionalEntry;
    private int _nItemsPerPageConditionalEntry;
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;
    private int _nIdActive = -1;
    private String _strWorkGroup = AdminWorkgroupService.ALL_GROUPS;
    private int _nIdForm = -1;
    private int _nIdEntry = -1;
    private List<FormSubmit> _listFormSubmitTest;
    private final IResponseService _responseService = SpringContextService.getBean( FormUtils.BEAN_FORM_RESPONSE_SERVICE );

    /*-------------------------------MANAGEMENT  FORM-----------------------------*/

    /**
     * Return management Form ( list of form )
     * @param request The Http request
     * @return Html form
     */
    public String getManageForm( HttpServletRequest request )
    {
        AdminUser adminUser = getUser(  );
        Plugin plugin = getPlugin(  );
        Locale locale = getLocale(  );
        ReferenceList refListWorkGroups;
        ReferenceList refListActive;
        List<FormAction> listActionsForFormEnable;
        List<FormAction> listActionsForFormDisable;
        List<FormAction> listActions;

        String strWorkGroup = request.getParameter( PARAMETER_WORKGROUP );
        String strActive = request.getParameter( PARAMETER_ACTIVE );
        _strCurrentPageIndexForm = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                _strCurrentPageIndexForm );
        _nItemsPerPageForm = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE,
                _nItemsPerPageForm, _nDefaultItemsPerPage );

        if ( ( strActive != null ) && !strActive.equals( EMPTY_STRING ) )
        {
            try
            {
                _nIdActive = Integer.parseInt( strActive );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );
            }
        }

        if ( strWorkGroup != null )
        {
            _strWorkGroup = strWorkGroup;
        }

        // build Filter
        FormFilter filter = new FormFilter(  );
        filter.setIdState( _nIdActive );
        filter.setWorkGroup( _strWorkGroup );

        List<Form> listForm = FormHome.getFormList( filter, getPlugin(  ) );
        listForm = (List<Form>) AdminWorkgroupService.getAuthorizedCollection( listForm, adminUser );

        refListWorkGroups = AdminWorkgroupService.getUserWorkgroups( adminUser, locale );
        refListActive = initRefListActive( plugin, locale );

        Map<String, Object> model = new HashMap<String, Object>(  );
        LocalizedPaginator<Form> paginator = new LocalizedPaginator<Form>( listForm, _nItemsPerPageForm,
                getJspManageForm( request ), PARAMETER_PAGE_INDEX, _strCurrentPageIndexForm, getLocale(  ) );

        listActionsForFormEnable = FormActionHome.selectActionsByFormState( Form.STATE_ENABLE, plugin, locale );
        listActionsForFormDisable = FormActionHome.selectActionsByFormState( Form.STATE_DISABLE, plugin, locale );

        for ( Form form : paginator.getPageItems(  ) )
        {
            if ( form.isActive(  ) )
            {
                listActions = listActionsForFormEnable;
            }
            else
            {
                listActions = listActionsForFormDisable;
            }

            listActions = (List<FormAction>) RBACService.getAuthorizedActionsCollection( listActions, form, getUser(  ) );
            form.setActions( listActions );
        }

        boolean bPermissionAdvancedParameter = RBACService.isAuthorized( Form.RESOURCE_TYPE,
                RBAC.WILDCARD_RESOURCES_ID, FormResourceIdService.PERMISSION_MANAGE_ADVANCED_PARAMETERS, getUser(  ) );

        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + _nItemsPerPageForm );
        model.put( MARK_USER_WORKGROUP_REF_LIST, refListWorkGroups );
        model.put( MARK_USER_WORKGROUP_SELECTED, _strWorkGroup );
        model.put( MARK_ACTIVE_REF_LIST, refListActive );
        model.put( MARK_ACTIVE_SELECTED, _nIdActive );
        model.put( MARK_FORM_LIST, paginator.getPageItems(  ) );
        model.put( MARK_LOCALE, request.getLocale(  ) );
        model.put( MARK_PERMISSION_MANAGE_ADVANCED_PARAMETERS, bPermissionAdvancedParameter );

        if ( RBACService.isAuthorized( Form.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    FormResourceIdService.PERMISSION_CREATE, adminUser ) )
        {
            model.put( MARK_PERMISSION_CREATE_FORM, true );
        }
        else
        {
            model.put( MARK_PERMISSION_CREATE_FORM, false );
        }

        setPageTitleProperty( EMPTY_STRING );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_FORM, locale, model );

        // ReferenceList refMailingList;
        // refMailingList=AdminMailingListService.getMailingLists(adminUser);
        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Returns advanced parameters form
     *
     * @param request The Http request
     * @return Html form
     */
    public String getManageAdvancedParameters( HttpServletRequest request )
    {
        if ( !RBACService.isAuthorized( Form.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    FormResourceIdService.PERMISSION_MANAGE_ADVANCED_PARAMETERS, getUser(  ) ) )
        {
            return getManageForm( request );
        }

        Map<String, Object> model = FormService.getInstance(  ).getManageAdvancedParameters( getUser(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_ADVANCED_PARAMETERS, getLocale(  ),
                model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Modify form parameter default values
     * @param request HttpServletRequest
     * @return JSP return
     * @throws AccessDeniedException
     */
    public String doModifyFormParameterDefaultValues( HttpServletRequest request )
        throws AccessDeniedException
    {
        if ( !RBACService.isAuthorized( Form.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    FormResourceIdService.PERMISSION_MANAGE_ADVANCED_PARAMETERS, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        ReferenceList listParams = FormParameterService.getService(  ).findDefaultValueParameters(  );

        for ( ReferenceItem param : listParams )
        {
            String strParamValue = request.getParameter( param.getCode(  ) );

            if ( strParamValue == null )
            {
                strParamValue = CONST_ZERO;
            }

            param.setName( strParamValue );
            FormParameterService.getService(  ).update( param );
        }

        return getJspManageAdvancedParameters( request );
    }

    /**
     * Modify entry parameter default values
     * @param request HttpServletRequest
     * @return JSP return
     * @throws AccessDeniedException
     */
    public String doModifyEntryParameterDefaultValues( HttpServletRequest request )
        throws AccessDeniedException
    {
        if ( !RBACService.isAuthorized( Form.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    FormResourceIdService.PERMISSION_MANAGE_ADVANCED_PARAMETERS, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        ReferenceList listParams = EntryParameterService.getService(  ).findAll(  );

        for ( ReferenceItem param : listParams )
        {
            String strParamValue = request.getParameter( param.getCode(  ) );

            if ( strParamValue == null )
            {
                strParamValue = CONST_ZERO;
            }

            param.setName( strParamValue );
            EntryParameterService.getService(  ).update( param );
        }

        return getJspManageAdvancedParameters( request );
    }

    /**
     * return url of the jsp manage advanced parameters form
     * @param request The HTTP request
     * @return url of the jsp manage form
     */
    private String getJspManageAdvancedParameters( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_ADVANCED_PARAMETERS;
    }

    /**
     * Get the request data and if there is no error insert the data in the form specified in parameter. return null if there is no error or else return the error page url
     * @param request the request
     * @param form form
     * @return null if there is no error or else return the error page url
     */
    private String getFormData( HttpServletRequest request, Form form )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strFrontOfficeTitle = request.getParameter( PARAMETER_FRONT_OFFICE_TITLE );
        String strIsShownFrontOfficeTitle = request.getParameter( PARAMETER_IS_SHOWN_FRONT_OFFICE_TITLE );
        String strDescription = request.getParameter( PARAMETER_DESCRIPTION );
        String strWorkgroup = request.getParameter( PARAMETER_WORKGROUP );
        String strMailingListId = request.getParameter( PARAMETER_ID_MAILINIG_LIST );
        String strActiveCaptcha = request.getParameter( PARAMETER_ACTIVE_CAPTCHA );
        String strActiveStoreAdresse = request.getParameter( PARAMETER_ACTIVE_STORE_ADRESSE );
        String strActiveRequirement = request.getParameter( PARAMETER_ACTIVE_REQUIREMENT );
        String strCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        String strActiveMyLuteceAuthentification = request.getParameter( PARAMETER_ACTIVE_MYLUTECE_AUTHENTIFICATION );

        String strLimitNumberResponse = request.getParameter( PARAMETER_LIMIT_NUMBER_RESPONSE );
        String strPublicationMode = request.getParameter( PARAMETER_PUBLICATION_MODE );
        String strDateBeginDisponibility = request.getParameter( PARAMETER_DATE_BEGIN_DISPONIBILITY );
        String strDateEndDisponibility = request.getParameter( PARAMETER_DATE_END_DISPONIBILITY );

        String strInformationComplementary1 = request.getParameter( PARAMETER_INFORMATION_COMPLEMENTARY_1 );
        String strInformationComplementary2 = request.getParameter( PARAMETER_INFORMATION_COMPLEMENTARY_2 );
        String strInformationComplementary3 = request.getParameter( PARAMETER_INFORMATION_COMPLEMENTARY_3 );
        String strInformationComplementary4 = request.getParameter( PARAMETER_INFORMATION_COMPLEMENTARY_4 );
        String strInformationComplementary5 = request.getParameter( PARAMETER_INFORMATION_COMPLEMENTARY_5 );
        String strSupportsHTTPS = request.getParameter( PARAMETER_SUPPORT_HTTPS );
        String strThemeXpage = request.getParameter( PARAMETER_THEME_XPAGE );

        String strFieldError = EMPTY_STRING;

        if ( ( strTitle == null ) || strTitle.trim(  ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_TITLE;
        }

        else if ( ( strDescription == null ) || strDescription.trim(  ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_DESCRIPTION;
        }

        if ( !strFieldError.equals( EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, getLocale(  ) ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        form.setTitle( strTitle );
        form.setDescription( strDescription );
        form.setWorkgroup( strWorkgroup );

        if ( ( strFrontOfficeTitle != null ) )
        {
            form.setFrontOfficeTitle( strFrontOfficeTitle );
        }

        if ( ( strInformationComplementary1 != null ) )
        {
            form.setInfoComplementary1( strInformationComplementary1 );
        }

        if ( ( strInformationComplementary2 != null ) )
        {
            form.setInfoComplementary2( strInformationComplementary2 );
        }

        if ( ( strInformationComplementary3 != null ) )
        {
            form.setInfoComplementary3( strInformationComplementary3 );
        }

        if ( ( strInformationComplementary4 != null ) )
        {
            form.setInfoComplementary4( strInformationComplementary4 );
        }

        if ( ( strInformationComplementary5 != null ) )
        {
            form.setInfoComplementary5( strInformationComplementary5 );
        }

        if ( strThemeXpage != null )
        {
            form.setCodeTheme( strThemeXpage );
        }

        if ( ( strSupportsHTTPS != null ) )
        {
            form.setSupportHTTPS( true );
        }
        else
        {
            form.setSupportHTTPS( false );
        }

        if ( strActiveCaptcha != null )
        {
            form.setActiveCaptcha( true );
        }
        else
        {
            form.setActiveCaptcha( false );
        }

        if ( strActiveStoreAdresse != null )
        {
            form.setActiveStoreAdresse( true );
        }
        else
        {
            form.setActiveStoreAdresse( false );
        }

        if ( strLimitNumberResponse != null )
        {
            form.setLimitNumberResponse( true );
        }
        else
        {
            form.setLimitNumberResponse( false );
        }

        if ( strActiveRequirement != null )
        {
            form.setActiveRequirement( true );
        }
        else
        {
            form.setActiveRequirement( false );
        }

        if ( strActiveMyLuteceAuthentification != null )
        {
            form.setActiveMyLuteceAuthentification( true );
        }
        else
        {
            form.setActiveMyLuteceAuthentification( false );
        }

        if ( strIsShownFrontOfficeTitle != null )
        {
            form.setIsShownFrontOfficeTitle( true );
        }
        else
        {
            form.setIsShownFrontOfficeTitle( false );
        }

        try
        {
            int nCategoryId = Integer.parseInt( strCategory );

            Category category = CategoryHome.findByPrimaryKey( nCategoryId, getPlugin(  ) );
            form.setCategory( category );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return getHomeUrl( request );
        }

        try
        {
            int nMailingListId = Integer.parseInt( strMailingListId );

            // if ( nMailingListId != -1 )
            // {
            form.setIdMailingList( nMailingListId );

            // }
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return getHomeUrl( request );
        }

        if ( ( strPublicationMode != null ) && strPublicationMode.equals( PUBLICATION_MODE_AUTO ) )
        {
            // Set date begin disponibility
            java.util.Date tDateBeginDisponibility = null;

            if ( ( strDateBeginDisponibility != null ) && !strDateBeginDisponibility.equals( EMPTY_STRING ) )
            {
                tDateBeginDisponibility = DateUtil.formatDate( strDateBeginDisponibility, getLocale(  ) );

                if ( tDateBeginDisponibility == null )
                {
                    return AdminMessageService.getMessageUrl( request, MESSAGE_ILLOGICAL_DATE_BEGIN_DISPONIBILITY,
                        AdminMessage.TYPE_STOP );
                }

                // no need to check the date begin of validity
            }

            // Set date end disponibility
            form.setDateBeginDisponibility( tDateBeginDisponibility );

            java.util.Date tDateEndDisponibility = null;

            if ( ( strDateEndDisponibility != null ) && !strDateEndDisponibility.equals( EMPTY_STRING ) )
            {
                tDateEndDisponibility = DateUtil.formatDate( strDateEndDisponibility, getLocale(  ) );

                if ( tDateEndDisponibility == null )
                {
                    return AdminMessageService.getMessageUrl( request, MESSAGE_ILLOGICAL_DATE_END_DISPONIBILITY,
                        AdminMessage.TYPE_STOP );
                }
                else
                {
                    if ( tDateEndDisponibility.before( FormUtils.getCurrentDate(  ) ) )
                    {
                        return AdminMessageService.getMessageUrl( request,
                            MESSAGE_DATE_END_DISPONIBILITY_BEFORE_CURRENT_DATE, AdminMessage.TYPE_STOP );
                    }
                }
            }

            if ( ( tDateBeginDisponibility != null ) && ( tDateEndDisponibility != null ) &&
                    tDateBeginDisponibility.after( tDateEndDisponibility ) )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_DATE_END_DISPONIBILITY_BEFORE_DATE_BEGIN,
                    AdminMessage.TYPE_STOP );
            }

            form.setDateEndDisponibility( tDateEndDisponibility );
            form.setActive( false );
            form.setAutoPublicationActive( true );
        }
        else
        {
            form.setDateBeginDisponibility( null );
            form.setDateEndDisponibility( null );
            form.setAutoPublicationActive( false );
        }

        return null; // No error
    }

    /**
     * Gets the form creation page
     * @param request The HTTP request
     * @return The form creation page
     */
    public String getCreateForm( HttpServletRequest request )
    {
        AdminUser adminUser = getUser(  );
        Locale locale = getLocale(  );
        ReferenceList refListWorkGroups;
        ReferenceList refMailingList;
        refListWorkGroups = AdminWorkgroupService.getUserWorkgroups( adminUser, locale );
        refMailingList = new ReferenceList(  );

        String strNothing = I18nService.getLocalizedString( PROPERTY_NOTHING, locale );
        refMailingList.addItem( -1, strNothing );
        refMailingList.addAll( AdminMailingListService.getMailingLists( adminUser ) );

        String defaultTheme = ThemeHome.getGlobalTheme(  );

        DefaultMessage defaultMessage = DefaultMessageHome.find( getPlugin(  ) );

        if ( !RBACService.isAuthorized( Form.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    FormResourceIdService.PERMISSION_CREATE, adminUser ) )
        {
            return getManageForm( request );
        }

        // Style management
        Collection<Theme> themes = ThemeHome.getThemesList(  );
        ReferenceList themesRefList = new ReferenceList(  );

        for ( Theme theme : themes )
        {
            themesRefList.addItem( theme.getCodeTheme(  ), theme.getThemeDescription(  ) );
        }

        // Default Values
        ReferenceList listParamDefaultValues = FormParameterService.getService(  ).findDefaultValueParameters(  );

        // Add categories
        List<Category> listCategoriesView = CategoryHome.getList( getPlugin(  ) );
        Category emptyCategory = new Category(  );
        emptyCategory.setIdCategory( -2 );
        emptyCategory.setTitle( "" );
        listCategoriesView.add( emptyCategory );

        ReferenceList refCategoryList = FormUtils.getRefListCategory( listCategoriesView );

        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_USER_WORKGROUP_REF_LIST, refListWorkGroups );
        model.put( MARK_MAILING_REF_LIST, refMailingList );
        model.put( MARK_THEME_REF_LIST, themesRefList );
        model.put( MARK_CATEGORY_LIST, refCategoryList );
        model.put( MARK_DEFAULT_MESSAGE, defaultMessage );
        model.put( MARK_DEFAULT_THEME, defaultTheme );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage(  ) );
        model.put( MARK_IS_ACTIVE_CAPTCHA, PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) );
        model.put( MARK_IS_ACTIVE_MYLUTECE_AUTHENTIFICATION, PluginService.isPluginEnable( MYLUTECE_PLUGIN ) );
        model.put( MARK_LIST_PARAM_DEFAULT_VALUES, listParamDefaultValues );
        model.put( MARK_DEFAULT_VALUE_WORKGROUP_KEY, AdminWorkgroupService.ALL_GROUPS );
        setPageTitleProperty( PROPERTY_CREATE_FORM_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_FORM, locale, model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Perform the form creation
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doCreateForm( HttpServletRequest request )
    {
        if ( ( request.getParameter( PARAMETER_CANCEL ) == null ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    FormResourceIdService.PERMISSION_CREATE, getUser(  ) ) )
        {
            Plugin plugin = getPlugin(  );
            Form form = new Form(  );
            String strError = getFormData( request, form );

            if ( strError != null )
            {
                return strError;
            }

            Recap recap = new Recap(  );
            recap.setIdRecap( RecapHome.create( recap, plugin ) );
            form.setRecap( recap );
            form.setDateCreation( FormUtils.getCurrentTimestamp(  ) );

            // Use default messages
            DefaultMessage defaultMessage = DefaultMessageHome.find( plugin );
            form.setWelcomeMessage( defaultMessage.getWelcomeMessage(  ) );
            form.setUnavailabilityMessage( defaultMessage.getUnavailabilityMessage(  ) );
            form.setRequirement( defaultMessage.getRequirement(  ) );
            form.setLibelleValidateButton( defaultMessage.getLibelleValidateButton(  ) );
            form.setLibelleResetButton( defaultMessage.getLibelleResetButton(  ) );

            FormHome.create( form, plugin );

            if ( PluginService.isPluginEnable( MYLUTECE_PLUGIN ) && form.isActiveMyLuteceAuthentification(  ) )
            {
                FormUtils.activateMyLuteceAuthentification( form, plugin, getLocale(  ), request );
            }
        }

        return getJspManageForm( request );
    }

    /**
     * Gets the form modification page
     * @param request The HTTP request
     * @return The form modification page
     */
    public String getModifyForm( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        List<IEntry> listEntry = new ArrayList<IEntry>(  );
        List<IEntry> listEntryFirstLevel;
        int nNumberQuestion;
        EntryFilter filter;
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = -1;
        Form form;

        if ( ( strIdForm != null ) && !strIdForm.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdForm = Integer.parseInt( strIdForm );
                _nIdForm = nIdForm;
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getManageForm( request );
            }
        }

        form = FormHome.findByPrimaryKey( nIdForm, plugin );

        if ( ( form == null ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_MODIFY,
                    getUser(  ) ) )
        {
            return getManageForm( request );
        }

        filter = new EntryFilter(  );
        filter.setIdForm( nIdForm );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        listEntryFirstLevel = EntryHome.getEntryList( filter, plugin );

        filter.setEntryParentNull( EntryFilter.ALL_INT );
        filter.setIdIsComment( EntryFilter.FILTER_FALSE );
        filter.setIdIsGroup( EntryFilter.FILTER_FALSE );
        nNumberQuestion = EntryHome.getNumberEntryByFilter( filter, plugin );

        Map<String, List<Integer>> mapIdParentOrdersChildren = new HashMap<String, List<Integer>>(  );

        // List of entry first level order
        List<Integer> listOrderEntryFirstLevel = new ArrayList<Integer>(  );
        initOrderFirstLevel( listEntryFirstLevel, listOrderEntryFirstLevel );

        mapIdParentOrdersChildren.put( "0", listOrderEntryFirstLevel );

        if ( listEntryFirstLevel.size(  ) != 0 )
        {
            listEntryFirstLevel.get( 0 ).setFirstInTheList( true );
            listEntryFirstLevel.get( listEntryFirstLevel.size(  ) - 1 ).setLastInTheList( true );
        }

        fillEntryListWithEntryFirstLevel( plugin, listEntry, listEntryFirstLevel );

        populateEntryMap( listEntry, mapIdParentOrdersChildren );

        _strCurrentPageIndexEntry = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                _strCurrentPageIndexEntry );
        _nItemsPerPageEntry = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE,
                _nItemsPerPageEntry, _nDefaultItemsPerPage );

        LocalizedPaginator<IEntry> paginator = new LocalizedPaginator<IEntry>( listEntry, _nItemsPerPageEntry,
                AppPathService.getBaseUrl( request ) + JSP_MODIFY_FORM + "?id_form=" + nIdForm, PARAMETER_PAGE_INDEX,
                _strCurrentPageIndexEntry, getLocale(  ) );

        AdminUser adminUser = getUser(  );

        Locale locale = getLocale(  );
        ReferenceList refListWorkGroups;
        ReferenceList refMailingList;
        ReferenceList refEntryType;

        refListWorkGroups = AdminWorkgroupService.getUserWorkgroups( adminUser, locale );

        refMailingList = new ReferenceList(  );

        String strNothing = I18nService.getLocalizedString( PROPERTY_NOTHING, locale );
        refMailingList.addItem( -1, strNothing );
        refMailingList.addAll( AdminMailingListService.getMailingLists( adminUser ) );

        List<Category> listCategoriesView = CategoryHome.getList( plugin );
        Category emptyCategory = new Category(  );
        emptyCategory.setIdCategory( -2 );
        emptyCategory.setTitle( "" );
        listCategoriesView.add( emptyCategory );

        ReferenceList refCategoryList = FormUtils.getRefListCategory( listCategoriesView );

        EntryType entryTypeGroup = new EntryType(  );
        refEntryType = initRefListEntryType( plugin, locale, entryTypeGroup );

        // Style management
        Collection<Theme> themes = ThemeHome.getThemesList(  );
        ReferenceList themesRefList = new ReferenceList(  );

        for ( Theme theme : themes )
        {
            themesRefList.addItem( theme.getCodeTheme(  ), theme.getThemeDescription(  ) );
        }

        if ( form.getCodeTheme(  ) == null )
        {
            form.setCodeTheme( ThemeHome.getGlobalTheme(  ) );
        }

        //get only the group type entries
        filter = new EntryFilter(  );
        filter.setIdForm( nIdForm );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setIdEntryType( EntryFilter.FILTER_GROUP_ENTRY );

        List<IEntry> listGroupEntry = EntryHome.getEntryList( filter, plugin );

        ReferenceList refListGroupEntry = ReferenceList.convert( listGroupEntry, "idEntry", "title", true );

        //add the root choice to the reference list
        ReferenceItem emptyItem = new ReferenceItem(  );
        emptyItem.setCode( StringUtils.EMPTY );
        emptyItem.setName( StringEscapeUtils.escapeHtml( I18nService.getLocalizedString( PROPERTY_NO_GROUP, locale ) ) );
        refListGroupEntry.add( 0, emptyItem );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + _nItemsPerPageEntry );
        model.put( MARK_USER_WORKGROUP_REF_LIST, refListWorkGroups );
        model.put( MARK_MAILING_REF_LIST, refMailingList );
        model.put( MARK_ENTRY_TYPE_REF_LIST, refEntryType );
        model.put( MARK_ENTRY_TYPE_GROUP, entryTypeGroup );
        model.put( MARK_FORM, form );
        model.put( MARK_CATEGORY_LIST, refCategoryList );
        model.put( MARK_ENTRY_LIST, paginator.getPageItems(  ) );
        model.put( MARK_THEME_REF_LIST, themesRefList );
        model.put( MARK_GROUP_ENTRY_LIST, refListGroupEntry );
        model.put( MARK_NUMBER_QUESTION, nNumberQuestion );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage(  ) );
        model.put( MARK_IS_ACTIVE_CAPTCHA, PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) );
        model.put( MARK_IS_ACTIVE_MYLUTECE_AUTHENTIFICATION, PluginService.isPluginEnable( MYLUTECE_PLUGIN ) );
        setPageTitleProperty( PROPERTY_MODIFY_FORM_TITLE );

        model.put( MARK_MAP_CHILD, mapIdParentOrdersChildren );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_FORM, locale, model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Fill an entry list with all the entries of the first level
     * @param plugin the plugin
     * @param listEntry the list of all the entries
     * @param listEntryFirstLevel the list of all the entries of the first level
     */
    private void fillEntryListWithEntryFirstLevel( Plugin plugin, List<IEntry> listEntry,
        List<IEntry> listEntryFirstLevel )
    {
        EntryFilter filter;

        for ( IEntry entry : listEntryFirstLevel )
        {
            listEntry.add( entry );

            if ( entry.getEntryType(  ).getGroup(  ) )
            {
                filter = new EntryFilter(  );
                filter.setIdEntryParent( entry.getIdEntry(  ) );
                entry.setChildren( EntryHome.getEntryList( filter, plugin ) );

                if ( entry.getChildren(  ).size(  ) != 0 )
                {
                    entry.getChildren(  ).get( 0 ).setFirstInTheList( true );
                    entry.getChildren(  ).get( entry.getChildren(  ).size(  ) - 1 ).setLastInTheList( true );
                }

                for ( IEntry entryChild : entry.getChildren(  ) )
                {
                    listEntry.add( entryChild );
                }
            }
        }
    }

    /**
     * Populate map with ( idParent : List<Orders> ) except for entry with
     * parent
     * @param listEntry
     * @param mapIdParentOrdersChildren
     */
    private void populateEntryMap( List<IEntry> listEntry, Map<String, List<Integer>> mapIdParentOrdersChildren )
    {
        List<Integer> listOrder;

        for ( IEntry entry : listEntry )
        {
            if ( entry.getParent(  ) != null )
            {
                Integer key = Integer.valueOf( entry.getParent(  ).getIdEntry(  ) );
                String strKey = key.toString(  );

                if ( mapIdParentOrdersChildren.get( strKey ) != null )
                {
                    mapIdParentOrdersChildren.get( key.toString(  ) ).add( entry.getPosition(  ) );
                }
                else
                {
                    listOrder = new ArrayList<Integer>(  );
                    listOrder.add( entry.getPosition(  ) );
                    mapIdParentOrdersChildren.put( key.toString(  ), listOrder );
                }
            }
        }
    }

    /**
     * Perform the form modification
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doModifyForm( HttpServletRequest request )
    {
        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            Plugin plugin = getPlugin(  );
            String strIdForm = request.getParameter( PARAMETER_ID_FORM );
            int nIdForm = -1;
            Form updatedForm;

            if ( ( strIdForm != null ) && !strIdForm.equals( EMPTY_STRING ) )
            {
                try
                {
                    nIdForm = Integer.parseInt( strIdForm );
                }
                catch ( NumberFormatException ne )
                {
                    AppLogService.error( ne );
                }
            }

            if ( nIdForm != -1 )
            {
                updatedForm = FormHome.findByPrimaryKey( nIdForm, plugin );

                Form form = FormHome.findByPrimaryKey( nIdForm, plugin );

                String strOldTheme = updatedForm.getCodeTheme(  );

                if ( ( updatedForm == null ) ||
                        !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm,
                            FormResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
                {
                    return getJspManageForm( request );
                }

                String strError = getFormData( request, updatedForm );

                if ( strError != null )
                {
                    return strError;
                }

                updatedForm.setIdForm( nIdForm );
                FormHome.update( updatedForm, getPlugin(  ) );

                if ( PluginService.isPluginEnable( MYLUTECE_PLUGIN ) &&
                        updatedForm.isActiveMyLuteceAuthentification(  ) && !form.isActiveMyLuteceAuthentification(  ) )
                {
                    FormUtils.activateMyLuteceAuthentification( updatedForm, plugin, getLocale(  ), request );
                }
                else if ( PluginService.isPluginEnable( MYLUTECE_PLUGIN ) &&
                        !updatedForm.isActiveMyLuteceAuthentification(  ) && form.isActiveMyLuteceAuthentification(  ) )
                {
                    FormUtils.deactivateMyLuteceAuthentification( updatedForm, plugin );
                }

                if ( request.getParameter( PARAMETER_APPLY ) != null )
                {
                    return getJspModifyForm( request, nIdForm );
                }
            }
        }

        return getJspManageForm( request );
    }

    /**
     * Gets the confirmation page of delete form
     * @param request The HTTP request
     * @return the confirmation page of delete form
     */
    public String getConfirmRemoveForm( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        String strMessage;
        int nIdForm = -1;

        if ( ( strIdForm == null ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_DELETE,
                    getUser(  ) ) )
        {
            return getHomeUrl( request );
        }

        try
        {
            nIdForm = Integer.parseInt( strIdForm );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return getHomeUrl( request );
        }

        ResponseFilter responseFilter = new ResponseFilter(  );
        responseFilter.setIdForm( nIdForm );

        if ( FormSubmitHome.getCountFormSubmit( responseFilter, plugin ) > 0 )
        {
            strMessage = MESSAGE_CONFIRM_REMOVE_FORM_WITH_FORM_SUBMIT;
        }
        else if ( ValidatorService.getInstance(  ).isAssociatedWithForm( nIdForm ) )
        {
            strMessage = MESSAGE_CONFIRM_REMOVE_FORM_WITH_VALIDATOR;
        }
        else
        {
            strMessage = MESSAGE_CONFIRM_REMOVE_FORM;
        }

        UrlItem url = new UrlItem( JSP_DO_REMOVE_FORM );
        url.addParameter( PARAMETER_ID_FORM, strIdForm );

        return AdminMessageService.getMessageUrl( request, strMessage, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform the form supression
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doRemoveForm( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        Plugin plugin = getPlugin(  );
        int nIdForm = -1;

        if ( request.getParameter( PARAMETER_ID_FORM ) == null )
        {
            return getHomeUrl( request );
        }

        try
        {
            nIdForm = Integer.parseInt( strIdForm );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        if ( ( nIdForm != -1 ) &&
                RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_DELETE,
                    getUser(  ) ) )
        {
            if ( FormPortletHome.getCountPortletByIdForm( nIdForm ) != 0 )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_CANT_REMOVE_FORM_ASSOCIATE_PORTLET,
                    AdminMessage.TYPE_CONFIRMATION );
            }

            ArrayList<String> listErrors = new ArrayList<String>(  );

            if ( !FormRemovalListenerService.getService(  ).checkForRemoval( strIdForm, listErrors, getLocale(  ) ) )
            {
                String strCause = AdminMessageService.getFormattedList( listErrors, getLocale(  ) );
                Object[] args = { strCause };

                return AdminMessageService.getMessageUrl( request, MESSAGE_CANT_REMOVE_FORM, args,
                    AdminMessage.TYPE_STOP );
            }

            FormHome.remove( nIdForm, plugin );
            OutputProcessorService.getInstance(  ).removeProcessorAssociationsByIdForm( nIdForm );

            // Removes the associations between all validators and the form
            ValidatorService.getInstance(  ).removeAssociationsWithForm( nIdForm );
        }

        return getJspManageForm( request );
    }

    /**
     * copy the form whose key is specified in the Http request
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doCopyForm( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        Form form;
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = -1;

        if ( request.getParameter( PARAMETER_ID_FORM ) == null )
        {
            return getHomeUrl( request );
        }

        try
        {
            nIdForm = Integer.parseInt( strIdForm );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        if ( ( nIdForm != -1 ) &&
                RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_COPY,
                    getUser(  ) ) )
        {
            form = FormHome.findByPrimaryKey( nIdForm, plugin );

            Object[] tabFormTileCopy = { form.getTitle(  ) };
            String strTitleCopyForm = I18nService.getLocalizedString( PROPERTY_COPY_FORM_TITLE, tabFormTileCopy,
                    getLocale(  ) );

            if ( strTitleCopyForm != null )
            {
                form.setTitle( strTitleCopyForm );
            }

            FormHome.copy( form, plugin );
        }

        return getJspManageForm( request );
    }

    /**
     * Gets the form recap modification page
     * @param request The HTTP request
     * @return the form recap modification page
     */
    public String getModifyRecap( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = -1;
        Form form;
        Recap recap;
        ReferenceList refListGraphType;

        if ( ( strIdForm != null ) &&
                RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_MODIFY,
                    getUser(  ) ) )
        {
            try
            {
                nIdForm = Integer.parseInt( strIdForm );
                _nIdForm = nIdForm;
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );
            }
        }

        if ( nIdForm == -1 )
        {
            return getManageForm( request );
        }

        form = FormHome.findByPrimaryKey( nIdForm, plugin );
        recap = RecapHome.findByPrimaryKey( form.getRecap(  ).getIdRecap(  ), plugin );
        recap.setForm( form );

        Locale locale = getLocale(  );
        refListGraphType = initRefListGraphType( plugin, locale );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_RECAP, recap );
        model.put( MARK_GRAPH_TYPE_REF_LIST, refListGraphType );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage(  ) );

        setPageTitleProperty( PROPERTY_MODIFY_RECAP_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_RECAP, locale, model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Get the request data and if there is no error insert the data in the recap specified in parameter. return null if there is no error or else return the error page url
     * @param request the request
     * @param recap the recap
     * @return null if there is no error or else return the error page url
     */
    private String getRecapData( HttpServletRequest request, Recap recap )
    {
        int nIdGraphType = -1;
        int nGraphThreeDimension = 0;
        String strBackUrl = request.getParameter( PARAMETER_BACK_URL );
        String strRecapMessage = request.getParameter( PARAMETER_RECAP_MESSAGE );
        String strRecapData = request.getParameter( PARAMETER_RECAP_DATA );
        String strGraph = request.getParameter( PARAMETER_GRAPH );
        String strIdGraphType = request.getParameter( PARAMETER_ID_GRAPH_TYPE );
        String strGraphThreeDimension = request.getParameter( PARAMETER_GRAPH_THREE_DIMENSION );

        // String strGraphLegende = request.getParameter( PARAMETER_GRAPH_LEGENDE );
        // String strGraphValueLegende = request.getParameter( PARAMETER_GRAPH_VALUE_LEGENDE );
        String strGraphLabel = request.getParameter( PARAMETER_GRAPH_LABEL_VALUE );
        GraphType graphType = null;
        String strFieldError = EMPTY_STRING;

        if ( ( strBackUrl == null ) || strBackUrl.trim(  ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_BACK_URL;
        }

        else if ( ( strRecapMessage == null ) || strRecapMessage.trim(  ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_RECAP_MESSAGE;
        }

        if ( !strFieldError.equals( EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, getLocale(  ) ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        recap.setBackUrl( strBackUrl );
        recap.setRecapMessage( strRecapMessage );

        if ( strRecapData != null )
        {
            recap.setRecapData( true );
        }
        else
        {
            recap.setRecapData( false );
        }

        if ( strGraph != null )
        {
            recap.setGraph( true );

            if ( ( strIdGraphType != null ) && !strIdGraphType.trim(  ).equals( EMPTY_STRING ) )
            {
                try
                {
                    nIdGraphType = Integer.parseInt( strIdGraphType );
                    graphType = new GraphType(  );
                    graphType.setIdGraphType( nIdGraphType );
                    recap.setGraphType( graphType );
                }
                catch ( NumberFormatException ne )
                {
                    AppLogService.error( ne );
                }
            }

            if ( strGraphThreeDimension != null )
            {
                try
                {
                    nGraphThreeDimension = Integer.parseInt( strGraphThreeDimension );
                }
                catch ( NumberFormatException ne )
                {
                    AppLogService.error( ne );
                }
            }

            if ( nGraphThreeDimension == 0 )
            {
                recap.setGraphThreeDimension( false );
            }
            else
            {
                recap.setGraphThreeDimension( true );
            }

            /*
             * if ( strGraphLegende != null ) { recap.setGraphLegende( true ); recap.setGraphValueLegende( strGraphValueLegende ); } else { recap.setGraphLegende( false ); recap.setGraphValueLegende(
             * null ); }
             */
            if ( strGraphLabel != null )
            {
                recap.setGraphLabelValue( true );
            }
            else
            {
                recap.setGraphLabelValue( false );
            }
        }
        else
        {
            recap.setGraph( false );
            recap.setGraphType( null );
            recap.setGraphLabelValue( false );
            recap.setGraphThreeDimension( false );
            recap.setGraphLegende( false );
            recap.setGraphValueLegende( null );
        }

        return null; // No error
    }

    /**
     * Perform the recap form modification
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doModifyRecap( HttpServletRequest request )
    {
        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            String strIdRecap = request.getParameter( PARAMETER_ID_RECAP );
            int nIdRecap = -1;
            Recap recap = null;

            if ( strIdRecap != null )
            {
                try
                {
                    nIdRecap = Integer.parseInt( strIdRecap );
                }
                catch ( NumberFormatException ne )
                {
                    AppLogService.error( ne );
                }
            }

            if ( nIdRecap != -1 )
            {
                recap = new Recap(  );
                recap.setIdRecap( nIdRecap );

                String strError = getRecapData( request, recap );

                if ( strError != null )
                {
                    return strError;
                }

                RecapHome.update( recap, getPlugin(  ) );
            }
        }

        return getJspManageForm( request );
    }

    /**
     * Gets the entry creation page
     * @param request The HTTP request
     * @return The entry creation page
     */
    public String getCreateEntry( HttpServletRequest request )
    {
        Form form;
        Plugin plugin = getPlugin(  );
        IEntry entry;
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = -1;
        entry = FormUtils.createEntryByType( request, plugin );

        if ( ( entry == null ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                    FormResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getManageForm( request );
        }

        if ( ( strIdField != null ) && !strIdField.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdField = Integer.parseInt( strIdField );

                Field field = new Field(  );
                field.setIdField( nIdField );
                entry.setFieldDepend( field );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getJspManageForm( request );
            }
        }

        form = FormHome.findByPrimaryKey( _nIdForm, plugin );
        entry.setForm( form );

        // Default Values
        ReferenceList listParamDefaultValues = EntryParameterService.getService(  ).findAll(  );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_ENTRY, entry );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage(  ) );
        model.put( MARK_LIST_PARAM_DEFAULT_VALUES, listParamDefaultValues );

        if ( entry.getEntryType(  ).getComment(  ) )
        {
            setPageTitleProperty( PROPERTY_CREATE_COMMENT_TITLE );
        }
        else
        {
            setPageTitleProperty( PROPERTY_CREATE_QUESTION_TITLE );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( entry.getTemplateCreate(  ), getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Perform the entry creation
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doCreateEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        IEntry entry;
        Field fieldDepend = null;
        Form form;
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = -1;

        if ( ( strIdField != null ) && !strIdField.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdField = Integer.parseInt( strIdField );
                fieldDepend = new Field(  );
                fieldDepend.setIdField( nIdField );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );
            }
        }

        if ( ( request.getParameter( PARAMETER_CANCEL ) == null ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                    FormResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            entry = FormUtils.createEntryByType( request, plugin );

            if ( entry == null )
            {
                return getJspManageForm( request );
            }

            String strError = entry.getRequestData( request, getLocale(  ) );

            if ( strError != null )
            {
                return strError;
            }

            entry.setFieldDepend( fieldDepend );
            form = new Form(  );
            form.setIdForm( _nIdForm );
            entry.setForm( form );
            entry.setIdEntry( EntryHome.create( entry, plugin ) );

            if ( entry.getFields(  ) != null )
            {
                for ( Field field : entry.getFields(  ) )
                {
                    field.setParentEntry( entry );
                    FieldHome.create( field, plugin );
                }
            }

            if ( request.getParameter( PARAMETER_APPLY ) != null )
            {
                return getJspModifyEntry( request, entry.getIdEntry(  ) );
            }
        }

        if ( fieldDepend != null )
        {
            return getJspModifyField( request, fieldDepend.getIdField(  ) );
        }
        else
        {
            return getJspModifyForm( request, _nIdForm );
        }
    }

    /**
     * Gets the entry modification page
     * @param request The HTTP request
     * @return The entry modification page
     */
    public String getModifyEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        IEntry entry;
        ReferenceList refListRegularExpression;
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = -1;

        if ( ( strIdEntry != null ) && !strIdEntry.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdEntry = Integer.parseInt( strIdEntry );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getManageForm( request );
            }
        }

        if ( ( nIdEntry == -1 ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                    FormResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getManageForm( request );
        }

        _nIdEntry = nIdEntry;
        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        List<Field> listField = new ArrayList<Field>(  );

        for ( Field field : entry.getFields(  ) )
        {
            field = FieldHome.findByPrimaryKey( field.getIdField(  ), plugin );
            listField.add( field );
        }

        entry.setFields( listField );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_ENTRY, entry );
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        LocalizedPaginator paginator = entry.getPaginator( _nItemsPerPage,
                AppPathService.getBaseUrl( request ) + JSP_MODIFY_ENTRY + "?id_entry=" + nIdEntry,
                PARAMETER_PAGE_INDEX, _strCurrentPageIndex, getLocale(  ) );

        if ( paginator != null )
        {
            model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + _nItemsPerPage );
            model.put( MARK_NUMBER_ITEMS, paginator.getItemsCount(  ) );
            model.put( MARK_LIST, paginator.getPageItems(  ) );
            model.put( MARK_PAGINATOR, paginator );
        }

        refListRegularExpression = entry.getReferenceListRegularExpression( entry, plugin );

        if ( refListRegularExpression != null )
        {
            model.put( MARK_REGULAR_EXPRESSION_LIST_REF_LIST, refListRegularExpression );
        }

        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage(  ) );

        if ( entry.getEntryType(  ).getComment(  ) )
        {
            setPageTitleProperty( PROPERTY_MODIFY_COMMENT_TITLE );
        }
        else if ( entry.getEntryType(  ).getGroup(  ) )
        {
            setPageTitleProperty( PROPERTY_MODIFY_GROUP_TITLE );
        }
        else
        {
            setPageTitleProperty( PROPERTY_MODIFY_QUESTION_TITLE );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( entry.getTemplateModify(  ), getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Perform the entry modification
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doModifyEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        IEntry entry;
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = -1;

        if ( ( strIdEntry != null ) && !strIdEntry.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdEntry = Integer.parseInt( strIdEntry );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );
            }
        }

        if ( ( nIdEntry == -1 ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                    FormResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getManageForm( request );
        }

        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            String strError = entry.getRequestData( request, getLocale(  ) );

            if ( strError != null )
            {
                return strError;
            }

            EntryHome.update( entry, plugin );

            if ( entry.getFields(  ) != null )
            {
                for ( Field field : entry.getFields(  ) )
                {
                    // Check if the field already exists in the database
                    Field fieldStored = FieldHome.findByPrimaryKey( field.getIdField(  ), plugin );

                    if ( fieldStored != null )
                    {
                        // If it exists, update
                        FieldHome.update( field, plugin );
                    }
                    else
                    {
                        // If it does not exist, create
                        FieldHome.create( field, plugin );
                    }
                }
            }
        }

        if ( request.getParameter( PARAMETER_APPLY ) == null )
        {
            if ( entry.getFieldDepend(  ) != null )
            {
                return getJspModifyField( request, entry.getFieldDepend(  ).getIdField(  ) );
            }
            else
            {
                return getJspModifyForm( request, _nIdForm );
            }
        }
        else
        {
            return getJspModifyEntry( request, nIdEntry );
        }
    }

    /**
     * Gets the confirmation page of delete entry
     * @param request The HTTP request
     * @return the confirmation page of delete entry
     */
    public String getConfirmRemoveEntry( HttpServletRequest request )
    {
        IEntry entry;
        Plugin plugin = getPlugin(  );
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        String strMessage;
        int nIdEntry = -1;

        if ( ( strIdEntry != null ) && !strIdEntry.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdEntry = Integer.parseInt( strIdEntry );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getJspManageForm( request );
            }
        }

        if ( ( nIdEntry == -1 ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                    FormResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getJspManageForm( request );
        }

        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        if ( entry.getEntryType(  ).getGroup(  ) )
        {
            if ( entry.getChildren(  ).size(  ) != 0 )
            {
                strMessage = MESSAGE_CONFIRM_REMOVE_GROUP_WITH_ENTRY;
            }
            else
            {
                strMessage = MESSAGE_CONFIRM_REMOVE_GROUP_WITH_ANY_ENTRY;
            }
        }
        else
        {
            strMessage = MESSAGE_CONFIRM_REMOVE_ENTRY;
        }

        UrlItem url = new UrlItem( JSP_DO_REMOVE_ENTRY );
        url.addParameter( PARAMETER_ID_ENTRY, strIdEntry + "#list" );

        return AdminMessageService.getMessageUrl( request, strMessage, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform the entry supression
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doRemoveEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        IEntry entry;
        int nIdEntry = -1;

        if ( ( strIdEntry != null ) && !strIdEntry.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdEntry = Integer.parseInt( strIdEntry );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getJspManageForm( request );
            }
        }

        if ( ( nIdEntry == -1 ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                    FormResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getJspManageForm( request );
        }

        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        ArrayList<String> listErrors = new ArrayList<String>(  );

        if ( !EntryRemovalListenerService.getService(  ).checkForRemoval( strIdEntry, listErrors, getLocale(  ) ) )
        {
            String strCause = AdminMessageService.getFormattedList( listErrors, getLocale(  ) );
            Object[] args = { strCause };

            return AdminMessageService.getMessageUrl( request, MESSAGE_CANT_REMOVE_ENTRY, args, AdminMessage.TYPE_STOP );
        }

        // Update order
        List<IEntry> listEntry;
        EntryFilter filter = new EntryFilter(  );
        filter.setIdForm( entry.getForm(  ).getIdForm(  ) );
        listEntry = EntryHome.getEntryList( filter, plugin );

        if ( entry.getFieldDepend(  ) == null )
        {
            this.moveDownEntryOrder( plugin, listEntry.size(  ), entry, entry.getForm(  ).getIdForm(  ) );
        }
        else
        {
            //conditional questions
            EntryHome.decrementOrderByOne( plugin, entry.getPosition(  ), entry.getFieldDepend(  ).getIdField(  ),
                entry.getForm(  ).getIdForm(  ) );
        }

        // Remove entry
        EntryHome.remove( nIdEntry, plugin );

        if ( entry.getFieldDepend(  ) != null )
        {
            return getJspModifyField( request, entry.getFieldDepend(  ).getIdField(  ) );
        }
        else
        {
            return getJspModifyForm( request, _nIdForm );
        }
    }

    /**
     * copy the entry whose key is specified in the Http request
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doCopyEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        IEntry entry;
        int nIdEntry = -1;

        if ( ( strIdEntry != null ) && !strIdEntry.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdEntry = Integer.parseInt( strIdEntry );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getJspManageForm( request );
            }
        }

        if ( ( nIdEntry == -1 ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                    FormResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getJspManageForm( request );
        }

        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        Object[] tabEntryTileCopy = { entry.getTitle(  ) };
        String strTitleCopyEntry = I18nService.getLocalizedString( PROPERTY_COPY_ENTRY_TITLE, tabEntryTileCopy,
                getLocale(  ) );

        if ( strTitleCopyEntry != null )
        {
            entry.setTitle( strTitleCopyEntry );
        }

        EntryHome.copy( entry, plugin );

        if ( entry.getFieldDepend(  ) != null )
        {
            return getJspModifyField( request, entry.getFieldDepend(  ).getIdField(  ) );
        }
        else
        {
            return getJspModifyForm( request, _nIdForm );
        }
    }

    /**
     * Gets the list of questions group
     * @param request The HTTP request
     * @return the list of questions group
     */
    public String getMoveEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        IEntry entry;
        List<IEntry> listGroup;
        EntryFilter filter;
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = -1;

        if ( ( strIdEntry != null ) && !strIdEntry.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdEntry = Integer.parseInt( strIdEntry );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getManageForm( request );
            }
        }

        if ( ( nIdEntry == -1 ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                    FormResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getManageForm( request );
        }

        _nIdEntry = nIdEntry;
        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );
        // recup group
        filter = new EntryFilter(  );
        filter.setIdForm( entry.getForm(  ).getIdForm(  ) );
        filter.setIdIsGroup( EntryFilter.FILTER_TRUE );
        listGroup = EntryHome.getEntryList( filter, plugin );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_ENTRY, entry );
        model.put( MARK_ENTRY_LIST, listGroup );
        setPageTitleProperty( EMPTY_STRING );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MOVE_ENTRY, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Move the entry in the questions group specified in parameter
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doMoveEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        IEntry entryToMove;
        IEntry entryGroup;
        String strIdEntryGroup = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntryGroup = -1;

        if ( ( strIdEntryGroup != null ) && !strIdEntryGroup.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdEntryGroup = Integer.parseInt( strIdEntryGroup );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getJspManageForm( request );
            }
        }

        if ( nIdEntryGroup == -1 )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_SELECT_GROUP, AdminMessage.TYPE_STOP );
        }

        if ( !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                    FormResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getJspManageForm( request );
        }

        entryToMove = EntryHome.findByPrimaryKey( _nIdEntry, plugin );
        entryGroup = EntryHome.findByPrimaryKey( nIdEntryGroup, plugin );

        Integer nPosition;

        if ( entryToMove.getPosition(  ) < entryGroup.getPosition(  ) )
        {
            nPosition = entryGroup.getPosition(  );
            this.moveDownEntryOrder( plugin, nPosition, entryToMove, entryToMove.getForm(  ).getIdForm(  ) );
        }
        else
        {
            nPosition = entryGroup.getPosition(  ) + entryGroup.getChildren(  ).size(  ) + 1;
            this.moveUpEntryOrder( plugin, nPosition, entryToMove, entryToMove.getForm(  ).getIdForm(  ) );
        }

        entryToMove.setParent( entryGroup );
        EntryHome.update( entryToMove, plugin );

        return getJspModifyForm( request, _nIdForm );
    }

    /**
     * Move out the entry
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doMoveOutEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        IEntry entry;
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = -1;

        if ( ( strIdEntry != null ) && !strIdEntry.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdEntry = Integer.parseInt( strIdEntry );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getJspManageForm( request );
            }
        }

        if ( ( nIdEntry == -1 ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                    FormResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getJspManageForm( request );
        }

        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        List<IEntry> listEntry;
        EntryFilter filter = new EntryFilter(  );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        filter.setIdForm( entry.getForm(  ).getIdForm(  ) );
        listEntry = EntryHome.getEntryList( filter, plugin );

        Integer nListEntrySize = listEntry.size(  );

        this.doMoveOutEntry( plugin, entry.getForm(  ).getIdForm(  ), nListEntrySize, entry );

        return this.getJspModifyForm( request, entry.getForm(  ).getIdForm(  ) );
    }

    /**
     * Gets the confirmation page of disable form
     * @param request The HTTP request
     * @return the confirmation page of disable form
     */
    public String getConfirmDisableForm( HttpServletRequest request )
    {
        return getConfirmDisable( request, false );
    }

    /**
     * Gets the confirmation page of disable auto published form
     * @param request The HTTP request
     * @return the confirmation page of disable form
     */
    public String getConfirmDisableAutoForm( HttpServletRequest request )
    {
        return getConfirmDisable( request, true );
    }

    /**
     * Gets the confirmation page of disable form
     * @param request The HTTP request
     * @param bAutoPublished If the form is auto published
     * @return the confirmation page of disable form
     */
    private String getConfirmDisable( HttpServletRequest request, boolean bAutoPublished )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nCountPortlet = 0;
        int nIdForm = -1;
        String strMessage;

        if ( ( strIdForm == null ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm,
                    FormResourceIdService.PERMISSION_CHANGE_STATE, getUser(  ) ) )
        {
            return getHomeUrl( request );
        }

        try
        {
            nIdForm = Integer.parseInt( strIdForm );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return getHomeUrl( request );
        }

        nCountPortlet = FormPortletHome.getCountPortletByIdForm( nIdForm );

        if ( nCountPortlet == 0 )
        {
            strMessage = MESSAGE_CONFIRM_DISABLE_FORM;
        }
        else
        {
            strMessage = MESSAGE_CONFIRM_DISABLE_FORM_WITH_PORTLET;
        }

        String strJspUrl = JSP_DO_DISABLE_FORM;

        if ( bAutoPublished )
        {
            strJspUrl = JSP_DO_DISABLE_AUTO_FORM;
        }

        UrlItem url = new UrlItem( strJspUrl );
        url.addParameter( PARAMETER_ID_FORM, strIdForm );

        return AdminMessageService.getMessageUrl( request, strMessage, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform disable form
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doDisableForm( HttpServletRequest request )
    {
        return doDisable( request, false );
    }

    /**
     * Perform disable auto published form
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doDisableAutoForm( HttpServletRequest request )
    {
        return doDisable( request, true );
    }

    /**
     * Perform disable form
     * @param request The HTTP request
     * @param bAutoPublished If the form is auto published
     * @return The URL to go after performing the action
     */
    private String doDisable( HttpServletRequest request, boolean bAutoPublished )
    {
        Form form;
        Plugin plugin = getPlugin(  );
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = -1;

        if ( ( strIdForm == null ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm,
                    FormResourceIdService.PERMISSION_CHANGE_STATE, getUser(  ) ) )
        {
            return getHomeUrl( request );
        }

        try
        {
            nIdForm = Integer.parseInt( strIdForm );
            form = FormHome.findByPrimaryKey( nIdForm, plugin );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return getHomeUrl( request );
        }

        if ( nIdForm != -1 )
        {
            form.setActive( false );

            if ( bAutoPublished )
            {
                form.setAutoPublicationActive( false );
            }

            FormHome.update( form, getPlugin(  ) );
        }

        return getJspManageForm( request );
    }

    /**
     * Perform enable form
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doEnableForm( HttpServletRequest request )
    {
        return doEnable( request, false );
    }

    /**
     * Perform enable auto published form
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doEnableAutoForm( HttpServletRequest request )
    {
        return doEnable( request, true );
    }

    /**
     * Perform enable form
     * @param request The HTTP request
     * @param bAutoPublished If the form is auto published
     * @return The URL to go after performing the action
     */
    private String doEnable( HttpServletRequest request, boolean bAutoPublished )
    {
        Form form;
        Plugin plugin = getPlugin(  );
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = -1;

        if ( ( strIdForm == null ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm,
                    FormResourceIdService.PERMISSION_CHANGE_STATE, getUser(  ) ) )
        {
            return getHomeUrl( request );
        }

        try
        {
            nIdForm = Integer.parseInt( strIdForm );
            form = FormHome.findByPrimaryKey( nIdForm, plugin );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return getHomeUrl( request );
        }

        if ( nIdForm != -1 )
        {
            // No need to check date begin validity
            if ( ( form.getDateEndDisponibility(  ) != null ) &&
                    form.getDateEndDisponibility(  ).before( FormUtils.getCurrentDate(  ) ) )
            {
                return AdminMessageService.getMessageUrl( request,
                    MESSAGE_CANT_ENABLE_FORM_DATE_END_DISPONIBILITY_BEFORE_CURRENT_DATE, AdminMessage.TYPE_STOP );
            }

            form.setActive( true );

            if ( bAutoPublished )
            {
                form.setAutoPublicationActive( true );
            }

            FormHome.update( form, getPlugin(  ) );
        }

        return getJspManageForm( request );
    }

    /**
     * return url of the jsp manage form
     * @param request The HTTP request
     * @return url of the jsp manage form
     */
    private String getJspManageForm( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_FORM;
    }

    /**
     * return url of the jsp modify form
     * @param request The HTTP request
     * @param nIdForm the key of form to modify
     * @return return url of the jsp modify form
     */
    private String getJspModifyForm( HttpServletRequest request, int nIdForm )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MODIFY_FORM + "?id_form=" + nIdForm;
    }

    /**
     * return url of the jsp test form
     * @param request The HTTP request
     * @param nIdForm the key of form to modify
     * @return return url of the jsp modify form
     */
    private String getJspTestForm( HttpServletRequest request, int nIdForm )
    {
        return AppPathService.getBaseUrl( request ) + JSP_TEST_FORM + "?id_form=" + nIdForm + "&session=session";
    }

    /**
     * return url of the jsp modify entry
     * @param request The HTTP request
     * @param nIdEntry the key of the entry to modify
     * @return return url of the jsp modify entry
     */
    private String getJspModifyEntry( HttpServletRequest request, int nIdEntry )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MODIFY_ENTRY + "?id_entry=" + nIdEntry;
    }

    /**
     * return url of the jsp modify field
     * @param request The HTTP request
     * @param nIdField the key of the field to modify
     * @return return url of the jsp modify field
     */
    private String getJspModifyField( HttpServletRequest request, int nIdField )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MODIFY_FIELD + "?id_field=" + nIdField;
    }

    /**
     * return url of the jsp modify form
     * @param request The HTTP request
     * @param nIdForm the key of form to modify
     * @return return url of the jsp modify form
     */
    public static String getJspManageOutputProcessForm( HttpServletRequest request, int nIdForm, String parameterName,
        String parameterValue )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_OUTPUT_PROCESS_FORM + "?id_form=" + nIdForm + "&" +
        parameterName + "=" + parameterValue;
    }

    /**
     * Init reference list whidth the different state of form
     * @param plugin the plugin
     * @param locale the locale
     * @return reference list of form state
     */
    private ReferenceList initRefListActive( Plugin plugin, Locale locale )
    {
        ReferenceList refListState = new ReferenceList(  );
        String strAll = I18nService.getLocalizedString( PROPERTY_ALL, locale );
        String strYes = I18nService.getLocalizedString( PROPERTY_YES, locale );
        String strNo = I18nService.getLocalizedString( PROPERTY_NO, locale );

        refListState.addItem( -1, strAll );
        refListState.addItem( 1, strYes );
        refListState.addItem( 0, strNo );

        return refListState;
    }

    /**
     * Init reference list whidth the different entry type
     * @param plugin the plugin
     * @param locale the locale
     * @param entryTypeGroup the entry type who represent a group
     * @return reference list of entry type
     */
    private ReferenceList initRefListEntryType( Plugin plugin, Locale locale, EntryType entryTypeGroup )
    {
        ReferenceList refListEntryType = new ReferenceList(  );
        List<EntryType> listEntryType = EntryTypeHome.getList( plugin );

        for ( EntryType entryType : listEntryType )
        {
            if ( !entryType.getGroup(  ) && !entryType.getMyLuteceUser(  ) )
            {
                refListEntryType.addItem( entryType.getIdType(  ), entryType.getTitle(  ) );
            }
            else if ( entryType.getGroup(  ) && !entryType.getMyLuteceUser(  ) )
            {
                entryTypeGroup.setIdType( entryType.getIdType(  ) );
            }
        }

        return refListEntryType;
    }

    /**
     * Init reference list whidth the different graph type
     * @param plugin the plugin
     * @param locale the locale
     * @return reference list of graph type
     */
    private ReferenceList initRefListGraphType( Plugin plugin, Locale locale )
    {
        ReferenceList refListGraphType = new ReferenceList(  );
        List<GraphType> listGraphType = GraphTypeHome.getList( plugin );

        for ( GraphType graphType : listGraphType )
        {
            refListGraphType.addItem( graphType.getIdGraphType(  ), graphType.getTitle(  ) );
        }

        return refListGraphType;
    }

    /**
     * Get the request data and if there is no error insert the data in the field specified in parameter. return null if there is no error or else return the error page url
     * @param request the request
     * @param field field
     * @return null if there is no error or else return the error page url
     */
    private String getFieldData( HttpServletRequest request, Field field )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strValue = request.getParameter( PARAMETER_VALUE );
        String strDefaultValue = request.getParameter( PARAMETER_DEFAULT_VALUE );
        String strNoDisplayTitle = request.getParameter( PARAMETER_NO_DISPLAY_TITLE );

        String strFieldError = EMPTY_STRING;

        if ( ( strTitle == null ) || strTitle.trim(  ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_TITLE_FIELD;
        }
        else if ( ( strValue == null ) || EMPTY_STRING.equals( strValue ) )
        {
            strFieldError = FIELD_VALUE_FIELD;
        }
        else if ( !StringUtil.checkCodeKey( strValue ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_FIELD_VALUE_FIELD, AdminMessage.TYPE_STOP );
        }

        if ( !strFieldError.equals( EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, getLocale(  ) ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        field.setTitle( strTitle );
        field.setValue( strValue );

        if ( strDefaultValue == null )
        {
            field.setDefaultValue( false );
        }
        else
        {
            field.setDefaultValue( true );
        }

        if ( strNoDisplayTitle == null )
        {
            field.setNoDisplayTitle( false );
        }
        else
        {
            field.setNoDisplayTitle( true );
        }

        return null; // No error
    }

    /**
     * Gets the field creation page
     * @param request The HTTP request
     * @return the field creation page
     */
    public String getCreateField( HttpServletRequest request )
    {
        Field field = new Field(  );
        IEntry entry = EntryHome.findByPrimaryKey( _nIdEntry, getPlugin(  ) );

        if ( ( entry == null ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                    FormResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getManageForm( request );
        }

        field.setParentEntry( entry );

        Map<String, Object> model = new HashMap<String, Object>(  );
        Locale locale = getLocale(  );

        if ( request.getParameter( PARAMETER_OPTION_NO_DISPLAY_TITLE ) != null )
        {
            model.put( MARK_OPTION_NO_DISPLAY_TITLE, true );
        }
        else
        {
            model.put( MARK_OPTION_NO_DISPLAY_TITLE, false );
        }

        model.put( MARK_FIELD, field );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_FIELD, locale, model );
        setPageTitleProperty( PROPERTY_CREATE_FIELD_TITLE );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Gets the field modification page
     * @param request The HTTP request
     * @param bWithConditionalQuestion true if the field is associate to conditionals questions
     * @return the field modification page
     */
    public String getModifyField( HttpServletRequest request, boolean bWithConditionalQuestion )
    {
        Field field = null;
        IEntry entry = null;
        Plugin plugin = getPlugin(  );
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = -1;

        if ( request.getParameter( PARAMETER_ID_FIELD ) == null )
        {
            return getHomeUrl( request );
        }

        try
        {
            nIdField = Integer.parseInt( strIdField );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return getManageForm( request );
        }

        if ( nIdField != -1 )
        {
            field = FieldHome.findByPrimaryKey( nIdField, getPlugin(  ) );
        }

        if ( ( field == null ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                    FormResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getManageForm( request );
        }

        entry = EntryHome.findByPrimaryKey( field.getParentEntry(  ).getIdEntry(  ), plugin );

        field.setParentEntry( entry );

        HashMap<String, Object> model = new HashMap<String, Object>(  );
        Locale locale = getLocale(  );
        model.put( MARK_FIELD, field );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_FIELD, locale, model );

        if ( bWithConditionalQuestion )
        {
            ReferenceList refEntryType;
            refEntryType = initRefListEntryType( plugin, locale, new EntryType(  ) );
            _strCurrentPageIndexConditionalEntry = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                    _strCurrentPageIndexConditionalEntry );
            _nItemsPerPageConditionalEntry = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE,
                    _nItemsPerPageConditionalEntry, _nDefaultItemsPerPage );

            LocalizedPaginator paginator = new LocalizedPaginator( field.getConditionalQuestions(  ),
                    _nItemsPerPageConditionalEntry,
                    AppPathService.getBaseUrl( request ) + JSP_MODIFY_FIELD + "?id_field=" + nIdField,
                    PARAMETER_PAGE_INDEX, _strCurrentPageIndexConditionalEntry, getLocale(  ) );

            model.put( MARK_ENTRY_TYPE_REF_LIST, refEntryType );
            model.put( MARK_PAGINATOR, paginator );
            model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + _nItemsPerPageEntry );
            model.put( MARK_ENTRY_LIST, paginator.getPageItems(  ) );
            model.put( MARK_NUMBER_ITEMS, paginator.getItemsCount(  ) );
            template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_FIELD_WITH_CONDITIONAL_QUESTION, locale, model );
        }

        setPageTitleProperty( PROPERTY_MODIFY_FIELD_TITLE );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Perform creation field
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doCreateField( HttpServletRequest request )
    {
        if ( ( request.getParameter( PARAMETER_CANCEL ) == null ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                    FormResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            IEntry entry = new Entry(  );
            entry.setIdEntry( _nIdEntry );

            Field field = new Field(  );
            field.setParentEntry( entry );

            String strError = getFieldData( request, field );

            if ( strError != null )
            {
                return strError;
            }

            FieldHome.create( field, getPlugin(  ) );
        }

        return getJspModifyEntry( request, _nIdEntry );
    }

    /**
     * Perform modification field
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doModifyField( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        Field field = null;
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = -1;

        if ( strIdField != null )
        {
            try
            {
                nIdField = Integer.parseInt( strIdField );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );
            }
        }

        if ( ( nIdField != -1 ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                    FormResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            field = FieldHome.findByPrimaryKey( nIdField, plugin );

            if ( request.getParameter( PARAMETER_CANCEL ) == null )
            {
                String strError = getFieldData( request, field );

                if ( strError != null )
                {
                    return strError;
                }

                FieldHome.update( field, getPlugin(  ) );
            }
        }
        else
        {
            return getJspManageForm( request );
        }

        if ( request.getParameter( PARAMETER_APPLY ) == null )
        {
            return getJspModifyEntry( request, field.getParentEntry(  ).getIdEntry(  ) );
        }
        else
        {
            return getJspModifyField( request, nIdField );
        }
    }

    /**
     * Gets the confirmation page of delete field
     * @param request The HTTP request
     * @return the confirmation page of delete field
     */
    public String getConfirmRemoveField( HttpServletRequest request )
    {
        if ( ( request.getParameter( PARAMETER_ID_FIELD ) == null ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                    FormResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getHomeUrl( request );
        }

        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        UrlItem url = new UrlItem( JSP_DO_REMOVE_FIELD );
        url.addParameter( PARAMETER_ID_FIELD, strIdField + "#list" );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_FIELD, url.getUrl(  ),
            AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform suppression field
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doRemoveField( HttpServletRequest request )
    {
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = -1;

        if ( ( strIdField == null ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                    FormResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getHomeUrl( request );
        }

        try
        {
            nIdField = Integer.parseInt( strIdField );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return getHomeUrl( request );
        }

        if ( nIdField != -1 )
        {
            FieldHome.remove( nIdField, getPlugin(  ) );

            return getJspModifyEntry( request, _nIdEntry );
        }

        return getJspManageForm( request );
    }

    /**
     * Move up the field
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doMoveUpField( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        List<Field> listField;
        Field field;
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = -1;

        if ( ( request.getParameter( PARAMETER_ID_FIELD ) == null ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                    FormResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getHomeUrl( request );
        }

        try
        {
            nIdField = Integer.parseInt( strIdField );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return getHomeUrl( request );
        }

        field = FieldHome.findByPrimaryKey( nIdField, plugin );

        listField = FieldHome.getFieldListByIdEntry( field.getParentEntry(  ).getIdEntry(  ), plugin );

        int nIndexField = FormUtils.getIndexFieldInTheFieldList( nIdField, listField );

        if ( nIndexField != 0 )
        {
            int nNewPosition;
            Field fieldToInversePosition;
            fieldToInversePosition = listField.get( nIndexField - 1 );
            nNewPosition = fieldToInversePosition.getPosition(  );
            fieldToInversePosition.setPosition( field.getPosition(  ) );
            field.setPosition( nNewPosition );
            FieldHome.update( field, plugin );
            FieldHome.update( fieldToInversePosition, plugin );
        }

        return getJspModifyEntry( request, _nIdEntry );
    }

    /**
     * Move down the field
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doMoveDownField( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        List<Field> listField;
        Field field;
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = -1;

        if ( ( request.getParameter( PARAMETER_ID_FIELD ) == null ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                    FormResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getHomeUrl( request );
        }

        try
        {
            nIdField = Integer.parseInt( strIdField );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return getHomeUrl( request );
        }

        field = FieldHome.findByPrimaryKey( nIdField, plugin );

        listField = FieldHome.getFieldListByIdEntry( field.getParentEntry(  ).getIdEntry(  ), plugin );

        int nIndexField = FormUtils.getIndexFieldInTheFieldList( nIdField, listField );

        if ( nIndexField != ( listField.size(  ) - 1 ) )
        {
            int nNewPosition;
            Field fieldToInversePosition;
            fieldToInversePosition = listField.get( nIndexField + 1 );
            nNewPosition = fieldToInversePosition.getPosition(  );
            fieldToInversePosition.setPosition( field.getPosition(  ) );
            field.setPosition( nNewPosition );
            FieldHome.update( field, plugin );
            FieldHome.update( fieldToInversePosition, plugin );
        }

        return getJspModifyEntry( request, _nIdEntry );
    }

    /**
     * Delete association between field and regular expression
     * @param request the Http Request
     * @return The URL to go after performing the action
     */
    public String doRemoveRegularExpression( HttpServletRequest request )
    {
        String strIdExpression = request.getParameter( PARAMETER_ID_EXPRESSION );
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = -1;
        int nIdExpression = -1;

        if ( ( strIdExpression != null ) && ( strIdField != null ) &&
                ( RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                    FormResourceIdService.PERMISSION_MODIFY, getUser(  ) ) ) )
        {
            try
            {
                nIdField = Integer.parseInt( strIdField );
                nIdExpression = Integer.parseInt( strIdExpression );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );
            }

            if ( ( nIdField != -1 ) && ( nIdExpression != -1 ) )
            {
                FieldHome.removeVerifyBy( nIdField, nIdExpression, getPlugin(  ) );
            }
        }

        return getJspModifyEntry( request, _nIdEntry );
    }

    /**
     * insert association between field and regular expression
     * @param request the Http Request
     * @return The URL to go after performing the action
     */
    public String doInsertRegularExpression( HttpServletRequest request )
    {
        String strIdExpression = request.getParameter( PARAMETER_ID_EXPRESSION );
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = -1;
        int nIdExpression = -1;

        if ( ( strIdExpression != null ) && ( strIdField != null ) &&
                ( RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                    FormResourceIdService.PERMISSION_MODIFY, getUser(  ) ) ) )
        {
            try
            {
                nIdField = Integer.parseInt( strIdField );
                nIdExpression = Integer.parseInt( strIdExpression );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );
            }

            if ( ( nIdField != -1 ) && ( nIdExpression != -1 ) )
            {
                FieldHome.createVerifyBy( nIdField, nIdExpression, getPlugin(  ) );
            }
        }

        return getJspModifyEntry( request, _nIdEntry );
    }

    /**
     * Gets the form test page
     * @param request the http request
     * @return the form test page
     */
    public String getTestForm( HttpServletRequest request )
    {
        if ( request.getParameter( PARAMETER_SESSION ) == null )
        {
            _listFormSubmitTest = new ArrayList<FormSubmit>(  );
        }

        Plugin plugin = getPlugin(  );
        HtmlTemplate template;

        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = -1;
        Form form;

        if ( ( strIdForm != null ) && !strIdForm.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdForm = Integer.parseInt( strIdForm );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getManageForm( request );
            }
        }

        if ( ( nIdForm == -1 ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_TEST,
                    getUser(  ) ) )
        {
            return getManageForm( request );
        }

        form = FormHome.findByPrimaryKey( nIdForm, plugin );

        Locale locale = getLocale(  );
        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_FORM, form );

        String strUrlAction = JSP_DO_TEST_FORM;

        if ( form.isSupportHTTPS(  ) && AppHTTPSService.isHTTPSSupportEnabled(  ) )
        {
            strUrlAction = AppHTTPSService.getHTTPSUrl( request ) + strUrlAction;
        }

        model.put( MARK_STR_FORM, FormUtils.getHtmlForm( form, strUrlAction, plugin, locale ) );
        model.put( MARK_EXPORT_FORMAT_REF_LIST, ExportFormatHome.getListExport( plugin ) );
        setPageTitleProperty( EMPTY_STRING );
        template = AppTemplateService.getTemplate( TEMPLATE_HTML_TEST_FORM, locale, model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * if there is no error perform in session the response of the form else return the error
     * @param request the http request
     * @return The URL to go after performing the action
     */
    public String doTestForm( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        List<IEntry> listEntryFirstLevel;
        EntryFilter filter;
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        String strRequirement = request.getParameter( PARAMETER_REQUIREMENT );
        String strErrorMessage = null;
        int nIdForm = -1;
        Form form;

        if ( ( strIdForm != null ) && !strIdForm.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdForm = Integer.parseInt( strIdForm );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );
            }
        }

        if ( ( nIdForm == -1 ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_TEST,
                    getUser(  ) ) )
        {
            return getManageForm( request );
        }

        form = FormHome.findByPrimaryKey( nIdForm, plugin );

        if ( form.isActiveRequirement(  ) && ( strRequirement == null ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_REQUIREMENT_ERROR, AdminMessage.TYPE_STOP );
        }

        if ( form.isActiveCaptcha(  ) && PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) )
        {
            CaptchaSecurityService captchaSecurityService = new CaptchaSecurityService(  );

            if ( !captchaSecurityService.validate( request ) )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_CAPTCHA_ERROR, AdminMessage.TYPE_STOP );
            }
        }

        filter = new EntryFilter(  );
        filter.setIdForm( nIdForm );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        filter.setIdIsComment( EntryFilter.FILTER_FALSE );
        listEntryFirstLevel = EntryHome.getEntryList( filter, plugin );

        Locale locale = getLocale(  );

        // create form response
        FormSubmit formSubmit = new FormSubmit(  );
        formSubmit.setForm( form );
        formSubmit.setDateResponse( FormUtils.getCurrentTimestamp(  ) );

        if ( form.isActiveStoreAdresse(  ) )
        {
            formSubmit.setIp( request.getRemoteAddr(  ) );
        }

        List<Response> listResponse = new ArrayList<Response>(  );
        formSubmit.setListResponse( listResponse );

        for ( IEntry entry : listEntryFirstLevel )
        {
            List<FormError> listFormError = FormUtils.getResponseEntry( request, entry.getIdEntry(  ), plugin,
                    formSubmit, false, locale );

            if ( ( listFormError != null ) && !listFormError.isEmpty(  ) )
            {
                // Only display the first error
                FormError formError = listFormError.get( 0 );

                if ( formError != null )
                {
                    if ( formError.isMandatoryError(  ) )
                    {
                        Object[] tabRequiredFields = { formError.getTitleQuestion(  ) };

                        strErrorMessage = AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_QUESTION,
                                tabRequiredFields, AdminMessage.TYPE_STOP );
                    }
                    else
                    {
                        Object[] tabRequiredFields = { formError.getTitleQuestion(  ), formError.getErrorMessage(  ) };

                        strErrorMessage = AdminMessageService.getMessageUrl( request, MESSAGE_FORM_ERROR,
                                tabRequiredFields, AdminMessage.TYPE_STOP );
                    }

                    return strErrorMessage;
                }
            }
        }

        _listFormSubmitTest.add( formSubmit );

        return getJspTestForm( request, nIdForm );
    }

    /**
     * write in the http response the export file of all response submit who are save during the test. if there is no response return a error
     * @param request the http request
     * @param response The http response
     * @return The URL to go after performing the action
     */
    public String doExportResponseTestForm( HttpServletRequest request, HttpServletResponse response )
    {
        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            Plugin plugin = getPlugin(  );
            Locale locale = getLocale(  );
            String strIdForm = request.getParameter( PARAMETER_ID_FORM );
            String strIdExportFormat = request.getParameter( PARAMETER_ID_EXPORT_FORMAT );
            int nIdForm = -1;
            int nIdExportFormat = -1;

            Form form;
            ExportFormat exportFormat;

            if ( ( strIdForm != null ) && ( strIdExportFormat != null ) && !strIdForm.equals( EMPTY_STRING ) &&
                    !strIdExportFormat.equals( EMPTY_STRING ) )
            {
                try
                {
                    nIdForm = Integer.parseInt( strIdForm );
                    nIdExportFormat = Integer.parseInt( strIdExportFormat );
                }
                catch ( NumberFormatException ne )
                {
                    AppLogService.error( ne );

                    return getManageForm( request );
                }
            }

            if ( ( nIdForm == -1 ) || ( nIdExportFormat == -1 ) ||
                    !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_TEST,
                        getUser(  ) ) )
            {
                return getManageForm( request );
            }

            exportFormat = ExportFormatHome.findByPrimaryKey( nIdExportFormat, plugin );
            form = FormHome.findByPrimaryKey( nIdForm, plugin );

            if ( ( _listFormSubmitTest != null ) && ( _listFormSubmitTest.size(  ) != 0 ) )
            {
                XmlTransformerService xmlTransformerService = new XmlTransformerService(  );
                String strXmlSource = XmlUtil.getXmlHeader(  ) +
                    FormUtils.getXmlResponses( request, form, _listFormSubmitTest, locale, plugin );
                String strXslUniqueId = XSL_UNIQUE_PREFIX_ID + nIdExportFormat;
                String strFileOutPut = xmlTransformerService.transformBySourceWithXslCache( strXmlSource,
                        exportFormat.getXsl(  ), strXslUniqueId, null, null );

                byte[] byteFileOutPut = strFileOutPut.getBytes(  );

                try
                {
                    String strFormatExtension = exportFormat.getExtension(  ).trim(  );
                    String strFileName = form.getTitle(  ) + "." + strFormatExtension;
                    FormUtils.addHeaderResponse( request, response, strFileName, strFormatExtension );
                    response.setContentLength( byteFileOutPut.length );

                    OutputStream os = response.getOutputStream(  );
                    os.write( byteFileOutPut );
                    os.close(  );
                }
                catch ( IOException e )
                {
                    AppLogService.error( e );
                }
            }
            else
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_NO_RESPONSE, AdminMessage.TYPE_STOP );
            }
        }

        return getJspManageForm( request );
    }

    /**
     * Gets the form result page
     * @param request the http request
     * @return the form test page
     */
    public String getResult( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        Locale locale = getLocale(  );
        HtmlTemplate template;
        ResponseFilter filter = new ResponseFilter(  );
        int nNumberResponse = 0;
        Date dFistResponseDate = null;
        Date dLastResponseDate = null;
        int nIdForm = -1;
        Form form;

        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        String strFistResponseDateFilter = request.getParameter( PARAMETER_FIRST_RESPONSE_DATE_FILTER );
        String strLastResponseDateFilter = request.getParameter( PARAMETER_LAST_RESPONSE_DATE_FILTER );
        String strTimesUnit = request.getParameter( PARAMETER_TIMES_UNIT );

        Timestamp tFistResponseDateFilter = null;
        Timestamp tLastResponseDateFilter = null;

        if ( strFistResponseDateFilter != null )
        {
            tFistResponseDateFilter = FormUtils.getDateFirstMinute( DateUtil.formatDate( strFistResponseDateFilter,
                        locale ), locale );
        }

        if ( strLastResponseDateFilter != null )
        {
            tLastResponseDateFilter = FormUtils.getDateLastMinute( DateUtil.formatDate( strLastResponseDateFilter,
                        locale ), locale );
        }

        if ( ( strIdForm != null ) && !strIdForm.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdForm = Integer.parseInt( strIdForm );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getManageForm( request );
            }
        }

        if ( ( nIdForm == -1 ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_VIEW_RESULT,
                    getUser(  ) ) )
        {
            return getManageForm( request );
        }

        form = FormHome.findByPrimaryKey( nIdForm, plugin );
        filter.setIdForm( nIdForm );
        filter.setDateFirst( tFistResponseDateFilter );
        filter.setDateLast( tLastResponseDateFilter );

        List<FormSubmit> listFormSubmit = FormSubmitHome.getFormSubmitList( filter, plugin );

        nNumberResponse = listFormSubmit.size(  );

        if ( nNumberResponse != 0 )
        {
            dFistResponseDate = new Date( listFormSubmit.get( 0 ).getDateResponse(  ).getTime(  ) );
            dLastResponseDate = new Date( listFormSubmit.get( nNumberResponse - 1 ).getDateResponse(  ).getTime(  ) );
        }

        if ( strTimesUnit != null )
        {
            if ( strTimesUnit.equals( FormUtils.CONSTANT_GROUP_BY_DAY ) )
            {
                filter.setGroupbyDay( true );
            }
            else if ( strTimesUnit.equals( FormUtils.CONSTANT_GROUP_BY_WEEK ) )
            {
                filter.setGroupbyWeek( true );
            }
            else if ( strTimesUnit.equals( FormUtils.CONSTANT_GROUP_BY_MONTH ) )
            {
                filter.setGroupbyMonth( true );
            }
        }
        else
        {
            filter.setGroupbyDay( true );
            strTimesUnit = FormUtils.CONSTANT_GROUP_BY_DAY;
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage(  ) );
        model.put( MARK_FORM, form );
        model.put( MARK_NUMBER_RESPONSE, nNumberResponse );
        model.put( MARK_FIRST_RESPONSE_DATE_FILTER,
            ( tFistResponseDateFilter == null ) ? null : new Date( tFistResponseDateFilter.getTime(  ) ) );
        model.put( MARK_FIRST_RESPONSE_DATE, dFistResponseDate );
        model.put( MARK_LAST_RESPONSE_DATE_FILTER,
            ( tLastResponseDateFilter == null ) ? null : new Date( tLastResponseDateFilter.getTime(  ) ) );
        model.put( MARK_LAST_RESPONSE_DATE, dLastResponseDate );
        model.put( MARK_TIMES_UNIT, strTimesUnit );
        model.put( MARK_EXPORT_FORMAT_REF_LIST, ExportFormatHome.getListExport( plugin ) );
        setPageTitleProperty( PROPERTY_RESULT_PAGE_TITLE );
        template = AppTemplateService.getTemplate( TEMPLATE_RESULT, locale, model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * write in the http response the export file of all response submit who verify the date filter if there is no response return a error
     * @param request the http request
     * @param response The http response
     * @return The URL to go after performing the action
     */
    public String doExportResult( HttpServletRequest request, HttpServletResponse response )
    {
        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            Plugin plugin = getPlugin(  );
            Locale locale = getLocale(  );
            String strIdForm = request.getParameter( PARAMETER_ID_FORM );
            String strIdExportFormat = request.getParameter( PARAMETER_ID_EXPORT_FORMAT );
            String strFistResponseDateFilter = request.getParameter( PARAMETER_FIRST_RESPONSE_DATE_FILTER );
            String strLastResponseDateFilter = request.getParameter( PARAMETER_LAST_RESPONSE_DATE_FILTER );
            Timestamp tFistResponseDateFilter = null;
            Timestamp tLastResponseDateFilter = null;

            if ( strFistResponseDateFilter != null )
            {
                tFistResponseDateFilter = FormUtils.getDateFirstMinute( DateUtil.formatDate( 
                            strFistResponseDateFilter, locale ), locale );
            }

            if ( strLastResponseDateFilter != null )
            {
                tLastResponseDateFilter = FormUtils.getDateLastMinute( DateUtil.formatDate( strLastResponseDateFilter,
                            locale ), locale );
            }

            int nIdForm = -1;
            int nIdExportFormat = -1;
            ResponseFilter filter = new ResponseFilter(  );
            Form form;
            ExportFormat exportFormat;

            if ( ( strIdForm != null ) && ( strIdExportFormat != null ) && !strIdForm.equals( EMPTY_STRING ) &&
                    !strIdExportFormat.equals( EMPTY_STRING ) )
            {
                try
                {
                    nIdForm = Integer.parseInt( strIdForm );
                    nIdExportFormat = Integer.parseInt( strIdExportFormat );
                }
                catch ( NumberFormatException ne )
                {
                    AppLogService.error( ne );

                    return getManageForm( request );
                }
            }

            if ( ( nIdForm == -1 ) || ( nIdExportFormat == -1 ) ||
                    !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm,
                        FormResourceIdService.PERMISSION_VIEW_RESULT, getUser(  ) ) )
            {
                return getManageForm( request );
            }

            exportFormat = ExportFormatHome.findByPrimaryKey( nIdExportFormat, plugin );
            form = FormHome.findByPrimaryKey( nIdForm, plugin );
            filter.setIdForm( nIdForm );
            filter.setDateFirst( tFistResponseDateFilter );
            filter.setDateLast( tLastResponseDateFilter );

            List<FormSubmit> listFormSubmit = FormSubmitHome.getFormSubmitList( filter, plugin );

            for ( FormSubmit formSubmit : listFormSubmit )
            {
                filter = new ResponseFilter(  );
                filter.setIdForm( formSubmit.getIdFormSubmit(  ) );
                filter.setOrderBy( SQL_FILTER_ENTRY_POS );
                filter.setOrderByAsc( true );
                formSubmit.setListResponse( _responseService.getResponseList( filter, false ) );
            }

            if ( ( listFormSubmit != null ) && ( listFormSubmit.size(  ) != 0 ) )
            {
                XmlTransformerService xmlTransformerService = new XmlTransformerService(  );
                String strXmlSource = XmlUtil.getXmlHeader(  ) +
                    FormUtils.getXmlResponses( request, form, listFormSubmit, locale, plugin );
                String strXslUniqueId = XSL_UNIQUE_PREFIX_ID + nIdExportFormat;
                String strFileOutPut = xmlTransformerService.transformBySourceWithXslCache( strXmlSource,
                        exportFormat.getXsl(  ), strXslUniqueId, null, null );

                String strFormatExtension = exportFormat.getExtension(  ).trim(  );
                String strFileName = form.getTitle(  ) + "." + strFormatExtension;
                FormUtils.addHeaderResponse( request, response, strFileName, strFormatExtension );

                PrintWriter out = null;

                try
                {
                    out = response.getWriter(  );
                    out.write( strFileOutPut );
                }
                catch ( IOException e )
                {
                    AppLogService.error( e );
                }
                finally
                {
                    if ( out != null )
                    {
                        out.flush(  );
                        out.close(  );
                    }
                }
            }
            else
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_NO_RESPONSE, AdminMessage.TYPE_STOP );
            }
        }

        return getJspManageForm( request );
    }

    /**
     * write in the http response the statistic graph of all response submit who verify the date filter
     * @param request the http request
     * @param response The http response
     *
     */
    public void doGenerateGraph( HttpServletRequest request, HttpServletResponse response )
    {
        Plugin plugin = getPlugin(  );
        Locale locale = getLocale(  );
        ResponseFilter filter = new ResponseFilter(  );
        int nIdForm = -1;
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        String strFistResponseDateFilter = request.getParameter( PARAMETER_FIRST_RESPONSE_DATE_FILTER );
        String strLastResponseDateFilter = request.getParameter( PARAMETER_LAST_RESPONSE_DATE_FILTER );
        String strTimesUnit = request.getParameter( PARAMETER_TIMES_UNIT );
        Timestamp tFistResponseDateFilter = null;
        Timestamp tLastResponseDateFilter = null;

        if ( strFistResponseDateFilter != null )
        {
            tFistResponseDateFilter = FormUtils.getDateFirstMinute( DateUtil.formatDate( strFistResponseDateFilter,
                        locale ), locale );
        }

        if ( strLastResponseDateFilter != null )
        {
            tLastResponseDateFilter = FormUtils.getDateLastMinute( DateUtil.formatDate( strLastResponseDateFilter,
                        locale ), locale );
        }

        if ( ( strIdForm != null ) && !strIdForm.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdForm = Integer.parseInt( strIdForm );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );
            }
        }

        filter.setIdForm( nIdForm );
        filter.setDateFirst( tFistResponseDateFilter );
        filter.setDateLast( tLastResponseDateFilter );

        if ( strTimesUnit != null )
        {
            if ( strTimesUnit.equals( FormUtils.CONSTANT_GROUP_BY_DAY ) )
            {
                filter.setGroupbyDay( true );
            }
            else if ( strTimesUnit.equals( FormUtils.CONSTANT_GROUP_BY_WEEK ) )
            {
                filter.setGroupbyWeek( true );
            }
            else if ( strTimesUnit.equals( FormUtils.CONSTANT_GROUP_BY_MONTH ) )
            {
                filter.setGroupbyMonth( true );
            }
        }
        else
        {
            filter.setGroupbyDay( true );
            strTimesUnit = FormUtils.CONSTANT_GROUP_BY_DAY;
        }

        List<StatisticFormSubmit> listStatisticResult = FormSubmitHome.getStatisticFormSubmit( filter, plugin );

        String strNumberOfResponseAxisX = AppPropertiesService.getProperty( PROPERTY_NUMBER_RESPONSE_AXIS_X );
        int nNumberOfResponseAxisX = 10;

        try
        {
            nNumberOfResponseAxisX = Integer.parseInt( strNumberOfResponseAxisX );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        List<StatisticFormSubmit> listStatisticGraph = new ArrayList<StatisticFormSubmit>(  );
        StatisticFormSubmit statisticFormSubmit;

        if ( listStatisticResult.size(  ) != 0 )
        {
            for ( int cpt = 0; cpt < nNumberOfResponseAxisX; cpt++ )
            {
                statisticFormSubmit = new StatisticFormSubmit(  );
                statisticFormSubmit.setNumberResponse( 0 );
                statisticFormSubmit.setStatisticDate( FormUtils.addStatisticInterval( 
                        listStatisticResult.get( 0 ).getStatisticDate(  ), strTimesUnit, cpt ) );
                listStatisticGraph.add( statisticFormSubmit );
            }
        }

        for ( StatisticFormSubmit statisticFormSubmitGraph : listStatisticGraph )
        {
            for ( StatisticFormSubmit statisticFormSubmitResult : listStatisticResult )
            {
                if ( FormUtils.sameDate( statisticFormSubmitGraph.getStatisticDate(  ),
                            statisticFormSubmitResult.getStatisticDate(  ), strTimesUnit ) )
                {
                    statisticFormSubmitGraph.setNumberResponse( statisticFormSubmitResult.getNumberResponse(  ) );
                }
            }
        }

        String strLabelAxisX = I18nService.getLocalizedString( PROPERTY_LABEL_AXIS_X, locale );
        String strLabelAxisY = I18nService.getLocalizedString( PROPERTY_LABEL_AXIS_Y, locale );

        JFreeChart chart = FormUtils.createXYGraph( listStatisticGraph, strLabelAxisX, strLabelAxisY, strTimesUnit );

        try
        {
            ChartRenderingInfo info = new ChartRenderingInfo( new StandardEntityCollection(  ) );
            BufferedImage chartImage = chart.createBufferedImage( 600, 200, info );
            response.setContentType( "image/PNG" );

            PngEncoder encoder = new PngEncoder( chartImage, false, 0, 9 );
            response.getOutputStream(  ).write( encoder.pngEncode(  ) );
            response.getOutputStream(  ).close(  );
        }
        catch ( Exception e )
        {
            AppLogService.error( e );
        }
    }

    /**
     * write in the http response the value of the response whose identifier is specified in the request if there is no response return a error
     * @param request the http request
     * @param response The http response
     * @return The URL to go after performing the action
     */
    public String doDownloadFile( HttpServletRequest request, HttpServletResponse response )
    {
        AdminUser adminUser = getUser(  );
        Response responseFile = null;
        String strIdResponse = request.getParameter( PARAMETER_ID_RESPONSE );
        Form form;
        int nIdResponse = -1;

        if ( strIdResponse != null )
        {
            try
            {
                nIdResponse = Integer.parseInt( strIdResponse );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );
            }
        }

        Plugin plugin = getPlugin(  );
        responseFile = _responseService.findByPrimaryKey( nIdResponse, true );

        if ( responseFile != null )
        {
            // is authoried to view result
            FormSubmit formSubmit = FormSubmitHome.findByPrimaryKey( responseFile.getFormSubmit(  ).getIdFormSubmit(  ),
                    plugin );

            List<Form> listForm = new ArrayList<Form>(  );

            if ( formSubmit != null )
            {
                form = FormHome.findByPrimaryKey( formSubmit.getForm(  ).getIdForm(  ), plugin );
                listForm.add( form );
            }

            listForm = (List) AdminWorkgroupService.getAuthorizedCollection( listForm, adminUser );

            if ( ( listForm.size(  ) == 0 ) ||
                    ( ( listForm.size(  ) != 0 ) &&
                    !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + listForm.get( 0 ).getIdForm(  ),
                        FormResourceIdService.PERMISSION_VIEW_RESULT, getUser(  ) ) ) )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_YOU_ARE_NOT_ALLOWED_TO_DOWLOAD_THIS_FILE,
                    AdminMessage.TYPE_STOP );
            }

            if ( ( responseFile.getFile(  ) != null ) && ( responseFile.getFile(  ).getPhysicalFile(  ) != null ) &&
                    ( responseFile.getFile(  ).getPhysicalFile(  ).getValue(  ) != null ) )
            {
                try
                {
                    byte[] byteFileOutPut = responseFile.getFile(  ).getPhysicalFile(  ).getValue(  );
                    FormUtils.addHeaderResponse( request, response, responseFile.getFile(  ).getTitle(  ),
                        FilenameUtils.getExtension( responseFile.getFile(  ).getTitle(  ) ) );
                    response.setContentLength( byteFileOutPut.length );

                    OutputStream os = response.getOutputStream(  );
                    os.write( byteFileOutPut );
                    os.close(  );
                }
                catch ( IOException e )
                {
                    AppLogService.error( e );
                }
            }
            else
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_DURING_DOWNLOAD_FILE,
                    AdminMessage.TYPE_STOP );
            }
        }

        return getJspManageForm( request );
    }

    /**
     * Gets the form modification page
     * @param request The HTTP request
     * @return The form modification page
     */
    public String getManageOutputProcessor( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = -1;
        Form form;

        if ( ( strIdForm != null ) && !strIdForm.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdForm = Integer.parseInt( strIdForm );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getManageForm( request );
            }
        }

        form = FormHome.findByPrimaryKey( nIdForm, getPlugin(  ) );

        if ( ( form == null ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm,
                    FormResourceIdService.PERMISSION_MANAGE_OUTPUT_PROCESSOR, getUser(  ) ) )
        {
            return getManageForm( request );
        }

        HashMap<String, Object> model = new HashMap<String, Object>(  );

        List<HashMap<String, Object>> listProcess = new ArrayList<HashMap<String, Object>>(  );

        HashMap<String, Object> hasMaProcess;
        Collection<IOutputProcessor> lisOutputProcessor = OutputProcessorService.getInstance(  ).getAllProcessors(  );

        for ( IOutputProcessor processor : lisOutputProcessor )
        {
            hasMaProcess = new HashMap<String, Object>(  );
            hasMaProcess.put( MARK_PROCESSOR_KEY, processor.getKey(  ) );
            hasMaProcess.put( MARK_PROCESSOR_CONFIGURATION,
                processor.getOutputConfigForm( request, form, getLocale(  ), getPlugin(  ) ) );
            hasMaProcess.put( MARK_IS_SELECTED,
                OutputProcessorService.getInstance(  ).isUsed( nIdForm, processor.getKey(  ) ) );

            listProcess.add( hasMaProcess );
        }

        model.put( MARK_PROCESSOR_LIST, listProcess );
        model.put( MARK_FORM, form );
        setPageTitleProperty( PROPERTY_MANAGE_OUTPUT_PROCESSOR_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_OUTPUT_PROCESSOR, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Perform the form modification
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doManageOutputProcessor( HttpServletRequest request )
    {
        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            Plugin plugin = getPlugin(  );
            String strIdForm = request.getParameter( PARAMETER_ID_FORM );
            String strIsSelected = request.getParameter( PARAMETER_IS_SELECTED );
            String strProcessorKey = request.getParameter( PARAMETER_PROCESSOR_KEY );

            int nIdForm = -1;
            Form form;

            if ( ( strIdForm != null ) && !strIdForm.equals( EMPTY_STRING ) )
            {
                try
                {
                    nIdForm = Integer.parseInt( strIdForm );
                }
                catch ( NumberFormatException ne )
                {
                    AppLogService.error( ne );
                }
            }

            form = FormHome.findByPrimaryKey( nIdForm, plugin );

            if ( ( form == null ) ||
                    !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm,
                        FormResourceIdService.PERMISSION_MANAGE_OUTPUT_PROCESSOR, getUser(  ) ) )
            {
                return getJspManageForm( request );
            }

            if ( strProcessorKey != null )
            {
                IOutputProcessor processor = OutputProcessorService.getInstance(  ).getProcessorByKey( strProcessorKey );

                if ( processor != null )
                {
                    if ( ( strIsSelected != null ) || ( request.getParameter( PARAMETER_ACTION_REDIRECT ) != null ) )
                    {
                        String strError = processor.doOutputConfigForm( request, getLocale(  ), getPlugin(  ) );

                        if ( ( strError != null ) && ( request.getParameter( PARAMETER_ACTION_REDIRECT ) != null ) )
                        {
                            return strError;
                        }
                        else if ( strError != null )
                        {
                            return AdminMessageService.getMessageUrl( request, strError, AdminMessage.TYPE_STOP );
                        }

                        if ( !OutputProcessorService.getInstance(  ).isUsed( nIdForm, processor.getKey(  ) ) )
                        {
                            OutputProcessorService.getInstance(  ).addProcessorAssociation( nIdForm,
                                processor.getKey(  ) );
                        }
                    }

                    else
                    {
                        if ( OutputProcessorService.getInstance(  ).isUsed( nIdForm, processor.getKey(  ) ) )
                        {
                            OutputProcessorService.getInstance(  )
                                                  .removeProcessorAssociation( nIdForm, processor.getKey(  ) );
                        }
                    }
                }
            }
        }

        return getJspManageForm( request );
    }

    /**
     * Gets the form messages modification page
     * @param request The HTTP request
     * @return the form messages modification page
     */
    public String getModifyMessage( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_MODIFY,
                    getUser(  ) ) )
        {
            return getManageForm( request );
        }

        Form form = null;

        if ( ( strIdForm != null ) && !strIdForm.trim(  ).equals( EMPTY_STRING ) )
        {
            int nIdForm = -1;

            try
            {
                nIdForm = Integer.parseInt( strIdForm );
                _nIdForm = nIdForm;
            }
            catch ( NumberFormatException nfe )
            {
                AppLogService.error( nfe );
            }

            if ( nIdForm != -1 )
            {
                form = FormHome.findByPrimaryKey( nIdForm, getPlugin(  ) );
            }
        }

        if ( form == null )
        {
            return getManageForm( request );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage(  ) );
        model.put( MARK_FORM, form );

        setPageTitleProperty( PROPERTY_MODIFY_MESSAGE_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_MESSAGE, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Perform the messages form modification
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doModifyMessage( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( ( request.getParameter( PARAMETER_CANCEL ) == null ) &&
                RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_MODIFY,
                    getUser(  ) ) )
        {
            Form form = null;

            if ( ( strIdForm != null ) && !strIdForm.trim(  ).equals( EMPTY_STRING ) )
            {
                int nIdForm = -1;

                try
                {
                    nIdForm = Integer.parseInt( strIdForm );
                }
                catch ( NumberFormatException nfe )
                {
                    AppLogService.error( nfe );
                }

                if ( nIdForm != -1 )
                {
                    form = FormHome.findByPrimaryKey( nIdForm, getPlugin(  ) );
                }
            }

            if ( form != null )
            {
                String strWelcomeMessage = request.getParameter( PARAMETER_WELCOME_MESSAGE );
                String strUnavailabilityMessage = request.getParameter( PARAMETER_UNAVAILABILITY_MESSAGE );
                String strRequirement = request.getParameter( PARAMETER_REQUIREMENT );
                String strLibelleValidateButton = request.getParameter( PARAMETER_LIBELLE_VALIDATE_BUTTON );
                String strLibelleResetButton = request.getParameter( PARAMETER_LIBELLE_RESET_BUTTON );

                String strFieldError = EMPTY_STRING;

                if ( ( strUnavailabilityMessage == null ) || strUnavailabilityMessage.trim(  ).equals( EMPTY_STRING ) )
                {
                    strFieldError = FIELD_UNAVAILABILITY_MESSAGE;
                }
                else if ( ( strRequirement == null ) || strRequirement.trim(  ).equals( EMPTY_STRING ) )
                {
                    strFieldError = FIELD_REQUIREMENT;
                }
                else if ( ( strLibelleValidateButton == null ) ||
                        strLibelleValidateButton.trim(  ).equals( EMPTY_STRING ) )
                {
                    strFieldError = FIELD_LIBELE_VALIDATE_BUTTON;
                }

                if ( !strFieldError.equals( EMPTY_STRING ) )
                {
                    Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, getLocale(  ) ) };

                    return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                        AdminMessage.TYPE_STOP );
                }

                form.setWelcomeMessage( strWelcomeMessage );
                form.setUnavailabilityMessage( strUnavailabilityMessage );
                form.setRequirement( strRequirement );
                form.setLibelleValidateButton( strLibelleValidateButton );
                form.setLibelleResetButton( strLibelleResetButton );

                FormHome.update( form, getPlugin(  ) );
            }
        }

        return getJspManageForm( request );
    }

    /**
     * Gets the form manage validators page
     * @param request The HTTP request
     * @return The form manage validators page
     */
    public String getManageValidator( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = -1;

        if ( StringUtils.isNotBlank( strIdForm ) )
        {
            try
            {
                nIdForm = Integer.parseInt( strIdForm );
            }
            catch ( NumberFormatException nfe )
            {
                AppLogService.error( nfe );

                return getManageForm( request );
            }
        }

        Form form = FormHome.findByPrimaryKey( nIdForm, getPlugin(  ) );

        if ( ( form == null ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm,
                    FormResourceIdService.PERMISSION_MANAGE_VALIDATOR, getUser(  ) ) )
        {
            return getManageForm( request );
        }

        Collection<String> colValidators = new ArrayList<String>(  );

        for ( IValidator validator : ValidatorService.getInstance(  ).getAllValidators(  ) )
        {
            colValidators.add( validator.getUI( request, form.getIdForm(  ) ) );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_VALIDATOR_LIST, colValidators );
        model.put( MARK_FORM, form );

        setPageTitleProperty( PROPERTY_MANAGE_VALIDATOR_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_VALIDATOR, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Gets the URL of the form manage validators JSP
     *
     * @param request The HTTP request
     * @param nIdForm The form identifier
     * @return The URL of the form manage validators JSP
     */
    public String getJspManageValidator( HttpServletRequest request, int nIdForm )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_VALIDATOR_FORM + QUESTION_MARK_STRING +
        PARAMETER_ID_FORM + EQUAL_STRING + nIdForm;
    }

    /**
     * Modify form export parameter default values
     * @param request HttpServletRequest
     * @return JSP return
     * @throws AccessDeniedException
     */
    public String doModifyExportParameters( HttpServletRequest request )
        throws AccessDeniedException
    {
        if ( !RBACService.isAuthorized( Form.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    FormResourceIdService.PERMISSION_MANAGE_ADVANCED_PARAMETERS, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        ReferenceList listParams = FormParameterService.getService(  ).findExportParameters(  );

        for ( ReferenceItem param : listParams )
        {
            String strParamValue = request.getParameter( param.getCode(  ) );

            if ( StringUtils.isNotBlank( strParamValue ) )
            {
                if ( FormParameterService.getService(  ).isExportEncodingParameter( param.getCode(  ) ) )
                {
                    // Test if the encoding is supported
                    try
                    {
                        strParamValue.getBytes( strParamValue );
                    }
                    catch ( UnsupportedEncodingException e )
                    {
                        Object[] tabRequiredFields = { strParamValue };

                        return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_EXPORT_ENCODING_NOT_SUPPORTED,
                            tabRequiredFields, AdminMessage.TYPE_STOP );
                    }
                }
            }
            else
            {
                return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
            }

            param.setName( strParamValue );
            FormParameterService.getService(  ).update( param );
        }

        return getJspManageAdvancedParameters( request );
    }

    /**
     * Change the attribute's order (move up or move down in the list)
     * @param request the request
     * @return The URL of the form management page
     */
    public String doChangeOrderEntry( HttpServletRequest request )
    {
        //gets the entry which needs to be changed (order)
        Plugin plugin = getPlugin(  );

        String strEntryId = StringUtils.EMPTY;
        String strOrderToSet = StringUtils.EMPTY;
        Integer nEntryId = 0;
        Integer nOrderToSet = 0;
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = FormUtils.convertStringToInt( strIdForm );
        IEntry entry;

        // To execute mass action "Move into"
        if ( request.getParameter( PARAMETER_MOVE_BUTTON + ".x" ) != null )
        {
            String strIdNewParent = request.getParameter( PARAMETER_ID_ENTRY_GROUP );
            Integer nIdNewParent = 0;

            if ( StringUtils.isNotBlank( strIdNewParent ) )
            {
                nIdNewParent = FormUtils.convertStringToInt( strIdNewParent );
            }

            // gets the entries which needs to be changed
            String[] entryToMoveList = request.getParameterValues( PARAMETER_ENTRY_ID );

            IEntry entryParent = EntryHome.findByPrimaryKey( nIdNewParent, plugin );
            List<IEntry> listEntry = new ArrayList<IEntry>(  );

            if ( entryParent == null )
            {
                EntryFilter filter = new EntryFilter(  );
                filter.setIdForm( nIdForm );
                filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
                listEntry = EntryHome.getEntryList( filter, plugin );
            }

            Integer nListEntrySize = listEntry.size(  );

            if ( entryToMoveList != null )
            {
                // for each entry, move it into selected group
                for ( String strIdEntryToMove : entryToMoveList )
                {
                    IEntry entryToMove = EntryHome.findByPrimaryKey( FormUtils.convertStringToInt( strIdEntryToMove ),
                            plugin );
                    entryParent = EntryHome.findByPrimaryKey( nIdNewParent, plugin );

                    if ( ( entryToMove == null ) )
                    {
                        return AdminMessageService.getMessageUrl( request, MESSAGE_SELECT_GROUP, AdminMessage.TYPE_STOP );
                    }

                    // if entryParent is null, move out selected entries
                    if ( ( entryParent == null ) && ( entryToMove.getParent(  ) != null ) )
                    {
                        doMoveOutEntry( plugin, nIdForm, nListEntrySize, entryToMove );
                    }

                    // Move entry into group if not already in
                    else if ( ( entryParent != null ) &&
                            ( ( entryToMove.getParent(  ) == null ) ||
                            ( ( entryToMove.getParent(  ) != null ) &&
                            ( entryToMove.getParent(  ).getIdEntry(  ) != entryParent.getIdEntry(  ) ) ) ) )
                    {
                        this.doMoveEntryIntoGroup( plugin, entryToMove, entryParent );
                    }
                }
            }
        }

        // To change order of one entry
        else
        {
            EntryFilter filter = new EntryFilter(  );
            filter.setIdForm( nIdForm );

            List<IEntry> entryList = EntryHome.getEntryList( filter, getPlugin(  ) );

            for ( int i = 0; i < entryList.size(  ); i++ )
            {
                entry = entryList.get( i );
                nEntryId = entry.getIdEntry(  );
                strEntryId = request.getParameter( PARAMETER_MOVE_BUTTON + "_" + nEntryId.toString(  ) );

                if ( StringUtils.isNotBlank( strEntryId ) )
                {
                    strEntryId = nEntryId.toString(  );
                    strOrderToSet = request.getParameter( PARAMETER_ORDER_ID + "_" + nEntryId.toString(  ) );
                    i = entryList.size(  );
                }
            }

            if ( StringUtils.isNotBlank( strEntryId ) )
            {
                nEntryId = FormUtils.convertStringToInt( strEntryId );
            }

            if ( StringUtils.isNotBlank( strOrderToSet ) )
            {
                nOrderToSet = FormUtils.convertStringToInt( strOrderToSet );
            }

            IEntry entryToChangeOrder = EntryHome.findByPrimaryKey( nEntryId, plugin );
            int nActualOrder = entryToChangeOrder.getPosition( );

            // does nothing if the order to set is equal to the actual order
            if ( nOrderToSet != nActualOrder )
            {
                // entry goes up in the list 
                if ( nOrderToSet < entryToChangeOrder.getPosition( ) )
                {
                    moveUpEntryOrder( plugin, nOrderToSet, entryToChangeOrder, entryToChangeOrder.getForm( )
                            .getIdForm( ) );
                }

                // entry goes down in the list
                else
                {
                    moveDownEntryOrder( plugin, nOrderToSet, entryToChangeOrder, entryToChangeOrder.getForm( )
                            .getIdForm( ) );
                }
            }
        }

        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MODIFY_FORM );
        url.addParameter( PARAMETER_ID_FORM, nIdForm );
        url.setAnchor( PARAMETER_LIST );

        return url.getUrl(  );
    }

    /**
     * Change the attribute's order to a greater one (move down in the list)
     * @param plugin the plugin
     * @param nOrderToSet the new order for the attribute
     * @param entryToChangeOrder the attribute which will change
     * @param nIdForm the id of the form
     */
    private void moveDownEntryOrder( Plugin plugin, int nOrderToSet, IEntry entryToChangeOrder, int nIdForm )
    {
        if ( entryToChangeOrder.getParent(  ) == null )
        {
            int nNbChild = 0;
            int nNewOrder = 0;

            EntryFilter filter = new EntryFilter(  );
            filter.setIdForm( nIdForm );
            filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
            filter.setFieldDependNull( EntryFilter.FILTER_TRUE );

            List<IEntry> listEntryFirstLevel = EntryHome.findEntriesWithoutParent( plugin,
                    entryToChangeOrder.getForm(  ).getIdForm(  ) );

            List<Integer> orderFirstLevel = new ArrayList<Integer>(  );
            initOrderFirstLevel( listEntryFirstLevel, orderFirstLevel );

            Integer nbChildEntryToChangeOrder = 0;

            if ( entryToChangeOrder.getChildren(  ) != null )
            {
                nbChildEntryToChangeOrder = entryToChangeOrder.getChildren(  ).size(  );
            }

            for ( IEntry entry : listEntryFirstLevel )
            {
                for ( int i = 0; i < orderFirstLevel.size(  ); i++ )
                {
                    if ( ( orderFirstLevel.get( i ) == entry.getPosition(  ) ) &&
                            ( entry.getPosition(  ) > entryToChangeOrder.getPosition(  ) ) &&
                            ( entry.getPosition(  ) <= nOrderToSet ) )
                    {
                        if ( nNbChild == 0 )
                        {
                            nNewOrder = orderFirstLevel.get( i - 1 );

                            if ( orderFirstLevel.get( i - 1 ) != entryToChangeOrder.getPosition(  ) )
                            {
                                nNewOrder -= nbChildEntryToChangeOrder;
                            }
                        }
                        else
                        {
                            nNewOrder += ( nNbChild + 1 );
                        }

                        entry.setPosition( nNewOrder );
                        EntryHome.update( entry, plugin );
                        nNbChild = 0;

                        if ( entry.getChildren(  ) != null )
                        {
                            for ( IEntry child : entry.getChildren(  ) )
                            {
                                nNbChild++;
                                child.setPosition( nNewOrder + nNbChild );
                                EntryHome.update( child, plugin );
                            }
                        }
                    }
                }
            }

            entryToChangeOrder.setPosition( nNewOrder + nNbChild + 1 );
            EntryHome.update( entryToChangeOrder, plugin );
            nNbChild = 0;

            for ( IEntry child : entryToChangeOrder.getChildren(  ) )
            {
                nNbChild++;
                child.setPosition( entryToChangeOrder.getPosition(  ) + nNbChild );
                EntryHome.update( child, plugin );
            }
        }
        else
        {
            EntryFilter filter = new EntryFilter(  );
            filter.setIdForm( nIdForm );
            filter.setFieldDependNull( EntryFilter.FILTER_TRUE );

            List<IEntry> listAllEntry = EntryHome.getEntryList( filter, plugin );

            for ( IEntry entry : listAllEntry )
            {
                if ( ( entry.getPosition(  ) > entryToChangeOrder.getPosition(  ) ) &&
                        ( entry.getPosition(  ) <= nOrderToSet ) )
                {
                    entry.setPosition( entry.getPosition(  ) - 1 );
                    EntryHome.update( entry, plugin );
                }
            }

            entryToChangeOrder.setPosition( nOrderToSet );
            EntryHome.update( entryToChangeOrder, plugin );
        }
    }

    /**
     * Init the list of the attribute's orders (first level only)
     * @param listEntryFirstLevel the list of all the attributes of the first
     *            level
     * @param orderFirstLevel the list to set
     */
    private void initOrderFirstLevel( List<IEntry> listEntryFirstLevel, List<Integer> orderFirstLevel )
    {
        for ( IEntry entry : listEntryFirstLevel )
        {
            orderFirstLevel.add( entry.getPosition(  ) );
        }
    }

    /**
     * Change the attribute's order to a lower one (move up in the list)
     * @param plugin the plugin
     * @param nOrderToSet the new order for the attribute
     * @param entryToChangeOrder the attribute which will change
     * @parem nIdForm the id of the form
     */
    private void moveUpEntryOrder( Plugin plugin, int nOrderToSet, IEntry entryToChangeOrder, int nIdForm )
    {
        if ( entryToChangeOrder.getParent(  ) == null )
        {
            List<Integer> orderFirstLevel = new ArrayList<Integer>(  );

            int nNbChild = 0;
            int nNewOrder = nOrderToSet;
            int nEntryToMoveOrder = entryToChangeOrder.getPosition(  );

            EntryFilter filter = new EntryFilter(  );
            filter.setIdForm( nIdForm );
            filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
            filter.setFieldDependNull( EntryFilter.FILTER_TRUE );

            List<IEntry> listEntryFirstLevel = EntryHome.findEntriesWithoutParent( plugin,
                    entryToChangeOrder.getForm(  ).getIdForm(  ) );
            //the list of all the orders in the first level
            initOrderFirstLevel( listEntryFirstLevel, orderFirstLevel );

            for ( IEntry entry : listEntryFirstLevel )
            {
                Integer entryInitialPosition = entry.getPosition(  );

                for ( int i = 0; i < orderFirstLevel.size(  ); i++ )
                {
                    if ( ( orderFirstLevel.get( i ) == entryInitialPosition ) &&
                            ( entryInitialPosition < nEntryToMoveOrder ) && ( entryInitialPosition >= nOrderToSet ) )
                    {
                        if ( entryToChangeOrder.getPosition(  ) == nEntryToMoveOrder )
                        {
                            entryToChangeOrder.setPosition( nNewOrder );
                            EntryHome.update( entryToChangeOrder, plugin );

                            for ( IEntry child : entryToChangeOrder.getChildren(  ) )
                            {
                                nNbChild++;
                                child.setPosition( entryToChangeOrder.getPosition(  ) + nNbChild );
                                EntryHome.update( child, plugin );
                            }
                        }

                        nNewOrder = nNewOrder + nNbChild + 1;
                        entry.setPosition( nNewOrder );
                        EntryHome.update( entry, plugin );
                        nNbChild = 0;

                        for ( IEntry child : entry.getChildren(  ) )
                        {
                            nNbChild++;
                            child.setPosition( nNewOrder + nNbChild );
                            EntryHome.update( child, plugin );
                        }
                    }
                }
            }
        }
        else
        {
            EntryFilter filter = new EntryFilter(  );
            filter.setIdForm( nIdForm );
            filter.setFieldDependNull( EntryFilter.FILTER_TRUE );

            List<IEntry> listAllEntry = EntryHome.getEntryList( filter, plugin );

            for ( IEntry entry : listAllEntry )
            {
                if ( ( entry.getPosition(  ) < entryToChangeOrder.getPosition(  ) ) &&
                        ( entry.getPosition(  ) >= nOrderToSet ) )
                {
                    entry.setPosition( entry.getPosition(  ) + 1 );
                    EntryHome.update( entry, plugin );
                }
            }

            entryToChangeOrder.setPosition( nOrderToSet );
            EntryHome.update( entryToChangeOrder, plugin );
        }
    }

    /**
     * Move down the entry (conditional questions only)
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doMoveDownEntryConditional( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        IEntry entry;

        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = -1;

        if ( ( strIdEntry != null ) && !strIdEntry.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdEntry = Integer.parseInt( strIdEntry );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getJspManageForm( request );
            }
        }

        if ( ( nIdEntry == -1 ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                    FormResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getJspManageForm( request );
        }

        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        EntryFilter filter = new EntryFilter(  );
        filter.setIdForm( entry.getForm(  ).getIdForm(  ) );

        if ( entry.getParent(  ) != null )
        {
            filter.setIdEntryParent( entry.getParent(  ).getIdEntry(  ) );
        }
        else
        {
            filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        }

        if ( entry.getFieldDepend(  ) != null )
        {
            filter.setIdFieldDepend( entry.getFieldDepend(  ).getIdField(  ) );
        }
        else
        {
            filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        }

        int nOrderToSet = entry.getPosition(  ) + 1;
        IEntry entryWithTheSelectedOrder = EntryHome.findByOrderAndIdFieldAndIdForm( plugin, nOrderToSet,
                entry.getFieldDepend(  ).getIdField(  ), entry.getForm(  ).getIdForm(  ) );

        entryWithTheSelectedOrder.setPosition( entry.getPosition(  ) );
        EntryHome.update( entryWithTheSelectedOrder, plugin );

        entry.setPosition( nOrderToSet );
        EntryHome.update( entry, plugin );

        if ( entry.getFieldDepend(  ) != null )
        {
            return getJspModifyField( request, entry.getFieldDepend(  ).getIdField(  ) );
        }
        else
        {
            return getJspModifyForm( request, _nIdForm );
        }
    }

    /**
     * Move up the entry (conditional questions only)
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doMoveUpEntryConditional( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        IEntry entry;

        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = -1;

        if ( ( strIdEntry != null ) && !strIdEntry.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdEntry = Integer.parseInt( strIdEntry );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getJspManageForm( request );
            }
        }

        if ( ( nIdEntry == -1 ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _nIdForm,
                    FormResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return getJspManageForm( request );
        }

        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        List<IEntry> listEntry;
        EntryFilter filter = new EntryFilter(  );
        filter.setIdForm( entry.getForm(  ).getIdForm(  ) );

        if ( entry.getParent(  ) != null )
        {
            filter.setIdEntryParent( entry.getParent(  ).getIdEntry(  ) );
        }
        else
        {
            filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        }

        if ( entry.getFieldDepend(  ) != null )
        {
            filter.setIdFieldDepend( entry.getFieldDepend(  ).getIdField(  ) );
        }
        else
        {
            filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        }

        int nOrderToSet = entry.getPosition(  ) - 1;
        IEntry entryWithTheSelectedOrder = EntryHome.findByOrderAndIdFieldAndIdForm( plugin, nOrderToSet,
                entry.getFieldDepend(  ).getIdField(  ), entry.getForm(  ).getIdForm(  ) );

        entryWithTheSelectedOrder.setPosition( entry.getPosition(  ) );
        EntryHome.update( entryWithTheSelectedOrder, plugin );

        entry.setPosition( nOrderToSet );
        EntryHome.update( entry, plugin );

        if ( entry.getFieldDepend(  ) != null )
        {
            return getJspModifyField( request, entry.getFieldDepend(  ).getIdField(  ) );
        }
        else
        {
            return getJspModifyForm( request, _nIdForm );
        }
    }

    /**
     * Move EntryToMove into entryGroup
     * @param plugin the plugin
     * @param entryToMove the entry which will be moved
     * @param entryGroup the entry group
     */
    private void doMoveEntryIntoGroup( Plugin plugin, IEntry entryToMove, IEntry entryGroup )
    {
        int nPosition;

        if ( ( entryGroup.getChildren(  ) != null ) && ( !entryGroup.getChildren(  ).isEmpty(  ) ) )
        {
            nPosition = entryGroup.getPosition(  ) + entryGroup.getChildren(  ).size(  ) + 1;
        }

        if ( entryToMove.getPosition(  ) < entryGroup.getPosition(  ) )
        {
            nPosition = entryGroup.getPosition(  );
            this.moveDownEntryOrder( plugin, nPosition, entryToMove, entryToMove.getForm(  ).getIdForm(  ) );
        }
        else
        {
            nPosition = entryGroup.getPosition(  ) + entryGroup.getChildren(  ).size(  ) + 1;
            this.moveUpEntryOrder( plugin, nPosition, entryToMove, entryToMove.getForm(  ).getIdForm(  ) );
        }

        entryToMove.setParent( entryGroup );
        EntryHome.update( entryToMove, plugin );
    }

    /**
     * Move out entry (no parent)
     * @param plugin the plugin
     * @param nIdForm the id form
     * @param nListEntrySize the number of entry
     * @param entryToMove the entry to move
     */
    private void doMoveOutEntry( Plugin plugin, int nIdForm, Integer nListEntrySize, IEntry entryToMove )
    {
        this.moveDownEntryOrder( plugin, nListEntrySize, entryToMove, nIdForm );
        entryToMove.setParent( null );
        EntryHome.update( entryToMove, plugin );
    }

    /**
     * Updates the entries position for a form
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String updateEntryOrder( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        List<IEntry> listEntryFirstLevel = new ArrayList<IEntry>(  );
        EntryFilter filter;
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = -1;
        Form form;

        if ( ( strIdForm != null ) && !strIdForm.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdForm = Integer.parseInt( strIdForm );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getManageForm( request );
            }
        }

        form = FormHome.findByPrimaryKey( nIdForm, plugin );

        if ( ( form == null ) ||
                !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_MODIFY,
                    getUser(  ) ) )
        {
            return getManageForm( request );
        }

        filter = new EntryFilter(  );
        filter.setIdForm( nIdForm );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        listEntryFirstLevel = EntryHome.getEntryList( filter, plugin );

        int nPosition = 1;

        for ( IEntry entry : listEntryFirstLevel )
        {
            entry.setPosition( nPosition );
            EntryHome.update( entry, plugin );
            nPosition++;

            EntryFilter filterSecondLevel = new EntryFilter(  );
            filterSecondLevel.setIdEntryParent( entry.getIdEntry(  ) );

            List<IEntry> listEntrySecondLevel = EntryHome.getEntryList( filterSecondLevel, plugin );

            if ( listEntrySecondLevel != null )
            {
                for ( IEntry entrySecondLevel : listEntrySecondLevel )
                {
                    entrySecondLevel.setPosition( nPosition );
                    EntryHome.update( entrySecondLevel, plugin );
                    nPosition++;
                }
            }
        }

        return getJspModifyForm( request, nIdForm );
    }
}
