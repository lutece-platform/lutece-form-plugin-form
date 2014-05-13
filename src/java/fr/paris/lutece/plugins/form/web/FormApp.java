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

import fr.paris.lutece.plugins.form.business.CaptchaFormError;
import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormFilter;
import fr.paris.lutece.plugins.form.business.FormHome;
import fr.paris.lutece.plugins.form.business.FormSubmit;
import fr.paris.lutece.plugins.form.business.FormSubmitHome;
import fr.paris.lutece.plugins.form.business.Recap;
import fr.paris.lutece.plugins.form.business.RecapHome;
import fr.paris.lutece.plugins.form.business.RequirementFormError;
import fr.paris.lutece.plugins.form.business.outputprocessor.IOutputProcessor;
import fr.paris.lutece.plugins.form.service.EntryTypeService;
import fr.paris.lutece.plugins.form.service.FormService;
import fr.paris.lutece.plugins.form.service.IResponseService;
import fr.paris.lutece.plugins.form.service.OutputProcessorService;
import fr.paris.lutece.plugins.form.service.draft.FormDraftBackupService;
import fr.paris.lutece.plugins.form.service.entrytype.EntryTypeNumbering;
import fr.paris.lutece.plugins.form.service.entrytype.EntryTypeSession;
import fr.paris.lutece.plugins.form.service.upload.FormAsynchronousUploadHandler;
import fr.paris.lutece.plugins.form.service.validator.ValidatorService;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.plugins.form.utils.JSONUtils;
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
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.http.SecurityUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;


/**
 * This class manages Form page.
 * 
 */
public class FormApp implements XPageApplication
{
    /**
     * Parameter 'id_form'
     */
    public static final String PARAMETER_ID_FORM = "id_form";

    /**
     * /** Serial version UID
     */
    private static final long serialVersionUID = -1385222847493418480L;

    // markers
    private static final String MARK_RECAP = "recap";
    private static final String MARK_FORM_SUBMIT = "formSubmit";
    private static final String MARK_REQUIREMENT = "requirement";
    private static final String MARK_VALIDATE_RECAP = "validate_recap";
    private static final String MARK_LIST_FORMS = "forms_list";
    private static final String MARK_FORM_HTML = "form_html";
    private static final String MARK_FORM = "form";
    private static final String MARK_MESSAGE_FORM_INACTIVE = "form_inactive";
    private static final String MARK_URL_ACTION = "url_action";
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

    // request parameters
    private static final String PARAMETER_FORM_SUBMIT = "form_submit";
    private static final String PARAMETER_REQUIREMENT = "requirement";
    private static final String PARAMETER_VIEW_REQUIREMENT = "view_requirement";
    private static final String PARAMETER_VALIDATE_RECAP = "validate_recap";
    private static final String PARAMETER_VOTED = "voted";
    private static final String PARAMETER_SAVE = "save";
    private static final String PARAMETER_RESET = "reset";
    private static final String PARAMETER_SESSION = "session";
    private static final String PARAMETER_SAVE_DRAFT = "save_draft";
    private static final String PARAMETER_FIELD_INDEX = "field_index";

    // session
    private static final String SESSION_VALIDATE_REQUIREMENT = "session_validate_requirement";

    //message
    private static final String MESSAGE_ERROR = "form.message.Error";
    private static final String MESSAGE_ALREADY_SUBMIT_ERROR = "form.message.alreadySubmitError";
    private static final String MESSAGE_SUBMIT_SAVE_ERROR = "form.message.submitSaveError";
    private static final String MESSAGE_ERROR_FORM_INACTIVE = "form.message.errorFormInactive";
    private static final String MESSAGE_SESSION_LOST = "form.message.session.lost";
    private static final String MESSAGE_UNIQUE_FIELD = "form.message.errorUniqueField";
    private static final String EMPTY_STRING = "";

    // Urls
    private static final String JSP_DO_SUBMIT_FORM = "jsp/site/Portal.jsp?page=form&id_form=";
    private static final String JSP_PAGE_FORM = "jsp/site/Portal.jsp?page=form";

    // Misc
    private static final String REGEX_ID = "^[\\d]+$";
    private transient IResponseService _responseService;
    private transient EntryTypeService _entryTypeService;

