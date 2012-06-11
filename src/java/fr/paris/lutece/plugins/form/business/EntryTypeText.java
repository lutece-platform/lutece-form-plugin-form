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

import fr.paris.lutece.plugins.form.service.IResponseService;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.string.StringUtil;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * class EntryTypeText
 *
 */
public class EntryTypeText extends Entry
{
    private final String _template_create = "admin/plugins/form/create_entry_type_text.html";
    private final String _template_modify = "admin/plugins/form/modify_entry_type_text.html";
    private final String _template_html_code = "admin/plugins/form/html_code_entry_type_text.html";

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
        String strMaxSizeEnter = request.getParameter( PARAMETER_MAX_SIZE_ENTER );
        String strConfirmField = request.getParameter( PARAMETER_CONFIRM_FIELD );
        String strConfirmFieldTitle = request.getParameter( PARAMETER_CONFIRM_FIELD_TITLE );
        String strUnique = request.getParameter( PARAMETER_UNIQUE );
        String strCSSClass = request.getParameter( PARAMETER_CSS_CLASS );

        int nWidth = -1;
        int nMaxSizeEnter = -1;

        String strFieldError = StringUtils.EMPTY;

        if ( StringUtils.isBlank( strTitle ) )
        {
            strFieldError = FIELD_TITLE;
        }

        else if ( StringUtils.isBlank( strWidth ) )
        {
            strFieldError = FIELD_WIDTH;
        }

        if ( ( strConfirmField != null ) && StringUtils.isBlank( strConfirmFieldTitle ) )
        {
            strFieldError = FIELD_CONFIRM_FIELD_TITLE;
        }

