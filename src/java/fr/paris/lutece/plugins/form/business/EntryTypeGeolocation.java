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


/**
 * The Class EntryTypeGeolocation.
 */
public class EntryTypeGeolocation extends Entry
{
    /** The Constant PARAMETER_MAP_PROVIDER. */
    public static final String PARAMETER_MAP_PROVIDER = "map_provider";

    /** The Constant PARAMETER_SUFFIX_X. */
    public static final String PARAMETER_SUFFIX_X = "_x";

    /** The Constant PARAMETER_SUFFIX_Y. */
    public static final String PARAMETER_SUFFIX_Y = "_y";

    /** The Constant PARAMETER_SUFFIX_ADDRESS. */
    public static final String PARAMETER_SUFFIX_ADDRESS = "_address";

    /** The Constant PARAMETER_SUFFIX_ID_ADDRESS. */
    public static final String PARAMETER_SUFFIX_ID_ADDRESS = "_idAddress";

    /** The Constant CONSTANT_X. */
    public static final String CONSTANT_X = "X";

    /** The Constant CONSTANT_Y. */
    public static final String CONSTANT_Y = "Y";

    /** The Constant CONSTANT_PROVIDER. */
    public static final String CONSTANT_PROVIDER = "provider";

    /** The Constant CONSTANT_ADDRESS. */
    public static final String CONSTANT_ADDRESS = "address";

    /** The Constant CONSTANT_ID_ADDRESS. */
    public static final String CONSTANT_ID_ADDRESS = "idAddress";
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

        /** we need 5 fields : 1 for X, 1 for Y, 1 for map provider, 1 for address and 1 for id address **/
        List<Field> listFields = new ArrayList<Field>(  );
        listFields.add( buildField( CONSTANT_X ) );
        listFields.add( buildField( CONSTANT_Y ) );
        listFields.add( buildFieldMapProvider( strMapProvider ) );
        listFields.add( buildField( CONSTANT_ADDRESS ) );
        listFields.add( buildField( CONSTANT_ID_ADDRESS ) );

        setFields( listFields );

        setTitle( strTitle );
        setHelpMessage( strHelpMessage );
        setComment( strComment );
        setCSSClass( strCSSClass );

        if ( strMandatory != null )
        {
            setMandatory( true );
        }
        else
        {
            setMandatory( false );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FormError getResponseData( HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        String strXValue = request.getParameter( getIdEntry(  ) + PARAMETER_SUFFIX_X );
        String strYValue = request.getParameter( getIdEntry(  ) + PARAMETER_SUFFIX_Y );
        String strAddressValue = request.getParameter( getIdEntry(  ) + PARAMETER_SUFFIX_ADDRESS );
        String strIdAddressValue = request.getParameter( getIdEntry(  ) + PARAMETER_SUFFIX_ID_ADDRESS );

        Field fieldX = FormUtils.findFieldByTitleInTheList( CONSTANT_X, getFields(  ) );
        Field fieldY = FormUtils.findFieldByTitleInTheList( CONSTANT_Y, getFields(  ) );
        Field fieldAddress = FormUtils.findFieldByTitleInTheList( CONSTANT_ADDRESS, getFields(  ) );
        Field fieldIdAddress = FormUtils.findFieldByTitleInTheList( CONSTANT_ID_ADDRESS, getFields(  ) );

        /**
         * The field "idAddress" exists since version 2.5.3 of the plugin-form.
         * Create the field "idAddress" in case the field does not exist in the database.
         * This task is used for existing applications that are using the plugin-address
         * and the module-form-address to get the addresses.
         * @since v2.5.3
         */
        if ( fieldIdAddress == null )
        {
            fieldIdAddress = buildField( CONSTANT_ID_ADDRESS );
            FieldHome.create( fieldIdAddress, FormUtils.getPlugin(  ) );
        }

        // 1 : Response X
        Response responseX = new Response(  );
        responseX.setEntry( this );
        responseX.setResponseValue( strXValue );
        responseX.setField( fieldX );
        responseX.setToStringValueResponse( strXValue );
        listResponse.add( responseX );

        // 2 : Response Y
        Response responseY = new Response(  );
        responseY.setEntry( this );
        responseY.setResponseValue( strYValue );
        responseY.setField( fieldY );
        responseY.setToStringValueResponse( strYValue );
        listResponse.add( responseY );

        // 3 : Response Address
        Response responseAddress = new Response(  );
        responseAddress.setEntry( this );
        responseAddress.setResponseValue( strAddressValue );
        responseAddress.setField( fieldAddress );
        responseAddress.setToStringValueResponse( strAddressValue );
        listResponse.add( responseAddress );

        // 4 : Response Id Address
        Response responseIdAddress = new Response(  );
        responseIdAddress.setEntry( this );
        responseIdAddress.setResponseValue( strIdAddressValue );
        responseIdAddress.setField( fieldIdAddress );
        responseIdAddress.setToStringValueResponse( strIdAddressValue );
        listResponse.add( responseIdAddress );

        if ( isMandatory(  ) )
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

                formError.setMandatoryError( isMandatory(  ) );
                formError.setTitleQuestion( getTitle(  ) );
                formError.setErrorMessage( MESSAGE_SPECIFY_BOTH_X_AND_Y );
                formError.setUrl( this );

                return formError;
            }
        }

        return super.getResponseData( request, listResponse, locale );
    }

    /**
     * {@inheritDoc}
     */
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

    // PRIVATE METHODS

    /**
     * Builds the field.
     *
     * @param strFieldTitle the str field title
     * @return the field
     */
    private Field buildField( String strFieldTitle )
    {
        Field field = new Field(  );
        field.setTitle( strFieldTitle );
        field.setValue( strFieldTitle );
        field.setParentEntry( this );

        return field;
    }

    /**
     * Builds the field map provider.
     *
     * @param strMapProvider the str map provider
     * @return the field
     */
    private Field buildFieldMapProvider( String strMapProvider )
    {
        Field fieldMapProvider = new Field(  );
        fieldMapProvider.setTitle( CONSTANT_PROVIDER );

        if ( StringUtils.isNotBlank( strMapProvider ) )
        {
            strMapProvider = strMapProvider.trim(  );
            fieldMapProvider.setValue( strMapProvider );
            setMapProvider( MapProviderManager.getMapProvider( strMapProvider ) );
        }
        else
        {
            fieldMapProvider.setValue( FormUtils.EMPTY_STRING );
        }

        fieldMapProvider.setParentEntry( this );

        return fieldMapProvider;
    }
}
