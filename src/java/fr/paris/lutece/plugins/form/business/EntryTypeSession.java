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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.form.utils.StringUtil;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;


/**
 *
 * class EntryTypeSession
 * This entry is used to fetch the value of a session's attribute.
 * One example is when coupling form with crm, the module-crm-form
 * will put in session the ID demand and the user GUID. This entry will
 * be able to fetch the ID demand and user GUID when validating the form. 
 * Then, it is easier to export the value to directory with the 
 * module-form-exportdirectory.
 *
 */
public class EntryTypeSession extends Entry
{
	private static final String FIELD_ATTRIBUTE_NAME = "form.createEntry.labelAttributeName";
    private final String _template_create = "admin/plugins/form/create_entry_type_session.html";
    private final String _template_modify = "admin/plugins/form/modify_entry_type_session.html";
    private final String _template_html_code = "admin/plugins/form/html_code_entry_type_session.html";

    /**
     * {@inheritDoc}
     */
    public String getHtmlCode(  )
    {
        return _template_html_code;
    }

    /**
     * {@inheritDoc}
     */
    public String getTemplateCreate(  )
    {
        return _template_create;
    }

    /**
     * {@inheritDoc}
     */
    public String getTemplateModify(  )
    {
        return _template_modify;
    }

    /**
     * {@inheritDoc}
     */
    public String getRequestData( HttpServletRequest request, Locale locale )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strAttibuteName = request.getParameter( PARAMETER_VALUE );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );

        String strFieldError = EMPTY_STRING;

        if ( StringUtils.isBlank( strTitle ) )
        {
            strFieldError = FIELD_TITLE;
        }
        else if ( StringUtils.isBlank( strAttibuteName ) )
        {
        	strFieldError = FIELD_ATTRIBUTE_NAME;
        }

        if ( StringUtils.isNotBlank( strFieldError ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        this.setTitle( strTitle );
        this.setHelpMessage( StringUtils.EMPTY );
        this.setComment( StringUtils.EMPTY );
        this.setMandatory( StringUtils.isNotEmpty( strMandatory ) );
        this.setConfirmField( false );
        this.setConfirmFieldTitle( null );
        this.setUnique( false );

        if ( this.getFields(  ) == null )
        {
            List<Field> listFields = new ArrayList<Field>(  );
            Field field = new Field(  );
            listFields.add( field );
            this.setFields( listFields );
        }

        this.getFields(  ).get( 0 ).setValue( strAttibuteName );
        this.getFields(  ).get( 0 ).setWidth( 0 );
        this.getFields(  ).get( 0 ).setMaxSizeEnter( 0 );

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public FormError getResponseData( HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
    	String strValueEntry = StringUtils.EMPTY;
    	HttpSession session = request.getSession( false );
    	if ( session != null )
    	{
    		if ( this.getFields(  ) != null && !this.getFields(  ).isEmpty(  ) &&
    				this.getFields(  ).get( 0 ) != null )
    		{
    			String strAttributeName = this.getFields(  ).get( 0 ).getValue(  );
    			strValueEntry = (String) session.getAttribute( strAttributeName );
    		}
    	}

        if ( StringUtils.isNotBlank( strValueEntry ) )
        {
        	Response response = new Response(  );
        	response.setEntry( this );
        	response.setResponseValue( strValueEntry );
            response.setToStringValueResponse( StringUtils.EMPTY );
            
            listResponse.add( response );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getResponseValueForExport( HttpServletRequest request, Response response, Locale locale )
    {
        return response.getResponseValue(  );
    }

    /**
     * {@inheritDoc}
     */
    public String getResponseValueForRecap( HttpServletRequest request, Response response, Locale locale )
    {
        return StringUtils.EMPTY;
    }
}
