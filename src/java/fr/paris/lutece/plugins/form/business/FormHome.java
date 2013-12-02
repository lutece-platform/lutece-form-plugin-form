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
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.IEntry;
import fr.paris.lutece.plugins.genericattributes.business.ResponseFilter;
import fr.paris.lutece.portal.business.style.Theme;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;
import java.util.Map;


/**
 * This class provides instances management methods (create, find, ...) for
 * ReportingProject objects
 */
public final class FormHome
{
    // Static variable pointed at the DAO instance
    private static IFormDAO _dao = SpringContextService.getBean( "form.formDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private FormHome( )
    {
    }

    /**
     * Creation of an instance of Form
     * 
     * @param form The instance of the Form which contains the informations to
     *            store
     * @param plugin the Plugin
     * @return The primary key of the new form.
     */
    public static int create( Form form, Plugin plugin )
    {
        return _dao.insert( form, plugin );
    }

    /**
     * Copy of an instance of Form
     * 
     * @param form The instance of the Form who must copy
     * @param plugin the Plugin
     * 
     */
    public static void copy( Form form, Plugin plugin )
    {
        Recap recap;
        List<IEntry> listEntry;
        EntryFilter filter = new EntryFilter( );
        filter.setIdResource( form.getIdForm( ) );
        filter.setResourceType( Form.RESOURCE_TYPE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        listEntry = EntryHome.getEntryList( filter );
        recap = RecapHome.findByPrimaryKey( form.getRecap( ).getIdRecap( ), plugin );
        recap.setIdRecap( RecapHome.copy( recap, plugin ) );
        form.setRecap( recap );
        form.setDateCreation( FormUtils.getCurrentTimestamp( ) );
        form.setIdForm( create( form, plugin ) );

        for ( IEntry entry : listEntry )
        {
            entry = EntryHome.findByPrimaryKey( entry.getIdEntry( ) );
            entry.setIdResource( form.getIdForm( ) );
            entry.setResourceType( Form.RESOURCE_TYPE );
            EntryHome.copy( entry );
        }
    }

    /**
     * Update of the form which is specified in parameter
     * 
     * @param form The instance of the Form which contains the informations to
     *            update
     * @param plugin the Plugin
     * 
     */
    public static void update( Form form, Plugin plugin )
    {
        _dao.store( form, plugin );
    }

    /**
     * Remove the form whose identifier is specified in parameter
     * 
     * @param nIdForm The form Id
     * @param plugin the Plugin
     */
    public static void remove( int nIdForm, Plugin plugin )
    {
        ResponseFilter responseFilter = new ResponseFilter( );
        responseFilter.setIdResource( nIdForm );

        List<FormSubmit> listFormSubmit = FormSubmitHome.getFormSubmitList( responseFilter, plugin );

        for ( FormSubmit formSubmit : listFormSubmit )
        {
            FormSubmitHome.remove( formSubmit.getIdFormSubmit( ), plugin );
        }

        Form form = findByPrimaryKey( nIdForm, plugin );
        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdResource( form.getIdForm( ) );
        entryFilter.setResourceType( Form.RESOURCE_TYPE );

        List<IEntry> listEntry = EntryHome.getEntryList( entryFilter );

        for ( IEntry entry : listEntry )
        {
            EntryHome.remove( entry.getIdEntry( ) );
        }

        _dao.delete( nIdForm, plugin );
        RecapHome.remove( form.getRecap( ).getIdRecap( ), plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders
    /**
     * Returns an instance of a Form whose identifier is specified in parameter
     * 
     * @param nKey The entry primary key
     * @param plugin the Plugin
     * @return an instance of Form
     */
    public static Form findByPrimaryKey( int nKey, Plugin plugin )
    {
        return _dao.load( nKey, plugin );
    }

    /**
     * Load the data of all the form who verify the filter and returns them in a
     * list
     * @param filter the filter
     * @param plugin the plugin
     * @return the list of form
     */
    public static List<Form> getFormList( FormFilter filter, Plugin plugin )
    {
        return _dao.selectFormList( filter, plugin );
    }

    /**
     * Get the list of forms that must be cleaned automatically. Only id,
     * automatic cleaning and cleaning by removal attributes or form are
     * fetched.
     * @param plugin The plugin
     * @return the list of form, or an empty list if no form was found. Only id,
     *         automatic cleaning and cleaning by removal attributes or form are
     *         fetched.
     */
    public static List<Form> getFormListForAutomaticCleaning( Plugin plugin )
    {
        return _dao.getFormListForAutomaticCleaning( plugin );
    }

    /**
     * Load the data of all enable form returns them in a reference list
     * @param plugin the plugin
     * @return a reference list of enable form
     */
    public static ReferenceList getFormList( Plugin plugin )
    {
        return _dao.getEnableFormList( plugin );
    }

    /**
     * Load the xpage themes for all forms
     * @param plugin The plugin
     * @return A map of themes associated with form ids
     * 
     */
    public static Map<Integer, Theme> getXPageThemes( Plugin plugin )
    {
        return _dao.getXPageThemesMap( plugin );
    }

    /**
     * Get the list of entries of a form to anonymize
     * @param nIdForm The id of the form
     * @param plugin The plugin
     * @return The list of ids of entries to anonymize, or an empty list if the
     *         form was not fount or if no entries of this form should be
     *         anonymized
     */
    public static List<Integer> getAnonymizeEntryList( int nIdForm, Plugin plugin )
    {
        return _dao.getAnonymizeEntryList( nIdForm, plugin );
    }

    /**
     * Insert an entry in the anonymize entries table
     * @param nIdForm The id of the form the entry is associated with
     * @param nIdEntry The id of the entry to anonymize
     * @param plugin The plugin
     */
    public static void insertAnonymizeEntry( int nIdForm, int nIdEntry, Plugin plugin )
    {
        _dao.insertAnonymizeEntry( nIdForm, nIdEntry, plugin );
    }

    /**
     * Remove entries in the anonymize entries table
     * @param nIdForm The id of the form
     * @param plugin The plugin
     */
    public static void removeAnonymizeEntry( int nIdForm, Plugin plugin )
    {
        _dao.removeAnonymizeEntry( nIdForm, plugin );
    }
}
