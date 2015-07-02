/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import com.keypoint.PngEncoder;

import fr.paris.lutece.plugins.form.business.ExportFormat;
import fr.paris.lutece.plugins.form.business.ExportFormatHome;
import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormAction;
import fr.paris.lutece.plugins.form.business.FormActionHome;
import fr.paris.lutece.plugins.form.business.FormFilter;
import fr.paris.lutece.plugins.form.business.FormHome;
import fr.paris.lutece.plugins.form.business.FormSubmit;
import fr.paris.lutece.plugins.form.business.FormSubmitHome;
import fr.paris.lutece.plugins.form.business.GraphType;
import fr.paris.lutece.plugins.form.business.GraphTypeHome;
import fr.paris.lutece.plugins.form.business.Recap;
import fr.paris.lutece.plugins.form.business.RecapHome;
import fr.paris.lutece.plugins.form.business.StatisticFormSubmit;
import fr.paris.lutece.plugins.form.business.outputprocessor.IOutputProcessor;
import fr.paris.lutece.plugins.form.business.portlet.FormPortletHome;
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
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseFilter;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.captcha.CaptchaSecurityService;
import fr.paris.lutece.portal.service.html.XmlTransformerService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppException;
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
import fr.paris.lutece.util.sql.TransactionManager;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.util.xml.XmlUtil;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;

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


/**
 * This class provides the user interface to manage form features ( manage,
 * create, modify, remove)
 */
public abstract class FormJspBean extends PluginAdminPageJspBean
{
    /**
     * Right to manage forms
     */
    public static final String RIGHT_MANAGE_FORM = "FORM_MANAGEMENT";

    /**
     * Parameter redirect
     */
    public static final String PARAMETER_ACTION_REDIRECT = "redirect";

    /**
     * Automatic publication mode
     */
    public static final String PUBLICATION_MODE_AUTO = "1";

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -7186917553276257014L;

    // templates
    private static final String TEMPLATE_MANAGE_FORM = "admin/plugins/form/manage_form.html";
    private static final String TEMPLATE_MANAGE_OUTPUT_PROCESSOR = "admin/plugins/form/manage_output_processor.html";
    private static final String TEMPLATE_HTML_TEST_FORM = "admin/plugins/form/test_form.html";
    private static final String TEMPLATE_MODIFY_RECAP = "admin/plugins/form/modify_recap.html";
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
    private static final String MESSAGE_CONFIRM_DISABLE_FORM = "form.message.confirmDisableForm";
    private static final String MESSAGE_CONFIRM_DISABLE_FORM_WITH_PORTLET = "form.message.confirmDisableFormWithPortlet";
    private static final String MESSAGE_MANDATORY_FIELD = "form.message.mandatory.field";
    private static final String MESSAGE_MANDATORY_QUESTION = "form.message.mandatory.question";
    private static final String MESSAGE_CAPTCHA_ERROR = "form.message.captchaError";
    private static final String MESSAGE_REQUIREMENT_ERROR = "form.message.requirementError";
    private static final String MESSAGE_NO_RESPONSE = "form.message.noResponse";
    private static final String MESSAGE_FORM_ERROR = "form.message.formError";
    private static final String MESSAGE_CANT_ENABLE_FORM_DATE_END_DISPONIBILITY_BEFORE_CURRENT_DATE = "form.message.cantEnableFormDateEndDisponibilityBeforeCurrentDate";
    private static final String MESSAGE_ERROR_DURING_DOWNLOAD_FILE = "form.message.errorDuringDownloadFile";
    private static final String MESSAGE_YOU_ARE_NOT_ALLOWED_TO_DOWLOAD_THIS_FILE = "form.message.youAreNotAllowedToDownloadFile";
    private static final String MESSAGE_ERROR_EXPORT_ENCODING_NOT_SUPPORTED = "form.message.error.export.encoding.not_supported";
    private static final String FIELD_LIBELE_VALIDATE_BUTTON = "form.createForm.labelLibelleValidateButton";
    private static final String FIELD_BACK_URL = "form.modifyRecap.labelBackUrl";
    private static final String FIELD_RECAP_MESSAGE = "form.modifyRecap.labelRecapMessage";
    private static final String FIELD_UNAVAILABILITY_MESSAGE = "form.createForm.labelUnavailabilityMessage";
    private static final String FIELD_REQUIREMENT = "form.createForm.labelRequirement";

