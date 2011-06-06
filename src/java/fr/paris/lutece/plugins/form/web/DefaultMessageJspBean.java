/*
 * Copyright (c) 2002-2011, Mairie de Paris
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

import fr.paris.lutece.plugins.form.business.DefaultMessage;
import fr.paris.lutece.plugins.form.business.DefaultMessageHome;
import fr.paris.lutece.plugins.form.service.DefaultMessageResourceIdService;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 *
 *  class DefaultMessageJspBean
 *
 */
public class DefaultMessageJspBean extends PluginAdminPageJspBean
{
    public static final String RIGHT_MANAGE_FORM = "FORM_MANAGEMENT";

    //	templates
    private static final String TEMPLATE_MANAGE_FORM = "admin/plugins/form/manage_default_message.html";

    //	Markers
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_DEFAULT_MESSAGE = "default_message";

    //	parameters form
    private static final String PARAMETER_WELCOME_MESSAGE = "welcome_message";
    private static final String PARAMETER_UNAVAILABILITY_MESSAGE = "unavailability_message";
    private static final String PARAMETER_REQUIREMENT = "requirement";
    private static final String PARAMETER_RECAP_MESSAGE = "recap_message";
    private static final String PARAMETER_LIBELLE_VALIDATE_BUTTON = "libelle_validate_button";
    private static final String PARAMETER_LIBELLE_RESET_BUTTON = "libelle_reset_button";
    private static final String PARAMETER_BACK_URL = "back_url";

    //	 other constants
    private static final String EMPTY_STRING = "";

    //	message
    private static final String MESSAGE_MANDATORY_FIELD = "form.message.mandatory.field";
    private static final String FIELD_WELCOME_MESSAGE = "form.manageDefaultMessage.labelWelcomeMessage";
    private static final String FIELD_UNAVAILABILITY_MESSAGE = "form.manageDefaultMessage.labelUnavailabilityMessage";
    private static final String FIELD_REQUIREMENT_MESSAGE = "form.manageDefaultMessage.labelRequirementMessage";
    private static final String FIELD_RECAP_MESSAGE = "form.manageDefaultMessage.labelRecapMessage";
    private static final String FIELD_LIBELLE_VALIDATE_BUTTON = "form.manageDefaultMessage.labelLibelleValidateButton";
    private static final String FIELD_LIBELLE_RESET_BUTTON = "form.manageDefaultMessage.labelLibelleResetButton";
    private static final String FIELD_BACK_URL = "form.manageDefaultMessage.labelBackUrl";

    //properties
    private static final String PROPERTY_MANAGE_DEFAULT_MESSAGE_TITLE = "form.manageDefaultMessage.title";

    /**
     * gets the manage default message page
     * @param request the Http request
     * @return the manage default message page
     */
    public String getManageDefaultMessage( HttpServletRequest request )
    {
        Locale locale = getLocale(  );
        HashMap model = new HashMap(  );
        DefaultMessage defaultMessage = DefaultMessageHome.find( getPlugin(  ) );

        model.put( MARK_DEFAULT_MESSAGE, defaultMessage );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage(  ) );
        setPageTitleProperty( PROPERTY_MANAGE_DEFAULT_MESSAGE_TITLE );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_FORM, locale, model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * perform the default message modification
     * @param request the Http request
     * @return  The URL to go after performing the action
     */
    public String doModifyDefaultMessage( HttpServletRequest request )
    {
        if ( RBACService.isAuthorized( DefaultMessage.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    DefaultMessageResourceIdService.PERMISSION_MANAGE, getUser(  ) ) )
        {
            DefaultMessage defaultMessage = new DefaultMessage(  );
            String strError = getDefaultMessageData( request, defaultMessage );

            if ( strError != null )
            {
                return strError;
            }

            DefaultMessageHome.update( defaultMessage, getPlugin(  ) );
        }

        return getHomeUrl( request );
    }

    /**
     * Get the request data and if there is no error insert the data in the default Message object specified in parameter.
     * return null if there is no error or else return the error page url
     * @param request the request
     * @param defaultMessage the default message
     * @return null if there is no error or else return the error page url
     */
    private String getDefaultMessageData( HttpServletRequest request, DefaultMessage defaultMessage )
    {
        String strWelcomeMessage = request.getParameter( PARAMETER_WELCOME_MESSAGE );
        String strUnavailabilityMessage = request.getParameter( PARAMETER_UNAVAILABILITY_MESSAGE );
        String strRequirementMessage = request.getParameter( PARAMETER_REQUIREMENT );
        String strRecapMessage = request.getParameter( PARAMETER_RECAP_MESSAGE );
        String strLibelleValidateButton = request.getParameter( PARAMETER_LIBELLE_VALIDATE_BUTTON );
        String strLibelleResetButton = request.getParameter( PARAMETER_LIBELLE_RESET_BUTTON );
        String strBackUrl = request.getParameter( PARAMETER_BACK_URL );
        String strFieldError = EMPTY_STRING;

        if ( ( strWelcomeMessage == null ) || strWelcomeMessage.trim(  ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_WELCOME_MESSAGE;
        }
        else if ( ( strUnavailabilityMessage == null ) || strUnavailabilityMessage.trim(  ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_UNAVAILABILITY_MESSAGE;
        }
        else if ( ( strRequirementMessage == null ) || strRequirementMessage.trim(  ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_REQUIREMENT_MESSAGE;
        }
        else if ( ( strRecapMessage == null ) || strRecapMessage.trim(  ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_RECAP_MESSAGE;
        }
        else if ( ( strLibelleValidateButton == null ) || strLibelleValidateButton.trim(  ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_LIBELLE_VALIDATE_BUTTON;
        }
        else if ( ( strBackUrl == null ) || strBackUrl.trim(  ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_BACK_URL;
        }

        if ( !strFieldError.equals( EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, getLocale(  ) ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        defaultMessage.setWelcomeMessage( strWelcomeMessage );
        defaultMessage.setUnavailabilityMessage( strUnavailabilityMessage );
        defaultMessage.setRequirement( strRequirementMessage );
        defaultMessage.setRecapMessage( strRecapMessage );
        defaultMessage.setLibelleValidateButton( strLibelleValidateButton );
        defaultMessage.setLibelleResetButton( strLibelleResetButton );
        defaultMessage.setBackUrl( strBackUrl );

        return null;
    }
}
