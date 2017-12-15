/*
 * Copyright (c) 2002-2017, Mairie de Paris
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.form.business.ExportFormat;
import fr.paris.lutece.plugins.form.business.ExportFormatHome;
import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormHome;
import fr.paris.lutece.plugins.form.business.FormSubmit;
import fr.paris.lutece.plugins.form.service.FormResourceIdService;
import fr.paris.lutece.plugins.form.service.upload.FormAsynchronousUploadHandler;
import fr.paris.lutece.plugins.form.utils.EntryTypeGroupUtils;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.captcha.CaptchaSecurityService;
import fr.paris.lutece.portal.service.html.XmlTransformerService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppHTTPSService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.UniqueIDGenerator;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.xml.XmlUtil;

/**
 * Controller for the Test of Test of the Form page
 */
@Controller( controllerJsp = TestFormJspBean.TEST_FORM_CONTROLLER_JSP_NAME, controllerPath = TestFormJspBean.TEST_FORM_CONTROLLER_PATH, right = TestFormJspBean.TEST_FORM_CONTROLLER_RIGHT )
public class TestFormJspBean extends FormJspBean
{
    /**
     * Serial UID
     */
    private static final long serialVersionUID = 1501846352423305144L;

    // Controller attributes
    protected static final String TEST_FORM_CONTROLLER_JSP_NAME = "TestForm.jsp";
    protected static final String TEST_FORM_CONTROLLER_PATH = "jsp/admin/plugins/form";
    protected static final String TEST_FORM_CONTROLLER_RIGHT = "FORM_MANAGEMENT";

    // Views
    private static final String VIEW_TEST_FORM = "testForm";

    // Actions
    private static final String ACTION_ADD_ITERATION = "addIteration";
    private static final String ACTION_REMOVE_ITERATION = "removeIteration";
    private static final String ACTION_SUBMIT_FORM = "submitForm";
    private static final String ACTION_EXPORT_RESPONSES = "exportResponses";

    // Template
    private static final String TEMPLATE_HTML_TEST_FORM = "admin/plugins/form/test_form.html";

    // Url
    private static final String URL_ACTION = "jsp/admin/plugins/form/TestForm.jsp";

    // Parameters
    private static final String PARAMETER_SESSION = "session";
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_REQUIREMENT = "requirement";
    private static final String PARAMETER_RESET = "reset";
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String PARAMETER_ID_EXPORT_FORMAT = "id_export_format";

    // Marks
    private static final String MARK_FORM = "form";
    private static final String MARK_STR_FORM = "str_form";
    private static final String MARK_EXPORT_FORMAT_REF_LIST = "export_format_list";

    // Messages
    private static final String MESSAGE_REQUIREMENT_ERROR = "form.message.requirementError";
    private static final String MESSAGE_CAPTCHA_ERROR = "form.message.captchaError";
    private static final String MESSAGE_MANDATORY_QUESTION = "form.message.mandatory.question";
    private static final String MESSAGE_FORM_ERROR = "form.message.formError";
    private static final String MESSAGE_NO_RESPONSE = "form.message.noResponse";

    // Plugin
    private static final String JCAPTCHA_PLUGIN = "jcaptcha";

    // Properties
    private static final String XSL_UNIQUE_PREFIX_ID = UniqueIDGenerator.getNewId( ) + "form-";

    // Variables
    private HttpServletResponse _response;
    private List<FormSubmit> _listFormSubmitTest;

    /**
     * {@inheritDoc}
     */
    @Override
    public String processController( HttpServletRequest request, HttpServletResponse response ) throws AccessDeniedException
    {
        _response = response;
        return super.processController( request, response );
    }

    /**
     * Gets the form test page
     * 
     * @param request
     *            The HttpServletRequest
     * @return the form test page
     */
    @View( value = VIEW_TEST_FORM, defaultView = true )
    public String getViewTestForm( HttpServletRequest request )
    {
        if ( request.getParameter( PARAMETER_SESSION ) == null )
        {
            _listFormSubmitTest = new ArrayList<>( );
        }

        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = NumberUtils.INTEGER_MINUS_ONE;

        if ( StringUtils.isNotBlank( strIdForm ) )
        {
            try
            {
                nIdForm = Integer.parseInt( strIdForm );
            }
            catch( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getManageForm( request );
            }
        }

        if ( nIdForm == NumberUtils.INTEGER_MINUS_ONE
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_TEST, getUser( ) ) )
        {
            return getManageForm( request );
        }

        // Populate the session with the information of the iteration
        EntryTypeGroupUtils.populateIterationGroupMap( request.getSession( ), nIdForm );