        if ( StringUtils.isNotBlank( strFieldError ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
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
            if ( StringUtils.isNotBlank( strMaxSizeEnter ) )
            {
                nMaxSizeEnter = Integer.parseInt( strMaxSizeEnter );
            }
        }
        catch ( NumberFormatException ne )
        {
            strFieldError = FIELD_MAX_SIZE_ENTER;
        }

        if ( StringUtils.isNotBlank( strFieldError ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_NUMERIC_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        this.setTitle( strTitle );
        this.setHelpMessage( strHelpMessage );
        this.setComment( strComment );
        this.setCSSClass( strCSSClass );

        if ( this.getFields(  ) == null )
        {
            ArrayList<Field> listFields = new ArrayList<Field>(  );
            Field field = new Field(  );
            listFields.add( field );
            this.setFields( listFields );
        }

        this.getFields(  ).get( 0 ).setValue( strValue );
        this.getFields(  ).get( 0 ).setWidth( nWidth );
        this.getFields(  ).get( 0 ).setMaxSizeEnter( nMaxSizeEnter );

        if ( strMandatory != null )
        {
            this.setMandatory( true );
        }
        else
        {
            this.setMandatory( false );
        }

        if ( strConfirmField != null )
        {
            this.setConfirmField( true );
            this.setConfirmFieldTitle( strConfirmFieldTitle );
        }
        else
        {
            this.setConfirmField( false );
            this.setConfirmFieldTitle( null );
        }

        if ( strUnique != null )
        {
            this.setUnique( true );
        }
        else
        {
            this.setUnique( false );
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
     * The paginator who is use in the template modify of the entry
     * @param nItemPerPage Number of items to display per page
    * @param strBaseUrl The base Url for build links on each page link
    * @param strPageIndexParameterName The parameter name for the page index
    * @param strPageIndex The current page index
     * @return the paginator who is use in the template modify of the entry
     */
    public Paginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
        String strPageIndex )
    {
        return new Paginator( this.getFields(  ).get( 0 ).getRegularExpressionList(  ), nItemPerPage, strBaseUrl,
            strPageIndexParameterName, strPageIndex );
    }

    /**
     * return the list of regular expression whose not associate to the entry
     * @param entry the entry
     * @param plugin the plugin
     * @return the list of regular expression whose not associate to the entry
     */
    public ReferenceList getReferenceListRegularExpression( IEntry entry, Plugin plugin )
    {
        ReferenceList refListRegularExpression = null;

        if ( RegularExpressionService.getInstance(  ).isAvailable(  ) )
        {
            refListRegularExpression = new ReferenceList(  );

            List<RegularExpression> listRegularExpression = RegularExpressionService.getInstance(  )
                                                                                    .getAllRegularExpression(  );

            for ( RegularExpression regularExpression : listRegularExpression )
            {
                if ( !entry.getFields(  ).get( 0 ).getRegularExpressionList(  ).contains( regularExpression ) )
                {
                    refListRegularExpression.addItem( regularExpression.getIdExpression(  ),
                        regularExpression.getTitle(  ) );
                }
            }
        }

        return refListRegularExpression;
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
        String strValueEntry = request.getParameter( PREFIX_FORM + this.getIdEntry(  ) ).trim(  );
        boolean bConfirmField = this.isConfirmField(  );
        boolean bUnique = this.isUnique(  );
        String strValueEntryConfirmField = null;

        if ( bConfirmField )
        {
            strValueEntryConfirmField = request.getParameter( PREFIX_FORM + this.getIdEntry(  ) + SUFFIX_CONFIRM_FIELD )
                                               .trim(  );
        }

        List<RegularExpression> listRegularExpression = this.getFields(  ).get( 0 ).getRegularExpressionList(  );
        Response response = new Response(  );
        response.setEntry( this );

        if ( strValueEntry != null )
        {
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
                formError.setUrl( this );

                return formError;
            }

            if ( this.isMandatory(  ) )
            {
                if ( StringUtils.isBlank( strValueEntry ) )
                {
                    return new MandatoryFormError( this, locale );
                }
            }

            if ( ( !strValueEntry.equals( FormUtils.EMPTY_STRING ) ) && ( listRegularExpression != null ) &&
                    ( listRegularExpression.size(  ) != 0 ) &&
                    RegularExpressionService.getInstance(  ).isAvailable(  ) )
            {
                for ( RegularExpression regularExpression : listRegularExpression )
                {
                    if ( !RegularExpressionService.getInstance(  ).isMatches( strValueEntry, regularExpression ) )
                    {
                        FormError formError = new FormError(  );
                        formError.setMandatoryError( false );
                        formError.setTitleQuestion( this.getTitle(  ) );
                        formError.setErrorMessage( regularExpression.getErrorMessage(  ) );
                        formError.setUrl( this );

                        return formError;
                    }
                }
            }

            if ( bConfirmField &&
                    ( ( strValueEntryConfirmField == null ) || !strValueEntry.equals( strValueEntryConfirmField ) ) )
            {
                FormError formError = new FormError(  );
                formError.setMandatoryError( false );
                formError.setTitleQuestion( this.getConfirmFieldTitle(  ) );
                formError.setErrorMessage( I18nService.getLocalizedString( MESSAGE_CONFIRM_FIELD,
                        new String[] { this.getTitle(  ) }, request.getLocale(  ) ) );
                formError.setUrl( this );

                return formError;
            }

            if ( bUnique )
            {
                ResponseFilter filter = new ResponseFilter(  );
                filter.setIdEntry( this.getIdEntry(  ) );

                IResponseService responseService = SpringContextService.getBean( FormUtils.BEAN_FORM_RESPONSE_SERVICE );
                Collection<Response> listSubmittedResponses = responseService.getResponseList( filter, false );

                for ( Response submittedResponse : listSubmittedResponses )
                {
                    String strSubmittedResponse = submittedResponse.getEntry(  )
                                                                   .getResponseValueForRecap( request,
                            submittedResponse, locale );

                    if ( StringUtils.isNotBlank( strValueEntry ) && StringUtils.isNotBlank( strSubmittedResponse ) &&
                            strValueEntry.equalsIgnoreCase( strSubmittedResponse ) )
                    {
                        FormError formError = new FormError(  );
                        formError.setMandatoryError( false );
                        formError.setTitleQuestion( this.getTitle(  ) );
                        formError.setErrorMessage( I18nService.getLocalizedString( MESSAGE_UNIQUE_FIELD,
                                request.getLocale(  ) ) );
                        formError.setUrl( this );

                        return formError;
                    }
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

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalizedPaginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
        String strPageIndex, Locale locale )
    {
        return new LocalizedPaginator( this.getFields(  ).get( 0 ).getRegularExpressionList(  ), nItemPerPage,
            strBaseUrl, strPageIndexParameterName, strPageIndex, locale );
    }
}
