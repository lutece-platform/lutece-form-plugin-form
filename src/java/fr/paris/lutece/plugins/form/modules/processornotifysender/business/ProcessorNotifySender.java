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
package fr.paris.lutece.plugins.form.modules.processornotifysender.business;

import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormSubmit;
import fr.paris.lutece.plugins.form.business.Recap;
import fr.paris.lutece.plugins.form.business.RecapHome;
import fr.paris.lutece.plugins.form.business.outputprocessor.OutputProcessor;
import fr.paris.lutece.plugins.form.modules.processornotifysender.service.NotifySenderResourceIdService;
import fr.paris.lutece.plugins.form.modules.processornotifysender.service.NotifySenderService;
import fr.paris.lutece.plugins.form.service.EntryTypeService;
import fr.paris.lutece.plugins.form.service.entrytype.EntryTypeSession;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * ProcessorNotifySender s
 *
 */
public class ProcessorNotifySender extends OutputProcessor
{
    //templates
    private static final String TEMPLATE_CONFIGURATION_NOTIFY_SENDER = "admin/plugins/form/modules/processornotifysender/configuration_notify_sender.html";
    private static final String TEMPLATE_NOTIFICATION_NOTIFY_SENDER = "admin/plugins/form/modules/processornotifysender/notification_notify_sender.html";
    private static final String TEMPLATE_NOTIFICATION_NOTIFY_SENDER_RECAP = "admin/plugins/form/modules/processornotifysender/notification_notify_sender_recap.html";
    private static final String PROPERTY_NOTIFICATION_NOTIFY_SENDER_SUBJECT = "module.form.processornotifysender.notification_notify_sender.sender_subject";
    private static final String PROPERTY_NOTIFICATION_NOTIFY_SENDER_SENDER_NAME = "module.form.processornotifysender.notification_notify_sender.sender_name";
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_ID_ENTRY_EMAIL_SENDER = "id_entry_email_sender";
    private static final String PARAMETER_MAIL_MESSAGE = "mail_message";
    private static final String PARAMETER_SEND_ATTACHMENTS = "send_attachments";
    private static final String MARK_FORM = "form";
    private static final String MARK_REF_LIST_ENTRY = "entry_list";
    private static final String MARK_CONFIGURATION = "configuration";
    private static final String MARK_RECAP_HTML = "recap";
    private static final String MARK_MESSAGE_RECAP = "messageRecap";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_FORM_SUBMIT = "formSubmit";
    private static final String MARK_MESSAGE = "mail_message";
    private static final String MARK_TITLE = "mail_title";
    private static final String MARK_PERMISSION_SEND_ATTACHMENTS = "permission_send_attachments";
    private static final String MARK_ENTRY_TYPE_SESSION = "entry_type_session";
    private static final String MESSAGE_CONFIGURATION_ERROR_ENTRY_NOT_SELECTED = "module.form.processornotifysender.message.error.configuration.entry_not_selected";
    private static final String MESSAGE_ERROR_NO_CONFIGURATION_ASSOCIATED = "module.form.processornotifysender.message.error.no_configuration_associated";
    private static final String MESSAGE_RECAP_INFORMATION = "module.form.processornotifysender.configuration_notify_sender.send_recap";
    private static final String PROPERTY_TAG_RECAP = "processornotifysender.recap_tag";
    private NotifySenderService _notifySenderService;
    private EntryTypeService _entryTypeService;

    /**
     * Set the notify sender service
     * @param notifySenderService the notify sender service
     */
    public void setNotifySenderService( NotifySenderService notifySenderService )
    {
        _notifySenderService = notifySenderService;
    }