    // properties
    private static final String PROPERTY_ITEM_PER_PAGE = "form.itemsPerPage";
    private static final String PROPERTY_ALL = "form.manageForm.select.all";
    private static final String PROPERTY_YES = "form.manageForm.select.yes";
    private static final String PROPERTY_NO = "form.manageForm.select.no";
    private static final String PROPERTY_COPY_FORM_TITLE = "form.copyForm.title";
    private static final String PROPERTY_MODIFY_RECAP_TITLE = "form.modifyRecap.title";
    private static final String PROPERTY_RESULT_PAGE_TITLE = "form.result.pageTitle";
    private static final String PROPERTY_LABEL_AXIS_X = "form.result.graph.labelAxisX";
    private static final String PROPERTY_LABEL_AXIS_Y = "form.result.graph.labelAxisY";
    private static final String PROPERTY_NUMBER_RESPONSE_AXIS_X = "graph.numberResponseAxisX";
    private static final String XSL_UNIQUE_PREFIX_ID = UniqueIDGenerator.getNewId(  ) + "form-";
    private static final String PROPERTY_MODIFY_MESSAGE_TITLE = "form.modifyMessage.title";
    private static final String PROPERTY_MANAGE_VALIDATOR_TITLE = "form.manageValidator.title";
    private static final String PROPERTY_MANAGE_OUTPUT_PROCESSOR_TITLE = "form.manageOutputProcessor.title";

    // Markers
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_USER_WORKGROUP_REF_LIST = "user_workgroup_list";
    private static final String MARK_USER_WORKGROUP_SELECTED = "user_workgroup_selected";
    private static final String MARK_ACTIVE_REF_LIST = "active_list";
    private static final String MARK_ACTIVE_SELECTED = "active_selected";
    private static final String MARK_RECAP = "recap";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_FORM_LIST = "form_list";
    private static final String MARK_FORM = "form";
    private static final String MARK_PERMISSION_CREATE_FORM = "permission_create_form";
    private static final String MARK_STR_FORM = "str_form";
    private static final String MARK_GRAPH_TYPE_REF_LIST = "graph_type_list";
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
    private static final String MARK_VALIDATOR_LIST = "validator_list";
    private static final String MARK_PERMISSION_MANAGE_ADVANCED_PARAMETERS = "permission_manage_advanced_parameters";
    private static final String MARK_SORT_ORDER = "asc_sort";
    private static final String MARK_TITLE = "title";
    private static final String FILTER_ASC = "asc";
    private static final String FILTER_DESC = "desc";

    // Jsp Definition
    private static final String JSP_DO_DISABLE_FORM = "jsp/admin/plugins/form/DoDisableForm.jsp";
    private static final String JSP_DO_DISABLE_AUTO_FORM = "jsp/admin/plugins/form/DoDisableAutoForm.jsp";
    private static final String JSP_DO_REMOVE_FORM = "jsp/admin/plugins/form/DoRemoveForm.jsp";
    private static final String JSP_MANAGE_FORM = "jsp/admin/plugins/form/ManageForm.jsp";
    private static final String JSP_TEST_FORM = "jsp/admin/plugins/form/TestForm.jsp";
    private static final String JSP_DO_TEST_FORM = "jsp/admin/plugins/form/DoTestForm.jsp";
    private static final String JSP_MANAGE_OUTPUT_PROCESS_FORM = "jsp/admin/plugins/form/ManageOutputProcessor.jsp";
    private static final String JSP_MANAGE_VALIDATOR_FORM = "jsp/admin/plugins/form/ManageValidator.jsp";
    private static final String JSP_MANAGE_ADVANCED_PARAMETERS = "jsp/admin/plugins/form/ManageAdvancedParameters.jsp";

