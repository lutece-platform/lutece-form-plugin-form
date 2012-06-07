/*
 * Copyright (c) 2002-2012, Mairie de Paris
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

import fr.paris.lutece.plugins.form.service.FormService;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * EntryTypeNumerotation
 *
 */
public class EntryTypeNumbering extends Entry
{
    // PARAMETERS
    private static final String PARAMETER_PREFIX = "prefix";

    // TEMPLATES
    private static final String TEMPLATE_CREATE = "admin/plugins/form/create_entry_type_numbering.html";
    private static final String TEMPLATE_MODIFY = "admin/plugins/form/modify_entry_type_numbering.html";
    private static final String TEMPLATE_HTML_CODE = "admin/plugins/form/html_code_entry_type_numbering.html";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHtmlCode(  )
    {
        return TEMPLATE_HTML_CODE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateCreate(  )
    {
        return TEMPLATE_CREATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateModify(  )
    {
        return TEMPLATE_MODIFY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( HttpServletRequest request, Locale locale )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strPrefix = request.getParameter( PARAMETER_PREFIX );

        String strFieldError = StringUtils.EMPTY;

        if ( StringUtils.isBlank( strTitle ) )
        {
            strFieldError = FIELD_TITLE;
        }

        if ( StringUtils.isNotBlank( strFieldError ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        this.setTitle( strTitle );

        if ( this.getFields(  ) == null )
        {
            List<Field> listFields = new ArrayList<Field>(  );
            Field field = new Field(  );
            listFields.add( field );
            this.setFields( listFields );
        }

        this.getFields(  ).get( 0 ).setTitle( StringUtils.isNotEmpty( strPrefix ) ? strPrefix : StringUtils.EMPTY );

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FormError getResponseData( HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        int numbering = FormService.getInstance(  ).getMaxNumber( this );
        Response response = new Response(  );
        response.setEntry( this );
        response.setResponseValue( String.valueOf( numbering ) );
        listResponse.add( response );

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForExport( HttpServletRequest request, Response response, Locale locale )
    {
        return this.getResponseValue( response );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForRecap( HttpServletRequest request, Response response, Locale locale )
    {
        return this.getResponseValue( response );
    }

    /**
     * @return the response value of the response for this entry
     */
    public String getResponseValue( Response response )
    {
        Field field = null;

        if ( getFields(  ) == null )
        {
            setFields( FieldHome.getFieldListByIdEntry( getIdEntry(  ), FormUtils.getPlugin(  ) ) );
        }

        if ( ( getFields(  ) != null ) && !getFields(  ).isEmpty(  ) )
        {
            field = getFields(  ).get( 0 );
        }

        if ( ( field != null ) && StringUtils.isNotBlank( field.getTitle(  ) ) )
        {
            return field.getTitle(  ) + response.getResponseValue(  );
        }

        return response.getResponseValue(  );
    }
}
