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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.form.business.CaptchaFormError;
import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormFilter;
import fr.paris.lutece.plugins.form.business.FormHome;
import fr.paris.lutece.plugins.form.business.FormSubmit;
import fr.paris.lutece.plugins.form.business.FormSubmitHome;
import fr.paris.lutece.plugins.form.business.Recap;
import fr.paris.lutece.plugins.form.business.RecapHome;
import fr.paris.lutece.plugins.form.business.RequirementFormError;
import fr.paris.lutece.plugins.form.business.iteration.IterationGroup;
import fr.paris.lutece.plugins.form.business.outputprocessor.IOutputProcessor;
import fr.paris.lutece.plugins.form.service.EntryTypeService;
import fr.paris.lutece.plugins.form.service.FormPlugin;
import fr.paris.lutece.plugins.form.service.FormService;
import fr.paris.lutece.plugins.form.service.IResponseService;
import fr.paris.lutece.plugins.form.service.OutputProcessorService;
import fr.paris.lutece.plugins.form.service.draft.FormDraftBackupService;
import fr.paris.lutece.plugins.form.service.entrytype.EntryTypeNumbering;
import fr.paris.lutece.plugins.form.service.entrytype.EntryTypeSession;
import fr.paris.lutece.plugins.form.service.upload.FormAsynchronousUploadHandler;
import fr.paris.lutece.plugins.form.service.validator.ValidatorService;
import fr.paris.lutece.plugins.form.utils.EntryTypeGroupUtils;
import fr.paris.lutece.plugins.form.utils.FormConstants;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseFilter;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.service.captcha.CaptchaSecurityService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppHTTPSService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.http.SecurityUtil;
import fr.paris.lutece.util.sql.TransactionManager;

/**
 * This class manages Form page.
 *
 */
@Controller( xpageName = FormApp.XPAGE_NAME, pageTitleI18nKey = FormApp.MESSAGE_PAGE_TITLE, pagePathI18nKey = FormApp.MESSAGE_PATH )
public class FormApp extends MVCApplication
{
    /**
     * /** Serial version UID
     */
    private static final long serialVersionUID = -1385222847493418480L;

    // Controller properties
    protected static final String XPAGE_NAME = "form";
    protected static final String MESSAGE_PAGE_TITLE = "form.xpage.form.pageTitle";
    protected static final String MESSAGE_PATH = "form.xpage.form.pagePathLabel";

    // markers
    private static final String MARK_RECAP = "recap";
    private static final String MARK_REQUIREMENT = "requirement";
    private static final String MARK_VALIDATE_RECAP = "validate_recap";
    private static final String MARK_LIST_FORMS = "forms_list";
    private static final String MARK_FORM_HTML = "form_html";
    private static final String MARK_MESSAGE_FORM_INACTIVE = "form_inactive";
    private static final String MARK_ENTRY_TYPE_SESSION = "entry_type_session";
    private static final String MARK_ENTRY_TYPE_NUMBERING = "entry_type_numbering";
    private static final String MARK_IS_DRAFT_SAVED = "is_draft_saved";
    private static final String MARK_FORM_ERRORS = "form_errors";
    private static final String MARK_ORDER = "order";
    private static final String MARK_ASC = "asc";

    // templates
    private static final String TEMPLATE_XPAGE_RECAP_FORM_SUBMIT = "skin/plugins/form/recap_form_submit.html";
    private static final String TEMPLATE_XPAGE_REQUIREMENT_FORM = "skin/plugins/form/requirement_form.html";
    private static final String TEMPLATE_XPAGE_LIST_FORMS = "skin/plugins/form/list_forms.html";
    private static final String TEMPLATE_XPAGE_FORM = "skin/plugins/form/form.html";
    private static final String JCAPTCHA_PLUGIN = "jcaptcha";

    // properties for page titles and path label
    private static final String PROPERTY_XPAGE_LIST_FORMS_PAGETITLE = "form.xpage.listForms.pagetitle";
    private static final String PROPERTY_XPAGE_LIST_FORMS_PATHLABEL = "form.xpage.listForms.pathlabel";
    private static final String PROPERTY_XPAGE_PAGETITLE = "form.xpage.pagetitle";
    private static final String PROPERTY_XPAGE_PATHLABEL = "form.xpage.pathlabel";
    private static final String PROPERTY_SESSION_INVALIDATE_URL_RETURN = "form.session.invalidate.urlReturn";
    private static final String PROPERTY_CLEAN_FORM_ANSWERS_KEY = "form.cleanFormAnswers.key";
    private static final String PROPERTY_CLEAN_FORM_ANSWERS_RETURN_CODE_UNAUTHORIZED = "form.cleanFormAnswers.returnCode.unauthorized";
    private static final String PROPERTY_CLEAN_FORM_ANSWERS_RETURN_CODE_OK = "form.cleanFormAnswers.returnCode.ok";
    private static final String PROPERTY_CLEAN_FORM_ANSWERS_RETURN_CODE_KO = "form.cleanFormAnswers.returnCode.ko";

    // request parameters
    private static final String PARAMETER_FORM_SUBMIT = "form_submit";
    private static final String PARAMETER_REQUIREMENT = "requirement";
    private static final String PARAMETER_VOTED = "voted";
    private static final String PARAMETER_ACTION_SUBMIT_FORM = "action_submitForm";
    private static final String PARAMETER_SESSION = "session";
    private static final String PARAMETER_ACTION_SAVE_DRAFT = "action_saveDraft";

    // session
    private static final String SESSION_VALIDATE_REQUIREMENT = "session_validate_requirement";

    // Views
    private static final String VIEW_LIST_FORM = "listForm";
    private static final String VIEW_FORM = "viewForm";
    private static final String VIEW_REQUIREMENT = "viewRequirement";