    /**
     * Returns the Form XPage result content depending on the request parameters
     * and the current mode.
     * 
     * @param request The HTTP request.
     * @param nMode The current mode.
     * @param plugin The Plugin
     * @return The page content.
     * @throws SiteMessageException the SiteMessageException
     * @throws UserNotSignedException If the user has not signed and the form
     *             has a entry of type MyLutece user.
     */
    @Override
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin ) throws SiteMessageException,
            UserNotSignedException
    {
        XPage page = new XPage( );

        Form form = null;
        HttpSession session = request.getSession( );

        // we find the required form
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = -1;

        if ( ( strIdForm != null ) && !strIdForm.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdForm = Integer.parseInt( strIdForm );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );
                SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );
            }

            form = FormHome.findByPrimaryKey( nIdForm, plugin );
        }

        // Special case for upload fields : if no action is specified, a submit
        // button associated with an upload might have been pressed :
        String strUploadAction = FormAsynchronousUploadHandler.getHandler( ).getUploadAction( request );

        if ( strUploadAction != null )
        {
            // the formsubmit may no be reused
            FormSubmit formSubmit = new FormSubmit( );
            formSubmit.setForm( form );
            // Upload the file
            FormAsynchronousUploadHandler.getHandler( ).doUploadAction( request, strUploadAction );
            // parse request & save draft
            doInsertResponseInFormSubmit( request, formSubmit, false, plugin );
            FormDraftBackupService.saveDraft( request, form );
            FormUtils.removeFormErrors( session );

            return getForm( request, session, nMode, plugin );
        }

        if ( ( form == null ) && ( session.getAttribute( PARAMETER_FORM_SUBMIT ) != null ) )
        {
            // find form stored in session
            FormSubmit formSubmit = (FormSubmit) session.getAttribute( PARAMETER_FORM_SUBMIT );

            if ( formSubmit != null )
            {
                form = formSubmit.getForm( );
            }
        }

        if ( ( form != null ) && form.isSupportHTTPS( ) && AppHTTPSService.isHTTPSSupportEnabled( ) )
        {
            //Put real base url in session
            request.getSession( ).setAttribute( AppPathService.SESSION_BASE_URL, AppPathService.getBaseUrl( request ) );
        }

        if ( request.getParameter( PARAMETER_VALIDATE_RECAP ) != null )
        {
            //the "recap" (summary) is valide
            page.setTitle( I18nService.getLocalizedString( PROPERTY_XPAGE_PAGETITLE, request.getLocale( ) ) );
            page.setPathLabel( I18nService.getLocalizedString( PROPERTY_XPAGE_PATHLABEL, request.getLocale( ) ) );
            page.setContent( getResult( request, session, nMode, plugin, true ) );

            // remove existing draft
            FormDraftBackupService.validateDraft( request, form );
        }

        else if ( request.getParameter( PARAMETER_VIEW_REQUIREMENT ) != null )
        {
            //See conditional use
            page.setTitle( I18nService.getLocalizedString( PROPERTY_XPAGE_PAGETITLE, request.getLocale( ) ) );
            page.setPathLabel( I18nService.getLocalizedString( PROPERTY_XPAGE_PATHLABEL, request.getLocale( ) ) );
            page.setContent( getRequirement( request, nMode, plugin ) );
        }
        else if ( ( request.getParameter( PARAMETER_SAVE_DRAFT ) != null )
                && ( request.getParameter( PARAMETER_ID_FORM ) != null ) )
        {
            // the formsubmit may no be reused
            FormSubmit formSubmit = new FormSubmit( );
            formSubmit.setForm( form );
            // parse request & save draft
            doInsertResponseInFormSubmit( request, formSubmit, true, plugin );
            FormDraftBackupService.saveDraft( request, form );
            page = getForm( request, session, nMode, plugin );
        }
        else if ( request.getParameter( PARAMETER_RESET ) != null )
        {
            FormUtils.removeResponses( session );
            FormUtils.removeFormErrors( session );

            String strSessionId = request.getSession( ).getId( );
            FormAsynchronousUploadHandler.getHandler( ).removeSessionFiles( strSessionId );

            return getForm( request, session, nMode, plugin );
        }
        else if ( ( request.getParameter( PARAMETER_SAVE ) != null )
                && ( request.getParameter( PARAMETER_ID_FORM ) != null ) )
        {
            page = getRecap( request, session, nMode, plugin );

            // Validate draft if the form does not have a recap and the session
            // contains a list of responses without errors
            if ( !FormService.getInstance( ).hasRecap( form ) && !FormService.getInstance( ).hasFormErrors( session ) )
            {
                // remove existing draft
                FormDraftBackupService.validateDraft( request, form );
            }
            else
            {
                // save draft
                // we can get FormSubmit here
                FormSubmit formSubmit = (FormSubmit) session.getAttribute( PARAMETER_FORM_SUBMIT );

                getResult( request, session, nMode, plugin, false );

                if ( formSubmit == null )
                {
                    FormDraftBackupService.saveDraft( request, form );
                }
                else
                {
                    FormDraftBackupService.saveDraft( request, formSubmit );
                }
            }
        }
        else if ( request.getParameter( PARAMETER_ID_FORM ) != null )
        {
            // Reset all responses in session if the user has not submitted any form
            // there is a few chances that PARAMETER_SESSION may not be blank but will be overwritten by draft if any
            if ( StringUtils.isBlank( request.getParameter( PARAMETER_SESSION ) ) )
            {
                FormUtils.removeResponses( session );
                FormUtils.removeFormErrors( session );
                session.removeAttribute( SESSION_VALIDATE_REQUIREMENT );

                String strSessionId = request.getParameter( PARAMETER_SESSION );

                if ( strSessionId == null )
                {
                    strSessionId = request.getSession( ).getId( );
                }

                FormAsynchronousUploadHandler.getHandler( ).removeSessionFiles( strSessionId );
            }

            // try to restore draft
            // preProcessRequest return true if the form should not be displayed (for deletion...)
            if ( !FormDraftBackupService.preProcessRequest( request, form ) )
            {
                //Display Form
                page = getForm( request, session, nMode, plugin );
            }
        }
        else
        {
            //See forms list
            page.setTitle( I18nService.getLocalizedString( PROPERTY_XPAGE_LIST_FORMS_PAGETITLE, request.getLocale( ) ) );
            page.setPathLabel( I18nService.getLocalizedString( PROPERTY_XPAGE_LIST_FORMS_PATHLABEL, request.getLocale( ) ) );
            page.setContent( getFormList( request, session, nMode, plugin ) );
        }

        return page;
    }

    /**
     * Perform formSubmit in database and return the result page
     * @param request The HTTP request
     * @param session The HTTP session
     * @param nMode The current mode.
     * @param plugin The Plugin
     * @param bDoPerformSubmit true if commit form submit, false otherwise
     * @return the form recap
     * @throws SiteMessageException SiteMessageException
     */
    private String getResult( HttpServletRequest request, HttpSession session, int nMode, Plugin plugin,
            boolean bDoPerformSubmit ) throws SiteMessageException
    {
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

                        Collection<Response> listSubmittedResponses = getResponseService( ).getResponseList( filter,
                                false );

                        for ( Response submittedResponse : listSubmittedResponses )
                        {
                            String strSubmittedResponse = EntryTypeServiceManager.getEntryTypeService(
                                    submittedResponse.getEntry( ) ).getResponseValueForRecap(
                                    submittedResponse.getEntry( ), request, submittedResponse, locale );

                            if ( !strValueEntry.equals( EMPTY_STRING ) && ( strSubmittedResponse != null )
                                    && !strSubmittedResponse.equals( EMPTY_STRING )
                                    && strValueEntry.equalsIgnoreCase( strSubmittedResponse ) )
                            {
                                Object[] tabRequiredFields = { response.getEntry( ).getTitle( ) };
                                SiteMessageService.setMessage( request, MESSAGE_UNIQUE_FIELD, tabRequiredFields,
                                        SiteMessage.TYPE_STOP );
                            }
                        }
                    }
                }
            }
        }

        if ( bDoPerformSubmit )
        {
            doPerformFormSubmit( request, session, formSubmit, plugin );
        }

        Recap recap = RecapHome.findByPrimaryKey( form.getRecap( ).getIdRecap( ), plugin );

        if ( form.isSupportHTTPS( ) && AppHTTPSService.isHTTPSSupportEnabled( ) )
        {
            recap.setBackUrl( AppHTTPSService.getHTTPSUrl( request ) + recap.getBackUrl( ) );
        }

        Map<String, Object> model = new HashMap<String, Object>( );

        model.put( MARK_RECAP, recap );
        model.put( MARK_FORM_SUBMIT, formSubmit );
        model.put( MARK_ENTRY_TYPE_SESSION, getEntryTypeService( ).getEntryType( EntryTypeSession.BEAN_NAME ) );
        model.put( MARK_ENTRY_TYPE_NUMBERING, getEntryTypeService( ).getEntryType( EntryTypeNumbering.BEAN_NAME ) );

        //String strPageId = request.getParameter( PARAMETER_PAGE_ID );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_RECAP_FORM_SUBMIT, locale, model );

        return template.getHtml( );
    }

    /**
     * Generate the HTML code for forms list xpage
     * @param request The {@link HttpServletRequest}
     * @param session The {@link HttpSession}
     * @param nMode The mode
     * @param plugin The {@link Plugin}
     * @return The HTML code for forms list xpage
     * @throws SiteMessageException If a site message needs to be display
     */
    private String getFormList( HttpServletRequest request, HttpSession session, int nMode, Plugin plugin )
            throws SiteMessageException
    {
        FormFilter filter = new FormFilter( );
        filter.setIdState( Form.STATE_ENABLE );

        String strOrder = StringUtils.defaultString( request.getParameter( MARK_ORDER ) );
        filter.setOrder( strOrder );

        String strAsc = StringUtils.defaultString( request.getParameter( MARK_ASC ) );
        filter.setAsc( strAsc );

        HashMap<String, Object> model = new HashMap<String, Object>( );
        Collection<Form> listAllForms = FormHome.getFormList( filter, plugin );

        if ( SecurityService.isAuthenticationEnable( )
                && ( SecurityService.getInstance( ).getRegisteredUser( request ) == null ) )
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
     * @param request The {@link HttpServletRequest}
     * @param session The {@link HttpSession}
     * @param nMode The mode
     * @param plugin The {@link Plugin}
     * @return The HTML code for form xpage
     * @throws SiteMessageException If a site message should be displayed
     * @throws UserNotSignedException If the user is not signed, and the form
     *             requires an authentication
     */
    private XPage getForm( HttpServletRequest request, HttpSession session, int nMode, Plugin plugin )
            throws SiteMessageException, UserNotSignedException
    {
        XPage page = new XPage( );
        Map<String, Object> model = new HashMap<String, Object>( );
        String strFormId = request.getParameter( PARAMETER_ID_FORM );

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

        if ( !form.isActive( ) )
        {
            model.put( MARK_MESSAGE_FORM_INACTIVE, form.getUnavailabilityMessage( ) );
        }
        else
        {
            String strUrlAction = JSP_DO_SUBMIT_FORM;

            if ( AppHTTPSService.isHTTPSSupportEnabled( ) )
            {
                request.getSession( ).setAttribute( AppPathService.SESSION_BASE_URL,
                        AppPathService.getBaseUrl( request ) );
                strUrlAction = AppHTTPSService.getHTTPSUrl( request ) + strUrlAction;
            }

            model.put( MARK_FORM_HTML,
                    FormUtils.getHtmlForm( form, strUrlAction + form.getIdForm( ), request.getLocale( ), true, request ) );
            model.put( MARK_FORM, form );
        }

        // The draft is saved either by clicking on "save" or by clicking on "validate"
        boolean bIsDraftSaved = false;

        if ( FormDraftBackupService.isDraftSupported( ) )
        {
            bIsDraftSaved = ( request.getParameter( PARAMETER_ID_FORM ) != null )
                    && ( ( request.getParameter( PARAMETER_SAVE ) != null ) || ( request
                            .getParameter( PARAMETER_SAVE_DRAFT ) != null ) );
        }

        model.put( MARK_IS_DRAFT_SAVED, bIsDraftSaved );

        // Check if there are responses in the session. If so, then there are errors
        model.put( MARK_FORM_ERRORS, FormUtils.getFormErrors( session ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_FORM, request.getLocale( ), model );
        page.setContent( template.getHtml( ) );

        return page;
    }

    /**
     * if the recap is activate perform form submit in session and return the
     * recap page
     * else perform form submit in database and return the result page
     * @param request The HTTP request
     * @param session the http session
     * @param nMode The current mode.
     * @param plugin The Plugin
     * @return the form recap
     * @throws SiteMessageException SiteMessageException
     * @throws UserNotSignedException If the user is not signed, and the form
     *             requires an authentication
     */
    private XPage getRecap( HttpServletRequest request, HttpSession session, int nMode, Plugin plugin )
            throws SiteMessageException, UserNotSignedException
    {
        List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>( );
        int nIdForm = -1;
        Map<String, Object> model = new HashMap<String, Object>( );
        Locale locale = request.getLocale( );
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( ( strIdForm != null ) && !strIdForm.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdForm = Integer.parseInt( strIdForm );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne.getMessage( ), ne );
                SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );
            }
        }

        if ( nIdForm == GenericAttributesUtils.CONSTANT_ID_NULL )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );
        }

        Form form = FormHome.findByPrimaryKey( nIdForm, plugin );

        checkMyLuteceAuthentification( form, request );

        //test already vote
        //if special condition are on
        String strRequirement = request.getParameter( PARAMETER_REQUIREMENT );

        if ( form.isActiveCaptcha( ) && PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) )
        {
            CaptchaSecurityService captchaSecurityService = new CaptchaSecurityService( );

            if ( !captchaSecurityService.validate( request ) )
            {
                listFormErrors.add( new CaptchaFormError( nIdForm, locale ) );
            }
        }

        //create form response
        FormSubmit formSubmit = new FormSubmit( );
        formSubmit.setForm( form );
        formSubmit.setDateResponse( FormUtils.getCurrentTimestamp( ) );

        if ( form.isActiveStoreAdresse( ) )
        {
            formSubmit.setIp( request.getRemoteAddr( ) );
        }

        formSubmit.setForm( form );

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

        listFormErrors.addAll( doInsertResponseInFormSubmit( request, formSubmit, true, plugin ) );

        if ( !listFormErrors.isEmpty( ) || !bValidateRequirement )
        {
            FormUtils.restoreFormErrors( session, listFormErrors );

            return getForm( request, session, nMode, plugin );
        }

        //get form Recap
        Recap recap = RecapHome.findByPrimaryKey( form.getRecap( ).getIdRecap( ), plugin );

        if ( ( recap != null ) && recap.isRecapData( ) )
        {
            model.put( MARK_VALIDATE_RECAP, true );

            session.setAttribute( PARAMETER_FORM_SUBMIT, formSubmit );
            FormUtils.removeResponses( session );
            FormUtils.removeFormErrors( session );
            session.removeAttribute( SESSION_VALIDATE_REQUIREMENT );

            //convert the value of the object response to string
            for ( Response response : formSubmit.getListResponse( ) )
            {
                if ( StringUtils.isNotBlank( response.getResponseValue( ) ) || ( response.getFile( ) != null ) )
                {
                    response.setToStringValueResponse( EntryTypeServiceManager
                            .getEntryTypeService( response.getEntry( ) ).getResponseValueForRecap(
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
        model.put( MARK_FORM_SUBMIT, formSubmit );
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

        model.put( MARK_URL_ACTION, strActionUrl );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_RECAP_FORM_SUBMIT, locale, model );

        //See result
        XPage page = new XPage( );
        page.setTitle( I18nService.getLocalizedString( PROPERTY_XPAGE_PAGETITLE, request.getLocale( ) ) );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_XPAGE_PATHLABEL, request.getLocale( ) ) );
        page.setContent( template.getHtml( ) );

        return page;
    }

    /**
     * Return the form requirement
     * @param request The HTTP request
     * @param nMode The current mode.
     * @param plugin The Plugin
     * @return the form recap
     * @throws SiteMessageException SiteMessageException
     */
    private String getRequirement( HttpServletRequest request, int nMode, Plugin plugin ) throws SiteMessageException
    {
        int nIdForm = -1;
        Map<String, Object> model = new HashMap<String, Object>( );
        Locale locale = request.getLocale( );
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( ( strIdForm != null ) && !strIdForm.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdForm = Integer.parseInt( strIdForm );
            }
            catch ( NumberFormatException ne )
            {
                AppLogService.error( ne );
                SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );
            }
        }

        if ( nIdForm == -1 )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );
        }

        Form form = FormHome.findByPrimaryKey( nIdForm, plugin );
        model.put( MARK_REQUIREMENT, form.getRequirement( ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_REQUIREMENT_FORM, locale, model );

        return template.getHtml( );
    }

    /**
     * insert response in the form submit
     * @param request request The HTTP request
     * @param formSubmit Form Submit
     * @param bReturnErrors true if errors must be returned
     * @param plugin the Plugin
     * @return true if there is an error, false otherwise
     */
    public List<GenericAttributeError> doInsertResponseInFormSubmit( HttpServletRequest request, FormSubmit formSubmit,
            boolean bReturnErrors, Plugin plugin )
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

        for ( Entry entry : listEntryFirstLevel )
        {
            listFormErrors.addAll( FormUtils.getResponseEntry( request, entry.getIdEntry( ), plugin, formSubmit, false,
                    bReturnErrors, locale ) );
        }

        return listFormErrors;
    }

    /**
     * perform the form submit in database
     * @param request The HTTP request
     * @param session the http session
     * @param formSubmit Form Submit
     * @param plugin the Plugin
     * @throws SiteMessageException SiteMessageException
     */
    public void doPerformFormSubmit( HttpServletRequest request, HttpSession session, FormSubmit formSubmit,
            Plugin plugin ) throws SiteMessageException
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
            if ( session.getAttribute( PARAMETER_ID_FORM + formSubmit.getForm( ).getIdForm( ) ) != null )
            {
                SiteMessageService.setMessage( request, MESSAGE_ALREADY_SUBMIT_ERROR, SiteMessage.TYPE_STOP );
            }
            else
            {
                session.setAttribute( PARAMETER_ID_FORM + formSubmit.getForm( ).getIdForm( ), PARAMETER_VOTED );
            }
        }

        // Validates the form submit using validators
        ValidatorService.getInstance( ).validateForm( request, formSubmit, plugin );

        formSubmit.setIdFormSubmit( FormSubmitHome.create( formSubmit, plugin ) );

        try
        {
            getResponseService( ).create( formSubmit );
        }
        catch ( Exception ex )
        {
            // something very wrong happened... a database check might be needed
            AppLogService.error( ex.getMessage( ) + " for FormSubmit " + formSubmit.getIdFormSubmit( ), ex );
            // revert
            // we clear the DB form the given formsubmit (FormSubmitHome also removes the reponses)
            FormSubmitHome.remove( formSubmit.getIdFormSubmit( ), plugin );
            // throw a message to the user
            SiteMessageService.setMessage( request, MESSAGE_SUBMIT_SAVE_ERROR, SiteMessage.TYPE_ERROR );
        }

        //Notify new form submit
        FormUtils.sendNotificationMailFormSubmit( formSubmit, locale );

        // We can safely remove session files : they are validated
        FormAsynchronousUploadHandler.getHandler( ).removeSessionFiles( session.getId( ) );

        //Process all outputProcess
        for ( IOutputProcessor outputProcessor : OutputProcessorService.getInstance( ).getProcessorsByIdForm(
                formSubmit.getForm( ).getIdForm( ) ) )
        {
            outputProcessor.process( formSubmit, request, plugin );
        }
    }

    /**
     * check if authentification
     * @param form Form
     * @param request HttpServletRequest
     * @throws UserNotSignedException exception if the form requires an
     *             authentification and the user is not logged
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
                    if ( ( SecurityService.getInstance( ).getRemoteUser( request ) == null )
                            && ( form.isActiveMyLuteceAuthentification( ) ) )
                    {
                        // Authentication is required to access to the portal
                        throw new UserNotSignedException( );
                    }
                }
            }
            else
            {
                //If portal authentication is enabled and user is null and the requested URL
                //is not the login URL, user cannot access to Portal
                if ( ( form.isActiveMyLuteceAuthentification( ) )
                        && ( SecurityService.getInstance( ).getRegisteredUser( request ) == null )
                        && !SecurityService.getInstance( ).isLoginUrl( request ) )
                {
                    // Authentication is required to access to the portal
                    throw new UserNotSignedException( );
                }
            }
        }
    }

    /**
     * Removes the uploaded fileItem
     * @param request the request
     * @return The JSON result
     * @category CALLED_BY_JS (formupload.js)
     */
    public String doRemoveAsynchronousUploadedFile( HttpServletRequest request )
    {
        String strIdEntry = request.getParameter( FormUtils.PARAMETER_ID_ENTRY );
        String strFieldIndex = request.getParameter( PARAMETER_FIELD_INDEX );

        if ( StringUtils.isBlank( strIdEntry ) || StringUtils.isBlank( strFieldIndex ) )
        {
            return JSONUtils.buildJsonErrorRemovingFile( request ).toString( );
        }

        // parse json
        JSON jsonFieldIndexes = JSONSerializer.toJSON( strFieldIndex );

        if ( !jsonFieldIndexes.isArray( ) )
        {
            return JSONUtils.buildJsonErrorRemovingFile( request ).toString( );
        }

        JSONArray jsonArrayFieldIndexers = (JSONArray) jsonFieldIndexes;
        int[] tabFieldIndex = new int[jsonArrayFieldIndexers.size( )];

        for ( int nIndex = 0; nIndex < jsonArrayFieldIndexers.size( ); nIndex++ )
        {
            try
            {
                tabFieldIndex[nIndex] = Integer.parseInt( jsonArrayFieldIndexers.getString( nIndex ) );
            }
            catch ( NumberFormatException nfe )
            {
                return JSONUtils.buildJsonErrorRemovingFile( request ).toString( );
            }
        }

        // inverse order (removing using index - remove greater first to keep order)
        Arrays.sort( tabFieldIndex );
        ArrayUtils.reverse( tabFieldIndex );

        FormAsynchronousUploadHandler handler = FormAsynchronousUploadHandler.getHandler( );

        for ( int nFieldIndex : tabFieldIndex )
        {
            handler.removeFileItem( strIdEntry, request.getSession( ).getId( ), nFieldIndex );
        }

        JSONObject json = new JSONObject( );
        // operation successful
        json.element( JSONUtils.JSON_KEY_SUCCESS, JSONUtils.JSON_KEY_SUCCESS );
        json.accumulateAll( JSONUtils.getUploadedFileJSON( handler.getFileItems( strIdEntry, request.getSession( )
                .getId( ) ) ) );
        json.element( JSONUtils.JSON_KEY_FIELD_NAME,
                FormAsynchronousUploadHandler.getHandler( ).buildFieldName( strIdEntry ) );

        return json.toString( );
    }

    /**
     * Do clean responses of a form
     * @param request The request
     * @return The error code
     */
    public static String doCleanFormAnswers( HttpServletRequest request )
    {
        try
        {
            String strKey = request.getParameter( FormUtils.PARAMETER_KEY );
            String strPrivateKey = AppPropertiesService.getProperty( FormUtils.PROPERTY_CLEAN_FORM_ANSWERS_KEY );

            if ( ( strPrivateKey != null ) && StringUtils.isNotEmpty( strPrivateKey )
                    && !StringUtils.equals( strKey, strPrivateKey ) )
            {
                AppLogService.error( "Illegal attempt to clean form responses : " + SecurityUtil.getRealIp( request ) );

                return AppPropertiesService
                        .getProperty( FormUtils.PROPERTY_CLEAN_FORM_ANSWERS_RETURN_CODE_UNAUTHORIZED );
            }

            String strIdForm = request.getParameter( PARAMETER_ID_FORM );

            if ( ( strIdForm != null ) && StringUtils.isNumeric( strIdForm ) )
            {
                int nIdForm = Integer.parseInt( strIdForm );
                Form form = FormHome.findByPrimaryKey( nIdForm, FormUtils.getPlugin( ) );
                FormService.getInstance( ).cleanFormResponses( form );
            }

            return AppPropertiesService.getProperty( FormUtils.PROPERTY_CLEAN_FORM_ANSWERS_RETURN_CODE_OK );
        }
        catch ( Exception e )
        {
            AppLogService.error( e.getMessage( ), e );

            return AppPropertiesService.getProperty( FormUtils.PROPERTY_CLEAN_FORM_ANSWERS_RETURN_CODE_KO );
        }
    }

    /**
     * Get the response service
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