    // parameters form
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_REQUIREMENT = "requirement";
    private static final String PARAMETER_WELCOME_MESSAGE = "welcome_message";
    private static final String PARAMETER_UNAVAILABILITY_MESSAGE = "unavailability_message";
    private static final String PARAMETER_LIBELLE_VALIDATE_BUTTON = "libelle_validate_button";
    private static final String PARAMETER_LIBELLE_RESET_BUTTON = "libelle_reset_button";
    private static final String PARAMETER_BACK_URL = "back_url";
    private static final String PARAMETER_ACTIVE = "active";
    private static final String PARAMETER_WORKGROUP = "workgroup";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_ID_RECAP = "id_recap";
    private static final String PARAMETER_RECAP_MESSAGE = "recap_message";
    private static final String PARAMETER_RECAP_DATA = "recap_data";
    private static final String PARAMETER_GRAPH = "graph";
    private static final String PARAMETER_GRAPH_THREE_DIMENSION = "graph_three_dimension";
    private static final String PARAMETER_GRAPH_LABEL_VALUE = "graph_label_value";
    private static final String PARAMETER_ID_GRAPH_TYPE = "id_graph_type";
    private static final String PARAMETER_ID_RESPONSE = "id_response";
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String PARAMETER_SESSION = "session";
    private static final String PARAMETER_ID_EXPORT_FORMAT = "id_export_format";
    private static final String PARAMETER_FIRST_RESPONSE_DATE_FILTER = "fist_response_date_filter";
    private static final String PARAMETER_LAST_RESPONSE_DATE_FILTER = "last_response_date_filter";
    private static final String PARAMETER_TIMES_UNIT = "times_unit";
    private static final String PARAMETER_PROCESSOR_KEY = "processor_key";
    private static final String PARAMETER_IS_SELECTED = "is_selected";

    // other constants
    private static final String EMPTY_STRING = "";
    private static final String JCAPTCHA_PLUGIN = "jcaptcha";
    private static final String QUESTION_MARK_STRING = "?";
    private static final String EQUAL_STRING = "=";
    private static final String CONST_ZERO = "0";
    private static final String SQL_FILTER_ENTRY_POS = " ent.pos ";