    // Actions
    private static final String ACTION_ADD_ITERATION = "addIteration";
    private static final String ACTION_REMOVE_ITERATION = "removeIteration";
    private static final String ACTION_SUBMIT_FORM = "submitForm";
    private static final String ACTION_RESET_FORM = "resetForm";
    private static final String ACTION_SAVE_DRAFT = "saveDraft";
    private static final String ACTION_VALIDATE_RECAP = "validateRecap";

    // message
    private static final String MESSAGE_ERROR = "form.message.Error";
    private static final String MESSAGE_ALREADY_SUBMIT_ERROR = "form.message.alreadySubmitError";
    private static final String MESSAGE_SUBMIT_SAVE_ERROR = "form.message.submitSaveError";
    private static final String MESSAGE_ERROR_FORM_INACTIVE = "form.message.errorFormInactive";
    private static final String MESSAGE_SESSION_LOST = "form.message.session.lost";
    private static final String MESSAGE_UNIQUE_FIELD = "form.message.errorUniqueField";

    // Urls
    private static final String JSP_DO_SUBMIT_FORM = "jsp/site/Portal.jsp?page=form&id_form=";
    private static final String JSP_PAGE_FORM = "jsp/site/Portal.jsp?page=form";

    // Misc
    private static final String REGEX_ID = "^[\\d]+$";
    private transient IResponseService _responseService;
    private transient EntryTypeService _entryTypeService;

    /**
     * Return the default XPage with the list of all available Form
     * 
     * @param request
     *            The HttpServletRequest
     * @return the list of all available forms
     * @throws SiteMessageException
     * @throws UserNotSignedException
     */
    @View( value = VIEW_LIST_FORM, defaultView = true )
    public XPage getListFormView( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException
    {
        XPage page = new XPage( );
        Locale locale = request.getLocale( );

        // Special case for upload fields : if no action is specified, a submit
        // button associated with an upload might have been pressed :
        String strUploadAction = FormAsynchronousUploadHandler.getHandler( ).getUploadAction( request );
        if ( strUploadAction != null )
        {
            return doSubmitForm( request );
        }

        if ( request.getParameter( FormConstants.PARAMETER_ID_FORM ) != null )
        {
            return getFormView( request );
        }

        page.setTitle( I18nService.getLocalizedString( PROPERTY_XPAGE_LIST_FORMS_PAGETITLE, locale ) );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_XPAGE_LIST_FORMS_PATHLABEL, locale ) );
        page.setContent( getFormList( request ) );

