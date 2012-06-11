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

import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.util.ReferenceList;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


public class EntryTypeGeolocation extends Entry
{
    public static final String PARAMETER_MAP_PROVIDER = "map_provider";
    public static final String PARAMETER_SUFFIX_X = "_x";
    public static final String PARAMETER_SUFFIX_Y = "_y";
    public static final String PARAMETER_SUFFIX_ADDRESS = "_address";
    private static final int CONSTANT_POSITION_X = 0;
    private static final int CONSTANT_POSITION_Y = 1;

    //private static final int CONSTANT_POSITION_PROVIDER = 2;
    private static final int CONSTANT_POSITION_ADDRESS = 3;
    public static final String CONSTANT_X = "X";
    public static final String CONSTANT_Y = "Y";
    public static final String CONSTANT_PROVIDER = "provider";
    public static final String CONSTANT_ADDRESS = "address";
    private static final String TEMPLATE_CREATE = "admin/plugins/form/create_entry_type_geolocation.html";
    private static final String TEMPLATE_MODIFY = "admin/plugins/form/modify_entry_type_geolocation.html";
    private static final String TEMPLATE_HTML_CODE = "admin/plugins/form/html_code_entry_type_geolocation.html";
    private static final String MESSAGE_SPECIFY_BOTH_X_AND_Y = "form.message.specifyBothXAndY";

    //private static final String MESSAGE_ADDRESS = "form.modifyField.address";

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
    public String getHtmlCode(  )
    {
        return TEMPLATE_HTML_CODE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( HttpServletRequest request, Locale locale )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strHelpMessage = ( request.getParameter( PARAMETER_HELP_MESSAGE ) != null )
            ? request.getParameter( PARAMETER_HELP_MESSAGE ).trim(  ) : null;
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strMapProvider = request.getParameter( PARAMETER_MAP_PROVIDER );
        String strCSSClass = request.getParameter( PARAMETER_CSS_CLASS );

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

        // we need 4 fields : 1 for X, 1 for Y, 1 for map provider, 1 for address
        Field xField = new Field(  );
        xField.setTitle( CONSTANT_X );
        xField.setValue( CONSTANT_X );
        xField.setParentEntry( this );

        Field yField = new Field(  );
        yField.setTitle( CONSTANT_Y );
        yField.setValue( CONSTANT_Y );
        yField.setParentEntry( this );

        Field mapProviderField = new Field(  );
        mapProviderField.setTitle( CONSTANT_PROVIDER );

        if ( StringUtils.isNotBlank( strMapProvider ) )
        {
            strMapProvider = strMapProvider.trim(  );
            mapProviderField.setValue( strMapProvider );
            setMapProvider( MapProviderManager.getMapProvider( strMapProvider ) );
        }
        else
        {
            mapProviderField.setValue( FormUtils.EMPTY_STRING );
        }

        mapProviderField.setParentEntry( this );

        Field addressField = new Field(  );
        addressField.setTitle( CONSTANT_ADDRESS );
        addressField.setValue( CONSTANT_ADDRESS );
        addressField.setParentEntry( this );

        List<Field> listEntries = new ArrayList<Field>(  );
        listEntries.add( xField );
        listEntries.add( yField );
        listEntries.add( mapProviderField );
        listEntries.add( addressField );

        this.setFields( listEntries );

        this.setTitle( strTitle );
        this.setHelpMessage( strHelpMessage );
        this.setComment( strComment );
        this.setCSSClass( strCSSClass );

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
     * {@inheritDoc}
     */
    @Override
    public FormError getResponseData( HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        String strXValue = request.getParameter( this.getIdEntry(  ) + PARAMETER_SUFFIX_X );
        String strYValue = request.getParameter( this.getIdEntry(  ) + PARAMETER_SUFFIX_Y );
        String strAddressValue = request.getParameter( this.getIdEntry(  ) + PARAMETER_SUFFIX_ADDRESS );

        Field xField = getFields(  ).get( CONSTANT_POSITION_X );
        Field yField = getFields(  ).get( CONSTANT_POSITION_Y );
        Field addressField = getFields(  ).get( CONSTANT_POSITION_ADDRESS );

        // add responses
        Response responseX = new Response(  );
        responseX.setEntry( this );
        responseX.setResponseValue( strXValue );
        responseX.setField( xField );
        responseX.setToStringValueResponse( strXValue );

        listResponse.add( responseX );

        Response responseY = new Response(  );
        responseY.setEntry( this );
        responseY.setResponseValue( strYValue );
        responseY.setField( yField );
        responseY.setToStringValueResponse( strYValue );

        listResponse.add( responseY );

        Response responseAddress = new Response(  );
        responseAddress.setEntry( this );
        responseAddress.setResponseValue( strAddressValue );
        responseAddress.setField( addressField );
        responseAddress.setToStringValueResponse( strAddressValue );

        listResponse.add( responseAddress );

        if ( this.isMandatory(  ) )
        {
            if ( StringUtils.isBlank( strAddressValue ) )
            {
                return new MandatoryFormError( this, locale );
            }
        }

        if ( ( StringUtils.isBlank( strXValue ) && StringUtils.isNotBlank( strYValue ) ) ||
                ( StringUtils.isNotBlank( strXValue ) && StringUtils.isBlank( strYValue ) ) )
        {
            if ( StringUtils.isBlank( strAddressValue ) )
            {
                FormError formError = new FormError(  );

                formError.setMandatoryError( this.isMandatory(  ) );
                formError.setTitleQuestion( this.getTitle(  ) );
                formError.setErrorMessage( MESSAGE_SPECIFY_BOTH_X_AND_Y );
                formError.setUrl( this );

                return formError;
            }
        }

        return super.getResponseData( request, listResponse, locale );
    }

    @Override
    public String getResponseValueForRecap( HttpServletRequest request, Response response, Locale locale )
    {
        String strTitle = response.getField(  ).getTitle(  );

        if ( CONSTANT_ADDRESS.equals( strTitle ) )
        {
            //strTitle = I18nService.getLocalizedString( MESSAGE_ADDRESS, locale );
            return response.getResponseValue(  );
        }

        return StringUtils.EMPTY;
    }

    /**
     * Returns the available map providers
     * @return all known map providers
     */
    public List<IMapProvider> getMapProviders(  )
    {
        return MapProviderManager.getMapProvidersList(  );
    }

    /**
     * Builds the {@link ReferenceList} of all available map providers
     * @return the {@link ReferenceList}
     */
    public ReferenceList getMapProvidersRefList(  )
    {
        ReferenceList refList = new ReferenceList(  );

        refList.addItem( FormUtils.EMPTY_STRING, FormUtils.EMPTY_STRING );

        for ( IMapProvider mapProvider : MapProviderManager.getMapProvidersList(  ) )
        {
            refList.add( mapProvider.toRefItem(  ) );
        }

        return refList;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForExport( HttpServletRequest request, Response response, Locale locale )
    {
        String fieldName = StringUtils.EMPTY;

        if ( response.getField(  ) != null )
        {
            fieldName = ObjectUtils.toString( response.getField(  ).getTitle(  ) );
        }

        return fieldName + FormUtils.CONSTANT_EQUAL + response.getResponseValue(  );
    }
}