    /**
     * Set the entry type service
     * @param entryTypeService the entry type service
     */
    public void setEntryTypeService( EntryTypeService entryTypeService )
    {
        _entryTypeService = entryTypeService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOutputConfigForm( HttpServletRequest request, Form form, Locale locale, Plugin plugin )
    {
        NotifySenderConfiguration configuration = NotifySenderConfigurationHome.findByPrimaryKey( form.getIdForm(  ),
                plugin );

        String strMessageRecap = I18nService.getLocalizedString( MESSAGE_RECAP_INFORMATION,
                new String[] { AppPropertiesService.getProperty( PROPERTY_TAG_RECAP ) }, locale );
        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_FORM, form );
        model.put( MARK_CONFIGURATION, configuration );
        model.put( MARK_CONFIGURATION, configuration );
        model.put( MARK_LOCALE, locale );
        model.put( MARK_REF_LIST_ENTRY, FormUtils.getRefListAllQuestions( form.getIdForm(  ), plugin ) );
        model.put( MARK_MESSAGE_RECAP, strMessageRecap );
        model.put( MARK_PERMISSION_SEND_ATTACHMENTS,
            RBACService.isAuthorized( NotifySenderResourceIdService.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                NotifySenderResourceIdService.PERMISSION_SEND_ATTACHMENTS, AdminUserService.getAdminUser( request ) ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CONFIGURATION_NOTIFY_SENDER, locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doOutputConfigForm( HttpServletRequest request, Locale locale, Plugin plugin )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = -1;

        try
        {
            nIdForm = Integer.parseInt( strIdForm );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        NotifySenderConfiguration config = new NotifySenderConfiguration(  );
        config.setIdForm( nIdForm );

        String strError = getConfigurationData( request, config );

        if ( strError != null )
        {
            return strError;
        }

        if ( NotifySenderConfigurationHome.findByPrimaryKey( nIdForm, plugin ) != null )
        {
            NotifySenderConfigurationHome.update( config, plugin );
        }
        else
        {
            NotifySenderConfigurationHome.create( config, plugin );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String process( FormSubmit formSubmit, HttpServletRequest request, Plugin plugin )
    {
        NotifySenderConfiguration config = NotifySenderConfigurationHome.findByPrimaryKey( formSubmit.getForm(  )
                                                                                                     .getIdForm(  ),
                plugin );

        if ( config == null )
        {
            return MESSAGE_ERROR_NO_CONFIGURATION_ASSOCIATED;
        }

        String strSubject = I18nService.getLocalizedString( PROPERTY_NOTIFICATION_NOTIFY_SENDER_SUBJECT,
                request.getLocale(  ) );
        String strSenderName = I18nService.getLocalizedString( PROPERTY_NOTIFICATION_NOTIFY_SENDER_SENDER_NAME,
                request.getLocale(  ) );
        String strSenderEmail = MailService.getNoReplyEmail(  );

        String strEmailSender = StringUtils.EMPTY;

        //----------------------------------
        for ( Response response : formSubmit.getListResponse(  ) )
        {
            if ( response.getEntry(  ).getIdEntry(  ) == config.getIdEntryEmailSender(  ) )
            {
                strEmailSender = EntryTypeServiceManager.getEntryTypeService( response.getEntry(  ) )
                                                        .getResponseValueForExport( response.getEntry(  ), request,
                        response, request.getLocale(  ) );
            }
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        Recap recap = RecapHome.findByPrimaryKey( ( formSubmit.getForm(  ) ).getRecap(  ).getIdRecap(  ), plugin );

        if ( ( recap != null ) && recap.isRecapData(  ) )
        {
            //convert the value of the object response to string 
            for ( Response response : formSubmit.getListResponse(  ) )
            {
                if ( StringUtils.isNotBlank( response.getResponseValue(  ) ) || ( response.getFile(  ) != null ) )
                {
                    response.setToStringValueResponse( EntryTypeServiceManager.getEntryTypeService( 
                            response.getEntry(  ) )
                                                                              .getResponseValueForRecap( response.getEntry(  ),
                            request, response, request.getLocale(  ) ) );
                }
                else
                {
                    response.setToStringValueResponse( StringUtils.EMPTY );
                }
            }
        }

        model.put( MARK_RECAP_HTML, recap );
        model.put( MARK_FORM_SUBMIT, formSubmit );
        model.put( MARK_ENTRY_TYPE_SESSION, _entryTypeService.getEntryType( EntryTypeSession.BEAN_NAME ) );

        HtmlTemplate templateRecap = AppTemplateService.getTemplate( TEMPLATE_NOTIFICATION_NOTIFY_SENDER_RECAP,
                request.getLocale(  ), model );

        //-------------------------------------------------------------
        String strTagRecap = AppPropertiesService.getProperty( PROPERTY_TAG_RECAP );
        String strMessage = config.getMessage(  ).replace( strTagRecap, templateRecap.getHtml(  ) );

        model.put( MARK_MESSAGE, strMessage );
        model.put( MARK_TITLE, strSubject );

        HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_NOTIFICATION_NOTIFY_SENDER, request.getLocale(  ),
                model );

        _notifySenderService.sendNotification( formSubmit, strEmailSender, strSenderName, strSenderEmail, strSubject,
            t.getHtml(  ), config.isSendAttachments(  ) );

        return null;
    }

    /**
     * Get the configuration data
     * @param request the request
     * @param config the configuration object
     * @return Message error if error appear else null
     */
    private String getConfigurationData( HttpServletRequest request, NotifySenderConfiguration config )
    {
        String strIdEntryEmailSender = request.getParameter( PARAMETER_ID_ENTRY_EMAIL_SENDER );

        int nIdEntryEmailSender = -1;
        String strMailMessage = request.getParameter( PARAMETER_MAIL_MESSAGE );
        String strSendAttachments = request.getParameter( PARAMETER_SEND_ATTACHMENTS );
        boolean bSendAttachments = false;

        // Check if it must send the attachments
        if ( RBACService.isAuthorized( NotifySenderResourceIdService.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    NotifySenderResourceIdService.PERMISSION_SEND_ATTACHMENTS, AdminUserService.getAdminUser( request ) ) &&
                ( strSendAttachments != null ) )
        {
            bSendAttachments = true;
        }

        try
        {
            nIdEntryEmailSender = Integer.parseInt( strIdEntryEmailSender );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        if ( nIdEntryEmailSender == -1 )
        {
            return MESSAGE_CONFIGURATION_ERROR_ENTRY_NOT_SELECTED;
        }

        config.setIdEntryEmailSender( nIdEntryEmailSender );
        config.setSendAttachments( bSendAttachments );

        config.setMessage( strMailMessage );

        return null; // No error
    }
}