        return page;
    }

    /**
     * Return the XPage associated to a Form content
     * 
     * @param request
     *            The HttpServletrequest
     * @return the XPage associated to a Form
     * @throws SiteMessageException
     * @throws UserNotSignedException
     */
    @View( value = VIEW_FORM )
    public XPage getFormView( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException
    {
        XPage page = new XPage( );

        int nIdForm = NumberUtils.toInt( request.getParameter( FormConstants.PARAMETER_ID_FORM ), NumberUtils.INTEGER_MINUS_ONE );
        if ( nIdForm == NumberUtils.INTEGER_MINUS_ONE )
        {
            return getListFormView( request );
        }

        String strSessionId = request.getParameter( PARAMETER_SESSION );
        if ( StringUtils.isBlank( strSessionId ) || isIterationMapLost( request ) )
        {
            initializeSession( request.getSession( ), strSessionId, nIdForm );
        }

        // Try to restore draft
        // PreProcessRequest return true if the form should not be displayed (for deletion...)
        Plugin pluginForm = PluginService.getPlugin( FormPlugin.PLUGIN_NAME );
        Form form = FormHome.findByPrimaryKey( nIdForm, pluginForm );
        if ( !FormDraftBackupService.preProcessRequest( request, form ) )
        {
            // Get the page associated to the draft
            page = getForm( request );
        }

        return page;
    }

    /**
     * Add an iteration to a group
     * 
     * @param request
     *            The HttpServletRequest
     * @return the page of the Form with an iteration added to the group
     * @throws UserNotSignedException
     * @throws SiteMessageException
     */
    @Action( value = ACTION_ADD_ITERATION )
    public XPage doAddIteration( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException
    {
        if ( isIterationMapLost( request ) )
        {
            return getFormView( request );
        }

        cleanSession( request.getSession( ) );
        
        EntryTypeGroupUtils.manageAddingIteration( request );

        return getForm( request );
    }

    /**
     * Remove an iteration to an iteration group
     * 
     * @param request
     *            The HttpServletRequest
     * @return the page of the Form with the iteration removed
     * @throws UserNotSignedException
     * @throws SiteMessageException
     */
    @Action( value = ACTION_REMOVE_ITERATION )
    public XPage doRemoveIteration( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException
    {
        if ( isIterationMapLost( request ) )
        {
            return getFormView( request );
        }

        cleanSession( request.getSession( ) );

        EntryTypeGroupUtils.manageRemoveIterationGroup( request );

        return getForm( request );
    }

    /**
     * Submit the Form with the data filling by the user
     * 
     * @param request
     *            The HttpServletRequest
     * @return the page associated to the recap page
     * @throws UserNotSignedException
     * @throws SiteMessageException
     */
    @Action( value = ACTION_SUBMIT_FORM )
    public XPage doSubmitForm( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException
    {
        if ( isIterationMapLost( request ) )
        {
            return getFormView( request );
        }

        XPage page = getRecap( request );

        Form form = getFormFromRequest( request );

        validateDraft( request, form );

        return page;
    }

    /**
     * Reset a Form
     * 
     * @param request
     *            The HttpServletRequest
     * @return the XPage of the Form
     * @throws SiteMessageException
     * @throws UserNotSignedException
     */
    @Action( value = ACTION_RESET_FORM )
    public XPage doResetForm( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException
    {
        if ( isIterationMapLost( request ) )
        {
            return getFormView( request );
        }

        HttpSession session = request.getSession( );

        cleanSession( session );

        // Remove the uploaded file in session
        FormAsynchronousUploadHandler.getHandler( ).removeSessionFiles( session.getId( ) );

        Map<String, String> model = new LinkedHashMap<>( );
        model.put( FormConstants.PARAMETER_ID_FORM, request.getParameter( FormConstants.PARAMETER_ID_FORM ) );

        return redirect( request, VIEW_FORM, model );
    }

    /**
     * Save the draft of a Form
     * 
     * @param request
     *            The HttpServletRequest
     * @return
     * @throws SiteMessageException
     * @throws UserNotSignedException
     */
    @Action( value = ACTION_SAVE_DRAFT )
    public XPage doSaveDraft( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException
    {
        if ( isIterationMapLost( request ) )
        {
            return getFormView( request );
        }

        Form form = getFormFromRequest( request );

        // Create a new FormSubmit object
        FormSubmit formSubmit = new FormSubmit( );
        formSubmit.setForm( form );

        // Parse request
        doInsertResponseInFormSubmit( request, formSubmit, true );

        // Save the draft
        FormDraftBackupService.saveDraft( request, form );

        return getForm( request );
    }

    /**
     * Return the requirement view
     * 
     * @param request
     *            The HttpServletRequest
     * @return the view associated to the Requirement
     * @throws SiteMessageException
     */
    @View( value = VIEW_REQUIREMENT )
    public XPage getRequirementView( HttpServletRequest request ) throws SiteMessageException
    {
        XPage page = new XPage( );
        page.setTitle( I18nService.getLocalizedString( PROPERTY_XPAGE_PAGETITLE, request.getLocale( ) ) );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_XPAGE_PATHLABEL, request.getLocale( ) ) );
        page.setContent( getRequirement( request ) );

        return page;
    }

    /**
     * Validate the recap of a Form
     * 
     * @param request
     *            The HttpServletRequest
     * @return the view after the validation of the recap
     * @throws SiteMessageException
     */
    @Action( value = ACTION_VALIDATE_RECAP )
    public XPage doValidateRecap( HttpServletRequest request ) throws SiteMessageException
    {
        XPage page = new XPage( );
        Locale locale = request.getLocale( );

        page.setTitle( I18nService.getLocalizedString( PROPERTY_XPAGE_PAGETITLE, locale ) );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_XPAGE_PATHLABEL, locale ) );
        page.setContent( getResult( request, true ) );

        // Remove existing draft
        FormDraftBackupService.validateDraft( request, getFormFromRequest( request ) );

        return page;
    }

    /**
     * Initialize the session object
     * 
     * @param session
     *            The session to clean
     * @param strSessionId
     *            The id of the session
     * @param nIdForm
     *            The id of the form
     */
    private void initializeSession( HttpSession session, String strSessionId, int nIdForm )
    {
        // Reset all responses in session if the user has not submitted any form
        // there is a few chances that PARAMETER_SESSION may not be blank but will be overwritten by draft if any
        cleanSession( session );
        session.removeAttribute( SESSION_VALIDATE_REQUIREMENT );

        if ( strSessionId == null )
        {
            strSessionId = session.getId( );
        }

        // Remove the file in session
        FormAsynchronousUploadHandler.getHandler( ).removeSessionFiles( strSessionId );

        // Add the iterationMap to the session
        populateIterationGroupMap( session, nIdForm );
    }

    /**
     * Clean the session. Remove the attributes SESSION_FORM_LIST_SUBMITTED_RESPONSES and SESSION_FORM_ERRORS from the session
     * 
     * @param session
     *            The session to cleaned
     */
    private void cleanSession( HttpSession session )
    {
        if ( session != null )
        {
            FormUtils.removeResponses( session );
            FormUtils.removeFormErrors( session );
        }
    }

    /**
     * Return the Form from the request
     * 
     * @param request
     *            The HttpServletRequest to retrieve the Form from
     * @return the Form which is in the request
     * @throws SiteMessageException
     */
    private Form getFormFromRequest( HttpServletRequest request ) throws SiteMessageException
    {
        // Find the required form
        Form form = getFormFromIdParameter( request );

        HttpSession session = request.getSession( );

        // Find form submit stored in session if the Form has not been found with the parameter values
        if ( form == null )
        {
            form = getFormFromFormSubmit( session );
        }

        // Put real base url in session if Https is supported
        if ( form != null && form.isSupportHTTPS( ) && AppHTTPSService.isHTTPSSupportEnabled( ) )
        {
            session.setAttribute( AppPathService.SESSION_BASE_URL, AppPathService.getBaseUrl( request ) );
        }

        return form;
    }

    /**
     * Return the Form associated to the form id specified in the request
     * 
     * @param request
     *            The request to retrieve the parameter from
     * @return the Form associated to the id in the request or null if not found
     * @throws SiteMessageException
     */
    private Form getFormFromIdParameter( HttpServletRequest request ) throws SiteMessageException
    {
        Form form = null;

        String strIdForm = request.getParameter( FormConstants.PARAMETER_ID_FORM );
        if ( StringUtils.isNotBlank( strIdForm ) )
        {
            int nIdForm = NumberUtils.INTEGER_MINUS_ONE;
            try
            {
                nIdForm = Integer.parseInt( strIdForm );
            }
            catch( NumberFormatException ne )
            {
                AppLogService.error( ne );
                SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );
            }

            form = FormHome.findByPrimaryKey( nIdForm, PluginService.getPlugin( FormPlugin.PLUGIN_NAME ) );
        }

        return form;
    }

    /**
     * Return the Form from the FormSubmit object in the session
     * 
     * @param session
     *            The session to retrieve the attribute from
     * @return the Form associated to the FormSubmit in the session
     */
    private Form getFormFromFormSubmit( HttpSession session )
    {
        Form form = null;

        Object objFormSubmit = session.getAttribute( PARAMETER_FORM_SUBMIT );

        if ( objFormSubmit != null )
        {
            try
            {
                form = ( (FormSubmit) objFormSubmit ).getForm( );
            }
            catch( ClassCastException ce )
            {
                AppLogService.error( ce );
            }
        }

        return form;
    }

    /**
     * Save the draft of a Form and return the content of the Result page if necessary
     * 
     * @param request
     *            The HttpServletRequest
     * @param form
     *            The form object to save the draft
     * @return the content of a result page if the FormSubmit object exist null otherwise
     * @throws SiteMessageException
     */
    private String validateDraft( HttpServletRequest request, Form form ) throws SiteMessageException
    {
        String strResult = null;
        HttpSession session = request.getSession( );

        // Validate draft if the form does not have a recap and the session
        // contains a list of responses without errors
        if ( !FormService.getInstance( ).hasRecap( form ) && !FormService.getInstance( ).hasFormErrors( session ) )
        {
            // Remove existing draft
            FormDraftBackupService.validateDraft( request, form );
        }
        else
        {
            // Save draft with the formSubmit if exist
            FormSubmit formSubmit = (FormSubmit) session.getAttribute( PARAMETER_FORM_SUBMIT );

            if ( formSubmit == null )
            {
                FormDraftBackupService.saveDraft( request, form );
            }
            else
            {
                strResult = getResult( request, false );
                FormDraftBackupService.saveDraft( request, formSubmit );
            }
        }

        // Return the content of the result page if the FormSubmit exists
        return strResult;
    }

    /**
     * Perform formSubmit in database and return the result page
     * 
     * @param request
     *            The HTTP request
     * @param bDoPerformSubmit
     *            true if commit form submit, false otherwise
     * @return the form recap
     * @throws SiteMessageException
     *             SiteMessageException
     */
    private String getResult( HttpServletRequest request, boolean bDoPerformSubmit ) throws SiteMessageException
    {
        // Retrieve the session from the request
        HttpSession session = request.getSession( );

        if ( ( session == null ) || ( session.getAttribute( PARAMETER_FORM_SUBMIT ) == null ) )
        {
            SiteMessageService.setMessage( request, MESSAGE_SESSION_LOST, SiteMessage.TYPE_STOP );

            // sonar "Correctness - Possible null pointer dereference" - exception already thrown by SiteMessageService.setMessage
            throw new SiteMessageException( );
        }

        // For the entry unique
        Locale locale = request.getLocale( );
        FormSubmit formSubmit = (FormSubmit) session.getAttribute( PARAMETER_FORM_SUBMIT );
        Form form = formSubmit.getForm( );

        if ( formSubmit.getListResponse( ) != null )
        {
            for ( Response response : formSubmit.getListResponse( ) )
            {
                if ( ( response != null ) && ( response.getEntry( ) != null ) && response.getEntry( ).isUnique( ) )
                {
                    String strValueEntry = response.getToStringValueResponse( );

                    if ( strValueEntry != null )
                    {
                        ResponseFilter filter = new ResponseFilter( );
                        filter.setIdEntry( response.getEntry( ).getIdEntry( ) );

                        Collection<Response> listSubmittedResponses = getResponseService( ).getResponseList( filter, false );

                        for ( Response submittedResponse : listSubmittedResponses )
                        {
                            String strSubmittedResponse = EntryTypeServiceManager.getEntryTypeService( submittedResponse.getEntry( ) )
                                    .getResponseValueForRecap( submittedResponse.getEntry( ), request, submittedResponse, locale );

                            if ( !strValueEntry.equals( StringUtils.EMPTY ) && ( strSubmittedResponse != null )
                                    && !strSubmittedResponse.equals( StringUtils.EMPTY ) && strValueEntry.equalsIgnoreCase( strSubmittedResponse ) )
                            {
                                Object [ ] tabRequiredFields = {
                                    response.getEntry( ).getTitle( )
                                };
                                SiteMessageService.setMessage( request, MESSAGE_UNIQUE_FIELD, tabRequiredFields, SiteMessage.TYPE_STOP );
                            }
                        }
                    }
                }
            }
        }

        // Retrieve the plugin
        Plugin plugin = PluginService.getPlugin( FormPlugin.PLUGIN_NAME );

        // Validates the form submit using validators
        ValidatorService.getInstance( ).validateForm( request, formSubmit, plugin );

        if ( bDoPerformSubmit )
        {
            doPerformFormSubmit( request, session, formSubmit, plugin );
        }

        Recap recap = RecapHome.findByPrimaryKey( form.getRecap( ).getIdRecap( ), plugin );

        if ( form.isSupportHTTPS( ) && AppHTTPSService.isHTTPSSupportEnabled( ) )
        {
            recap.setBackUrl( AppHTTPSService.getHTTPSUrl( request ) + recap.getBackUrl( ) );
        }

        // During the validation of the form we will restore all original id of the entries
        // because all entries belong to an iterable group entry have their identifiers recreated
        if ( bDoPerformSubmit )
        {
            manageFormSubmitResponseOnValidate( formSubmit );
        }

        Map<String, Object> model = new HashMap<String, Object>( );

        model.put( MARK_RECAP, recap );
        model.put( FormConstants.MARK_FORM_SUBMIT, formSubmit );
        model.put( MARK_ENTRY_TYPE_SESSION, getEntryTypeService( ).getEntryType( EntryTypeSession.BEAN_NAME ) );
        model.put( MARK_ENTRY_TYPE_NUMBERING, getEntryTypeService( ).getEntryType( EntryTypeNumbering.BEAN_NAME ) );

        // String strPageId = request.getParameter( PARAMETER_PAGE_ID );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_RECAP_FORM_SUBMIT, locale, model );

        return template.getHtml( );
    }

    /**
     * Generate the HTML code for forms list xpage
     * 
     * @param request
     *            The {@link HttpServletRequest}
     * @return The HTML code for forms list xpage
     * @throws SiteMessageException
     *             If a site message needs to be display
     */
    private String getFormList( HttpServletRequest request ) throws SiteMessageException
    {
        FormFilter filter = new FormFilter( );
        filter.setIdState( Form.STATE_ENABLE );

        String strOrder = StringUtils.defaultString( request.getParameter( MARK_ORDER ) );
        filter.setOrder( strOrder );

        String strAsc = StringUtils.defaultString( request.getParameter( MARK_ASC ) );
        filter.setAsc( strAsc );

        HashMap<String, Object> model = new HashMap<String, Object>( );
        Plugin plugin = PluginService.getPlugin( FormPlugin.PLUGIN_NAME );
        Collection<Form> listAllForms = FormHome.getFormList( filter, plugin );

        if ( SecurityService.isAuthenticationEnable( ) && ( SecurityService.getInstance( ).getRegisteredUser( request ) == null ) )
        {
            Collection<Form> listForms = new ArrayList<Form>( );

            for ( Form form : listAllForms )
            {
                if ( !form.isActiveMyLuteceAuthentification( ) )
                {
                    listForms.add( form );
                }
            }

            model.put( MARK_LIST_FORMS, listForms );
        }
        else
        {
            model.put( MARK_LIST_FORMS, listAllForms );
        }

        model.put( MARK_ORDER, strOrder );
        model.put( MARK_ASC, strAsc );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_LIST_FORMS, request.getLocale( ), model );

        return template.getHtml( );
    }
    
    /**
     * Generate the HTML code for form xpage
     * 
     * @param request
     *            The {@link HttpServletRequest}
     * @return The HTML code for form xpage
     * @throws SiteMessageException
     *             If a site message should be displayed
     * @throws UserNotSignedException
     *             If the user is not signed, and the form requires an authentication
     */
    private XPage getForm( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException
    {
        XPage page = new XPage( );
        Plugin plugin = PluginService.getPlugin( FormPlugin.PLUGIN_NAME );
        Map<String, Object> model = new HashMap<String, Object>( );
        String strFormId = request.getParameter( FormConstants.PARAMETER_ID_FORM );

        if ( !strFormId.matches( REGEX_ID ) )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_ERROR );
        }

        Form form = FormHome.findByPrimaryKey( Integer.parseInt( strFormId ), plugin );

        if ( form == null )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_ERROR );

            return null;
        }

        ResponseFilter responseFilter = new ResponseFilter( );
        responseFilter.setIdResource( Integer.parseInt( strFormId ) );

        // If max number of responses is set and reached, then deactivate access to the form
        if ( ( form.getMaxNumberResponse( ) > 0 ) && ( FormSubmitHome.getCountFormSubmit( responseFilter, plugin ) >= form.getMaxNumberResponse( ) ) )
        {
            model.put( MARK_MESSAGE_FORM_INACTIVE, form.getUnavailabilityMessage( ) );
        }

        // Check if the session contains all the attributes set by the mandatory EntryTypeSession
        if ( !FormService.getInstance( ).isSessionValid( form, request ) )
        {
            String strUrlReturn = AppPropertiesService.getProperty( PROPERTY_SESSION_INVALIDATE_URL_RETURN );
            SiteMessageService.setMessage( request, Messages.USER_ACCESS_DENIED, SiteMessage.TYPE_STOP, strUrlReturn );
        }

        // Check if the form needs MyLutece authentication
        checkMyLuteceAuthentification( form, request );

        if ( StringUtils.isNotBlank( form.getFrontOfficeTitle( ) ) )
        {
            page.setTitle( form.getFrontOfficeTitle( ) );
            page.setPathLabel( form.getFrontOfficeTitle( ) );
        }
        else
        {
            page.setTitle( form.getTitle( ) );
            page.setPathLabel( form.getTitle( ) );
        }

        // Retrieve the session from the request
        HttpSession session = request.getSession( );

        if ( !form.isActive( ) )
        {
            model.put( MARK_MESSAGE_FORM_INACTIVE, form.getUnavailabilityMessage( ) );
        }
        else
        {
            String strUrlAction = JSP_DO_SUBMIT_FORM;

            if ( AppHTTPSService.isHTTPSSupportEnabled( ) )
            {
                session.setAttribute( AppPathService.SESSION_BASE_URL, AppPathService.getBaseUrl( request ) );
                strUrlAction = AppHTTPSService.getHTTPSUrl( request ) + strUrlAction;
            }

            model.put( MARK_FORM_HTML, FormUtils.getHtmlForm( form, strUrlAction + form.getIdForm( ), request.getLocale( ), true, request ) );
            model.put( FormConstants.MARK_FORM, form );
        }

        // The draft is saved either by clicking on "save" or by clicking on "validate"
        boolean bIsDraftSaved = false;

        if ( FormDraftBackupService.isDraftSupported( ) )
        {
            bIsDraftSaved = ( request.getParameter( FormConstants.PARAMETER_ID_FORM ) != null )
                    && ( request.getParameter( PARAMETER_ACTION_SUBMIT_FORM ) != null || request.getParameter( PARAMETER_ACTION_SAVE_DRAFT ) != null );
        }

        model.put( MARK_IS_DRAFT_SAVED, bIsDraftSaved );

        // Check if there are responses in the session. If so, then there are errors
        model.put( MARK_FORM_ERRORS, FormUtils.getFormErrors( session ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_FORM, request.getLocale( ), model );
        page.setContent( template.getHtml( ) );

        return page;
    }

    /**
     * if the recap is activate perform form submit in session and return the recap page else perform form submit in database and return the result page
     * 
     * @param request
     *            The HTTP request
     * @return the form recap
     * @throws SiteMessageException
     *             SiteMessageException
     * @throws UserNotSignedException
     *             If the user is not signed, and the form requires an authentication
     */
    private XPage getRecap( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException
    {
        List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>( );
        int nIdForm = -1;
        Map<String, Object> model = new HashMap<String, Object>( );
        Locale locale = request.getLocale( );
        String strIdForm = request.getParameter( FormConstants.PARAMETER_ID_FORM );

        if ( ( strIdForm != null ) && !strIdForm.equals( StringUtils.EMPTY ) )
        {
            try
            {
                nIdForm = Integer.parseInt( strIdForm );
            }
            catch( NumberFormatException ne )
            {
                AppLogService.error( ne.getMessage( ), ne );
                SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );
            }
        }

        if ( nIdForm == GenericAttributesUtils.CONSTANT_ID_NULL )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );
        }

        Plugin plugin = PluginService.getPlugin( FormPlugin.PLUGIN_NAME );
        Form form = FormHome.findByPrimaryKey( nIdForm, plugin );

        checkMyLuteceAuthentification( form, request );

        // test already vote
        // if special condition are on
        String strRequirement = request.getParameter( PARAMETER_REQUIREMENT );

        if ( form.isActiveCaptcha( ) && PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) )
        {
            CaptchaSecurityService captchaSecurityService = new CaptchaSecurityService( );

            if ( !captchaSecurityService.validate( request ) )
            {
                listFormErrors.add( new CaptchaFormError( nIdForm, locale ) );
            }
        }

        // create form response
        FormSubmit formSubmit = new FormSubmit( );
        formSubmit.setForm( form );
        formSubmit.setDateResponse( FormUtils.getCurrentTimestamp( ) );

        if ( form.isActiveStoreAdresse( ) )
        {
            formSubmit.setIp( request.getRemoteAddr( ) );
        }

        formSubmit.setForm( form );

        HttpSession session = request.getSession( );
        boolean bValidateRequirement = true;

        if ( form.isActiveRequirement( ) && ( strRequirement == null ) )
        {
            session.setAttribute( SESSION_VALIDATE_REQUIREMENT, false );
            listFormErrors.add( new RequirementFormError( nIdForm, locale ) );
            bValidateRequirement = false;
        }
        else
        {
            session.setAttribute( SESSION_VALIDATE_REQUIREMENT, true );
        }

        listFormErrors.addAll( doInsertResponseInFormSubmit( request, formSubmit, true ) );

        if ( !listFormErrors.isEmpty( ) || !bValidateRequirement )
        {
            FormUtils.restoreFormErrors( session, listFormErrors );

            // Add an attribute in the request to tell that there are errors during the validation of the form
            request.setAttribute( FormConstants.ATTRIBUTE_RETURN_FROM_ERRORS, Boolean.TRUE );

            return getForm( request );
        }

        // get form Recap
        Recap recap = RecapHome.findByPrimaryKey( form.getRecap( ).getIdRecap( ), plugin );

        // Sort the list of response with the group management to the form
        List<Response> responseManagedList = EntryTypeGroupUtils.orderResponseList( request, formSubmit.getListResponse( ) );
        formSubmit.setListResponse( responseManagedList );

        if ( ( recap != null ) && recap.isRecapData( ) )
        {
            model.put( MARK_VALIDATE_RECAP, true );

            session.setAttribute( PARAMETER_FORM_SUBMIT, formSubmit );
            FormUtils.removeResponses( session );
            FormUtils.removeFormErrors( session );
            session.removeAttribute( SESSION_VALIDATE_REQUIREMENT );

            // convert the value of the object response to string
            for ( Response response : formSubmit.getListResponse( ) )
            {
                if ( StringUtils.isNotBlank( response.getResponseValue( ) ) || ( response.getFile( ) != null ) )
                {
                    response.setToStringValueResponse( EntryTypeServiceManager.getEntryTypeService( response.getEntry( ) ).getResponseValueForRecap(
                            response.getEntry( ), request, response, locale ) );
                }
                else
                {
                    response.setToStringValueResponse( StringUtils.EMPTY );
                }
            }

            if ( form.isSupportHTTPS( ) && AppHTTPSService.isHTTPSSupportEnabled( ) )
            {
                recap.setBackUrl( AppHTTPSService.getHTTPSUrl( request ) + recap.getBackUrl( ) );
            }
        }
        else
        {
            doPerformFormSubmit( request, session, formSubmit, plugin );
        }

        model.put( MARK_RECAP, recap );
        model.put( FormConstants.MARK_FORM_SUBMIT, formSubmit );
        model.put( MARK_ENTRY_TYPE_SESSION, getEntryTypeService( ).getEntryType( EntryTypeSession.BEAN_NAME ) );
        model.put( MARK_ENTRY_TYPE_NUMBERING, getEntryTypeService( ).getEntryType( EntryTypeNumbering.BEAN_NAME ) );

        String strActionUrl;

        if ( form.isSupportHTTPS( ) && AppHTTPSService.isHTTPSSupportEnabled( ) )
        {
            strActionUrl = AppHTTPSService.getHTTPSUrl( request ) + JSP_PAGE_FORM;
        }
        else
        {
            strActionUrl = JSP_PAGE_FORM;
        }

        model.put( FormConstants.MARK_URL_ACTION, strActionUrl );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_RECAP_FORM_SUBMIT, locale, model );

        // See result
        XPage page = new XPage( );
        page.setTitle( I18nService.getLocalizedString( PROPERTY_XPAGE_PAGETITLE, request.getLocale( ) ) );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_XPAGE_PATHLABEL, request.getLocale( ) ) );
        page.setContent( template.getHtml( ) );

        return page;
    }

    /**
     * Return the form requirement
     * 
     * @param request
     *            The HTTP request
     * @return the form recap
     * @throws SiteMessageException
     *             SiteMessageException
     */
    private String getRequirement( HttpServletRequest request ) throws SiteMessageException
    {
        int nIdForm = -1;
        Map<String, Object> model = new HashMap<String, Object>( );
        Locale locale = request.getLocale( );
        String strIdForm = request.getParameter( FormConstants.PARAMETER_ID_FORM );

        if ( ( strIdForm != null ) && !strIdForm.equals( StringUtils.EMPTY ) )
        {
            try
            {
                nIdForm = Integer.parseInt( strIdForm );
            }
            catch( NumberFormatException ne )
            {
                AppLogService.error( ne );
                SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );
            }
        }

        if ( nIdForm == -1 )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );
        }

        Form form = FormHome.findByPrimaryKey( nIdForm, PluginService.getPlugin( FormPlugin.PLUGIN_NAME ) );
        model.put( MARK_REQUIREMENT, form.getRequirement( ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_REQUIREMENT_FORM, locale, model );

        return template.getHtml( );
    }

    /**
     * insert response in the form submit
     * 
     * @param request
     *            request The HTTP request
     * @param formSubmit
     *            Form Submit
     * @param bReturnErrors
     *            true if errors must be returned
     * @return true if there is an error, false otherwise
     */
    public List<GenericAttributeError> doInsertResponseInFormSubmit( HttpServletRequest request, FormSubmit formSubmit, boolean bReturnErrors )
    {
        List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>( );
        Locale locale = request.getLocale( );

        EntryFilter filter = new EntryFilter( );
        filter.setIdResource( formSubmit.getForm( ).getIdForm( ) );
        filter.setResourceType( Form.RESOURCE_TYPE );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        filter.setIdIsComment( EntryFilter.FILTER_FALSE );

        List<Entry> listEntryFirstLevel = EntryHome.getEntryList( filter );

        List<Response> listResponse = new ArrayList<Response>( );
        formSubmit.setListResponse( listResponse );

        Map<Integer, List<Response>> listSubmittedResponses = new HashMap<Integer, List<Response>>( );
        FormUtils.restoreResponses( request.getSession( ), listSubmittedResponses );

        Plugin plugin = PluginService.getPlugin( FormPlugin.PLUGIN_NAME );
        for ( Entry entry : listEntryFirstLevel )
        {
            listFormErrors.addAll( FormUtils.getResponseEntry( request, entry.getIdEntry( ), plugin, formSubmit, false, bReturnErrors, locale ) );
        }

        return listFormErrors;
    }

    /**
     * perform the form submit in database
     * 
     * @param request
     *            The HTTP request
     * @param session
     *            the http session
     * @param formSubmit
     *            Form Submit
     * @param plugin
     *            the Plugin
     * @throws SiteMessageException
     *             SiteMessageException
     */
    public void doPerformFormSubmit( HttpServletRequest request, HttpSession session, FormSubmit formSubmit, Plugin plugin ) throws SiteMessageException
    {
        Locale locale = request.getLocale( );
        Form form = formSubmit.getForm( );

        if ( !form.isActive( ) )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR_FORM_INACTIVE, SiteMessage.TYPE_STOP );
        }

        // If the number of submitted response is set to one, then we check if the user
        // has not already answer to the form before submitting the response
        if ( form.isLimitNumberResponse( ) )
        {
            if ( session.getAttribute( FormConstants.PARAMETER_ID_FORM + formSubmit.getForm( ).getIdForm( ) ) != null )
            {
                SiteMessageService.setMessage( request, MESSAGE_ALREADY_SUBMIT_ERROR, SiteMessage.TYPE_STOP );
            }
            else
            {
                session.setAttribute( FormConstants.PARAMETER_ID_FORM + formSubmit.getForm( ).getIdForm( ), PARAMETER_VOTED );
            }
        }

        // Validates the form submit using validators
        ValidatorService.getInstance( ).validateForm( request, formSubmit, plugin );

        TransactionManager.beginTransaction( plugin );

        try
        {
            formSubmit.setIdFormSubmit( FormSubmitHome.create( formSubmit, plugin ) );
            getResponseService( ).create( formSubmit );

            // Process all outputProcess
            for ( IOutputProcessor outputProcessor : OutputProcessorService.getInstance( ).getProcessorsByIdForm( formSubmit.getForm( ).getIdForm( ) ) )
            {
                outputProcessor.process( formSubmit, request, plugin );
            }

            TransactionManager.commitTransaction( plugin );
        }
        catch( Exception ex )
        {
            // something very wrong happened... a database check might be needed
            AppLogService.error( ex.getMessage( ) + " for FormSubmit " + formSubmit.getIdFormSubmit( ), ex );
            TransactionManager.rollBack( plugin );
            // revert
            // We do not remove the for submit since we rolled back the transaction
            // FormSubmitHome.remove( formSubmit.getIdFormSubmit( ), plugin );
            // throw a message to the user
            SiteMessageService.setMessage( request, MESSAGE_SUBMIT_SAVE_ERROR, SiteMessage.TYPE_ERROR );
        }

        // Notify new form submit
        FormUtils.sendNotificationMailFormSubmit( formSubmit, locale );

        // We can safely remove session files : they are validated
        FormAsynchronousUploadHandler.getHandler( ).removeSessionFiles( session.getId( ) );
    }

    /**
     * check if authentification
     * 
     * @param form
     *            Form
     * @param request
     *            HttpServletRequest
     * @throws UserNotSignedException
     *             exception if the form requires an authentification and the user is not logged
     */
    private void checkMyLuteceAuthentification( Form form, HttpServletRequest request ) throws UserNotSignedException
    {
        // Try to register the user in case of external authentication
        if ( SecurityService.isAuthenticationEnable( ) )
        {
            if ( SecurityService.getInstance( ).isExternalAuthentication( ) )
            {
                // The authentication is external
                // Should register the user if it's not already done
                if ( SecurityService.getInstance( ).getRegisteredUser( request ) == null )
                {
                    if ( ( SecurityService.getInstance( ).getRemoteUser( request ) == null ) && ( form.isActiveMyLuteceAuthentification( ) ) )
                    {
                        // Authentication is required to access to the portal
                        throw new UserNotSignedException( );
                    }
                }
            }
            else
            {
                // If portal authentication is enabled and user is null and the requested URL
                // is not the login URL, user cannot access to Portal
                if ( ( form.isActiveMyLuteceAuthentification( ) ) && ( SecurityService.getInstance( ).getRegisteredUser( request ) == null )
                        && !SecurityService.getInstance( ).isLoginUrl( request ) )
                {
                    // Authentication is required to access to the portal
                    throw new UserNotSignedException( );
                }
            }
        }
    }

    /**
     * Do clean responses of a form
     * 
     * @param request
     *            The request
     * @return The error code
     */
    public static String doCleanFormAnswers( HttpServletRequest request )
    {
        try
        {
            String strKey = request.getParameter( FormConstants.PARAMETER_KEY );
            String strPrivateKey = AppPropertiesService.getProperty( PROPERTY_CLEAN_FORM_ANSWERS_KEY );

            if ( ( strPrivateKey != null ) && StringUtils.isNotEmpty( strPrivateKey ) && !StringUtils.equals( strKey, strPrivateKey ) )
            {
                AppLogService.error( "Illegal attempt to clean form responses : " + SecurityUtil.getRealIp( request ) );

                return AppPropertiesService.getProperty( PROPERTY_CLEAN_FORM_ANSWERS_RETURN_CODE_UNAUTHORIZED );
            }

            String strIdForm = request.getParameter( FormConstants.PARAMETER_ID_FORM );

            if ( ( strIdForm != null ) && StringUtils.isNumeric( strIdForm ) )
            {
                int nIdForm = Integer.parseInt( strIdForm );
                Form form = FormHome.findByPrimaryKey( nIdForm, FormUtils.getPlugin( ) );
                FormService.getInstance( ).cleanFormResponses( form );
            }

            return AppPropertiesService.getProperty( PROPERTY_CLEAN_FORM_ANSWERS_RETURN_CODE_OK );
        }
        catch( Exception e )
        {
            AppLogService.error( e.getMessage( ), e );

            return AppPropertiesService.getProperty( PROPERTY_CLEAN_FORM_ANSWERS_RETURN_CODE_KO );
        }
    }

    /**
     * Reset the identifiers of all entries foreach response of the formSubmit object
     * 
     * @param formSubmit
     *            The FormSubmit which contains the list of response
     */
    private void manageFormSubmitResponseOnValidate( FormSubmit formSubmit )
    {
        List<Integer> listIdEntryProcessed = new ArrayList<>( );
        if ( formSubmit != null && formSubmit.getForm( ) != null && formSubmit.getListResponse( ) != null && !formSubmit.getListResponse( ).isEmpty( ) )
        {
            Iterator<Response> iteratorResponse = formSubmit.getListResponse( ).iterator( );
            while ( iteratorResponse.hasNext( ) )
            {
                Response response = iteratorResponse.next( );
                if ( response != null && response.getEntry( ) != null )
                {
                    int nResponseIdEntry = response.getEntry( ).getIdEntry( );
                    if ( !listIdEntryProcessed.contains( nResponseIdEntry ) )
                    {
                        listIdEntryProcessed.add( nResponseIdEntry );
                    }
                    else
                    {
                        // We will remove all entry which are already present in the list for display only one graphic on the final page
                        iteratorResponse.remove( );
                    }
                }
            }
        }
    }

    /**
     * Populate the iteration map of a form and set it to the session
     * 
     * @param session
     *            The session to set the map inside
     * @param nIdForm
     *            The id of the form to create the iteration group from
     */
    private void populateIterationGroupMap( HttpSession session, int nIdForm )
    {
        if ( session != null )
        {
            Map<Integer, IterationGroup> mapIterationGroup = new LinkedHashMap<Integer, IterationGroup>( );
            List<Integer> listIdEntryGroupIterable = EntryTypeGroupUtils.findIdEntryGroupIterable( nIdForm );
            if ( listIdEntryGroupIterable != null )
            {
                for ( Integer identryIterableGroup : listIdEntryGroupIterable )
                {
                    mapIterationGroup.put( identryIterableGroup, new IterationGroup( identryIterableGroup ) );
                }
            }

            session.setAttribute( FormConstants.SESSION_ITERATION_MAP, mapIterationGroup );
        }
    }

    /**
     * Detect if the iteration map of the form is lost or not.
     * 
     * @param request
     *            The HttpServletRequest to retrieve the map from its session
     * @return true if the Iteration map is no more present in the session of the request false otherwise.
     */
    private boolean isIterationMapLost( HttpServletRequest request )
    {
        return EntryTypeGroupUtils.retrieveIterationMap( request ) == null;
    }

    /**
     * Get the response service
     * 
     * @return
     */
    private IResponseService getResponseService( )
    {
        if ( _responseService == null )
        {
            _responseService = SpringContextService.getBean( FormUtils.BEAN_FORM_RESPONSE_SERVICE );
        }

        return _responseService;
    }

    /**
     * Get the entry type service
     * 
     * @return The entry type service
     */
    private EntryTypeService getEntryTypeService( )
    {
        if ( _entryTypeService == null )
        {
            _entryTypeService = SpringContextService.getBean( FormUtils.BEAN_ENTRY_TYPE_SERVICE );
        }

        return _entryTypeService;
    }
}
