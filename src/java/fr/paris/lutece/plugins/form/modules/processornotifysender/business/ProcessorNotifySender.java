/*
 * Copyright (c) 2002-2009, Mairie de Paris
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
import fr.paris.lutece.plugins.form.business.Response;
import fr.paris.lutece.plugins.form.business.outputprocessor.OutputProcessor;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;

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
    private static final String PARAMETER_SEND_RECAP = "send_recap";
    private static final String MARK_FORM = "form";
    private static final String MARK_REF_LIST_ENTRY = "entry_list";
    private static final String MARK_CONFIGURATION = "configuration";
    private static final String MARK_RECAP_HTML = "recap";
    private static final String MARK_MESSAGE_RECAP = "messageRecap";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_FORM_SUBMIT = "formSubmit";
    private static final String MARK_MESSAGE = "mail_message";
    private static final String MARK_TITLE = "mail_title";
    private static final String MESSAGE_CONFIGURATION_ERROR_ENTRY_NOT_SELECTED = "module.form.processornotifysender.message.error.configuration.entry_not_selected";
    private static final String MESSAGE_ERROR_NO_CONFIGURATION_ASSOCIATED = "module.form.processornotifysender.message.error.no_configuration_associated";
    private static final String MESSAGE_RECAP_INFORMATION = "module.form.processornotifysender.configuration_notify_sender.send_recap";
    private static final String PROPERTY_TAG_RECAP = "processornotifysender.recap_tag";

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.form.business.outputprocessor.IOutputProcessor#getOutputConfigForm(fr.paris.lutece.plugins.form.business.Form, java.util.Locale, fr.paris.lutece.portal.service.plugin.Plugin)
     */
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

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CONFIGURATION_NOTIFY_SENDER, locale, model );

        return template.getHtml(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.form.business.outputprocessor.IOutputProcessor#doOutputConfigForm(javax.servlet.http.HttpServletRequest, java.util.Locale, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public String doOutputConfigForm( HttpServletRequest request, Locale locale, Plugin plugin )
    {
        // TODO Auto-generated method stub
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

        NotifySenderConfiguration configuration = new NotifySenderConfiguration(  );
        configuration.setIdForm( nIdForm );

        String strError = getConfigurationData( request, configuration, locale );

        if ( strError != null )
        {
            return strError;
        }

        if ( NotifySenderConfigurationHome.findByPrimaryKey( nIdForm, plugin ) != null )
        {
            NotifySenderConfigurationHome.update( configuration, plugin );
        }
        else
        {
            NotifySenderConfigurationHome.create( configuration, plugin );
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.form.business.outputprocessor.IOutputProcessor#process(fr.paris.lutece.plugins.form.business.FormSubmit, javax.servlet.http.HttpServletRequest, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public String process( FormSubmit formSubmit, HttpServletRequest request, Plugin plugin )
    {
        NotifySenderConfiguration configuration = NotifySenderConfigurationHome.findByPrimaryKey( formSubmit.getForm(  )
                                                                                                            .getIdForm(  ),
                plugin );

        if ( configuration == null )
        {
            return MESSAGE_ERROR_NO_CONFIGURATION_ASSOCIATED;
        }

        String strSubject = I18nService.getLocalizedString( PROPERTY_NOTIFICATION_NOTIFY_SENDER_SUBJECT,
                request.getLocale(  ) );
        String strSenderName = I18nService.getLocalizedString( PROPERTY_NOTIFICATION_NOTIFY_SENDER_SENDER_NAME,
                request.getLocale(  ) );
        String strSenderEmail = MailService.getNoReplyEmail(  );

        String strEmailSender = FormUtils.EMPTY_STRING;

        //----------------------------------
        for ( Response response : formSubmit.getListResponse(  ) )
        {
            if ( response.getEntry(  ).getIdEntry(  ) == configuration.getIdEntryEmailSender(  ) )
            {
                strEmailSender = response.getEntry(  )
                                         .getResponseValueForExport( request, response, request.getLocale(  ) );
            }
        }

        HashMap<String, Object> model = new HashMap<String, Object>(  );
        Recap recap = RecapHome.findByPrimaryKey( formSubmit.getForm(  ).getRecap(  ).getIdRecap(  ), plugin );

        if ( ( recap != null ) && recap.isRecapData(  ) )
        {
            //convert the value of the object response to string 
            for ( Response response : formSubmit.getListResponse(  ) )
            {
                byte[] byResponseValue = response.getValueResponse(  );

                if ( byResponseValue != null )
                {
                    response.setToStringValueResponse( response.getEntry(  )
                                                               .getResponseValueForRecap( request, response,
                            request.getLocale(  ) ) );
                }
                else
                {
                    response.setToStringValueResponse( FormUtils.EMPTY_STRING );
                }
            }
        }

        model.put( MARK_RECAP_HTML, recap );
        model.put( MARK_FORM_SUBMIT, formSubmit );

        HtmlTemplate templateRecap = AppTemplateService.getTemplate( TEMPLATE_NOTIFICATION_NOTIFY_SENDER_RECAP,
                request.getLocale(  ), model );

        //-------------------------------------------------------------
        String strTagRecap = AppPropertiesService.getProperty( PROPERTY_TAG_RECAP );
        String strMessage = configuration.getMessage(  ).replace( strTagRecap, templateRecap.getHtml(  ) );

        model.put( MARK_MESSAGE, strMessage );
        model.put( MARK_TITLE, strSubject );

        HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_NOTIFICATION_NOTIFY_SENDER, request.getLocale(  ),
                model );

        try
        {
            // Send Mail
            MailService.sendMailHtml( strEmailSender, strSenderName, strSenderEmail, strSubject, t.getHtml(  ) );
        }
        catch ( Exception e )
        {
            AppLogService.error( " Error during Process > Notify sender : " + e.getMessage(  ) );
        }

        return null;
    }

    /**
     * Get the configuration data
     * @param request the request
     * @param configuration the configuration object
     * @param locale the locale
     * @return Message error if error appear else null
     */
    private String getConfigurationData( HttpServletRequest request, NotifySenderConfiguration configuration,
        Locale locale )
    {
        String strIdEntryEmailSender = request.getParameter( PARAMETER_ID_ENTRY_EMAIL_SENDER );

        int nIdEntryEmailSender = -1;
        String strMailMessage = request.getParameter( PARAMETER_MAIL_MESSAGE );
        String strSendRecap = request.getParameter( PARAMETER_SEND_RECAP );

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

        configuration.setIdEntryEmailSender( nIdEntryEmailSender );

        configuration.setMessage( strMailMessage );

        return null; // No error
    }
}