        HtmlTemplate template = getHtmlTemplatePage( request, nIdForm );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Add an iteration for the specified group and return the test form page
     * 
     * @param request
     *            The HttpServletRequest
     * @return the form test page
     */
    @Action( value = ACTION_ADD_ITERATION )
    public String doAddIteration( HttpServletRequest request )
    {
        EntryTypeGroupUtils.manageAddingIteration( request );

        int nIdForm = NumberUtils.INTEGER_MINUS_ONE;
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        if ( StringUtils.isNotBlank( strIdForm ) )
        {
            try
            {
                nIdForm = Integer.parseInt( strIdForm );
            }
            catch( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getManageForm( request );
            }
        }

        HtmlTemplate template = getHtmlTemplatePage( request, nIdForm );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Remove an iteration for the specified group and return the test form page
     * 
     * @param request
     *            The HttpServletRequest
     * @return the test form page
     */
    @Action( value = ACTION_REMOVE_ITERATION )
    public String doRemoveIteration( HttpServletRequest request )
    {
        EntryTypeGroupUtils.manageRemoveIterationGroup( request );

        int nIdForm = NumberUtils.INTEGER_MINUS_ONE;
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        if ( StringUtils.isNotBlank( strIdForm ) )
        {
            try
            {
                nIdForm = Integer.parseInt( strIdForm );
            }
            catch( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getManageForm( request );
            }
        }

        HtmlTemplate template = getHtmlTemplatePage( request, nIdForm );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * if there is no error perform in session the response of the form else return the error
     * 
     * @param request
     *            The HttpServletRequest
     * @return Return to the test form page which have been reset if there are no errors
     */
    @Action( value = ACTION_SUBMIT_FORM )
    public String doSubmitForm( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        HttpSession session = request.getSession( );
        List<Entry> listEntryFirstLevel;
        EntryFilter filter;
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        String strRequirement = request.getParameter( PARAMETER_REQUIREMENT );
        String strErrorMessage = null;
        int nIdForm = -1;

        Form form;

        if ( StringUtils.isNotBlank( strIdForm ) )
        {
            try
            {
                nIdForm = Integer.parseInt( strIdForm );
            }
            catch( NumberFormatException ne )
            {
                AppLogService.error( ne );
            }
        }

        if ( nIdForm == -1 || !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_TEST, getUser( ) ) )
        {
            return getManageForm( request );
        }

        form = FormHome.findByPrimaryKey( nIdForm, plugin );

        if ( request.getParameter( PARAMETER_RESET ) != null )
        {
            cleanSession( session );

            return redirectView( request, VIEW_TEST_FORM );
        }

        if ( form.isActiveRequirement( ) && ( strRequirement == null ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_REQUIREMENT_ERROR, AdminMessage.TYPE_STOP ) );
        }

        if ( form.isActiveCaptcha( ) && PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) )
        {
            CaptchaSecurityService captchaSecurityService = new CaptchaSecurityService( );

            if ( !captchaSecurityService.validate( request ) )
            {
                return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_CAPTCHA_ERROR, AdminMessage.TYPE_STOP ) );
            }
        }

        filter = new EntryFilter( );
        filter.setIdResource( nIdForm );
        filter.setResourceType( Form.RESOURCE_TYPE );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        filter.setIdIsComment( EntryFilter.FILTER_FALSE );
        listEntryFirstLevel = EntryHome.getEntryList( filter );

        Locale locale = getLocale( );

        // create form response
        FormSubmit formSubmit = new FormSubmit( );
        formSubmit.setForm( form );
        formSubmit.setDateResponse( FormUtils.getCurrentTimestamp( ) );

        if ( form.isActiveStoreAdresse( ) )
        {
            formSubmit.setIp( request.getRemoteAddr( ) );
        }

        List<Response> listResponse = new ArrayList<Response>( );
        formSubmit.setListResponse( listResponse );

        for ( Entry entry : listEntryFirstLevel )
        {
            List<GenericAttributeError> listFormError = FormUtils.getResponseEntry( request, entry.getIdEntry( ), plugin, formSubmit, false, true, locale );

            if ( ( listFormError != null ) && !listFormError.isEmpty( ) )
            {
                // Only display the first error
                GenericAttributeError formError = listFormError.get( 0 );

                if ( formError != null )
                {
                    if ( formError.isMandatoryError( ) )
                    {
                        Object [ ] tabRequiredFields = {
                            formError.getTitleQuestion( )
                        };

                        strErrorMessage = AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_QUESTION, tabRequiredFields, AdminMessage.TYPE_STOP );
                    }
                    else
                    {
                        Object [ ] tabRequiredFields = {
                                formError.getTitleQuestion( ), formError.getErrorMessage( )
                        };

                        strErrorMessage = AdminMessageService.getMessageUrl( request, MESSAGE_FORM_ERROR, tabRequiredFields, AdminMessage.TYPE_STOP );
                    }

                    return redirect( request, strErrorMessage );
                }
            }
        }

        // Reorder the responses which belong to iteration
        EntryTypeGroupUtils.orderResponseList( request, formSubmit.getListResponse( ) );

        _listFormSubmitTest.add( formSubmit );

        cleanSession( session );

        Map<String, String> model = new LinkedHashMap<>( );
        model.put( PARAMETER_ID_FORM, strIdForm );
        model.put( PARAMETER_SESSION, "session" );

        return redirect( request, VIEW_TEST_FORM, model );
    }

    /**
     * write in the http response the export file of all response submit who are save during the test. if there is no response return a error
     * 
     * @param request
     *            the http request
     * @param response
     *            The http response
     * @return The URL to go after performing the action
     */
    @Action( value = ACTION_EXPORT_RESPONSES )
    public String doExportResponses( HttpServletRequest request )
    {
        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            Plugin plugin = getPlugin( );
            Locale locale = getLocale( );
            String strIdForm = request.getParameter( PARAMETER_ID_FORM );
            String strIdExportFormat = request.getParameter( PARAMETER_ID_EXPORT_FORMAT );
            int nIdForm = -1;
            int nIdExportFormat = -1;

            Form form;
            ExportFormat exportFormat;

            if ( StringUtils.isNotBlank( strIdForm ) && StringUtils.isNotBlank( strIdExportFormat ) )
            {
                try
                {
                    nIdForm = Integer.parseInt( strIdForm );
                    nIdExportFormat = Integer.parseInt( strIdExportFormat );
                }
                catch( NumberFormatException ne )
                {
                    AppLogService.error( ne );

                    return getManageForm( request );
                }
            }

            if ( ( nIdForm == -1 ) || ( nIdExportFormat == -1 )
                    || !RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormResourceIdService.PERMISSION_TEST, getUser( ) ) )
            {
                return getManageForm( request );
            }

            exportFormat = ExportFormatHome.findByPrimaryKey( nIdExportFormat, plugin );
            form = FormHome.findByPrimaryKey( nIdForm, plugin );

            if ( ( _listFormSubmitTest != null ) && ( _listFormSubmitTest.size( ) != 0 ) )
            {
                XmlTransformerService xmlTransformerService = new XmlTransformerService( );
                String strXmlSource = XmlUtil.getXmlHeader( ) + FormUtils.getXmlResponses( request, form, _listFormSubmitTest, locale, plugin );
                String strXslUniqueId = XSL_UNIQUE_PREFIX_ID + nIdExportFormat;

                String strFileOutPut = xmlTransformerService.transformBySourceWithXslCache( strXmlSource, exportFormat.getXsl( ), strXslUniqueId, null, null );

                byte [ ] byteFileOutPut = strFileOutPut.getBytes( );

                try
                {
                    String strFormatExtension = exportFormat.getExtension( ).trim( );
                    String strFileName = form.getTitle( ) + "." + strFormatExtension;
                    FormUtils.addHeaderResponse( request, _response, strFileName, strFormatExtension );
                    _response.setContentLength( byteFileOutPut.length );

                    OutputStream os = _response.getOutputStream( );
                    os.write( byteFileOutPut );
                    os.close( );
                }
                catch( IOException e )
                {
                    AppLogService.error( e );
                }
            }
            else
            {
                return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_NO_RESPONSE, AdminMessage.TYPE_STOP ) );
            }
        }

        return getManageForm( request );
    }

    /**
     * Clean the session, remove the response, the errors and the files attached to it
     * 
     * @param session
     *            The HttpSession to clean
     */
    public void cleanSession( HttpSession session )
    {
        if ( session != null )
        {
            FormUtils.removeResponses( session );
            FormUtils.removeFormErrors( session );
            FormAsynchronousUploadHandler.getHandler( ).removeSessionFiles( session.getId( ) );
        }
    }

    /**
     * Generate the Html Template of a Test Form page
     * 
     * @param request
     *            The HttpServletRequest
     * @param nIdForm
     *            The identifier of the form
     * @return the HtmlTemplate of the Test Form
     */
    private HtmlTemplate getHtmlTemplatePage( HttpServletRequest request, int nIdForm )
    {
        String strUrlAction = URL_ACTION;
        Plugin plugin = getPlugin( );

        Form form = FormHome.findByPrimaryKey( nIdForm, plugin );

        if ( form.isSupportHTTPS( ) && AppHTTPSService.isHTTPSSupportEnabled( ) )
        {
            strUrlAction = AppHTTPSService.getHTTPSUrl( request ) + strUrlAction;
        }

        Locale locale = getLocale( );
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_FORM, form );
        model.put( MARK_STR_FORM, FormUtils.getHtmlForm( form, strUrlAction, locale, false, request ) );
        model.put( MARK_EXPORT_FORMAT_REF_LIST, ExportFormatHome.getListExport( plugin ) );
        setPageTitleProperty( StringUtils.EMPTY );

        return AppTemplateService.getTemplate( TEMPLATE_HTML_TEST_FORM, locale, model );
    }

}
