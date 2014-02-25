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
package fr.paris.lutece.plugins.form.service.entrytype;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.MandatoryError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeMyLuteceUser;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceList;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 * class EntryTypeText
 */
public class EntryTypeMyLuteceUserAttribute extends AbstractEntryTypeMyLuteceUser
{
    /**
     * Name of the bean of this service
     */
    public static final String BEAN_NAME = "form.entryTypeMyLuteceUser";
    private static final String TEMPLATE_HTML_CODE = "admin/plugins/form/entries/html_code_entry_type_mylutece_user_attribute.html";
    private static final String TEMPLATE_CREATE = "admin/plugins/form/entries/create_entry_type_mylutece_user_attribute.html";
    private static final String TEMPLATE_MODIFY = "admin/plugins/form/entries/modify_entry_type_mylutece_user_attribute.html";
    private static final String PROPERTY_ENTRY_TITLE = "form.entryTypeMyLuteceUserAttribute.title";
    private static final String PARAMETER_DISPLAY_IN_FRONT_OFFICE = "display_front_office";
    private static final String PARAMETER_MYLUTECE_ATTRIBUTE_NAME = "mylutece_attribute_name";
    private ReferenceList _refListUserAttributes;

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
    public String getRequestData( Entry entry, HttpServletRequest request, Locale locale )
    {
        entry.setTitle( I18nService.getLocalizedString( PROPERTY_ENTRY_TITLE, locale ) );

        entry.setComment( StringUtils.EMPTY );
        entry.setConfirmField( Boolean.parseBoolean( request.getParameter( PARAMETER_DISPLAY_IN_FRONT_OFFICE ) ) );
        entry.setMandatory( entry.isConfirmField(  ) &&
            Boolean.parseBoolean( request.getParameter( PARAMETER_MANDATORY ) ) );
        entry.setCSSClass( request.getParameter( PARAMETER_CSS_CLASS ) );
        entry.setTitle( request.getParameter( PARAMETER_TITLE ) );
        entry.setHelpMessage( request.getParameter( PARAMETER_HELP_MESSAGE ) );

        if ( entry.getFields(  ) == null )
        {
            ArrayList<Field> listFields = new ArrayList<Field>(  );
            Field field = new Field(  );
            listFields.add( field );
            entry.setFields( listFields );
        }

        entry.getFields(  ).get( 0 ).setValue( request.getParameter( PARAMETER_MYLUTECE_ATTRIBUTE_NAME ) );
        entry.getFields(  ).get( 0 ).setWidth( 50 );
        entry.getFields(  ).get( 0 ).setMaxSizeEnter( 0 );

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericAttributeError getResponseData( Entry entry, HttpServletRequest request, List<Response> listResponse,
        Locale locale )
    {
        LuteceUser user = SecurityService.getInstance(  ).getRegisteredUser( request );

        if ( ( user == null ) && SecurityService.isAuthenticationEnable(  ) &&
                SecurityService.getInstance(  ).isExternalAuthentication(  ) )
        {
            try
            {
                user = SecurityService.getInstance(  ).getRemoteUser( request );
            }
            catch ( UserNotSignedException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
        }

        String strAttribute = entry.getFields(  ).get( 0 ).getValue(  );

        if ( ( user == null ) ||
                ( StringUtils.isNotEmpty( strAttribute ) && StringUtils.isEmpty( user.getUserInfo( strAttribute ) ) ) )
        {
            if ( entry.isMandatory(  ) )
            {
                return new MandatoryError( entry, locale );
            }

            return null;
        }

        Response response = new Response(  );
        response.setEntry( entry );
        response.setResponseValue( user.getUserInfo( strAttribute ) );

        listResponse.add( response );

        return null;
    }

    /**
     * Get a reference list with every lutece user attributes
     * @return The reference list with every user attributes
     */
    public ReferenceList getLuteceUserAttributesRefList(  )
    {
        if ( _refListUserAttributes != null )
        {
            return _refListUserAttributes;
        }

        ReferenceList referenceList = new fr.paris.lutece.util.ReferenceList(  );

        referenceList.addItem( LuteceUser.BDATE, LuteceUser.BDATE );
        referenceList.addItem( LuteceUser.GENDER, LuteceUser.GENDER );
        referenceList.addItem( LuteceUser.EMPLOYER, LuteceUser.EMPLOYER );
        referenceList.addItem( LuteceUser.DEPARTMENT, LuteceUser.DEPARTMENT );
        referenceList.addItem( LuteceUser.JOBTITLE, LuteceUser.JOBTITLE );
        referenceList.addItem( LuteceUser.PREFIX, LuteceUser.PREFIX );
        referenceList.addItem( LuteceUser.DATE_LAST_LOGIN, LuteceUser.DATE_LAST_LOGIN );
        referenceList.addItem( LuteceUser.NAME_GIVEN, LuteceUser.NAME_GIVEN );
        referenceList.addItem( LuteceUser.NAME_FAMILY, LuteceUser.NAME_FAMILY );
        referenceList.addItem( LuteceUser.NAME_MIDDLE, LuteceUser.NAME_MIDDLE );
        referenceList.addItem( LuteceUser.NAME_SUFFIX, LuteceUser.NAME_SUFFIX );
        referenceList.addItem( LuteceUser.NAME_NICKNAME, LuteceUser.NAME_NICKNAME );
        referenceList.addItem( LuteceUser.NAME_CIVILITY, LuteceUser.NAME_CIVILITY );

        referenceList.addItem( LuteceUser.HOME_INFO_POSTAL_NAME, LuteceUser.HOME_INFO_POSTAL_NAME );
        referenceList.addItem( LuteceUser.HOME_INFO_POSTAL_STREET, LuteceUser.HOME_INFO_POSTAL_STREET );
        referenceList.addItem( LuteceUser.HOME_INFO_POSTAL_STREET_NUMBER, LuteceUser.HOME_INFO_POSTAL_STREET_NUMBER );
        referenceList.addItem( LuteceUser.HOME_INFO_POSTAL_STREET_SUFFIX, LuteceUser.HOME_INFO_POSTAL_STREET_SUFFIX );
        referenceList.addItem( LuteceUser.HOME_INFO_POSTAL_STREET_NAME, LuteceUser.HOME_INFO_POSTAL_STREET_NAME );
        referenceList.addItem( LuteceUser.HOME_INFO_POSTAL_STREET_TYPE, LuteceUser.HOME_INFO_POSTAL_STREET_NAME );
        referenceList.addItem( LuteceUser.HOME_INFO_POSTAL_STREET_URBAN_DISTRICT,
            LuteceUser.HOME_INFO_POSTAL_STREET_URBAN_DISTRICT );
        referenceList.addItem( LuteceUser.HOME_INFO_POSTAL_CITY, LuteceUser.HOME_INFO_POSTAL_CITY );
        referenceList.addItem( LuteceUser.HOME_INFO_POSTAL_STATEPROV, LuteceUser.HOME_INFO_POSTAL_STATEPROV );
        referenceList.addItem( LuteceUser.HOME_INFO_POSTAL_POSTALCODE, LuteceUser.HOME_INFO_POSTAL_POSTALCODE );
        referenceList.addItem( LuteceUser.HOME_INFO_POSTAL_COUNTRY, LuteceUser.HOME_INFO_POSTAL_COUNTRY );
        referenceList.addItem( LuteceUser.HOME_INFO_POSTAL_ORGANIZATION, LuteceUser.HOME_INFO_POSTAL_ORGANIZATION );
        referenceList.addItem( LuteceUser.HOME_INFO_TELECOM_TELEPHONE_INTCODE,
            LuteceUser.HOME_INFO_TELECOM_TELEPHONE_INTCODE );
        referenceList.addItem( LuteceUser.HOME_INFO_TELECOM_TELEPHONE_LOCCODE,
            LuteceUser.HOME_INFO_TELECOM_TELEPHONE_LOCCODE );
        referenceList.addItem( LuteceUser.HOME_INFO_TELECOM_TELEPHONE_NUMBER,
            LuteceUser.HOME_INFO_TELECOM_TELEPHONE_NUMBER );
        referenceList.addItem( LuteceUser.HOME_INFO_TELECOM_TELEPHONE_EXT, LuteceUser.HOME_INFO_TELECOM_TELEPHONE_EXT );
        referenceList.addItem( LuteceUser.HOME_INFO_TELECOM_TELEPHONE_COMMENT,
            LuteceUser.HOME_INFO_TELECOM_TELEPHONE_COMMENT );
        referenceList.addItem( LuteceUser.HOME_INFO_TELECOM_FAX_INT, LuteceUser.HOME_INFO_TELECOM_FAX_INT );
        referenceList.addItem( LuteceUser.HOME_INFO_TELECOM_FAX_LOCCODE, LuteceUser.HOME_INFO_TELECOM_FAX_LOCCODE );
        referenceList.addItem( LuteceUser.HOME_INFO_TELECOM_FAX_NUMBER, LuteceUser.HOME_INFO_TELECOM_FAX_NUMBER );
        referenceList.addItem( LuteceUser.HOME_INFO_TELECOM_FAX_EXT, LuteceUser.HOME_INFO_TELECOM_FAX_EXT );
        referenceList.addItem( LuteceUser.HOME_INFO_TELECOM_FAX_COMMENT, LuteceUser.HOME_INFO_TELECOM_FAX_COMMENT );
        referenceList.addItem( LuteceUser.HOME_INFO_TELECOM_MOBILE_INTCODE, LuteceUser.HOME_INFO_TELECOM_MOBILE_INTCODE );
        referenceList.addItem( LuteceUser.HOME_INFO_TELECOM_MOBILE_LOCCODE, LuteceUser.HOME_INFO_TELECOM_MOBILE_LOCCODE );
        referenceList.addItem( LuteceUser.HOME_INFO_TELECOM_MOBILE_NUMBER, LuteceUser.HOME_INFO_TELECOM_MOBILE_NUMBER );
        referenceList.addItem( LuteceUser.HOME_INFO_TELECOM_MOBILE_EXT, LuteceUser.HOME_INFO_TELECOM_MOBILE_EXT );
        referenceList.addItem( LuteceUser.HOME_INFO_TELECOM_MOBILE_COMMENT, LuteceUser.HOME_INFO_TELECOM_MOBILE_COMMENT );
        referenceList.addItem( LuteceUser.HOME_INFO_TELECOM_PAGER_INTCODE, LuteceUser.HOME_INFO_TELECOM_PAGER_INTCODE );
        referenceList.addItem( LuteceUser.HOME_INFO_TELECOM_PAGER_LOCCODE, LuteceUser.HOME_INFO_TELECOM_PAGER_LOCCODE );
        referenceList.addItem( LuteceUser.HOME_INFO_TELECOM_PAGER_NUMBER, LuteceUser.HOME_INFO_TELECOM_PAGER_NUMBER );
        referenceList.addItem( LuteceUser.HOME_INFO_TELECOM_PAGER_EXT, LuteceUser.HOME_INFO_TELECOM_PAGER_EXT );
        referenceList.addItem( LuteceUser.HOME_INFO_TELECOM_PAGER_COMMENT, LuteceUser.HOME_INFO_TELECOM_PAGER_COMMENT );
        referenceList.addItem( LuteceUser.HOME_INFO_ONLINE_EMAIL, LuteceUser.HOME_INFO_ONLINE_EMAIL );
        referenceList.addItem( LuteceUser.HOME_INFO_ONLINE_URI, LuteceUser.HOME_INFO_ONLINE_URI );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_POSTAL_NAME, LuteceUser.BUSINESS_INFO_POSTAL_NAME );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_POSTAL_STREET, LuteceUser.BUSINESS_INFO_POSTAL_STREET );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_POSTAL_CITY, LuteceUser.BUSINESS_INFO_POSTAL_CITY );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_POSTAL_STATEPROV, LuteceUser.BUSINESS_INFO_POSTAL_STATEPROV );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_POSTAL_POSTALCODE, LuteceUser.BUSINESS_INFO_POSTAL_POSTALCODE );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_POSTAL_COUNTRY, LuteceUser.BUSINESS_INFO_POSTAL_COUNTRY );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_POSTAL_ORGANIZATION,
            LuteceUser.BUSINESS_INFO_POSTAL_ORGANIZATION );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_TELECOM_TELEPHONE_INTCODE,
            LuteceUser.BUSINESS_INFO_TELECOM_TELEPHONE_INTCODE );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_TELECOM_TELEPHONE_LOCCODE,
            LuteceUser.BUSINESS_INFO_TELECOM_TELEPHONE_LOCCODE );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_TELECOM_TELEPHONE_NUMBER,
            LuteceUser.BUSINESS_INFO_TELECOM_TELEPHONE_NUMBER );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_TELECOM_TELEPHONE_EXT,
            LuteceUser.BUSINESS_INFO_TELECOM_TELEPHONE_EXT );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_TELECOM_TELEPHONE_COMMENT,
            LuteceUser.BUSINESS_INFO_TELECOM_TELEPHONE_COMMENT );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_TELECOM_FAX_INTCODE,
            LuteceUser.BUSINESS_INFO_TELECOM_FAX_INTCODE );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_TELECOM_FAX_LOCCODE,
            LuteceUser.BUSINESS_INFO_TELECOM_FAX_LOCCODE );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_TELECOM_FAX_NUMBER, LuteceUser.BUSINESS_INFO_TELECOM_FAX_NUMBER );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_TELECOM_FAX_EXT, LuteceUser.BUSINESS_INFO_TELECOM_FAX_EXT );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_TELECOM_FAX_COMMENT,
            LuteceUser.BUSINESS_INFO_TELECOM_FAX_COMMENT );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_TELECOM_MOBILE_INTCODE,
            LuteceUser.BUSINESS_INFO_TELECOM_MOBILE_INTCODE );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_TELECOM_MOBILE_LOCCODE,
            LuteceUser.BUSINESS_INFO_TELECOM_MOBILE_LOCCODE );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_TELECOM_MOBILE_NUMBER,
            LuteceUser.BUSINESS_INFO_TELECOM_MOBILE_NUMBER );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_TELECOM_MOBILE_EXT, LuteceUser.BUSINESS_INFO_TELECOM_MOBILE_EXT );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_TELECOM_MOBILE_COMMENT,
            LuteceUser.BUSINESS_INFO_TELECOM_MOBILE_COMMENT );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_TELECOM_PAGER_INTCODE,
            LuteceUser.BUSINESS_INFO_TELECOM_PAGER_INTCODE );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_TELECOM_PAGER_LOCCODE,
            LuteceUser.BUSINESS_INFO_TELECOM_PAGER_LOCCODE );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_TELECOM_PAGER_NUMBER,
            LuteceUser.BUSINESS_INFO_TELECOM_PAGER_NUMBER );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_TELECOM_PAGER_EXT, LuteceUser.BUSINESS_INFO_TELECOM_PAGER_EXT );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_TELECOM_PAGER_COMMENT,
            LuteceUser.BUSINESS_INFO_TELECOM_PAGER_COMMENT );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_ONLINE_EMAIL, LuteceUser.BUSINESS_INFO_ONLINE_EMAIL );
        referenceList.addItem( LuteceUser.BUSINESS_INFO_ONLINE_URI, LuteceUser.BUSINESS_INFO_ONLINE_URI );

        // We save the reference list to avoid its generation each time this method is called
        _refListUserAttributes = referenceList;

        return referenceList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForExport( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        return response.getResponseValue(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForRecap( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        return response.getResponseValue(  );
    }
}
