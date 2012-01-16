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
package fr.paris.lutece.plugins.form.business;

import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.string.StringUtil;

import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * class EntryTypeDate
 *
 */
public class EntryTypeDate extends Entry
{
    private static final String _message_illogical_date = "form.message.illogicalDate";
    private final String _template_create = "admin/plugins/form/create_entry_type_date.html";
    private final String _template_modify = "admin/plugins/form/modify_entry_type_date.html";
    private final String _template_html_code = "admin/plugins/form/html_code_entry_type_date.html";

    /**
     * Get the HtmlCode  of   the entry
     * @return the HtmlCode  of   the entry
     *
     * */
    public String getHtmlCode(  )
    {
        return _template_html_code;
    }

    /**
     * Get the request data
     * @param request HttpRequest
     * @param locale the locale
     * @return null if all data requiered are in the request else the url of jsp error
     */
    public String getRequestData( HttpServletRequest request, Locale locale )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strHelpMessage = ( request.getParameter( PARAMETER_HELP_MESSAGE ) != null )
            ? request.getParameter( PARAMETER_HELP_MESSAGE ).trim(  ) : null;
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strValue = request.getParameter( PARAMETER_VALUE );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );

        String strFieldError = EMPTY_STRING;

        if ( ( strTitle == null ) || strTitle.trim(  ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_TITLE;
        }

        if ( !strFieldError.equals( EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        Date dDateValue = null;

        if ( ( strValue != null ) && !strValue.equals( EMPTY_STRING ) )
        {
            dDateValue = DateUtil.formatDate( strValue, locale );

            if ( dDateValue == null )
            {
                return AdminMessageService.getMessageUrl( request, _message_illogical_date, AdminMessage.TYPE_STOP );
            }
        }

        this.setTitle( strTitle );
        this.setHelpMessage( strHelpMessage );
        this.setComment( strComment );

        if ( this.getFields(  ) == null )
        {
            ArrayList<Field> listFields = new ArrayList<Field>(  );
            Field field = new Field(  );
            listFields.add( field );
            this.setFields( listFields );
        }

        this.getFields(  ).get( 0 ).setValueTypeDate( dDateValue );

        if ( strMandatory != null )
        {
            this.setMandatory( true );
        }
        else
        {
            this.setMandatory( false );
        }

        return null;
    }

    /**
     * Get template create url of the entry
     * @return template create url of the entry
     */
    public String getTemplateCreate(  )
    {
        return _template_create;
    }

    /**
     * Get the template modify url  of the entry
     * @return template modify url  of the entry
     */
    public String getTemplateModify(  )
    {
        return _template_modify;
    }

    /**
     * save in the list of response the response associate to the entry in the form submit
     * @param request HttpRequest
     * @param listResponse the list of response associate to the entry in the form submit
     * @param locale the locale
     * @return a Form error object if there is an error in the response
     */
    public FormError getResponseData( HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        String strValueEntry = request.getParameter( FormUtils.EMPTY_STRING + this.getIdEntry(  ) ).trim(  );
        Response response = new Response(  );
        response.setEntry( this );

        if ( strValueEntry != null )
        {
            Date tDateValue = DateUtil.formatDate( strValueEntry, locale );

            if ( tDateValue != null )
            {
                response.setResponseValue( Long.toString( tDateValue.getTime(  ) ) );
            }
            else
            {
                response.setResponseValue( strValueEntry );
            }

            if ( StringUtils.isNotBlank( response.getResponseValue(  ) ) )
            {
                response.setToStringValueResponse( getResponseValueForRecap( request, response, locale ) );
            }
            else
            {
                response.setToStringValueResponse( EMPTY_STRING );
            }

            listResponse.add( response );

            // Checks if the entry value contains XSS characters
            if ( StringUtil.containsXssCharacters( strValueEntry ) )
            {
                FormError formError = new FormError(  );
                formError.setMandatoryError( false );
                formError.setTitleQuestion( this.getTitle(  ) );
                formError.setErrorMessage( I18nService.getLocalizedString( MESSAGE_XSS_FIELD, request.getLocale(  ) ) );

                return formError;
            }

            if ( this.isMandatory(  ) )
            {
                if ( FormUtils.EMPTY_STRING.equals( strValueEntry ) )
                {
                    FormError formError = new FormError(  );
                    formError.setMandatoryError( true );
                    formError.setTitleQuestion( this.getTitle(  ) );

                    return formError;
                }
            }

            if ( tDateValue == null )
            {
                String strError = I18nService.getLocalizedString( _message_illogical_date, locale );
                FormError formError = new FormError(  );
                formError.setTitleQuestion( this.getTitle(  ) );
                formError.setMandatoryError( false );
                formError.setErrorMessage( strError );

                return formError;
            }
        }

        return null;
    }

    /**
         * Get the response value  associate to the entry  to export in the file export
         * @param response the response associate to the entry
         * @param locale the locale
         * @param request the request
         * @return  the response value  associate to the entry  to export in the file export
         */
    public String getResponseValueForExport( HttpServletRequest request, Response response, Locale locale )
    {
        Long newLong = Long.parseLong( response.getResponseValue(  ) );
        Timestamp date = new Timestamp( newLong );

        return DateUtil.getDateString( date, locale );
    }

    /**
     * Get the response value  associate to the entry  to write in the recap
     * @param response the response associate to the entry
     * @param locale the locale
     * @param request the request
     * @return the response value  associate to the entry  to write in the recap
     */
    public String getResponseValueForRecap( HttpServletRequest request, Response response, Locale locale )
    {
        Long newLong = Long.parseLong( response.getResponseValue(  ) );
        Timestamp date = new Timestamp( newLong );

        return DateUtil.getDateString( date, locale );
    }

    /**
     * Sets the date.
     */
    @Override
    public void setResponseToStringValue( Response response, Locale locale )
    {
        if ( StringUtils.isNotBlank( response.getResponseValue(  ) ) )
        {
            Long newLong = Long.parseLong( response.getResponseValue(  ) );
            Timestamp date = new Timestamp( newLong );

            response.setToStringValueResponse( DateUtil.getDateString( date, locale ) );
        }
    }
}
