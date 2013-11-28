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

import fr.paris.lutece.plugins.form.service.IResponseService;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;


/**
 * 
 * class EntryTypeText
 * 
 */
public class EntryTypeText extends AbstractEntryTypeText
{
    private final String _template_create = "admin/plugins/form/entries/create_entry_type_text.html";
    private final String _template_modify = "admin/plugins/form/entries/modify_entry_type_text.html";
    private final String _template_html_code = "admin/plugins/form/entries/html_code_entry_type_text.html";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHtmlCode( )
    {
        return _template_html_code;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateCreate( )
    {
        return _template_create;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateModify( )
    {
        return _template_modify;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FormError getResponseData( HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        FormError formError = super.getResponseData( request, listResponse, locale );

        if ( formError != null )
        {
            return formError;
        }

        String strValueEntry = request.getParameter( PREFIX_ATTRIBUTE + this.getIdEntry( ) ).trim( );
        boolean bUnique = this.isUnique( );

        if ( bUnique )
        {
            ResponseFilter filter = new ResponseFilter( );
            filter.setIdEntry( this.getIdEntry( ) );

            IResponseService responseService = SpringContextService.getBean( FormUtils.BEAN_FORM_RESPONSE_SERVICE );
            Collection<Response> listSubmittedResponses = responseService.getResponseList( filter, false );

            for ( Response submittedResponse : listSubmittedResponses )
            {
                String strSubmittedResponse = submittedResponse.getEntry( ).getResponseValueForRecap( request,
                        submittedResponse, locale );

                if ( StringUtils.isNotBlank( strValueEntry ) && StringUtils.isNotBlank( strSubmittedResponse )
                        && strValueEntry.equalsIgnoreCase( strSubmittedResponse ) )
                {
                    formError = new FormError( );
                    formError.setMandatoryError( false );
                    formError.setTitleQuestion( this.getTitle( ) );
                    formError.setErrorMessage( I18nService.getLocalizedString( MESSAGE_UNIQUE_FIELD,
                            request.getLocale( ) ) );

                    return formError;
                }
            }
        }

        return null;
    }
}
