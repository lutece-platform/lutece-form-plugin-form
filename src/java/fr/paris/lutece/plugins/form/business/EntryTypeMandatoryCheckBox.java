/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
import fr.paris.lutece.portal.service.util.AppLogService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;


/**
 * 
 * class EntryTypeCheckBox
 * 
 */
public class EntryTypeMandatoryCheckBox extends EntryTypeCheckBox
{

    private final String _template_create = "admin/plugins/form/create_entry_type_mandatory_check_box.html";
    private final String _template_modify = "admin/plugins/form/modify_entry_type_mandatory_check_box.html";

    /**
     * Get template create url of the entry
     * @return template create url of the entry
     */
    public String getTemplateCreate( )
    {
        return _template_create;
    }

    /**
     * Get the template modify url of the entry
     * @return template modify url of the entry
     */
    public String getTemplateModify( )
    {
        return _template_modify;
    }

    /**
     * save in the list of response the response associate to the entry in the
     * form submit
     * @param request HttpRequest
     * @param listResponse the list of response associate to the entry in the
     *            form submit
     * @param locale the locale
     * @return a Form error object if there is an error in the response
     */
    public FormError getResponseData( HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        String[] strTabIdField = request.getParameterValues( PREFIX_FORM + this.getIdEntry( ) );
        List<Field> listFieldInResponse = new ArrayList<Field>( );
        int nIdField = -1;
        Field field = null;
        Response response;

        if ( strTabIdField != null )
        {
            for ( int cpt = 0; cpt < strTabIdField.length; cpt++ )
            {
                try
                {
                    nIdField = Integer.parseInt( strTabIdField[cpt] );
                }
                catch ( NumberFormatException ne )
                {
                    AppLogService.error( ne );
                }

                field = FormUtils.findFieldByIdInTheList( nIdField, this.getFields( ) );

                if ( field != null )
                {
                    listFieldInResponse.add( field );
                }
            }
        }

        if ( listFieldInResponse.size( ) == 0 )
        {
            response = new Response( );
            response.setEntry( this );
            listResponse.add( response );
        }
        else
        {
            for ( Field fieldInResponse : listFieldInResponse )
            {
                response = new Response( );
                response.setEntry( this );
                response.setResponseValue( fieldInResponse.getValue( ) );
                response.setField( fieldInResponse );
                listResponse.add( response );
            }
        }

        int nSubmitedFields = 0;

        for ( Field fieldInResponse : listFieldInResponse )
        {
            if ( StringUtils.isNotEmpty( fieldInResponse.getValue( ) ) )
            {
                nSubmitedFields++;
            }
        }

        if ( nSubmitedFields < getFields( ).size( ) )
        {
            if ( StringUtils.isNotBlank( getErrorMessage( ) ) )
            {
                FormError formError = new FormError( );
                formError.setMandatoryError( true );
                formError.setErrorMessage( getErrorMessage( ) );
                return formError;
            }
            return new MandatoryFormError( this, locale );
        }

        return null;
    }
}