    // session fields
    /**
     * The default numbers of items to display per page for the current user
     */
    protected final int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 50 );

    /**
     * The id of the current form
     */
    private int _nIdForm = -1;
    private String _strCurrentPageIndexForm;
    private int _nIdActive = -1;
    private int _nItemsPerPageForm;
    private String _strWorkGroup = AdminWorkgroupService.ALL_GROUPS;
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

        if ( StringUtils.isNotBlank( request.getParameter( MARK_SORT_ORDER ) ) )
        {
            filter.setOrder( MARK_TITLE );

            if ( Boolean.TRUE.toString(  ).equals( request.getParameter( MARK_SORT_ORDER ) ) )
            {
                filter.setAsc( FILTER_ASC );
            }
            else
            {
                filter.setAsc( FILTER_DESC );
            }
        }

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
     * @throws AccessDeniedException If the user is not authorized to access
     *             this feature
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
     * @throws AccessDeniedException If the user is not authorized to access
     *             this feature
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
        responseFilter.setIdResource( nIdForm );

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

            TransactionManager.beginTransaction( getPlugin(  ) );

            try
            {
                FormHome.remove( nIdForm, plugin );
                OutputProcessorService.getInstance(  ).removeProcessorAssociationsByIdForm( nIdForm );

                // Removes the associations between all validators and the form
                ValidatorService.getInstance(  ).removeAssociationsWithForm( nIdForm );
                TransactionManager.commitTransaction( getPlugin(  ) );
            }
            catch ( Exception e )
            {
                TransactionManager.rollBack( getPlugin(  ) );
                throw new AppException( e.getMessage(  ), e );
            }
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
     * Get the request data and if there is no error insert the data in the
     * recap specified in parameter. return null if there is no error or else
     * return the error page url
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
             * if ( strGraphLegende != null ) { recap.setGraphLegende( true );
             * recap.setGraphValueLegende( strGraphValueLegende ); } else {
             * recap.setGraphLegende( false ); recap.setGraphValueLegende(
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
     * return url of the jsp modify form
     * @param request The HTTP request
     * @param nIdForm the key of form to modify
     * @param parameterName The name of the additional parameter of the url
     * @param parameterValue The value of the additional parameter of the url
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

        model.put( MARK_STR_FORM, FormUtils.getHtmlForm( form, strUrlAction, locale, false, request ) );
        model.put( MARK_EXPORT_FORMAT_REF_LIST, ExportFormatHome.getListExport( plugin ) );
        setPageTitleProperty( EMPTY_STRING );
        template = AppTemplateService.getTemplate( TEMPLATE_HTML_TEST_FORM, locale, model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * if there is no error perform in session the response of the form else
     * return the error
     * @param request the http request
     * @return The URL to go after performing the action
     */
    public String doTestForm( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        List<Entry> listEntryFirstLevel;
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
        filter.setIdResource( nIdForm );
        filter.setResourceType( Form.RESOURCE_TYPE );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        filter.setIdIsComment( EntryFilter.FILTER_FALSE );
        listEntryFirstLevel = EntryHome.getEntryList( filter );

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

        for ( Entry entry : listEntryFirstLevel )
        {
            List<GenericAttributeError> listFormError = FormUtils.getResponseEntry( request, entry.getIdEntry(  ),
                    plugin, formSubmit, false, true, locale );

            if ( ( listFormError != null ) && !listFormError.isEmpty(  ) )
            {
                // Only display the first error
                GenericAttributeError formError = listFormError.get( 0 );

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
     * write in the http response the export file of all response submit who are
     * save during the test. if there is no response return a error
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
        filter.setIdResource( nIdForm );
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
     * write in the http response the export file of all response submit who
     * verify the date filter if there is no response return a error
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
            filter.setIdResource( nIdForm );
            filter.setDateFirst( tFistResponseDateFilter );
            filter.setDateLast( tLastResponseDateFilter );

            List<FormSubmit> listFormSubmit = FormSubmitHome.getFormSubmitList( filter, plugin );

            for ( FormSubmit formSubmit : listFormSubmit )
            {
                filter = new ResponseFilter(  );
                filter.setOrderBy( SQL_FILTER_ENTRY_POS );
                filter.setOrderByAsc( true );

                List<Integer> responseId = FormSubmitHome.getResponseListFromIdFormSubmit( formSubmit.getIdFormSubmit(  ),
                        plugin );
                filter.setListId( responseId );

                formSubmit.setListResponse( _responseService.getResponseList( filter, false ) );
            }

            if ( listFormSubmit.size(  ) != 0 )
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
     * write in the http response the statistic graph of all response submit who
     * verify the date filter
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

        filter.setIdResource( nIdForm );
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
     * write in the http response the value of the response whose identifier is
     * specified in the request if there is no response return a error
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
            // is authorized to view result
            FormSubmit formSubmit = FormSubmitHome.findFormSubmitFromResponseId( responseFile.getIdResponse(  ), plugin );

            List<Form> listForm = new ArrayList<Form>(  );

            if ( formSubmit != null )
            {
                form = FormHome.findByPrimaryKey( formSubmit.getForm(  ).getIdForm(  ), plugin );
                listForm.add( form );
            }

            listForm = (List<Form>) AdminWorkgroupService.getAuthorizedCollection( listForm, adminUser );

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
     * @throws AccessDeniedException If the user is not authorized to acces this
     *             feature
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
     * Return the URL of the JSP manage form
     * @param request The HTTP request
     * @return The URL of the JSP manage form
     */
    protected String getJspManageForm( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_FORM;
    }

    /**
     * Return the URL of the JSP test form
     * @param request The HTTP request
     * @param nIdForm The key of form to modify
     * @return The return URL of the JSP modify form
     */
    protected String getJspTestForm( HttpServletRequest request, int nIdForm )
    {
        return AppPathService.getBaseUrl( request ) + JSP_TEST_FORM + "?id_form=" + nIdForm + "&session=session";
    }

    /**
     * Get the id of the current form
     * @return The id of the form
     */
    protected int getFormId(  )
    {
        return _nIdForm;
    }

    /**
     * Set the id of the current form
     * @param nFormId The id of the form
     */
    protected void setFormId( int nFormId )
    {
        this._nIdForm = nFormId;
    }
}
