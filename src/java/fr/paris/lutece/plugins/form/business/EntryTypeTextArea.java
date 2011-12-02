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
import fr.paris.lutece.util.string.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;


/**
 *
 * class EntryTypeTextArea
 *
 */
public class EntryTypeTextArea extends Entry
{
    private final String _template_create = "admin/plugins/form/create_entry_type_text_area.html";
    private final String _template_modify = "admin/plugins/form/modify_entry_type_text_area.html";
    private final String _template_html_code = "admin/plugins/form/html_code_entry_type_text_area.html";

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
        String strWidth = request.getParameter( PARAMETER_WIDTH );
        String strHeight = request.getParameter( PARAMETER_HEIGHT );
        String strMaxSizeEnter = request.getParameter( PARAMETER_MAX_SIZE_ENTER );

        int nWidth = -1;
        int nHeight = -1;
        int nMaxSizeEnter = -1;

        String strFieldError = EMPTY_STRING;

        if ( ( strTitle == null ) || strTitle.trim(  ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_TITLE;
        }

        else if ( ( strWidth == null ) || strWidth.trim(  ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_WIDTH;
        }
        else if ( ( strHeight == null ) || strHeight.trim(  ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_HEIGHT;
        }

        if ( !strFieldError.equals( EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        try
        {
            nHeight = Integer.parseInt( strHeight );
        }
        catch ( NumberFormatException ne )
        {
            strFieldError = FIELD_HEIGHT;
        }

        try
        {
            nWidth = Integer.parseInt( strWidth );
        }
        catch ( NumberFormatException ne )
        {
            strFieldError = FIELD_WIDTH;
        }
        
        try
        {
            if ( ( strMaxSizeEnter != null ) && !strMaxSizeEnter.trim(  ).equals( EMPTY_STRING ) )
            {
                nMaxSizeEnter = Integer.parseInt( strMaxSizeEnter );
            }
        }
        catch ( NumberFormatException ne )
        {
            strFieldError = FIELD_MAX_SIZE_ENTER;
        }

        if ( !strFieldError.equals( EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_NUMERIC_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
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

        this.getFields(  ).get( 0 ).setValue( strValue );
        this.getFields(  ).get( 0 ).setWidth( nWidth );
        this.getFields(  ).get( 0 ).setHeight( nHeight );
        this.getFields(  ).get( 0 ).setMaxSizeEnter( nMaxSizeEnter );

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
        String strValueEntry = request.getParameter( FormUtils.EMPTY_STRING + this.getIdEntry(  ) );
        Response response = new Response(  );
        response.setEntry( this );

        if ( strValueEntry != null )
        {
        	int nMaxSize = getFields(  ).get( 0 ).getMaxSizeEnter(  );
        	response.setResponseValue( strValueEntry );

            if ( StringUtils.isNotBlank( response.getResponseValue(  ) ) )
            {
                response.setToStringValueResponse( getResponseValueForRecap( request, response, locale ) );
            }
            else
            {
                response.setToStringValueResponse( StringUtils.EMPTY );
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
            
            // check max size for the field. 0 means no limit
            if ( nMaxSize != - 1 && strValueEntry.length(  ) > nMaxSize )
            {
            	FormError formError = new FormError(  );
                formError.setMandatoryError( false );
                formError.setTitleQuestion( this.getTitle(  ) );
                Object[] messageArgs = new Object[] {
                		nMaxSize,
                };
                formError.setErrorMessage( I18nService.getLocalizedString( MESSAGE_MAXLENGTH, messageArgs, request.getLocale(  ) ) );

                return formError;
            }
            if ( this.isMandatory(  ) )
            {
                if ( strValueEntry.equals( FormUtils.EMPTY_STRING ) )
                {
                    FormError formError = new FormError(  );
                    formError.setMandatoryError( true );
                    formError.setTitleQuestion( this.getTitle(  ) );

                    return formError;
                }
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
    	return response.getResponseValue(  );
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
    	return response.getResponseValue(  );
    }
}
