/*
 * Copyright (c) 2002-2017, Mairie de Paris
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

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeGroup;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;

/**
 *
 * class EntryTypeGroup
 *
 */
public class EntryTypeGroup extends AbstractEntryTypeGroup
{
    /**
     * Name of the bean of this service
     */
    public static final String BEAN_NAME = "form.entryTypeGroup";

    // Parameters
    private static final String PARAMETER_IS_ITERABLE = "is_iterable";
    private static final String PARAMETER_NB_ITERATION = "nb_iterations";
    private static final String PARAMETER_ITERATION_ADDING_LABEL = "iteration_adding_label";

    // Constants
    public static final String CONSTANT_NB_ITERATION = "nb_iterations";
    public static final String CONSTANT_ITERATION_ADDING_LABEL = "iteration_adding_label";
    private static final String MESSAGE_ERROR_MANDATORY_FIELD_NB_ITERATIONS = "form.modifyEntry.typeGroup.message.error.fieldNbIterations.mandatory";
    private static final String MESSAGE_ERROR_FIELD_NB_ITERATIONS = "form.modifyEntry.typeGroup.message.error.fieldNbIterations";

    // templates
    private static final String TEMPLATE_MODIFY = "admin/plugins/form/entries/modify_entry_type_group.html";
    private static final String TEMPLATE_HTML_CODE = "skin/plugins/form/entries/html_code_entry_type_group.html";
    private static final String TEMPLATE_HTML_CODE_MULTI_GROUP = "skin/plugins/form/entries/html_code_iterable_entry_type_group.html";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateHtmlForm( Entry entry, boolean bDisplayFront )
    {
        if ( GenericAttributesUtils.findFieldByTitleInTheList( CONSTANT_NB_ITERATION, entry.getFields( ) ) != null )
        {
            return TEMPLATE_HTML_CODE_MULTI_GROUP;
        }

        return TEMPLATE_HTML_CODE;
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
        String strMessageUrl = super.getRequestData( entry, request, locale );

        if ( strMessageUrl != null )
        {
            return strMessageUrl;
        }

        return manageIterationFields( request, entry );
    }

    /**
     * Check all the fields related to the iteration
     * 
     * @param request
     *            the request to retrieve data from
     * @param entry
     *            the entry to inject the new Field
     * @return null if there is no problem false otherwise
     */
    private String manageIterationFields( HttpServletRequest request, Entry entry )
    {
        String strError = null;

        strError = manageNbIterationsField( request, entry );
        if ( strError != null )
        {
            return strError;
        }

        strError = manageIterationAddingLabel( request, entry );

        return strError;
    }

    /**
     * Create a new Field and set it to the entry if the label for the iteration adding button is present in the request otherwise do nothing
     * 
     * @param request
     *            the request to retrieve data from
     * @param entry
     *            the entry to inject the new Field
     * @return null if there is no problem false otherwise
     */
    private String manageIterationAddingLabel( HttpServletRequest request, Entry entry )
    {
        if ( entry != null )
        {
            Field fieldIterationAddingLabel = GenericAttributesUtils.findFieldByTitleInTheList( CONSTANT_ITERATION_ADDING_LABEL, entry.getFields( ) );

            if ( request.getParameter( PARAMETER_IS_ITERABLE ) != null )
            {
                createIterationField( entry, fieldIterationAddingLabel, CONSTANT_ITERATION_ADDING_LABEL,
                        request.getParameter( PARAMETER_ITERATION_ADDING_LABEL ) );
            }
            else
            {
                // The iterations has been disabled on this group so we remove the field which concern the iteration for this entry
                removeIterationField( entry, fieldIterationAddingLabel );
            }
        }

        return null;
    }

    /**
     * Create a new Field and set it to the entry if the number of iteration parameter is present in the request otherwise do nothing
     * 
     * @param request
     *            the request to retrieve data from
     * @param entry
     *            the entry to inject the new Field
     * @return null if there is no problem false otherwise
     */
    private String manageNbIterationsField( HttpServletRequest request, Entry entry )
    {
        if ( entry != null )
        {
            Field fieldNbIteration = GenericAttributesUtils.findFieldByTitleInTheList( CONSTANT_NB_ITERATION, entry.getFields( ) );

            if ( request.getParameter( PARAMETER_IS_ITERABLE ) != null )
            {
                String strNbIterations = request.getParameter( PARAMETER_NB_ITERATION );

                if ( StringUtils.isBlank( strNbIterations ) )
                {
                    return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_MANDATORY_FIELD_NB_ITERATIONS, AdminMessage.TYPE_STOP );
                }

                if ( !StringUtils.isNumeric( strNbIterations ) || NumberUtils.toInt( strNbIterations, NumberUtils.INTEGER_ZERO ) < NumberUtils.INTEGER_ONE )
                {
                    return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_FIELD_NB_ITERATIONS, AdminMessage.TYPE_STOP );
                }

                createIterationField( entry, fieldNbIteration, CONSTANT_NB_ITERATION, strNbIterations );
            }
            else
            {
                // The iterations has been disabled on this group so we remove the field which concern the iteration for this entry
                removeIterationField( entry, fieldNbIteration );
            }
        }

        return null;
    }

    /**
     * Create a new field for an entry or update it if it already exists
     * 
     * @param entry
     *            The entry to add or update a field
     * @param field
     *            The field to add to the entry
     * @param fieldTitle
     *            The title to set to the field
     * @param fieldValue
     *            The value to set to the field
     */
    private void createIterationField( Entry entry, Field field, String fieldTitle, String fieldValue )
    {
        if ( field == null )
        {
            field = new Field( );

            entry.getFields( ).add( field );
        }

        field.setParentEntry( entry );
        field.setTitle( fieldTitle );
        field.setValue( fieldValue );
    }

    /**
     * Remove a field to an entry and remove it from database
     * 
     * @param entry
     *            The entry to remove the field
     * @param fieldToRemove
     *            The field to remove from the entry
     */
    private void removeIterationField( Entry entry, Field fieldToRemove )
    {
        entry.getFields( ).remove( fieldToRemove );

        FieldHome.remove( fieldToRemove.getIdField( ) );
    }
}
