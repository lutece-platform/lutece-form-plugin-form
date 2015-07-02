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
package fr.paris.lutece.plugins.form.business;

import fr.paris.lutece.portal.business.style.Theme;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;
import java.util.Map;


/**
 * IFormDAO Interface
 */
public interface IFormDAO
{
    /**
     * Insert a new record in the table.
     *
     * @param form instance of the Form to insert
     * @param plugin the plugin
     * @return the id of the new form
     */
    int insert( Form form, Plugin plugin );

    /**
     * Update the form in the table
     *
     * @param form instance of the Form object to update
     * @param plugin the plugin
     */
    void store( Form form, Plugin plugin );

    /**
     * Delete a record from the table
     *
     * @param nIdForm The identifier of the form
     * @param plugin the plugin
     */
    void delete( int nIdForm, Plugin plugin );

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Load the data of the Form from the table
     *
     * @param nKey The identifier of the form
     * @param plugin the plugin
     * @return the instance of the Form
     */
    Form load( int nKey, Plugin plugin );

    /**
     * Load the data of all the form who verify the filter and returns them in a
     * list
     * @param filter the filter
     * @param plugin the plugin
     * @return the list of form
     */
    List<Form> selectFormList( FormFilter filter, Plugin plugin );

    /**
     * Load the data of all enable form returns them in a reference list
     * @param plugin the plugin
     * @return a reference list of form
     */
    ReferenceList getEnableFormList( Plugin plugin );

    /**
     * Load all the themes for form xpages
     * @param plugin the plugin
     * @return a map containing the themes by form id
     */
    Map<Integer, Theme> getXPageThemesMap( Plugin plugin );

    /**
     * Get the list of entries of a form to anonymize
     * @param nIdForm The id of the form
     * @param plugin The plugin
     * @return The list of ids of entries to anonymize, or an empty list if the
     *         form was not fount or if no entries of this form should be
     *         anonymized
     */
    List<Integer> getAnonymizeEntryList( int nIdForm, Plugin plugin );

    /**
     * Insert an entry in the anonymize entries table
     * @param nIdForm The id of the form the entry is associated with
     * @param nIdEntry The id of the entry to anonymize
     * @param plugin The plugin
     */
    void insertAnonymizeEntry( int nIdForm, int nIdEntry, Plugin plugin );

    /**
     * Remove entries in the anonymize entries table
     * @param nIdForm The id of the form
     * @param plugin The plugin
     */
    void removeAnonymizeEntry( int nIdForm, Plugin plugin );

    /**
     * Get the list of forms that must be cleaned automatically. Only id,
     * automatic cleaning and cleaning by removal attributes or form are
     * fetched.
     * @param plugin The plugin
     * @return the list of form, or an empty list if no form was found. Only id,
     *         automatic cleaning and cleaning by removal attributes or form are
     *         fetched.
     */
    List<Form> getFormListForAutomaticCleaning( Plugin plugin );
}
