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
package fr.paris.lutece.plugins.form.service.entrytype;

import fr.paris.lutece.plugins.form.service.IResponseService;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseFilter;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeText;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * class EntryTypeText
 *
 */
public class EntryTypeText extends AbstractEntryTypeText
{
    private static final String TEMPLATE_CREATE = "admin/plugins/form/entries/create_entry_type_text.html";
    private static final String TEMPLATE_MODIFY = "admin/plugins/form/entries/modify_entry_type_text.html";
    private static final String TEMPLATE_HTML_CODE = "skin/plugins/form/entries/html_code_entry_type_text.html";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateHtmlForm( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_HTML_CODE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateCreate( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_CREATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateModify( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_MODIFY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericAttributeError getResponseData( Entry entry, HttpServletRequest request, List<Response> listResponse,
        Locale locale )
    {
        GenericAttributeError formError = super.getResponseData( entry, request, listResponse, locale );

        if ( formError != null )
        {
            return formError;
        }

        String strValueEntry = request.getParameter( PREFIX_ATTRIBUTE + entry.getIdEntry(  ) ).trim(  );

        if ( entry.isUnique(  ) )
        {
            ResponseFilter filter = new ResponseFilter(  );
            filter.setIdEntry( entry.getIdEntry(  ) );

            IResponseService responseService = SpringContextService.getBean( FormUtils.BEAN_FORM_RESPONSE_SERVICE );
            Collection<Response> listSubmittedResponses = responseService.getResponseList( filter, false );

            for ( Response submittedResponse : listSubmittedResponses )
            {
                String strSubmittedResponse = getResponseValueForRecap( entry, request, submittedResponse, locale );

                if ( StringUtils.isNotBlank( strValueEntry ) && StringUtils.isNotBlank( strSubmittedResponse ) &&
                        strValueEntry.equalsIgnoreCase( strSubmittedResponse ) )
                {
                    formError = new GenericAttributeError(  );
                    formError.setMandatoryError( false );
                    formError.setTitleQuestion( entry.getTitle(  ) );
                    formError.setErrorMessage( I18nService.getLocalizedString( MESSAGE_UNIQUE_FIELD,
                            request.getLocale(  ) ) );

                    return formError;
                }
            }
        }

        return null;
    }
}
